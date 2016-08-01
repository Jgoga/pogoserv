#!/bin/sh
SCRIPT_PATH=$(cd $(dirname $0); pwd -P)
JAVA="java"
PROTOBUF="$HOME/.m2/repository/com/google/protobuf/protobuf-java/3.0.0-beta-3/protobuf-java-3.0.0-beta-3.jar"
JSON="$HOME/.m2/repository/com/eclipsesource/minimal-json/minimal-json/0.9.4/minimal-json-0.9.4.jar"
S2="$HOME/.m2/repository/org/isuper/s2-geometry-library-java/0.0.1/s2-geometry-library-java-0.0.1.jar"
GUAVA="$HOME/.m2/repository/com/google/guava/guava/18.0/guava-18.0.jar"
SQLITE="$HOME/.m2/repository/org/xerial/sqlite-jdbc/3.8.11.2/sqlite-jdbc-3.8.11.2.jar"
POGOPROTO="$SCRIPT_PATH/lib/pogoproto.jar"

$JAVA -cp "$PROTOBUF":"$JSON":"$S2":"$GUAVA":"$SQLITE":"$POGOPROTO":"$SCRIPT_PATH/target/classes"\
		pm.cat.pogoserv.Main $@
