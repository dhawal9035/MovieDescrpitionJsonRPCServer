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
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.widget.ExpandableListView;

import java.net.URL;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;

public class MainActivity extends AppCompatActivity {

    public String selectedStuff;
    public ExpandableListView elview;
    public ExpandableMovieListAdapter myListAdapter;
    public LinkedHashMap<String,List<String>> map;
    static final int DELETE_CONTACT_REQUEST = 1;
    static final int ADD_CONTACT_REQUEST = 2;
    Handler handler;
    String url = "";
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_main);
        map = new LinkedHashMap<String,List<String>>();
        Intent intent = getIntent();
        url = intent.getStringExtra("url");
        if(url.isEmpty())
            url=this.getString(R.string.urlString);

        try {
            handler = new Handler();
            JsonRPCClientViaThread names = new JsonRPCClientViaThread(new URL(url),
                    handler, this, "getMovieList", "[  ]");
            names.start();
        } catch (Exception ex) {
            android.util.Log.w(this.getClass().getSimpleName(), "Exception constructing URL" +
                    " " + url + " message " + ex.getMessage());
        }
        elview = (ExpandableListView) findViewById(R.id.lvExp);
        myListAdapter = new ExpandableMovieListAdapter(this);
        elview.setAdapter(myListAdapter);

        myListAdapter.notifyDataSetChanged();


    }

    public void setSelectedStuff(String selectedStuff) {
        this.selectedStuff = selectedStuff;
        try {
            handler = new Handler();
            JsonRPCClientViaThread names = new JsonRPCClientViaThread(new URL(url),
                    handler, this, "get","[ \""+selectedStuff+"\" ]");
            names.start();
        }catch(Exception ex){
            android.util.Log.w(this.getClass().getSimpleName(),"Exception constructing URL"+
                    " "+url+" message "+ex.getMessage());
        }
    }

    public boolean onCreateOptionsMenu(Menu menu) {
        android.util.Log.d(this.getClass().getSimpleName(), "called onCreateOptionsMenu()");
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.main_activity_actions, menu);
        return super.onCreateOptionsMenu(menu);
    }

    /*
     * Implement onOptionsItemSelected(MenuItem item){} to handle clicks of buttons that are
     * in the action bar.
     */
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        android.util.Log.d(this.getClass().getSimpleName(), "called onOptionsItemSelected()");
        Intent intent = new Intent(this, AddActivity.class);
        switch (item.getItemId()) {
            case R.id.action_add:
                startActivityForResult(intent,2);
                return true;
            case R.id.action_refresh:
                finish();
                startActivity(getIntent());
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data){
        super.onActivityResult(requestCode, resultCode, data);
        if(requestCode == DELETE_CONTACT_REQUEST){
            if (resultCode == DELETE_CONTACT_REQUEST){
                String removeTitle = (String) data.getSerializableExtra("removeMovieJSON");
                myListAdapter.notifyDataSetChanged();

                try {
                    handler = new Handler();
                    JsonRPCClientViaThread names = new JsonRPCClientViaThread(new URL(url),
                            handler, this, "remove", "[ \"" + removeTitle + "\" ]");
                    names.start();
                } catch (Exception ex) {
                    android.util.Log.w(this.getClass().getSimpleName(), "Exception constructing URL" +
                            " " + url + " message " + ex.getMessage());
                }
                this.recreate();
            }
        }

        if(requestCode == ADD_CONTACT_REQUEST){
            if (resultCode == ADD_CONTACT_REQUEST){
                String jsonString = (String) data.getSerializableExtra("newJsonString");
                try {
                    handler = new Handler();
                    JsonRPCClientViaThread names = new JsonRPCClientViaThread(new URL(url),
                            handler, this, "add", "["+jsonString+"]");
                    names.start();
                } catch (Exception ex) {
                    android.util.Log.w(this.getClass().getSimpleName(), "Exception constructing URL" +
                            " " + url + " message " + ex.getMessage());
                }
                myListAdapter.notifyDataSetChanged();
                this.recreate();
            }
        }


    }

}


