package com.example.appprototype;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.lifecycle.ViewModelProvider;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CheckBox;

import java.util.ArrayList;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link SightsFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class SightsFragment extends Fragment
{
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    private String mParam1;
    private String mParam2;

    View view;
    private ArrayList<String> vsgList = new ArrayList<>();

    public SightsFragment()
    {
        // Required empty public constructor
    }

    public static SightsFragment newInstance(String param1, String param2)
    {
        SightsFragment fragment = new SightsFragment();
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
        view = inflater.inflate(R.layout.fragment_sights, container, false);
        return view;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState)
    {
        CheckBox cbKultur = (CheckBox) view.findViewById(R.id.cbKultur);
        CheckBox cbNatur = (CheckBox) view.findViewById(R.id.cbNatur);
        CheckBox cbKunst = (CheckBox) view.findViewById(R.id.cbArchitektur);
        CheckBox cbGeschichte = (CheckBox) view.findViewById(R.id.cbUnterhaltung);
        CheckBox cbAnderes = (CheckBox) view.findViewById(R.id.cbAnderes);

        Button btnSights2 = view.findViewById(R.id.btnSights2);
        btnSights2.setOnClickListener(v -> proceedToNextFragment(cbKultur, cbNatur, cbKunst, cbGeschichte, cbAnderes));
    }

    private void proceedToNextFragment(CheckBox cbKultur, CheckBox cbNatur, CheckBox cbArchitektur, CheckBox cbUnterhaltung, CheckBox cbAnderes) {

        if(cbKultur.isChecked()) {
            vsgList.add(" Kultur");
        }
        if(cbNatur.isChecked()) {
            vsgList.add(" Natur");
        }
        if(cbArchitektur.isChecked()) {
            vsgList.add(" Architektur");
        }
        if(cbUnterhaltung.isChecked()) {
            vsgList.add(" Unterhaltung");
        }
        if(cbAnderes.isChecked()) {
            vsgList.add(" Anderes");
        }

        StringViewModel model = new ViewModelProvider(requireActivity()).get(StringViewModel.class);
        model.setList("sgtList", vsgList);

        Sights2Fragment sgt2Frag = new Sights2Fragment();
        FragmentManager fragMan = getFragmentManager();
        if (fragMan != null) {
            fragMan.beginTransaction()
                    .replace(R.id.frame_layout, sgt2Frag)
                    .commit();
        }
    }
}