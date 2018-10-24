package com.appdev.a503_02.a1022htmlparsing;

import android.os.Handler;
import android.os.Message;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URI;
import java.net.URL;
import java.net.URLEncoder;
import java.util.ArrayList;

public class ApiUse extends AppCompatActivity {

    ArrayList<String> list;
    ArrayAdapter<String> adapter;
    ListView listView;
    EditText editText;


    class ThreadEx extends Thread{
        //다운로드 받는 문자열을 저장할 변수
        String json = "";

        @Override
        public void run(){
            try{
                editText = (EditText)findViewById(R.id.bookname);
                String sam = URLEncoder.encode(editText.getText().toString(),"UTF-8");

                //다운로드 받을 주소 생성
                //URL url = new URL("https://apis.daum.net/search/book?&q="+sam+"&output=json");
                URL url = new URL("https://apis.daum.net/search/book?&output=json&q="+sam);


                //URL 연결 객체 생성
                HttpURLConnection con = (HttpURLConnection)url.openConnection();

                //kakao API 인증 설정
                con.setRequestProperty("Authorization","KakaoAK b16d7284a45e8beff8b1dccb4e8a272f");
                con.setConnectTimeout(20000);

                BufferedReader br = new BufferedReader(new InputStreamReader(con.getInputStream()));

                StringBuilder sb = new StringBuilder();

                while(true){
                    String line = br.readLine();
                    if(line == null) break;
                    sb.append(line+"\n");
                }

                json = sb.toString();
                Log.e("json",json);

                br.close();
                con.disconnect();








            }catch (Exception e){
                Log.e("다운로드 실패", e.getMessage());
            }


            //json 파싱
            try{
                //문자열을 객체로 생성
                JSONObject book = new JSONObject(json);

                //channel 키의 데이터를 JSONObject 타입으로 가져오기
                JSONObject channel = book.getJSONObject("channel");

                //Log.e("channel", channel.toString());

                JSONArray items = channel.getJSONArray("item");

                list.clear();

                //배열 데이터를 순회
                for(int i=0; i<items.length(); i++){
                    //각 인덱스에 해당하는 항목을 JSONObject로 가져오기
                    JSONObject item = items.getJSONObject(i);

                    //List에 제목과 세일 가격을 가져와서 추가
                    list.add(item.getString("title")+":"+item.getString("sale_price"));
                }

                //핸들러를 호출해서 리스트 뷰를 다시 출력하도록 한다.
                handler.sendEmptyMessage(0);

            }catch (Exception e){

            }

        };



    }


    Handler handler = new Handler(){
        @Override
        public void handleMessage(Message msg){
            adapter.notifyDataSetChanged();
        }
    };



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_api_use);


        list=new ArrayList<>();
        adapter = new ArrayAdapter<>(this, android.R.layout.simple_list_item_1, list);
        listView = (ListView)findViewById(R.id.listView);
        listView.setAdapter(adapter);


        Button json = (Button)findViewById(R.id.json);
        json.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //스레드 생성 및 시작
                ThreadEx th = new ThreadEx();
                th.start();

            }
        });

    }
}
