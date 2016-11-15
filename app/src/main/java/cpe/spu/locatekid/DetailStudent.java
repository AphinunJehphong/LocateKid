package cpe.spu.locatekid;

import android.content.Context;
import android.os.AsyncTask;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.widget.ImageView;
import android.widget.TextView;

import com.google.android.gms.games.internal.constants.RequestStatus;
import com.squareup.okhttp.FormEncodingBuilder;
import com.squareup.okhttp.OkHttpClient;
import com.squareup.okhttp.Request;
import com.squareup.okhttp.RequestBody;
import com.squareup.okhttp.Response;
import com.squareup.picasso.Picasso;

import org.json.JSONArray;
import org.json.JSONObject;

public class DetailStudent extends AppCompatActivity {

    private TextView nameTextView, surnameTextView, phoneTextView, addressTextView;
    private ImageView picstuImageView, picparImageView;;
    private String[] idStrings, showpicString, showdataStrings;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_detail_student);

        nameTextView = (TextView) findViewById(R.id.textView32);
        surnameTextView = (TextView) findViewById(R.id.textView33);
        phoneTextView = (TextView) findViewById(R.id.textView34);
        addressTextView = (TextView) findViewById(R.id.textView36);
        picstuImageView = (ImageView) findViewById(R.id.imageView7);
        picparImageView = (ImageView) findViewById(R.id.imageView6);




        //get ค่าจาก intent ที่แล้วมาใช้
        //idStrings = getIntent().getStringArrayExtra("Getcontent");
        showpicString = getIntent().getStringArrayExtra("Getpic"); //นำค่าจากหน้าที่แล้วมาจาก putextra



        /*if (showpicString[5].length() != 0) {
            if ((showpicString.equals(getIntent().getStringArrayExtra("Getpic")))) {
                for (int i = 0; i < showpicString.length; i++) {

                    getImage(showpicString[i]);

                }
            }
        }*/
        if (showpicString[5].length() != 0) {
            getImage(showpicString[0]); //เช็คความยาวของตัวอักษรแล้วเทียบถ้ามีให้ส่งลง
        }

        showDetailParent(showpicString[0]);
        //getload();

    }//Main Method

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

                    Picasso.with(context).load(parentStrings[4]).into(picparImageView);

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


    private void getImage(String id) {

        Log.d("TestDetail", "Load image at id ==> " + id);
        Loadpicstu loadpicstu = new Loadpicstu(this, id);
        loadpicstu.execute();

    }//getImage

    private class Loadpicstu extends AsyncTask<String, Void, String> {

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
    }//GetdetailPic class


}//Main Class
