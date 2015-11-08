package com.wallet.utilis;

/**
 * Created by Raymond on 8/11/2015.
 */
public class MenuItem {
    public String  title;
    public Integer imageResource;
    public String cost;
    public int quantity;

    public MenuItem(String title, Integer imageResource, String cost) {
        this.title = title;
        this.imageResource = imageResource;
        this.cost = cost;
        quantity = 0;
    }
}
