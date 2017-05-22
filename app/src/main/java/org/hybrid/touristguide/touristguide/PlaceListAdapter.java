package org.hybrid.touristguide.touristguide;

import android.content.Context;
import android.content.Intent;
import android.graphics.Typeface;
import android.media.Image;
import android.support.annotation.LayoutRes;
import android.support.annotation.NonNull;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import java.net.MalformedURLException;
import java.net.URISyntaxException;
import java.net.URL;
import java.util.List;

/**
 * Created by nithi on 11-05-2017.
 */

public class PlaceListAdapter extends ArrayAdapter<Place> {

    List<Place> placeList;
    Context context;
    protected LayoutInflater mInflater;

    public PlaceListAdapter(@NonNull Context context, @LayoutRes int resource, @NonNull List<Place> objects) {
        super(context, resource, objects);
        placeList=objects;
        this.context=context;
        mInflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
    }



    @Override
    public View getView(int position, View convertView, ViewGroup parent) {


        ViewHolder viewHolder;
        final Place place = getItem(position);
        if (convertView != null) {
            viewHolder = (ViewHolder) convertView.getTag();

        } else {
            convertView = mInflater.inflate(R.layout.activity_item_place, parent, false);
            viewHolder = new ViewHolder();
            viewHolder.title = (TextView) convertView.findViewById(R.id.title);
            viewHolder.image = (ImageView) convertView.findViewById(R.id.placeImage);
            convertView.setTag(viewHolder);
        }
        viewHolder.title.setTypeface(null, Typeface.BOLD|Typeface.ITALIC);
        viewHolder.title.setText(place.getTitle());
        new ImageLoaderAsync(place.getUrl(), viewHolder.image).execute();


        convertView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent=new Intent(context, PlaceActitivity.class);
                intent.putExtra("TITLE",place.getTitle());
                intent.putExtra("DESCRIPTION",place.getDescription());
                intent.putExtra("URL",place.getUrl());
                intent.putExtra("LAT",place.getLat());
                intent.putExtra("LON",place.getLon());
                context.startActivity(intent);

            }
        });

        return convertView;

    }


    private static class ViewHolder {
        public TextView title;
        public ImageView image;

    }

}
