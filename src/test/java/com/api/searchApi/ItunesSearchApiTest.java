package com.api.searchApi;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.Properties;

import org.testng.Assert;
import org.testng.annotations.BeforeTest;
import org.testng.annotations.Test;

import com.jayway.restassured.RestAssured;
import com.jayway.restassured.path.json.JsonPath;
import com.jayway.restassured.response.Response;
import com.jayway.restassured.specification.RequestSpecification;

public class ItunesSearchApiTest {
	Properties prop = new Properties();

	@BeforeTest
	public void getData() throws IOException{

		FileInputStream fis = new FileInputStream("src/test/resources/config.properties");
		prop.load(fis);	
	}

	@Test
	public void test_statusCode() {

		RestAssured.baseURI=prop.getProperty("baseUrl");
		com.jayway.restassured.RestAssured.given().
		param("term","spiderman").
		param("country", "US").
		param("media", "movie").
		param("limit", "15").
		when().get("search").then().statusCode(200);
	}
	@Test
	public void test_contentType() {

		RestAssured.baseURI=prop.getProperty("baseUrl");
		Response result=com.jayway.restassured.RestAssured.given().
				param("term","spiderman").
				param("country", "US").
				param("media", "movie").
				param("limit", "15").
				when().get("search").then().statusCode(200).extract().response();		
		Assert.assertEquals(result.getHeader("Content-Type"),"text/javascript; charset=utf-8");
	}

	@Test
	public void test_withAllParameters() {

		RestAssured.baseURI=prop.getProperty("baseUrl");
		int limitCount = 5;
		Response result=com.jayway.restassured.RestAssured.given().
				param("term"," jack+johnson").
				param("country", "US").
				param("media", "movie").
				param("limit", limitCount).
				when().get("search").then().assertThat().statusCode(200).and().contentType("text/javascript; charset=utf-8").extract().response();
		JsonPath js= ReusableMethod.rawToJson(result);
		int count =js.get("resultCount");
		Assert.assertTrue(count<=limitCount);
		for(int i=0;i<count;i++)
		{
			Assert.assertNotNull(js.get("results["+i+"].trackId"));
			Assert.assertEquals(js.get("results["+i+"].kind"),"feature-movie");

		}

	}
	@Test
	public void test_withNoParameters() {

		RestAssured.baseURI=prop.getProperty("baseUrl");
		Response result=com.jayway.restassured.RestAssured.given().
				when().get("search").then().assertThat().statusCode(200).and().contentType("text/javascript; charset=utf-8").extract().response();
		JsonPath js= ReusableMethod.rawToJson(result);
		int count =js.get("resultCount");
		Assert.assertEquals(count,0);


	}

	@Test
	public void test_withInvalidCountryParameter() {
		RestAssured.baseURI=prop.getProperty("baseUrl");

		Response result=com.jayway.restassured.RestAssured.given().
				param("term"," jack+johnson").
				param("country", "S").
				param("media", "movie").
				when().get("search").then().assertThat().statusCode(400).extract().response();
		JsonPath js= ReusableMethod.rawToJson(result);
		Assert.assertEquals(js.get("errorMessage"),"Invalid value(s) for key(s): [country]");

	}

	@Test
	public void test_withInvalidMediaParameter() {
		RestAssured.baseURI=prop.getProperty("baseUrl");

		Response result=com.jayway.restassured.RestAssured.given().
				param("term"," jack+johnson").
				param("country", "US").
				param("media", "ovie").
				when().get("search").then().assertThat().statusCode(400).extract().response();
		JsonPath js= ReusableMethod.rawToJson(result);
		Assert.assertEquals(js.get("errorMessage"),"Invalid value(s) for key(s): [mediaType]");

	}
	@Test
	public void test_withLimitGreaterThanMaxValue(){
		RestAssured.baseURI=prop.getProperty("baseUrl");

		Response result=com.jayway.restassured.RestAssured.given().
				param("term"," jack+johnson").
				param("country", "US").
				param("media", "movie").
				param("limit",300).
				when().get("search").then().assertThat().statusCode(200).extract().response();
		JsonPath js= ReusableMethod.rawToJson(result);
		int count =js.get("resultCount");
		Assert.assertTrue(count<=200);
	}

}
