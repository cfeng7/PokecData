package example;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.io.PrintWriter;
import java.net.UnknownHostException;

import com.mongodb.BasicDBObject;
import com.mongodb.DB;
import com.mongodb.DBCollection;
import com.mongodb.MongoClient;

public class InsertFile {
 static int featureNum=59; // feature number for one profile
 static double cutper=30; // thrs to cut of the profile
 static int insertRange=1600000;
// static String fileName="updateProfile1.txt";
 static String fileName="newProfile.csv";
 // static int insertRange=1600000;
	public static void main(String[] args) throws IOException {
		// TODO Auto-generated method stub

		// deal with mongo db
      try {

   // end mongo db
         
		BufferedReader br;
			br = new BufferedReader(new FileReader("data/soc-pokec-profiles.txt"));	
			File f= new File(fileName);
			if(f.exists()) f.delete();
			// write text
			PrintWriter p= new PrintWriter(fileName,"UTF-8");
		     for(int i=0;i<insertRange;i++){
		    lineToDBobj(br,p);
		     }
		     br.close();
		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		

      
	}
    
	
	// read one line from file and to Mongodb
	public static BasicDBObject lineToDBobj(BufferedReader br,PrintWriter p){
		try{
		 
		String line;
			line = br.readLine();
			String[] s = line.split("\t");
			if(Integer.parseInt(s[2])>30){
				
				// pre-process the registration
	            String registration=((String) s[6].split(" ")[0].substring(0,4));
	            if(!registration.equals("null")){
	            	registration=Integer.toString(Integer.parseInt(registration)-1999);
	            }
		            String sline =s[0]+","
		            +s[1]+","
		            +s[3]+","
		            +changeRegion(s[4])+","
		            +registration+","      
		            +s[7]+","
		            +changeColor1((String) s[16].split(" ")[0].replaceAll("[^a-zA-Z]+", ""))+","
		            +changeColor1((String) s[17].split(" ")[0].replaceAll("[^a-zA-Z]+", ""))+","
		            +changeColor1((String) s[20].split(" ")[0].replaceAll("[^a-zA-Z]+", ""))+","
		            +changeSmoke(s[21])+","
		            +changeDrink(s[22])+","
		            +changeStar(s[23])+","
		            +changeMarry((String) s[28].split(" ")[0].replaceAll("[^a-zA-Z]+", ""));
             p.println(sline);

			}
			return null;
		}catch(Exception e){
			
		}
		return null;
	}
	
	public static String changeStar(String sb){
		//System.out.println(sb);
		String s="null";
		switch(sb){
		case "null": s="null"; break;
		case "baran": s="0";break;
		case "blizenci": s="1";break; 
		case "byk":	s="2";break;
		case "kozorozec": s="3";break;
		case "lev": s="4";break;
		case "panna": s="5";break;
		case "rak": s="6";break;
		case "ryby": s="7";break;
		case "skorpion": s="8";break;
		case "strelec": s="9";break;
		case "vahy": s="10";break;
		case "vodnar": s="11";break;
		default: s="12";break;
		}
		return s;
	}
	
	public static String changeRegion(String sb){
		String st=((String) sb.split(" ")[0]).replaceAll("[^a-zA-Z]+", "");
		switch(st){
		case "null": return "null";
		case "banskobystricky": return "0";
		case "bratislavsky": return "1";
		case "kosicky": return "2";
		case "nitriansky": return "3";
		case "presovsky": return "4";
		case "trenciansky": return "5";
		case "trnavsky": return "6";
		case "zahranicie": return "7";
		case "zilinsky": return "8";
		default: return "9";
		}
	}
	
	public static String changeSmoke(String sb){
		String st=((String) sb.split(" ")[0]).replaceAll("[^a-zA-Z]+", "");
		if(st.equals("nefajcim")) return "0";
	    if(st.equals("fajcim")) return "1";
		switch(sb){
		case "null": return "null";
		case "uz nefajcim": return "0";
		default: return "2";
		}
	}
	
	public static String changeDrink(String sb){
		String st=((String) sb.split(" ")[0]).replaceAll("[^a-zA-Z]+", "");
		if(st.equals("abstinent")) return "0";
	    if(st.equals("nepijem")) return "0";
	    if(st.equals("pijem")) return "1";
		switch(sb){
		case "null": return "null";
		case "prilezitostne":
		case "prilezitostny":
		case "pozitivny": return "1";
		case "uz nepijem": return "0";
		default: return "0";
		}
	}
	
	public static String changeMarry(String sb){
		String s="null";
		switch(sb){
		case "null": return "null";
		case "slobodnya":
		case "slobodny":
		case "slobodna": return "0";
		default: return "1";
		}
	}
	public static String changeColor1(String sb){
		//System.out.println(sb);
		String s="null";
		switch(sb){
		case "null": s="null"; break;
		case "cierna":
		case "cierne": s="0";break;
		case "hneda":
		case "hnede":
		case "hnedo": s="1";break;
		case "hnedozelene": 
		case "zelenohnede":	s="2";break;
		case "modra":
		case "modre": 
		case "modro": s="3";break;
		case "modrozelene": 
		case "zelenomodre": s="4";break;
		case "sive": s="5";break;
		case "zelena":
		case "zelene":
		case "zeleno": s="6";break;
		case "blond": s="7";break;
		case "rysave": 
		case "cervene": s="8";break;
		case "odfarbene": s="9";break;
		case "tmave":
		case "tmavo":
		case "tmavohnede": s="10";break;
		case "biela": s="11";break;
		case "fialova": s="12";break;
		default: s="13";break;
		}
		return s;
	}
}
