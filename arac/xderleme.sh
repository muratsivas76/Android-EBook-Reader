./clean.sh

#/usr/java/android-sdk/tools/android list target
#/usr/java/android-sdk/tools/android --verbose create avd --name MuratAVD --target 3 --sdcard 1024M
#/usr/java/android-sdk/tools/android --verbose create avd --name MuratBVD --target 3 --skin qvga-l --sdcard 2048M

#Windows
#"c:\Users\murat\AppData\Local\Android\Sdk\tools\bin\sdkmanager.bat" --list
#"c:\Users\murat\AppData\Local\Android\Sdk\tools\bin\sdkmanager.bat" --install "system-images;android-29;google_apis_playstore;x86_64"
#c:\Users\murat\AppData\Local\Android\Sdk\tools\bin\avdmanager.bat --verbose create avd --force --name "MuratAVD" --package "system-images;android-29;google_apis_playstore;x86_64" --tag "google_apis_playstore" --abi "x86_64"
#"c:\Users\murat\AppData\Local\Android\Sdk\emulator\emulator.exe" -list-avds
# MSVCP140_1.dll gereklidir. Bunun için ilgili Visual Basic exe kurulup çalıştırılır bilgisayar yeniden başlatılır.
#####DISM /Online /Enable-Feature /All /FeatureName:HypervisorPlatform #Gerekli olmayabilir
#####Bilgisayarı yeniden başlat. #Gerekli olmayabilir.
#BIOS üzerinden Configurations/Virtualization enable yap.
#Hyper-V-Enabler.exe dosyasını indir ve yönetici olarak çalıştır.
#Bilgisayarı yeniden başlat.
#
#"c:\Users\murat\AppData\Local\Android\Sdk\emulator\emulator.exe" @MuratAVD
#
#"c:\Users\murat\AppData\Local\Android\Sdk\platform-tools\adb.exe" -e install bin/EBook.apk"
#
#"You can remove it from emulator with this command:"
#"c:\Users\murat\AppData\Local\Android\Sdk\platform-tools\adb.exe" shell rm /data/app/net.murat.ebook.apk"
#
#"You can uninstall it from emulator with this command:"
#"c:\Users\murat\AppData\Local\Android\Sdk\platform-tools\adb.exe" -e uninstall net.murat.ebook"
#
#"For debugging intall firstly the apk. Then give this command and run the apk:"
#"c:\Users\murat\AppData\Local\Android\Sdk\platform-tools\adb.exe" -e logcat


/usr/java/jdk1.6.0/bin/keytool -genkey -alias EBook.keystore -keyalg RSA -validity 1000000 -dname "CN=Murat inan, OU=Freelance, O=Traductor, S=Merkez, C=TR" -keystore EBook.keystore -storepass 5EmrE432 -keypass 5EmrE432 

/usr/java/android-sdk/platforms/android-1.5/tools/aapt package -v -f -m -S res -J src -M AndroidManifest.xml -I /usr/java/android-sdk/platforms/android-1.5/android.jar

#/usr/java/jdk1.6.0/bin/javac -bootclasspath /usr/java/android-sdk/platforms/android-1.5/android.jar: -sourcepath src/: -cp /usr/java/android-sdk/platforms/android-1.5/android.jar:build/classes: -g:none -proc:none -nowarn -O src/net/murat/ebook/EBook.java -d build/classes
/usr/java/jdk1.6.0/bin/javac -bootclasspath /usr/java/android-sdk/platforms/android-1.5/android.jar: -sourcepath src/: -cp /usr/java/android-sdk/platforms/android-1.5/android.jar:build/classes: -g:source -proc:none -nowarn -O src/net/murat/ebook/EBook.java -d build/classes

/usr/java/android-sdk/platforms/android-1.5/tools/dx --dex --verbose --output=dist/classes.dex build/classes dist/lib

/usr/java/android-sdk/platforms/android-1.5/tools/aapt package -v -f -M AndroidManifest.xml -A assets -S res -I /usr/java/android-sdk/platforms/android-1.5/android.jar -F dist/EBook.unsigned.apk dist

/usr/java/jdk1.6.0/bin/jarsigner -verbose -keystore EBook.keystore -storepass 5EmrE432 -keypass 5EmrE432 -signedjar dist/EBook.signed.apk dist/EBook.unsigned.apk EBook.keystore

/usr/java/android-sdk/tools/zipalign -v -f 4 dist/EBook.signed.apk dist/EBook.apk

rm -fv dist/*.signed.* dist/*.unsigned.* dist/classes.dex

#/usr/java/jdk1.6.0/bin/javadoc -verbose -d docs -sourcepath src -classpath /usr/java/android-sdk/platforms/android-1.5/android.jar:build/classes -author -package -use -splitindex -version -windowtitle 'EBook' -doctitle 'EBook' src/net/murat/ebook/*.java

echo " "
echo "Run this firstly: "
echo "/usr/java/android-sdk/tools/emulator -wipe-data -avd MuratAVD &"

echo " "
echo "Run this when the emulator is ready:"
echo "/usr/java/android-sdk/tools/adb -e install dist/EBook.apk"

echo " "
echo "You can remove it from emulator with this command:"
echo "/usr/java/android-sdk/tools/adb shell rm /data/app/net.murat.ebook.apk"

echo " "
echo "You can uninstall it from emulator with this command:"
echo "/usr/java/android-sdk/tools/adb -e uninstall net.murat.ebook"

echo ""
echo "For debugging intall firstly the apk. Then give this command and run the apk:"
echo "/usr/java/android-sdk/tools/adb -e logcat &"

echo " "
