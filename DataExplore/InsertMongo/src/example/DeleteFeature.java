package example;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;

import com.mongodb.BasicDBObject;

public class DeleteFeature {
	 static int featureNum=59; 
	 static int sum=1600000;
	public static void main(String[] args) throws IOException {
		// TODO Auto-generated method stub
		BufferedReader br;
		br = new BufferedReader(new FileReader("data/soc-pokec-profiles.txt"));	
		double[] count= new double[featureNum];
	     for(int i=0;i<sum;i++){
	    	 if(i%100000==0) System.out.println(i);
	    	 try{
	 		String line;
			line = br.readLine();
			String[] s = line.split("\t");
			for(int j=0;j<featureNum;j++){
				if(s[j].equals("null")){count[j]++;}
			}
	    	 }catch(Exception e){
	    		 
	    	 }
	     }
	     br.close();
	     for(int i=0;i<featureNum;i++){
	    	count[i]=count[i]/sum;
	    	 count[i]= ((double)((int)(count[i]*100)))/(double)100;
	     }
	     
	     int keepnum=0;
	     for(int i=0;i<featureNum;i++){
	    	 if(count[i]<0.52){
	    		 keepnum++;
	    	 System.out.print(i+" ");
	    	 }
	     }
	     System.out.println("feature to be kept: "+keepnum);
	}
	

}
