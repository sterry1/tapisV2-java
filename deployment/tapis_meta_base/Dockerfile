##
#  Usage: docker build -t tapis/tapis-meta-base
#  $> docker run -it tapis/tapis-meta-base:latest bash java -version; mongoexport --version
#
FROM openjdk:13-oraclelinux7

MAINTAINER CIC Support <cicsupport@tacc.utexas.edu>

COPY mongodb-org-4.4.repo /etc/yum.repos.d/mongodb-org-4.4.repo

RUN yum -y install nano; yum -y install mongodb-database-tools

CMD bash
