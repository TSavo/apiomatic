package com.cpn.apiomatic.rest;

import org.apache.http.auth.AuthScope;
import org.apache.http.auth.UsernamePasswordCredentials;
import org.apache.http.conn.scheme.PlainSocketFactory;
import org.apache.http.conn.scheme.Scheme;
import org.apache.http.conn.scheme.SchemeRegistry;
import org.apache.http.conn.ssl.SSLSocketFactory;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.impl.conn.PoolingClientConnectionManager;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.client.HttpComponentsClientHttpRequestFactory;
import org.springframework.web.client.RestTemplate;

public class RestCommand<Request, Response> {

	private RestTemplate restTemplate;// = new RestTemplate();
	private String url;
	private Request requestModel;
	private Class<Response> responseModel;
	private HttpHeaderDelegate headerDelegate = new NoAuthHeaderDelegate();
	private static SchemeRegistry schemeRegistry = new SchemeRegistry();
	private static PoolingClientConnectionManager cm = new PoolingClientConnectionManager(schemeRegistry);
	static{
		schemeRegistry.register(
		         new Scheme("http", 80, PlainSocketFactory.getSocketFactory()));
		schemeRegistry.register(
		         new Scheme("https", 443, SSLSocketFactory.getSocketFactory()));
		// Increase max total connection to 200
		cm.setMaxTotal(100);
		// Increase default max connection per route to 20
		cm.setDefaultMaxPerRoute(20);
	}
	
	public RestCommand() {
		restTemplate=new RestTemplate(new HttpComponentsClientHttpRequestFactory(new DefaultHttpClient(cm)));
	}

	public RestCommand(final String aUserName, final String aPassword, final String anAuthDomain, int aPort) {
		DefaultHttpClient client = new DefaultHttpClient(cm);
		UsernamePasswordCredentials credentials = new UsernamePasswordCredentials(aUserName, aPassword);
		client.getCredentialsProvider().setCredentials(new AuthScope(anAuthDomain, aPort, AuthScope.ANY_REALM), credentials);
		HttpComponentsClientHttpRequestFactory commons = new HttpComponentsClientHttpRequestFactory(client);
		restTemplate = new RestTemplate(commons);
	}

	public RestCommand(final String aUrl, Request aRequest, Class<Response> aResponse) {
		this();
		url = aUrl;
		requestModel = aRequest;
		responseModel = aResponse;
	}

	public RestCommand(final String aUrl, Class<Response> aResponse) {
		this();
		url = aUrl;
		responseModel = aResponse;
	}

	public RestCommand(final String aUrl, Class<Response> aResponse, HttpHeaderDelegate aHeaderDelegate) {
		this(aUrl, aResponse);
		headerDelegate = aHeaderDelegate;
	}

	public RestCommand(final String aUrl, Request aRequest, Class<Response> aResponse, HttpHeaderDelegate aHeaderDelegate) {
		this(aUrl, aRequest, aResponse);
		headerDelegate = aHeaderDelegate;
	}

	public void delete() {
		if (getRequestModel() == null) {
			restTemplate.exchange(getUrl(), HttpMethod.DELETE, new HttpEntity<String>(getHttpHeaders()), null);
		} else {
			restTemplate.exchange(getUrl(), HttpMethod.DELETE, new HttpEntity<Request>(getRequestModel(), getHttpHeaders()), null);
		}
	}

	public Response get() {
		return restTemplate.exchange(getUrl(), HttpMethod.GET, new HttpEntity<String>(getHttpHeaders()), getResponseModel()).getBody();
	}

	public HttpHeaders getHttpHeaders() {
		return headerDelegate.getHttpHeaders();
	}

	public String getUrl() {
		return url;
	}

	public Request getRequestModel() {
		return requestModel;
	}

	public Class<Response> getResponseModel() {
		return responseModel;
	}

	public Response post() {
		return restTemplate.exchange(getUrl(), HttpMethod.POST, new HttpEntity<Request>(getRequestModel(), getHttpHeaders()), getResponseModel()).getBody();
	}

	public Response put() {
		return restTemplate.exchange(getUrl(), HttpMethod.PUT, new HttpEntity<Request>(getRequestModel(), getHttpHeaders()), getResponseModel()).getBody();
	}

	public void setUrl(final String path) {
		this.url = path;
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
