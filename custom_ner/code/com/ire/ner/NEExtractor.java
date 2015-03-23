package com.ire.ner;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

/**
 * _USR (user eg @shwetha) 
 * _HT (hash tag eg #felicity) 
 * _NNP (eg Boehner)
 * _NNP+ ( repeated occurence of NNP) 
 * _NNP+ _IN _NNP+ ( two sets of NNP with preposition or subordinating conjunction in between
 * 
 * 
 **/

public class NEExtractor {
	private static final char _ = '_';
	private static final String PIPE = "|";
	private static final String _NNP = "_NNP";
	private static final String _HT = "_HT";
	private static final String _USR = "_USR";
	private static final String _IN = "_IN";

	private static final String INP_FILE = "tagged_doc.txt";

	private List<String> getNamedEntities() throws FileNotFoundException {
		List<String> list = new ArrayList<String>();
		Scanner sc = new Scanner(new File(INP_FILE));
		String tweet, nes;
		while (sc.hasNext()) {
			tweet = sc.nextLine();
			nes = getNEs(tweet);
			list.add(nes);
		}
		sc.close();
		return list;
	}

	private String getNEs(String tweet) {
		String words[] = tweet.split(" ");
		String word, nes = "";
		int i = 0;
		for (i = 0; i < words.length; i++) {
			word = words[i];
			if (word.endsWith(_HT) || word.endsWith(_USR)) {
				nes += word.substring(1, word.indexOf(_)) + PIPE;
			} else if (word.endsWith(_NNP)) {
				while (word.endsWith(_NNP)) {
					nes += word.substring(0, word.indexOf(_)) + " ";
					i++;
					if (i == words.length)
						break;
					word = words[i];
				}
				if(i!=words.length && i+1 != words.length && words[i].endsWith(_IN) && words[i+1].endsWith(_NNP)){
					nes += word.substring(0, word.indexOf(_)) + " ";
					i++;
					word = words[i];
					while (word.endsWith(_NNP)) {
						nes += word.substring(0, word.indexOf(_)) + " ";
						i++;
						if (i == words.length)
							break;
						word = words[i];
					}
				}
				nes += PIPE;
				i--;
			}

		}
		if(nes.contains(PIPE))
			return nes.substring(0, nes.lastIndexOf(PIPE));
		return nes;
	}

	public static void main(String[] args) {
		NEExtractor ner = new NEExtractor();

		try {
			List<String> nerlist = ner.getNamedEntities();
			PrintWriter writer = new PrintWriter(new File("output.txt"));
			for (String s : nerlist) {
				if ("".equals(s))
					writer.println("{}");
				else
					writer.println(s);
			}
			writer.close();
		} catch (IOException e) {
			System.out.println("Exception in fetching named entities "
					+ e.getMessage());
		}
	}

}
