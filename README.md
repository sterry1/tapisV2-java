# tapis-java
Texas Advanced Computing Center APIs

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

### Deployment
1. check the config files in etc.
2. view the defaults
3. modify core-compose repo files to update image version.
4. use burnup and burndown on api-prod (api) and vdj-lrq (wrkr).

  