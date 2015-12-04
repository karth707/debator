package com.asu.debator.util;
import java.io.IOException;

import org.apache.http.HttpResponse;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClientBuilder;
import org.apache.http.util.EntityUtils;

import com.google.gson.Gson;

public class SentimentScore {
 private final static String  URL = "http://text-processing.com/api/sentiment/";
 Gson gson= new Gson();

 public String GetSentimentScore(String sentence){
	 Data senddata = new Data();
	 senddata.SetSentence(sentence);
	 try (CloseableHttpClient httpClient = HttpClientBuilder.create().build()) {
         HttpPost request = new HttpPost(URL);
         StringEntity params =new StringEntity("language=english&text= " + sentence +"");
         request.setHeader("content-type", "text/html");
         request.setEntity(params);
         HttpResponse result = httpClient.execute(request);
         String json = EntityUtils.toString(result.getEntity(), "UTF-8");     
         Response ResponseResult = gson.fromJson(json, Response.class);
         return ResponseResult.label;      

     } catch (IOException ex) {
     }
     return null;
	 
 }  
 
 public class Data {
	 
	 public String language = "english";
	 public String sentence;
	 public void SetSentence(String sentence){
		 this.sentence = sentence;
	 }
	 
 }
 
 public class probabilities {
	 
	 private double pos;
	 private double neg;
	 public double getpos(){
		 return pos;
	 }
	 public double getneg(){
		 return neg;
	 }
 } 
	 public class Response{
	
	     private String label;
	     private probabilities probability;	     
	     
	     public String getlabel(){
			 return label;
		 }
		 public probabilities getprobablility(){
			 return probability;
		 }
	     	 }
	public static void main(String[] args) {
		// TODO Auto-generated method stub
	}

}
