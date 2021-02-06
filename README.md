# TapisV2 - Meta V3 java
Texas Advanced Computing Center APIs
Currently images are built locally and pushed to private repo. Some needed jars were deleted from the .m2 local repository on jenkins3. The build should work but doesn't. It is much faster building on jenkins than it is on my local machine.

Git repo :   https://github.com/sterry1/tapisV2-java.git
default branch "dev"

### Building local

 mvn clean install -DskipTests=true compiles all java artifacts

 deployment/build-All-lcl.sh will build images from java artifacts.
 1. tapis-meta-base
 2. metaapi
 3. metawrkr

### Setup
There are multiple ways to run in a local development mode.
* all local using docker compose file meta-all-3-tst.yml
*  hybrid approach running
   * security connected to dev,staging, prod environment
   * wrkr the same

### Deployment in dev
1. check the config files in etc.
2. view the defaults
3. modify core-compose repo files to update image version.
4. on each host aloe.dev.2, aloe.dev.3 run update-repo.sh and update-meta.sh
5. use burnup and burndown on each host   
6. run smoke tests for LRQs while watching logs.

### Deployment in staging
1. check the config files in etc.
2. view the defaults
3. modify core-compose repo files to update image version.
4. on each host aloe.dev.6, aloe.dev.7 run update-repo.sh and update-meta.sh
5. use burnup and burndown on each host

### Deployment in prod
1. check the config files in etc.
2. view the defaults
3. modify core-compose repo files to update image version.
4. on each host  api-prod (api) and vdj-lrq (wrkr) run update-repo.sh and update-meta.sh
4. use burnup and burndown on api-prod (api) and vdj-lrq (wrkr).

  