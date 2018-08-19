package com.shareefoo.pubgcompanion.model;

import com.google.gson.annotations.SerializedName;

public class Player {

    @SerializedName("data")
    private PlayerSeasonMatchesData data;

    public PlayerSeasonMatchesData getData() {
        return data;
    }

    public void setData(PlayerSeasonMatchesData data) {
        this.data = data;
    }

}
