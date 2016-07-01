package com.bt.aeo.bt_client;

import android.app.Activity;
import android.app.DatePickerDialog;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.Toast;

import java.text.DecimalFormat;
import java.util.Calendar;

/**
 * Created by aeo on 2016/6/30.
 */
public class ReadActivity extends AppCompatActivity {
    // Debugging
    private static final String TAG = "READActivity";
    private static final boolean D = true;

    EditText EDT_time;


    private DatePickerDialog.OnDateSetListener DatePickerListener = new DatePickerDialog.OnDateSetListener() {
        @Override
        public void onDateSet(DatePicker view, int year, int monthOfYear,
                              int dayOfMonth) {
            // TODO Auto-generated method stub
            DecimalFormat df=new DecimalFormat("00");
            String month=df.format((monthOfYear+1));
            String day=df.format(dayOfMonth);
            EDT_time.setText(new StringBuilder().append("").append(year).append("-").append(month).append("-").append(day).append(""));
        }
    };
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (D) Log.e(TAG, "+++ ON CREATE +++");

        // Set up the window layout
        setContentView(R.layout.activity_read);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        EDT_time = (EditText) findViewById(R.id.input_time);
        EDT_time.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                final Calendar objTime = Calendar.getInstance();
                int iYear = objTime.get(Calendar.YEAR);
                int iMonth = objTime.get(Calendar.MONTH);
                int iDay = objTime.get(Calendar.DAY_OF_MONTH);

                DatePickerDialog picker = new DatePickerDialog(ReadActivity.this, DatePickerListener,
                        iYear, iMonth, iDay);
                picker.setCancelable(true);
                picker.setCanceledOnTouchOutside(true);
                picker.setButton(DialogInterface.BUTTON_POSITIVE, "确定",
                        new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {


                            }
                        });
                picker.setButton(DialogInterface.BUTTON_NEGATIVE, "取消",
                        new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {


                            }
                        });
                picker.show();
            }
        });





    }

    @Override
    public void onStart() {
        super.onStart();
        if (D) Log.e(TAG, "++ ON START ++");

        // Initialize the BluetoothChatService to perform bluetooth connections
        if (MainActivity.mBTService == null)
            this.finish();
        //else
        // Init();

    }

    @Override
    public synchronized void onResume() {
        super.onResume();
        if (D) Log.e(TAG, "+ ON RESUME +");

        if (MainActivity.mBTService != null) {
            if (MainActivity.mBTService.getState() != BluetoothService.STATE_CONNECTED) {
                Toast.makeText(this, R.string.str_not_connected, Toast.LENGTH_SHORT).show();
            }
        }

    }

    @Override
    public synchronized void onPause() {
        super.onPause();
        if (D) Log.e(TAG, "- ON PAUSE -");
    }

    @Override
    public void onStop() {
        super.onStop();
        if (D) Log.e(TAG, "-- ON STOP --");
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        // Stop the Bluetooth chat services
        if (D) Log.e(TAG, "--- ON DESTROY ---");
    }

    //返回键
    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK) {

            this.finish();
            return true;
        }
        return false;
    }


}
