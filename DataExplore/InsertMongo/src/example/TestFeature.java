package example;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.HashMap;
import java.util.TreeMap;

public class TestFeature {
      static int age=0;
      static int num=0;
	 static int featureNum=59; 
	 static int sum=860000;
	public static void main(String[] args) throws IOException {
		// TODO Auto-generated method stub
		BufferedReader br;
		br = new BufferedReader(new FileReader("data/soc-pokec-profiles.txt"));	
		TreeMap<String,Integer> hmap = new TreeMap<String,Integer>();
		double[] count= new double[featureNum];
	     for(int i=0;i<sum;i++){
	    	 if(i%100000==0) System.out.println(i);
	    	 try{
	 		String line;
			line = br.readLine();
			String[] s = line.split("\t");
			String sb="null";
			if(!s[21].equals("null")){
				//sb=Integer.toString(chargeColor(s[20]));
			//sb=((String) s[28].split(" ")[0]).replaceAll("[^a-zA-Z]+", "");
			//sb=((String) s[6].split(" ")[0].substring(0,4));
				//sb=s[4];
				sb=s[21];
				int p=Integer.parseInt(s[7]);
				if(p!=0){
				age=age+p;
				num++;
				}
			}
			//String sb=s[16];
                if(hmap.containsKey(sb)){
                	hmap.put(sb, hmap.get(sb)+1);
                }else{
                	hmap.put(sb, 0);
                }
	    	 }catch(Exception e){
	    		 
	    	 }
	     }
	     br.close();
System.out.println(hmap.size());
for(String s:hmap.keySet()){
	if(hmap.get(s)>2000){
	System.out.println(s+" "+hmap.get(s));
	}
}

System.out.println("average age is "+(age/num));
	}

	
	
	public static int chargeColor(String s){
		String sb=((String) s.split(" ")[0]).replaceAll("[^a-zA-Z]+", "");
		//System.out.println(sb);
		switch(sb){
		case "cierna":
		case "cierne": return 0;
		case "hneda":
		case "hnede":
		case "hnedo": return 1;
		case "hnedozelene": 
		case "zelenohnede":	return 2;
		case "modra":
		case "modre": 
		case "modro": return 3;
		case "modrozelene": 
		case "zelenomodre": return 4;
		case "sive": return 5;
		case "zelena":
		case "zelene":
		case "zeleno": return 6;
		case "blond": return 7;
		case "rysave": 
		case "cervene": return 8;
		case "odfarbene": return 9;
		case "tmave":
		case "tmavo":
		case "tmavohnede": return 10;
		case "biela": return 11;
		case "fialova": return 12;
		default: return 13;
		}
	}
}
