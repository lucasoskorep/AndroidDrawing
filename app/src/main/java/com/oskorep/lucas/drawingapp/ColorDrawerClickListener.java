package com.oskorep.lucas.drawingapp;

import android.app.Activity;
import android.content.Context;
import android.support.v4.widget.DrawerLayout;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.Toast;

import com.azeesoft.lib.colorpicker.ColorPickerDialog;

import java.util.ArrayList;

/**
 * Created by lucas on 11/5/2016
 * This class serves as a ClickListener for the color drawer items.
 * The listener should get the proper navColor for the list of navColors and set the color according to which button was hit last.
 */

class ColorDrawerClickListener implements ListView.OnItemClickListener {

    private Context mContext;
    private CanvasView mCanvas;
    private ArrayList<NavColor> navColors;

    /**
     * Default constructor, assigns the variables totheir corresponding class variables
     *
     * @param context - context of the application.
     * @param canvas  - CanvasView to edit the functionality of.
     * @param colors  - ArrayList of colors that can be clicked.
     */
    ColorDrawerClickListener(Context context, CanvasView canvas, ArrayList<NavColor> colors) {
        mContext = context;
        mCanvas = canvas;
        navColors = colors;

    }

    /**
     * Sets the color fo the canvas to the one clicked by the user in the color drawer.
     *
     * @param parent   View that was activated
     * @param view     - View of the clicked which was clicked
     * @param position - Position of hte child in the ArrayList
     * @param id       - ID of the child if assigned one.
     */
    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        if (position == 0) {
            closeLayout();
            createColorPicker();


        } else {
            selectItem(position);
        }
    }

    /**
     * Creates a ColorPicker from the ColorPicker library in android assets.
     * The output of the colorpicker should be used to update the brush in hte main activity.
     *
     */
    private void createColorPicker(){

        ColorPickerDialog colorPickerDialog = ColorPickerDialog.createColorPickerDialog(mContext, ColorPickerDialog.LIGHT_THEME);
        colorPickerDialog.setOnColorPickedListener(new ColorPickerDialog.OnColorPickedListener() {
            @Override
            public void onColorPicked(int color, String hexVal) {
                mCanvas.setBrushColor(color);
            }
        });
        colorPickerDialog.show();

    }

    /**
     * Sets the brush color to the selectedItem from the slide-in drawer layout menu.
     * @param position - The position of the item in the list.
     */
    private void selectItem(int position) {

        NavColor currColor = navColors.get(position);
        mCanvas.setBrushColor(currColor.getColor());
        closeLayout();

    }


    /**
     * Closes the slide-in drawer layout menu
     */
    private void closeLayout(){
        DrawerLayout closeLayout = (DrawerLayout) ((Activity) mContext).findViewById(R.id.drawer_layout);
        ListView closeList = (ListView) ((Activity) mContext).findViewById(R.id.drawer_list);

        closeLayout.closeDrawer(closeList);
    }
}
