#!/usr/bin/env bash

#  "rhtestuser-prod-bearer":{
#  "comment": "app: rhtestapp, key: 87IjLfzqqmb0iUTWI3RLu3o1v5Ea, secret: rQnMm1mtcOjVvHWlcx8heL63gA8a",
#  "host": "https://vdj-agave-api.tacc.utexas.edu/",
#  "token": "e29050e698c6ba99da3ca1a21dc34f",

export tok=e29050e698c6ba99da3ca1a21dc34f
export host=https://vdj-agave-api.tacc.utexas.edu
export base=meta/v3

###################################################
#​
# simple list of collections in the api database
# curl -X GET -H "Accept: application/json" -H "Authorization : Bearer $tok" "$host/$base"/api | jq  # > result.json

# POST of a long running query
# currently timeout occurs at 1 minute on client side and 3 minutes on core server.
# curl -v -H 'Authorization:Bearer e29050e698c6ba99da3ca1a21dc34f' -H 'Content-Type: application/json' -d '{"vdjserver_junction_suffixes":{"$regex":"^SLQGATEAF"}}' https://vdj-agave-api.tacc.utexas.edu/meta/v3/v1airr/rearrangement_0/_filter


###################################################
#​
#  try this with jwt for vdj-guest
export jwt=eyJ0eXAiOiJKV1QiLCJhbGciOiJSUzI1NiIsIng1dCI6Ik5tSm1PR1V4TXpabFlqTTJaRFJoTlRabFlUQTFZemRoWlRSaU9XRTBOV0kyTTJKbU9UYzFaQT09In0=.eyJpc3MiOiJ3c28yLm9yZy9wcm9kdWN0cy9hbSIsImV4cCI6MTU5NzYwMDQ2ODA4NSwiaHR0cDovL3dzbzIub3JnL2NsYWltcy9zdWJzY3JpYmVyIjoiVkRKL3ZkaiIsImh0dHA6Ly93c28yLm9yZy9jbGFpbXMvYXBwbGljYXRpb25pZCI6IjI2NiIsImh0dHA6Ly93c28yLm9yZy9jbGFpbXMvYXBwbGljYXRpb25uYW1lIjoidmRqX2FpcnIiLCJodHRwOi8vd3NvMi5vcmcvY2xhaW1zL2FwcGxpY2F0aW9udGllciI6IlVubGltaXRlZCIsImh0dHA6Ly93c28yLm9yZy9jbGFpbXMvYXBpY29udGV4dCI6Ii9tZXRhL3YzIiwiaHR0cDovL3dzbzIub3JnL2NsYWltcy92ZXJzaW9uIjoidjMiLCJodHRwOi8vd3NvMi5vcmcvY2xhaW1zL3RpZXIiOiJVbmxpbWl0ZWQiLCJodHRwOi8vd3NvMi5vcmcvY2xhaW1zL2tleXR5cGUiOiJQUk9EVUNUSU9OIiwiaHR0cDovL3dzbzIub3JnL2NsYWltcy91c2VydHlwZSI6IkFQUExJQ0FUSU9OX1VTRVIiLCJodHRwOi8vd3NvMi5vcmcvY2xhaW1zL2VuZHVzZXIiOiJ2ZGotZ3Vlc3RAY2FyYm9uLnN1cGVyIiwiaHR0cDovL3dzbzIub3JnL2NsYWltcy9lbmR1c2VyVGVuYW50SWQiOiItMTIzNCIsImh0dHA6Ly93c28yLm9yZy9jbGFpbXMvZW1haWxhZGRyZXNzIjoidmRqc2VydmVyQHV0c291dGh3ZXN0ZXJuLmVkdSIsImh0dHA6Ly93c28yLm9yZy9jbGFpbXMvZnVsbG5hbWUiOiJ2ZGotZ3Vlc3QiLCJodHRwOi8vd3NvMi5vcmcvY2xhaW1zL2xhc3RuYW1lIjoidmRqLWd1ZXN0IiwiaHR0cDovL3dzbzIub3JnL2NsYWltcy9yb2xlIjoiSW50ZXJuYWwvdmRqLW1ldGF2MyxJbnRlcm5hbC9ldmVyeW9uZSJ9.WfHOi53swUkYIhr82QpQ4YswKJyMfdrJtHxP5ls5JhiyZ01O/ziPR8hYzwikxdvMXsFAaxTpnCPNsAPnE2fOtYhr97VQlgcFlqzRknLWNyUpsnyE29yhjCA3ITMdDOgfl/1qxEOIDjii+tpp1CGaGopMX/xVIQxOInLC+iXlpGg=

# going thru the api temp proxy times out after 3 minutes
# curl --write-out '%{time_total}' -H "X-JWT-Assertion-vdjserver.org: $jwt" -H 'Content-Type: application/json' -d '{"vdjserver_junction_suffixes":{"$regex":"^SLQGATEAF"}}' http://api-prod-agave.tacc.utexas.edu:81/meta/v3/v1airr/rearrangement_0/_filter
req=$(curl -v -H "X-JWT-Assertion-vdjserver.org: $jwt" -H "Accept: application/json" -H 'Content-Type: application/json' -d '{"vdjserver_junction_suffixes":{"$regex":"^SLQGATEAF"}}' http://api-prod-agave.tacc.utexas.edu:81/meta/v3/v1airr/rearrangement_0/_filter)

# req=$(curl -s -X GET http://host:8080/some/resource -H "Accept: application/json") 2>&1
echo "${req}"