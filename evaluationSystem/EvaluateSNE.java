import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Scanner;

public class EvaluateSNE {

	private String srcFile;
	private String genFile;
	private double precision, recall, fScore;
	private long positiveCnt, negativeCnt, partialCnt, missingCnt;

	public EvaluateSNE() {
		precision = recall = fScore = 0.0;

		positiveCnt = negativeCnt = partialCnt = missingCnt = 0;
	}

	private void printResults() throws IOException {
		PrintWriter writer = new PrintWriter(new File("./output.txt"));

		System.out.println();
		System.out.println("Positive count -->" + positiveCnt);
		writer.println("Positive count -->" + positiveCnt);
		
		System.out.println("Negative count --> " + negativeCnt);
		writer.println("Negative count -->" + negativeCnt);
		
		System.out.println("Partial match count -->" + partialCnt);
		writer.println("Partial match count -->" + partialCnt);
		
		System.out.println("Missing count -->" + missingCnt);
		writer.println("Missing count -->" + missingCnt);
		
		precision = (positiveCnt + 0.5 * partialCnt) / (double)(positiveCnt + negativeCnt + partialCnt);
		precision *= 100.0;
		
		recall = (positiveCnt + 0.5 * partialCnt) / (double)(positiveCnt + partialCnt + missingCnt);
		recall *= 100.0;
		
		fScore = (2 * precision * recall)/(precision+recall);
		
		System.out.println("PRECISION :" + precision);
		writer.println("PRECISION :" + precision);
		
		System.out.println("RECALL :" + recall);
		writer.println("RECALL :" + recall);
		
		System.out.println("F-SCORE : " + fScore );
		writer.println("F-SCORE : " + fScore);
		
		writer.close();
	}

	private void evaluate() throws FileNotFoundException {
		Scanner manual = new Scanner(new File(srcFile));
		Scanner generated = new Scanner(new File(genFile));
		String srline, genline;
		String srArr[], genArr[];
		while (manual.hasNext() && generated.hasNext()) {
			srline = manual.nextLine();
			genline = generated.nextLine();
			srArr = srline.split(",");
			genArr = genline.split(",");
			getCounts(new ArrayList<String>(Arrays.asList(srArr)),
					new ArrayList<String>(Arrays.asList(genArr)));

		}
		manual.close();
		generated.close();
	}

	private void getCounts(ArrayList<String> srArr, ArrayList<String> genArr){
		int slen = srArr.size();
		int glen = genArr.size();
		
		/*********To find positive negative and partial positive*********/
			for(int i=0;i<glen;i++){
				String word;
				if((word=genArr.get(i))!=null){
					if(i<slen && word.equalsIgnoreCase(srArr.get(i))){
						positiveCnt++;
					}else if(srArr.contains(word)){
						partialCnt++;
					}else{
						negativeCnt++;
					}
				}
			}
		/**********To find missing count ***********/
			int count = 0;
			for(String word : srArr){
				
				if(!genArr.contains(word)){
					missingCnt++;
				}
				count++;
				//Check miss count for top 3 expected
				if(count == 3)
					break;
			}
		
	}

	public static void main(String[] args) {
		if (args.length != 2) {
			System.err
					.println("format <expected res file path> <obtained file path>");
			return;
		}
		EvaluateSNE esne = new EvaluateSNE();
		esne.srcFile = args[0];
		esne.genFile = args[1];

		try {
			esne.evaluate();
			esne.printResults();
		} catch (IOException e) {
			System.err.println("Invalid file path");
			e.printStackTrace();
		}
		
	}

}
