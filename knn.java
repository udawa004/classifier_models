import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;
import java.util.List;


public class knn{
	
	static String trainFile;
	static String validationFile;
	static String testFile;
	static String outputFile;
			
	// find index of k nearest neighbors

	public static int[][] kNearestNeighbors(int k ,List<recordFormat> trainList, List<recordFormat> testList) {
		
		List<Double> l1 = new ArrayList<Double>();
		List<Double> l2 = new ArrayList<Double>();
		
		recordFormat sampleTrain = new recordFormat();
		recordFormat sampleTest = new recordFormat();
				
		int[][] outArr = new int[testList.size()][k];
		
		Double[][] maxSimilarity = new Double[k][2];
		double minSim;
		int minIndex;
		int i;
		Integer j = 0;
		
		for(i = 0 ; i < testList.size() ; i++){	
			
			sampleTest = testList.get(i);
			l1 = sampleTest.inputData;
			
			for(int n = 0 ; n <k ; n++){
				maxSimilarity[n][1] = Double.NEGATIVE_INFINITY;
			}
			minSim = Double.NEGATIVE_INFINITY;
			minIndex = 0;

			for(j = 0 ; j < trainList.size() ; j++){
					
				sampleTrain = trainList.get(j);
				l2 = sampleTrain.inputData;

				double temp = 0;
				
				/*	
				for(int m = 0 ; m <l2.size() ; m++){					
					temp = temp +  (l1.get(m) - l2.get(m))*(l1.get(m) - l2.get(m));					
				}			
				temp = Math.sqrt(temp);
				*/
				
				double abs1 = 0.0;
				double abs2 = 0.0;
				double prod = 0.0;
				double prod1 = 0.0;
				for(int m = 0 ; m <l2.size() ; m++){					
					prod1 = prod1 +  l1.get(m)*l2.get(m);			
				}	
				for(int m = 0 ; m <l1.size() ; m++){					
					abs1 = abs1 +  l1.get(m)*l1.get(m);			
				}	
				abs1 = Math.sqrt(abs1);
				for(int m = 0 ; m <l2.size() ; m++){					
					abs2 = abs2 +  l2.get(m)*l2.get(m);			
				}
				abs2 = Math.sqrt(abs2);
				abs1 = Math.sqrt(abs1);
				prod = abs1*abs2;
				temp = prod1/prod;
				
				if(temp > minSim){
					maxSimilarity[minIndex][0] = (double) j.intValue();
					maxSimilarity[minIndex][1] = temp;
					//maxDist = temp;
				}				
				for(int n = 0 ; n < k ; n++){
					if(maxSimilarity[n][1] < temp){
						minSim = maxSimilarity[n][1];
						minIndex = n;		
					}
				}
			}		
			
			Arrays.sort(maxSimilarity, new Comparator<Double[]>() {
				public int compare(Double[] num1, Double[] num2) {
					Double number1 = num1[1];
					Double number2 = num2[1];
					return number2.compareTo(number1);
				}
			});
			
			for(int p = 0 ; p < k ; p++){
				outArr[i][p] = (maxSimilarity[p][0]).intValue();
			}
			
		}
			
		return outArr;
	} 
		
	// find label

public static List<Integer> findLabel(int[][] outArr, List<recordFormat> trainList ) {
	
	List<Integer> predLabel = new ArrayList<Integer>();
	
	int trainIndex;
	recordFormat record = new recordFormat();
	int largestVal;
	int label;	
	int a[]=new int[10];	

	
	for(int i = 0; i<outArr.length; i++) { 
				
		Arrays.fill(a, 0);
		
		largestVal = 0;
		label = 100;	
		
		for(int j = 0 ; j<outArr[0].length ; j++){						
			trainIndex = outArr[i][j];		
			record = trainList.get(trainIndex);
			int dummyLabel = record.label;		
			a[dummyLabel]++;			
		}
			
		for(int j = 0 ; j<10 ; j++)
		{
			if(a[j] > largestVal){				
				largestVal = a[j];
				label = j;
				}			
		}
				
		predLabel.add(i,label);
	}	
	return predLabel;
}	

	//find accuracy
public static double calculateAccuracy(List<Integer> predLabel, List<recordFormat> testList){
	
	int predictedLabel;
	int trueLabel;
	recordFormat record = new recordFormat();
	
	double correctPred = 0;
	
	double accuracy;
	
	for(int i = 0 ; i< predLabel.size() ; i++){
		
		predictedLabel = predLabel.get(i);
		record = testList.get(i);
		trueLabel = record.label;
		
		if(predictedLabel == trueLabel)
			correctPred = correctPred + 1;		
	}
	
	accuracy = (correctPred/predLabel.size())*100;
	return accuracy;	
}
	//model selection

public static int modelSelection(List<recordFormat> trainList, List<recordFormat> valList){
	
	int selectedParam = 0;
	int kMax = 20;
	double maxAcc = 0;
	double acc[] = new double[kMax+1]; 
	int[][] outArr;
	List<Integer> predList;
	double accuracy;
	
	outArr = kNearestNeighbors(kMax ,trainList,valList);
	
	int[] temp = new int[outArr[0].length];
	int[] temp1; 
	int[][] outArr1 = new int[outArr.length][]; 	
	
	for(int k = 1; k<21 ; k++){
		outArr1 = new int[outArr.length][k];
		for(int i = 0 ; i <outArr.length ; i++){
			
			for(int j = 0 ; j<outArr[0].length ; j++){
				temp[j] = outArr[i][j];
			}
			temp1 = new int[k];
			temp1 = Arrays.copyOfRange(temp, 0, k);
						
			
			for(int l = 0 ; l<temp1.length ; l++){
				outArr1[i][l] = temp1[l];
			}
		}
				
		predList = findLabel(outArr1, trainList);
		//System.out.println(predList);
		accuracy = calculateAccuracy(predList, valList);
		//System.out.println(accuracy);
		acc[k] = accuracy;	
	}
	
	for(int k = 1 ; k < kMax ; k++ ){
		//System.out.println(acc[k]);

		if(acc[k] > maxAcc){
			
			maxAcc = acc[k];
			selectedParam = k;
		}
	}
	
	return selectedParam;
}
	
	public static void main(String[] args) throws IOException {
		
		long startTime = System.currentTimeMillis();
		String trainFile = args[0];
		String validationFile = args[1];
		String testFile = args[2];
		String outputFile = args[3];			
		
		List <recordFormat> trainData = new ArrayList<recordFormat>();
		List <recordFormat> valData = new ArrayList<recordFormat>();
		List <recordFormat> testData = new ArrayList<recordFormat>();

		//Read training data file
		FileReader fr = new FileReader(new File(trainFile).getAbsoluteFile());
		BufferedReader bufferedReader = new BufferedReader(fr);
		String line = new String();
		while((line = bufferedReader.readLine()) != null){		
			String[] train = line.split(",");
			int trainLabel = Integer.parseInt(train[0]);
			List<Double> trainInput = new ArrayList<Double>();			
			for(int i = 1 ; i < train.length ; i++){				
				trainInput.add(Double.parseDouble(train[i]));	
			}
		
			recordFormat trainSample = new recordFormat(trainLabel,trainInput);
			trainData.add(trainSample);
		}
			
		//Read validation data file
			FileReader fr1 = new FileReader(new File(validationFile).getAbsoluteFile());
			BufferedReader bufferedReader1 = new BufferedReader(fr1);
			String line1 = new String();
			while((line1 = bufferedReader1.readLine()) != null){		
				String[] val = line1.split(",");
				int valLabel = Integer.parseInt(val[0]);
				List<Double> valInput = new ArrayList<Double>();			
				for(int i = 1 ; i < val.length ; i++){				
					valInput.add(Double.parseDouble(val[i]));				
				}
			
				recordFormat valSample = new recordFormat(valLabel,valInput);
				valData.add(valSample);
			}
			
		//Read test data file
				FileReader fr2 = new FileReader(new File(testFile).getAbsoluteFile());
				BufferedReader bufferedReader2 = new BufferedReader(fr2);
				String line2 = new String();
				while((line2 = bufferedReader2.readLine()) != null){		
					String[] test = line2.split(",");
					int testLabel = Integer.parseInt(test[0]);
					List<Double> testInput = new ArrayList<Double>();			
					for(int i = 1 ; i < test.length ; i++){				
						testInput.add(Double.parseDouble(test[i]));				
					}	
				
					recordFormat testSample = new recordFormat(testLabel,testInput);
					testData.add(testSample);			
				}		
			
		
				bufferedReader.close();
				fr.close();	
				bufferedReader1.close();
				fr1.close();	
				bufferedReader2.close();
				fr2.close();	
		
								
				int k;
				
				int[][] outArr;
				List<Integer> predList;
				double accuracy;
				
				List <recordFormat> newTrainData = new ArrayList<recordFormat>(trainData);
				newTrainData.addAll(valData);
		
				
				k = modelSelection(trainData, valData);
				//System.out.println(k);
				outArr = kNearestNeighbors(k,newTrainData,testData);				
				predList = findLabel(outArr, newTrainData);
				accuracy = calculateAccuracy(predList, testData);
				System.out.println("ACCURACY: "+accuracy);
				
				//generate output file
				File outFile = new File(outputFile);
				if (!outFile.exists()) {
					outFile.createNewFile();
				}
				FileWriter fw = new FileWriter(outFile.getAbsoluteFile()); 
				BufferedWriter bw = new BufferedWriter(fw);
				try {	
					for(int i = 0 ; i<predList.size() ; i++){
						bw.write(predList.get(i)+"\n");
						
					}
				}catch(IOException E){
					E.printStackTrace();
				}
				bw.close();
				fw.close();
				long endTime = System.currentTimeMillis();
				System.out.println("Runtime:" + ((endTime - startTime)/1000) + "sec");
			
	}
}
class recordFormat {
	public int label;
	public List <Double> inputData = new ArrayList<Double>();
	
	public recordFormat(int label, List<Double> testInput) {
		super();
		this.label = label;
		this.inputData = testInput;
	}

	public recordFormat() {
		super();
	}

}
