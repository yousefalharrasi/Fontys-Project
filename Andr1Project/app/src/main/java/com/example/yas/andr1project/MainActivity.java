package com.example.yas.andr1project;

import android.content.Intent;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.util.Log;
import android.view.View;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.Toast;

import com.squareup.picasso.Picasso;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.AbstractQueue;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

public class MainActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener, TokenFragment.OnFragmentInteractionListener {

    ListView listViewB, listViewG;
    GridView gridView;
    ImageView imageView;
    static String tokenKey;
    List<Building> buildings = new ArrayList<>();
    List<Group> groups = new ArrayList<>();
    TokenFragment tokenFragment;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        setTitle("Home");
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        imageView = findViewById(R.id.imageView);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);


        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.addDrawerListener(toggle);
        toggle.syncState();

        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);

        FragmentManager fragmentManager = getSupportFragmentManager();
        FragmentTransaction fragTrans = fragmentManager.beginTransaction();
        tokenFragment = new TokenFragment();
        fragTrans.add(R.id.framelayout, tokenFragment);
        fragTrans.commit();


    }

    @Override
    public void onBackPressed() {
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            setTitle("Home");
            new TokenFragment.OnFragmentInteractionListener() {
                @Override
                public void onFragmentInteraction(String token) {
                    MainActivity.tokenKey = token;
                }
            };
            FragmentManager fragmentManager = getSupportFragmentManager();
            FragmentTransaction fragTrans = fragmentManager.beginTransaction();
            tokenFragment = new TokenFragment();
            fragTrans.replace(R.id.framelayout, tokenFragment);
            fragTrans.commit();
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        // Handle navigation view item clicks here.
        int id = item.getItemId();

        if (id == R.id.nav_building) {
            setTitle("Buildings");
            FragmentManager fragmentManager = getSupportFragmentManager();
            FragmentTransaction fragTrans = fragmentManager.beginTransaction();
            BuildingFragment buildingFragment = new BuildingFragment();
            fragTrans.replace(R.id.framelayout, buildingFragment);
            fragTrans.commit();

            BuildingAsyncTask buildingAsyncTask = new BuildingAsyncTask();
            buildingAsyncTask.execute();


        } else if (id == R.id.nav_group) {
            setTitle("Groups");
            FragmentManager fragmentManager = getSupportFragmentManager();
            FragmentTransaction fragTrans = fragmentManager.beginTransaction();
            GroupFragment groupFragment = new GroupFragment();
            fragTrans.replace(R.id.framelayout, groupFragment);
            fragTrans.commit();

            GroupAsyncTask groupAsyncTask = new GroupAsyncTask();
            groupAsyncTask.execute();
           /* String[] letters = new String[]{"A", "B", "C"};
            ArrayAdapter<String> xx = new ArrayAdapter<String>(MainActivity.this
                    , R.layout.support_simple_spinner_dropdown_item, letters);
            gridView = findViewById(R.id.groupGrid);
            gridView.setAdapter(xx);*/

        } else if (id == R.id.nav_logout) {
            setTitle("Home");

            new TokenFragment.OnFragmentInteractionListener() {
                @Override
                public void onFragmentInteraction(String token) {
                    MainActivity.tokenKey = null;
                    MainActivity.tokenKey = token;
                }
            };
            FragmentManager fragmentManager = getSupportFragmentManager();
            FragmentTransaction fragTrans = fragmentManager.beginTransaction();
            tokenFragment = new TokenFragment();
            fragTrans.replace(R.id.framelayout, tokenFragment);
            fragTrans.commit();
        }


        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }

    @Override
    public void onFragmentInteraction(String token) {
        this.tokenKey = token;
    }


    private class BuildingAsyncTask extends AsyncTask<Void, Void, Void> {
        URL url = null;

        @Override
        protected Void doInBackground(Void... voids) {
            try {
                buildings.clear();
                url = new URL("https://api.fhict.nl/buildings");
                HttpURLConnection connection = (HttpURLConnection) url.openConnection();
                connection.setRequestProperty("Accept", "application/json");
                connection.setRequestProperty("Authorization", "Bearer " + MainActivity.tokenKey);
                connection.connect();

                InputStream inputStream = connection.getInputStream();
                String jsonString = (new Scanner(inputStream)).useDelimiter("\\A").next();

                JSONArray jsonArray = new JSONArray(jsonString);
                for (int i = 0; i < jsonArray.length(); i++) {
                    JSONObject jsonObjectBuilding = jsonArray.getJSONObject(i);
                    String id = jsonObjectBuilding.getString("id");
                    String name = jsonObjectBuilding.getString("name");
                    String description = jsonObjectBuilding.getString("description");
                    String address = jsonObjectBuilding.getString("address");
                    String postalCode = jsonObjectBuilding.getString("postalCode");

                    buildings.add(new Building(id, name, description, address, postalCode));
                }


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
        protected void onPostExecute(Void aVoid) {

            listViewB = findViewById(R.id.listviewBuilding);
            List<String> buildingsNames = new ArrayList<>();
            for (Building b : buildings) {
                buildingsNames.add(b.getName());
            }
            ArrayAdapter<String> arrayAdapter = new ArrayAdapter<String>(MainActivity.this
                    , R.layout.support_simple_spinner_dropdown_item, buildingsNames);
            listViewB.setAdapter(arrayAdapter);
            listViewB.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                @Override
                public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                    Intent intent = new Intent(MainActivity.this, BuildingDetailsActivity.class);
                    intent.putExtra("id", buildings.get(i).getId());
                    startActivity(intent);
                }
            });
        }
    }


    private class GroupAsyncTask extends AsyncTask<Void, Void, Void> {
        URL url = null;

        @Override
        protected Void doInBackground(Void... voids) {
            try {
                groups.clear();
                url = new URL("https://api.fhict.nl/groups");
                HttpURLConnection connection = (HttpURLConnection) url.openConnection();
                connection.setRequestProperty("Accept", "application/json");
                connection.setRequestProperty("Authorization", "Bearer " + MainActivity.tokenKey);
                connection.connect();

                InputStream inputStream = connection.getInputStream();
                String jsonString = (new Scanner(inputStream)).useDelimiter("\\A").next();

                JSONArray jsonArray = new JSONArray(jsonString);
                for (int i = 0; i < jsonArray.length(); i++) {
                    JSONObject jsonObjectGroup = jsonArray.getJSONObject(i);
                    String id = jsonObjectGroup.getString("id");
                    String name = jsonObjectGroup.getString("groupName");
                    String url = jsonObjectGroup.getString("url");

                    groups.add(new Group(id, name, url));
                }


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
        protected void onPostExecute(Void aVoid) {

            listViewG = findViewById(R.id.listviewGroup);
            List<String> groupsNames = new ArrayList<>();
            for (Group g : groups) {
                groupsNames.add(g.getName());
            }
            ArrayAdapter<String> arrayAdapter = new ArrayAdapter<String>(MainActivity.this
                    , R.layout.support_simple_spinner_dropdown_item, groupsNames);
            listViewG.setAdapter(arrayAdapter);
            try {
                listViewG.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                    @Override
                    public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                        Intent intent = new Intent(MainActivity.this, GroupDetailsActivity.class);
                        intent.putExtra("id", groups.get(i).getId());
                        startActivity(intent);
                    }
                });
            } catch (Exception ex) {
                Toast.makeText(MainActivity.this, ex.getMessage(), Toast.LENGTH_SHORT).show();
            }

        }
    }




}
