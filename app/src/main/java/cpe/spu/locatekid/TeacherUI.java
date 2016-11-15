package cpe.spu.locatekid;

import android.app.AlertDialog;
import android.app.PendingIntent;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.database.Cursor;
import android.location.Criteria;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.net.Uri;
import android.nfc.FormatException;
import android.nfc.NdefMessage;
import android.nfc.NdefRecord;
import android.nfc.NfcAdapter;
import android.nfc.Tag;
import android.nfc.tech.Ndef;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Handler;
import android.os.Parcelable;
import android.os.StrictMode;
import android.provider.MediaStore;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.squareup.okhttp.FormEncodingBuilder;
import com.squareup.okhttp.OkHttpClient;
import com.squareup.okhttp.Request;
import com.squareup.okhttp.RequestBody;
import com.squareup.okhttp.Response;
import com.squareup.picasso.Picasso;

import org.jibble.simpleftp.SimpleFTP;
import org.json.JSONArray;
import org.json.JSONObject;

import java.io.File;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.jar.Manifest;


public class TeacherUI extends AppCompatActivity implements View.OnClickListener {

    //ประกาศตัวแปร
    private TextView nameTextView, surnameTextView, phoneTextView;
    private ImageView avatarImageView;
    private String[] loginStrings, myStudentStrings;
    private String imagePathString, imageNameString;
    private static final String urlPHP = "http://swiftcodingthai.com/golf1/edit_image_teacher.php";
    private Button buttonexit, buttonwritenfc, buttonlist;

    //ประกาศตัวแปร NFC
    public static final String ERROR_DETECTED = "No NFC tag detected!";
    public static final String WRITE_SUCCESS = "Text written to the NFC tag successfully!";
    public static final String WRITE_ERROR = "Error during writing, is the NFC tag close enough to your device?";
    NfcAdapter nfcAdapter;
    PendingIntent pendingIntent;
    IntentFilter writeTagFilters[];
    boolean writeMode;
    Tag myTag;
    Context context;

    //private TextView message;
    //private Button btnWrite;

    //Display เด็กนักเรียน
    private TextView namestuTextView, surstuTextView, classTextView, addressTextView;
    private ImageView studentImageView;

    //For check student
    private RadioGroup radioGroup;
    private RadioButton inRadioButton, outRadioButton;
    private String currentDateString;



    //For Get location teacher
    private LocationManager locationManager;
    private Criteria criteria;
    private double latADouble, lngADouble;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_teacher_ui);

        //Widget
        nameTextView = (TextView) findViewById(R.id.textView8);
        surnameTextView = (TextView) findViewById(R.id.textView9);
        phoneTextView = (TextView) findViewById(R.id.textView10);
        avatarImageView = (ImageView) findViewById(R.id.imageView3);
        buttonexit = (Button) findViewById(R.id.button8);
        buttonwritenfc = (Button) findViewById(R.id.button4);
        buttonlist = (Button) findViewById(R.id.button3);
        namestuTextView = (TextView) findViewById(R.id.textView11);
        surstuTextView = (TextView) findViewById(R.id.textView12);
        classTextView = (TextView) findViewById(R.id.textView13);
        addressTextView = (TextView) findViewById(R.id.textView14);
        studentImageView = (ImageView) findViewById(R.id.imageView4);
        radioGroup = (RadioGroup) findViewById(R.id.ragCheck);
        inRadioButton = (RadioButton) findViewById(R.id.radioButton3);
        outRadioButton = (RadioButton) findViewById(R.id.radioButton4);

        //Set location
        locationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE); //ขอใช้เซอร์วิส โลเคชั่นทีอยู่
        criteria = new Criteria(); //
        criteria.setAccuracy(Criteria.ACCURACY_FINE);
        criteria.setAltitudeRequired(false);
        criteria.setBearingRequired(false); //ตัดการค้นหาารอ้างอิงจากความเปรียบเทียบลึกสูงของระดับน้ำทะเล หรือ แกน z

        //Check Student
        radioGroup.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup radioGroup, int i) {

                switch (i) {

                    case R.id.radioButton3: //ขึ้นรถ
                        myAlertCheck(1);
                        break;
                    case R.id.radioButton4: //ลงรถ
                        myAlertCheck(0);
                        break;
                }//switch

            }// onCheck
        });


        //ประกาศใช้ adapter ในหน้านี้
        nfcAdapter = NfcAdapter.getDefaultAdapter(TeacherUI.this);

        //get ค่าจาก intent ที่แล้วมาใช้
        loginStrings = getIntent().getStringArrayExtra("Loginteacher"); //นำค่าจากหน้าที่แล้วมาจาก putextra

        //เช็ค image ว่ารูปมีไหม
        if (loginStrings[4].length() != 0) {
            loadImageAvatar(loginStrings[0]); //เช็คความยาวของตัวอักษรแล้วเทียบถ้ามีให้ส่งลง

        }// if

        //นำค่าจาก database มาโชว์ตามจุดที่ต้องการ
        nameTextView.setText("ชื่อ : " + loginStrings[1]);
        surnameTextView.setText("นามสกุล : " + loginStrings[2]);
        phoneTextView.setText("Phone : " + loginStrings[3]);


        //Image Controller
        avatarImageView.setOnClickListener(this);

        buttonexit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent goexit = new Intent(TeacherUI.this, MainActivity.class);
                startActivity(goexit);
                finish();
            }
        });

        buttonwritenfc.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent goWrite = new Intent(TeacherUI.this, Writetag.class);
                startActivity(goWrite);
            }
        });

        buttonlist.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent golist = new Intent(TeacherUI.this, Showliststudent.class);
                startActivity(golist);
            }
        });


        //NFC mode

        context = this;

        nfcAdapter = NfcAdapter.getDefaultAdapter(this);
        if (nfcAdapter == null) {
            // Stop here, we definitely need NFC
            Toast.makeText(this, "This device doesn't support NFC.", Toast.LENGTH_LONG).show();
            finish();
        }
        readFromIntent(getIntent());

        pendingIntent = PendingIntent.getActivity(this, 0, new Intent(this, getClass()).addFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP), 0);
        IntentFilter tagDetected = new IntentFilter(NfcAdapter.ACTION_TAG_DISCOVERED);
        tagDetected.addCategory(Intent.CATEGORY_DEFAULT);
        writeTagFilters = new IntentFilter[] { tagDetected };

        //loop
        myloop();



    }   // Main Method



    private class EditLocation extends AsyncTask<String, Void, String> {

        //ประกาศตัวแปร
        private Context context;
        private String idTeacherString, latString, lngString;

        //constuctor
        public EditLocation(Context context,
                            String idTeacherString,
                            String latString,
                            String lngString) {
            this.context = context;
            this.idTeacherString = idTeacherString;
            this.latString = latString;
            this.lngString = lngString;
        }

        @Override
        protected String doInBackground(String... params) {

            try {

                OkHttpClient okHttpClient = new OkHttpClient();
                RequestBody requestBody = new FormEncodingBuilder()
                        .add("isAdd", "true")
                        .add("ID_Teacher", idTeacherString)
                        .add("Lat", latString)
                        .add("Lng", lngString)
                        .build();
                Request.Builder builder = new Request.Builder();
                Request request = builder.url(params[0]).post(requestBody).build();
                Response response = okHttpClient.newCall(request).execute();
                return response.body().string();

            } catch (Exception e){
                e.printStackTrace();
                return null;
            }

        }//doInback

        @Override
        protected void onPostExecute(String s) {
            super.onPostExecute(s);

            Log.d("21OctV3", "Result ==> " + s);
        }//onPost

    } //edit Location

    private void myloop() {

        Log.d("21OctV3", "lat ==> " + latADouble);
        Log.d("21OctV3", "lng ==> " + lngADouble);

        String urlEdit = "http://swiftcodingthai.com/golf1/edit_teacher.php";

        //to do
        EditLocation editLocation = new EditLocation(TeacherUI.this,
                loginStrings[0], Double.toString(latADouble), Double.toString(lngADouble));
        editLocation.execute(urlEdit);

        Handler handler = new Handler();
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                myloop();
            }
        },3000);

    }//myloop ให้วนค่าการสร้างเธดเพื่อโยนค่าไปยัง database


    @Override
    protected void onStop() {
        super.onStop();
        locationManager.removeUpdates(locationListener);
    }//ปิดเซอร์วิสเมื่อไม่ได้ใช้แอพ เพื่อปิด module GPS

    public Location myFindLocation(String strProvider) {

        Location location = null;

        if (locationManager.isProviderEnabled(strProvider)) {

            locationManager.requestLocationUpdates(strProvider, 1000 , 10 , locationListener); // ให้มันหาเซอร์วิสให้หาพิกัดทุก ๆ 1000ms และ ทุก ๆ 10 เมตร ให้ทำการค้นหาเลย
            location = locationManager.getLastKnownLocation(strProvider); //ได้ค่า lat long แล้ว

        } else {
            Log.d("21OctV3", "Cannot Find Location");
        }

        return location;
    } // การค้นหา GPS จาก Module ในเครื่อง หรือ internet




    public LocationListener locationListener = new LocationListener() {
        @Override
        public void onLocationChanged(Location location) {

            latADouble = location.getLatitude();
            lngADouble = location.getLongitude();

        } //การ get ค่า location

        @Override
        public void onStatusChanged(String provider, int status, Bundle extras) {

        }

        @Override
        public void onProviderEnabled(String provider) {//เปิดการเชื่อมต่อจะทำยังไง

        }

        @Override
        public void onProviderDisabled(String provider) {//ปิดการเชื่อมต่อจะทำยังไง

        }
    }; //ถ้ามีการเปลี่ยนแปลง จะจับการเปลี่ยนแปลงตาม เช่น การย้ายตำแหน่งของค่า lat long




    private void myAlertCheck(final int index) {

        Log.d("21OctV1", "i == " + index); //เช็คค่าจากปุ่ม radio ที่ได้มา ตาม switch บรรทัดที่ 130

        String[] strings = new String[]{"นักเรียนลงรถ","นักเรียนขึ้นรถ"};

        AlertDialog.Builder builder = new AlertDialog.Builder(TeacherUI.this);
        builder.setCancelable(false);
        builder.setIcon(R.drawable.rat48);
        builder.setTitle("จะทำการใด ?");
        builder.setMessage(strings[index]);
        builder.setNegativeButton("ยกเลิก", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                dialogInterface.cancel();

            }
        });
        builder.setPositiveButton("ตกลง", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                //ถ้ามีค่าเท่ากับ 1 ทำการสร้าง record เข้า database , ถ้าเป็น 0 จะทำการแก้ไข record โดยการ where ให้ครบในตอลัมน์ที่มีใน database
                createTimeRecord(index);
                dialogInterface.dismiss();
            }
        });
        builder.show();

    } // myAlertCheck

    private void createTimeRecord(int i) {

        String strURL = "http://swiftcodingthai.com/golf1/add_time_student_master.php";
        String strURLedit = "http://swiftcodingthai.com/golf1/edit_time_out_master.php";

        //get date
        Calendar calendar = Calendar.getInstance();
        DateFormat dateFormat = new SimpleDateFormat("dd/MM/yyyy"); // วดป
        DateFormat dateFormat1 = new SimpleDateFormat("HH:mm"); // ชั่วโมง นาที
        currentDateString = dateFormat.format(calendar.getTime());
        String strTime = dateFormat1.format(calendar.getTime());
        Log.d("21OctV1", "currentDateString ==> " + currentDateString);
        Log.d("21OctV1", "strTime ==> " + strTime);

        //Get ID_Student
        String strIDstudent = myStudentStrings[0];
        Log.d("21OctV1", "strIDstudent ==> " + strIDstudent);

        //Get ID_Teacher
        String strIDteacher = loginStrings[0];
        Log.d("21OctV1", "strIDteacher ==> " + strIDteacher);


        switch (i) {
            case 0:
                Log.d("21OctV2", "Edit Process");
                EditTimeStudent editTimeStudent = new EditTimeStudent(TeacherUI.this,
                        currentDateString, strTime, strIDstudent);
                editTimeStudent.execute(strURLedit);
                break;
            case 1:
                AddTimeStudent addTimeStudent = new AddTimeStudent(TeacherUI.this,
                        currentDateString, strTime, strIDstudent, strIDteacher);
                addTimeStudent.execute(strURL);
                break;
        }

    } //createTimeRecord

    private class EditTimeStudent extends AsyncTask<String, Void, String> {

        //ประกาศตัวแปร
        private Context context;
        private String dateString, timeString, idStudentString;

        public EditTimeStudent(Context context,
                               String dateString,
                               String timeString,
                               String idStudentString) {
            this.context = context;
            this.dateString = dateString;
            this.timeString = timeString;
            this.idStudentString = idStudentString;
        }

        @Override
        protected String doInBackground(String... strings) {

            try {

                OkHttpClient okHttpClient = new OkHttpClient();
                RequestBody requestBody = new FormEncodingBuilder()
                        .add("isAdd", "true")
                        .add("CurrentDate", dateString)
                        .add("Time_out", timeString)
                        .add("ID_Student", idStudentString)
                        .build();
                Request.Builder builder = new Request.Builder();
                Request request = builder.url(strings[0]).post(requestBody).build();
                Response response = okHttpClient.newCall(request).execute();
                return response.body().string();


            }catch (Exception e) {
                e.printStackTrace();
                return null;
            }
        }//doInBack

        @Override
        protected void onPostExecute(String s) {
            super.onPostExecute(s);

            Log.d("21OctV2", "Result ==> " + s);
            String Result = null;
            if (Boolean.parseBoolean(s)) {
                Result = "นักเรียนลงรถเรียบร้อยแล้ว";
            } else {
                Result = "ทำการไม่ถูกต้อง ลองเริ่มใหม่อีกครั้ง";
            }
            Toast.makeText(context, Result, Toast.LENGTH_SHORT).show();

        }//onPost
    }// EditTimestudent เพิ่ม time out เข้าไป




    private class AddTimeStudent extends AsyncTask<String, Void, String> {

       //ประกาศตัวแปร
        private Context context;
        private String dateString, timeInString, idStudentString, idTeacherString;

        //สร้าง constuctor
        public AddTimeStudent(Context context,
                              String dateString,
                              String timeInString,
                              String idStudentString,
                              String idTeacherString) {
            this.context = context;
            this.dateString = dateString;
            this.timeInString = timeInString;
            this.idStudentString = idStudentString;
            this.idTeacherString = idTeacherString;
        }

        @Override
        protected String doInBackground(String... strings) {

            try {

                OkHttpClient okHttpClient = new OkHttpClient();
                RequestBody requestBody = new FormEncodingBuilder()
                        .add("isAdd" , "true")
                        .add("Date" , dateString)
                        .add("Time_in" , timeInString)
                        .add("ID_Student" , idStudentString)
                        .add("ID_Teacher" , idTeacherString)
                        .build();
                Request.Builder builder = new Request.Builder();
                Request request = builder.url(strings[0]).post(requestBody).build();
                Response response = okHttpClient.newCall(request).execute();
                return response.body().string();

            } catch (Exception e) {
                e.printStackTrace();
                return null;
            }

        }//doinBack

        @Override
        protected void onPostExecute(String s) {
            super.onPostExecute(s);

            Log.d("21OctV1", "Result ==> " + s);
            String result = null;

            if (Boolean.parseBoolean(s)) {
                result = "นักเรียนขึ้นรถเรียบร้อยแล้ว";
            } else {
                result = "ทำการไม่ถูกต้อง ลองเริ่มใหม่อีกครั้ง";
            }

            Toast.makeText(context, result, Toast.LENGTH_SHORT).show();

        }//onPost


    } // AddTimestu class

    /******************************************************************************
     **********************************Read From NFC Tag***************************
     ******************************************************************************/
    private void readFromIntent(Intent intent) {
        String action = intent.getAction();
        if (NfcAdapter.ACTION_TAG_DISCOVERED.equals(action)
                || NfcAdapter.ACTION_TECH_DISCOVERED.equals(action)
                || NfcAdapter.ACTION_NDEF_DISCOVERED.equals(action)) {
            Parcelable[] rawMsgs = intent.getParcelableArrayExtra(NfcAdapter.EXTRA_NDEF_MESSAGES);
            NdefMessage[] msgs = null;
            if (rawMsgs != null) {
                msgs = new NdefMessage[rawMsgs.length];
                for (int i = 0; i < rawMsgs.length; i++) {
                    msgs[i] = (NdefMessage) rawMsgs[i];
                }
            }
            buildTagViews(msgs);
        }
    }
    private void buildTagViews(NdefMessage[] msgs) {
        if (msgs == null || msgs.length == 0) return;

        String strTagNFC = ""; //สิ่งที่อ่านได้จาก NFC
//        String tagId = new String(msgs[0].getRecords()[0].getType());
        byte[] payload = msgs[0].getRecords()[0].getPayload();
        String textEncoding = ((payload[0] & 128) == 0) ? "UTF-8" : "UTF-16"; // Get the Text Encoding
        int languageCodeLength = payload[0] & 0063; // Get the Language Code, e.g. "en"
        // String languageCode = new String(payload, 1, languageCodeLength, "US-ASCII");

        try {
            // Get the Text
            strTagNFC = new String(payload, languageCodeLength + 1, payload.length - languageCodeLength - 1, textEncoding);
        } catch (UnsupportedEncodingException e) {
            Log.e("UnsupportedEncoding", e.toString());
        }

        //Get TagNFC ได้แล้ว

        showDetailStudent(strTagNFC);

    }

    private void showDetailStudent(String strTagNFC) {

        SyncStudent syncStudent = new SyncStudent(TeacherUI.this);
        syncStudent.execute(strTagNFC);

    } //showDetail

    private class SyncStudent extends AsyncTask<String, Void, String> {

        //ประกาศตัวแปร
        private Context context;
        private static final String urlJSON = "http://swiftcodingthai.com/golf1/get_student.php";
        private String ID_StudentString;
        private boolean aBoolean = true;
        private String[] studentStrings;
        private String[] columnStudent = new String[]{
                "ID_Student",
                "Name_Student",
                "Sur_Student",
                "Class_Student",
                "Address_Student",
                "Pic_Student",
                "ID_Parent"};

        public SyncStudent(Context context) {
            this.context = context;
        }

        @Override
        protected String doInBackground(String... strings) {

            ID_StudentString = strings[0];

            try {

                OkHttpClient okHttpClient = new OkHttpClient();
                Request.Builder builder = new Request.Builder();
                Request request = builder.url(urlJSON).build();
                Response response = okHttpClient.newCall(request).execute();
                return response.body().string();

            } catch (Exception e) {
                Log.d("14OctV1", "e doInBack ==> " + e.toString());
                return null;
            }

        } //doInback

        @Override
        protected void onPostExecute(String s) {
            super.onPostExecute(s);

            Log.d("14OctV1", "JSON ==> " + s);
            Log.d("14OctV1", "ID_Student ==> " + ID_StudentString);
            try {

                JSONArray jsonArray = new JSONArray(s);
                studentStrings = new String[columnStudent.length];
                myStudentStrings = new String[columnStudent.length];

                for (int i=0;i<jsonArray.length();i++) {

                    JSONObject jsonObject = jsonArray.getJSONObject(i);

                    if (ID_StudentString.equals(jsonObject.getString(columnStudent[0]))) { //เป็นการเรียกว่าค่าที่เอาออกมาเปรียบเทียบ ID_StudentString ที่ดาต้าเบสในคอลัมน์ที่กำหนดนั้นมาเปรียบเทียบว่ามีหรือไม่
                        Log.d("14OctV2", "ID_StudentString OK");
                        aBoolean = false;
                        for (int i1 = 0; i1 < columnStudent.length; i1++) {

                            studentStrings[i1] = jsonObject.getString(columnStudent[i1]);
                            Log.d("14OctV2", "studentString(" + i1 + ") = " + studentStrings[i1]);

                            myStudentStrings[i1] = studentStrings[i1];

                        }

                    } //if

                }//for

                if (aBoolean) {
                    //ถ้าหาข้อมูลใน TAG ที่กระทำไม่เจอ
                    Alert alert = new Alert();
                    alert.myDialog(context, "ไม่มี TAG ข้อมูลในนี้ในระบบ", "ไม่มี " + ID_StudentString + " ในระบบของเรา");

                } else {
                    //ถ้าหาเจอ TAG
                    Log.d("14OctV1", "Tag " + ID_StudentString + " OK");

                    namestuTextView.setText("ชื่อ : "+ studentStrings[1]);
                    surstuTextView.setText("สกุล : "+ studentStrings[2]);
                    classTextView.setText("ชั้นเรียน : "+ studentStrings[3]);
                    addressTextView.setText("ที่อยู่ : "+ studentStrings[4]);

                    Picasso.with(context)
                            .load(studentStrings[5])
                            .resize(80, 100)
                            .into(studentImageView);

                    radioGroup.clearCheck();

                }//if

            } catch (Exception e) {
                e.printStackTrace();

            }
        } //onPost
    }// Sync data Student class



    /******************************************************************************
     **********************************Write to NFC Tag****************************
     ******************************************************************************/
    private void write(String text, Tag tag) throws IOException, FormatException {
        NdefRecord[] records = { createRecord(text) };
        NdefMessage message = new NdefMessage(records);
        // Get an instance of Ndef for the tag.
        Ndef ndef = Ndef.get(tag);
        // Enable I/O
        ndef.connect();
        // Write the message
        ndef.writeNdefMessage(message);
        // Close the connection
        ndef.close();
    }
    private NdefRecord createRecord(String text) throws UnsupportedEncodingException {
        String lang       = "en";
        byte[] textBytes  = text.getBytes();
        byte[] langBytes  = lang.getBytes("US-ASCII");
        int    langLength = langBytes.length;
        int    textLength = textBytes.length;
        byte[] payload    = new byte[1 + langLength + textLength];

        // set status byte (see NDEF spec for actual bits)
        payload[0] = (byte) langLength;

        // copy langbytes and textbytes into payload
        System.arraycopy(langBytes, 0, payload, 1,              langLength);
        System.arraycopy(textBytes, 0, payload, 1 + langLength, textLength);

        NdefRecord recordNFC = new NdefRecord(NdefRecord.TNF_WELL_KNOWN,  NdefRecord.RTD_TEXT,  new byte[0], payload);

        return recordNFC;
    }



    @Override
    protected void onNewIntent(Intent intent) {
        setIntent(intent);
        readFromIntent(intent);
        if(NfcAdapter.ACTION_TAG_DISCOVERED.equals(intent.getAction())){
            myTag = intent.getParcelableExtra(NfcAdapter.EXTRA_TAG);
        }
    }

    @Override
    public void onPause(){
        super.onPause();
        WriteModeOff();
    }

    @Override
    public void onResume(){
        super.onResume();
        WriteModeOn();

        //For get location
        locationManager.removeUpdates(locationListener); // เคลียออกเพื่อเริ่มนับใหม่จากการใช้ครั้งต่อไป
        latADouble = 0;
        lngADouble = 0;

        Location networkLocation = myFindLocation(LocationManager.NETWORK_PROVIDER); //ให้ค้นหาผ่าน internet ก่อน
        if (networkLocation != null) {
            latADouble = networkLocation.getLatitude();
            lngADouble = networkLocation.getLongitude();
        }

        Location gpsLocation = myFindLocation(LocationManager.GPS_PROVIDER);//ให้ค้นหาจาก Module GPS
        if (gpsLocation != null) {
            latADouble = gpsLocation.getLatitude();
            lngADouble = gpsLocation.getLongitude();
        }

    } //onResume



    /******************************************************************************
     **********************************Enable Write********************************
     ******************************************************************************/
    private void WriteModeOn(){
        writeMode = true;
        nfcAdapter.enableForegroundDispatch(this, pendingIntent, writeTagFilters, null);
    }
    /******************************************************************************
     **********************************Disable Write*******************************
     ******************************************************************************/
    private void WriteModeOff(){
        writeMode = false;
        nfcAdapter.disableForegroundDispatch(this);
    }   // WriteModeOff




    private void loadImageAvatar(String id) {

        Log.d("15SepV3", "Load image at id ==> " + id);
        LoadImage loadImage = new LoadImage(this, id);
        loadImage.execute();

    }//Loadimage

    private class LoadImage extends AsyncTask<Void, Void, String>
    {
        private Context context;
        private String idString;
        private static final String urlPHPimage = "http://swiftcodingthai.com/golf1/get_image_teacher_where_id.php";
        public LoadImage(Context context, String idString) {
            this.context = context;
            this.idString = idString;
        }

        @Override
        protected String doInBackground(Void... voids) {
                //เช็คค่าว่าที่ได้จากการโหลดรูปจาก database
            try {

                OkHttpClient okHttpClient = new OkHttpClient();
                RequestBody requestBody = new FormEncodingBuilder()
                        .add("isAdd", "true")
                        .add("ID_Teacher", idString)
                        .build();
                Request.Builder builder = new Request.Builder();
                Request request = builder.url(urlPHPimage).post(requestBody).build();
                Response response = okHttpClient.newCall(request).execute();
                return response.body().string();

            } catch (Exception e) {
                Log.d("15SepV3", "e doIn ==>" + e.toString());
                return null;
            }
        }//doInBack

        @Override
        protected void onPostExecute(String s) {
            super.onPostExecute(s);

            Log.d("15SepV3", "JSON ==> " + s);

            try {

                JSONArray jsonArray = new JSONArray(s);
                JSONObject jsonObject = jsonArray.getJSONObject(0); //มาจากค่าที่รับมาคือ loginstring0
                String strURLimage = jsonObject.getString("Pic_Teacher"); //ดึงค่าที่ต้องการมาโชว์
                Picasso.with(context).load(strURLimage).resize(120, 150).into(avatarImageView);//ไม่ว่ารูปจะขนาดเท่าไหร่จัดให้เป็นขนาดนี้เลย

            } catch (Exception e) {
                e.printStackTrace();
            }

        }//onPost
    }// method การโหลดรูปภาพจาก database มาแสดง

    @Override
    public void onClick(View view) {

        switch (view.getId()) {

            case R.id.imageView3:
                confirmEditImage();
                break;

        }   // switch เมื่อคลิกที่รูปจะเกิดกิจกรรมนี้ขึ้น

    }   // onClick

    private void confirmEditImage() {

        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setCancelable(false);
        builder.setTitle("Change Image?");
        builder.setIcon(R.drawable.rat48);
        builder.setMessage("คุณต้องการเปลี่ยนรูปหรือไม่ ?");
        builder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                dialogInterface.dismiss();
            }
        });
        builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                changeAvatar();
                dialogInterface.dismiss();
            }
        });
        builder.show();


    }   // confirmEditImage สร้างหน้าต่างการเลือกว่าจะตกลง หรือ ยกเลิก

    private void changeAvatar() {

        Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
        intent.setType("image/*");
        startActivityForResult(Intent.createChooser(intent, "โปรดเลือกภาพที่ต้องการ"), 1);


    }   // changeAvatar

    @Override
    protected void onActivityResult(int requestCode,
                                    int resultCode,
                                    Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if ((requestCode == 1) && (resultCode == RESULT_OK)) {

            Log.d("15SepV1", "Choose Image OK");

            //get path image
            Uri uri = data.getData();
            imagePathString = myFindPathOfImage(uri);

            Log.d("15SepV1", "imagePathString ==> " + imagePathString);

            //Get ชื่อรูปภาพที่ได้มา
            imageNameString = imagePathString.substring(imagePathString.lastIndexOf("/") + 1);

            Log.d("15SepV1", "imageNameString ==> " + imageNameString);

            uploadImageToServer();

        }   // if

    }   // onActivityresult เช็คค่าจากการ positive ของ edit avatar

    private void uploadImageToServer() {

        StrictMode.ThreadPolicy threadPolicy = new StrictMode.ThreadPolicy
                .Builder().permitAll().build();
        StrictMode.setThreadPolicy(threadPolicy); //ช่วยทำให้การอัพโหลดรูปได้

        try {

            SimpleFTP simpleFTP = new SimpleFTP();
            simpleFTP.connect("ftp.swiftcodingthai.com",
                  21, "golf1@swiftcodingthai.com", "Abc12345");
            simpleFTP.bin(); //แปลงเป็น binary โยนไปดัง database
            simpleFTP.cwd("Picteacher"); //กำหนด directory ที่เก็บรูปไว้
            simpleFTP.stor(new File(imagePathString));
            simpleFTP.disconnect();

            updateImageOnMySQL();

            Toast.makeText(this, "Upload " + imagePathString + "Finished",
                    Toast.LENGTH_SHORT).show();

        } catch (Exception e) {
            Log.d("15SepV1", "e ==> " + e.toString());
        }

    }// method อัพรูปขึ้น server

    private void updateImageOnMySQL() {

        EditPicTeacher editPicTeacher = new EditPicTeacher(this);
        editPicTeacher.execute();

    } // uploadImageOnMySQL

    private class EditPicTeacher extends AsyncTask<Void, Void, String> {

        //ประกาศตัวแปร
        private Context context;

        public EditPicTeacher(Context context) {
            this.context = context;
        }

        @Override
        protected String doInBackground(Void... voids) {
            //เป็นส่วนการรับรูปจากการเลือกแล้วนำลงไปยัง ดาต้าเบสเพื่อแสดงผล  (รูปที่ add ล่าสุดจะถูกบันทึกใน database)
            try {

                OkHttpClient okHttpClient = new OkHttpClient();
                RequestBody requestBody = new FormEncodingBuilder()
                        .add("isAdd", "true")
                        .add("ID_Teacher", loginStrings[0])
                        .add("Pic_Teacher", "http://swiftcodingthai.com/golf1/Picteacher/" + imageNameString)
                        .build();
                Request.Builder builder = new Request.Builder();
                Request request = builder.url(urlPHP).post(requestBody).build();
                Response response = okHttpClient.newCall(request).execute();

                return response.body().string();

            } catch (Exception e) {
                Log.d("15SepV2", "e doIn ==> " + e.toString());
                return null;
            }
        }//doInBack

        @Override
        protected void onPostExecute(String s) {
            super.onPostExecute(s);

            Log.d("15SepV2", "Result ==> " + s);
            loadImageAvatar(loginStrings[0]);

        }//onPost

    }//editPicTeacher คลาสเปลี่ยนรูป

    private String myFindPathOfImage(Uri uri) {

        String strResult = null;

        String[] columnStrings = {MediaStore.Images.Media.DATA};
        Cursor cursor = getContentResolver().query(uri, columnStrings,
                null, null, null);

        if (cursor != null) {

            cursor.moveToFirst();
            int intColumnIndex = cursor.getColumnIndexOrThrow(MediaStore.Images.Media.DATA); //เข้าไปที่พาร์ทที่มีไฟล์ภาพใน gallery เพื่อนำมาใช้ในการอัพโหลด
            strResult = cursor.getString(intColumnIndex);

        } else {
            strResult = uri.getPath();
        }
        return strResult;
    } // เมธอดการเลือกและนำทางไปหารูปที่ gallery
}   // Main Class