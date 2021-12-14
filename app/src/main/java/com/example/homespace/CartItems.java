package com.example.homespace;

import androidx.lifecycle.MutableLiveData;

import java.util.ArrayList;

public class CartItems {

    public static ArrayList<AgentInfoAdapter> agent = new ArrayList<>();
    public static ArrayList<AgentInfoAdapter> compareList = new ArrayList<>();

    public static MutableLiveData<Boolean> comparelistchecker = new MutableLiveData<Boolean>();
    public static MutableLiveData<Boolean> updateRecyclerView = new MutableLiveData<Boolean>();

    public CartItems() { }
}
