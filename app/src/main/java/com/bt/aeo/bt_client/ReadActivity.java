package com.bt.aeo.bt_client;

import android.app.Activity;
import android.app.DatePickerDialog;
import android.app.FragmentTransaction;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.Message;
import android.provider.MediaStore;
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

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.PrintStream;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Scanner;

import zhou.tools.fileselector.FileSelector;
import zhou.tools.fileselector.FileSelectorActivity;
import zhou.tools.fileselector.FileSelectorAlertDialog;
import zhou.tools.fileselector.FileSelectorDialog;
import zhou.tools.fileselector.config.FileConfig;
import zhou.tools.fileselector.config.FileTheme;
import zhou.tools.fileselector.utils.FileFilter;

/**
 * Created by aeo on 2016/6/30.
 */
public class ReadActivity extends  AppCompatActivity{
    // Debugging
    private static final String TAG = "READActivity";
    private static final boolean D = true;

    private FileConfig fileConfig;

    EditText EDT_time;
    EditText EDT_path;
    static String PATH;                                //完整路径
    String FILENAME = "SaveData";         //文件名
    String FILEEND = ".csv";                  //文件后缀
    String DIR = "BT_Recieve";                //文件夹名，也可以不要文件夹
    String month;
    String day;
    String myear;

    private DatePickerDialog.OnDateSetListener DatePickerListener = new DatePickerDialog.OnDateSetListener() {
        @Override
        public void onDateSet(DatePicker view, int year, int monthOfYear,
                              int dayOfMonth) {
            // TODO Auto-generated method stub
            DecimalFormat df = new DecimalFormat("00");
            myear = Integer.toString(year);
            month = df.format((monthOfYear + 1));
            day = df.format(dayOfMonth);
            EDT_time.setText(new StringBuilder().append("").append(myear).append("-").append(month).append("-").append(day).append(""));
            FILENAME = EDT_time.getText().toString();
            PATH =  DIR + File.separator + FILENAME + FILEEND;
            EDT_path.setText(PATH);
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
        fileConfig = new FileConfig();

        EDT_path = (EditText) findViewById(R.id.input_path);
        EDT_time = (EditText) findViewById(R.id.input_time);

        DIR=getSDPath() + File.separator +DIR;
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


        Button Bt_read = (Button) findViewById(R.id.bt_read);
        Button Bt_choose = (Button) findViewById(R.id.bt_choose);
        Bt_read.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //发送数据，更新输入的路径
                PATH = EDT_path.getText().toString();
                String senddata = "GetResult:" + myear + month + day + ".TXT";
                sendMessage(senddata);
            }
        });
        Bt_choose.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                FileSelectorDialog fileDialog = new FileSelectorDialog();
                fileDialog.setOnSelectFinish(new FileSelectorDialog.OnSelectFinish() {
                    @Override
                    public void onSelectFinish(ArrayList<String> paths) {
                        DIR=paths.get(0).toString();
                        PATH =  DIR + File.separator + FILENAME + FILEEND;
                        EDT_path.setText(PATH);
                        Toast.makeText(getApplicationContext(), paths.get(0).toString(), Toast.LENGTH_SHORT).show();
                    }
                });
                Bundle bundle = new Bundle();
                fileConfig.multiModel=true;
                bundle.putSerializable(FileConfig.FILE_CONFIG, fileConfig);
                fileDialog.setArguments(bundle);
                FragmentTransaction ft = getFragmentManager().beginTransaction();
                ft.setTransition(FragmentTransaction.TRANSIT_FRAGMENT_FADE);
                fileDialog.show(ft, "fileDialog");
            }
        });

    }

    private void sendMessage(String message) {

        //按下设置未连接蓝牙发出提醒
        if (MainActivity.mBTService.getState() != BluetoothService.STATE_CONNECTED) {
            Toast.makeText(ReadActivity.this, R.string.str_not_connected, Toast.LENGTH_SHORT).show();
        } else {
            //发送数据到蓝牙
            //没输入提示警告
            if (EDT_time.getText().length() == 0 || EDT_path.getText().length() == 0)
                Toast.makeText(ReadActivity.this, R.string.str_input_waring, Toast.LENGTH_SHORT).show();
            else {
                byte[] send = message.getBytes();
                MainActivity.mBTService.write(send);
            }
        }

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




    public String getSDPath() {
        File sdDir = null;
        boolean sdCardExist = Environment.getExternalStorageState()
                .equals(android.os.Environment.MEDIA_MOUNTED);//判断sd卡是否存在
        if (sdCardExist) {
            sdDir = Environment.getExternalStorageDirectory();//获取跟目录
        } else { // SDCard不存在，使用Toast提示用户
            Toast.makeText(this, "打开路径失败，SD卡不存在！", Toast.LENGTH_LONG).show();
        }
        return sdDir.toString();
    }

    // 文件写操作函数
    public static void writeFile(String content, boolean append) {

        if (Environment.getExternalStorageState().equals(
                Environment.MEDIA_MOUNTED)) { // 如果sdcard存在
            File file = new File(PATH); // 定义File类对象

            if (!file.getParentFile().exists()) { // 父文件夹不存在
                file.getParentFile().mkdirs(); // 创建文件夹
            }
            PrintStream out = null; // 打印流对象用于输出
            try {
                out = new PrintStream(new FileOutputStream(file, append)); // 追加文件
                if (!content.equals("")) //空的就不输出了
                    out.println(content);
            } catch (Exception e) {
                e.printStackTrace();
            } finally {
                if (out != null) {
                    out.close(); // 关闭打印流
                }
            }
        }
    }


    // 文件读操作函数
    private String read() {

        if (Environment.getExternalStorageState().equals(
                Environment.MEDIA_MOUNTED)) { // 如果sdcard存在
            File file = new File(Environment.getExternalStorageDirectory()
                    .toString()
                    + File.separator
                    + DIR
                    + File.separator
                    + FILENAME); // 定义File类对象
            if (!file.getParentFile().exists()) { // 父文件夹不存在
                file.getParentFile().mkdirs(); // 创建文件夹
            }
            Scanner scan = null; // 扫描输入
            StringBuilder sb = new StringBuilder();
            try {
                scan = new Scanner(new FileInputStream(file)); // 实例化Scanner
                while (scan.hasNext()) {            // 循环读取
                    sb.append(scan.next() + "\n"); // 设置文本
                }
                return sb.toString();
            } catch (Exception e) {
                e.printStackTrace();
            } finally {
                if (scan != null) {
                    scan.close(); // 关闭打印流
                }
            }
        } else { // SDCard不存在，使用Toast提示用户
            Toast.makeText(this, "读取失败，SD卡不存在！", Toast.LENGTH_LONG).show();
        }
        return null;
    }


}
