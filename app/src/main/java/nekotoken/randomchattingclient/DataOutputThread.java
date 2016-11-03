package nekotoken.randomchattingclient;

import java.io.DataOutputStream;
import java.net.Socket;

public class DataOutputThread extends Thread {

    Socket              socket;
    DataOutputStream    dataOutputStream;

    public DataOutputThread(Socket socket, DataOutputStream dataOutputStream ) {
        this.socket             = socket;
        this.dataOutputStream   = dataOutputStream;
    }

    @Override
    public void run() {

    }
}
