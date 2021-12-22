#!/bin/sh

set -e

which java > /dev/null || { echo "You need to install Java >= 8" ; exit 1 ; }

mytmpdir=$(mktemp -d 2>/dev/null || mktemp -d -t 'mytmpdir')

echo "$mytmpdir"/linked-data-fu-0.9.12 > .tmppath

cd "$mytmpdir"

wget --quiet https://github.com/linked-data-fu/linked-data-fu.github.io/raw/master/releases/0.9.12/linked-data-fu-standalone-0.9.12-bin.tar.gz

tar xzf linked-data-fu-standalone-0.9.12-bin.tar.gz

# Patching LDFu
rm linked-data-fu-0.9.12/lib/nxparser-parsers-5f525167a26773c482a63256065ae8d21addd06f.jar
wget --quiet https://search.maven.org/remotecontent?filepath=org/semanticweb/yars/nxparser-parsers/3.0.1/nxparser-parsers-3.0.1.jar -O linked-data-fu-0.9.12/lib/nxparser-parsers-3.0.1.jar

echo "initialised :)"
