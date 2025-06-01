package com.example.mymobileapp.model;


public class GridItem {
    private String name;
    private int imageResource;

    public GridItem(String name, int imageResource) {
        this.name = name;
        this.imageResource = imageResource;
    }

    public String getName() {
        return name;
    }

    public int getImageResource() {
        return imageResource;
    }
}


