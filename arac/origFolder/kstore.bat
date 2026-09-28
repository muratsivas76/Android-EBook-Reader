del /f /s /q *.keystore* classes.dex bin\*.signed.* bin\*.unsigned.* bin\*.apk src\net\murat\ebook\R.java obj\*

rmdir /s /q "obj\*"

"C:\Program Files\java\jdk-1.8\bin\keytool.exe" -genkey -alias EBook.keystore -keyalg RSA -validity 100000 -dname "CN=Murat inan, OU=Freelance, O=Traductor, S=Sivas, C=TR" -keystore EBook.keystore -storepass 5EmrE432 -keypass 5EmrE432 

"C:\Program Files\java\jdk-1.8\bin\keytool.exe" -importkeystore -srckeystore EBook.keystore -destkeystore EBook.keystore -deststoretype pkcs12
