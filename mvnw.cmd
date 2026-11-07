@echo off
REM Thin wrapper: runs the project-local Maven distribution bundled under .tools
REM so no system-wide Maven install is required.
set MAVEN_HOME=%~dp0.tools\apache-maven-3.9.9
"%MAVEN_HOME%\bin\mvn.cmd" %*
