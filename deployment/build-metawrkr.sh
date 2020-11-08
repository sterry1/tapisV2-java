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
# usage : $TAPIS_ROOT/deployment/build-metawrkr.sh
#
###########################################################
VER=$(mvn org.apache.maven.plugins:maven-help-plugin:3.2.0:evaluate -Dexpression=project.version -q -DforceStdout)

TAPIS_ENV=$TAPIS_ENV
export SRVC=metawrkr
export SRVC_API=${SRVC}
export TAPIS_ROOT=.    #$(pwd)
export SRVC_DIR="${TAPIS_ROOT}/tapis-${SRVC_API}/target"
export TAG="tapis/${SRVC_API}:$VER"
export IMAGE_BUILD_DIR="$TAPIS_ROOT/deployment/tapis-${SRVC_API}"
export BUILD_FILE="$IMAGE_BUILD_DIR/Dockerfile"
export GIT_COMMIT=$(git log -1 --pretty=format:"%h")
export JAR_NAME=metawrkr.jar    # matches final name in pom file

echo "VER: $VER"
echo "TAPIS_ENV: $TAPIS_ENV"
echo "SRVC: $SRVC"
echo "SRVC_API: $SRVC_API"
echo "TAPIS_ROOT: $TAPIS_ROOT"
echo "SRVC_DIR: $SRVC_DIR"
echo "TAG: $TAG"
echo "IMAGE_BUILD_DIR: $IMAGE_BUILD_DIR"
echo "BUILD_FILE: $BUILD_FILE"
echo "GIT_COMMIT: $GIT_COMMIT"
echo "JAR_NAME: $JAR_NAME"
echo "JAVA VERSION : $(java -version)"

# cd tapis-metawrkr
echo " ***   We assume a global build has already taken place.  "

# echo " ***   do a build on metawrkr  "
# echo " ***   mvn clean install -DskipTests"
# mvn clean install -DskipTests

echo "";echo ""

# cd ..  # jump back up to project root directory

echo "***      removing any old metawrkr jar from Docker build context"
echo "***      $IMAGE_BUILD_DIR/$JAR_NAME "
# if test -d "$IMAGE_BUILD_DIR/$JAR_NAME"; then
#      rm -rf $IMAGE_BUILD_DIR/$JAR_NAME
#      echo " removed $IMAGE_BUILD_DIR/$JAR_NAME "
# fi

echo " mkdir -p ${IMAGE_BUILD_DIR}/${SRVC_API}"
mkdir -p ${IMAGE_BUILD_DIR}/${SRVC_API}

echo "";echo "point 1";echo ""


echo "***   copy the new worker package to our docker build directory "
echo "***   cp  $SRVC_DIR/$JAR_NAME ${IMAGE_BUILD_DIR}/${SRVC_API}/$JAR_NAME "
            cp  $SRVC_DIR/$JAR_NAME ${IMAGE_BUILD_DIR}/${SRVC_API}/$JAR_NAME
echo "***   cp -p -R $SRVC_DIR/lib/ ${IMAGE_BUILD_DIR}/${SRVC_API}/lib "
            cp -p -R $SRVC_DIR/lib/ ${IMAGE_BUILD_DIR}/${SRVC_API}/lib

echo "";echo "point 2";echo ""

echo " ***   jump to the deployment build directory "
echo " ***   cd ${IMAGE_BUILD_DIR}"
             cd ${IMAGE_BUILD_DIR}

echo "";echo "point 3";echo ""

echo "***      building the docker image from deployment directory docker build tapis-${SRVC_API}/Dockerfile"
echo "***      docker image build --build-arg VER=$VER --build-arg GIT_COMMIT=$GIT_COMMIT  -t $TAG-$TAPIS_ENV . "
               docker image build --build-arg VER=$VER --build-arg GIT_COMMIT=$GIT_COMMIT  -t $TAG-$TAPIS_ENV .
               
echo "";echo "point 4";echo ""

# echo "***    push the image to docker hub "
 echo "***      export META_IMAGE=$TAG-$TAPIS_ENV"
                export META_IMAGE=$TAG-$TAPIS_ENV
# echo "         push docker hub  -  $META_IMAGE   currently NA "
                # docker push "$META_IMAGE"
 echo "***      tag image for our private repository  -  jenkins2.tacc.utexas.edu:5000/$META_IMAGE"
                 docker tag $META_IMAGE jenkins2.tacc.utexas.edu:5000/$META_IMAGE
                 docker push jenkins2.tacc.utexas.edu:5000/$META_IMAGE

echo "***      "
echo "***      rm -rf ${IMAGE_BUILD_DIR}/${JAR_NAME}"
               # rm -rf ${IMAGE_BUILD_DIR}/${WAR_NAME}

