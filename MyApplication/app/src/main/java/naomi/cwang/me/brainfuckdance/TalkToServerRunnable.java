package naomi.cwang.me.brainfuckdance;

import android.os.AsyncTask;
import android.os.Handler;
import android.util.Log;

import java.io.BufferedWriter;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.net.Socket;
import java.net.UnknownHostException;

/**
 * Created by naomikoo on 16-07-23.
 */
public class TalkToServerRunnable implements Runnable {

    private Socket mSocket;

    @Override
    public void run() {
        try {
            Log.d("Naomi", "setting mSocket");
            mSocket = new Socket("corn-syrup.uwaterloo.ca", 12346);
        } catch (UnknownHostException e) {
            Log.d("Naomi", "is it this");
        } catch (IOException e) {
            Log.d("Naomi", "but why");
            e.printStackTrace();
        }
    }

    public void send(char direction) {
        PrintWriter out = null;
        Log.d("Naomi", "DID I HIT THIS 1");


        if (mSocket != null) {
            Log.d("Naomi", "DID I HIT THIS 2");

            try {
                out = new PrintWriter(new BufferedWriter(new OutputStreamWriter(mSocket.getOutputStream())), true);
            } catch (IOException e) {
                e.printStackTrace();
            }
            if (out != null && !out.checkError()) {
                Log.d("Naomi", "DID I HIT THIS 3");
                out.println(direction);
                out.flush();
            }
        }
    }
}
