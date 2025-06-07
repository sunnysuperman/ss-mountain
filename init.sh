#!/bin/bash
set -e

projectDir=$(cd "$(dirname "$0")"; pwd)
destFile=$projectDir/.git/hooks/pre-commit

cp -f $projectDir/assets/format/pre-commit $destFile
chmod +x $destFile