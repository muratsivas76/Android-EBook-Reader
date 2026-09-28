del /f /s /q *.keystore* classes.dex bin\*.signed.* bin\*.unsigned.* obj\* src\net\murat\ebook\R.java

rmdir /s /q "obj\net\murat\ebook\"
rmdir /s /q "obj\net\murat\"
rmdir /s /q "obj\net\"

"C:\Users\murat\AppData\Local\Android\Sdk\build-tools\29.0.0\aapt.exe" package -v -f -m -S "res" -J "src" -M "AndroidManifest.xml" -I "C:\Users\murat\AppData\Local\Android\Sdk\platforms\android-29\android.jar"

"C:\Program Files\Java\jdk-1.8\bin\javac.exe" -bootclasspath "C:\Users\murat\AppData\Local\Android\Sdk\platforms\android-29\android.jar"; -sourcepath "src"; -cp "C:\Users\murat\AppData\Local\Android\Sdk\platforms\android-29\android.jar";"obj"; -g:none -proc:none -nowarn -O -Xlint:none "src\net\murat\ebook\EBook.java" -d "obj"

del /f /s /q *.keystore* classes.dex bin\*.signed.* bin\*.unsigned.* obj\* src\net\murat\ebook\R.java

rmdir /s /q "obj\net\murat\ebook\"
rmdir /s /q "obj\net\murat\"
rmdir /s /q "obj\net\"
