package com.example.a.aviator2019_aircliques;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;

public class collectData extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.collect_data);
    }

    public void backMainActivity(View view) {

        Intent mainActivity = new Intent(collectData.this, MainActivity.class);
        mainActivity.putExtra("key", "test");
        collectData.this.startActivity(mainActivity);


    }
}
