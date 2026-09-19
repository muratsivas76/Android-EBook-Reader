java -cp arac\cls; del -s bin\*.apk

java -cp arac\cls; copy -v arac\infos\r00000.dat assets\texts\r00000.dat

java -cp arac\cls; copy -v arac\customInfo.dat assets\texts\info.txt

java -cp arac\cls; PLogoGen libro

"C:\Users\murat\AppData\Local\Android\Sdk\build-tools\29.0.0\aapt.exe" package -v -f -m -S "res" -J "src" -M "AndroidManifest.xml" -I "C:\Users\murat\AppData\Local\Android\Sdk\platforms\android-29\android.jar"

"C:\Program Files\Java\jdk-1.8\bin\javac.exe" -bootclasspath "C:\Users\murat\AppData\Local\Android\Sdk\platforms\android-29\android.jar"; -sourcepath "src"; -cp "C:\Users\murat\AppData\Local\Android\Sdk\platforms\android-29\android.jar";"obj"; -g:none -proc:none -nowarn -O -Xlint:none "src\net\murat\ebook\EBook.java" -d "obj"
::"C:\Program Files\Java\jdk-1.8\bin\javac.exe" -bootclasspath "C:\Users\murat\AppData\Local\Android\Sdk\platforms\android-29\android.jar"; -sourcepath "src"; -cp "C:\Users\murat\AppData\Local\Android\Sdk\platforms\android-29\android.jar";"obj"; -g:none -proc:none -nowarn -O -Xlint:all "src\net\murat\ebook\EBook.java" -d "obj"

"C:\Users\murat\AppData\Local\Android\Sdk\build-tools\29.0.0\dx.bat" --dex --verbose --output="classes.dex" "obj"
