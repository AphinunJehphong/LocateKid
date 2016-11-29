package cpe.spu.locatekid;

import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;


import com.squareup.okhttp.OkHttpClient;
import com.squareup.okhttp.Request;
import com.squareup.okhttp.Response;
import com.squareup.picasso.Picasso;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.IOException;



public class Showliststudent extends AppCompatActivity implements View.OnClickListener {

    //ประกาศตัวแปร
    private ListView mylistView;
    private Button backButton;
    private static final String urlList = "http://swiftcodingthai.com/golf1/get_student.php";


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_showliststudent);

        //widget
        mylistView = (ListView) findViewById(R.id.listView);
        //backButton = (Button) findViewById(R.id.button10);

        //Set click Back
        //backButton.setOnClickListener(this);

        //Create ListView by Data on Server
        //createListView();
        SyncList syncList = new SyncList(this, mylistView, urlList);
        syncList.execute();



    } //Main Method


    //Create Inner Class
    private class SyncList extends AsyncTask<String, Void, String> {

        //ประกาศตัวแปร
        private Context context;
        private ListView myListView;
        private String urlList;

        //constructor
        public SyncList(Context context, ListView myListView, String urlList) {
            this.context = context;
            this.myListView = myListView;
            this.urlList = urlList;
        }

        @Override
        protected String doInBackground(String... strings) {

            try {

                OkHttpClient okHttpClient = new OkHttpClient();
                Request.Builder builder = new Request.Builder();
                Request request = builder.url(urlList).build();
                Response response = okHttpClient.newCall(request).execute();
                return response.body().string();

            } catch (IOException e) {
                Log.d("Testlist", "e doInBack ==> " + e.toString());
                return null;
            }
        } //doInBack

        @Override
        protected void onPostExecute(String s) {
            super.onPostExecute(s);

            Log.d("Testlist", "JSON ==> " + s);

            try {

                JSONArray jsonArray = new JSONArray(s);

                final String[] idStrings = new String[jsonArray.length()];
                final String[] nameStrings = new String[jsonArray.length()];
                final String[] surStrings = new String[jsonArray.length()];
                final String[] classStrings = new String[jsonArray.length()];
                final String[] addressStrings = new String[jsonArray.length()];
                final String[] picStrings = new String[jsonArray.length()];
                final String[] idparentStrings = new String[jsonArray.length()];

                for (int i=0; i<jsonArray.length(); i++) {

                    JSONObject jsonObject = jsonArray.getJSONObject(i);
                    idStrings[i] = jsonObject.getString("ID_Student");
                    nameStrings[i] = jsonObject.getString("Name_Student");
                    surStrings[i] = jsonObject.getString("Sur_Student");
                    classStrings[i] = jsonObject.getString("Class_Student");
                    addressStrings[i] = jsonObject.getString("Address_Student");
                    picStrings[i] = jsonObject.getString("Pic_Student");
                    idparentStrings[i] = jsonObject.getString("ID_Parent");

                } //for

                ShowAdapter showAdapter = new ShowAdapter(context,
                        nameStrings, surStrings, classStrings, picStrings);
                mylistView.setAdapter(showAdapter);


                //Active click Listview
                myListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                    @Override
                    public void onItemClick(AdapterView<?> parent, View view, int i, long l) {

                        Intent goDetail = new Intent(Showliststudent.this, DetailStudent.class);
                        int itemPosition  = i;
                        goDetail.putExtra("Getindex", itemPosition);
                        goDetail.putExtra("GetIDparent", idparentStrings);
                        goDetail.putExtra("GetIDstu", idStrings);
                        startActivity(goDetail);
                    }
                });

            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    } //SyncList

    /*private void createListView() {

        SyncList syncList = new SyncList(this, mylistView, urlList);
        syncList.execute();
    }//createListview*/

    @Override
    public void onClick(View v) {
        finish();
    }
} //Main Class