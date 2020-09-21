#!/usr/bin/env bash

# must run from the deployment directory

docker-compose -f meta-security-core.yml pull

sleep 10

docker-compose -f meta-security-core.yml down

sleep 10

docker-compose -f meta-security-core.yml up -d

 sleep 10

 docker-compose -f meta-security-core.yml  logs --tail 100 -f

