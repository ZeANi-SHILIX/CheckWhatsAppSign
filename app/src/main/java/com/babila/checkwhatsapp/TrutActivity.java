package com.babila.checkwhatsapp;

import androidx.appcompat.app.AppCompatActivity;

import android.content.ComponentName;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Toast;


public class TrutActivity extends AppCompatActivity {
    String value = "com.whatsapp";

    private void gotoFmSetting(String sighApp){

        Toast.makeText(this, getString(R.string.openFMsetting)+sighApp,
                Toast.LENGTH_SHORT).show();

        Intent intent = new Intent();//sighApp+".youbasha.ui.YoSettings.AllSettings");
        //intent.setPackage(sighApp);
        intent.setComponent(new ComponentName(sighApp,sighApp+ ".youbasha.ui.YoSettings.AllSettings"));


        try {
            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            startActivity(intent);
        } catch (Exception e){
            Toast.makeText(this, R.string.fail_openFMsetting,
                    Toast.LENGTH_LONG).show();
        }

    }

    public void clicktogo(View v){
        gotoFmSetting(this.value);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_trut);
        getSupportActionBar().setTitle(R.string.title_activity_scrolling);
        Bundle bundle = getIntent().getExtras();
        if(bundle != null) {
            value = bundle.getString("signName");
        }
        //value = getIntent().getStringExtra("signName");
    }


}