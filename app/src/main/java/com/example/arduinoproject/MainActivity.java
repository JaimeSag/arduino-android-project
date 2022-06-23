package com.example.arduinoproject;

import androidx.appcompat.app.AppCompatActivity;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import androidx.navigation.ui.NavigationUI;

import android.annotation.SuppressLint;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothSocket;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.material.bottomnavigation.BottomNavigationView;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.UUID;

public class MainActivity extends AppCompatActivity{
    BottomNavigationView bottomNavigationView;
    TextView jpotValue, jcapValue, jsecPotText, jsecCapText;
    ConnectThread connectThread;
    Handler bluetoothIn;
    ConnectedThread connectedThread;
    private BluetoothAdapter bluetoothAdapter;
    private BluetoothDevice bluetoothDevice;
    private BluetoothSocket mmSocket;
    public static String address;
    final int handleState = 0;
    private static final UUID btModuleUUID = UUID.fromString("00001101-0000-1000-8000-00805F9B34FB");

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        bottomNavigationView = findViewById(R.id.bottom_navigation_view);
        bluetoothAdapter = BluetoothAdapter.getDefaultAdapter();

        jpotValue = findViewById(R.id.xpot_value);
        jcapValue = findViewById(R.id.xcap_value);
        jsecPotText = findViewById(R.id.xsec_text_pot);
        jsecCapText = findViewById(R.id.xsec_text_cap);

        bluetoothIn = new Handler(){
            public  void handleMessage(android.os.Message msg){
                if(msg.what == 0) {
                    String readMessage = (String) msg.obj;
                    String[] arrSplit = readMessage.split(",");
                    jpotValue.setText(arrSplit[0]);
                    jsecPotText.setText(arrSplit[1]);
                    jcapValue.setText(arrSplit[2]);
                }
            }
        };

        NavController navController = Navigation.findNavController(this, R.id.nav_host_fragment_container);
        NavigationUI.setupWithNavController(bottomNavigationView, navController);
    }

    @SuppressLint("MissingPermission")
    @Override
    protected void onResume() {
        super.onResume();
        Intent intd = getIntent();
        address = intd.getStringExtra(LinkedDevices.EXTRA_DEVICE_ADDRESS);
        bluetoothDevice = bluetoothAdapter.getRemoteDevice(address);
        connectThread = new ConnectThread(bluetoothDevice);
        connectThread.start();
        while(true){
            if(!connectThread.isAlive()){
                if(!mmSocket.isConnected()) {
                    startActivity(new Intent(this, LinkedDevices.class));
                    Toast.makeText(this, "No se pudo establecer la conexión", Toast.LENGTH_LONG).show();
                    finish();
                    break;
                }
                else Toast.makeText(this, "Dispositivo conectado", Toast.LENGTH_LONG).show();
                break;
            }
        }
        connectedThread = new ConnectedThread(mmSocket, bluetoothIn);
        connectedThread.start();
    }

    private class ConnectThread extends Thread {
        private final BluetoothDevice mmDevice;
        String TAG = "ERROR";

        @SuppressLint("MissingPermission")
        public ConnectThread(BluetoothDevice device) {
            BluetoothSocket tmp = null;
            mmDevice = device;
            BluetoothAdapter.getDefaultAdapter().cancelDiscovery();
            try {
                tmp = device.createInsecureRfcommSocketToServiceRecord(btModuleUUID);
            } catch (IOException e) {
                Log.e(TAG, "El método create() de Socket falló\n", e);
            }
            mmSocket = tmp;
        }

        @SuppressLint("MissingPermission")
        public void run() {
            bluetoothAdapter.cancelDiscovery();
            try {
                mmSocket.connect();
            } catch (IOException connectException) {
                Log.e(TAG, "", connectException);
                try {
                    mmSocket.close();
                } catch (IOException closeException) {
                    Log.e(TAG, "No se pudo cerrar el socket del cliente", closeException);
                }
                return;
            }
            if(mmSocket.isConnected()){
                Log.i("INFO", "Dispositivo conectado " + mmSocket.getRemoteDevice());
            }
        }

        public void cancel() {
            try {
                mmSocket.close();
            } catch (IOException e) {
                Log.e(TAG, "No se pudo cerrar el socket del cliente", e);
            }
        }
    }

    private class ConnectedThread extends Thread {
        private final BluetoothSocket mmSocket;
        private final InputStream mmInStream;
        private final OutputStream mmOutStream;
        private static final String TAG = "MY_APP_DEBUG_TAG";
        private Handler handler;
        private byte[] mmBuffer;

        public ConnectedThread(BluetoothSocket socket, Handler handlerBT) {
            handler = handlerBT;
            mmSocket = socket;
            InputStream tmpIn = null;
            OutputStream tmpOut = null;

            try {
                tmpIn = socket.getInputStream();
            } catch (IOException e) {
                Log.e(TAG, "Ocurrió un error al crear el flujo de entrada", e);
            }
            try {
                tmpOut = socket.getOutputStream();
            } catch (IOException e) {
                Log.e(TAG, "Ocurrió un error al crear el flujo de salida", e);
            }

            mmInStream = tmpIn;
            mmOutStream = tmpOut;
        }

        public void run() {
            mmBuffer = new byte[1024];
            int numBytes;

            while (true) {
                try {
                    numBytes = mmInStream.read(mmBuffer);
                    String readMsg = new String(mmBuffer, 0, numBytes);
                    handler.obtainMessage(handleState, numBytes, -1, readMsg).sendToTarget();
                } catch (IOException e) {
                    Log.d(TAG, "El flujo de entrada se desconectó", e);
                    break;
                }
            }
        }

        public void write(String input) {
            try {
                mmOutStream.write(input.getBytes());
            } catch (IOException e) {
                Log.e(TAG, "Ocurrió un error al enviar datos", e);
            }
        }

        public void cancel() {
            try {
                mmSocket.close();
            } catch (IOException e) {
                Log.e(TAG, "No se pudo cerrar el socket de conexión", e);
            }
        }
    }
}