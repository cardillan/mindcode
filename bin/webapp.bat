@echo off
setlocal enableDelayedExpansion
pushd %~dp0
set __CLPATH=.
for /R ..\webapp\target %%i in (*.jar) do set __CLPATH=!__CLPATH!;%%i
popd
"%JAVA_HOME%\bin\java.exe" --enable-preview  -classpath !__CLPATH! info.teksol.mindcode.webapp.WebappApplication %*
endlocal
