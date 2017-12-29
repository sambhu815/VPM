package com.ecitta.android.leftdrawer;

/**
 * Created by parth.lad on 4/13/2016.
 */
public class NavDrawerItem {
    private boolean showNotify;
    private String title;
    int icon;


    public NavDrawerItem() {

    }

    public NavDrawerItem(boolean showNotify, String title, int icon) {
        this.showNotify = showNotify;
        this.title = title;
        this.icon = icon;
    }

    public boolean isShowNotify() {
        return showNotify;
    }

    public void setShowNotify(boolean showNotify) {
        this.showNotify = showNotify;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public int getIcon() {
        return icon;
    }

    public void setIcon(int icon) {
        this.icon = icon;
    }

    @Override
    public String toString() {
        return "NavDrawerItem{" +
                "showNotify=" + showNotify +
                ", title='" + title + '\'' +
                ", icon=" + icon +
                '}';
    }
}
