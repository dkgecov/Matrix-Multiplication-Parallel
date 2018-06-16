#!/bin/bash
cls
java -cp ..\src\source\commons-cli-1.4.jar; source.Main -m 2000 -n 2500 -k 2000 -lb 0 -ub 100 -t 4 -q
pause