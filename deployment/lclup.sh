#!/usr/bin/env bash
# export JAVA_OPTS="-Xms1g -Xmx2g -Dlogback.configurationFile=/metawrkr/logback.xml"

# docker-compose -f meta-security-core-tst.yml -p lcl up -d
# docker-compose -f meta-worker-tst.yml --env-file ./etc/worker.env -p lcl up -d

docker-compose -f meta-all-3-tst.yml --env-file ./etc/worker.env -p lcl up -d