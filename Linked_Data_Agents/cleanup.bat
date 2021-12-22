@echo off

set /p tmppath=<.tmppath.

rd /s "%tmppath:~0,-23%"
