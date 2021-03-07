#!/bin/bash
# this current token is for meta user with admin tenant. Assumes all access permissions for meta user are set.
export token="<meta admin token>"
export base_url="https://dev.tenants.aloedev.tacc.cloud"
export header1="X-Tapis-Token: ${token}"
# for tests with output included set this variable to "" default is "-o /dev/null" for no output.
export includeOutput="-o /dev/null"



