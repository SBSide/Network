package com.ivsa.network;

import android.content.Intent;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.Scanner;

public class HTTPRead extends AppCompatActivity {
    String urlstr = "";
    Handler handler = new Handler();
    EditText eurl;
    TextView text;
    Thread thread = new Thread(){
        @Override
        public void run() {
            try {
                urlstr = eurl.getText().toString();
                URL url = new URL(urlstr);
                HttpURLConnection urlConnection =
                        (HttpURLConnection) url.openConnection();
                if(urlConnection.getResponseCode()==HttpURLConnection.HTTP_OK){
                    final String data = readData(urlConnection.getInputStream());
                    handler.post(new Runnable() {
                        @Override
                        public void run() {
                            text.setText(data);
                        }
                    });
                    urlConnection.disconnect();
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        String readData(InputStream is){
            String dt = "";
            Scanner s = new Scanner(is);
            while(s.hasNext()) dt += s.nextLine() + "\n";
            s.close();
            return dt;
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_httpread);
        eurl = (EditText) findViewById(R.id.eturl);
        text = (TextView) findViewById(R.id.tvweb);

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        menu.add(0,1,1,"이전 Activity로");
        menu.add(0,2,2,"다음 Activity로");
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == 1) {
            Intent intent = new Intent(HTTPRead.this, MainActivity.class);
            startActivity(intent);
            finish();
            return true;
        }
        else {
            Intent intent = new Intent(HTTPRead.this, RSSRead.class);
            startActivity(intent);
            finish();
            return true;
        }
    }

    public void onClick2(View v){
        thread.start();
    }
}
