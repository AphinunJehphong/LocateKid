package cpe.spu.locatekid;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.RadioGroup;

public class MainActivity extends AppCompatActivity {

    //ประกาศตัวแปร
    private EditText usernameEditText , passwordEditText;
    private String usernameString , passwordString;
    private RadioGroup radioGroup;
    private RadioButton parentRadioButton, teacherRadioButton;
    private int modeChoice = 0;
    private String[] urlPHPStrings = new String[]{"http://swiftcodingthai.com/golf1/get_parent.php"
            ,"http://swiftcodingthai.com/golf1/get_userteacher.php"};


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        //widget
        usernameEditText = (EditText) findViewById(R.id.editText);
        passwordEditText = (EditText) findViewById(R.id.editText2);
        radioGroup = (RadioGroup) findViewById(R.id.choiceMode);
        parentRadioButton = (RadioButton) findViewById(R.id.radioButton);
        teacherRadioButton = (RadioButton) findViewById(R.id.radioButton2);

        //กำหนดเงื่อนไขต่าง ๆ ของการเลือกผู้ปกครองและครู
        choiceMode();

    } //main method

    private void choiceMode() {

        radioGroup.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup radioGroup, int i) {

                switch (i) {
                    case R.id.radioButton:
                        modeChoice = 0;
                        break;
                    case R.id.radioButton2:
                        modeChoice = 1;
                        break;
                    default: modeChoice = 0;
                        break;
                }

            }//การเช็คค่าเงื่อนไขการเลือก
        });

    }// Class choicemode

    public void clickButton(View view) {

        //นำค่ามาจาก edittext
        usernameString = usernameEditText.getText().toString().trim();
        passwordString = passwordEditText.getText().toString().trim();

        //การ check ช่องว่าง
        if (usernameString.equals("") || passwordString.equals("")) {
            //มีช่องว่าง
            Alert alert = new Alert();
            alert.myDialog(this, "Error" , "โปรดกรอกให้ครบถ้วน");
        } else {
            //มีการกระทำ
        } //เงื่อนไขเช็คว่า

    } //เมธอดปุ่มล็อคอิน
} //Class main
