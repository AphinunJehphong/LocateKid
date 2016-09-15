package cpe.spu.locatekid;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.provider.MediaStore;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;


public class TeacherUI extends AppCompatActivity implements View.OnClickListener {

    //Explicit
    private TextView nameTextView, surnameTextView, phoneTextView;
    private ImageView avataImageView;
    private String[] loginStrings;
    private String imagePathString, imageNameString;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_teacher_ui);

        //Bind Widget
        nameTextView = (TextView) findViewById(R.id.textView8);
        surnameTextView = (TextView) findViewById(R.id.textView9);
        phoneTextView = (TextView) findViewById(R.id.textView10);
        avataImageView = (ImageView) findViewById(R.id.imageView3);

        //Get Value From Intent
        loginStrings = getIntent().getStringArrayExtra("Login");

        //Show Text
        nameTextView.setText("ชื่อ : " + loginStrings[1]);
        surnameTextView.setText("นามสกุล : " + loginStrings[2]);
        phoneTextView.setText("Phone : " + loginStrings[3]);

        //Image Controller
        avataImageView.setOnClickListener(this);


    }   // Main Method




    @Override
    public void onClick(View view) {

        switch (view.getId()) {

            case R.id.imageView3:
                confirmEditImage();
                break;

        }   // switch

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


        }   // if

    }   // onActivityResult

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