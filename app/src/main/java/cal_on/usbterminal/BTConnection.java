package cal_on.usbterminal;

import android.annotation.TargetApi;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothSocket;
import android.os.Handler;
import android.util.Log;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.Iterator;
import java.util.Set;
import java.util.UUID;

public class BTConnection {
    static BluetoothAdapter mBluetoothAdapter;
    static BluetoothSocket mmSocket;
    static BluetoothDevice mmDevice;
    static OutputStream mmOutputStream;
    static InputStream mmInputStream;
    private static final int REQUEST_ENABLE_BT = 1;
    static Thread workerThread;
    static byte[] readBuffer;
    static int readBufferPosition;
    int counter;
    static volatile boolean stopWorker;

    public BTConnection() {
    }

    @TargetApi(5)
    public static void openBT(String address) throws IOException {
        BTConnection.findBT(address);

        try {
            UUID uuid2 = UUID.fromString("00001101-0000-1000-8000-00805f9b34fb");
            BTConnection.mmSocket = BTConnection.mmDevice.createRfcommSocketToServiceRecord(uuid2);
            mmSocket.connect();
            mmOutputStream = mmSocket.getOutputStream();
            mmInputStream = mmSocket.getInputStream();
            beginListenForData();
        } catch (NullPointerException var3) {
            ;
        } catch (Exception var4) {
            ;
        }

    }

    @TargetApi(19)
    public static void findBT(String address) {
        try {
            mBluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
            if(!mBluetoothAdapter.isEnabled()) {
                mBluetoothAdapter.enable();
            }

            Set var6 = mBluetoothAdapter.getBondedDevices();
            BluetoothDevice device1;
            if(var6.size() > 0) {
                Iterator var4 = var6.iterator();

                while(var4.hasNext()) {
                    device1 = (BluetoothDevice)var4.next();
                    if(device1.getAddress().equals(address)) {
                        mmDevice = device1;
                        break;
                    }
                }
            }

            device1 = null;
            mmDevice = mBluetoothAdapter.getRemoteDevice(address);
            mmDevice.createBond();
        } catch (NullPointerException var5) {
            var5.printStackTrace();
        } catch (Exception var61) {
            var61.printStackTrace();
        }

    }

    public static void closeBT() throws IOException {
        try {
            mmOutputStream.close();
            mmInputStream.close();
            mmSocket.close();
        } catch (NullPointerException var2) {
            var2.printStackTrace();

        } catch (Exception var3) {
            var3.printStackTrace();

        }

    }

    private static void beginListenForData() {
        try {
            final Handler handler = new Handler();
            final byte delimiter = 10; //This is the ASCII code for a newline character

            stopWorker = false;
            readBufferPosition = 0;
            readBuffer = new byte[1024];
            workerThread = new Thread(new Runnable()
            {
                public void run()
                {
                    while(!Thread.currentThread().isInterrupted() && !stopWorker)
                    {
                        try
                        {
                            int bytesAvailable = mmInputStream.available();
                            if(bytesAvailable > 0)
                            {
                                Log.e("sdfdsfdsfds","sdfdsfdsdfdsf1111111111111");
                                byte[] packetBytes = new byte[bytesAvailable];
                                mmInputStream.read(packetBytes);
                                for(int i=0;i<bytesAvailable;i++)
                                {
                                    byte b = packetBytes[i];
                                    readBuffer[readBufferPosition++] = b;
                                    handler.post(new Runnable()
                                    {
                                        public void run()
                                        {
                                            final byte[] encodedBytes = new byte[readBufferPosition];
                                        System.arraycopy(readBuffer, 0, encodedBytes, 0, encodedBytes.length);
                                        readBufferPosition = 0;

                                            Bluetooth.showBluetoothData(encodedBytes);
                                        }
                                    });
                                }
                            }
                        }
                        catch (IOException ex)
                        {
                            stopWorker = true;
                        }
                    }
                }
            });

            workerThread.start();
        } catch (NullPointerException var3) {
            var3.printStackTrace();
        } catch (Exception var41) {
            var41.printStackTrace();
        }

    }

    public boolean printData(byte[] msg) {
        boolean flag = false;

        try {
            this.mmOutputStream.write(msg);
            this.mmOutputStream.flush();
            this.mmOutputStream.write(13);
            this.mmOutputStream.flush();
            flag = true;
        } catch (Exception var4) {
            var4.printStackTrace();
        }

        return flag;
    }

    public boolean printData(String msg) {
        boolean flag = false;

        try {
            this.mmOutputStream.write(msg.getBytes());
            this.mmOutputStream.flush();
            this.mmOutputStream.write(13);
            this.mmOutputStream.flush();
        } catch (IOException var4) {
            var4.printStackTrace();
        }

        flag = true;
        return flag;
    }


}
