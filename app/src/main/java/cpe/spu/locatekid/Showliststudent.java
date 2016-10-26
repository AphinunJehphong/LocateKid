package cpe.spu.locatekid;

import android.content.Context;
import android.os.AsyncTask;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.widget.ListView;
import android.widget.TextView;

import com.squareup.okhttp.OkHttpClient;
import com.squareup.okhttp.Request;
import com.squareup.okhttp.Response;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.IOException;

public class Showliststudent extends AppCompatActivity {

    //ประกาศตัวแปร
    private ListView listView;
    private static final String urlList = "http://swiftcodingthai.com/golf1/get_student.php";


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_showliststudent);

        //widget
        listView = (ListView) findViewById(R.id.listView);

        //Create ListView by Data on Server
        creatListView();

    } //Main Method

    //Create Inner Class
    private class SyncList extends AsyncTask<String, Void, String> {

        //ประกาศตัวแปร
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

                String[] idStuStrings = new String[jsonArray.length()];
                String[] nameStrings = new String[jsonArray.length()];
                String[] surStrings = new String[jsonArray.length()];
                String[] picStrings = new String[jsonArray.length()];

                for (int i=0; i<jsonArray.length(); i++) {

                    JSONObject jsonObject = jsonArray.getJSONObject(i);
                    idStuStrings[i] = jsonObject.getString("ID_Student");
                    nameStrings[i] = jsonObject.getString("Name_Student");
                    surStrings[i] = jsonObject.getString("Sur_Student");
                    picStrings[i] = jsonObject.getString("Pic_Student");

                } //for

                ShowAdapter showAdapter = new ShowAdapter(Showliststudent.this,
                        idStuStrings,nameStrings, surStrings, picStrings);
                listView.setAdapter(showAdapter);

            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    } //SyncList

    private void creatListView() {

        SyncList syncList = new SyncList();
        syncList.execute();
    }

} //Main Class
