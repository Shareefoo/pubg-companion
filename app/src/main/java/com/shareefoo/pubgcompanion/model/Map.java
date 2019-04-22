package com.shareefoo.pubgcompanion.model;

import android.graphics.Bitmap;

public class Map {

    private String mapName;
    private Bitmap mapPicture;

    public Map(String mapName, Bitmap mapPicture) {
        this.mapName = mapName;
        this.mapPicture = mapPicture;
    }

    public String getMapName() {
        return mapName;
    }

    public void setMapName(String mapName) {
        this.mapName = mapName;
    }

    public Bitmap getMapPicture() {
        return mapPicture;
    }

    public void setMapPicture(Bitmap mapPicture) {
        this.mapPicture = mapPicture;
    }
}
