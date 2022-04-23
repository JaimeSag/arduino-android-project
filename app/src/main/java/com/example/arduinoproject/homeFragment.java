package com.example.arduinoproject;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import android.text.SpannableString;
import android.text.Spanned;
import android.text.style.ForegroundColorSpan;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

public class homeFragment extends Fragment {
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view =inflater.inflate(R.layout.fragment_home, container, false);
        return view;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        TextView ftitle = view.findViewById(R.id.home_title);
        String titleText = ftitle.getText().toString();

        SpannableString aux = new SpannableString(titleText);
        ForegroundColorSpan bluecolor = new ForegroundColorSpan(getResources().getColor(R.color.blue_primary));
        aux.setSpan(bluecolor, 8, 15, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);

        ftitle.setText(aux);
    }
}