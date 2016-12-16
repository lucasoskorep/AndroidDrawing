package com.oskorep.lucas.drawingapp;

import android.content.Context;
import android.support.annotation.NonNull;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.ArrayList;

/**
 * Created by lucas on 11/4/2016.
 *
 * This class serves as a custom adapter for creating the Color drawer Navigation drawer.
 * The class should take in an arraylist of navColors and adapt them to the view using the getView function.
 *
 */

public class ColorAdapter extends ArrayAdapter<NavColor> {

    private final Context mContext;
    private final ArrayList<NavColor> colorArrayList;

    /**
     * Default adapter for the ColorAdapter class.
     *
     * @param context - The context fo teh application
     * @param colors - The color to be adapted into the view
     */
    public ColorAdapter(Context context, ArrayList<NavColor> colors){
        super(context, R.layout.color_drawer_item, colors);
        this.mContext = context;
        this.colorArrayList = colors;
    }

    /**
     * Returns a populated view for a listView item upon request from Android.SYSTEM
     *
     * @param position - position of the list to get a view for
     * @param convertView - The view to be converted/the current view
     * @param parent - The parent view/viewgroup for hte current view element.
     * @return The view containing the newly made color_drawer_item populated by the elements of the navColor ArrayList.
     */
    @Override@NonNull
    public View getView(int position, View convertView, ViewGroup parent){

        LayoutInflater inflater = (LayoutInflater) mContext.getSystemService(Context.LAYOUT_INFLATER_SERVICE);

        View view = inflater.inflate(R.layout.color_drawer_item, parent, false);

        ImageView imageView = (ImageView) view.findViewById(R.id.color_drawer_item);
        TextView textView = (TextView) view.findViewById(R.id.color_drawer_text);

        NavColor currColor = colorArrayList.get(position);

        imageView.setImageResource(currColor.getDrawColor());
        textView.setText(currColor.getName());

        return view;
    }
}
