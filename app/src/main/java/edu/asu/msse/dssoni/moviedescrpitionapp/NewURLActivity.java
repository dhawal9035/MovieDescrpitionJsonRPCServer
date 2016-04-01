package edu.asu.msse.dssoni.moviedescrpitionapp;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;

public class NewURLActivity extends AppCompatActivity {

    String url="";
    private Intent intent;
    EditText urlText;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_new_url);
        urlText = (EditText) findViewById(R.id.editText6);
    }

    public void buttonClicked(View v){
        intent = new Intent(this,MainActivity.class);
        url = urlText.getText().toString();
        intent.putExtra("url",url);
        startActivity(intent);
    }
}
