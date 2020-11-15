#!/usr/bin/env bash
###########################################################
#  This script helps build images for service specified
#  It relies on Docker 18.06.0-ce and acts as a template
#  for future Tapis services and building an image from a
#  set of maven artifacts.
#
# environment : VER set to the version in tapis/pom.xml
#               TAPIS_ENV sets the environment target the image is created to run in
#               for Aloe dev this "T2dev". We need to distinguish images for this version
#               of the security container from Tapis V3 versions.
#
# export VER=0.0.1
# export TAPIS_ENV=T2dev
# usage : $TAPIS_ROOT/deployment/build-metaapi.sh
#
###########################################################
VER=$(mvn org.apache.maven.plugins:maven-help-plugin:3.2.0:evaluate -Dexpression=project.version -q -DforceStdout)

export TAPIS_ENV=T2dev
export SRVC=meta
export TAPIS_ROOT=$(pwd)
export GIT_COMMIT=$(git log -1 --pretty=format:"%h")

echo "VER: $VER"
echo "TAPIS_ENV: $TAPIS_ENV"
echo "SRVC: $SRVC"
echo "TAPIS_ROOT: $TAPIS_ROOT"
echo "GIT_COMMIT: $GIT_COMMIT"
echo "JAVA VERSION : ";$(java -version)

echo " ***   Global build of modules.  "
echo " ***   mvn clean install -DskipTests";echo ""

mvn clean install -DskipTests

echo ""; echo ""
cd deployment/tapis_meta_base
docker image build -t tapis/tapis-meta-base .
docker tag tapis/tapis-meta-base jenkins2.tacc.utexas.edu:5000/tapis/tapis-meta-base
docker push jenkins2.tacc.utexas.edu:5000/tapis/tapis-meta-base

cd $TAPIS_ROOT

echo " ***   build api image and publish ";echo ""
deployment/build-metaapi.sh

echo " ***   build worker image and publish ";echo ""
deployment/build-metawrkr.sh


echo " ***   Images published and ready to deploy ";echo ""




