package cn.mijack.persistentsearchdemo.model;

/**
 * Created by Mr.Yuan on 2017/8/19.
 */

import java.util.List;

/**
 * Copyright 2017 bejson.com
 */
public class UserResponse {

    private int total_count;
    private boolean incomplete_results;
    private List<User> items;

    public void setTotal_count(int total_count) {
        this.total_count = total_count;
    }

    public int getTotal_count() {
        return total_count;
    }

    public void setIncomplete_results(boolean incomplete_results) {
        this.incomplete_results = incomplete_results;
    }

    public boolean getIncomplete_results() {
        return incomplete_results;
    }

    public void setItems(List<User> items) {
        this.items = items;
    }

    public List<User> getItems() {
        return items;
    }

}