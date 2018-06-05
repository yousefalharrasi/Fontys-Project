package com.example.yas.andr1project;

import android.content.Intent;
import android.os.AsyncTask;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

public class BuildingDetailsActivity extends AppCompatActivity {
    ArrayList<String> buildingInfoList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_building_details);

        String ID = getIntent().getExtras().getString("id");

        new  BuildingDetailsAsyncTask().execute(ID);

    }


    private class BuildingDetailsAsyncTask extends AsyncTask<String, Void, ArrayList<String>> {
        URL url = null;

        @Override
        protected ArrayList<String> doInBackground(String... params) {
            try {
                url = new URL("https://api.fhict.nl/buildings/" + params[0]);
                HttpURLConnection connection = (HttpURLConnection) url.openConnection();
                connection.setRequestProperty("Accept", "application/json");
                connection.setRequestProperty("Authorization", "Bearer " + MainActivity.tokenKey);
                connection.connect();

                InputStream inputStream = connection.getInputStream();
                String jsonString = (new Scanner(inputStream)).useDelimiter("\\A").next();

                JSONObject jsonObjectBuildingDetail = new JSONObject(jsonString);

                String name = jsonObjectBuildingDetail.getString("name");
                String city = jsonObjectBuildingDetail.getString("city");
                String url = jsonObjectBuildingDetail.getString("url");
                String description = jsonObjectBuildingDetail.getString("description");
                String address = jsonObjectBuildingDetail.getString("address");
                String postalCode = jsonObjectBuildingDetail.getString("postalCode");

                buildingInfoList = new ArrayList<>();
                buildingInfoList.add(name);
                buildingInfoList.add(city);
                buildingInfoList.add(url);
                buildingInfoList.add(description);
                buildingInfoList.add(address);
                buildingInfoList.add(postalCode);
                return buildingInfoList;


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

            TextView tvName, tvCity, tvUrl, tvDescription,tvAddress,tvPostalCode;
            tvName = findViewById(R.id.building_name);
            tvCity = findViewById(R.id.building_city);
            tvUrl = findViewById(R.id.building_url);
            tvDescription = findViewById(R.id.building_description);
            tvAddress = findViewById(R.id.building_address);
            tvPostalCode = findViewById(R.id.building_postalCode);

            tvName.setText(buildingInfoList.get(0));
            tvCity.setText(buildingInfoList.get(1));
            tvUrl.setText(buildingInfoList.get(2));
            tvDescription.setText(buildingInfoList.get(3));
            tvAddress.setText(buildingInfoList.get(4));
            tvPostalCode.setText(buildingInfoList.get(5));

        }
    }


}
