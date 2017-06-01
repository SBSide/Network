package com.ivsa.network;

import android.content.Intent;
import android.os.Handler;
import android.provider.DocumentsContract;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import org.w3c.dom.DOMException;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Scanner;

import javax.net.ssl.HttpsURLConnection;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;

public class RSSRead extends AppCompatActivity {
    ListView listView;
    ArrayAdapter adapter;
    ArrayList<String> data = new ArrayList<>();
    Handler handler = new Handler();
    Thread thread = new Thread(){
        @Override
        public void run() {
            try {
                URL url = new URL(
                        "https://news.google.com/news?cf=all&hl=ko&pz=1&ned=kr&topic=m&output=rss");
                HttpsURLConnection urlConnection =
                        (HttpsURLConnection) url.openConnection();
                if(urlConnection.getResponseCode() == HttpsURLConnection.HTTP_OK){
                    int itemCount = readData(urlConnection.getInputStream());
                    handler.post(new Runnable() {
                        @Override
                        public void run() {
                            adapter.notifyDataSetChanged();
                        }
                    });
                    urlConnection.disconnect();
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        int readData(InputStream is){
            DocumentBuilderFactory builderFactory =
                    DocumentBuilderFactory.newInstance();
            try {
                DocumentBuilder builder = builderFactory.newDocumentBuilder();
                Document document = builder.parse(is);
                int datacount = parseDocument(document);
                return datacount;
            } catch (ParserConfigurationException e) {
                e.printStackTrace();
            } catch (SAXException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            }
            return 0;
        }

        private int parseDocument(Document doc){ //item단위로 RSS를 분할한다.
            Element docEle = doc.getDocumentElement();
            NodeList nodeList = docEle.getElementsByTagName("item");
            int count = 0;
            if((nodeList !=  null) && (nodeList.getLength() > 0)) {
                for(int i = 0; i < nodeList.getLength(); i++) {
                    String newsItem = getTagData(nodeList, i);
                    if(newsItem != null) {
                        data.add(newsItem);
                        count++;
                    }
                }
            }
            return count;
        }

        private String getTagData(NodeList nodelist, int index) {
            /*item단위로 분할한 RSS를
            * 제목(title), 날짜(pubDate)로 분리하고
            * String 하나로 다시 합친다.
            * */
            String newsItem = null;
            try {
                Element entry = (Element) nodelist.item(index);
                Element title = (Element) entry.getElementsByTagName("title").item(0);
                Element pubDate = (Element) entry.getElementsByTagName("pubDate").item(0);
                String titleValue = null;
                if (title != null) {//제목부분
                    Node firstChild = title.getFirstChild();
                    if (firstChild != null) titleValue = firstChild.getNodeValue();
                }
                String pubDateValue = null;
                if (pubDate != null) {//날짜부분
                    Node ff = pubDate.getFirstChild();
                    if (ff != null) pubDateValue = ff.getNodeValue();
                }
                //날짜 포맷을 설정하는 부분
                SimpleDateFormat simpleDateFormat = new SimpleDateFormat("YYYY-MM-dd");
                Date date = new Date();
                //String으로 합치는 부분
                newsItem = titleValue + "-" + simpleDateFormat.format(date.parse(pubDateValue));
            } catch (DOMException e) {
                e.printStackTrace();
            } return newsItem;
        }
    };
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_rssread);
        listView = (ListView) findViewById(R.id.list);
        adapter = new ArrayAdapter(getApplicationContext(),android.R.layout.simple_list_item_1,data);
        listView.setAdapter(adapter);
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
            Intent intent = new Intent(RSSRead.this, HTTPRead.class);
            startActivity(intent);
            finish();
            return true;
        }
        else {
            Intent intent = new Intent(RSSRead.this, Login.class);
            startActivity(intent);
            finish();
            return true;
        }
    }

    public void onClick(View v){
        thread.start();
    }
}
