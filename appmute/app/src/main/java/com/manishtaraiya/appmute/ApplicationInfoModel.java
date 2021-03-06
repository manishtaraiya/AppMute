package com.manishtaraiya.appmute;


import android.graphics.drawable.Drawable;

public class ApplicationInfoModel {
    private String packageName;
    private boolean isSelected;
    private String appName;

    public ApplicationInfoModel() {
    }


    public ApplicationInfoModel(String packageName, boolean isSelected, String appName) {
        this.packageName = packageName;
        this.isSelected = isSelected;
        this.appName = appName;

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
