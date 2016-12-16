package com.oskorep.lucas.drawingapp;

/**
 * Created by lucas on 11/4/2016.
 *
 * Small class which serves to hold the name, color, and id of a nav_drawer item in a single object
 * for injection into an Arraylist for the default AdapterView constructor
 *
 */

class NavColor {


    private String name;
    private int color;
    private int resourceID;

    /**
     * Default constructor for the navColor
     *
     * @param name - Name of the navColor
     * @param colorID - ID of the navColor
     * @param color - Color fo the navColor(in HEX)
     */
    NavColor(String name, int colorID, int color){
        this.name = name;
        this.resourceID = colorID;
        this.color = color;
    }

    /**
     * @return the color of a navColor
     */
    int getDrawColor(){
        return resourceID;
    }

    /**
     * @return the name of the navColor
     */
    public String getName(){
        return name;
    }

    /**
     * @return The color (HEX) of the navColor
     */
    public int getColor(){
        return color;
    }
}
