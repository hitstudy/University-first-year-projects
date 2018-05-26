package com.example.towifiapp;

import android.annotation.SuppressLint;
import android.os.AsyncTask;
import android.os.Handler;
import android.os.Message;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import java.io.IOException;
import java.io.PrintStream;
import java.lang.ref.WeakReference;
import java.net.InetSocketAddress;
import java.net.Socket;
import java.net.SocketAddress;
import java.util.Scanner;


public class MainActivity extends AppCompatActivity {
    private Button sendMessage;
    private TextView showstate;
    private TextView connectState;
    private TextView serveState;
    private EditText ipAddress;
    private EditText portAddress;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        init_view();

        //这部分是重写的点击事件，原来是通过在布局界面绑定方法的，今天突然报错说找不到方法，也不知道为什么，不过还好重写一遍也不麻烦
        sendMessage.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View view) {
                new SendMessage().execute();
            }
        });

        //开启服务器
//        ReceiveMessage serve = new ReceiveMessage();
//        serve.setHandler(mhandler);
//        new Thread(serve).start();


    }


    void init_view() {
        sendMessage = findViewById(R.id.button);
        showstate = findViewById(R.id.textView);
        connectState = findViewById(R.id.textView2);
        serveState = findViewById(R.id.textView3);
        ipAddress = findViewById(R.id.ipAddress);
        portAddress = findViewById(R.id.portAddress);
    }


//这个函数原来是用来在属性里直接绑定按键方法的，由于不知道为什么报错，已经改为使用OnclickLinsier
//    void sendmessage() {
//        new SendMessage().execute();//开启多线程
//    }


    //一个异步处理的内部类
    //4.8日注释：我好像需要对这个类也进行弱引用处理，书上好像没说需要这样啊
    //@SuppressLint("StaticFieldLeak")//这个标注是忽略指定的警告，用来暂时看一下,问题其实还在
     class SendMessage extends AsyncTask<Void, Void, Boolean> {


        private Socket client;
        private String url ;
        private int port;
        private String str = "k";
        private PrintStream out = null;
        private boolean isConnect = false;
        private boolean inputcorrect = true;
        private boolean firstClick=true;

        //后台任务执行前进行操作
        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            //Toast.makeText(getApplicationContext(),"前一步正常",Toast.LENGTH_SHORT).show();
            showstate.setText("正在发送");
            connectState.setText("尝试连接");
        }

        //运行结束后会得到doinbackground的返回值，利用返回值进行ui操作
        @Override
        protected void onPostExecute(Boolean aBoolean) {
            super.onPostExecute(aBoolean);
            //Toast.makeText(getApplicationContext(),"结尾开始执行",Toast.LENGTH_SHORT).show();
            if(inputcorrect) {

                if (isConnect) {
                    connectState.setText("连接成功");
                } else {
                    connectState.setText("连接失败");
                }

                if (aBoolean) {
                    showstate.setText("发送成功");
                } else {
                    showstate.setText("发送失败");
                }
            }else{
                connectState.setText("未连接");
                showstate.setText("未发送");
                Toast.makeText(getApplicationContext(),"请输入正确的ip和端口",Toast.LENGTH_SHORT).show();
            }

        }

        //后台操作接收数据
        @Override
        protected Boolean doInBackground(Void... voids) {
            try {

                if(!("".equals(ipAddress.getText().toString()))&&!("".equals(portAddress.getText().toString()))) {
                    url = ipAddress.getText().toString();
                    port = Integer.parseInt(portAddress.getText().toString());

                    //建立套接字连接，设置超时
                    client = new Socket();
                    SocketAddress address = new InetSocketAddress(url, port);
                    client.connect(address, 1000);//获取连接状态
                    isConnect = client.isConnected();

                    //这边在第一次点击后开启接收消息线程
                    if (firstClick){
                        ReceiveMessage receiveMessage = new ReceiveMessage(client);
                        receiveMessage.setHandler(mhandler);
                        new Thread(receiveMessage).start();
                        firstClick = false;
                    }
                    //获取client连接的输出流，用于发送数据
                    out = new PrintStream(client.getOutputStream());
                    out.print(str);
                    out.flush();
                    client.close();
                    out.close();
                    return true;
                }else{
                    //Toast.makeText(getApplicationContext(), "请输入正确的ip和端口", Toast.LENGTH_SHORT).show();
                    inputcorrect = false;
                    return false;
                }

            } catch (IOException e) {
                return false;
            }


        }
    }


    //这种写法在内部类声明周期和actixity生命周期不一致时可能会导致内存泄露，出现了warning,应改用为以下写法.
//    Handler handler = new Handler(){
//        @Override
//        public void handleMessage(Message msg) {
//            switch(msg.what){
//                case 1:
//                    serveState.setText( "服务器传回"+msg.obj);
//            }
//
//        }
//    };



    //用弱引用防止内存泄露
    //定义一个Handler 的子类
    private static class Myhandler extends Handler {
        //持有对MainAcitvity类的弱引用
        private WeakReference<MainActivity> mactivity;

        //构造函数要求一个MainActivity类型的activity对象
        private Myhandler(MainActivity acitvity) {
            mactivity = new WeakReference<MainActivity>(acitvity);
        }

        @Override
        public void handleMessage(Message msg) {
            //通过引用得到传入的activity
            MainActivity activity = mactivity.get();
            //重写Handler父类的方法，操作得到的activity中的view对象
            switch (msg.what) {
                case 1:
                    activity.serveState.setText(""+msg.obj);
            }
        }
    }
    //新建一个弱引用的handler对象
    private final Myhandler mhandler=new Myhandler(this);

}

