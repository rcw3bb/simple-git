@echo off
cd /d %~dp0

REM | Parameter | Description                  |
REM |-----------|------------------------------|
REM | %1        | The git project directory.   |
REM | %2        | The git executable.          |
REM | %3*       | The git command arguments.   |

pushd %1

shift
set git_cmd=%1

:loop
shift
if (%1)==() goto done
set git_cmd=%git_cmd% %1
goto loop
:done

%git_cmd%
popd