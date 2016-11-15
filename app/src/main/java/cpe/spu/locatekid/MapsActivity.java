package cpe.spu.locatekid;

import android.content.Context;
import android.os.AsyncTask;
import android.support.v4.app.FragmentActivity;
import android.os.Bundle;
import android.util.Log;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;


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

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.Map;

public class MapsActivity extends FragmentActivity implements OnMapReadyCallback {

    private GoogleMap mMap;
    private ImageView imageView;
    private String[] teacherStrings;
    private static final String urlTeacher = "http://swiftcodingthai.com/golf1/get_userteacher.php";
    private LatLng latlng;




    //Display Job
    private TextView timeinTextView, timeoutTextView, dateTextView;
    private ImageView statTextView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_maps);

        //Widget
        timeinTextView = (TextView) findViewById(R.id.textView20);
        timeoutTextView = (TextView) findViewById(R.id.textView21);
        dateTextView = (TextView) findViewById(R.id.textView23);


        //Imageview
        imageView = (ImageView) findViewById(R.id.imageView9);

        /*//Get Value From JSON
        constantsActivity = new ConstantsActivity();
        GetJob getJob = new GetJob(MapsActivity.this);
        getJob.execute(constantsActivity.getUrlGetPassengerWhereID());*/


        showlocation();


        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);



    } // Main Method

    private void showlocation() {

        Getlocation getlocation = new Getlocation(MapsActivity.this);
        getlocation.execute();
    }


    private class Getlocation extends AsyncTask<String, Void, String> {

        //ประกาศตัวแปร
        private Context context;
        private static final String urlStuimage = "http://swiftcodingthai.com/golf1/get_latlng_teacher_where_id.php";
        private String[] teacherStrings;
        private String[] columnStrings = new String[] {"ID_Teacher", "Name_Teacher", "Sur_Teacher", "Tel_Teacher",
                "Pic_Teacher", "Username", "Password", "Lat", "Lng"};


        public Getlocation(Context context) {
            this.context = context;
        }

        @Override
        protected String doInBackground(String... strings) {

            try {

                OkHttpClient okHttpClient = new OkHttpClient();
                RequestBody requestBody = new FormEncodingBuilder()
                        .add("isAdd", "true")
                        .add("ID_Teacher", teacherStrings[0])
                        .build();
                Request.Builder builder = new Request.Builder();
                Request request = builder.url(strings[0]).post(requestBody).build();
                Response response = okHttpClient.newCall(request).execute();
                return response.body().string();

            } catch (Exception e) {
                Log.d("TestGPS", "e doIN ==> " + e.toString());
                e.printStackTrace();
                return null;
            }
        }//doInback

        @Override
        protected void onPostExecute(String s) {
            super.onPostExecute(s);

            Log.d("TestGPS", "Result ==> " + s);

            try {

                JSONArray jsonArray = new JSONArray(s);
                columnStrings = new String[columnStrings.length];

                for (int i=0; i < columnStrings.length; i++) {

                    JSONObject jsonObject = jsonArray.getJSONObject(0);
                    columnStrings[i] = jsonObject.getString(columnStrings[i]);
                    Log.d("TestGPS", "columnString (" + i + ") ==> " + columnStrings[i]);

                }


                Log.d("TestGPS", "lat ==> " + columnStrings[7]);
                LatLng getLatLng = new LatLng(Double.parseDouble(columnStrings[7]),
                        Double.parseDouble(columnStrings[8]));
                mMap.addMarker(new MarkerOptions()
                        .position(getLatLng)
                        .icon(BitmapDescriptorFactory.fromResource(R.drawable.van))
                        .title("Bus"));



            } catch (Exception e) {
                Log.d("TestGPS", "e ==> " + e.toString());
            }

        }//onPost
    }//Getlocation class




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

        try {
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
            }
    }
}  // main Class