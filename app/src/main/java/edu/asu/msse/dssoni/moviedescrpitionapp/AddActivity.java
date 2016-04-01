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
 *  Created by Dhawal Soni on 2/11/2016.
 *
 *  @author Dhawal Soni mailto:dhawal.soni@asu.edu
 *  @version February 11, 2016
 */

package edu.asu.msse.dssoni.moviedescrpitionapp;

import android.app.Activity;
import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.Spinner;

import org.json.JSONException;
import org.json.JSONObject;

public class AddActivity extends AppCompatActivity {

    String title="";
    String year = "";
    String rated ="";
    String released = "";
    String runtime = "";
    String genre = "";
    String actors = "";
    String plot = "";
    Spinner spinner;
    ArrayAdapter<CharSequence> arrayAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add);
        spinner = (Spinner) findViewById(R.id.spinner);
        arrayAdapter = ArrayAdapter.createFromResource(this,R.array.genre,android.R.layout.simple_spinner_item);
        arrayAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinner.setAdapter(arrayAdapter);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu){
        android.util.Log.d(this.getClass().getSimpleName(), "called onCreateOptionsMenu()");
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.menu_main, menu);
        return super.onCreateOptionsMenu(menu);
    }

    /*
     * Implement onOptionsItemSelected(MenuItem item){} to handle clicks of buttons that are
     * in the action bar.
     */
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        android.util.Log.d(this.getClass().getSimpleName(), "called onOptionsItemSelected()");
        Intent i = new Intent(this, AddActivity.class);
        switch (item.getItemId()) {
            case R.id.action_add:
                i.putExtra("message", "This is a search dialog");
                startActivityForResult(i,1);
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }


    public void addNewItem(View v) throws JSONException{
        EditText titleText = (EditText) findViewById(R.id.editText );
        EditText yearText = (EditText) findViewById(R.id.editText2 );
        EditText ratingText = (EditText) findViewById(R.id.editText3 );
        EditText releasedText = (EditText) findViewById(R.id.editText4 );
        EditText actorsText = (EditText) findViewById(R.id.editText5 );
        EditText runText = (EditText) findViewById(R.id.editText7 );
        EditText plotText = (EditText) findViewById(R.id.editText8 );
        Spinner spinner = (Spinner) findViewById(R.id.spinner);

        title = titleText.getText().toString();
        year = yearText.getText().toString();
        rated = ratingText.getText().toString();
        released = releasedText.getText().toString();
        actors = actorsText.getText().toString();
        runtime = runText.getText().toString();
        plot = plotText.getText().toString();
        genre = spinner.getSelectedItem().toString();

        JSONObject jo = new JSONObject();
        jo.put("Title",title);
        jo.put("Year",year);
        jo.put("Rated",rated);
        jo.put("Released",released);
        jo.put("Runtime",runtime);
        jo.put("Genre",genre);
        jo.put("Actors",actors);
        jo.put("Plot",plot);

        String jsonString = jo.toString();
        Intent intent = new Intent();
        intent.putExtra("newJsonString",jsonString);
        setResult(2, intent);
        finish();
    }
}
