@echo off

where java > NUL 2> NUL
if %ERRORLEVEL% == 1 (
echo Please install Java
pause
exit /b
) 

set "mytmpdir=%temp%\%~nx0_%time::=.%"

md "%mytmpdir%"

echo %mytmpdir%\linked-data-fu-0.9.12 > .tmppath.

REM Download agent runtime
powershell.exe -nologo -noprofile -command "Invoke-WebRequest -Uri https://raw.githubusercontent.com/linked-data-fu/linked-data-fu.github.io/master/releases/0.9.12/linked-data-fu-standalone-0.9.12-bin.zip -Outfile '%mytmpdir%\linked-data-fu-standalone-0.9.12-bin.zip'"

REM Unzip agent runtime
powershell.exe -nologo -noprofile -command "& { Add-Type -A 'System.IO.Compression.FileSystem'; [IO.Compression.ZipFile]::ExtractToDirectory('%mytmpdir%\linked-data-fu-standalone-0.9.12-bin.zip', '%mytmpdir%'); }"

REM Patch agent runtime
del "%mytmpdir%\linked-data-fu-0.9.12\lib\nxparser-parsers-5f525167a26773c482a63256065ae8d21addd06f.jar"
powershell.exe -nologo -noprofile -command "Invoke-WebRequest -Uri https://search.maven.org/remotecontent?filepath=org/semanticweb/yars/nxparser-parsers/3.0.1/nxparser-parsers-3.0.1.jar  -Outfile '%mytmpdir%\linked-data-fu-0.9.12\lib\nxparser-parsers-3.0.1.jar'"

powershell.exe -nologo -noprofile -command "copy .ldf. '%mytmpdir%\linked-data-fu-0.9.12\bin\ldf.bat'"

echo initialised :)

