package com.example.appprototype;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;

import java.util.ArrayList;

public class Map2Fragment extends Fragment {

    private static final String ARG_SOURCE = "source";
    private static final String ARG_DESTINATION = "destination";
    private static final String ARG_WAYPOINTS = "waypoints";
    private View view;
    private String source;
    private String destination;
    private ArrayList<String> waypoints;
    private RadioGroup radioGroup;
    private Button btnSubmit;
    private String travelMode;

    public static Map2Fragment newInstance(String source, String destination, ArrayList<String> waypoints) {
        Map2Fragment fragment = new Map2Fragment();
        Bundle args = new Bundle();
        args.putString(ARG_SOURCE, source);
        args.putString(ARG_DESTINATION, destination);
        args.putStringArrayList(ARG_WAYPOINTS, waypoints);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            source = getArguments().getString(ARG_SOURCE);
            destination = getArguments().getString(ARG_DESTINATION);
            waypoints = getArguments().getStringArrayList(ARG_WAYPOINTS);
        }
    }
    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.fragment_map2, container, false);
        return view;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        radioGroup = view.findViewById(R.id.radioGroup);
        btnSubmit = view.findViewById(R.id.btnSubmit);

        // Setzen des OnCheckedChangeListener für die RadioGroup
        radioGroup.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup group, int checkedId) {
                // Einschalten des "Weiter"-Buttons, sobald ein Fortbewgungsmittel gewählt wurde
                btnSubmit.setEnabled(true);
                btnSubmit.setAlpha(1);
            }
        });

        btnSubmit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                int selectedId = radioGroup.getCheckedRadioButtonId();
                if (selectedId != -1) {
                    if (selectedId == R.id.radio_auto) {
                        travelMode = "auto";
                    } else if (selectedId == R.id.radio_bike) {
                        travelMode = "fahrrad";
                    } else if (selectedId == R.id.radio_transit) {
                        travelMode = "oepnv";
                    } else if (selectedId == R.id.radio_walk) {
                        travelMode = "laufen";
                    } else {
                        travelMode = "auto";
                    }

                    // Übergang zu einem neuen Fragment zur Anzeige der Navigation
                    Map3Fragment map2Frag = Map3Fragment.newInstance(source, destination, waypoints, travelMode);
                    FragmentTransaction transaction = getFragmentManager().beginTransaction();
                    transaction.replace(R.id.frame_layout, map2Frag);
                    transaction.addToBackStack(null);
                    transaction.commit();
                } else {
                    Toast.makeText(getActivity(), "Bitte wählen Sie eine Option", Toast.LENGTH_SHORT).show();
                }
            }
        });
    }
}
