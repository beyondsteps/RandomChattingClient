package nekotoken.randomchattingclient;

import android.os.Bundle;
import android.os.Handler;
import android.os.StrictMode;
import android.widget.Toast;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.ScrollView;
import android.view.View;
import android.content.Context;
import android.support.v7.app.AppCompatActivity;
import java.net.Socket;
import java.io.IOException;
import java.io.DataInputStream;
import java.io.DataOutputStream;

public class ChattingRoom extends AppCompatActivity {

    private final String     IP_ADDRESS  = "192.168.0.19";   // set here
    private final int        SERVER_PORT = 7777;             // set here too

    private Button                  sendButton;
    private Button                  closeButton;
    private Button                  newSearchingButton;
    private ScrollView              scrollView;
    private TextView                myTextView;
    private TextView                yourTextView;
    private EditText                editText;
    private Context                 mContext;

    private Socket                  socket;
    private DataInputThread         dataInputThread;
    private DataOutputThread        dataOutputThread;
    private DataInputStream         dataInputStream;
    private DataOutputStream        dataOutputStream;
    private Handler                 handler;
    private StrictMode.ThreadPolicy threadPolicy;

    @Override
    protected void onPause() {
        super.onPause();
        networkClosing();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chatting_room);

        sendButton          = (Button)      findViewById(R.id.sendButton);
        closeButton         = (Button)      findViewById(R.id.closeButton);
        newSearchingButton  = (Button)      findViewById(R.id.newSearchingButton);
        scrollView          = (ScrollView)  findViewById(R.id.scrollView);
        myTextView          = (TextView)    findViewById(R.id.textView_myTextView);
        yourTextView        = (TextView)    findViewById(R.id.textView_yourTextView);
        editText            = (EditText)    findViewById(R.id.editText);
        handler             = new Handler();
        mContext            = getApplicationContext();
        threadPolicy        = new StrictMode.ThreadPolicy.Builder().permitAll().build();

        StrictMode.setThreadPolicy(threadPolicy);
        this.networkAccessing();


        sendButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                try {
                    String message = editText.getText().toString();
                    if ( message.equals("") ) {
                        Toast.makeText(ChattingRoom.this, "메세지를 입력하세요.", Toast.LENGTH_SHORT).show();
                    }
                    else {
                        if (socket != null) {
                            dataOutputStream.writeUTF(message);
                            dataOutputStream.flush();
                            myTextView.append(message + "\n");
                        } else {
                            Toast.makeText(ChattingRoom.this, "연결 안됨", Toast.LENGTH_SHORT).show();
                        }
                        editText.setText(null);
                    }
                } catch (IOException e) {e.printStackTrace();}
            }
        });


        newSearchingButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                networkClosing();
                networkAccessing();
                myTextView.setText("나!\n\n");
                yourTextView.setText("상대방!\n\n");
            }
        });


        closeButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                activityFinishing();
            }
        });
    }

    private void networkAccessing() {
        try {
            socket            = new Socket( IP_ADDRESS, SERVER_PORT );
            dataInputStream   = new DataInputStream(socket.getInputStream());
            dataOutputStream  = new DataOutputStream(socket.getOutputStream());
            dataOutputThread  = new DataOutputThread(socket, dataOutputStream);
            dataInputThread   = new DataInputThread(socket, dataInputStream, handler, yourTextView);
            dataInputThread.start();
            dataOutputThread.start();
        }catch(Exception e) {
            e.printStackTrace();
            Toast.makeText(ChattingRoom.this, "서버에 접속할 수 없습니다.", Toast
                    .LENGTH_SHORT).show();
            finish();
        }
    }

    private void networkClosing() {
        try {
            if (socket != null) {
                dataInputThread.interrupt();
                dataOutputThread.interrupt();
                dataInputStream.close();
                dataOutputStream.close();
                socket.close();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void activityFinishing() {
        networkClosing();
        Toast.makeText(ChattingRoom.this, "연결 종료", Toast.LENGTH_SHORT).show();
        finish();
    }

}
