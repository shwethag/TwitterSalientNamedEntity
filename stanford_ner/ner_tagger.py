import ner
tagger = ner.SocketNER(host='localhost', port=8080)
v=open("../Dataset/dataset_tweets.txt").readlines()
w=open("ne_dataset_tweets.txt","w")
for i in v:
	ne={}
	ne=tagger.get_entities(i)
	if len(ne) is 0:
		w.write("{}\n")
		continue
	for i in ne:
		for j in ne[i]:
			w.write(j.encode('ascii', 'replace'))
			w.write("|")
	w.write("\n")
w.close()
		



