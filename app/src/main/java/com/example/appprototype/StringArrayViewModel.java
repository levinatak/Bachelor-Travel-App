package com.example.appprototype;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class StringArrayViewModel extends ViewModel
{
    private final Map<String, MutableLiveData<ArrayList<String[]>>> dataMap = new HashMap<>();

    public void setList(String tag, ArrayList<String[]> list)
    {
        MutableLiveData<ArrayList<String[]>> liveData = dataMap.get(tag);
        if (liveData == null) {
            liveData = new MutableLiveData<>();
            dataMap.put(tag, liveData);
        }
        liveData.setValue(list);
    }

    public LiveData<ArrayList<String[]>> getList(String tag)
    {
        MutableLiveData<ArrayList<String[]>> liveData = dataMap.get(tag);
        if (liveData == null) {
            liveData = new MutableLiveData<>();
            dataMap.put(tag, liveData);
        }
        return liveData;
    }
}
