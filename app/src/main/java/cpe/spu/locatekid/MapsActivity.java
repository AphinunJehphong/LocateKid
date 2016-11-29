package cpe.spu.locatekid;

import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.support.v4.app.FragmentActivity;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.View;
import android.view.Window;
import android.view.WindowAnimationFrameStats;
import android.webkit.WebView;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;


import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.squareup.okhttp.FormEncodingBuilder;
import com.squareup.okhttp.OkHttpClient;
import com.squareup.okhttp.Request;
import com.squareup.okhttp.RequestBody;
import com.squareup.okhttp.Response;
import com.squareup.picasso.Picasso;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.Map;

public class MapsActivity extends FragmentActivity implements OnMapReadyCallback {

    private GoogleMap mMap;
    private String[] studentStrings;
    private static final String urlTeacher = "http://swiftcodingthai.com/golf1/get_userteacher.php";
    private Button refreshButton;

    //Display Job
    private TextView timeinTextView, timeoutTextView, dateTextView;
    private ImageView statImageView;




    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_maps);

        //Get instant delay refresh
        //getInstance();

        //Widget
        timeinTextView = (TextView) findViewById(R.id.textView20);
        timeoutTextView = (TextView) findViewById(R.id.textView21);
        dateTextView = (TextView) findViewById(R.id.textView23);


        refreshButton = (Button) findViewById(R.id.button5);


        //Imageview
        statImageView = (ImageView) findViewById(R.id.imageView9);

        //get ค่าจาก intent ที่แล้วมาใช้
        studentStrings = getIntent().getStringArrayExtra("GetIDStudent"); //นำค่าจากหน้าที่แล้วมาจาก putextra

        //นำค่าที่ได้ไปใช้ใน class getjob
         getJoblist(studentStrings[0]);

        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);

        refreshButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                finish();
                startActivity(getIntent());
            }
        });




    } // Main Method

    @Override
    protected void onRestart() {
        super.onRestart();

        finish();
        startActivity(getIntent());

        }


    private void getJoblist(String studentString){

        showJoblist showJoblist = new showJoblist(MapsActivity.this);
        showJoblist.execute(studentString);

    }//getJoblist class

    private class showJoblist extends AsyncTask<String, Void, String> {

        //ประกาศตัวแปร
        private Context context;
        private static final String urlJoblist = "http://swiftcodingthai.com/golf1/get_joblist.php";
        private String ID_StudentString;
        private boolean aBoolean = true;
        private String[] resultStrings;
        private String[] columnResult = new String[]{
                "id",
                "CurrentDate",
                "Time_in",
                "Time_out",
                "ID_Student",
                "ID_Teacher",
                "Stat_stu"};


        //Constructor
        public showJoblist(Context context) {
            this.context = context;
        }


        @Override
        protected String doInBackground(String... strings) {

            ID_StudentString = strings[0];

            try {

                OkHttpClient okHttpClient = new OkHttpClient();
                Request.Builder builder = new Request.Builder();
                Request request = builder.url(urlJoblist).build();
                Response response = okHttpClient.newCall(request).execute();
                return response.body().string();

            } catch (Exception e) {
                Log.d("TestJoblist", "e doInBack ==> " + e.toString());
                return null;
            }

        }//doInBack

        @Override
        protected void onPostExecute(String s) {
            super.onPostExecute(s);

            Log.d("TestJoblist", "JSON ==> " + s);
            Log.d("TestJoblist", "ID_Student ==> " + ID_StudentString);

            try {

                JSONArray jsonArray = new JSONArray(s);
                resultStrings = new String[columnResult.length];

                for (int i=0; i < jsonArray.length(); i++) {

                    JSONObject jsonObject = jsonArray.getJSONObject(i);

                    if (ID_StudentString.equals(jsonObject.getString(columnResult[4]))) {
                        Log.d("TestJoblist", "ID_StudentString OK");
                        aBoolean = false;
                        for (int i1 = 0; i1 < columnResult.length; i1++) {

                            resultStrings[i1] = jsonObject.getString(columnResult[i1]);
                            Log.d("TestJoblist", "resultStrings(" + i1 + ") = " + columnResult[i1]);

                        }
                    }
                }

                if (aBoolean) {

                    Toast.makeText(MapsActivity.this,
                            "รถโรงเรียนยังไม่ทำการใช้งาน หรือ ยังไม่ถึงเวลาที่กำหนด",Toast.LENGTH_LONG).show();


                } else {

                    Log.d("TestJoblist", "Result " + ID_StudentString + " OK");


                        dateTextView.setText(resultStrings[1]);
                        timeinTextView.setText("Time In : " + resultStrings[2]);
                        timeoutTextView.setText("Time Out : " + resultStrings[3]);


                        switch (resultStrings[6]) {

                            case "0":
                                Picasso.with(context).load(R.drawable.stat0).into(statImageView);
                                Toast.makeText(MapsActivity.this,
                                        "นักเรียนได้ทำการลงจากรถเรียบร้อยแล้ว", Toast.LENGTH_LONG).show();
                                break;

                            case "1":
                                Picasso.with(context).load(R.drawable.stat1).into(statImageView);
                                Toast.makeText(MapsActivity.this,
                                        "นักเรียนได้ทำการขึ้นรถเรียบร้อยแล้ว", Toast.LENGTH_LONG).show();
                                        break;

                            case "2":
                                Picasso.with(context).load(R.drawable.stat2).into(statImageView);
                                Toast.makeText(MapsActivity.this,
                                        "คุณครูได้ทำการลาป่วย/ไม่มาโรงเรียนเรียบร้อยแล้ว", Toast.LENGTH_LONG).show();
                                break;


                        }

                    }

                //นำค่าที่มีใน class นี้ไปใช้ยังอีกคลาสตามต้องการ
                getGPS(resultStrings[5]);

            } catch (Exception e) {
                e.printStackTrace();
            }

        }//onPost

    }//ShowJoblist class

    private void getGPS (String resultString) {

        ShowGPS showGPS = new ShowGPS(MapsActivity.this);
        showGPS.execute(resultString);

    }//getGPS class


    private class ShowGPS extends AsyncTask <String, Void, String> {

        //ประกาศตัวแปร
        private Context context;
        private static final String urlTeacher = "http://swiftcodingthai.com/golf1/get_userteacher.php";
        private String ID_TeacherString;
        private boolean aBoolean = true;
        private String[] teacherStrings;
        private String[] columnTeacher = new String[]{
                "ID_Teacher",
                "Name_Teacher",
                "Sur_Teacher",
                "Tel_Teacher",
                "Pic_Teacher",
                "Username",
                "Password",
                "Lat",
                "Lng"};


        //Constructor
        public ShowGPS(Context context) {
            this.context = context;
        }

        @Override
        protected String doInBackground(String... strings) {

            ID_TeacherString = strings[0];

            try {

                OkHttpClient okHttpClient = new OkHttpClient();
                Request.Builder builder = new Request.Builder();
                Request request = builder.url(urlTeacher).build();
                Response response = okHttpClient.newCall(request).execute();
                return response.body().string();


            } catch (Exception e) {
                Log.d("TestJoblist", "e doInBack ==> " + e.toString());
                return null;
            }
        }//doInBack


        @Override
        protected void onPostExecute(String s) {
            super.onPostExecute(s);

            Log.d("TestJoblist", "JSON ==> " + s);
            Log.d("TestJoblist", "ID_Teacher ==> " + ID_TeacherString);

            try {

                JSONArray jsonArray = new JSONArray(s);
                teacherStrings = new String[columnTeacher.length];

                for (int i = 0; i < jsonArray.length(); i++) {

                    JSONObject jsonObject = jsonArray.getJSONObject(i);

                    if(ID_TeacherString.equals(jsonObject.getString(columnTeacher[0]))) {
                        Log.d("TestJoblist", "ID_TeacherString OK");
                        aBoolean = false;
                        for (int i1 = 0; i1 < columnTeacher.length; i1++) {

                            teacherStrings[i1] = jsonObject.getString(columnTeacher[i1]);
                            Log.d("TestJoblist", "teacherStrings(" + i + ") ==> " + columnTeacher[i1]);

                        }
                    }
                }

                if (aBoolean) {

                    Toast.makeText(MapsActivity.this,
                            "รถโรงเรียนยังไม่ทำการใช้งาน หรือ ยังไม่ถึงเวลาที่กำหนด",Toast.LENGTH_LONG).show();

                } else {
                    //Create Marker พิกัดของครูที่อยู่บนรถรับส่งนักเรียนของผู้ปกครองมาแสดงให้ผู้ปกครอง
                    Log.d("TestJoblist", "GPSGO ==> " + teacherStrings[7]);
                    Log.d("TestJoblist", "GPSGO ==> " + teacherStrings[8]);



                    LatLng getGPS = new LatLng(Double.parseDouble(teacherStrings[7]),
                            Double.parseDouble(teacherStrings[8]));
                    mMap.moveCamera(CameraUpdateFactory.newLatLng(getGPS));
                    mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(getGPS, 17));

                    //Create Marker
                    MarkerOptions markerOptions = new MarkerOptions()
                            .position(new LatLng(Double.parseDouble(teacherStrings[7]),
                                    Double.parseDouble(teacherStrings[8])))
                            .title("Bus " + teacherStrings[1]);
                    markerOptions.icon(BitmapDescriptorFactory.fromResource(R.drawable.bus_icon));
                    mMap.addMarker(markerOptions);
                }


            } catch (Exception e) {

                 Log.d("TestJoblist", "e ==> " + e.toString());
                    Toast.makeText(MapsActivity.this,
                    "ไม่สามารถหาพิกัดได้",Toast.LENGTH_LONG).show();

            }

        }//onPost



    }//ShowGPS class









    /**
     * Manipulates the map once available.
     * This callback is triggered when the map is ready to be used.
     * This is where we can add markers or lines, add listeners or move the camera. In this case,
     * we just add a marker near Sydney, Australia.
     * If Google Play services is not installed on the device, the user will be prompted to install
     * it inside the SupportMapFragment. This method will only be triggered once the user has
     * installed Google Play services and returned to the app.
     */
    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;

        //Log.d("TestGPS", "getLAT ==>" + );
        //Log.d("TestGPS", "getLNG" + );

       /* try {
            // Add a marker in Sydney and move the camera
            LatLng SchoolBus = new LatLng(13.8492258,100.5794144);
            mMap.moveCamera(CameraUpdateFactory.newLatLng(SchoolBus));
            mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(SchoolBus, 16));

            //Create Marker Bus
            mMap.addMarker(new MarkerOptions()
                    .position(SchoolBus)
                    .icon(BitmapDescriptorFactory.fromResource(R.drawable.van))
                    .title("Bus"));
            } catch (Exception e) {

                Log.d("TestGPS", "e ==> " + e.toString());
                Toast.makeText(MapsActivity.this,
                        "ไม่สามารถหาพิกัดได้",
                        Toast.LENGTH_SHORT).show();
            }*/
    }//onMapReady class
}  // main Class
