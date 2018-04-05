package com.example.towifiapp;

import android.os.AsyncTask;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import java.io.IOException;
import java.io.PrintStream;
import java.net.InetSocketAddress;
import java.net.Socket;
import java.net.SocketAddress;


public class MainActivity extends AppCompatActivity {
    private Button sendMessage;
    private TextView showstate;
    private TextView connectState;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        sendMessage = findViewById(R.id.button);
        showstate = findViewById(R.id.textView);
        connectState=findViewById(R.id.textView2);



    }
    void sendmessage(View view){
        new SendMessage().execute();//开启多线程
    }


    class SendMessage extends AsyncTask<Void,Void,Boolean> {

        private Socket client;
        private static final String url ="192.168.4.1";
        private static final int port=333;
        private String str = "send from Android";
        private PrintStream out=null;
        private boolean isConnect=false;


        @Override//后台任务执行前进行操作
        protected void onPreExecute() {
            super.onPreExecute();
            //Toast.makeText(getApplicationContext(),"前一步正常",Toast.LENGTH_SHORT).show();
            showstate.setText("正在发送");
            connectState.setText("尝试连接");
        }

        @Override//运行结束后会得到doinbackground的返回值，利用返回值进行ui操作
        protected void onPostExecute(Boolean aBoolean) {
            super.onPostExecute(aBoolean);
            //Toast.makeText(getApplicationContext(),"结尾开始执行",Toast.LENGTH_SHORT).show();

            if(isConnect){
                connectState.setText("单片机连接成功");
            }else{
                connectState.setText("单片机连接失败");
            }

            if(aBoolean){
                showstate.setText("发送状态：发送成功");
            }else{
                showstate.setText("发送状态：发送失败");
            }

        }


        @Override
        protected Boolean doInBackground(Void... voids) {
            try {
                //建立套接字连接，设置超时
                client = new Socket();
                SocketAddress address = new InetSocketAddress(url,port);
                client.connect(address,1000);//获取连接状态
                isConnect=client.isConnected();

                //获取client连接的输出流，用于发送数据
                out = new PrintStream(client.getOutputStream());
                out.print(str);
                out.flush();
                client.close();
                out.close();



            } catch (IOException e) {
                return false;
            }

            return true;
        }
    }


}

