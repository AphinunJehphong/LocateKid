package cpe.spu.locatekid;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Build;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.squareup.okhttp.FormEncodingBuilder;
import com.squareup.okhttp.OkHttpClient;
import com.squareup.okhttp.Request;
import com.squareup.okhttp.RequestBody;
import com.squareup.okhttp.Response;
import com.squareup.picasso.Picasso;

import org.json.JSONArray;
import org.json.JSONObject;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Objects;

public class DetailStudent extends AppCompatActivity implements View.OnClickListener{

    private TextView nameTextView, surnameTextView, phoneTextView;
    private ImageView picparImageView;
    private String[] showpicString,myStudentStrings;
    private String currentDateString;
    private Button addButton ,backButton;
    private int getitemString;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_detail_student);

        nameTextView = (TextView) findViewById(R.id.textView32);
        surnameTextView = (TextView) findViewById(R.id.textView33);
        phoneTextView = (TextView) findViewById(R.id.textView34);
        //addressTextView = (TextView) findViewById(R.id.textView36);
        picparImageView = (ImageView) findViewById(R.id.imageView6);
        addButton = (Button) findViewById(R.id.button7);
        //backButton = (Button) findViewById(R.id.button5);


        //get ค่าจาก intent ที่แล้วมาใช้
        showpicString = getIntent().getStringArrayExtra("GetIDparent");//นำค่าจากหน้าที่แล้วมาจาก putextra
        myStudentStrings = getIntent().getStringArrayExtra("GetIDstu");
        getitemString = getIntent().getIntExtra("Getindex",getitemString);



        /*
        backButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Intent golist = new Intent(DetailStudent.this, Showliststudent.class);
                startActivity(golist);

            }
        });*/

        addButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                myAlertgo("บุตรหลานท่านจะทำการลาป่วย/ไม่มาโรงเรียนใช่หรือไม่ ?");

            }

        });







    /*for (int i = 0; i < showpicString.length; i++) {
        switch (showpicString[i]) {

            case "10000":
                showDetailParent(showpicString[i]);
                break;
            case "10001":
                showDetailParent(showpicString[i]);
                break;
            case "10002":
                showDetailParent(showpicString[i]);
                break;
            case "10003":
                showDetailParent(showpicString[i]);
                break;
            case "10004":
                showDetailParent(showpicString[i]);
                break;
            case "10005":
                showDetailParent(showpicString[i]);
                break;
        }
    }*/


        //showDetailParent(showpicString[0]); //ใน showpicString อาเรย์ที่[0] คือ ID_Parent ที่ 10000 และ
        // ใน showpicString อาเรย์ที่[1] คือ ID_Parent ที่ 10001

        showDetailParent(showpicString[getitemString]);
        /*if (showpicString[0].length() != 0) {

            if ((showpicString.equals(getIntent().getStringArrayExtra("GetIDparent")))) {
                for (int i = 0; i < showpicString.length; i++) {

                    showDetailParent(showpicString[i]);

                }
            }
        }*/
       /*if (showpicString[5].length() != 0) {
            getImage(showpicString[0]); //เช็คความยาวของตัวอักษรแล้วเทียบถ้ามีให้ส่งลง
        }*/





    }//Main Method

    private void myAlertgo(final String strMessage) {

        Log.d("Teststat", "i == " + strMessage); //เช็คค่าจากปุ่ม button ที่ได้มา ตามบรรทัดที่ 58

        String[] strings = new String[]{"บุตรหลานท่านจะทำการลาป่วย/ไม่มาโรงเรียนใช่หรือไม่ ?"};

        AlertDialog.Builder builder = new AlertDialog.Builder(DetailStudent.this);
        builder.setCancelable(false);
        builder.setIcon(R.drawable.rat48);
        builder.setTitle("ทำการลาป่วย/แจ้งว่าไม่มาโรงเรียน");
        builder.setMessage(strMessage);
        builder.setNegativeButton("ยกเลิก", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                dialogInterface.cancel();

            }
        });
        builder.setPositiveButton("ตกลง", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                //ถ้ามีค่าเท่ากับ "บุตรหลานท่านจะทำการลาป่วย/ไม่มาโรงเรียนใช่หรือไม่ ?" ทำการสร้าง record เข้า database , ถ้าเป็น 0 จะทำการแก้ไข record โดยการ where ให้ครบในตอลัมน์ที่มีใน database
                Absent(strMessage);
                dialogInterface.dismiss();
            }
        });
        builder.show();

    } // myAlertgo


    private void Absent(String i) {

        String strURLadd = "http://swiftcodingthai.com/golf1/add_status_student.php";

        //Get date
        Calendar calendar = Calendar.getInstance();
        DateFormat dateFormat = new SimpleDateFormat("dd/MM/yyyy"); // วดป
        currentDateString = dateFormat.format(calendar.getTime());
        Log.d("Teststat", "currentDateString ==> " + currentDateString);


        //Get ID_Student
        String strIDstudent = myStudentStrings[0];
        Log.d("Teststat", "strIDstudent ==> " + strIDstudent);

        switch (i) {
            case "บุตรหลานท่านจะทำการลาป่วย/ไม่มาโรงเรียนใช่หรือไม่ ?":
                Log.d("Teststat", "Edit Process");
                AbsentStudent absentStudent = new AbsentStudent(DetailStudent.this,
                        currentDateString, strIDstudent);
                absentStudent.execute(strURLadd);
                break;
        }

    }//addAbsent class เช็คค่า

    @Override
    public void onClick(View v) {
        finish();
    }//onclick

    private class AbsentStudent extends AsyncTask<String, Void, String> {

        //ประกาศตัวแปร
        private Context context;
        private String dateString, idStudentString;

        //Constructor
        public AbsentStudent(Context context,
                             String dateString,
                             String idStudentString) {
            this.context = context;
            this.dateString = dateString;
            this.idStudentString = idStudentString;
        }

        @Override
        protected String doInBackground(String... strings) {

            try {

                OkHttpClient okHttpClient = new OkHttpClient();
                RequestBody requestBody = new FormEncodingBuilder()
                        .add("isAdd", "true")
                        .add("Date", dateString)
                        .add("ID_Student", idStudentString)
                        .build();
                Request.Builder builder = new Request.Builder();
                Request request = builder.url(strings[0]).post(requestBody).build();
                Response response = okHttpClient.newCall(request).execute();
                return response.body().string();


            } catch (Exception e) {
                e.printStackTrace();
                return null;

            }

        }//doInBack

        @Override
        protected void onPostExecute(String s) {
            super.onPostExecute(s);

            Log.d("Teststat", "Result ==> " + s);
            String Result = null;
            if (Boolean.parseBoolean(s)) {
                Result = "แจ้งเตือนสำเร็จ";
            } else {
                Result = "ทำการไม่ถูกต้อง ลองเริ่มใหม่อีกครั้ง";
            }
            Toast.makeText(context, Result, Toast.LENGTH_SHORT).show();

        }//onPost
    }

    private void showDetailParent(String showpicString) {

        Showparent showparent= new Showparent(DetailStudent.this);
        showparent.execute(showpicString);

    }

    private class Showparent extends AsyncTask<String, Void, String > {

        //ประกาศตัวแปร
        private Context context;
        private static final String urlJSON = "http://swiftcodingthai.com/golf1/get_userparent.php";
        private String ID_ParentString;
        private boolean aBoolean = true;
        private String[] parentStrings;
        private String[] columnParent = new String[]{
                "ID_Parent",
                "Name_Parent",
                "Sur_Parent",
                "Tel_Parent",
                "Pic_Parent",
                "Username",
                "Password"};

        //Constructor
        public Showparent(Context context) {
            this.context = context;
        }

        @Override
        protected String doInBackground(String... strings) {

            ID_ParentString = strings[0];

            try {

                OkHttpClient okHttpClient = new OkHttpClient();
                Request.Builder builder = new Request.Builder();
                Request request = builder.url(urlJSON).build();
                Response response = okHttpClient.newCall(request).execute();
                return response.body().string();

            } catch (Exception e) {
                Log.d("TestDetail", "e doInBack ==> " + e.toString());
                return null;
            }

        }//doInBack

        @Override
        protected void onPostExecute(String s) {
            super.onPostExecute(s);

            Log.d("TestDetail", "JSON ==> " + s);
            Log.d("TestDetail", "ID_Parent ==> " + ID_ParentString);

            try {

                JSONArray jsonArray = new JSONArray(s);
                parentStrings = new String[columnParent.length];

                for (int i=0; i < jsonArray.length(); i++) {

                    JSONObject jsonObject = jsonArray.getJSONObject(i);

                    if (ID_ParentString.equals(jsonObject.getString(columnParent[0]))) {
                        Log.d("TestDetail", "ID_ParentString OK");
                        aBoolean = false;
                        for (int i1 = 0; i1 < columnParent.length; i1++) {

                            parentStrings[i1] = jsonObject.getString(columnParent[i1]);
                            Log.d("TestDetail", "ParentString(" + i1 + ") = " + columnParent[i1]);

                        }
                    }

                }

                if (aBoolean) {

                    Alert alert = new Alert();
                    alert.myDialog(context, "ไม่มี ข้อมูลนี้ในระบบ", "ไม่มี " + ID_ParentString + " ในระบบของเรา");

                } else  {

                    Log.d("TestDetail", "Result " + ID_ParentString + " OK");

                    nameTextView.setText("ชื่อผู้ปกครอง : " + parentStrings[1]);
                    surnameTextView.setText("นามสกุล : " + parentStrings[2]);
                    phoneTextView.setText("เบอร์ติดต่อ : " + parentStrings[3]);

                    Picasso.with(context).load(parentStrings[4]).resize(180, 200).into(picparImageView);

                }

            } catch (Exception e) {
                e.printStackTrace();

            }
        }//onPost
    }//Showparent Method

}//Main Class