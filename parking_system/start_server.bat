@echo off
cd /d %~dp0

mvn exec:java -Dexec.mainClass=server.Server

pause