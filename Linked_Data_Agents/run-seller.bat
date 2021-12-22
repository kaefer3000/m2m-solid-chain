REM @echo off

set /p tmp=<.tmppath.

set tmppath=%tmp:~0,-1%

"%tmppath%\bin\ldf.bat" -p agents/SAgent.n3 -n 200
