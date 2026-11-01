@echo off
setlocal enableDelayedExpansion
pushd %~dp0..\webapp\target
for %%i in (*.jar) do set __CLPATH=..\%%i
cd dependency
for %%i in (*.jar) do set __CLPATH=!__CLPATH!;%%i
"%JAVA_HOME%\bin\java.exe" -classpath !__CLPATH! io.github.cardillan.mindcode.webapp.WebappApplication %*
popd
endlocal
