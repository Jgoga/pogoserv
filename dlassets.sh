#!/bin/sh
SCRIPT_PATH=$(cd $(dirname $0); pwd -P)
PTC_USERNAME=$1
shift
PTC_PASSWORD=$1
shift
rm -r "$SCRIPT_PATH/data/assets"
mkdir -pv "$SCRIPT_PATH/data/assets/"
PYTHONPATH="$SCRIPT_PATH/lib/pyapi/" python "$SCRIPT_PATH/dlassets.py" \
			-u "$PTC_USERNAME" \
			-p "$PTC_PASSWORD" \
			-d "$SCRIPT_PATH/data/" \
			$@
