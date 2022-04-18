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
import android.util.Log;

import com.google.android.material.bottomnavigation.BottomNavigationView;

import java.io.IOException;
import java.util.UUID;

public class MainActivity extends AppCompatActivity{
    BottomNavigationView bottomNavigationView;
    private BluetoothAdapter bluetoothAdapter;
    private BluetoothDevice bluetoothDevice;
    public static String address;
    ConnectThread connectThread;
    private static final UUID btModuleUUID = UUID.fromString("00001105-0000-1000-8000-00805F9B34FB");

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        bottomNavigationView = findViewById(R.id.bottom_navigation_view);
        bluetoothAdapter = BluetoothAdapter.getDefaultAdapter();

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
    }

    private class ConnectThread extends Thread {
        private final BluetoothSocket mmSocket;
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
}