package com.example.arduinoproject;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;

public class InitActivity extends AppCompatActivity {
    Button jcontinue;
    CheckBox jcheckinit;
    SharedPreferences preferences;
    SharedPreferences.Editor editor;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_init);

        preferences = this.getPreferences(Context.MODE_PRIVATE);
        editor = preferences.edit();

        jcontinue = findViewById(R.id.xcontinue);
        jcheckinit = findViewById(R.id.xcheckbox_init);

        if(checkPreferences()){
            nextStep();
        }

        jcontinue.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                savePreferences(jcheckinit.isChecked());
                nextStep();
            }
        });
    }

    private Boolean checkPreferences() {
        return this.preferences.getBoolean("show_again", false);
    }

    private void savePreferences(Boolean checked){
        editor.putBoolean("show_again", checked);
        editor.apply();
    }

    private void nextStep(){
        Intent next = new Intent(getApplicationContext(), LinkedDevices.class);
        startActivity(next);
        finish();
    }
}