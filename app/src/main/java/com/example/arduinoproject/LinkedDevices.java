package com.example.arduinoproject;

import androidx.appcompat.app.AppCompatActivity;
import android.annotation.SuppressLint;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;
import com.airbnb.lottie.LottieAnimationView;
import com.airbnb.lottie.LottieDrawable;

import java.util.Set;

public class LinkedDevices extends AppCompatActivity {
    static String EXTRA_DEVICE_ADDRESS = "";
    private BluetoothAdapter bluetoothAdapter;
    private ArrayAdapter mParDevices;
    ListView listaDispositivos;
    Button jitemSelected;
    LottieAnimationView btAnimation;
    String name, mac;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_linked_devices);

        bluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
        listaDispositivos = findViewById(R.id.list_devices);
        btAnimation = findViewById(R.id.bt_animation);
        jitemSelected = findViewById(R.id.bt_item_selected);

        jitemSelected.setEnabled(false);

        if (bluetoothAdapter == null) Log.e("ERROR", "No hay dispositivos bluetooth");

        listaDispositivos.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @SuppressLint("MissingPermission")
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                view.setSelected(true);
                name = ((TextView) view).getText().toString();
                mac = name.substring(name.length() - 17);
                jitemSelected.setEnabled(true);
            }
        });

        jitemSelected.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finishAffinity();
                Intent inconn = new Intent(LinkedDevices.this, MainActivity.class);
                inconn.putExtra(EXTRA_DEVICE_ADDRESS, mac);
                startActivity(inconn);
            }
        });
    }

    @Override
    protected void onResume() {
        super.onResume();
        bluetoothStatus();
        getListLinkedDevices();
    }

    @SuppressLint("MissingPermission")
    private void getListLinkedDevices() {
        btAnimation.playAnimation();
        btAnimation.setRepeatCount(LottieDrawable.INFINITE);
        mParDevices = new ArrayAdapter(this, R.layout.devices_found);
        listaDispositivos.setAdapter(mParDevices);

        Set<BluetoothDevice> pairedDevices = bluetoothAdapter.getBondedDevices();

        if (pairedDevices.size() > 0)
            for (BluetoothDevice device : pairedDevices)
                mParDevices.add(device.getName() + "\n" + device.getAddress());
        else
            mParDevices.add("No hay dispositivos vinculados. Asegúrate de vincular el módulo bluetooth.");
    }

    @SuppressLint("MissingPermission")
    public void bluetoothStatus(){
        if(!bluetoothAdapter.isEnabled()){
            Intent enableBtIntent = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
            startActivityForResult(enableBtIntent, 0);
        }
    }
}