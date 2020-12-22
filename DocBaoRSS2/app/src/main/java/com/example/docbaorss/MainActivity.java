package com.example.docbaorss;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity {
    ListView listView;
    List<String> arrayList= new ArrayList<>();
    List<String> arrayLink= new ArrayList<>();
    ArrayAdapter arrayAdapter;
    Intent itent;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        listView=findViewById(R.id.lvTinTuc);
        AsyncTask<String,Void,String> content= new DongBoDuLieu().execute("https://vnexpress.net/rss/tin-xem-nhieu.rss");
        arrayAdapter= new ArrayAdapter(this, android.R.layout.simple_list_item_1,arrayList);
        listView.setAdapter(arrayAdapter);
        itent=new Intent(MainActivity.this,Detail.class);
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                String link=arrayLink.get(position);
                itent.putExtra("linkURL",link);
                startActivity(itent);
            }
        });
    }
    public  class DongBoDuLieu extends AsyncTask<String,Void,String>{
        //doc tren dien thoai
        @Override
        protected void onPostExecute(String s) {
            super.onPostExecute(s);
            XMLParser xmlParser= new XMLParser();
            try {
                Document document=xmlParser.getDocument(s);
                NodeList nodeList= document.getElementsByTagName("item");
                String title="";
                for (int i=0;i<nodeList.getLength();i++){
                    Element element= (Element) nodeList.item(i);
                    title=xmlParser.getValue(element,"title")+"\n";
                    //add list
                    arrayList.add(title);
                    arrayLink.add(xmlParser.getValue(element,"link"));
                }
                arrayAdapter.notifyDataSetChanged();
            } catch (IOException e) {
                e.printStackTrace();
            } catch (SAXException e) {
                e.printStackTrace();
            }

        }
        // doc tren server
        @Override
        protected String doInBackground(String... strings) {
            StringBuilder content= new StringBuilder();
            try {
                URL url= new URL(strings[0]); //lấy đường link
                InputStreamReader reader= new InputStreamReader(url.openConnection().getInputStream());
                BufferedReader bufferedReader= new BufferedReader(reader);
                String line="";
                while ((line=bufferedReader.readLine())!=null){
                    content.append(line);
                }
                bufferedReader.close();
            } catch (MalformedURLException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            }
            return content.toString();
        }
    }
}