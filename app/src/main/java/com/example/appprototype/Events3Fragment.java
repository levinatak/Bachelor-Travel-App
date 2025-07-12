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
 * Use the {@link Events3Fragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class Events3Fragment extends Fragment {

    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    private String mParam1;
    private String mParam2;

    private View view;
    private LinearLayout layoutList;
    private LinearLayout layoutList2;

    public Events3Fragment() {
        // Required empty public constructor
    }

    public static Events3Fragment newInstance(String param1, String param2) {
        Events3Fragment fragment = new Events3Fragment();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1);
        args.putString(ARG_PARAM2, param2);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mParam1 = getArguments().getString(ARG_PARAM1);
            mParam2 = getArguments().getString(ARG_PARAM2);
        }
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.fragment_events3, container, false);
        return view;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        layoutList = view.findViewById(R.id.layout_list);
        layoutList2 = view.findViewById(R.id.layout_list2);

        //VorschlagViewModel modelVsg = new ViewModelProvider(requireActivity()).get(VorschlagViewModel.class);
        //observeVorschlagList(modelVsg);

        StringViewModel modelVsg = new ViewModelProvider(requireActivity()).get(StringViewModel.class);
        observeVorschlagList(modelVsg);

        StringArrayViewModel modelEvtArray = new ViewModelProvider(requireActivity()).get(StringArrayViewModel.class);
        observeEventList(modelEvtArray);
    }

    private void observeVorschlagList(StringViewModel modelVsg) {
        modelVsg.getList("evtList").observe(getViewLifecycleOwner(), vsgList -> {
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

    private void observeEventList(StringArrayViewModel modelEvtArray) {
        modelEvtArray.getList("evtList").observe(getViewLifecycleOwner(), evtList -> {
            layoutList2.removeAllViews();
            for (String[] evtData : evtList) {
                Log.d("EVT LOG", "Displaying " + evtData[0]);
                View evtView = getLayoutInflater().inflate(R.layout.row_submitted_events, null, false);
                layoutList2.addView(evtView);
                TextView evtTitle = evtView.findViewById(R.id.title_elem);
                evtTitle.setText(evtData[0]);
                TextView evtPlace = evtView.findViewById(R.id.place_elem);
                evtPlace.setText(evtData[1]);
                TextView evtDate = evtView.findViewById(R.id.date_elem);
                evtDate.setText(evtData[2]);
            }
        });
    }
}
