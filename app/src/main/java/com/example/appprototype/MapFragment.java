package com.example.appprototype;

import android.os.Bundle;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;
import androidx.lifecycle.ViewModelProvider;

import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Toast;

import java.io.IOException;
import java.util.ArrayList;

public class MapFragment extends Fragment {

    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    private String mParam1;
    private String mParam2;
    private View view;
    private EditText sourceInput;
    private EditText destinationInput;
    private ImageButton btnAdd;
    private LinearLayout layoutList;
    private ArrayList<String> waypointList = new ArrayList<>();
    private ArrayList<String> routeList = new ArrayList<>();

    public MapFragment() {
        // Required empty public constructor
    }

    public static MapFragment newInstance(String param1, String param2) {
        MapFragment fragment = new MapFragment();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1);
        args.putString(ARG_PARAM2, param2);
        fragment.setArguments(args);
        return fragment;
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.fragment_map, container, false);
        return view;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        sourceInput = view.findViewById(R.id.tfSource);
        destinationInput = view.findViewById(R.id.tfDest);
        layoutList = view.findViewById(R.id.layout_list);
        btnAdd = view.findViewById(R.id.btnAdd);

        btnAdd.setOnClickListener(v -> {
            try {
                addView();
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        });

        Button submitButton = view.findViewById(R.id.btnSubmit);
        submitButton.setOnClickListener(v -> startNavigation());
    }

    private void startNavigation() {
        String source = sourceInput.getText().toString();
        String destination = destinationInput.getText().toString();

        if (TextUtils.isEmpty(source) || TextUtils.isEmpty(destination)) {
            Toast.makeText(getActivity(), "Start und Ziel müssen angegeben werden", Toast.LENGTH_SHORT).show();
            return;
        }

        source = capFirstLetter(source);
        destination = capFirstLetter(destination);

        routeList.clear();
        waypointList.clear();

        routeList.add(formatInput(source));
        addWaypointsToList();
        routeList.add(formatInput(destination));

        StringViewModel modelRoute = new ViewModelProvider(requireActivity()).get(StringViewModel.class);
        modelRoute.setList("routeList", routeList);

        // Übergang zu einem neuen Fragment zur Anzeige der Navigation
        Map2Fragment map2Frag = Map2Fragment.newInstance(source, destination, waypointList);
        FragmentTransaction transaction = getFragmentManager().beginTransaction();
        transaction.replace(R.id.frame_layout, map2Frag);
        transaction.commit();
        transaction.addToBackStack(null);
    }

    private void addView() throws IOException {
        View wpView = getLayoutInflater().inflate(R.layout.waypoint_add, null, false);

        ImageView imgRemove = wpView.findViewById(R.id.img_remove);
        imgRemove.setOnClickListener(v -> removeView(wpView));

        layoutList.addView(wpView);
        Log.d("API LOG", "View added to layoutList");
    }

    private void removeView(View v) {
        layoutList.removeView(v);
    }

    private void addWaypointsToList() {
        for (int i = 0; i < layoutList.getChildCount(); i++) {
            EditText tfWaypoint = layoutList.getChildAt(i).findViewById(R.id.tfWaypoint);
            String strWaypoint = tfWaypoint.getText().toString();
            if (!TextUtils.isEmpty(strWaypoint)) {
                strWaypoint = capFirstLetter(strWaypoint);
                waypointList.add(strWaypoint);
                routeList.add(formatInput(strWaypoint));
            }
        }
    }

    private String formatInput(String input) {
        return " " + capFirstLetter(input.trim()) + " ";
    }

    private String capFirstLetter(String input) {
        return input.substring(0, 1).toUpperCase() + input.substring(1);
    }
}
