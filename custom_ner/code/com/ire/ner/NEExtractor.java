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
 * _NN(P) (eg Boehner)
 * _NN(P)+ ( repeated occurence of NN(P)) 
 * _NN(P)+ _IN _NN(P)+ ( two sets of NN(P) with 'of' or 'for' eg Bank of Thailand)
 **/

public class NEExtractor {
	private static final String FULL_STOP = ".";
	private static final String COMMA = ",";
	private static final char _ = '_';
	private static final String PIPE = "|";
	private static final String _NN = "_NN";
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
			}else if (word.contains(_NN)) {
				while (word.contains(_NN)) {
					nes += word.substring(0, word.indexOf(_)) + " ";
					i++;
					// More than one NEs could be separated by , or .
					if (word.contains(COMMA) || word.contains(FULL_STOP)) {
						nes = nes.replace(COMMA, "");
						nes = nes.replace(FULL_STOP, "");
						break;
					}
					if (i == words.length)
						break;
					word = words[i];
				}
				if (i != words.length && i + 1 != words.length
						&& words[i].endsWith(_IN)
						&& (word.equals("of") || word.equals("for"))
						&& words[i + 1].contains(_NN)) {
					nes += word.substring(0, word.indexOf(_)) + " ";
					i++;
					word = words[i];
					while (word.contains(_NN)) {
						nes += word.substring(0, word.indexOf(_)) + " ";
						i++;
						// More than one NEs could be separated by , or .
						if (word.contains(COMMA) || word.contains(FULL_STOP)) {
							nes = nes.replace(COMMA, "");
							nes = nes.replace(FULL_STOP, "");
							break;
						}
						if (i == words.length)
							break;
						word = words[i];
					}
				}
				nes += PIPE;
				i--;
			}

		}
		if (nes.contains(PIPE))
			return nes.substring(0, nes.lastIndexOf(PIPE));
		return nes;
	}

	public static void main(String[] args) {
		NEExtractor ner = new NEExtractor();

		try {
			List<String> nerlist = ner.getNamedEntities();
			PrintWriter writer = new PrintWriter(new File("custom_ner_output.txt"));
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
