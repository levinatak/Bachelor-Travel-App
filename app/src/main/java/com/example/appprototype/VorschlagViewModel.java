package com.example.appprototype;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class VorschlagViewModel extends ViewModel
{
    private final Map<String, MutableLiveData<ArrayList<VorschlagListItem>>> dataMap = new HashMap<>();

    public void setList(String tag, ArrayList<VorschlagListItem> list)
    {
        MutableLiveData<ArrayList<VorschlagListItem>> liveData = dataMap.get(tag);
        if (liveData == null) {
            liveData = new MutableLiveData<>();
            dataMap.put(tag, liveData);
        }
        liveData.setValue(list);
    }

    public LiveData<ArrayList<VorschlagListItem>> getList(String tag)
    {
        MutableLiveData<ArrayList<VorschlagListItem>> liveData = dataMap.get(tag);
        if (liveData == null) {
            liveData = new MutableLiveData<>();
            dataMap.put(tag, liveData);
        }
        return liveData;
    }
}

