package cpe.spu.locatekid;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.os.AsyncTask;
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

public class DetailStudent extends AppCompatActivity {

    private TextView nameTextView, surnameTextView, phoneTextView;
    private ImageView picparImageView;;
    private String[] showpicString,myStudentStrings;
    private String currentDateString;
    private Button addButton;

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


        //get ค่าจาก intent ที่แล้วมาใช้
        showpicString = getIntent().getStringArrayExtra("Getpic");//นำค่าจากหน้าที่แล้วมาจาก putextra
        myStudentStrings = getIntent().getStringArrayExtra("GetIDstu");

        addButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                myAlertgo("บุตรหลานท่านจะทำการลาป่วย/ไม่มาโรงเรียนใช่หรือไม่ ?");

            }

        });

        showDetailParent(showpicString[0]); //ใน showpicString อาเรย์ที่[0] คือ ID_Parent ที่ 10000 และ
                                            // ใน showpicString อาเรย์ที่[1] คือ ID_Parent ที่ 10001

        //showDetailParent(showpicString[0]);
        /*if (showpicString[5].length() != 0) {
            if ((showpicString.equals(getIntent().getStringArrayExtra("Getpic")))) {
                for (int i = 0; i < showpicString.length; i++) {

                    getImage(showpicString[i]);

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



   /*private void getload() {

        Loadparent loadparent = new Loadparent(this, urlparent);
        loadparent.execute();

    }

    private class Loadparent extends AsyncTask<Void, Void, String> {

        //ประกาศตัวแปร
        private Context context;
        private String urlparent;

        //Constructor


        public Loadparent(Context context, String urlparent) {
            this.context = context;
            this.urlparent = urlparent;
        }

        @Override
        protected String doInBackground(Void... params) {

            try {

                OkHttpClient okHttpClient = new OkHttpClient();
                Request.Builder builder = new Request.Builder();
                Request request = builder.url(urlparent).build();
                Response response = okHttpClient.newCall(request).execute();
                return response.body().string();


            } catch (Exception e) {
                Log.d("TestDetail", "e doInBack ==> " + e.toString());
                return null;
            }
        }//doInback

        @Override
        protected void onPostExecute(String s) {
            super.onPostExecute(s);

            Log.d("TestDetail", "JSON ==> " + s);

            try {

                JSONArray jsonArray = new JSONArray(s);
                final String[] idStrings = new String[jsonArray.length()];
                final String[] nameStrings = new String[jsonArray.length()];
                final String[] surStrings = new String[jsonArray.length()];
                final String[] telStrings = new String[jsonArray.length()];
                final String[] picStrings = new String[jsonArray.length()];

                for(int i=0; i < jsonArray.length(); i++) {

                    JSONObject jsonObject = jsonArray.getJSONObject(i);
                    idStrings[i] = jsonObject.getString("ID_Parent");
                    nameStrings[i] = jsonObject.getString("Name_Parent");
                    surStrings[i] = jsonObject.getString("Sur_Parent");
                    telStrings[i] = jsonObject.getString("Tel_Parent");
                    picStrings[i] = jsonObject.getString("Pic_Student");

                    nameTextView.setText("ชื่อ : "+ nameStrings[i]);
                    surnameTextView.setText("สกุล : "+ surStrings[i]);
                    phoneTextView.setText("เบอร์โทร : "+ telStrings[i]);
                    Picasso.with(context)
                            .load(picStrings[i])
                            .resize(80, 100)
                            .into(picparImageView);
                }


            } catch (Exception e) {
                e.printStackTrace();
            }

        }//onPost
    }//Loadparent class*/


    /*private void getImage(String id) {

        Log.d("TestDetail", "Load image at id ==> " + id);
        Loadpicstu loadpicstu = new Loadpicstu(this, id);
        loadpicstu.execute();

    }//getImage

    /*private class Loadpicstu extends AsyncTask<String, Void, String> {

        //ประกาศตัวแปร
        private Context context;
        private String idString;
        private String[] ID_StudentStrings;
        private static final String urlStuimage = "http://swiftcodingthai.com/golf1/get_image_student_where_id.php";

        //Constructor
        public Loadpicstu(Context context, String idString) {
            this.context = context;
            this.idString = idString;
        }

        @Override
        protected String doInBackground(String... strings) {


            try {

                OkHttpClient okHttpClient = new OkHttpClient();
                RequestBody requestBody = new FormEncodingBuilder()
                        .add("isAdd", "true")
                        .add("ID_Student", idString)
                        .build();
                Request.Builder builder = new Request.Builder();
                Request request = builder.url(urlStuimage).post(requestBody).build();
                Response response = okHttpClient.newCall(request).execute();
                return response.body().string();

            } catch (Exception e) {
                Log.d("TestDetail", "e doIn ==>" + e.toString());
                return null;
            }
        }//doInback

        @Override
        protected void onPostExecute(String s) {
            super.onPostExecute(s);

            Log.d("TestDetail", "JSON ==> " + s);

            try {


                JSONArray jsonArray = new JSONArray(s);
                JSONObject jsonObject = jsonArray.getJSONObject(0);
                String strFormURL = jsonObject.getString("Pic_Student");
                Picasso.with(context).load(strFormURL).resize(120, 150).into(picstuImageView);


            } catch (Exception e) {
                e.printStackTrace();
            }

        }//onPost
    }//GetdetailPic class*/


}//Main Class
