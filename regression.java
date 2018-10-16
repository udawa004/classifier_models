import java.io.BufferedReader;
import java.util.Random;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;


public class regression {
	
	// class label generator
	public static ArrayList<Integer> labelGenerator(int modelNum, ArrayList<Integer> labelList){
		ArrayList<Integer> classLabelList = new ArrayList<Integer>();	
		int label = 0;
		for(int i = 0; i< labelList.size() ; i++){
			if(labelList.get(i) == modelNum){
				label = 1;
			}
			else 
				label = -1;
			classLabelList.add(i,label);			
		}
		//System.out.println(classLabelList);
		return classLabelList;
	}
	
	//training the model
	public static Double[] modelTrain(double lambda, List<Integer> y, List<ArrayList<Double>> trainData){
		
		double[][] data = new double[trainData.size()][trainData.get(0).size()];
		double[][] dataTranspose = new double[trainData.get(0).size()][trainData.size()];
		double[] prodData = new double[trainData.get(0).size()];
		Double[] w = new Double[trainData.get(0).size()];
		double err = 10000;
		double loss = 100000000.0;
		double[] xw = new double[y.size()];
		double[] diff = new double[y.size()];
		double num;
		double den;
		double[] pred = new double[trainData.size()]; 
		double loss1 = 0;
		
		Random rand = new Random();
		for(int i = 0; i<w.length ; i++){
			w[i] = rand.nextDouble();
		}
		
		
		for(int i = 0 ; i<trainData.size() ; i++){	
			for(int j = 0 ; j<trainData.get(0).size() ; j++){
				data[i][j] = (trainData.get(i)).get(j);
				dataTranspose[j][i] = data[i][j];
				prodData[j] = prodData[j] + (data[i][j]*data[i][j]);
			}
		}
		
		
		while(err > 0.00001){
			
			//calculate new w
			for(int i = 0 ; i<w.length ; i++){	
				num = 0;
				Arrays.fill(xw, 0);
			//calculate w(i)
				//calculate denominator
				den = prodData[i]+lambda;
				
				//calculate numerator
				for(int j = 0 ; j < trainData.size();j++){
					for(int k = 0 ; k< trainData.get(0).size(); k++){
						if(k != i){
							xw[j] = xw[j] + data[j][k]*w[k];
						}
					}
				}
				
				for(int j = 0 ; j<y.size() ; j++){
					diff[j] = y.get(j) - xw[j];
				}
				
				for(int j = 0 ; j<y.size() ; j++){
					num = num+ (dataTranspose[i][j]*y.get(j));
				}
				//calculate w(i) from num and den
				w[i] = num/den;		
			
			}
			
			//calculate error
			loss1 = 0;
			Arrays.fill(pred, 0);
			
			//dot product of x and w
			for(int p  =0 ; p< trainData.size();p++){
				for(int q = 0 ; q< trainData.get(0).size() ; q++){
					pred[p] = pred[p] + data[p][q]*w[q];
				}
				//loss1 = loss1 + (pred[p] - y.get(p))*(pred[p] - y.get(p));
			}
			
			//calculate loss function
			for(int i = 0 ; i<y.size() ; i++){
				loss1 = loss1 + (pred[i] - y.get(i))*(pred[i] - y.get(i));
			}
			for(int i = 0 ; i<w.length; i++){
				loss1 = loss1 + lambda*w[i]*w[i];
			}
			
			//calculate error from loss
			err = (loss - loss1)/loss; 
			//System.out.println("ERROR" + err);
			loss = loss1;
		}	
		
	return w;	
	}
	
	public static List<Integer> predictLabel(List<Double[]> weights, List<ArrayList<Double>> inData){
	
		double[][] pred = new double[inData.size()][weights.size()]; 
		List<Double> temp = new ArrayList<Double>();
		List<Integer> label = new ArrayList<Integer>();
		double max;
		int tempLabel = 0;
					
		for(int j = 0 ; j < inData.size();j++){				
			
			temp = inData.get(j);			
				
			for(int k = 0 ; k< inData.get(0).size(); k++){
					
				for(int i = 0 ; i<weights.size();i++){
						
					pred[j][i] = pred[j][i] + temp.get(k)*weights.get(i)[k];
					
				}
			}
		 }
		
		for(int i = 0 ; i < inData.size() ; i++){
			max = Double.NEGATIVE_INFINITY;
			for(int j = 0 ; j < pred[0].length ; j++){
				if(pred[i][j] > max){
					max = pred[i][j];
					tempLabel = j;
				}
			}
			label.add(i,tempLabel);
		}
		return label;
					
	}
	
	public static double CalculateAccuracy(List<Integer> label,List<Integer> y ){
		
		double correctPred = 0;
		double accuracy = 0;
		
		for(int i = 0 ; i<y.size() ; i++){
			if(label.get(i) == y.get(i)){
				correctPred = correctPred + 1;
			}
		}
		
		accuracy = (correctPred/y.size())*100;
		return accuracy;
		
	}
	
	
	public static void main(String[] args) throws IOException  {
		// TODO Auto-generated method stub
		
		long startTime = System.currentTimeMillis();
		String trainFile = args[0];
		String validationFile = args[1];
		String testFile = args[2];
		String outputFile = args[3];
		String weightsFile = args[4];
		
		ArrayList<Integer> trainLabel = new ArrayList<Integer>();
		ArrayList<Integer> valLabel = new ArrayList<Integer>();
		ArrayList<Integer> testLabel = new ArrayList<Integer>();

		List<ArrayList<Double>> trainData = new ArrayList<ArrayList<Double>>();
		List<ArrayList<Double>> valData = new ArrayList<ArrayList<Double>>();
		List<ArrayList<Double>> testData = new ArrayList<ArrayList<Double>>();
	
	
		//Read training data file
				FileReader fr = new FileReader(new File(trainFile).getAbsoluteFile());
				BufferedReader bufferedReader = new BufferedReader(fr);
				String line = new String();
				
				while((line = bufferedReader.readLine()) != null){		
					String[] train = line.split(",");
					trainLabel.add(Integer.parseInt(train[0]));
					ArrayList<Double> trainInput = new ArrayList<Double>();			

					for(int i = 1 ; i < train.length ; i++){
						trainInput.add(Double.parseDouble(train[i]));	
					}
					trainData.add(trainInput);
				}
				bufferedReader.close();
				fr.close();	
				
		//Read validation file
				FileReader fr1 = new FileReader(new File(validationFile).getAbsoluteFile());
				BufferedReader bufferedReader1 = new BufferedReader(fr1);
				String line1 = new String();
				
				while((line1 = bufferedReader1.readLine()) != null){		
					String[] val = line1.split(",");
					valLabel.add(Integer.parseInt(val[0]));
					ArrayList<Double> valInput = new ArrayList<Double>();			

					for(int i = 1 ; i < val.length ; i++){				
						valInput.add(Double.parseDouble(val[i]));
					}
					valData.add(valInput);					
				}
				bufferedReader1.close();
				fr1.close();
				
		//Read test file
				FileReader fr2 = new FileReader(new File(testFile).getAbsoluteFile());
				BufferedReader bufferedReader2 = new BufferedReader(fr2);
				String line2 = new String();
				while((line2 = bufferedReader2.readLine()) != null){		
					String[] test = line2.split(",");
					testLabel.add(Integer.parseInt(test[0]));
					ArrayList<Double> testInput = new ArrayList<Double>();			

					for(int i = 1 ; i < test.length ; i++){				
						testInput.add(Double.parseDouble(test[i]));
					}	
					testData.add(testInput);
							
				}		
				bufferedReader2.close();
				fr2.close();	
		
		
		Double[] w = new Double[trainData.get(0).size()];
		List<Double[]> weights = new ArrayList<Double[]>();
		double[] lambda = {.01, 0.05, 0.1, 0.5, 1.0, 2.0, 5.0};
		double[] accuracy = new double[lambda.length];
		
		List<Integer> label = new ArrayList<Integer>();
		List<Integer> label1 = new ArrayList<Integer>();
		List<Integer> label2 = new ArrayList<Integer>();

		ArrayList<Integer> classList = new ArrayList<Integer>();
		List<ArrayList<Integer>> classLabelLists = new ArrayList<ArrayList<Integer>>();

		List<ArrayList<Double>> newTrainData = new ArrayList<ArrayList<Double>>(trainData);
		newTrainData.addAll(valData);
		
		ArrayList<Integer> newtrainLabel = new ArrayList<Integer>(trainLabel);
		newtrainLabel.addAll(valLabel);
		
		//Model Selection (finding the value of best lambda using validation set)
		
		for(int i = 0 ; i <10 ; i++){
			classList = labelGenerator(i,trainLabel);
			classLabelLists.add(i,classList); 
		}
		for(int j = 0 ; j<lambda.length ; j++){
			weights.clear();
			label.clear();
			for(int i = 0 ; i <10 ; i++){
				w = modelTrain(lambda[j],classLabelLists.get(i), trainData);			
				weights.add(w);
			}	
			label = predictLabel(weights,valData);
			accuracy[j] = CalculateAccuracy(label,valLabel);
		}
		
		double selectedLambda = 0;
		double maxAccuracy = Double.NEGATIVE_INFINITY;
		for(int i = 0 ; i < accuracy.length ; i++){
			if(accuracy[i] > maxAccuracy){
				maxAccuracy = accuracy[i];
				selectedLambda = lambda[i];
			}
		}

		//Running Model on Test Data for Selected Lambda and 2*(Selected Lambda)
		double finalAccuracy1;
		double finalAccuracy2;
		double finalAccuracy;
		
		classLabelLists.clear();
		for(int i = 0 ; i <10 ; i++){			
			classList = labelGenerator(i, newtrainLabel);
			classLabelLists.add(i,classList);						
		}
		weights.clear();
		label.clear();
		for(int i = 0 ; i <10 ; i++){
			w = modelTrain(selectedLambda,classLabelLists.get(i), newTrainData);			
			weights.add(w);
		}			
						
		label1 = predictLabel(weights,testData);
		finalAccuracy1 = CalculateAccuracy(label1,testLabel);
		
		weights.clear();
		for(int i = 0 ; i <10 ; i++){
			w = modelTrain(2*selectedLambda,classLabelLists.get(i), newTrainData);			
			weights.add(w);
		}			
						
		label2 = predictLabel(weights,testData);
		finalAccuracy2 = CalculateAccuracy(label2,testLabel);
		
		if(finalAccuracy1 > finalAccuracy2){
			finalAccuracy = finalAccuracy1;
			label = label1;
		}
		else{
			finalAccuracy = finalAccuracy2;
			label = label2;
		}
		
		System.out.println("ACCURACY " + finalAccuracy);
		
		File outFile = new File(weightsFile);
		if (!outFile.exists()) {
			outFile.createNewFile();
		}
		FileWriter fw = new FileWriter(outFile.getAbsoluteFile()); 
		BufferedWriter bw = new BufferedWriter(fw);
		try {	
			for(int i = 0 ; i<weights.size() ; i++){
				w = weights.get(i);
				for(int j = 0 ; j<w.length ; j++){
					bw.write(w[j]+" ");								
				}
				bw.write("\n");					
			}
		}catch(IOException E){
			E.printStackTrace();
		}
		bw.close();
		fw.close();
		
		File outFile1 = new File(outputFile);
		if (!outFile1.exists()) {
			outFile1.createNewFile();
		}
		FileWriter fw1 = new FileWriter(outFile1.getAbsoluteFile()); 
		BufferedWriter bw1 = new BufferedWriter(fw1);
		try {	
			for(int i = 0 ; i<label.size() ; i++){
				bw1.write(label.get(i)+"\n");							
			}
		}catch(IOException E){
			E.printStackTrace();
		}
		bw1.close();
		fw1.close();
		
		long endTime = System.currentTimeMillis();
		System.out.println("Runtime:" + ((endTime - startTime)/1000) + "sec");
		
	}

}
