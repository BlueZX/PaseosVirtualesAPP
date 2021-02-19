package com.ubb.paseosVirtuales.helper;

import android.content.Context;

import com.android.volley.Request;
import com.android.volley.Request.Method;
import com.android.volley.RequestQueue;
import com.android.volley.Response.ErrorListener;
import com.android.volley.Response.Listener;
import com.android.volley.error.AuthFailureError;
import com.android.volley.request.JsonObjectRequest;
import com.android.volley.request.SimpleMultiPartRequest;
import com.android.volley.toolbox.ImageCache;
import com.android.volley.toolbox.ImageLoader;
import com.android.volley.toolbox.Volley;

import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

/**.
 * <BASEURL>" Maybe get url from getString
 * Usage:
 * volley = new VolleyHelper(this, "<BASEURL>");
 * volley.post("<service_name>", null, new Listener<JSONObject>() {
 *      @override
 *      public void onResponse(JSONObject response) {
 *          Log.i("Got JSON", response.toString());
 *      }
 * }, new ErrorListener() {
 *
 * });
 */

public class VolleyHelper {

    private final Context mContext;
    private final RequestQueue mRequestQueue;
    private final ImageLoader mImageLoader;
    private final String mBaseUrl;

    public VolleyHelper(Context c, String baseUrl) {
        mContext = c;
        mRequestQueue = Volley.newRequestQueue(mContext);
        mBaseUrl = baseUrl;
        mImageLoader = new ImageLoader(mRequestQueue, new BitmapLruCache());
    }

    private String contructUrl(String method) {
        return mBaseUrl + "/" + method;
    }

    public ImageLoader getImageLoader() {
        return mImageLoader;
    }

    public void get(String method, JSONObject jsonRequest,
                    Listener<JSONObject> listener, ErrorListener errorListener) {

        JsonObjectRequest objRequest = new JsonObjectRequest(Method.GET, contructUrl(method), jsonRequest, listener, errorListener);
        mRequestQueue.add(objRequest);
    }

    public void get(String method, JSONObject jsonRequest,
                    Listener<JSONObject> listener, ErrorListener errorListener, String token) {

        JsonObjectRequest objRequest = new JsonObjectRequest(Method.GET, contructUrl(method), jsonRequest, listener, errorListener) {
            @Override
            public Map<String, String> getHeaders() throws AuthFailureError {
                Map<String, String> headers = new HashMap<>();
                headers.put("TOKEN", token);

                return headers;
            }
        };
        mRequestQueue.add(objRequest);
    }


    public void put(String method, JSONObject jsonRequest,
                    Listener<JSONObject> listener, ErrorListener errorListener, String token) {

        JsonObjectRequest objRequest = new JsonObjectRequest(Method.PUT, contructUrl(method), jsonRequest, listener, errorListener) {
            @Override
            public Map<String, String> getHeaders() throws AuthFailureError {
                Map<String, String> headers = new HashMap<>();
                headers.put("TOKEN", token);

                return headers;
            }
        };
        mRequestQueue.add(objRequest);
    }

    public void put(String method, JSONObject jsonRequest,
                    Listener<JSONObject> listener, ErrorListener errorListener){

        JsonObjectRequest objRequest = new JsonObjectRequest(Method.PUT, contructUrl(method), jsonRequest, listener, errorListener);
        mRequestQueue.add(objRequest);
    }

    public void post(String method, JSONObject jsonRequest,
                     Listener<JSONObject> listener, ErrorListener errorListener){

        JsonObjectRequest objRequest = new JsonObjectRequest(Method.POST, contructUrl(method), jsonRequest, listener, errorListener);
        mRequestQueue.add(objRequest);
    }

    public void post(String method, Listener<String> listener, ErrorListener errorListener, String token, String imagePath){
        SimpleMultiPartRequest objRequest = new SimpleMultiPartRequest(Request.Method.POST, contructUrl(method), listener, errorListener)  {
            @Override
            public Map<String, String> getHeaders() throws AuthFailureError {
                Map<String, String> headers = new HashMap<>();
                headers.put("TOKEN", token);

                return headers;
            }
        };

        objRequest.addFile("archivo", imagePath);
        mRequestQueue.add(objRequest);

    }

    public void delete(String method, JSONObject jsonRequest,
                       Listener<JSONObject> listener, ErrorListener errorListener){

        JsonObjectRequest objRequest = new JsonObjectRequest(Method.DELETE, contructUrl(method), jsonRequest, listener, errorListener);
        mRequestQueue.add(objRequest);
    }

}
