package com.ire.ner;


/**
 * _USR (user eg @username) _HT (hash tag eg #felicity) _NN(P) (eg Boehner)
 * _NN(P)+ ( repeated occurence of NN(P)) _NN(P)+ _IN _NN(P)+ ( two sets of
 * NN(P) with 'of' or 'for' eg Bank of Thailand)
 **/

public class NEExtractor {
	private static final String FULL_STOP = ".";
	private static final String COMMA = ",";
	private static final String QM = "?";
	private static final String EXCL = "!";
	private static final char _ = '_';
	private static final String PIPE = "|";
	private static final String _NN = "_NN";
	private static final String _NNS = "_NNS";
	private static final String _HT = "_HT";
	private static final String _USR = "_USR";
	private static final String _IN = "_IN";


	private String getNEs(String tweet) {
		boolean isNNP = false;
		String words[] = tweet.split(" ");
		String word, nes = "";
		int i = 0;
		for (i = 0; i < words.length; i++) {
			word = words[i];
			if (word.endsWith(_HT) || word.endsWith(_USR)) {
				nes += word.substring(1, word.indexOf(_)) + PIPE;
			} else if (word.contains(_NN)
					&& Character.isUpperCase(word.charAt(0))) {
				isNNP = true;
				while (word.contains(_NN)
						&& Character.isUpperCase(word.charAt(0))) {
					nes += word.substring(0, word.indexOf(_)) + " ";
					i++;
					// More than one NEs could be separated by , or .
					if (word.contains(COMMA) || word.contains(FULL_STOP)
							|| word.contains(QM) || word.contains(EXCL)) {
						nes = nes.replace(COMMA, "");
						nes = nes.replace(FULL_STOP, "");
						nes = nes.replace(QM, "");
						nes = nes.replace(EXCL, "");
						break;
					}
					if (i == words.length)
						break;
					word = words[i];
				}
				/*StringBuilder sb = new StringBuilder(nes);
				sb.deleteCharAt(sb.length() - 1);
				nes = sb.toString();
				*/
				
			} else if (word.endsWith(_NN) || word.endsWith(_NNS)) {
				while (word.endsWith(_NN) || word.endsWith(_NNS)) {
					nes += word.substring(0, word.indexOf(_)) + " ";
					i++;
					// More than one NEs could be separated by , or .
					if (word.contains(COMMA) || word.contains(FULL_STOP)
							|| word.contains(QM) || word.contains(EXCL)) {
						nes = nes.replace(COMMA, "");
						nes = nes.replace(FULL_STOP, "");
						nes = nes.replace(QM, "");
						nes = nes.replace(EXCL, "");
						break;
					}
					if (i == words.length)
						break;
					word = words[i];
				}
				StringBuilder sb = new StringBuilder(nes);
				sb.deleteCharAt(sb.length() - 1);
				nes = sb.toString();
				nes += PIPE;
				
				// NN after NNP not getting recognized
			}
			
			if (i != words.length && i + 1 != words.length
					&& words[i].endsWith(_IN)
					&& (word.equals("of_IN") || word.equals("for_IN"))
					&& words[i + 1].contains(_NN)) {
				System.out.println("here");
				nes += word.substring(0, word.indexOf(_)) + " ";
				i++;
				word = words[i];
				while (word.contains(_NN)) {
					nes += word.substring(0, word.indexOf(_)) + " ";
					i++;
					// More than one NEs could be separated by , or .
					if (word.contains(COMMA) || word.contains(FULL_STOP)
							|| word.contains(QM)) {
						nes = nes.replace(COMMA, "");
						nes = nes.replace(FULL_STOP, "");
						nes = nes.replace(QM, "");
						break;
					}
					word = words[i];

				}

				i--;
			}else if(isNNP){
				if(nes.endsWith(" "))
					nes = nes.substring(0, nes.length()-1);
				nes += "~" + PIPE;
				isNNP = false;
			}
			

		}
		if (nes.contains(PIPE))
			return nes.substring(0, nes.lastIndexOf(PIPE));
		return nes;
	}

	public String getNamedEntities(String taggedTweet) {
		NEExtractor ner = new NEExtractor();

		String nes = ner.getNEs(taggedTweet);
		return nes;

	}

}
