package org.hybrid.touristguide.touristguide;

import android.*;
import android.Manifest;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.LocationManager;
import android.os.Build;
import android.os.Bundle;
import android.provider.Settings;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.Toast;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.io.IOException;
import java.io.InputStream;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

public class MainActivity extends AppCompatActivity {


    private ListView listView;
    private PlaceListAdapter placeListAdapter;
private  AlertDialog alert;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        listView=(ListView)findViewById(R.id.placeList);
        loadPlaces();

        if(Build.VERSION.SDK_INT >= 23) {
            if (ContextCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {

                ActivityCompat.requestPermissions(this, new String[]{android.Manifest.permission.ACCESS_COARSE_LOCATION, Manifest.permission.ACCESS_COARSE_LOCATION},
                        Constants.MY_PERMISSION_ACCESS_COURSE_LOCATION);
            }
        }

        LocationManager service = (LocationManager) getSystemService(LOCATION_SERVICE);
        boolean enabled = service
                .isProviderEnabled(LocationManager.GPS_PROVIDER);
        Log.d("MainActivity"," Location GPS Enabled ? : "+enabled);
        if (!enabled) {
            Intent intent = new Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS);
            startActivity(intent);
        }
        else{
            new LocationService(this, new LocationChangedListener() {
                @Override
                public void onLocationchange(Object result) {
                    Log.d("MainActivity"," Latitude"+Constants.Latitude.toString());
                    Toast.makeText(MainActivity.this, Constants.Latitude.toString(), Toast.LENGTH_SHORT).show();
                    if(Constants.Latitude!=0) {
                        checkNearbyTouristAttractions();
                    }
                }
            });
        }

    }

    private void checkNearbyTouristAttractions() {

    sortLocations(Constants.Latitude,Constants.Longitude);

        if(alert!=null && alert.isShowing()) {

        }
        else{
            AlertDialog.Builder builder;
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                builder = new AlertDialog.Builder(this, android.R.style.Theme_Material_Dialog_Alert);
            } else {
                builder = new AlertDialog.Builder(this);
            }
            builder.setTitle("Goto Location")
                    .setMessage("You are near a tourist location. Do you wish to see details?")
                    .setPositiveButton(android.R.string.yes, new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int which) {
                            Intent intent = new Intent(MainActivity.this, PlaceActitivity.class);
                            intent.putExtra("TITLE", Constants.PLACELIST.get(0).getTitle());
                            intent.putExtra("DESCRIPTION", Constants.PLACELIST.get(0).getDescription());
                            intent.putExtra("URL", Constants.PLACELIST.get(0).getUrl());
                            intent.putExtra("LAT", Constants.PLACELIST.get(0).getLat());
                            intent.putExtra("LON", Constants.PLACELIST.get(0).getLon());
                            MainActivity.this.startActivity(intent);
                        }
                    })
                    .setNegativeButton(android.R.string.no, new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int which) {

                        }
                    })
                    .setIcon(android.R.drawable.ic_dialog_alert);
            alert = builder.create();
            alert.show();
        }

    }


    private void loadPlaces(){
    String data=loadJSONFromAsset();
    Gson gson=new Gson();
    List<Place> placeList= gson.fromJson(data,new TypeToken<List<Place>>(){}.getType());
        Constants.PLACELIST=placeList;

for(Place place:Constants.PLACELIST){
    Log.v("Tourist","Name : "+place.getTitle());
    Log.v("Tourist","Url : "+place.getUrl());
    Log.v("Tourist","Lat : "+place.getLat());
    Log.v("Tourist","Lon : "+place.getLon());
}


    setListView();


    }



    public void sortLocations(final double myLatitude,final double myLongitude) {
        Comparator comp = new Comparator<Place>() {
            @Override
            public int compare(Place p1, Place p2) {
                float[] result1 = new float[3];
                android.location.Location.distanceBetween(myLatitude, myLongitude, Double.parseDouble(p1.getLat()), Double.parseDouble(p1.getLon()), result1);
                Float distance1 = result1[0];

                float[] result2 = new float[3];
                android.location.Location.distanceBetween(myLatitude, myLongitude, Double.parseDouble(p2.getLat()), Double.parseDouble(p2.getLon()), result2);
                Float distance2 = result2[0];

                return distance1.compareTo(distance2);
            }
        };


        Collections.sort(Constants.PLACELIST, comp);

    }




    private void setListView(){


        placeListAdapter=new PlaceListAdapter(this,R.layout.activity_item_place,Constants.PLACELIST);
        listView.setAdapter(placeListAdapter);



    }


    public String loadJSONFromAsset() {
        String json = null;
        try {

            InputStream is = getAssets().open("places.json");

            int size = is.available();

            byte[] buffer = new byte[size];

            is.read(buffer);

            is.close();

            json = new String(buffer, "UTF-8");


        } catch (IOException ex) {
            ex.printStackTrace();
            return null;
        }
        return json;

    }


}
