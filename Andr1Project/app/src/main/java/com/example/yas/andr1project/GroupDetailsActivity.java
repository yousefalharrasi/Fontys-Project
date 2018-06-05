package com.example.yas.andr1project;

import android.os.AsyncTask;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.TextView;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.Scanner;

public class GroupDetailsActivity extends AppCompatActivity {
    ArrayList<String> groupInfoList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_group_details);

        String ID = getIntent().getExtras().getString("id");
        new GroupDetailsAsyncTask().execute(ID);
    }

    private class GroupDetailsAsyncTask extends AsyncTask<String, Void, ArrayList<String>> {
        URL url = null;

        @Override
        protected ArrayList<String> doInBackground(String... params) {
            try {
                url = new URL("https://api.fhict.nl/groups/" + params[0]);
                HttpURLConnection connection = (HttpURLConnection) url.openConnection();
                connection.setRequestProperty("Accept", "application/json");
                connection.setRequestProperty("Authorization", "Bearer " + MainActivity.tokenKey);
                connection.connect();

                InputStream inputStream = connection.getInputStream();
                String jsonString = (new Scanner(inputStream)).useDelimiter("\\A").next();

                JSONObject jsonObjectBuildingDetail = new JSONObject(jsonString);

                String id = jsonObjectBuildingDetail.getString("id");
                String name = jsonObjectBuildingDetail.getString("groupName");
                String url = jsonObjectBuildingDetail.getString("url");


                groupInfoList = new ArrayList<>();
                groupInfoList.add(id);
                groupInfoList.add(name);
                groupInfoList.add(url);
                return groupInfoList;


            } catch (MalformedURLException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            } catch (JSONException e) {
                e.printStackTrace();
            }
            return null;
        }

        @Override
        protected void onPostExecute(ArrayList<String> strings) {

            TextView tvID, tvName, tvUrl;
            tvID = findViewById(R.id.group_id);
            tvName = findViewById(R.id.group_name);
            tvUrl = findViewById(R.id.group_url);

            tvID.setText(groupInfoList.get(0));
            tvName.setText(groupInfoList.get(1));
            tvUrl.setText(groupInfoList.get(2));


        }
    }
}
