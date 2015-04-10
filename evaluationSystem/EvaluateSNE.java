import java.io.File;
import java.io.FileNotFoundException;
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

	private void printResults() {
		precision = (positiveCnt + 0.5 * partialCnt) / (positiveCnt + negativeCnt + partialCnt);
		precision *= 100;
		
		recall = (positiveCnt + 0.5 * partialCnt) / (positiveCnt + partialCnt + missingCnt);
		recall *= 100;
		
		fScore = (2 * precision * recall)/(precision+recall);
		
		System.out.println("PRECISION :" + precision);
		System.out.println("RECALL :" + recall);
		System.out.println("F-SCORE : " + fScore );
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
		} catch (FileNotFoundException e) {
			System.err.println("Invalid file path");
			e.printStackTrace();
		}
		esne.printResults();
	}

}
