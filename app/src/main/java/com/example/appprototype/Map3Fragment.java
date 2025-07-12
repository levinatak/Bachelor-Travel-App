package com.example.appprototype;

import android.os.AsyncTask;
import android.os.Bundle;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapView;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.LatLngBounds;
import com.google.android.gms.maps.model.PolylineOptions;
import com.google.maps.DirectionsApiRequest;
import com.google.maps.GeoApiContext;
import com.google.maps.DirectionsApi;
import com.google.maps.errors.ApiException;
import com.google.maps.model.DirectionsResult;
import com.google.maps.model.DirectionsRoute;
import com.google.maps.model.TravelMode;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class Map3Fragment extends Fragment implements OnMapReadyCallback {

    private static final String ARG_SOURCE = "source";
    private static final String ARG_DESTINATION = "destination";
    private static final String ARG_WAYPOINTS = "waypoints";
    private static final String ARG_TRAVEL_MODE = "travel_mode";

    private View view;
    private String source;
    private String destination;
    private List<String> waypoints;
    private String travelMode;

    private MapView mapView;
    private GoogleMap googleMap;

    public static Map3Fragment newInstance(String source, String destination, ArrayList<String> waypoints, String travelMode) {
        Map3Fragment fragment = new Map3Fragment();
        Bundle args = new Bundle();
        args.putString(ARG_SOURCE, source);
        args.putString(ARG_DESTINATION, destination);
        args.putStringArrayList(ARG_WAYPOINTS, waypoints);
        args.putString(ARG_TRAVEL_MODE, travelMode);
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
            travelMode = getArguments().getString(ARG_TRAVEL_MODE);
        }
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.fragment_map3, container, false);
        return view;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        mapView = view.findViewById(R.id.mapView);
        mapView.onCreate(savedInstanceState);
        mapView.getMapAsync(this);

        TextView sourceTextView = view.findViewById(R.id.sourceTextView);
        TextView destinationTextView = view.findViewById(R.id.destinationTextView);
        TextView waypointsTextView = view.findViewById(R.id.waypointsTextView);

        sourceTextView.setText("Start: " + source);
        destinationTextView.setText("Ziel: " + destination);
        waypointsTextView.setText("Wegpunkte: " + waypoints.toString());
    }

    @Override
    public void onMapReady(GoogleMap map) {
        googleMap = map;
        LatLng initialLocation = new LatLng(49.4430795, 11.0862744); // Galgenghetto
        googleMap.moveCamera(CameraUpdateFactory.newLatLngZoom(initialLocation, 10));

        new GetDirectionsTask().execute(source, destination);
    }

    private TravelMode getTravelMode(String mode) {
        switch (mode.toLowerCase()) {
            case "auto":
                return TravelMode.DRIVING;
            case "laufen":
                return TravelMode.WALKING;
            case "fahrrad":
                return TravelMode.BICYCLING;
            case "oepnv":
                return TravelMode.TRANSIT;
            default:
                return TravelMode.DRIVING;
        }
    }

    private class GetDirectionsTask extends AsyncTask<String, Void, DirectionsResult> {

        @Override
        protected DirectionsResult doInBackground(String... params) {
            String source = params[0];
            String destination = params[1];

            GeoApiContext context = new GeoApiContext.Builder()
                    .apiKey(/*YOUR_API_KEY*/)
                    .build();

            try {
                DirectionsApiRequest request = DirectionsApi.getDirections(context, source, destination)
                        .language("de") // Sprache auf Deutsch setzen
                        .mode(getTravelMode(travelMode)); // Fortbewegungsmittel setzen

                if (waypoints != null && !waypoints.isEmpty()) {
                    request.waypoints(waypoints.toArray(new String[0]));
                }
                return request.await();
            } catch (ApiException | InterruptedException | IOException e) {
                Log.e("DirectionsAPI", "Error getting directions: ", e);
                return null;
            }
        }

        @Override
        protected void onPostExecute(DirectionsResult result) {
            if (result != null && result.routes != null && result.routes.length > 0) {
                DirectionsRoute route = result.routes[0];
                googleMap.clear(); // Alte Markierungen und Polylines entfernen

                List<LatLng> path = getPathFromRoute(route);

                if (!path.isEmpty()) {
                    googleMap.addPolyline(new PolylineOptions().addAll(path));
                    adjustCameraToRoute(path);
                } else {
                    showToast("Keine Route gefunden");
                }
            } else {
                showToast("Keine Routen gefunden");
            }
        }

        private List<LatLng> getPathFromRoute(DirectionsRoute route) {
            List<LatLng> path = new ArrayList<>();
            if (route.overviewPolyline != null) {
                List<com.google.maps.model.LatLng> coords = route.overviewPolyline.decodePath();
                for (com.google.maps.model.LatLng coord : coords) {
                    path.add(new LatLng(coord.lat, coord.lng));
                }
            }
            return path;
        }

        private void adjustCameraToRoute(List<LatLng> path) {
            LatLngBounds.Builder builder = new LatLngBounds.Builder();
            for (LatLng latLng : path) {
                builder.include(latLng);
            }
            LatLngBounds bounds = builder.build();
            int padding = 50; // padding around the edges of the map in pixels
            googleMap.animateCamera(CameraUpdateFactory.newLatLngBounds(bounds, padding));
        }

        private void showToast(String message) {
            if (getActivity() != null) {
                Toast.makeText(getActivity(), message, Toast.LENGTH_SHORT).show();
            }
        }
    }

    @Override
    public void onResume() {
        super.onResume();
        mapView.onResume();
    }

    @Override
    public void onPause() {
        super.onPause();
        mapView.onPause();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        mapView.onDestroy();
    }

    @Override
    public void onLowMemory() {
        super.onLowMemory();
        mapView.onLowMemory();
    }
}
