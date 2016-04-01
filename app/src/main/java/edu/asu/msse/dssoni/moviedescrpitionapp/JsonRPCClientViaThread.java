/**
 *  Copyright 2016 Dhawal Soni
 *
 *  I give the Instructor and Arizona State University right to use
 *  this application source code to build and evaluate the software package.
 *
 *  Licensed under the Apache License, Version 2.0 (the "License");
 *  you may not use this file except in compliance with the License.
 *  You may obtain a copy of the License at
 *
 *  http://www.apache.org/licenses/LICENSE-2.0
 *
 *  Unless required by applicable law or agreed to in writing, software
 *  distributed under the License is distributed on an "AS IS" BASIS,
 *  WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *  See the License for the specific language governing permissions and
 *  limitations under the License.
 *
 *  Created by Dhawal Soni on 3/13/2016.
 *
 *  @author Dhawal Soni mailto:dhawal.soni@asu.edu
 *  @version March 13, 2016
 */

package edu.asu.msse.dssoni.moviedescrpitionapp;

import android.content.Intent;
import android.os.Handler;
import android.util.Log;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedInputStream;
import java.io.ByteArrayOutputStream;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.zip.GZIPInputStream;


public class JsonRPCClientViaThread extends Thread {

    private final Map<String, String> headers;
    private URL url;
    private String method;
    private String requestData;
    private Handler handler;
    private MainActivity parent;

    public JsonRPCClientViaThread(URL url, Handler handler, MainActivity parent, String method, String paramsArray) {
        this.url = url;
        this.parent = parent;
        this.method = method;
        this.handler = handler;
        this.headers = new HashMap<String, String>();
        requestData = "{ \"jsonrpc\":\"2.0\", \"method\":\""+method+"\", \"params\":"+paramsArray+
                ",\"id\":3}";
    }

    public void run(){
        try {
            String respData = this.post(url, headers, requestData);
            android.util.Log.d(this.getClass().getSimpleName(),"Result of JsonRPC request: "+respData);
            if(method.equals("getNames")){
                JSONObject jo = new JSONObject(respData);
                JSONObject jo_result = jo.getJSONObject("result");
                handler.post(new UpdateMovie(parent,jo_result));
            }else if(method.equals("get")){
                JSONObject jo = new JSONObject(respData);
                MovieDescription md = new MovieDescription(jo.getJSONObject("result"));
                handler.post(new DisplayDetails(parent,md));
            }
            else if(method.equals("remove")){
                JSONObject jo = new JSONObject(respData);
            } else if(method.equals("getMovieList")) {
                JSONObject jo = new JSONObject(respData);
                JSONObject jo_result = jo.getJSONObject("result");
                handler.post(new UpdateMovie(parent,jo_result));
            }

            else if(method.equals("add")){
                JSONObject jo = new JSONObject(respData);
                if(jo.toString() == "true"){
                    Log.d("Add operation:", "true");
                }
                else {
                    Log.d("Add operation:", "false");
                }
            }
        }catch (Exception ex){
            android.util.Log.d(this.getClass().getSimpleName(),"Exception in JsonRPC request: "+ex.getMessage());
        }
    }

    public void setHeader(String key, String value) {
        this.headers.put(key, value);
    }

    public String call(String requestData) throws Exception {
        String respData = post(url, headers, requestData);
        return respData;
    }

    private String post(URL url, Map<String, String> headers, String data) throws Exception {
        HttpURLConnection connection = (HttpURLConnection) url.openConnection();
        if (headers != null) {
            for (Map.Entry<String, String> entry : headers.entrySet()) {
                connection.addRequestProperty(entry.getKey(), entry.getValue());
            }
        }
        connection.addRequestProperty("Accept-Encoding", "gzip");
        connection.setRequestMethod("POST");
        connection.setDoOutput(true);
        connection.connect();
        OutputStream out = null;
        try {
            out = connection.getOutputStream();
            out.write(data.getBytes());
            out.flush();
            out.close();
            int statusCode = connection.getResponseCode();
            if (statusCode != HttpURLConnection.HTTP_OK) {
                throw new Exception(
                        "Unexpected status from post: " + statusCode);
            }
        } finally {
            if (out != null) {
                out.close();
            }
        }
        String responseEncoding = connection.getHeaderField("Content-Encoding");
        responseEncoding = (responseEncoding == null ? "" : responseEncoding.trim());
        ByteArrayOutputStream bos = new ByteArrayOutputStream();
        InputStream in = connection.getInputStream();
        try {
            in = connection.getInputStream();
            if ("gzip".equalsIgnoreCase(responseEncoding)) {
                in = new GZIPInputStream(in);
            }
            in = new BufferedInputStream(in);
            byte[] buff = new byte[1024];
            int n;
            while ((n = in.read(buff)) > 0) {
                bos.write(buff, 0, n);
            }
            bos.flush();
            bos.close();
        } finally {
            if (in != null) {
                in.close();
            }
        }
        android.util.Log.d(this.getClass().getSimpleName(),"json rpc request via http returned string "+bos.toString());
        return bos.toString();
    }
}

class UpdateMovie extends Thread  {

    JSONObject jo;
    MainActivity parent;
    UpdateMovie (MainActivity parent, JSONObject jo){
        this.jo = jo;
        this.parent = parent;
    }
    public void run (){
      //  parent.myListAdapter.model.clear();
        LinkedHashMap<String, List<String>> model = new LinkedHashMap<>();

        try {
            for(int i =0; i<jo.length();i++) {
                String title = jo.names().getString(i);
                String values = jo.getString((jo.names().getString(i)));
                JSONArray ja = new JSONArray(values);
                List<String> list = new ArrayList<>(ja.length());
                for(int j=0;j<ja.length();j++){
                    list.add(ja.getString(j));
                }
                if(!model.containsKey(title))
                    model.put(title,list);
            }
            parent.myListAdapter.model = model;
            parent.myListAdapter.notifyDataSetChanged();
        } catch (JSONException e) {
            e.printStackTrace();
        }

    }
}


class DisplayDetails extends Thread {

    MovieDescription movieDescription;
    MainActivity parent;

    DisplayDetails (MainActivity parent, MovieDescription movieDescription){
        this.parent = parent;
        this.movieDescription = movieDescription;
    }

    public void run(){
        Intent intent = new Intent(parent, MovieLayout.class);
        intent.putExtra("SelectedMovie", movieDescription);
        parent.startActivityForResult(intent, 1);
    }
}

