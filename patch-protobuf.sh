#!/bin/bash
SCRIPT_PATH=$(cd $(dirname $0); pwd -P)
PROTO_PATH="$SCRIPT_PATH/../proto"

cd $PROTO_PATH
for i in $SCRIPT_PATH/protobuf-patches/*.patch; do
	echo "-> Patch: $i"
	patch -p0 < $i
done
