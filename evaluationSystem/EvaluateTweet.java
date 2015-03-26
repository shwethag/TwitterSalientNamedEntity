import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.ArrayList;

//Add code to dynamically get the file path :

/*
 * Evalautaion system for tweets
 * Approach :3 files, one +ve values (all record matches), -ve(none record matches), p+ve(records matching partially)
 */
//@Author : Joshita Mishra
public class EvaluateTweet {

	// compare two files initally making one file as base file :

	public static void compareOutputFiles(String file1InputFile,
			String file2InputFile, String file1_o) throws Throwable {

		try {
			// Files for Input
			File f1 = new File(file1InputFile);
			File f2 = new File(file2InputFile);

			// Files for Output :
			File f1_o = new File(file1_o);
			File f2_o = new File("fileforInternalUse.txt");
			deleteFileIfExists(f1_o);
			deleteFileIfExists(f2_o);

			ArrayList<String> listOfWordsFile1 = new ArrayList<String>();
			ArrayList<String> listOfWordsFile2 = new ArrayList<String>();

			FileReader fR1 = new FileReader(f1);
			FileReader fR2 = new FileReader(f2);

			BufferedReader reader1 = new BufferedReader(fR1);
			BufferedReader reader2 = new BufferedReader(fR2);

			String line1 = null;
			String line2 = null;

			boolean res1 = false;
			boolean res2 = false;

			int postitivecount = 0, partialcount = 0, missingcount = 0, incorrectcount = 0;

			// String s=reader1.readLine();

			while (((line1 = reader1.readLine()) != null)) {

				listOfWordsFile1 = new ArrayList<String>();
				listOfWordsFile2 = new ArrayList<String>();

				line2 = reader2.readLine(); // Reading the second file

				String replacement1 = line1.replace("|", ",");
				String[] array1 = replacement1.split(",");

				String replacement2 = line2.replace("|", ",");
				String[] array2 = replacement2.split(",");

				// Every time this only happens for 1 line
				for (int i = 0; i < array1.length; i++) {

					System.out.println(array1[i]);
					listOfWordsFile1.add(array1[i]);

				}

				// Every time this only happens for 1 line
				for (int i = 0; i < array2.length; i++) {

					System.out.println(array2[i]);
					listOfWordsFile2.add(array2[i]);
				}

				for (String arr1 : listOfWordsFile1) {

					System.out.println("Inside for loop : comparing results ");
					res1 = matchWordExactly(arr1, listOfWordsFile2);
					if (res1) {
						postitivecount++;
					}

					if (!res1) {
						res2 = matchWordPartially(arr1, listOfWordsFile2);
						if (res2) {
							partialcount++;
						}
					}
					if (!res2 && !res1) {
						missingcount++;
					}

				}// for loop

			}

			// Another loop to cal Incorrect matrix
			while (((line1 = reader1.readLine()) != null)) {

				listOfWordsFile1 = new ArrayList<String>();
				listOfWordsFile2 = new ArrayList<String>();

				line2 = reader2.readLine(); // Reading the second file

				String replacement1 = line1.replace("|", ",");
				String[] array1 = replacement1.split(",");

				String replacement2 = line2.replace("|", ",");
				String[] array2 = replacement2.split(",");

				// Every time this only happens for 1 line
				for (int i = 0; i < array1.length; i++) {

					System.out.println(array1[i]);
					listOfWordsFile1.add(array1[i]);

				}

				for (int i = 0; i < array2.length; i++) {

					System.out.println(array2[i]);
					listOfWordsFile2.add(array2[i]);
				}

				for (String arr2 : listOfWordsFile2) {

					if (!res1) {
						res2 = matchWordsIncorrect(listOfWordsFile1, arr2);
						if (res2) {
							incorrectcount++;
						}
					}
				}// for loop

			}
			reader1.close();
			reader2.close();

			countToFile(postitivecount, "correct", f1_o);
			countToFile(partialcount, "partialCorrect", f1_o);
			countToFile(missingcount, "missing", f1_o);
			countToFile(incorrectcount, "incorrect", f1_o);

			// For internal use only : not for reference
			countToFile_OnlyNumeric(postitivecount);
			countToFile_OnlyNumeric(partialcount);
			countToFile_OnlyNumeric(missingcount);
			countToFile_OnlyNumeric(incorrectcount);
			ArrayList<String> arrlist = readLinesFromFiles("fileforInternalUse.txt");
			String recall=calRecall(arrlist);
			String precision=calPrecision(arrlist);
			
			//This will print results:
			printToFile("Recall",recall,f1_o);
			printToFile("Precision",precision,f1_o);

		} catch (Exception e) {
			System.out.println("There is error in reading file");
			e.printStackTrace();
		}
	}

	public static boolean matchWordExactly(String word,
			ArrayList<String> wordlist) {

		boolean flag = false;

		for (String word_match : wordlist) {

			if (word_match.equals(word)) {
				flag = true;
				System.out.println("\n Word has matched exactly");
			}
		}
		return flag;
	}

	public static boolean matchWordPartially(String word,
			ArrayList<String> wordlist) {

		boolean flag = false;

		for (String word1 : wordlist) {
			if (word.contains(word1) || word1.contains(word)) {
				flag = true;
				System.out.println("\n word matches partially");
			}
		}
		return flag;

	}

	public static boolean matchWordsIncorrect(ArrayList<String> arraylist1,
			String word) {

		boolean flag = false;

		for (String word1 : arraylist1) {
			if (word.contains(word1) || word1.contains(word)) {
				flag = true;
			}
		}
		return flag;

	}

	/*
	 * Calculate Recall and pression :
	 */
	public static String calRecall(ArrayList<String> list) {

		String correct = null;
		String incorrect = null;
		String missing = null;
		String partiallycorrect = null;
		int i = 0;

		for (String word : list) {
			i++;
			if (i == 1) {
				correct = word ;
			}
			if (i == 2) {
				partiallycorrect = word;
			}
			if (i == 3) {
				missing = word ;
			}
			if (i == 4) {
				incorrect = word  ;
			}
		}
		Integer value_correct = Integer.valueOf(correct);
		Integer value_incorrect = Integer.valueOf(incorrect);
		Integer value_missing = Integer.valueOf(missing);
		Integer value_partiallycorrect = Integer.valueOf(partiallycorrect);
		
		double recall1 = value_correct + (0.5 * value_partiallycorrect);
		recall1 = recall1 / (value_correct + value_partiallycorrect + value_missing);
		recall1 *= 10;
		String recall= Double.toString(recall1);

		return recall;
	}

	/*
	 * Calculate pression :
	 */
	public static String calPrecision(ArrayList<String> list) {

		String correct = null;
		String incorrect = null;
		String partiallycorrect = null;
		int i = 0;
		
		for (String word : list) {
			i++;
			if (i == 1) {
				correct = word;
			}
			if (i == 2) {
				partiallycorrect = word;
			}
			if (i == 4) {
				incorrect = word;
			}
		}
		
		Integer value_correct = Integer.valueOf(correct);
		Integer value_incorrect = Integer.valueOf(incorrect);
		Integer value_partiallycorrect = Integer.valueOf(partiallycorrect);
		
		double precision1 = value_correct + (0.5 * value_partiallycorrect);
		precision1 = precision1 / ( value_correct + value_incorrect + value_partiallycorrect);
		precision1 *= 10;
		String precision=Double.toString(precision1);
		
		return precision;
	}

	public static void deleteFileIfExists(File file) {

		if (file.exists()) {
			boolean result = file.delete();
			if (result) {
				System.out.println("File deletion successful");
			}
			try {
				file.createNewFile();
			} catch (IOException e) {
				// TODO Auto-generated catch block
				System.out.println("Cannot create file , cannot move further");
				e.printStackTrace();
			}
		} else {
			try {
				file.createNewFile();
			} catch (IOException e) {
				// TODO Auto-generated catch block
				System.out.println("Cannot create file , cannot move further");
				e.printStackTrace();
			}
		}
	}

	public static ArrayList<String> readLinesFromFiles(String fileforInternalUse) {

		File file = new File(fileforInternalUse);

		ArrayList<String> arraylist = new ArrayList<String>();
		try {
			file.createNewFile();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		FileReader fR1 = null;
		try {
			fR1 = new FileReader(file);
		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		BufferedReader reader1 = new BufferedReader(fR1);
		String line1 = null;

		try {
			while (((line1 = reader1.readLine()) != null)) {
				if(line1.equals("")){
					
				}else{
				arraylist.add(line1);
				}
			}
			reader1.close();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		return arraylist;
	}

	public static void countToFile_OnlyNumeric(int countNumber) {
		PrintWriter writer_file3;

		try {
			writer_file3 = new PrintWriter(new FileWriter(
					"fileforInternalUse.txt", true));
			writer_file3.printf("\n%s", countNumber);
			writer_file3.close();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	public static void countToFile(int countNumber, String type,
			File filetoPrint) {
		PrintWriter writer_file3;

		try {
			writer_file3 = new PrintWriter(new FileWriter(filetoPrint, true));
			writer_file3.printf("\n%s:%s", type, countNumber);
			writer_file3.close();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	public static void printToFile(String toPrint, String result,
			File filetoPrint) {

		PrintWriter writer_file1;

		try {
			writer_file1 = new PrintWriter(new FileWriter(filetoPrint, true));
			writer_file1.printf("\n%s:%s", toPrint, result);
			writer_file1.close();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	public static void main(String args[]) {

		try {
			// arg1 : for input file 1(manual file), arg2: other file, arg3 :
			// name of result file
			EvaluateTweet.compareOutputFiles(args[0], args[1], args[2]);
		} catch (Throwable e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

	}
}
