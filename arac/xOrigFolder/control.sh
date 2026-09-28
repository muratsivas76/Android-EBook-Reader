./clean.sh

/usr/java/android/build-tools/33.0.2/aapt package -v -f -m -S res -J src -M AndroidManifest.xml -I /usr/java/android/platforms/android-33/android.jar

/usr/java/jdk-25/bin/javac -source 1.8 -target 1.8 -bootclasspath /usr/java/android/platforms/android-33/android.jar: -sourcepath src/: -cp /usr/java/android/platforms/android-33/android.jar:obj: -g:none -proc:none -nowarn -O -d obj src/net/murat/ebook/EBook.java
