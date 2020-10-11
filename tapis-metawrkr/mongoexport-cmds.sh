#!/usr/bin/env bash
# --out=events.json [additional options]
echo "testing mongoexport of simple query"
echo ""

# export URI="mongodb://tapisadmin:d3f%40ult@aloe-dev08.tacc.utexas.edu:27019/v1airr?authSource\=admin"
# mongoexport --host=129.114.103.27 --port 27017 --db=v1airr --collection=rearrangement --out=/tmp/scott.json --query='{"repertoire_id":"7696940659763179030-242ac116-0001-012"}'
# echo "version of mongoexport"
# mongoexport --version

# echo " "

mongoexport -h=aloe-dev08.tacc.utexas.edu:27019 -u=tapisadmin -p=d3f@ult --authenticationDatabase=admin -d=v1airr -c=rearrangement -o=onejson.json -f="repertoire_id,locus" -q='{"repertoire_id":"1993707260355416551-242ac11c-0001-012"}'

