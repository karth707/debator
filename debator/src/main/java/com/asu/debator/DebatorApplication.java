package com.asu.debator;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;
import com.asu.debator.util.SemanticScore;
import com.asu.debator.util.SentimentScore;


public class DebatorApplication {
     
	private static String INPUT_FILE_PATH , OUTPUT_FILE_PATH;
	private static String TOPIC , TOPIC_LABEL;
	private static String ANNOTATION_DELIMITER = "<-->";
	private static List<String> arguments = new ArrayList<String>();
	private HashMap<String, Double> argSemanticscores = new HashMap<String, Double>();
	private HashMap<String, String> argSentimentscores = new HashMap<String, String>();
	private List<String> positive , negative ;
	
	
	public DebatorApplication(String Topic , String InputFile , String OutputFile){
		TOPIC = Topic;
		INPUT_FILE_PATH = InputFile;
		OUTPUT_FILE_PATH = OutputFile;
	}
	 public void ExtractFactsfromFile(){
		 BufferedReader Reader = null;
		 String sentence , text , annotation= "";
		
			try {
				 Reader = new BufferedReader(new FileReader(INPUT_FILE_PATH));
			} catch (FileNotFoundException e) {
				e.printStackTrace();
			}
			try {
				while ((sentence = Reader.readLine()) != null){
					text = sentence.split(ANNOTATION_DELIMITER)[0];
					annotation = sentence.split(ANNOTATION_DELIMITER)[1].toString();
					if(annotation.equals("F") && text.length() > 12){ 
						arguments.add(text);
					}
				}
				Reader.close();	
			}
			
			catch (IOException e) {
					e.printStackTrace();
				}
			
		 }
	
	 public void PopulateScores(){
		 SemanticScore semantic = new SemanticScore(TOPIC);	
		 SentimentScore sentiment = new SentimentScore();
		 double Score;  String label; 
		 int poscount = 0, negcount = 0;
		 TOPIC_LABEL = sentiment.GetSentimentScore(TOPIC);
		 if(TOPIC_LABEL.equals("neutral"))
			 TOPIC_LABEL = "pos";
		 for(String item:arguments){			  
			  Score = semantic.GetSemanticScore(item);
			  argSemanticscores.put(item, Score);
		    }	
		 TreeMap<String, Double> sortedSemanticMap = GetSortedResult();
		 for(Map.Entry<String,Double> item:sortedSemanticMap.entrySet()){
			 String key = item.getKey();
			 label = sentiment.GetSentimentScore(key);
			  if(label.equals("pos")) poscount++;
			  else if (label.equals("neg")) negcount++; 
			  if((label.equals("pos") && poscount <= 5))
				  argSentimentscores.put(key, label);
			  else if ((label.equals("neg") && negcount <= 5))
				  argSentimentscores.put(key, label);
			  else
			  {
				  if(poscount >= 5 && negcount >= 5){
					  break;
					 }
				  else
					  continue;
			  }
				  
			  
		 }
		 GetResult();
	 }
	 public TreeMap<String, Double> GetSortedResult(){
		 positive = new ArrayList<String>();
		 negative = new ArrayList<String>();
		 TreeMap<String, Double> sortedMap = SortByValue(argSemanticscores);
		 return sortedMap;
	 }
	 
	 public void GetResult(){
		 String label;
		 for(String item:argSentimentscores.keySet()) {
			 // String key = entry.getKey();
			  label = (String) argSentimentscores.get(item);
			  if(label.equals("pos")) {
				  positive.add(item);
			  }
			  else if (label.equals("neg")){
				  negative.add(item);
			  }
			 // Double value = entry.getValue();
			}
	     Outputresults();
	 }
	 
	 public void Outputresults(){
		 BufferedWriter writer = null;
			try {
				writer = new BufferedWriter(new FileWriter(new File(OUTPUT_FILE_PATH)));
				writer.write(TOPIC_LABEL);
				if(TOPIC_LABEL.equals("pos")){
					writer.write("Supporting Arguments" + System.lineSeparator() + System.lineSeparator());
						for(String nextValue : positive)
						{
							writer.write(nextValue + "-" + argSentimentscores.get(nextValue) + System.lineSeparator());					
						}
					writer.write(System.lineSeparator() + "Opposing Arguments" + System.lineSeparator() + System.lineSeparator());
						for(String nextValue : negative)
						{
							writer.write(nextValue + "-" + argSentimentscores.get(nextValue)+ System.lineSeparator());
						}
				}else {
					writer.write("Supporting Arguments" + System.lineSeparator()+ System.lineSeparator());
						for(String nextValue : negative)
						{
							writer.write(nextValue + "-" + argSentimentscores.get(nextValue)+ System.lineSeparator());					
						}
					writer.write(System.lineSeparator() + "Opposing Arguments" + System.lineSeparator()+ System.lineSeparator());
						for(String nextValue : positive)
						{
							writer.write(nextValue + "-" + argSentimentscores.get(nextValue) + System.lineSeparator());
						}
					}								
			writer.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
	 }
	
	 public TreeMap<String, Double> SortByValue(HashMap<String, Double> map) {
		 SortByValueDescending sortvalue;
		 sortvalue = new SortByValueDescending(map);
	     TreeMap<String,Double> sortedMap = new TreeMap<String,Double>(sortvalue);
	     sortedMap.putAll(map);
	     return sortedMap;
       }
	 
	 class SortByValueDescending implements Comparator<String>{		 
		 HashMap<String, Double> map;
		 
		    public SortByValueDescending(HashMap<String, Double> base) {
		        this.map = base;
		    }
		 
		    public int compare(String a, String b) {
		        if (map.get(a) >= map.get(b)) {
		            return -1;
		        } else {
		            return 1;
		        } // returning 0 would merge keys 
		    }
		}
	 
	
	
	public static void main(String[] args) {
		// TODO Auto-generated method stub
		String topic = args[0];
		String inputfilePath = args[1];
		String outputfilePath = args[2];
		DebatorApplication debator = new DebatorApplication(topic,inputfilePath , outputfilePath);
		debator.ExtractFactsfromFile();
		debator.PopulateScores();
		//debator.GetResult();
	}
	 
	
}
