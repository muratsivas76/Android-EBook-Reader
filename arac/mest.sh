#!/bin/sh

./clean.sh

JAVA_HOME=/usr/java/jdk1.8.0_261
PATH=$PATH:$JAVA_HOME/bin:

java -version

java -cp cls: ToMDK test.txt SansSerif,0,27 ISO-8859-9 5 542F yes

java -cp cls: CountPages
echo " "
