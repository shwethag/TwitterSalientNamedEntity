#!/bin/bash

cd tagger > /dev/null 
java -jar twitie_tag.jar model/gate-EN-twitter.model ../$1  > tagged_doc.txt
mv tagged_doc.txt ../
cd .. > /dev/null 