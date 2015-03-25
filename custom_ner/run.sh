#!/bin/bash

tr -cd '\11\12\15\40-\176' < $1 > no_noise.txt
sed 's/[^a-zA-Z0-9#?.@]/ /g' no_noise.txt  | tr -s ' ' > no_spl.txt

cd tagger > /dev/null 
java -jar twitie_tag.jar model/gate-EN-twitter.model ../no_spl.txt  > tagged_doc.txt
mv tagged_doc.txt ../
cd .. > /dev/null 
javac -d . code/com/ire/ner/*.java
java com.ire.ner.NEExtractor
rm no_spl.txt
rm no_noise.txt