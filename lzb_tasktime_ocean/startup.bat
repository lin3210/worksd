title ≈‹≈˙∆Ù∂Ø
@echo off
set TMP_CLASSPATH=%CLASSPATH%

set CLASSPATH=.\bin\;%CLASSPATH%
rem Add all jars....
for %%i in (".\lib\*.jar") do call ".\cpappend.bat" %%i
for %%i in (".\lib\*.zip") do call ".\cpappend.bat" %%i

set CUR_CLASSPATH=%CLASSPATH%
set CLASSPATH=%TMP_CLASSPATH%

java -cp "%CUR_CLASSPATH%"  Main

