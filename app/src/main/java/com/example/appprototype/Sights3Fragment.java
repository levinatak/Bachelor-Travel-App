package com.example.appprototype;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link Sights3Fragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class Sights3Fragment extends Fragment
{
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    private String mParam1;
    private String mParam2;

    View view;
    LinearLayout layoutList;
    LinearLayout layoutList2;

    public Sights3Fragment()
    {
        // Required empty public constructor
    }

    public static Sights3Fragment newInstance(String param1, String param2)
    {
        Sights3Fragment fragment = new Sights3Fragment();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1);
        args.putString(ARG_PARAM2, param2);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mParam1 = getArguments().getString(ARG_PARAM1);
            mParam2 = getArguments().getString(ARG_PARAM2);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState)
    {
        // Inflate the layout for this fragment
        view = inflater.inflate(R.layout.fragment_sights3, container, false);
        return view;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState)
    {
        layoutList = view.findViewById(R.id.layout_list);
        layoutList2 = view.findViewById(R.id.layout_list2);

        StringViewModel modelVsg = new ViewModelProvider(requireActivity()).get(StringViewModel.class);
        observeVorschlagList(modelVsg);

        StringArrayViewModel modelSgtArray = new ViewModelProvider(requireActivity()).get(StringArrayViewModel.class);
        observeSightList(modelSgtArray);
    }

    private void observeVorschlagList(StringViewModel modelVsg) {
        modelVsg.getList("sgtList").observe(getViewLifecycleOwner(), vsgList -> {
            layoutList.removeAllViews();
            for (String item : vsgList) {
                Log.d("EVT LOG", "Vorschlag " + item);
                View vsgView = getLayoutInflater().inflate(R.layout.row_submitted_vorschlag, null, false);
                layoutList.addView(vsgView);
                TextView vsgText = vsgView.findViewById(R.id.title_elem);
                vsgText.setText(item);
            }
        });
    }

    private void observeSightList(StringArrayViewModel modelEvtArray) {
        modelEvtArray.getList("sgtList").observe(getViewLifecycleOwner(), sightList -> {
            layoutList2.removeAllViews();
            for (String[] evtData : sightList) {
                Log.d("EVT LOG", "Displaying " + evtData[0]);
                View sightView = getLayoutInflater().inflate(R.layout.row_submitted_events, null, false);
                layoutList2.addView(sightView);
                TextView sgtTitle = sightView.findViewById(R.id.title_elem);
                sgtTitle.setText(evtData[0]);
                TextView sgtPlace = sightView.findViewById(R.id.place_elem);
                sgtPlace.setText(evtData[1]);
            }
        });
    }
}