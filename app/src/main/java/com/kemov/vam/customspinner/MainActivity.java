package com.kemov.vam.customspinner;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

import com.kemov.vam.myspinner.CustomSpinner;

import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity {

    CustomSpinner spinner;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        List<String> list = new ArrayList<>();
        for (int i = 0; i < 5; i++) {
            list.add(""+i);
        }
        spinner = (CustomSpinner) this.findViewById(R.id.spinner);
        spinner.setItems(list);
        spinner.setDefault(3);

    }
}
