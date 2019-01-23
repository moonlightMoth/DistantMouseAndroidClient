package com.crutcher.inpctrlclient;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.Switch;

import static java.lang.StrictMath.abs;

public class ControlPanelActivity extends AppCompatActivity {

    SocketProcessor socketProcessor;
    boolean isConnected = false;

    private int x;
    private int y;
    private int olderX;
    private int olderY;
    private boolean isDragOn = false;

    private long lastTouchDown;
    private static int CLICK_ACTION_THRESHOLD = 100;

    private Button setIP;
    private Button rec;
    private Button clickButton;
    private Button scDw;
    private Button scUp;
    private Button disc;
    private EditText etSetIP;
    private View mvPane;
    private Switch dragSwitch;

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_control_panel);

        setIP = findViewById(R.id.bSetIP);
        rec = findViewById(R.id.reconnect);
        disc = findViewById(R.id.disc);
        scUp = findViewById(R.id.scrollUp);
        scDw = findViewById(R.id.scrollDown);
        clickButton = findViewById(R.id.clickButton);
        etSetIP = findViewById(R.id.etSetIP);
        mvPane = findViewById(R.id.mvPane);
        dragSwitch = findViewById(R.id.dragSwitch);

        connectListeners();
    }

    private void connectListeners()
    {
        disc.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (isConnected)
                    disconnect();
            }
        });

        setIP.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                GlobalVars.setIpAddr(etSetIP.getText().toString());
            }
        });

        rec.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(!isConnected)
                    connect();
            }
        });
    }

    private void connectSocketRelatedListeners()
    {
        scUp.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                socketProcessor.scrollUp();
            }
        });

        scDw.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                socketProcessor.scrollDw();
            }
        });

        clickButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                socketProcessor.clickRmb();
            }
        });


        dragSwitch.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener()
        {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked)
            {
                isDragOn = isChecked;

                if (isChecked)
                    socketProcessor.dragOn();
                else
                    socketProcessor.dragOff();
            }
        });

        mvPane.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View view, MotionEvent motionEvent) {

                int action = motionEvent.getAction();
                x = (int) motionEvent.getX();
                y = (int) motionEvent.getY();

                if (action == MotionEvent.ACTION_DOWN)
                {
                    olderX = x;
                    olderY = y;
                    lastTouchDown = System.currentTimeMillis();

                    return true;
                }

                if (action == MotionEvent.ACTION_UP && isDragOn)
                {
                    if (System.currentTimeMillis() - lastTouchDown < CLICK_ACTION_THRESHOLD)
                    {
                        socketProcessor.clickLmb();
                    }

                    return false;
                }

                if (action == MotionEvent.ACTION_MOVE)
                {
                    byte deltaXByte = Utils.intToByte(x - olderX);
                    byte deltaYByte = Utils.intToByte(y - olderY);

                    socketProcessor.movePointer(deltaXByte, deltaYByte);

                    olderX = x;
                    olderY = y;
                    return true;
                }


                return true;
            }
        });
    }

    private void removeSocketRelatedListeners()
    {
        scUp.setOnClickListener(null);
        scDw.setOnClickListener(null);
        clickButton.setOnClickListener(null);
        mvPane.setOnTouchListener(null);
        dragSwitch.setOnCheckedChangeListener(null);
    }

    private void connect()
    {
        isConnected = true;

        socketProcessor = new SocketProcessor();
        socketProcessor.start();

        connectSocketRelatedListeners();
    }

    private void disconnect()
    {
        isConnected = false;
        Log.d("disconnect", "processing");

        try
        {
            Log.d("SocketAsyncTask", "closing socketProcessor...");

            socketProcessor.closeConnection();
            removeSocketRelatedListeners();

            if (socketProcessor.socket.isClosed())
                Log.d("SocketAsyncTask", "socketProcessor closed");
        }
        catch (Exception e)
        {
            e.printStackTrace();
        }
    }


    @Override
    protected void onStart()
    {
        super.onStart();

        final EditText etSetIP = findViewById(R.id.etSetIP);
        etSetIP.setText(GlobalVars.getIpAddr());
    }

    @Override
    protected void onDestroy() {
        Log.d("Activity", "OnDestroy");

        disconnect();

        super.onDestroy();
    }
}
