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
 * Use the {@link EventsFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class EventsFragment extends Fragment {

    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    private String mParam1;
    private String mParam2;

    private View view;
    private ArrayList<String> vsgList = new ArrayList<>();

    public EventsFragment() {
        // Required empty public constructor
    }

    public static EventsFragment newInstance(String param1, String param2) {
        EventsFragment fragment = new EventsFragment();
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
        view = inflater.inflate(R.layout.fragment_events, container, false);
        return view;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        CheckBox cbMusikfeste = view.findViewById(R.id.cbMusikfeste);
        CheckBox cbTheater = view.findViewById(R.id.cbTheater);
        CheckBox cbSport = view.findViewById(R.id.cbSport);
        CheckBox cbKunst = view.findViewById(R.id.cbKunst);
        CheckBox cbWeiterbildung = view.findViewById(R.id.cbWeiterbildung);
        CheckBox cbAnderes = view.findViewById(R.id.cbAnderes);

        Button btnEvt2 = view.findViewById(R.id.btnEvt2);
        btnEvt2.setOnClickListener(v -> proceedToNextFragment(cbMusikfeste, cbTheater, cbSport, cbKunst, cbWeiterbildung, cbAnderes));
    }

    private void proceedToNextFragment(CheckBox cbMusikfeste, CheckBox cbTheater, CheckBox cbSport, CheckBox cbKunst, CheckBox cbWeiterbildung,CheckBox cbAnderes) {

        if (cbMusikfeste.isChecked()) {
            vsgList.add(" Musik/Feste");
        }
        if (cbTheater.isChecked()) {
            vsgList.add(" Theater/Performance");
        }
        if (cbSport.isChecked()) {
            vsgList.add(" Sport");
        }
        if (cbKunst.isChecked()) {
            vsgList.add(" Kunst");
        }
        if (cbWeiterbildung.isChecked()) {
            vsgList.add(" Weiterbildung");
        }
        if (cbAnderes.isChecked()) {
            vsgList.add(" Anderes");
        }

        StringViewModel model = new ViewModelProvider(requireActivity()).get(StringViewModel.class);
        model.setList("evtList", vsgList);

        Events2Fragment evt2Frag = new Events2Fragment();
        FragmentManager fragMan = getFragmentManager();
        if (fragMan != null) {
            fragMan.beginTransaction()
                    .replace(R.id.frame_layout, evt2Frag)
                    .commit();
        }
    }
}
