package com.example.towifiapp;

import android.os.Message;

import java.io.DataInputStream;
import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.Scanner;

import android.os.Handler;

/**
 * 通过新建服务器接收消息，只适用于局域网
 * Created by 28785 on 2018/4/5.
 * 改动为，从新建一个服务器接收消息变为获得原来的tcp连接从中读取消息，这样方便单片机方面编程以及连接到外网
 * Created by 287785 on 2018/5/24
 */

public class ReceiveMessage implements Runnable {
//    private ServerSocket serveSocket;
    private DataInputStream in;
    private byte[] receive;
//    private String response;
    private Handler handler;
    private Socket client;
    public ReceiveMessage(Socket client){
        this.client = client;
    }

    public void setHandler(Handler handler){
        this.handler = handler;
    }

    @Override
    public void run() {
        try {
            //在5000端口设置监听服务
//            serveSocket=new ServerSocket(5000);
//            Socket client=null;
            //监听端口消息
            while (true) {
//                client = serveSocket.accept();
                in = new DataInputStream(client.getInputStream());
                receive = new byte[20];
                in.read(receive);
                //使用Scanner类从流中读取消息
//                Scanner in = new Scanner(client.getInputStream());
//                response = in.nextLine();
                in.close();

                //发送监听到的消息给主线程
                Message message = new Message();
                message.what = 1;
                message.obj = new String(receive);
//                message.obj = response;
                this.handler.sendMessage(message);
            }

        } catch (IOException e) {
            e.printStackTrace();
        }
//        try{
//            if (serveSocket!=null)
//            serveSocket.close();
//        }catch (IOException e){
//            e.printStackTrace();
//        }


    }
}
