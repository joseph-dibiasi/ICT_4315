:: Lightweight Script. Can be run directly or with a client count parameter. Main purpose is to call the ps1 script. 
@echo off
setlocal

set "SCRIPT_DIR=%~dp0"
set "CLIENTS=%~1"
if "%CLIENTS%"=="" set "CLIENTS=20"

powershell -NoProfile -ExecutionPolicy Bypass -File "%SCRIPT_DIR%test-multithread.ps1" -clients %CLIENTS%
exit /b %errorlevel%
