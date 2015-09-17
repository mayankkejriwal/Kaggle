package BIG2015;

import java.io.*;
import java.util.*;

import utils.Utilities;

public class Analysis {

	static String prefix="/host/kaggle/BIG2015/";
	
	public static void main(String[] args)throws IOException{
		submission3();
		//countLabelDistribution();
	}
	
	public static double[] countLabelDistribution() throws IOException{
		Scanner in=new Scanner(new FileReader(prefix+"trainLabels.csv"));
		if(in.hasNextLine())
			in.nextLine();
		int total=0;
		int[] counts=new int[10]; //counts[0] is 0.
		while(in.hasNextLine()){
			int k=Integer.parseInt(in.nextLine().split(",")[1]);
			counts[k]++;
			total++;
		}
		in.close();
		double[] distr=new double[10];
		for(int i=0; i<=9; i++)
			distr[i]=counts[i]*1.0/total;
		
		return distr;
		//Utilities.printDoubleArray(distr);
	}
	
	//mimick training set distribution
	public static void submission1()throws IOException{
		Scanner in=new Scanner(new FileReader(prefix+"sampleSubmission.csv"));
		PrintWriter out=new PrintWriter(new File(prefix+"submission1.csv"));
		double[] d=countLabelDistribution();
		String score=",";
		for(int i=1; i<=8; i++)
			score+=(d[i]+",");
		score+=d[9];
		if(in.hasNextLine())
			out.println(in.nextLine());
		while(in.hasNextLine()){
			String a=in.nextLine().split(",")[0];
			out.println(a+score);
		}
		
		out.close();
		in.close();
	}
	
	
	//random
	public static void submission2()throws IOException{
		
		
		Random rand=new Random(System.currentTimeMillis());
		int[] counts=new int[10];
		for(int i=1; i<=9; i++)
			counts[i]=rand.nextInt(100);
		double[] d=Utilities.convertToDistribution(counts);
		
		Scanner in=new Scanner(new FileReader(prefix+"sampleSubmission.csv"));
		PrintWriter out=new PrintWriter(new File(prefix+"submission2.csv"));
		
		String score=",";
		for(int i=1; i<=8; i++)
			score+=(d[i]+",");
		score+=d[9];
		if(in.hasNextLine())
			out.println(in.nextLine());
		while(in.hasNextLine()){
			String a=in.nextLine().split(",")[0];
			out.println(a+score);
		}
		
		out.close();
		in.close();
	}

	//random
	public static void submission3()throws IOException{
		Scanner in=new Scanner(new FileReader(prefix+"sampleSubmission.csv"));
		PrintWriter out=new PrintWriter(new File(prefix+"submission3.csv"));
		double[] d2=countLabelDistribution();
		Random rand=new Random(System.currentTimeMillis());
		if(in.hasNextLine())
			out.println(in.nextLine());
		while(in.hasNextLine()){
			
			int[] counts=new int[10];
			for(int i=1; i<=9; i++)
				counts[i]=rand.nextInt(100);
			double[] d1=Utilities.convertToDistribution(counts);
			double[] d=Utilities.averageArrays(d1,d2);
			
			String score=",";
			for(int i=1; i<=8; i++)
				score+=(d[i]+",");
			score+=d[9];
			String a=in.nextLine().split(",")[0];
			out.println(a+score);
		}
		
		out.close();
		in.close();
	}

}
