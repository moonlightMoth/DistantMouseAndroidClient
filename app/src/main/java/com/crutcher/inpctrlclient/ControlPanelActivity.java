package com.crutcher.inpctrlclient;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.view.autofill.AutofillValue;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;

import java.net.InetAddress;

import static java.lang.Math.abs;

public class ControlPanelActivity extends AppCompatActivity {

    private static final String TAG = "ControlPanelActivity";
    SocketProcessor socketProcessor;
    boolean isConnected = false;

    private int x;
    private int y;
    private int olderX;
    private int olderY;

    private long lastTouchDown;
    private int motionCountSinceLastDown = 0;
    private static final int CLICK_ACTION_THRESHOLD = 170;
    private static final int SCROLL_THRESHOLD = 20;
    private static final int MV_PANE_DRAG_ON_THRESHOLD = 330;

    private Button setIP;
    private Button rec;
    private Button clickButton;
    private View scPane;
    private Button disc;
    private EditText etSetIP;
    private View mvPane;
    private Switch dragSwitch;
    private Button scanButton;
    private ProgressBar progressBar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_control_panel);

        setIP = findViewById(R.id.bSetIP);
        rec = findViewById(R.id.reconnect);
        disc = findViewById(R.id.disc);
        scPane = findViewById(R.id.scPane);
        clickButton = findViewById(R.id.clickButton);
        etSetIP = findViewById(R.id.etSetIP);
        mvPane = findViewById(R.id.mvPane);
        dragSwitch = findViewById(R.id.dragSwitch);
        scanButton = findViewById(R.id.bScan);
        progressBar = findViewById(R.id.progressBar);

        ((TextView)findViewById(R.id.tvVersion)).setText(BuildConfig.VERSION_NAME);

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
                disconnect();
                GlobalVars.setIpAddr(etSetIP.getText().toString());
                connect();
            }
        });

        scanButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                Log.d(TAG, "onClick: Scan clicked");

                progressBar.setVisibility(View.VISIBLE);

                ScanAsyncTask scanAsyncTask = new ScanAsyncTask(ControlPanelActivity.this::scanObserverMethod);
                scanAsyncTask.execute(0);

            }
        });
    }

    private void scanObserverMethod(InetAddress inetAddress)
    {
        progressBar.setVisibility(View.GONE);

        insertDiscoveredAddress(inetAddress);
    }

    private void insertDiscoveredAddress(InetAddress inetAddress)
    {


        if (inetAddress != null) {
            Toast.makeText(ControlPanelActivity.this, inetAddress.getHostAddress() + " found", Toast.LENGTH_LONG).show();
            etSetIP.autofill(AutofillValue.forText(inetAddress.getHostAddress()));
        }
        else
            Toast.makeText(ControlPanelActivity.this, "No response from any server", Toast.LENGTH_LONG).show();

    }

    private void connectSocketRelatedListeners()
    {
        scPane.setOnTouchListener(new View.OnTouchListener()
        {
            @Override
            public boolean onTouch(View v, MotionEvent motionEvent)
            {
                int action = motionEvent.getAction();
                y = (int) motionEvent.getY();

                if (action == MotionEvent.ACTION_DOWN)
                {
                    olderY = y;

                    return true;
                }

                if (action == MotionEvent.ACTION_UP)
                {
                    return false;
                }

                if (action == MotionEvent.ACTION_MOVE)
                {
                    if (abs(y - olderY) > SCROLL_THRESHOLD)
                    {
                        socketProcessor.scrollFreely(Utils.intToByte((y - olderY) / SCROLL_THRESHOLD));
                        olderY = y;
                    }
                    return true;
                }

                return true;
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
                if (isChecked)
                    socketProcessor.dragOn();
                else
                    socketProcessor.dragOff();
            }
        });

        mvPane.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View view, MotionEvent motionEvent) {


                if (motionEvent.getAction() == MotionEvent.ACTION_MOVE)
                {
                    Log.d(TAG, "onTouch: ACTION_MOVE");
                    socketProcessor.movePointer(Utils.intToByte((int) motionEvent.getX() - olderX),
                            Utils.intToByte((int) motionEvent.getY() - olderY));

                    olderX = (int) motionEvent.getX();
                    olderY = (int) motionEvent.getY();

                    motionCountSinceLastDown++;

                    return true;
                }

                if (motionEvent.getAction() == MotionEvent.ACTION_DOWN)
                {
                    Log.d(TAG, "onTouch: ACTION_DOWN");

                    if (System.currentTimeMillis() - lastTouchDown < MV_PANE_DRAG_ON_THRESHOLD
                            && motionCountSinceLastDown < 4)
                    {
                        dragSwitch.setChecked(true);
                        Log.d(TAG, "onTouch: DRAG_ON");
                    }

                    olderX = (int) motionEvent.getX();
                    olderY = (int) motionEvent.getY();
                    lastTouchDown = System.currentTimeMillis();

                    motionCountSinceLastDown = 0;

                    return true;
                }

                if (motionEvent.getAction() == MotionEvent.ACTION_UP)
                {
                    Log.d(TAG, "onTouch: ACTION_UP");
                    if (dragSwitch.isChecked())
                        dragSwitch.setChecked(false);

                    if (System.currentTimeMillis() - lastTouchDown < CLICK_ACTION_THRESHOLD
                            && motionCountSinceLastDown < 4)
                    {
                        socketProcessor.clickLmb();
                        Log.d(TAG, "onTouch: clicked LMB");
                    }



                    return true;
                }

                return true;
            }
        });
    }

    private void removeSocketRelatedListeners()
    {
        scPane.setOnClickListener(null);
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
        dragSwitch.setChecked(false);
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
