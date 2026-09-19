./clean.sh

/usr/java/android-sdk/platforms/android-1.5/tools/aapt package -v -f -m -S res -J src -M AndroidManifest.xml -I /usr/java/android-sdk/platforms/android-1.5/android.jar

/usr/java/jdk1.6.0/bin/javac -bootclasspath /usr/java/android-sdk/platforms/android-1.5/android.jar: -sourcepath src/: -cp /usr/java/android-sdk/platforms/android-1.5/android.jar:build/classes: -g:none -proc:none -nowarn -O src/net/murat/ebook/EBook.java -d build/classes
