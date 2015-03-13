#!/bin/bash
echo $1
java -jar gateNer.jar $1 $2

fil="./res.xml"

while read line
do
	pat=`echo $line | egrep -o ">(\w*\s*)*</" | tr -d ">" | tr -d "</"`
	pat=`echo $pat | tr ' ' '|'`
	echo $pat >> output.txt
done < $fil