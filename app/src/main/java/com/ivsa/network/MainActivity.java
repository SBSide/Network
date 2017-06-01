package com.ivsa.network;

import android.content.Intent;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;
import java.util.Scanner;

public class MainActivity extends AppCompatActivity {
    String SERVER_IP = "172.17.64.137";
    int SERVER_PORT = 200;
    String msg = "";
    EditText e1;
    Handler mHandler = new Handler();
    Thread myThread = new Thread() {
        @Override
        public void run() {
            super.run();
            try {
                Socket aSocket = new Socket(SERVER_IP, SERVER_PORT);

                ObjectOutputStream outstream = new ObjectOutputStream(aSocket.getOutputStream());
                msg = "Client >>" + e1.getText().toString();
                outstream.writeObject(msg);
                outstream.flush();

                ObjectInputStream instream = new ObjectInputStream(aSocket.getInputStream());
                final String obj = (String) instream.readObject();
                Log.d("Before,",obj);
                mHandler.post(new Runnable() {
                    @Override
                    public void run() {
                        Toast.makeText(getApplicationContext(),"Server>> "+obj,
                        Toast.LENGTH_SHORT).show();
                        Log.d("After,",obj);
                    }
                });
                aSocket.close();
            } catch (IOException e) {
                e.printStackTrace();
            } catch (ClassNotFoundException e) {
                e.printStackTrace();
            }
        }
    };
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        e1 = (EditText) findViewById(R.id.etmsg);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        menu.add(0,1,1,"다음 Activity로");
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        Intent intent = new Intent(MainActivity.this, HTTPRead.class);
        startActivity(intent);
        finish();
        return true;
    }

    public void onClick(View v){
        myThread.start();
    }
}
