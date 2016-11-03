package nekotoken.randomchattingclient;

import android.os.Handler;
import android.widget.TextView;
import java.io.DataInputStream;
import java.io.IOException;
import java.net.Socket;

public class DataInputThread extends Thread {

    Socket          socket;
    Handler         mainThreadHandler;
    TextView        yourTextView;
    DataInputStream dataInputStream;

    public DataInputThread( Socket  socket, DataInputStream dataInputStream,
                            Handler mainThreadHandler, TextView yourTextView) {
        this.socket             = socket;
        this.mainThreadHandler  = mainThreadHandler;
        this.yourTextView       = yourTextView;
        this.dataInputStream    = dataInputStream;
    }

    @Override
    public void run() {
        while (true) {
            try {
                final String message = dataInputStream.readUTF().toString();
                mainThreadHandler.postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        yourTextView.append(message + "\n");
                    }
                }, 100);
            } catch (IOException e) {e.printStackTrace();}
        }
    }
}
