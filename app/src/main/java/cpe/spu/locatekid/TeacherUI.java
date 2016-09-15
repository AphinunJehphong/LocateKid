package cpe.spu.locatekid;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.StrictMode;
import android.provider.MediaStore;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
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


public class TeacherUI extends AppCompatActivity implements View.OnClickListener {

    //Explicit
    private TextView nameTextView, surnameTextView, phoneTextView;
    private ImageView avataImageView;
    private String[] loginStrings;
    private String imagePathString, imageNameString;
    private static final String urlPHP = "http://swiftcodingthai.com/golf1/edit_image_teacher.php";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_teacher_ui);

        //Widget
        nameTextView = (TextView) findViewById(R.id.textView8);
        surnameTextView = (TextView) findViewById(R.id.textView9);
        phoneTextView = (TextView) findViewById(R.id.textView10);
        avataImageView = (ImageView) findViewById(R.id.imageView3);

        //Get Value From Intent
        loginStrings = getIntent().getStringArrayExtra("Login");

        //เช็ค image ว่ารูปมีไหม
        if (loginStrings[4].length() != 0) {
            loadImageAvatar(loginStrings[0]);

        }// if

        //Show Text
        nameTextView.setText("ชื่อ : " + loginStrings[1]);
        surnameTextView.setText("นามสกุล : " + loginStrings[2]);
        phoneTextView.setText("Phone : " + loginStrings[3]);

        //Image Controller
        avataImageView.setOnClickListener(this);


    }   // Main Method

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
                Picasso.with(context).load(strURLimage).resize(120, 150).into(avataImageView);//ไม่ว่ารูปจะขนาดเท่าไหร่จัดให้เป็นขนาดนี้เลย

            } catch (Exception e) {
                e.printStackTrace();
            }

        }//onPost
    }// method การโหลดรูปภาพ

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
        builder.setTitle("เปลี่ยนรูป Avata");
        builder.setIcon(R.drawable.rat48);
        builder.setMessage("คุณต้องการเปลี่ยนรูป Avata หรือ คะ ?");
        builder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                dialogInterface.dismiss();
            }
        });
        builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                changeAvata();
                dialogInterface.dismiss();
            }
        });
        builder.show();



    }   // confirmEditImage

    private void changeAvata() {

        Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
        intent.setType("image/*");
        startActivityForResult(Intent.createChooser(intent, "โปรดเลือกภาพ"), 1);


    }   // changeAvate


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

    }   // onActivityResult

    private void uploadImageToServer() {

        StrictMode.ThreadPolicy threadPolicy = new StrictMode.ThreadPolicy
                .Builder().permitAll().build();
        StrictMode.setThreadPolicy(threadPolicy); //ช่วยทำให้การอัพโหลดรูปได้

        try {

            SimpleFTP simpleFTP = new SimpleFTP();
            simpleFTP.connect("ftp.swiftcodingthai.com",
                  21, "golf1@swiftcodingthai.com", "Abc12345");
            simpleFTP.bin(); //แปลงเป็น binary โยนไปดัง database
            simpleFTP.cwd("Image");
            simpleFTP.stor(new File(imagePathString));
            simpleFTP.disconnect();

            updateImageOnMySQL();

            Toast.makeText(this, "Upload " + imagePathString + "finish",
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

            try {

                OkHttpClient okHttpClient = new OkHttpClient();
                RequestBody requestBody = new FormEncodingBuilder()
                        .add("isAdd", "true")
                        .add("ID_Teacher", loginStrings[0])
                        .add("Pic_Teacher", "http://swiftcodingthai.com/golf1/Image/" + imageNameString)
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

    }//editPicTeacher


    private String myFindPathOfImage(Uri uri) {

        String strResult = null;

        String[] columnStrings = {MediaStore.Images.Media.DATA};
        Cursor cursor = getContentResolver().query(uri, columnStrings,
                null, null, null);

        if (cursor != null) {

            cursor.moveToFirst();
            int intColumnIndex = cursor.getColumnIndexOrThrow(MediaStore.Images.Media.DATA);
            strResult = cursor.getString(intColumnIndex);

        } else {
            strResult = uri.getPath();
        }


        return strResult;
    }
}   // Main Class