package cal_on.usbterminal;

import android.Manifest;
import android.annotation.SuppressLint;
import android.annotation.TargetApi;
import android.app.AlertDialog;
import android.app.PendingIntent;
import android.app.ProgressDialog;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ScrollView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.lang.reflect.Method;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Set;

public class Bluetooth extends AppCompatActivity {



    Spinner bluetooth_devices;
    private P25Connector mConnector;
    private ProgressDialog mProgressDlg;
    private ProgressDialog mConnectingDlg;
    private BluetoothAdapter mBluetoothAdapter;
    public static String BTAddress;
    boolean blueToothconnected = false;
    private final String TAG = Bluetooth.class.getSimpleName();
    byte[] imagedata = null;
    protected static final int CAMERA_IMAGE_CAPTUE_OK = -1;
    protected static final int CAMERA_PIC_REQUEST = 1337;
    BTConnection uConn;
    static String imagesDir = Environment.getExternalStorageDirectory().getPath();
    String capturedImgSave;
    LinearLayout blueDisable;
    LinearLayout blueEnable;
    Bluetooth_Img_ThermalAPI printer;

    private static EditText mDumpTextView,heighted;
    private static ScrollView mScrollView;
    private static   String totalData = "";
    private PendingIntent permissionIntent;
    TextView filePath;
    boolean isPrinterConnected;
    private static final int MESSAGE_REFRESH = 101;
    Button  linefeed, testprint,textprint,takepick,datarecvi,printpic,fontsize,height;
    TextView connect_btn;
    TextView btn_disconnect;
    Button enableBlutooth;
    private ArrayList<BluetoothDevice> mDeviceList = new ArrayList<BluetoothDevice>();
    public String font_Size_38(String paramString)
    {
        return String.valueOf(new String(new byte[] { 27, 75, 9 })) + paramString;
    }
    public String font_Size_10(String paramString)
    {
        return String.valueOf(new String(new byte[] { 27, 75, 8 })) + paramString;
    }
    public String font_Sans_Serif_20(String paramString)
    {
        return String.valueOf(new String(new byte[] { 27, 75, 14 })) + paramString;
    }
    public String font_Sans_Serif_30(String paramString)
    {
        return String.valueOf(new String(new byte[] { 27, 75, 0})) + paramString;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_bluetooth);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);


        toolbar.setTitle(" BTP Application");

        setSupportActionBar(toolbar);
        blueEnable=(LinearLayout) findViewById(R.id.blueEnable);
        blueDisable=(LinearLayout) findViewById(R.id.blueDisable);
        mDumpTextView = (EditText) findViewById(R.id.givedata);
        mScrollView = (ScrollView) findViewById(R.id.demoScrollerusb);
        bluetooth_devices = (Spinner) findViewById(R.id.spinner_baudrate);
        testprint=(Button)findViewById(R.id.testprint);
        textprint=(Button)findViewById(R.id.textprint);
        printpic=(Button)findViewById(R.id.printimage);
        takepick=(Button)findViewById(R.id.takepick);
        fontsize=(Button)findViewById(R.id.fontsize);
        TextView save = (TextView) findViewById(R.id.tv_save_usb);


        datarecvi=(Button)findViewById(R.id.datarecvi);

//        consoleTextUSB=(TextView) findViewById(R.id.consoleTextusb);
        connect_btn = (TextView) findViewById(R.id.btn_connect);
        btn_disconnect = (TextView) findViewById(R.id.btn_disconnect);
        enableBlutooth = (Button) findViewById(R.id.enableBlutooth);
        enableBlutooth.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
                startActivityForResult(intent, 1000);
            }
        });
        mBluetoothAdapter = BluetoothAdapter.getDefaultAdapter();

        btn_disconnect.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                try {
                    uConn.closeBT();
                    showDisonnected();
                } catch (IOException e) {
                    e.printStackTrace();
                }

        }
            });
        testprint.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                try
                {
                    String str = new String(new byte[]{27, 65, 13});
                    uConn.printData(str);
                    showToast("Test Successfuly Done");
                    return;
                }
                catch (Exception localException)
                {
                    localException.printStackTrace();
                    showToast("Unable to Print");
                }
            }
        });
        textprint.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
               try{

                        String str7 = mDumpTextView.getText().toString();
                        String str8 = font_Size_38(str7);
                        str8 += "\n";
                        uConn.printData(str8);

                        showToast("Data Is Printing");

                }
                catch (Exception localException)
                {
                    localException.printStackTrace();
                    showToast("Unable to Print ");
                }

            }
        });


        printpic.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                try
                {
                    Bluetooth.this.printer = new Bluetooth_Img_ThermalAPI();
                    System.out.println("aaa");
                    File str = Environment.getExternalStorageDirectory();
                    Bluetooth.this.imagedata = Bluetooth.this.printer.prepareImageToPrint(str + "/001.jpg");
                    System.out.println("bbb");
                    if (!Bluetooth.this.imagedata.equals(null))
                    {
                        System.out.println("cccc");
                        Toast.makeText(Bluetooth.this.getBaseContext(), "please wait....." + Bluetooth.this.imagedata,Toast.LENGTH_SHORT).show();
                        Bluetooth.this.uConn.printData(Bluetooth.this.imagedata);
                        return;
                    }
                    Toast.makeText(Bluetooth.this.getBaseContext(), "No Img Data......." + Bluetooth.this.imagedata,Toast.LENGTH_SHORT).show();
                    return;
                }
                catch (Exception localException)
                {
                    localException.printStackTrace();
                }
            }
        });
        takepick.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                try
                {
                    File localFile = new File(Bluetooth.imagesDir, "001.jpg");
                    Bluetooth.this.capturedImgSave = localFile.getPath();
                    Intent localIntent = new Intent("android.media.action.IMAGE_CAPTURE");
                    localIntent.putExtra("output", Uri.fromFile(localFile));
                    Bluetooth.this.startActivityForResult(localIntent, 1337);
                    return;
                }
                catch (Exception localException)
                {
                    localException.printStackTrace();
                }
            }
        });
        fontsize.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                final String arr[]={"Font Size 10","Font Size 20","Font Size 30"};
                AlertDialog.Builder s=new AlertDialog.Builder(Bluetooth.this);
                s.setTitle("Different Sizes");
                s.setItems(arr, new DialogInterface.OnClickListener() {

                    @Override
                    public void onClick(DialogInterface arg0, int arg1) {
                        // TODO Auto-generated method stub


                        switch (arg1) {
                            case 0:
                                try {
                                    String str5 = Bluetooth.this.mDumpTextView.getText().toString();
                                    String str6 = font_Size_10(str5);
                                    Bluetooth.this.uConn.printData(str6);
                                    showToast("Font changed to 10");
                                }
                                catch (Exception localException)
                                {
                                    localException.printStackTrace();
                                    showToast("Font Not cahanged");
                                }
                                break;
                            case 1:
                                try {
                                    String str7 = Bluetooth.this.mDumpTextView.getText().toString();
                                    String str8 = font_Sans_Serif_20(str7);
                                    Bluetooth.this.uConn.printData(str8);
                                    showToast("Font changed to 20");
                                }
                                catch (Exception localException)
                                {
                                    localException.printStackTrace();
                                    showToast("Font Not cahanged");
                                }
                                break;
                            case 2:
                                try {
                                    String str9 = Bluetooth.this.mDumpTextView.getText().toString();
                                    String str10 = font_Sans_Serif_30(str9);
                                    Bluetooth.this.uConn.printData(str10);
                                    showToast("Font changed to 30");
                                }
                                catch (Exception localException)
                                {
                                    localException.printStackTrace();
                                    showToast("Font Not cahanged");
                                }

                            default:
                                break;
                        }

                    }
                });
                AlertDialog s1=s.create();
                s1.show();
            }
        });


        connect_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                connect();
            }
        });
        linefeed = (Button) findViewById(R.id.linefeeed);
        linefeed.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                try {



                    String str = new String(new byte[]{27, 64, 13});

                    uConn.printData(str);
                    showToast("Line Feed is Done");


                } catch (NullPointerException e) {

                    e.printStackTrace();
                    showToast("Line Feed is not Done");
                }
            }
        });

        if (mBluetoothAdapter == null) {
            showUnsupported();
        } else {
            if (!mBluetoothAdapter.isEnabled()) {
                showDisabled();
            } else {
                showEnabled();

                Set<BluetoothDevice> pairedDevices = mBluetoothAdapter.getBondedDevices();

                if (pairedDevices != null) {
                    mDeviceList.addAll(pairedDevices);

                    updateDeviceList();
                }
            }

            mProgressDlg = new ProgressDialog(this);

            mProgressDlg.setMessage("Scanning...");
            mProgressDlg.setCancelable(false);
            mProgressDlg.setButton(DialogInterface.BUTTON_NEGATIVE, "Cancel", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    dialog.dismiss();

                    mBluetoothAdapter.cancelDiscovery();
                }
            });

            mConnectingDlg = new ProgressDialog(this);

            mConnectingDlg.setMessage("Connecting...");
            mConnectingDlg.setCancelable(false);

            mConnector = new P25Connector(new P25Connector.P25ConnectionListener() {

                @Override
                public void onStartConnecting() {
                    mConnectingDlg.show();
                }

                @Override
                public void onConnectionSuccess() {
                    mConnectingDlg.dismiss();

                    //showConnected();
                }

                @Override
                public void onConnectionFailed(String error) {
                    mConnectingDlg.dismiss();
                }

                @Override
                public void onConnectionCancelled() {
                    mConnectingDlg.dismiss();
                }

                @Override
                public void onDisconnected() {
                    showDisonnected();
                }
            });
        }
            uConn = new BTConnection();
            IntentFilter filter = new IntentFilter();
            filter.addAction(BluetoothAdapter.ACTION_STATE_CHANGED);
            filter.addAction(BluetoothDevice.ACTION_FOUND);
            filter.addAction(BluetoothAdapter.ACTION_DISCOVERY_STARTED);
            filter.addAction(BluetoothAdapter.ACTION_DISCOVERY_FINISHED);
            filter.addAction(BluetoothDevice.ACTION_BOND_STATE_CHANGED);
            registerReceiver(mReceiver, filter);
        save.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                try {
                    filePath.setText(exporttoCSV(mDumpTextView.getText().toString()));
                    mDumpTextView.setText("");
                    Toast.makeText(getApplicationContext(), "CSV file created and saved in internal storage" + filePath.getText().toString(), Toast.LENGTH_SHORT).show();
                } catch (IOException e) {
                    Toast.makeText(getApplicationContext(), "Error Occurred while export file", Toast.LENGTH_SHORT).show();
                    filePath.setText("Error Occurred while export file");
                    e.printStackTrace();
                }
            }
        });

        datarecvi.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {


                try
                {
                    String str = new String(new byte[]{27,87,13});
                    uConn.printData(str);

                    showToast("Displaying");
                    return;
                }
                catch (Exception localException)
                {
                    localException.printStackTrace();
                    showToast("Unable to Receive");
                }
            }
        });

    }
    private final BroadcastReceiver mReceiver = new BroadcastReceiver() {
        public void onReceive(Context context, Intent intent) {
            String action = intent.getAction();

            if (BluetoothAdapter.ACTION_STATE_CHANGED.equals(action))
            {
                final int state = intent.getIntExtra(BluetoothAdapter.EXTRA_STATE, BluetoothAdapter.ERROR);

                if (state == BluetoothAdapter.STATE_ON) {
                    showEnabled();
                } else if (state == BluetoothAdapter.STATE_OFF) {
                    showDisabled();
                }
            }
            else if (BluetoothAdapter.ACTION_DISCOVERY_STARTED.equals(action)) {
                mDeviceList = new ArrayList<BluetoothDevice>();

                mProgressDlg.show();
            } else if (BluetoothAdapter.ACTION_DISCOVERY_FINISHED.equals(action)) {
                mProgressDlg.dismiss();

                updateDeviceList();
            } else if (BluetoothDevice.ACTION_FOUND.equals(action)) {
                BluetoothDevice device = (BluetoothDevice) intent.getParcelableExtra(BluetoothDevice.EXTRA_DEVICE);

                mDeviceList.add(device);

                showToast("Found device " + device.getName());
            } else if (BluetoothDevice.ACTION_BOND_STATE_CHANGED.equals(action)) {
                final int state = intent.getIntExtra(BluetoothDevice.EXTRA_BOND_STATE, BluetoothDevice.ERROR);

                if (state == BluetoothDevice.BOND_BONDED) {
                    showToast("Paired");

                    connect();
                }
            }
        }
    };
    public void showToast(String message) {
        Toast.makeText(this, message, Toast.LENGTH_SHORT).show();
    }
    private String[] getArray(ArrayList<BluetoothDevice> data) {
        String[] list = new String[0];

        if (data == null) return list;

        int size = data.size();
        list = new String[size];

        for (int i = 0; i < size; i++) {
            list[i] = data.get(i).getName();
        }

        return list;
    }
    private void updateDeviceList() {
        ArrayAdapter<String> adapter = new ArrayAdapter<String>(this, R.layout.simple_spinner_item, getArray(mDeviceList));

        adapter.setDropDownViewResource(R.layout.simple_spinner_dropdown_item);

        bluetooth_devices.setAdapter(adapter);
        bluetooth_devices.setSelection(0);
    }

    private void showDisabled() {
        showToast("Bluetooth disabled");

        blueEnable.setVisibility(View.GONE);
        blueDisable.setVisibility(View.VISIBLE);
    }

    private void showEnabled() {
        showToast("Bluetooth enabled");
        blueEnable.setVisibility(View.VISIBLE);
        blueDisable.setVisibility(View.GONE);
    }
    private void showConnected() {
        showToast("Connected");
        isPrinterConnected=true;
        connect_btn.setVisibility(View.GONE);
        btn_disconnect.setVisibility(View.VISIBLE);
        enableBlutooth.setVisibility(View.GONE);
        bluetooth_devices.setEnabled(false);
    }

    private void showDisonnected() {
        showToast("Disconnected");
        connect_btn.setVisibility(View.VISIBLE);
        btn_disconnect.setVisibility(View.GONE);
        enableBlutooth.setVisibility(View.GONE);
        bluetooth_devices.setEnabled(true);
    }
    private void showUnsupported() {
        showToast("Bluetooth is unsupported by this device");

        connect_btn.setEnabled(false);
        bluetooth_devices.setEnabled(false);
    }

    public  void createBond(BluetoothDevice device) throws Exception {
        BTAddress = device.toString();
        BTAddress.trim();
        try {
            uConn.openBT(BTAddress);
            Log.e("coonected", "bt connected2");
            Log.e("coonected", BTAddress);
        } catch (IOException e) {

            e.printStackTrace();
        }
        try {
            Class<?> cl = Class.forName("android.bluetooth.BluetoothDevice");
            Class<?>[] par = {};

            Method method = cl.getMethod("createBond", par);

            method.invoke(device);

        } catch (Exception e) {
            e.printStackTrace();

            throw e;
        }
    }

    private void connect() {
        if (mDeviceList == null || mDeviceList.size() == 0) {
            return;
        }

        BluetoothDevice device = mDeviceList.get(bluetooth_devices.getSelectedItemPosition());

        if (device.getBondState() == BluetoothDevice.BOND_NONE) {
            try {
                uConn.openBT(device.getAddress());
                registerReceiver(mReceiver, new IntentFilter(BluetoothDevice.ACTION_FOUND));
                createBond(device);
            } catch (Exception e) {
                showToast("Failed to pair device");

                return;
            }
        }

        try {
            if (!blueToothconnected) {
                // mConnector.connect(device);
                Log.e("coonected", "bt connected2");
                uConn.openBT(device.getAddress());
                Log.e("blueToothconnected", String.valueOf(blueToothconnected));
                showConnected();
                blueToothconnected = true;
            } else {
                Log.e("blueToothconnected", String.valueOf(blueToothconnected));
                BTConnection.closeBT();
                blueToothconnected = false;
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
    @Override
    protected void onPause() {
        super.onPause();

    }

    void showStatus(TextView theTextView, String theLabel, boolean theValue) {
        String msg = theLabel + ": " + (theValue ? "enabled" : "disabled") + "\n";
        theTextView.append(msg);
    }


    @TargetApi(Build.VERSION_CODES.HONEYCOMB_MR1)
    @Override
    protected void onResume() {
        super.onResume();

    }
    public static void showBluetoothData( byte[] data) {
        String s=new String(data);
        totalData=totalData+s;
        mDumpTextView.append(s);
        mScrollView.smoothScrollTo(0, mScrollView.getBottom());

    }
    public String exporttoCSV(final String totalData) throws IOException {
        if (ContextCompat.checkSelfPermission(this,
                Manifest.permission.WRITE_EXTERNAL_STORAGE)
                != PackageManager.PERMISSION_GRANTED) {

            if (ActivityCompat.shouldShowRequestPermissionRationale(this,
                    Manifest.permission.WRITE_EXTERNAL_STORAGE)) {

            } else {


                ActivityCompat.requestPermissions(this,
                        new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE},23
                );
            }
        }
        File mainFolder = new File(Environment.getExternalStorageDirectory()
                + "/Cal-ONTerminal");
        boolean mainVar = false;
        if (!mainFolder.exists())
            mainVar=mainFolder.mkdir();
        final File folder = new File(Environment.getExternalStorageDirectory()
                + "/Cal-ONTerminal/Bluetooth_terminal_files");

        boolean var = false;
        if (!folder.exists())
            var = folder.mkdir();

        System.out.println("" + var);

        @SuppressLint("SimpleDateFormat") SimpleDateFormat fmt = new SimpleDateFormat("yyMMdd_HHmmss");
        Date dt = new Date();
        final String fileFormatName = fmt.format(dt) + ".csv";
        final String filename = folder.toString() + "/" + fileFormatName;

        new Thread() {
            public void run() {
                try {

                    FileWriter fw = new FileWriter(filename);
                    fw.append(totalData);
                    fw.close();

                } catch (Exception e) {
                }
            }
        }.start();
        return "  [/Cal-ONTerminal/Bluetooth_terminal_files/" + fileFormatName + "]";
    }

}
