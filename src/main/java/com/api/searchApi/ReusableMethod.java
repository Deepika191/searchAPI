package com.api.searchApi;

import com.jayway.restassured.path.json.JsonPath;
import com.jayway.restassured.response.Response;

public class ReusableMethod {
	public static JsonPath rawToJson(Response r)
	{ 
		String respon=r.asString();
		JsonPath x=new JsonPath(respon);
		return x;
	}

}
