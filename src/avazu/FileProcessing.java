package avazu;

import general.CSVParser;

import java.util.*;
import java.io.*;

import svm.svm_train;
import weka.classifiers.functions.*;
import weka.classifiers.rules.DecisionTable;
import weka.core.Instances;

public class FileProcessing {
	
	static String prefix="/host/kaggle/avazu/";
	
	public static void main(String[] args)throws Exception{
		//for(int i=1; i<=99; i++)
		//convertToArffFile(i);
		//convertTestsToArffFile();
		trainAndTest();
		//count();
		//countFiles();
		
	}
	
	public static void trainAndTest()throws Exception{
		BufferedReader reader = null;
		Logistic[] booster=new Logistic[99];		
		
		for(int i=1; i<=99; i++){
			System.out.println("Training model "+i);
		reader=new BufferedReader(new FileReader(prefix+"train_splits/ARFF/"+i+".arff"));
		Instances train = new Instances(reader);
		reader.close();
		train.setClassIndex(train.numAttributes() - 1);
		booster[i-1]=new Logistic();
		booster[i-1].buildClassifier(train);
		train=null;
		}
		System.out.println("Training complete");
		
		
		String[] results=new String[100];
		String[] arff=new String[100];
		String[] ids=new String[100];
		
		for(int i=1; i<=100; i++){
			results[i-1]=prefix+"test_splits/results/"+i+".csv";
			arff[i-1]=prefix+"test_splits/arff/"+i+".csv";
			ids[i-1]=prefix+"test_splits/ids/"+i+".csv";
		}
		
		for(int i=0; i<100; i++){
		Scanner in=new Scanner(new FileReader(ids[i]));
		
		ArrayList<String> id=new ArrayList<String>();
		while(in.hasNextLine()){
			id.add(in.nextLine());
		}
		in.close();
		
		//System.out.println(id.size());
		
		reader=new BufferedReader(
                new FileReader(arff[i]));
		Instances test = new Instances(reader);
		reader.close();
		test.setClassIndex(test.numAttributes() - 1);
		
		int size=id.size();
		for(int j=0; j<size; j++){
			String t=id.get(j);
			double avg=0.0;
			for(int k=0; k<99; k++)
				avg+=booster[k].distributionForInstance(test.get(j))[0];
			avg/=99;
			t+=","+avg;
			id.set(j, t);
		}
		PrintWriter out=new PrintWriter(new File(results[i]));
		//out.println("id,click");
		for(String t:id)
			out.println(t);
		out.close();
		
		}
		
		System.out.println("combining results...");
		PrintWriter out=new PrintWriter(new File(prefix+"results.csv"));
		out.println("id,click");
		for(int i=0; i<100; i++){
			Scanner in=new Scanner(new FileReader(results[i]));
			while(in.hasNextLine())
				out.println(in.nextLine());
			in.close();
		}
		out.close();
		
		
		
	}
	
	public static void convertToArffFile(int n)throws IOException{
		Scanner in=new Scanner(new FileReader(prefix+"train_splits/FS/"+n+"FS.csv"));
		PrintWriter out=new PrintWriter(new File(prefix+"train_splits/ARFF/"+n+".arff"));
		int attributes=12;
		out.println("@RELATION train");
		out.println();
		for(int i=0; i<attributes; i++)
			out.println("@ATTRIBUTE a"+i+" NUMERIC");
		out.println("@ATTRIBUTE class {1,0}");
		out.println();
		out.println("@DATA");
		out.println();
		CSVParser k=new CSVParser();
		int[] arr={1,2,3,4,5,6,7,8,9,10,11,12};
		while(in.hasNextLine()){
			String[] line=k.parseLine(in.nextLine());
			String m=convertToString(line,arr);
			if(line[0].equals("0"))
				m+=",0";
			else m+=",1";
			out.println(m);
		}
		in.close();
		out.close();
	}
	
	//also converts to IDs
	public static void convertTestsToArffFile()throws IOException{
		String[] files=new String[100];
		String[] arff=new String[100];
		String[] ids=new String[100];
		
		for(int i=1; i<=100; i++){
			files[i-1]=prefix+"test_splits/"+i+".csv";
			arff[i-1]=prefix+"test_splits/arff/"+i+".csv";
			ids[i-1]=prefix+"test_splits/ids/"+i+".csv";
		}
		
		for(int j=0; j<100; j++){
		Scanner in=new Scanner(new FileReader(files[j]));
		PrintWriter out=new PrintWriter(new File(arff[j]));
		PrintWriter out1=new PrintWriter(new File(ids[j]));
		int attributes=12;
		out.println("@RELATION test");
		out.println();
		for(int i=0; i<attributes; i++)
			out.println("@ATTRIBUTE a"+i+" NUMERIC");
		out.println("@ATTRIBUTE class {1,0}");
		out.println();
		out.println("@DATA");
		out.println();
		CSVParser k=new CSVParser();
		int[] arr={2,3,13,14,15,16,17,18,19,20,21,22};
		
		while(in.hasNextLine()){
			String[] line=k.parseLine(in.nextLine());
			String m=convertToString(line,arr);
			m+=",?";
			out.println(m);
			out1.println(line[0]);
		}
		in.close();
		out.close();
		out1.close();
		}
	}

	public static void trainSVM()throws IOException{
		String infile=prefix+"50.svm";
		String svmModel=prefix+"50.model";
		svm_train t=new svm_train();
		String arg="-b 1 "+(infile)+" "+(svmModel);
		
		t.run(arg.split(" "));
	}
	
	public static void convertToSVMFile(int n)throws IOException{
		Scanner in=new Scanner(new FileReader(prefix+n+"FS.csv"));
		PrintWriter out=new PrintWriter(new File(prefix+n+".svm"));
		CSVParser k=new CSVParser();
		while(in.hasNextLine()){
			String[] t=k.parseLine(in.nextLine());
			String res=null;
			if(t[0].equals("1"))
				res="1 ";
			else
				res="-1 ";
			for(int i=1; i<t.length-1; i++)
				res+=(i+":"+t[i]+" ");
			res+=(t.length-1+":"+t[t.length-1]);
			out.println(res);
			
		}
		
		out.close();
		in.close();
	}
	
	
	public static void combineAndFeatureSelection(int n)throws IOException{
		Scanner in=new Scanner(new FileReader(prefix+"splits/pos/"+n+".csv"));
		PrintWriter out=new PrintWriter(new File(prefix+"train_splits/FS/"+n+".csv"));
		while(in.hasNextLine()){
			out.println(in.nextLine());
		}
		in.close();
		in=new Scanner(new FileReader(prefix+"splits/neg/"+n+".csv"));
		while(in.hasNextLine()){
			out.println(in.nextLine());
		}
		
		in.close();
		out.close();
		
		in=new Scanner(new FileReader(prefix+"train_splits/FS/"+n+".csv"));
		out=new PrintWriter(new File(prefix+"train_splits/FS/"+n+"FS.csv"));
		CSVParser k=new CSVParser();
		int[] arr={1,3,4,14,15,16,17,18,19,20,21,22,23};
		while(in.hasNextLine()){
			String[] line=k.parseLine(in.nextLine());
			out.println(convertToString(line,arr));
		}
		
		in.close();
		out.close();
	}
	
	private static String convertToString(String[] line, int[] arr){
		String a="";
		for(int i=0; i<arr.length-1; i++)
			a+=(line[arr[i]]+",");
		a+=line[arr[arr.length-1]];
		return a;
	}
	
	//not to be confused with naiveBayes! 
	public static void naiveClassifier()throws IOException{
		//Random k=new Random(System.currentTimeMillis());
		Scanner in=new Scanner(new FileReader(prefix+"test.csv"));
		PrintWriter out=new PrintWriter(new File(prefix+"results.csv"));
		out.println("id,click");
		if(in.hasNextLine())
			in.nextLine();
		while(in.hasNextLine()){
			String a=(new CSVParser()).parseLine(in.nextLine())[0];
			out.println(a+",0.1");
			/*int p=k.nextInt(10);
			if(p>=8)
				out.println(a+",2");
			else
				out.println(a+",0");*/
		}
		
		out.close();
		in.close();
	}
	
	public static void countFiles()throws IOException{
		//String[] files=new String[100];
		int total=0;
		for(int i=1; i<=100; i++)
			total+=count((prefix+"test_splits/ids/"+i+".csv"));
			
		System.out.println(total);
			
	}
	
	//will count number of lines in file
	public static void count()throws IOException{
		Scanner in=new Scanner(new FileReader(prefix+"results.csv"));
		int count=0;
		while(in.hasNextLine()){
			in.nextLine();
			count++;
		}
		in.close();
		System.out.println(count);
	}
	
	private static int count(String file)throws IOException{
		Scanner in=new Scanner(new FileReader(file));
		int count=0;
		while(in.hasNextLine()){
			in.nextLine();
			count++;
		}
		in.close();
		return count;
	}
	
	public static void split()throws IOException{
		int count=0;
		int fileindex=0;
		String[] files=new String[100];
		
		for(int i=1; i<=100; i++)
			files[i-1]=prefix+"test_splits/"+i+".csv";
		
		Scanner in=new Scanner(new FileReader(prefix+"test.csv"));
		if(in.hasNextLine())
			in.nextLine();
		
		PrintWriter out=new PrintWriter(new File(files[fileindex]));
		
		while(in.hasNextLine()){
			
			out.println(in.nextLine());
			count++;
			if(count==46000){
				System.out.println((fileindex+1)+" File(s) done");
				count=0;
				fileindex++;
				out.close();
				out=new PrintWriter(new File(files[fileindex]));
			}
			
		}
		in.close();
		out.close();
	}
	
	public static void splitPosNeg()throws IOException{
		int fileindex=0;
		String[] filesIn=new String[99];
		String[] filesPos=new String[99];
		String[] filesNeg=new String[99];
		CSVParser k=new CSVParser();
		for(int i=1; i<=99; i++){
			filesIn[i-1]=prefix+"splits/"+i+".csv";
			filesPos[i-1]=prefix+"splits/pos/"+i+".csv";
			filesNeg[i-1]=prefix+"splits/neg/"+i+".csv";
		}
		
		for(fileindex=0; fileindex<99; fileindex++){
		System.out.println("On File "+(fileindex+1));
		Scanner in=new Scanner(new FileReader(filesIn[fileindex]));
		PrintWriter out1=new PrintWriter(new File(filesPos[fileindex]));
		PrintWriter out0=new PrintWriter(new File(filesNeg[fileindex]));
		
		while(in.hasNextLine()){
			String line=in.nextLine();
			String label=k.parseLine(line)[1];
			if(label.equals("0")){
				out0.println(line);
				
			}
			else if(label.equals("1")){
				out1.println(line);
				
			}
		}
		in.close();out1.close();out0.close();
		}
	}
	
	//separate train.csv into (count based) 0/1 files without headers. No other preprocessing.
	public static void separateLabels()throws IOException{
		Scanner in=new Scanner(new FileReader(prefix+"train.csv"));
		PrintWriter out1=new PrintWriter(new File(prefix+"pos.csv"));
		PrintWriter out0=new PrintWriter(new File(prefix+"neg.csv"));
		int count1=0;
		int count0=0;
		CSVParser k=new CSVParser();
		if(in.hasNextLine())
			in.nextLine();
		while(in.hasNextLine()){
			String line=in.nextLine();
			String label=k.parseLine(line)[1];
			if(label.equals("0")){
				out0.println(line);
				count0++;
			}
			else if(label.equals("1")){
				out1.println(line);
				count1++;
			}
			else{
				System.out.println("Error!");
				System.out.println(line);
			}
			if(count1>=100000&&count0>=100000)
				break;
		}
		
		in.close();
		out1.close();
		out0.close();
	}

}
