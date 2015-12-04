package com.asu.debator.util;


import java.io.IOException;



import org.apache.http.HttpResponse;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClientBuilder;
import org.apache.http.util.EntityUtils;
import com.google.gson.Gson;


public class SemanticScore {  
	private final static String  URL = "http://api.cortical.io/rest/compare?retina_name=en_associative";
	private  static String  TOPIC;
	
	public SemanticScore(String topic){		
	    TOPIC = topic;
	}
    	   
	Gson gson= new Gson();

	 public double GetSemanticScore(String sentence){
	
		 try (CloseableHttpClient httpClient = HttpClientBuilder.create().build()) {
	         HttpPost request = new HttpPost(URL);
	         StringEntity params =new StringEntity("[{\"text\" : \" " + TOPIC + " \"},{\"text\" : \"  " + sentence + " \"}]");
	         request.setHeader("Content-Type", "application/json");
	         request.setHeader("api-key","0056e300-93eb-11e5-9e69-03c0722e0d16");
	         request.setEntity(params);
	         HttpResponse result = httpClient.execute(request);
	         String json = EntityUtils.toString(result.getEntity(), "UTF-8");

	         
	         ResponseResult responseResult = gson.fromJson(json, ResponseResult.class);
	         return responseResult.getcosineSimilarity();
	     } catch (IOException ex) {
	     }
	     return 0.00;
		 
	 }  
	
	 public class ResponseResult {
		 double cosineSimilarity;	
		 public double getcosineSimilarity(){
			 return cosineSimilarity;
		 }
	 }
	
	 
	 
	public static void main(String[] args) {
		
	}

}
