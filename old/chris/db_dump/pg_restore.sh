#!/bin/sh
sudo -u postgres pg_restore -Ccv -d postgres chris.dmp