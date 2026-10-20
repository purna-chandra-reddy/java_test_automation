package com.automation.api;

import com.fasterxml.jackson.annotation.JsonInclude.Include;
import com.fasterxml.jackson.databind.MapperFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import io.restassured.RestAssured;
import io.restassured.authentication.PreemptiveBasicAuthScheme;
import io.restassured.builder.RequestSpecBuilder;
import io.restassured.config.EncoderConfig;
import io.restassured.config.HttpClientConfig;
import io.restassured.config.RestAssuredConfig;
import io.restassured.filter.log.RequestLoggingFilter;
import io.restassured.filter.log.ResponseLoggingFilter;
import io.restassured.http.ContentType;
import io.restassured.parsing.Parser;
import io.restassured.response.Response;
import io.restassured.specification.RequestSpecification;
import org.apache.http.params.CoreConnectionPNames;
import org.assertj.core.api.Assertions;
import org.testng.Reporter;

import java.io.*;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.TimeUnit;

import static io.restassured.RestAssured.given;

public class BaseApi {

	private RequestSpecBuilder requestSpecBuilder = new RequestSpecBuilder();
	protected MethodType method;
	private Object body;
	private ContentType contentType;
	private String baseUri;
	private Map<String, Object> queryParams = new HashMap<>();
	private Map<String, Object> formURLEncoded = new HashMap<>();
	private Map<String, Object> params = new HashMap<>();
	private String basePath;
	private String cookie;
    private Map<String, Object> headers = new HashMap<>();
    private Boolean isCaptureExtent = true;
    private Boolean isCaptureExtentResponse = true;
    private Response response;
    private String authName;
    private String authPassword;
	PrintStream log;

	{
		try {
			log = new PrintStream(new FileOutputStream("logging.txt"));
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		}
	}

	public String getAuthName() {
		return authName;
	}

	public void setAuthName(String authName) {
		this.authName = authName;
	}

	public String getAuthPassword() {
		return authPassword;
	}

	public void setAuthPassword(String authPassword) {
		this.authPassword = authPassword;
	}

	public Boolean getIsCaptureExtent() {
		return isCaptureExtent;
	}

	public void setIsCaptureExtent(Boolean captureExtent) {
		isCaptureExtent = captureExtent;
	}

	public enum MethodType {
		POST, GET, PUT, DELETE, PATCH
	}

	public Boolean getCaptureExtentResponse() {
		return isCaptureExtentResponse;
	}

	public void setCaptureExtentResponse(Boolean captureExtentResponse) {
		isCaptureExtentResponse = captureExtentResponse;
	}

	public void setup(int connTime,int respTime) {
		HttpClientConfig httpClientConfig = HttpClientConfig.httpClientConfig()
				.setParam(CoreConnectionPNames.CONNECTION_TIMEOUT, connTime)
				.setParam(CoreConnectionPNames.SO_TIMEOUT, respTime);
		RestAssured.config = RestAssuredConfig.config().httpClient(httpClientConfig);
	}

	public void logAllData(){
		requestSpecBuilder.addFilter(RequestLoggingFilter.logRequestTo(log))
				.addFilter(ResponseLoggingFilter.logResponseTo(log))
				.addFilter(ResponseLoggingFilter.responseLogger());
	}

	public MethodType getMethod() {
		return method;
	}

	public void setMethod(MethodType method) {
		this.method = method;
	}

    public void setBody(Object obj) {
	    this.body = obj;
		requestSpecBuilder.setBody(obj);
    }

	public void setPOJOBody(Object obj) {
		this.body = obj;
		requestSpecBuilder.setBody(obj);
	}

    public Object getBody(){
	    return this.body;
    }

    public void setContentType(ContentType contentType){
	    this.contentType = contentType;
	    requestSpecBuilder.setContentType(contentType);
    }

    public ContentType getContentType(){
	    return this.contentType;
    }

    public void setBaseUri(String baseUri){
	    this.baseUri = baseUri;
	    requestSpecBuilder.setBaseUri(baseUri);
    }

    public String getBaseUri(){
	    return this.baseUri;
    }

	public void setCookie(String cookie){
		this.cookie = cookie;
		requestSpecBuilder.addCookie(cookie);
	}

	public String getCookie(){
		return this.cookie;
	}



    public void setBasePath(String basePath){
        this.basePath = basePath;
        requestSpecBuilder.setBasePath(basePath);
    }

    public String getBasePath(){
        return this.basePath;
    }

    public void addHeaders(Map<String, String> headers){
        this.headers.putAll(headers);
        requestSpecBuilder.addHeaders(headers);
    }

    public void addHeader(String headerKey, String headerValue){
		this.headers.put(headerKey,headerValue);
		requestSpecBuilder.addHeader(headerKey,headerValue);
	}

    public Map<String, Object> getHeaders(){
        return this.headers;
    }


    public void addQueryParam(String paramKey, Object paramValue){
	    this.queryParams.put(paramKey,paramValue);
	    requestSpecBuilder.addQueryParam(paramKey,paramValue);
    }

	public void removeQueryParam(String paramKey){
		requestSpecBuilder.removeQueryParam(paramKey);
	}

    public void addQueryParams(Map<String, String> queryParams){
	    this.queryParams.putAll(queryParams);
	    requestSpecBuilder.addQueryParams(queryParams);
    }

    public Map<String, Object> getQueryParams(){
	    return this.queryParams;
    }


	public void addFormURLEncoded(String paramKey, Object paramValue){
		this.formURLEncoded.put(paramKey,paramValue);
		requestSpecBuilder.addFormParam(paramKey,paramValue);
	}

	public void addFormURLEncoded(Map<String, Object> formURLEncoded){
		this.formURLEncoded.putAll(formURLEncoded);
		requestSpecBuilder.addFormParams(formURLEncoded);
	}

	public Map<String, Object> getFormURLEncoded(){
		return this.formURLEncoded;
	}

	public void addParam(String paramKey, Object paramValue){
		this.params.put(paramKey,paramValue);
		requestSpecBuilder.addParam(paramKey,paramValue);
	}

	public void addParams(Map<String, String> queryParams){
		this.params.putAll(queryParams);
		requestSpecBuilder.addParams(queryParams);
	}

	public Map<String, Object> getParams(){
		return this.params;
	}

	public RequestSpecBuilder getRequestSpecBuilder() {
		return requestSpecBuilder;
	}

	public Response execute() {
		RequestSpecification requestSpecification = requestSpecBuilder.addFilter(new RequestLoggingFilter())
				.addFilter(new ResponseLoggingFilter()).build();
		Response response;
		if(this.authName!=null && this.authPassword!=null){
			PreemptiveBasicAuthScheme basicAuth = new PreemptiveBasicAuthScheme();
			basicAuth.setUserName(this.authName);
			basicAuth.setPassword(this.authPassword);
			requestSpecBuilder.setAuth(basicAuth);
		}
		 EncoderConfig ec = new EncoderConfig();
	     ec.appendDefaultContentCharsetToContentTypeIfUndefined(false);
		RestAssured.defaultParser = Parser.JSON;
		RestAssuredConfig config = RestAssured.config().httpClient(HttpClientConfig.httpClientConfig().setParam("http.socket.timeout", 50000)).encoderConfig(ec);
		switch (method) {
		case GET:
			response = given().config(config).spec(requestSpecification).when().get();
			break;
		case POST:
			response = given().config(config).spec(requestSpecification).when().post();
			break;
		case PUT:
			response = given().config(config).spec(requestSpecification).when().put();
			break;
		case DELETE:
			response = given().config(config).spec(requestSpecification).when().delete();
			break;
		case PATCH:
			response = given().config(config).spec(requestSpecification).when().patch();
			break;
		default:
			throw new RuntimeException("API method not specified");

		}
		this.response=response;
		printResponse(response);
		return response;
	}

	public Response executeUntilExpectedConditionMet(String expectedJSONKey, String expectedJSONValue,
			int pollingTimeInSec, int pollingCountInSec) {
		Response response = null;
		String actualJSONValue = "";
		try {
			for (int i = 0; i < pollingCountInSec; i++) {
				response = this.execute();
				actualJSONValue = response.jsonPath().get(expectedJSONKey).toString();
				if (expectedJSONValue.equalsIgnoreCase(actualJSONValue)) {
					break;
				}
				Thread.sleep(pollingTimeInSec * 1000);
			}
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
		Assertions.assertThat(actualJSONValue).isEqualToIgnoringCase(expectedJSONValue);
		return response;
	}

	private void printResponse(Response response) {
		String contentType = response.contentType();

		if (contentType.toLowerCase().contains("text/html")) {
			final DateFormat timeFormat = new SimpleDateFormat("MM.dd.yyyy HH-mm-ss");
			final String fileName = Reporter.getCurrentTestResult().getMethod().getMethodName() + "_"
					+ timeFormat.format(new Date()) + ".html";

			String outputDir = Reporter.getCurrentTestResult().getTestContext().getOutputDirectory();
			outputDir = outputDir.substring(0, outputDir.lastIndexOf(File.separator)) + "/html";

			File file = new File(outputDir + File.separator + fileName);
			if (file.getParentFile() != null) {
				file.getParentFile().mkdirs();
			}
			PrintWriter writer = null;

			try {
				file.createNewFile();
				writer = new PrintWriter(file);
				writer.write(response.asString());
				writer.flush();
				Reporter.log("<a href=\"" + fileName + "\" target=\"_blank\"><b>API Response</b></a><br>");
			} catch (Throwable e) {
				throw new RuntimeException(e);
			} finally {
				writer.close();
			}
		} else {
			Reporter.log("<br>" + "API Response:" + response.getBody().prettyPrint());
		}
	}

	public BaseApi setBodyOmitNullValueAttributes(Object obj) {
		String request = "";
		ObjectMapper mapper = new ObjectMapper();
		mapper.configure(MapperFeature.USE_ANNOTATIONS, false);
		mapper.setSerializationInclusion(Include.NON_NULL);
		try {
			request = mapper.writeValueAsString(obj);
		} catch (Throwable e) {
			throw new RuntimeException(e);
		}
		requestSpecBuilder.setBody(request);
		return this;
	}

	public BaseApi setBodyKeepNullValueAttributes(Object obj) {
		String request = "";
		ObjectMapper mapper = new ObjectMapper();
		mapper.configure(MapperFeature.USE_ANNOTATIONS, false);
		mapper.setSerializationInclusion(Include.ALWAYS);
		try {
			request = mapper.writeValueAsString(obj);
		} catch (Exception e) {
			throw new RuntimeException(e);
		}
		requestSpecBuilder.setBody(request);
		return this;
	}

}
