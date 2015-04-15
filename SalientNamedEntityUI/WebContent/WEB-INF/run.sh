#!/bin/bash
cd $2/wtpwebapps/SalientNamedEntityUI/WEB-INF
pwd
no_noise=`echo $1 | tr -cd '\11\12\15\40-\176'`
no_spl=`echo $no_noise | sed 's/[^a-zA-Z0-9#.@,]/ /g' | tr -s ' ' `
# replace # with <space># to ensure tags like #india#cricket gets identified properly
h_sp=`echo $no_spl | sed 's/#/ #/g'`
echo $h_sp > no_noise.txt
cd tagger
java -jar twitie_tag.jar model/gate-EN-twitter.model ../no_noise.txt  
cd .. > /dev/null 
