##########################################################################
# tapis image for metawrkr
#
# usage:
#   docker build -f $BUILD_FILE --build-arg SRVC_WAR=meta.war --build-arg VER=0.1 --build-arg GIT_COMMIT=1234 -t $TAG .
#
#   ${pwd}          the root of the git repository working directory.
#   $SRVC_WAR       the aloe service api being installed as a war file from target
#                     directory   ex. target/jobs.war
#   $CATALINA_HOME  set as installation convention from tomcat image
#   $TAG            the tag for image identification
#
#   Tested with Docker version 18.06.0-ce
##########################################################################
FROM openjdk:13-oraclelinux7

ARG VER
ARG GIT_COMMIT

MAINTAINER CIC Support <cicsupport@tacc.utexas.edu>

RUN yum -y install nano

WORKDIR /metawrkr

# COPY dependencies to image
# add
COPY metawrkr /metawrkr

# Set label for git revision
LABEL "GIT_COMMIT"=$GIT_COMMIT

# Launch worker on startup
# CMD java -cp jobslib.jar:lib/* ${JAVA_OPTS} edu/utexas/tacc/aloe/jobs/worker/JobWorker -n ${ALOE_JOBWKR_NAME} -t ${ALOE_JOBWKR_TENANT} -w ${ALOE_JOBWKR_WORKER_THREADS} -q ${ALOE_JOBWKR_QUEUE} ${ALOE_JOBWKR_OPTS}
# CMD java -cp metawrkr.jar:lib/* edu/utexas/tacc/tapis/meta/LRQWorker
CMD bash