#!/bin/bash

tr -cd '\11\12\15\40-\176' < $2 > no_noise.txt
sed 's/[^a-zA-Z0-9#?.@]/ /g' no_noise.txt  | tr -s ' ' > no_spl.txt
# replace # with <space># to ensure tags like #india#cricket gets identified properly
tr '#' ' #' < no_spl.txt > no_noise.txt 

if [ -e gate_ner_output.txt ] 
	then
	rm gate_ner_output.txt
fi

java -jar gateNer.jar $1 no_noise.txt



fil="./res.xml"

while read line
do
	pat=`echo $line | egrep -o ">(\w*\s*)*</" | tr -d ">" | tr -d "</"`
	pat=`echo $pat | tr ' ' '|'`
	if [ $pat ] 
	then
		echo $pat >> gate_ner_output.txt
	else
		echo "{}" >> gate_ner_output.txt
	fi
done < $fil

rm no_spl.txt
rm no_noise.txt