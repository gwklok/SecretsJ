@echo off
javadoc -d doc BlowfishJ\*.java
echo Compiling (junit tests excluded)
javac BlowfishJ\*.java
javac BlowfishJ\test\BlowfishDemo.java
javac BlowfishJ\test\SHA1Demo.java
echo Creating JAR file
jar cvf BlowfishJ.jar BlowfishJ\*.class