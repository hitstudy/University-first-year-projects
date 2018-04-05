package com.example.towifiapp;

import android.os.Message;

import java.io.DataInputStream;
import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import android.os.Handler;

/**
 * Created by 28785 on 2018/4/5.
 */

public class ReceiveMessage implements Runnable {
    private ServerSocket serveSocket;
    private DataInputStream in;
    private byte[] receive;
    private Handler handler;

    public ReceiveMessage(){
    }

    public void setHandler(Handler handler){
        this.handler = handler;
    }

    @Override
    public void run() {
        try {
            serveSocket=new ServerSocket(5000);
            while (true) {
                Socket client = serveSocket.accept();
                in = new DataInputStream(client.getInputStream());
                receive = new byte[20];
                in.read(receive);
                in.close();

                Message message = new Message();
                message.what = 1;
                message.obj = new String(receive);
                this.handler.sendMessage(message);
            }

        } catch (IOException e) {
            e.printStackTrace();
        }try{
            serveSocket.close();
        }catch (IOException e){
            e.printStackTrace();
        }

    }
}
