#!/bin/sh
SCRIPT_PATH=$(cd $(dirname $0); pwd -P)
JAVA="java"
PROTOBUF="$HOME/.m2/repository/com/google/protobuf/protobuf-java/3.0.0-beta-3/protobuf-java-3.0.0-beta-3.jar"
JSON="$HOME/.m2/repository/com/eclipsesource/minimal-json/minimal-json/0.9.4/minimal-json-0.9.4.jar"
S2="$SCRIPT_PATH/lib/s2-geometry-java.jar"
GUAVA="$SCRIPT_PATH/lib/guava-r09.jar"
POGOPROTO="$SCRIPT_PATH/lib/pogoproto.jar"

$JAVA -cp "$PROTOBUF":"$JSON":"$S2":"$GUAVA":"$POGOPROTO":"$SCRIPT_PATH/target/classes" pm.cat.pogoserv.Main $@
