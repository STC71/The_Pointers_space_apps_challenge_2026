@echo off
setlocal

rem Start from the project directory, including paths with spaces or network shares.
pushd "%~dp0"
if errorlevel 1 exit /b 1

call ".\gradlew.bat" :composeApp:run %*
set "LAUNCH_EXIT_CODE=%ERRORLEVEL%"

popd
if not "%LAUNCH_EXIT_CODE%"=="0" pause
exit /b %LAUNCH_EXIT_CODE%
