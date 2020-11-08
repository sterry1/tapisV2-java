#!/usr/bin/env bash
export JAVA_OPTS="-Xms1g -Xmx2g -Dlogback.configurationFile=/metawrkr/logback.xml"

# docker-compose -f meta-security-core-tst.yml -p lcl down
# docker-compose -f meta-worker-tst.yml -p lcl down

docker-compose -f meta-all-3-tst.yml -p lcl down
