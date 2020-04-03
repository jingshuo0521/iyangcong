package com.iyangcong.reader.bean;

/**
 * Created by Administrator on 2017/4/19.
 */

public class MineShoppingPrice {
    private double original_price;
    private double free_status;
    private double virtual_price;
    private double original_price_name;
    private double special_price;
    private double price;
    private double virtual_status;
    private double price_type;
    private double special_status;

    public double getFree_status() {
        return free_status;
    }

    public void setFree_status(double free_status) {
        this.free_status = free_status;
    }

    public double getOriginal_price() {
        return original_price;
    }

    public void setOriginal_price(double original_price) {
        this.original_price = original_price;
    }

    public double getOriginal_price_name() {
        return original_price_name;
    }

    public void setOriginal_price_name(double original_price_name) {
        this.original_price_name = original_price_name;
    }

    public double getPrice() {
        return price;
    }

    public void setPrice(double price) {
        this.price = price;
    }

    public double getPrice_type() {
        return price_type;
    }

    public void setPrice_type(double price_type) {
        this.price_type = price_type;
    }

    public double getSpecial_price() {
        return special_price;
    }

    public void setSpecial_price(double special_price) {
        this.special_price = special_price;
    }

    public double getSpecial_status() {
        return special_status;
    }

    public void setSpecial_status(double special_status) {
        this.special_status = special_status;
    }

    public double getVirtual_price() {
        return virtual_price;
    }

    public void setVirtual_price(double virtual_price) {
        this.virtual_price = virtual_price;
    }

    public double getVirtual_status() {
        return virtual_status;
    }

    public void setVirtual_status(double virtual_status) {
        this.virtual_status = virtual_status;
    }
}
