"C:\Users\murat\AppData\Local\Android\Sdk\build-tools\29.0.0\aapt.exe" package -v -f -M "AndroidManifest.xml" -A "assets" -S "res" -I "C:\Users\murat\AppData\Local\Android\Sdk\platforms\android-29\android.jar" -F "bin\EBook.unsigned.apk" "bin"

"C:\Users\murat\AppData\Local\Android\Sdk\build-tools\29.0.0\aapt.exe" add "bin\EBook.unsigned.apk" "classes.dex"

"C:\Program Files\java\jdk-1.8\bin\jarsigner.exe" -keystore EBook.keystore -storepass 5EmrE432 -keypass 5EmrE432 -signedjar "bin\EBook.signed.apk" "bin\EBook.unsigned.apk" EBook.keystore

"C:\Users\murat\AppData\Local\Android\Sdk\build-tools\29.0.0\zipalign.exe" -v -f 4 "bin\EBook.signed.apk" "bin\EBook.apk"

java -cp arac\cls; del -s auto2.bat *.keystore.old classes.dex bin\*.signed.* bin\*.unsigned.* obj\* src\net\murat\ebook\R.java

java -cp arac\cls; del -s obj\net
