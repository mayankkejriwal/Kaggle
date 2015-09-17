package utils;



import java.io.FileReader;
import java.io.IOException;
import java.util.Scanner;

public class Utilities {

	
	static String prefix="/host/kaggle/avazu/";
	
	public static void main(String[] args)throws IOException{
		printFirstNLinesCSV(50);
	}
	
	public static void printDoubleArray(double[] g){
		int count=0;
		for(double m:g)
			System.out.println((count++)+" "+m);
	}
	
	//takes an integer array and converts to normalized distribution
	public static double[] convertToDistribution(int[] counts){
		int total=0;
		for(int c:counts)
			total+=c;
		double[] res=new double[counts.length];
		
		for(int i=0; i<res.length; i++)
			res[i]=counts[i]*1.0/total;
		
		return res;
	}
	
	//no error checking!
	public static double[] averageArrays(double[] a, double[] b){
		double[] res=new double[a.length];
		for(int i=0; i<a.length; i++)
			res[i]=(a[i]+b[i])/2;
		return res;
	}
	
	public static void printFirstNLinesCSV(int n)throws IOException{
		String file=prefix+"results.csv";
		Scanner in=new Scanner(new FileReader(file));
		for(int i=0; i<n; i++)
		{	if(in.hasNextLine())
				System.out.println(in.nextLine());
		}
		in.close();
	}
	
	
}
