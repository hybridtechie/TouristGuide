package org.hybrid.touristguide.touristguide;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.renderscript.Double2;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import org.w3c.dom.Text;

public class PlaceActitivity extends AppCompatActivity {

    private TextView title;
    private TextView description;
    private Button directionButton;
    private ImageView imageView;
    private Double lat;
    private Double lon;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_place);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);


        title=(TextView)findViewById(R.id.title);
        description=(TextView)findViewById(R.id.description);
        imageView=(ImageView) findViewById(R.id.placeImage);
        directionButton=(Button) findViewById(R.id.button);


        Intent intent=getIntent();
        title.setText(intent.getStringExtra("TITLE"));
        description.setText(intent.getStringExtra("DESCRIPTION"));
        new ImageLoaderAsync(intent.getStringExtra("URL"), imageView).execute();


        lat=Double.parseDouble(intent.getStringExtra("LAT"));
        lon= Double.parseDouble(intent.getStringExtra("LON"));

        directionButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Uri gmmIntentUri = Uri.parse("google.navigation:q="+lat+","+lon);
                Intent mapIntent = new Intent(Intent.ACTION_VIEW, gmmIntentUri);
                mapIntent.setPackage("com.google.android.apps.maps");
                startActivity(mapIntent);
            }
        });

    }

}
