package it.polimi.dice.verification.httpclient;

/*
 * ====================================================================
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements.  See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership.  The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License.  You may obtain a copy of the License at
 *
 *   http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied.  See the License for the
 * specific language governing permissions and limitations
 * under the License.
 * ====================================================================
 *
 * This software consists of voluntary contributions made by many
 * individuals on behalf of the Apache Software Foundation.  For more
 * information on the Apache Software Foundation, please see
 * <http://www.apache.org/>.
 *
 */

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import org.apache.http.Header;
import org.apache.http.HttpEntity;
import org.apache.http.HttpHeaders;
import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.ResponseHandler;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.util.EntityUtils;

/**
 * This example demonstrates the use of the {@link ResponseHandler} to simplify
 * the process of processing the HTTP response and releasing associated resources.
 */
public class HttpClientOld {

	
	public static void postJSON() 
			  throws ClientProtocolException, IOException {
			    CloseableHttpClient client = HttpClients.createDefault();
			    HttpPost httpPost = new HttpPost("http://localhost:5000/longtasks");
			    String json = "{\"title\":\"pinellaxJAVA\",\"json_context\":{\"verification_params\": {\"base_quantity\": 10, \"periodic_queues\": [\"expander\"], \"num_steps\": 20, \"max_time\": 20000, \"plugin\": [\"ae2bvzot\", \"ae2sbvzot\"]}, \"version\": \"0.1\", \"app_name\": \"SIMPLIFIED FOCUSED CRAWLER\", \"topology\": {\"bolts\": [{\"d\": 0.0, \"parallelism\": 4, \"min_ttf\": 1000, \"alpha\": 0.5, \"sigma\": 2.0, \"id\": \"WpDeserializer\", \"subs\": [\"wpSpout\"]}, {\"d\": 0.0, \"parallelism\": 8, \"min_ttf\": 1000, \"alpha\": 3.0, \"sigma\": 0.75, \"id\": \"expander\", \"subs\": [\"WpDeserializer\"]}, {\"d\": 0.0, \"parallelism\": 1, \"min_ttf\": 1000, \"alpha\": 1.0, \"sigma\": 1.0, \"id\": \"articleExtraction\", \"subs\": [\"expander\"]}, {\"d\": 0.0, \"parallelism\": 1, \"min_ttf\": 1000, \"alpha\": 1.0, \"sigma\": 1.0, \"id\": \"mediaExtraction\", \"subs\": [\"expander\"]}], \"init_queues\": 4, \"max_reboot_time\": 100, \"max_idle_time\": 1.0, \"min_reboot_time\": 10, \"spouts\": [{\"avg_emit_rate\": 4.0, \"id\": \"wpSpout\"}], \"queue_threshold\": 0}, \"description\": \"\"}}";
			    StringEntity entity = new StringEntity(json);
			    httpPost.setEntity(entity);
			    httpPost.setHeader("Accept", "application/json");
			    httpPost.setHeader("Content-type", "application/json");
			 
			    CloseableHttpResponse response = client.execute(httpPost);
			    System.out.println(response.getStatusLine().getStatusCode());
			    Header[] headers = response.getAllHeaders();
				for (Header header : headers) {
					System.out.println("Key : " + header.getName() 
					      + " ,Value : " + header.getValue());
				}
			    client.close();
			}
	
	public static void getTaskStatus() throws ClientProtocolException, IOException{
        CloseableHttpClient httpclient = HttpClients.createDefault();
        try {
            HttpGet httpget = new HttpGet("http://localhost:5000/status/a2a6d0e0-b56d-4ec2-b0b1-08d9c29cf84c");

            System.out.println("Executing request " + httpget.getRequestLine());

            // Create a custom response handler
            ResponseHandler<String> responseHandler = new ResponseHandler<String>() {

                @Override
                public String handleResponse(
                        final HttpResponse response) throws ClientProtocolException, IOException {
                    int status = response.getStatusLine().getStatusCode();
                    if (status >= 200 && status < 300) {
                        HttpEntity entity = response.getEntity();
                        return entity != null ? EntityUtils.toString(entity) : null;
                    } else {
                        throw new ClientProtocolException("Unexpected response status: " + status);
                    }
                }

            };
            String responseBody = httpclient.execute(httpget, responseHandler);
            System.out.println("----------------------------------------");
            System.out.println(responseBody);
            
            
            
        } finally {
            httpclient.close();
        }

		
	}
	
    public final static void main(String[] args) throws Exception {
        getTaskStatus();
    	postJSON();
    }

}
