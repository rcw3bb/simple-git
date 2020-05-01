@echo off
cd /d %~dp0

REM | Parameter | Description                  |
REM |-----------|------------------------------|
REM | %1        | The git project directory.   |
REM | %2        | The git executable.          |
REM | %3        | The git command arguments.   |

pushd %1
%2 %3
popd
