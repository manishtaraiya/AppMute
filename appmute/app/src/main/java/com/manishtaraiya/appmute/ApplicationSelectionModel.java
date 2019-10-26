package com.manishtaraiya.appmute;


import android.graphics.drawable.Drawable;

public class ApplicationSelectionModel {
    private String packageName;
    private boolean isSelected;
    private String appName;
    private Drawable image;

    public ApplicationSelectionModel() {
    }

    public Drawable getImage() {
        return image;
    }

    public void setImage(Drawable image) {
        this.image = image;
    }

    public ApplicationSelectionModel(String packageName, boolean isSelected, String appName, Drawable image) {
        this.packageName = packageName;
        this.isSelected = isSelected;
        this.appName = appName;
        this.image = image;
    }


    public String getPackageName() {
        return packageName;
    }

    public void setPackageName(String packageName) {
        this.packageName = packageName;
    }

    public boolean isSelected() {
        return isSelected;
    }

    public void setSelected(boolean selected) {
        isSelected = selected;
    }

    public String getAppName() {
        return appName;
    }

    public void setAppName(String appName) {
        this.appName = appName;
    }
}
