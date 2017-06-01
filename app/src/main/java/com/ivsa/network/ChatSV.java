package com.ivsa.network;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.net.ServerSocket;
import java.net.Socket;

/**
 * Created by iveci on 2017-06-01.
 */

public class ChatSV {
    public static void main(String[] args) {
        int SV_PORT = 200;
        try {
            ServerSocket sv = new ServerSocket(SV_PORT);
            System.out.println("[SV] Server launched. PORT: " + SV_PORT);
            while(true) {
                Socket asocket = sv.accept();
                System.out.println("[SV] Client connect: " + asocket.getLocalAddress());
                ObjectInputStream instream = new ObjectInputStream(asocket.getInputStream());
                Object object = instream.readObject();
                System.out.println("[SV] Received: " + object);


            }
        } catch (IOException e) {
            e.printStackTrace();
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        }
    }
}
