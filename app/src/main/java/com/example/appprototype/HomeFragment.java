package com.example.appprototype;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import org.w3c.dom.Text;

import java.util.ArrayList;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link HomeFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class HomeFragment extends Fragment
{
    View view;
    LinearLayout routeLayoutList;
    LinearLayout evtLayoutList;
    LinearLayout sgtLayoutList;

    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    public HomeFragment()
    {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment HomeFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static HomeFragment newInstance(String param1, String param2)
    {
        HomeFragment fragment = new HomeFragment();
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
        view = inflater.inflate(R.layout.fragment_home, container, false);
        return view;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState)
    {
        StringViewModel modelRoute = new ViewModelProvider(requireActivity()).get(StringViewModel.class);
        routeLayoutList = view.findViewById(R.id.route_list);
        modelRoute.getList("routeList").observe(getViewLifecycleOwner(), new Observer<ArrayList<String>>() {
            @Override
            public void onChanged(ArrayList<String> routeList) {
                String start = routeList.get(0);
                String destination = routeList.get(routeList.size()-1);

                View routeView = getLayoutInflater().inflate(R.layout.row_submitted_routes, null, false);
                routeLayoutList.addView(routeView);
                TextView tfStart = routeLayoutList.getChildAt(0).findViewById(R.id.start_elem);
                tfStart.setText(start);
                TextView tfDest = routeView.findViewById(R.id.dest_elem);
                tfDest.setText(destination);

                LinearLayout waypointLayout = routeView.findViewById(R.id.horizExtra);

                for(int i = 1; i < (routeList.size()-1); i++)
                {
                    View waypointView = getLayoutInflater().inflate(R.layout.row_submitted_waypoints, null, false);
                    waypointLayout.addView(waypointView);
                    TextView tfWaypoint = waypointLayout.getChildAt(i-1).findViewById(R.id.waypoint_elem);
                    tfWaypoint.setText(routeList.get(i));
                }
            }
        });


        StringArrayViewModel modelEvtArray = new ViewModelProvider(requireActivity()).get(StringArrayViewModel.class);
        evtLayoutList = view.findViewById(R.id.event_list);
        modelEvtArray.getList("evtList").observe(getViewLifecycleOwner(), new Observer<ArrayList<String[]>>() {
            @Override
            public void onChanged(ArrayList<String[]> evtList) {
                for(int i = 0; i < evtList.size(); i++)
                {
                    Log.d("EVT LOG", "Displaying " + evtList.get(i)[0]);
                    View evtView = getLayoutInflater().inflate(R.layout.row_submitted_events, null, false);
                    evtLayoutList.addView(evtView);
                    TextView evtTitle = evtLayoutList.getChildAt(i).findViewById(R.id.title_elem);
                    evtTitle.setText(evtList.get(i)[0]);
                    TextView evtPlace = evtLayoutList.getChildAt(i).findViewById(R.id.place_elem);
                    evtPlace.setText(evtList.get(i)[1]);
                    TextView evtDate = evtLayoutList.getChildAt(i).findViewById(R.id.date_elem);
                    evtDate.setText(evtList.get(i)[2]);
                }
            }
        });

        StringArrayViewModel modelSgtArray = new ViewModelProvider(requireActivity()).get(StringArrayViewModel.class);
        sgtLayoutList = view.findViewById(R.id.sight_list);
        modelSgtArray.getList("sgtList").observe(getViewLifecycleOwner(), new Observer<ArrayList<String[]>>() {
            @Override
            public void onChanged(ArrayList<String[]> sightList) {
                for(int i = 0; i < sightList.size(); i++)
                {
                    Log.d("EVT LOG", "Displaying " + sightList.get(i)[0]);
                    View sightsView = getLayoutInflater().inflate(R.layout.row_submitted_sights, null, false);
                    sgtLayoutList.addView(sightsView);
                    TextView sgtTitle = sgtLayoutList.getChildAt(i).findViewById(R.id.title_elem);
                    sgtTitle.setText(sightList.get(i)[0]);
                    TextView sgtPlace = sgtLayoutList.getChildAt(i).findViewById(R.id.place_elem);
                    sgtPlace.setText(sightList.get(i)[1]);
                }
            }
        });
    }
}