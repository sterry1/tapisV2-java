#!/usr/bin/env bash
​
# testuser7
# client app: rsthrtapp key: k258KfoXvHLor0TyF310QNbria0a  secret: iGG8JW1m3i1ev22k265qvj65Ndwa
# forever token: a5ea10c768d3ad8d612bebf2405cfd
​
export tok=a5ea10c768d3ad8d612bebf2405cfd
export host=https://dev.tenants.aloedev.tacc.cloud
export base=meta/v3
​
###################################################
​
# simple list of collections in the api database
# curl -X GET -H "Accept: application/json" -H "Authorization : Bearer $tok" "$host/$base"/api | jq  # > result.json
​
# simple list of documents from the metadata collection
curl -X GET -H "Accept: application/json" -H "Authorization : Bearer $tok" "$host/$base"/api/metadata | jq  # > result.json
​
# create a document in metadata collection
# limit for document size is 16M otherwise you will need to use buckets
# curl -X POST -H "Authorization : Bearer $tok" -H "Content-Type: application/json" -d "@document1.json" "$host/$base"/api/metadata
​
# get the document created in metadata
# curl -H "Accept: application/json" -H "Authorization : Bearer $tok" "$host/$base"/api/metadata/5d095783028ae24619924ea0 | jq
​
​
​
​
​
###################################################
​
# create a collection for large files
# curl -X PUT -H "Content-Type: application/json" -H "Authorization : Bearer $tok" "$host/$base"/api/mybucket.files
​
# add a large file to collection defaulting to MongoDB naming
# curl -X POST -H "Authorization : Bearer $tok" -F "file=@binary-data/jenkins-the-definitive-guide.pdf" "$host/$base"/api/mybucket.files
​
# simple list of api/mybucket.files
# curl -H "Accept: application/json" -H "Authorization : Bearer $tok" "$host/$base"/api/mybucket.files | jq
​
# using PUT to give large file a meaningful name
# curl -v -X PUT -H "Authorization : Bearer $tok" -F "file=@binary-data/jenkins-the-definitive-guide.pdf" "$host/$base"/api/mybucket.files/jenkins-the-definitive-guide.pdf | jq
​
# simple list of api/mybucket.files
# curl -H "Accept: application/json" -H "Authorization : Bearer $tok" "$host/$base"/api/mybucket.files/jenkins-the-definitive-guide.pdf | jq
​
# get the binary of a file /api/mybucket.files/jenkins-the-definitive-guide.pdf
# curl -H "Accept: application/json" -H "Authorization : Bearer $tok" "$host/$base"/api/mybucket.files/jenkins-the-definitive-guide.pdf/binary > tmp.pdf
