package com.amazonaws.lambda.demo;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;

import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

import com.amazonaws.lambda.db.DAO;
import com.amazonaws.services.lambda.runtime.Context;
import com.amazonaws.services.lambda.runtime.LambdaLogger;
import com.amazonaws.services.lambda.runtime.RequestStreamHandler;
import com.google.gson.Gson;

public class DeletedScheduleHandler implements RequestStreamHandler{
	public LambdaLogger logger = null;
	/** Load from RDS, if it exists
	 * 
	 * @throws Exception 
	 */
	boolean deleteSchedule(String secretCode) throws Exception {
		if (logger != null) { logger.log("in deleteSchedule"); }
		DAO dao = new DAO();
		return dao.deleteSchedule(secretCode);
	}
	
	
	
	@Override
	public void handleRequest(InputStream input, OutputStream output, Context context) throws IOException {
		logger = context.getLogger();
		logger.log("Loading Java Lambda handler to delete schedule");

		JSONObject headerJson = new JSONObject();
		headerJson.put("Content-Type",  "application/json");  // not sure if needed anymore?
		headerJson.put("Access-Control-Allow-Methods", "GET,POST,OPTIONS");
	    headerJson.put("Access-Control-Allow-Origin",  "*");
	        
		JSONObject responseJson = new JSONObject();
		responseJson.put("headers", headerJson);

		DeletedScheduleResponse response = null;
		// extract body from incoming HTTP POST request. If any error, then return 422 error
				String body;
				boolean processed = false;
				try {
					BufferedReader reader = new BufferedReader(new InputStreamReader(input));
					JSONParser parser = new JSONParser();
					JSONObject event = (JSONObject) parser.parse(reader);
					logger.log("event:" + event.toJSONString());
					
					String method = (String) event.get("httpMethod");
					if (method != null && method.equalsIgnoreCase("OPTIONS")) {
						logger.log("Options request");
						response = new DeletedScheduleResponse("name", 200);  // OPTIONS needs a 200 response
				        responseJson.put("body", new Gson().toJson(response));
				        processed = true;
				        body = null;
					} else {
						body = (String)event.get("body");
						if (body == null) {
							body = event.toJSONString();  // this is only here to make testing easier
						}
					}
				} catch (ParseException pe) {
					logger.log(pe.toString());
					response = new DeletedScheduleResponse("Bad Request:" + pe.getMessage(), 422);  // unable to process input
			        responseJson.put("body", new Gson().toJson(response));
			        processed = true;
			        body = null;
				}

				if (!processed) {
					DeletedScheduleRequest req = new Gson().fromJson(body, DeletedScheduleRequest.class);
					logger.log(req.toString());

					DeletedScheduleResponse resp;
					logger.log("text1");
					try {
						if (deleteSchedule(req.secretCode)) {
							resp = new DeletedScheduleResponse("Confirmed");
						} else {
							resp = new DeletedScheduleResponse("Unable to delete schedule for  [" + req.secretCode + "]", 422);
						}
					} catch (Exception e) {
						resp = new DeletedScheduleResponse("Unable to delete schedule (" + e.getMessage() + ")", 403);
					}
					// compute proper response
			        responseJson.put("body", new Gson().toJson(resp));
			        logger.log("end result:" + responseJson.toJSONString());
			        logger.log(responseJson.toJSONString());
			        OutputStreamWriter writer = new OutputStreamWriter(output, "UTF-8");
			        writer.write(responseJson.toJSONString());  
			        writer.close();
				}
				
				
			}

}
