package com.ivsa.network;

import android.os.Handler;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.Scanner;

public class Main2Activity extends AppCompatActivity {
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
        setContentView(R.layout.activity_main2);
        eurl = (EditText) findViewById(R.id.eturl);
        text = (TextView) findViewById(R.id.tvweb);

    }

    public void onClick2(View v){
        thread.start();
    }
}
