#!/bin/sh

set -e

if [ "$1" != "configure" ]; then
    exit 0
  fi

service stub-idp restart
