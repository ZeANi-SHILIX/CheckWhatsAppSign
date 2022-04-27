package com.babila.checkwhatsapp;

import static android.view.Gravity.CENTER;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;
import androidx.core.view.ViewCompat;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.ComponentName;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.res.Resources;
import android.graphics.Typeface;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.TextView;
import android.widget.LinearLayout.LayoutParams;
import android.widget.Toast;

import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdView;
import com.google.android.gms.ads.MobileAds;
import com.google.android.gms.ads.initialization.InitializationStatus;
import com.google.android.gms.ads.initialization.OnInitializationCompleteListener;
import com.google.android.material.shape.CornerFamily;
import com.google.android.material.shape.MaterialShapeDrawable;
import com.google.android.material.shape.ShapeAppearanceModel;


import org.json.JSONArray;
import org.json.JSONException;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;

import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;


public class MainActivity extends AppCompatActivity {
    ProgressDialog pd;
    String stringfromgit="";
    JSONArray jsonRoot = new JSONArray();

    private boolean isInstalledFromStore(String signPackage){
        String installer = getPackageManager().getInstallerPackageName(signPackage);

        if (installer.equals("com.android.vending") || installer.equals("com.google.android.feedback") ) {
            return true;
        }
        //Toast.makeText(this, installer, Toast.LENGTH_SHORT).show();
        return false;
    }

    private boolean isfouad(String info, String signName){
        String fouad1 = "Fouad",fouad2 = "FMWhatsApp",fouad3 = "YoWhatsApp",fouad4 = "GBWhatsApp" , aero = "Aero";
        double installedVersion = Double.parseDouble(info.replaceAll("[^\\.0123456789]",""));
        if (info.toLowerCase().contains(fouad1.toLowerCase())||info.toLowerCase().contains(fouad2.toLowerCase())||info.toLowerCase().contains(fouad3.toLowerCase())||info.toLowerCase().contains(fouad4.toLowerCase())) {
           return true;
        }
        else if (info.toLowerCase().contains(aero.toLowerCase())) {
            return true;
        }
        return false;
    }

    public void clickMe(View v){
        Intent browserIntent = new Intent(Intent.ACTION_VIEW);
        browserIntent.setData(Uri.parse("http://t.me/shilobabila"));
        startActivity(browserIntent);
    }

    /*public void toAds(View v){
        Intent myIntent = new Intent(MainActivity.this, googleAds.class);
        //myIntent.putExtra("key", value); //Optional parameters
        MainActivity.this.startActivity(myIntent);
    }*/

    public String loadJSONFromAsset() {
        String json = "fail";

        try {
            InputStream is = this.getAssets().open("lastedVersion.json");
            int size = is.available();
            byte[] buffer = new byte[size];
            is.read(buffer);
            is.close();
            json = new String(buffer, "UTF-8");
        } catch (IOException ex) {
            ex.printStackTrace();
            return null;
        }


        Toast.makeText(this, R.string.openLocalJSON,
                Toast.LENGTH_SHORT).show();
        return json;
    }

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

    private boolean isAppInstalled(String packageName) {
        PackageManager pm = getPackageManager();
        boolean installed = false;
        try {
            pm.getPackageInfo(packageName, PackageManager.GET_ACTIVITIES);
            installed = true;
        } catch (PackageManager.NameNotFoundException e) {
            installed = false;
        }
        return installed;
    }

    private String[] checkUpdate(String modType, String modSign , double installedVer){
        String linkToUpdate ="checking";
        double lastetVer =3.2;

        for (int i=0;i< jsonRoot.length();i++){
            try {
                if (modSign.toLowerCase().equals(jsonRoot.getJSONObject(i).getString("Signature").toLowerCase())){
                    lastetVer = jsonRoot.getJSONObject(i).getDouble("Version"+modType);
                    if (lastetVer>installedVer){
                        linkToUpdate = jsonRoot.getJSONObject(i).getString("Version"+modType+"Link");
                    } else {
                        linkToUpdate ="";
                    }
                    //Toast.makeText(this,"Last: "+lastetVer+"\nInstalled: "+installedVer,Toast.LENGTH_LONG).show();

                }


            } catch (JSONException e){
                e.printStackTrace();
            }

        }
        String[] toReturn = {linkToUpdate,lastetVer+""};


        return toReturn;
    }

    private TableRow createButtons(String signName, String info, boolean isStore){
        TableRow row = new TableRow(this);
        row.setLayoutParams(new TableRow.LayoutParams(TableRow.LayoutParams.MATCH_PARENT, TableRow.LayoutParams.WRAP_CONTENT));

        Button checkUpdateButton = new Button(this);
        checkUpdateButton.setText(R.string.checkUpdate);
        checkUpdateButton.setLayoutParams(new LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT));

        checkUpdateButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                //TODO --- add more word
                // todo - move to list
                String fouad1 = "Fouad",fouad2 = "FMWhatsApp",fouad3 = "YoWhatsApp",fouad4 = "GBWhatsApp" , aero = "Aero";
                String[] needUpdate ={"",""};

                double installedVersion = Double.parseDouble(info.replaceAll("[^\\.0123456789]",""));

                // Fouad Version
                // check every name
                if (info.toLowerCase().contains(fouad1.toLowerCase())||info.toLowerCase().contains(fouad2.toLowerCase())||info.toLowerCase().contains(fouad3.toLowerCase())||info.toLowerCase().contains(fouad4.toLowerCase())) {
                    needUpdate = checkUpdate("Fouad", signName, installedVersion);

                }
                // Aero Version
                // the name Aero shown in both signatures
                else if (info.toLowerCase().contains(aero.toLowerCase())) {
                    needUpdate = checkUpdate("Aero", signName, installedVersion);

                }


                // founded update
                if (needUpdate[0]!="") {
                    String message = "במכשיר זה מותקנת הגרסה V"+installedVersion+"\nקיימת גרסה חדשה יותר V"+needUpdate[1]+"\n\nהאם תרצה להוריד את העדכון כעת?";
                    String finalNeedUpdate = needUpdate[0];
                    new AlertDialog.Builder(MainActivity.this)
                            .setTitle(R.string.foundUpdate)
                            .setMessage(message)

                            // Specifying a listener allows you to take an action before dismissing the dialog.
                            // The dialog is automatically dismissed when a dialog button is clicked.
                            .setPositiveButton(android.R.string.yes, new DialogInterface.OnClickListener() {
                                public void onClick(DialogInterface dialog, int which) {
                                    // Check if its URL
                                    try {
                                        Intent browserIntent = new Intent(Intent.ACTION_VIEW);
                                        browserIntent.setData(Uri.parse(finalNeedUpdate));
                                        startActivity(browserIntent);
                                    } catch (Exception e){
                                        e.printStackTrace();
                                        new AlertDialog.Builder(MainActivity.this)
                                                .setTitle(R.string.error)
                                                .setMessage("חלה שגיאה בפתיחת הקישור. \n\nתוכלו לחפש את העדכון בערוץ או לשאול בקבוצת התמיכה.")

                                                // Specifying a listener allows you to take an action before dismissing the dialog.
                                                // The dialog is automatically dismissed when a dialog button is clicked.
                                                .setPositiveButton(android.R.string.yes, new DialogInterface.OnClickListener() {
                                                    public void onClick(DialogInterface dialog, int which) {
                                                        // Continue with delete operation
                                                    }
                                                })

                                                .setIcon(android.R.drawable.stat_notify_error)
                                                .show();
                                    }


                                }
                            })

                            // A null listener allows the button to dismiss the dialog and take no further action.
                            .setNegativeButton(android.R.string.no, null)
                            .setIcon(android.R.drawable.stat_sys_download)
                            .show();
                    checkUpdateButton.setText(getString(R.string.updateTo)+needUpdate[1]);
                }
                // not founded update
                else {
                    new AlertDialog.Builder(MainActivity.this)
                            .setTitle(R.string.noFoundUpdate)
                            .setMessage(R.string.lastedVersionIsInstalled)

                            // Specifying a listener allows you to take an action before dismissing the dialog.
                            // The dialog is automatically dismissed when a dialog button is clicked.
                            .setPositiveButton(android.R.string.yes, new DialogInterface.OnClickListener() {
                                public void onClick(DialogInterface dialog, int which) {
                                    // Continue with delete operation
                                }
                            })

                            .setIcon(android.R.drawable.stat_sys_warning)
                            .show();
                    checkUpdateButton.setText(R.string.noUpdateAvailable);
                }


            }
        });
        checkUpdateButton.setLayoutParams(new TableRow.LayoutParams(TableRow.LayoutParams.WRAP_CONTENT, TableRow.LayoutParams.WRAP_CONTENT));

        Button b = new Button(this);
        b.setText(R.string.moreSetting_FM);

        b.setLayoutParams(new LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT));
        b.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                gotoFmSetting(signName);
            }
        });
        b.setLayoutParams(new TableRow.LayoutParams(TableRow.LayoutParams.WRAP_CONTENT, TableRow.LayoutParams.WRAP_CONTENT));

        Button c = new Button(this);
        c.setText(R.string.moreInfo_btn);
        c.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                Intent myIntent = new Intent(MainActivity.this, TrutActivity.class);
                myIntent.putExtra("signName", signName); //Optional parameters
                MainActivity.this.startActivity(myIntent);

            }

        });
        c.setLayoutParams(new TableRow.LayoutParams(TableRow.LayoutParams.WRAP_CONTENT, TableRow.LayoutParams.WRAP_CONTENT));

        if (info!=""){

            if(!isfouad(info,signName)){
                TextView notfromstore = new TextView(this);
                notfromstore.setGravity(CENTER);
                notfromstore.setText("לא הצלחתי לזהות את סוג הוואטסאפ");
                notfromstore.setTextSize(15);
                row.addView(notfromstore);
            }
            else {
                row.addView(checkUpdateButton);
            }
            row.addView(b);
            row.addView(c);

        }
        //not fouad or aero
        else if (signName.toLowerCase().equals("com.whatsapp")||signName.toLowerCase().equals("com.whatsapp.w4b")) {

            //text or button
            //button when it from store
            if(!isStore){
                TextView notfromstore = new TextView(this);
                notfromstore.setGravity(CENTER);
                notfromstore.setText("לא הצלחתי לזהות את סוג הוואטסאפ");
                notfromstore.setTextSize(15);
                row.addView(notfromstore);
            }
            else {
                Button store = new Button(this);
                store.setText(R.string.officalPackage_goToStore_btn);
                store.setLayoutParams(new LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT));
                store.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        Intent browserIntent = new Intent(Intent.ACTION_VIEW);
                        browserIntent.setData(Uri.parse("https://play.google.com/store/apps/details?id=" + signName));
                        startActivity(browserIntent);
                    }
                });
                store.setLayoutParams(new TableRow.LayoutParams(TableRow.LayoutParams.WRAP_CONTENT, TableRow.LayoutParams.WRAP_CONTENT));
                row.addView(store);
            }
            Button tutorialButton = new Button(this);
            tutorialButton.setText("מדריך מעבר\nלוואטסאפ אחר");
            tutorialButton.setLayoutParams(new LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT));
            tutorialButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    Intent browserIntent = new Intent(Intent.ACTION_VIEW);
                    browserIntent.setData(Uri.parse("https://telegra.ph/%D7%92%D7%99%D7%91%D7%95%D7%99-%D7%95%D7%94%D7%97%D7%9C%D7%A4%D7%AA-%D7%97%D7%AA%D7%99%D7%9E%D7%94---%D7%91%D7%9B%D7%9C-%D7%92%D7%A8%D7%A1%D7%AA-%D7%95%D7%95%D7%90%D7%98%D7%A1%D7%90%D7%A4-03-08"));
                    startActivity(browserIntent);
                }
            });
            tutorialButton.setLayoutParams(new TableRow.LayoutParams(TableRow.LayoutParams.WRAP_CONTENT, TableRow.LayoutParams.WRAP_CONTENT));

            row.addView(tutorialButton);
        }




        row.setGravity(CENTER);

        return row;
    }
    private TableRow padding(int x){
        TableRow pad = new TableRow(this);
        pad.setPadding(0,x,0,1);
        return pad;
    }

    private TextView makeTitle(String text,int color){
        TextView textView1 = new TextView(this);

        textView1.setText(text);
        textView1.setTextSize(22);
        textView1.setTypeface(null,Typeface.BOLD);

        //rounded corner
        ShapeAppearanceModel shapeAppearanceModel = new ShapeAppearanceModel()
                .toBuilder()
                .setAllCorners(CornerFamily.ROUNDED,16)
                .build();
        MaterialShapeDrawable shapeDrawable = new MaterialShapeDrawable(shapeAppearanceModel);
        //color
        shapeDrawable.setFillColor(ContextCompat.getColorStateList(this,color));
        ViewCompat.setBackground(textView1,shapeDrawable);

        textView1.setPadding(10, 20, 10, 20);// in pixels (left, top, right, bottom)
        textView1.setGravity(CENTER);

        return textView1;
    }

    private String get_FM_Info(String appSign){
        String x="";

        Resources res = null;
        try {
            res = getPackageManager().getResourcesForApplication(appSign);
            //Toast.makeText(this ,"לפני TRY",Toast.LENGTH_SHORT).show();
        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
        }
        if(null != res) {
            int sId = res.getIdentifier(appSign+":string/yowav", null, null);
            if(0 != sId) {
                x= res.getString(sId);
            }
        }

        return x;
    }

    @SuppressLint("ResourceAsColor")
    private void makeActivityMain(String result){
        TableLayout tl = (TableLayout) findViewById(R.id.tablelayout);
        JSONArray jsonRoot = new JSONArray();
        TextView source = findViewById(R.id.source);
        boolean isFromStore = false;


        try {
            if (result!=null){
                jsonRoot = new JSONArray(result);
                source.setText(R.string.json_github_info);
            }
            else
                {
                jsonRoot = new JSONArray(loadJSONFromAsset());
                source.setText(R.string.json_local_info);
            }
            this.jsonRoot = jsonRoot;
        } catch (JSONException e) {
            e.printStackTrace();
        }

        /*
        * Update the jsonRoot - which app installed, and count them.
        * and catch the first
        */
        int counterSigh = 0;
        String Signature ="",first="";

        for (int i =0; i < jsonRoot.length();i++){
            try {
                Signature = jsonRoot.getJSONObject(i).getString("Signature");
                boolean doExist = isAppInstalled(Signature);
                if (doExist) {
                    jsonRoot.getJSONObject(i).put("InstalledFlag", true);
                    counterSigh++;
                    if (counterSigh==1) {
                        first = Signature;
                    }

                    // check if the app installed from google play
                    if (isInstalledFromStore(Signature)){
                        jsonRoot.getJSONObject(i).put("InstalledFromStore", true);
                    }
                    isFromStore = Boolean.parseBoolean(jsonRoot.getJSONObject(i).getString("InstalledFromStore"));
                }
            } catch (JSONException e) {
                e.printStackTrace();
            }

        }

        //found only one
        if (counterSigh==1){
            tl.addView(padding(10));
            tl.addView(makeTitle(getString(R.string.foundOneAppWA),R.color.found1));

            tl.addView(padding(50));
            tl.addView(makeTitle(getString(R.string.foundOne_Sign)+first,R.color.found1sign));

            String info = get_FM_Info(first);
            if (info!=""){
                tl.addView(padding(10));
                TextView tvInfo = makeTitle(info,R.color.found_more_sign);
                tvInfo.setTextColor(R.color.black);
                tl.addView(tvInfo);
            }

            tl.addView(createButtons(first,info, isFromStore),new TableLayout.LayoutParams(TableLayout.LayoutParams.MATCH_PARENT, TableLayout.LayoutParams.WRAP_CONTENT));
        }
        //not found
        else if (counterSigh==0){
            tl.addView(padding(30));
            tl.addView(makeTitle(getString(R.string.noWA_onDevice),R.color.red_error));
        }
        //found more then one
        else if (counterSigh>1){
            tl.addView(padding(20));
            tl.addView(makeTitle(getString(R.string.moreThenOneWAInstalled), R.color.foundmore));
            tl.addView(padding(50));

            int i=0;
            boolean doexist = false;
            String exist="";
            for (i=0;i< jsonRoot.length();i++){
                try {
                    doexist = jsonRoot.getJSONObject(i).getBoolean("InstalledFlag");
                    if (doexist){
                        Signature = jsonRoot.getJSONObject(i).getString("Signature");
                        String info = get_FM_Info(Signature);

                        //tl.addView(makeTitle(ListWaExist[i]+"",R.color.found_more_sign));
                        tl.addView(makeTitle(Signature,R.color.foundmorelist));
                        tl.addView(padding(10));

                        //Toast.makeText(this ,info, Toast.LENGTH_SHORT).show();
                        if (info!="") {
                            TextView tvInfo = makeTitle(info, R.color.found_more_sign);
                            tvInfo.setTextColor(R.color.black);
                            tl.addView(tvInfo);

                        }
                        isFromStore = Boolean.parseBoolean(jsonRoot.getJSONObject(i).getString("InstalledFromStore"));
                        tl.addView(createButtons(Signature, info, isFromStore));
                        tl.addView(padding(70));
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }


            }

        }


        //tl.addView(makeTitle("Number "+counterSigh, R.color.found1));

    }


    @SuppressLint("ResourceAsColor")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        TableLayout tl = (TableLayout) findViewById(R.id.tablelayout);

        String url ="https://raw.githubusercontent.com/ZeANi-SHILIX/UpdatedWALink/main/lastedVersion.json";
        JsonTask exp = new JsonTask();

        exp.execute(url);

        try {
            MobileAds.initialize(this, new OnInitializationCompleteListener() {
                @Override
                public void onInitializationComplete(InitializationStatus initializationStatus) {
                }
            });
            //new RequestConfiguration.Builder().setTestDeviceIds(Arrays.asList("FCFD9D49C69A486548C342E1A02C5856"));

            AdView adView = findViewById(R.id.adView);
            AdRequest adRequest = new AdRequest.Builder().build();
            adView.loadAd(adRequest);
        } catch (Exception e){
            Log.d("ADS: ","fail to show ads");
        }



        /*AdView adView = new AdView(this);

        adView.setAdSize(AdSize.BANNER);

        adView.setAdUnitId("ca-app-pub-6474275493517014/6855159833");*/


    }




    private class JsonTask extends AsyncTask<String, String, String> {
        private String value ="test2";


        protected void onPreExecute() {
            super.onPreExecute();

            pd = new ProgressDialog(MainActivity.this);
            pd.setMessage(getString(R.string.waitforjsonload));
            pd.setCancelable(false);
            pd.show();
        }

        protected String doInBackground(String... params) {
            HttpURLConnection connection = null;
            BufferedReader reader = null;

            try {
                URL url = new URL(params[0]);
                connection = (HttpURLConnection) url.openConnection();
                connection.connect();
                InputStream stream = connection.getInputStream();
                reader = new BufferedReader(new InputStreamReader(stream));
                StringBuffer buffer = new StringBuffer();
                String line = "";
                while ((line = reader.readLine()) != null) {
                    buffer.append(line + "\n");
                    //Log.d("Response: ", "> " + line);   //here u ll get whole response...... :-)
                }
                return buffer.toString();
            } catch (MalformedURLException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            } finally {
                if (connection != null) {
                    connection.disconnect();
                }
                try {
                    if (reader != null) {
                        reader.close();
                    }
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
            return null;
        }

        @Override
        protected void onPostExecute(String result) {
            super.onPostExecute(result);

            makeActivityMain(result);

            /*
            try {
                JSONObject json_web = new JSONObject(result);

                //load json from local
                JSONObject json_local = new JSONObject(loadJSONFromAsset());

                //json from web


                String text = json_web.getString(value);
                //String allText = jsonObject.toString();
                test.setText(text);






            } catch (JSONException e) {
                e.printStackTrace();
            }*/
            if (pd.isShowing()) {
                pd.dismiss();
            }
        }
    }

}

