#! /bin/bash
cd src
javac Main.java
java Main
rm -r *.class
rm -r */*.class
