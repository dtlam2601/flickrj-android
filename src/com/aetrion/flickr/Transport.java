/*
 * Copyright (c) 2005 Aetrion LLC.
 */
package com.aetrion.flickr;

import java.io.IOException;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.util.List;

import com.yuyang226.flickr.oauth.OAuthInterface;
import com.yuyang226.flickr.oauth.OAuthTokenParameter;
import com.yuyang226.flickr.oauth.OAuthUtils;
import com.yuyang226.flickr.org.json.JSONException;

/**
 * The abstract Transport class provides a common interface for transporting requests to the Flickr servers. Flickr
 * offers several transport methods including REST, SOAP and XML-RPC. FlickrJ currently implements the REST transport
 * and work is being done to include the SOAP transport.
 *
 * @author Matt Ray
 * @author Anthony Eden
 */
public abstract class Transport {

    public static final String REST = "REST";

    private String transportType;
    protected Class<?> responseClass;
    private String path;
    private String host;
    private int port = 80;

    public String getHost() {
        return host;
    }

    public void setHost(String host) {
        this.host = host;
    }

    public int getPort() {
        return port;
    }

    public void setPort(int port) {
        this.port = port;
    }

    public String getTransportType() {
        return transportType;
    }

    public void setTransportType(String transport) {
        this.transportType = transport;
    }

    /**
     * Invoke an HTTP GET request on a remote host.  You must close the InputStream after you are done with.
     *
     * @param path The request path
     * @param parameters The parameters (collection of Parameter objects)
     * @return The Response
     * @throws IOException
     * @throws JSONException
     */
    public abstract Response get(String path, List<Parameter> parameters) throws IOException, JSONException;

    /**
     * Invoke an HTTP POST request on a remote host.
     *
     * @param path The request path
     * @param parameters The parameters (collection of Parameter objects)
     * @return The Response object
     * @throws IOException
     * @throws JSONException
     */
    public Response post(String path, List<Parameter> parameters) throws IOException, JSONException {
        return post(path, parameters, false);
    }
    
    public Response postJSON(String apiKey, String apiSharedSecret, 
    		List<Parameter> parameters) throws IOException, JSONException, InvalidKeyException, NoSuchAlgorithmException {
    	boolean isOAuth = false;
    	for (int i = parameters.size() - 1; i >= 0; i--) {
    		if (parameters.get(i) instanceof OAuthTokenParameter) {
    			isOAuth = true;
    			break;
    		}
    	}
    	parameters.add(new Parameter("nojsoncallback", "1"));
		parameters.add(new Parameter("format", "json"));
		if (isOAuth) {
			OAuthUtils.addOAuthParams(apiKey, apiSharedSecret, OAuthInterface.URL_REST, parameters);
		}
    	return post(OAuthInterface.PATH_REST, parameters, false);
    }
    
    /**
     * Invoke an HTTP POST request on a remote host.
     *
     * @param path The request path
     * @param parameters The parameters (List of Parameter objects)
     * @param multipart Use multipart
     * @return The Response object
     * @throws IOException
     * @throws JSONException
     */
    public abstract Response post(String path, List<Parameter> parameters, boolean multipart) throws IOException,
    JSONException;

    /**
     * @return Returns the path.
     */
    public String getPath() {
        return path;
    }

    /**
     * @param path The path to set.
     */
    public void setPath(String path) {
        this.path = path;
    }

    public Class<?> getResponseClass() {
        return responseClass;
    }

    public void setResponseClass(Class<?> responseClass) {
        if (responseClass == null) {
            throw new IllegalArgumentException("The response Class cannot be null");
        }
        this.responseClass = responseClass;
    }

}