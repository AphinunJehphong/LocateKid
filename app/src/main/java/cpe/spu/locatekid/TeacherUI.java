package cpe.spu.locatekid;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

public class TeacherUI extends AppCompatActivity implements View.OnClickListener {

    //ประกาศตัวแปร
    private TextView nameTextview, surnameTextview, phoneTextview;
    private ImageView avatarImageView;
    private String[] loginStrings;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_teacher_ui);

        //Bind widget
        nameTextview = (TextView) findViewById(R.id.textView8);
        surnameTextview = (TextView) findViewById(R.id.textView9);
        phoneTextview = (TextView) findViewById(R.id.textView10);
        avatarImageView = (ImageView) findViewById(R.id.imageView3);

        //get ค่าจาก intent ที่แล้วมาใช้
        loginStrings = getIntent().getStringArrayExtra("Login"); //นำค่าจากหน้าที่แล้วมาจาก putextra

        //นำค่าจาก database มาโชว์ตามจุดที่ต้องการ
        nameTextview.setText("ชื่อ : "+ loginStrings[1]);
        surnameTextview.setText("นามสกุล : "+ loginStrings[2]);
        phoneTextview.setText("Phone : "+ loginStrings[3]);

        //การ get image
        avatarImageView.setOnClickListener(this);

    }//main method

    @Override
    public void onClick(View view) {

        switch (view.getId()){

            case R.id.imageView3 :
                confirmEditImage();
                break;

        }//switch

    } //method onclick

    private void confirmEditImage() {

        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setCancelable(false);
        builder.setTitle("Change Image");
        builder.setIcon(R.drawable.rat48);
        builder.setMessage("คุณต้องการเปลี่ยนรูปหรือไม่");
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

    }// method confirmEditImage

    private void changeAvatar() {

        Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
        intent.setType("image/*");
        startActivityForResult(Intent.createChooser(intent, "โปรดเลือกภาพที่ต้องการ"), 1);

    }//method changeavatar

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if ((requestCode == 1) && (requestCode == RESULT_OK)) {

            Log.d("15SepV1", "Choose Image OK");

        }// if

    }// onActivityresult เช็คค่าจากการ positive ของ edit avatar
}//main class
