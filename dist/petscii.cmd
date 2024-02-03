@echo off
java -Xmx2048m -cp %~dp0/petsciiator.jar;petsciiator.jar;dist/petsciiator.jar;lib/* com.sixtyfour.petscii.Petsciiator %*
