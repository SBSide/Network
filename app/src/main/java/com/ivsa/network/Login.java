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
import android.widget.Toast;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLEncoder;
import java.util.Scanner;

public class Login extends AppCompatActivity {
    EditText user, pw;
    TextView msg;
    Handler handler = new Handler();
    Thread thread = new Thread() {
        @Override
        public void run() {
            try {
                final String  userid   = user.getText().toString(),
                        password = pw.getText().toString();
                URL url = new URL(
                        "http://jerry1004.dothome.co.kr/info/login.php");
                HttpURLConnection httpURLConnection =
                        (HttpURLConnection) url.openConnection();
                httpURLConnection.setRequestMethod("POST");
                httpURLConnection.setDoOutput(true);
                String postData = "userid=" + URLEncoder.encode(userid,"utf-8")
                        + "&password=" + URLEncoder.encode(password,"utf-8");
                OutputStream outputStream = httpURLConnection.getOutputStream();
                outputStream.write(postData.getBytes("UTF-8"));
                outputStream.flush();
                outputStream.close();
                InputStream inputStream;
                if (httpURLConnection.getResponseCode() == HttpURLConnection.HTTP_OK)
                    inputStream = httpURLConnection.getInputStream();
                else
                    inputStream = httpURLConnection.getErrorStream();
                final String result = loginResult(inputStream);
                handler.post(new Runnable() {
                    @Override
                    public void run() {
                        if(result.contains("FAIL"))
                            msg.setText("로그인하지 못했습니다.");
                        else
                            msg.setText(userid+"님 로그인 성공.");
                    }
                });
            } catch (MalformedURLException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        private String loginResult(InputStream inputStream) {
            String dt = "";
            Scanner s = new Scanner(inputStream);
            while(s.hasNext()) dt += s.nextLine() + "\n";
            s.close();
            return dt;
        }
    };
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        user = (EditText) findViewById(R.id.etuser);
        pw   = (EditText) findViewById(R.id.etpw);
        msg  = (TextView) findViewById(R.id.tmsg);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        menu.add(0,1,1,"이전 Activity로");
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        Intent intent = new Intent(Login.this, RSSRead.class);
        startActivity(intent);
        finish();
        return true;
    }

    public void onClick(View v){
        if(user.getText().toString().equals("") || pw.getText().toString().equals("")) {
            Toast.makeText(getApplicationContext(),"아이디와 비밀번호를 입력하세요.",Toast.LENGTH_SHORT).show();
        }
        else thread.start();
    }
}
