#!/bin/bash
cls
java -cp ..\src\source\commons-cli-1.4.jar; source.Main -i in.txt -o out.txt -t 4 -q
pause