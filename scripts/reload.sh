#!/bin/bash

set -a
source .env
set +a

WD=`pwd`

cd $MAVEN_LOCAL_PATH/repository
rm -r io/github/leawind/perspectiveapi/perspective_api

cd $WD
cd $MOD_SOURCE_PATH
./gradlew build -x test --offline
./gradlew publishToMavenLocal --offline --no-parallel

cd $WD
rm -r .gradle/loom-cache/remapped_mods/remapped/io/github/leawind/perspectiveapi

