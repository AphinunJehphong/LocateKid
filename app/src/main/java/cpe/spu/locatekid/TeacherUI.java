package cpe.spu.locatekid;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.ImageView;
import android.widget.TextView;

public class TeacherUI extends AppCompatActivity {

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

    }//main method



}//main class
