package app.controller;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;

public class RequestBuilder {

	private static final String USER_AGENT = "Mozilla/5.0";
	private String url;
	private RequestType requestType;
	private Integer responseCode;
	private String response;

	public RequestBuilder(String url, RequestType requestType) {
		this.url = url;
		this.requestType = requestType;
	}

	public String getUrl() {
		return url;
	}

	public void setUrl(String url) {
		this.url = url;
	}

	public RequestType getRequestType() {
		return requestType;
	}

	public void setRequestType(RequestType requestType) {
		this.requestType = requestType;
	}

	public int getResponseCode() {
		return responseCode;
	}

	public String getResponse() {
		return response;
	}

	public RequestBuilder sendRequest() throws IOException {
		if (this.url == null) return this;

		HttpURLConnection con = (HttpURLConnection) new URL(url).openConnection();
		con.setRequestMethod(requestType.getRequestType());
		con.setRequestProperty("User-Agent", USER_AGENT);

		if (requestType == RequestType.POST) {
			con.setDoOutput(true);
			OutputStream os = con.getOutputStream();

			String[] split = url.split("?");
			String params = "";
			if (split != null) {
				params = split[1];
			}
			os.write(params.getBytes());
			os.flush();
			os.close();
		}

		this.responseCode = con.getResponseCode();

		if (responseCode == HttpURLConnection.HTTP_OK) { // success
			BufferedReader in = new BufferedReader(new InputStreamReader(con.getInputStream()));
			StringBuffer response = new StringBuffer();
			String inputLine;
			while ((inputLine = in.readLine()) != null) {
				response.append(inputLine);
			}
			in.close();

			this.response = response.toString();
		} else {
			// error
		}

		return this;
	}

}

enum RequestType {

	GET("GET"), POST("POST");

	private String requestType;

	private RequestType(String requestType) {
		this.requestType = requestType;
	}

	public String getRequestType() {
		return requestType;
	}

}