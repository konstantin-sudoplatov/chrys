#!/bin/sh
export PGDATABASE=chris
export PGUSER=chris
export PGPASSWORD=chris
pg_dump -Fc > chris.dmp