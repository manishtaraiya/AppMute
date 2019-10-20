package com.manishtaraiya.appmute;


public class ApplicationInfoModel {
    private String packageName;
    private boolean isSelected;

    public ApplicationInfoModel() {
    }

    public ApplicationInfoModel(String packageName, boolean isSelected) {
        this.packageName = packageName;
        this.isSelected = isSelected;
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
}
