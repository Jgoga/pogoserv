#!/bin/bash
JAVAC="javac"
JAR="jar"
PROTOBUF_JAR="$HOME/.m2/repository/com/google/protobuf/protobuf-java/3.0.0-beta-3/protobuf-java-3.0.0-beta-3.jar"
SCRIPT_PATH=$(cd $(dirname $0); pwd -P)
PROTO_PATH="$SCRIPT_PATH/../proto/"
OUT_PATH="$SCRIPT_PATH/lib/pogoproto.jar"

echo "-> Compiling proto files"
cd "$PROTO_PATH"
if [ ! -f "./compile_single_proto3.py" ]; then
	echo "-> (Patching compile_single.py)"
	sed 's/\"protoc\"/\"protobuf3-protoc\"/g' "./compile_single.py" > "./compile_single_proto3.py"
fi
python2 "./compile_single_proto3.py" -l java -o __protobuf_tmp__

echo "-> Compiling java files"
shopt -s globstar
$JAVAC -cp "$PROTOBUF_JAR" __protobuf_tmp__/**/*.java

echo "-> Packing jar: $OUT_PATH"
cd __protobuf_tmp__
$JAR cf "$OUT_PATH" **/*.class

echo "-> Removing temp files"
cd ..
rm -rf __protobuf_tmp__

echo "-> Done!"
