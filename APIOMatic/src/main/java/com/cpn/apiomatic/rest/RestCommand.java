package com.cpn.apiomatic.rest;

import org.apache.http.auth.AuthScope;
import org.apache.http.auth.UsernamePasswordCredentials;
import org.apache.http.impl.client.DefaultHttpClient;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.client.HttpComponentsClientHttpRequestFactory;
import org.springframework.web.client.RestTemplate;

public class RestCommand<Request, Response> {

	private RestTemplate restTemplate;// = new RestTemplate();
	private String path;
	private Request requestModel;
	private Class<Response> responseModel;
	private HttpHeaderDelegate headerDelegate=new NoAuthHeaderDelegate();


	public RestCommand(){
		restTemplate=new RestTemplate();
	}
	
	public RestCommand(final String aUserName, final String aPassword,final String anAuthDomain, int aPort){
		DefaultHttpClient client = new DefaultHttpClient();
		UsernamePasswordCredentials credentials = new UsernamePasswordCredentials(aUserName, aPassword);
		client.getCredentialsProvider().setCredentials(new AuthScope(anAuthDomain, aPort, AuthScope.ANY_REALM), credentials);
		HttpComponentsClientHttpRequestFactory commons = new HttpComponentsClientHttpRequestFactory(client);
		restTemplate=new RestTemplate(commons);
	}
	
	public void delete() {
		if (getRequestModel() == null) {
			restTemplate.exchange(getPath(), HttpMethod.DELETE, new HttpEntity<String>(getHttpHeaders()), null);
		} else {
			restTemplate.exchange(getPath(), HttpMethod.DELETE, new HttpEntity<Request>(getRequestModel(), getHttpHeaders()), null);
		}
	}

	public Response get() {
		return restTemplate.exchange(getPath(), HttpMethod.GET, new HttpEntity<String>(getHttpHeaders()), getResponseModel()).getBody();
	}

	public HttpHeaders getHttpHeaders() {
		return headerDelegate.getHttpHeaders();
	}

	public String getPath() {
		return path;
	}

	public Request getRequestModel() {
		return requestModel;
	}

	public Class<Response> getResponseModel() {
		return responseModel;
	}

	public Response post() {
		return restTemplate.exchange(getPath(), HttpMethod.POST, new HttpEntity<Request>(getRequestModel(), getHttpHeaders()), getResponseModel()).getBody();
	}

	public Response put() {
		return restTemplate.exchange(getPath(), HttpMethod.PUT, new HttpEntity<Request>(getRequestModel(), getHttpHeaders()), getResponseModel()).getBody();
	}

	public void setPath(final String path) {
		this.path = path;
	}

	public void setRequestModel(final Request requestModel) {
		this.requestModel = requestModel;
	}

	public void setResponseModel(final Class<Response> responseModel) {
		this.responseModel = responseModel;
	}

	public HttpHeaderDelegate getHeaderDelegate() {
		return headerDelegate;
	}

	public void setHeaderDelegate(HttpHeaderDelegate headerDelegate) {
		this.headerDelegate = headerDelegate;
	}

	
}
