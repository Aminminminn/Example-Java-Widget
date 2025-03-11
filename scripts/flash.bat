@ECHO off

SET CURRENT_DIRECTORY=%CD%
ECHO "Current directory: %CURRENT_DIRECTORY%"

cd /build/application/executable/

REM Need to rename the .out into .elf to call the LinkServer flash cmd
ren application.out application.elf

LinkServer flash MIMXRT1176xxxxx:MIMXRT1170-EVKB load .\application.elf

IF %ERRORLEVEL% NEQ 0 (
	ECHO "Failed to flash the board"
	EXIT /B %ERRORLEVEL%
)

cd %CURRENT_DIRECTORY%