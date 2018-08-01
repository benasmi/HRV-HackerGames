package com.mabe.productions.hrv_madison;

import android.content.res.Resources;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;

import com.google.android.gms.maps.CameraUpdate;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.LatLngBounds;
import com.google.android.gms.maps.model.MapStyleOptions;
import com.google.android.gms.maps.model.PolylineOptions;
import com.mabe.productions.hrv_madison.fragments.DataTodayFragment;
import com.mabe.productions.hrv_madison.measurements.WorkoutMeasurements;

public class AdvancedWorkoutHistoryActivity extends AppCompatActivity {

    private GoogleMap route_display_googlemap;
    private SupportMapFragment map_fragment;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_advanced_workout_history);

        map_fragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.history_workout_route);

        //settingWorkoutMap(null);

    }


    private void settingWorkoutMap(final WorkoutMeasurements workout) {
        if (route_display_googlemap == null) {


            map_fragment.getMapAsync(new OnMapReadyCallback() {
                @Override
                public void onMapReady(GoogleMap googleMap) {
                    route_display_googlemap = googleMap;

                    try {
                        // Customise the styling of the base map using a JSON object defined
                        // in a raw resource file.
                        boolean success = googleMap.setMapStyle(
                                MapStyleOptions.loadRawResourceStyle(AdvancedWorkoutHistoryActivity.this, R.raw.dark_google_map));

                        if (!success) {
                            Log.e("GMAPS", "Style parsing failed.");
                        } else {
                            Log.e("GMAPS", "Style SUCCESS");
                        }
                    } catch (Resources.NotFoundException e) {
                        Log.e("GMAPS", "Can't find style. Error: ", e);
                    }

                    map_fragment.getView().setClickable(false);
                    displayMapRoute(workout.getRoute());

                }
            });
        } else {
            displayMapRoute(workout.getRoute());
        }
    }

    private void displayMapRoute(LatLng[] route) {
        //Instantiates a new Polyline object and adds points to define a rectangle
        PolylineOptions lineOptions = new PolylineOptions()
                .width(9)
                .color(getResources().getColor(R.color.colorAccent))
                .geodesic(false);

        //This will zoom the map to our polyline
        LatLngBounds.Builder builder = new LatLngBounds.Builder();

        for (LatLng point : route) {
            lineOptions.add(point);
            builder.include(point);
        }
        route_display_googlemap.addPolyline(lineOptions);

        final CameraUpdate cu;
        if (route.length == 0) {
            //If no points are present for some reason
            cu = CameraUpdateFactory.newLatLngZoom(new LatLng(55.19f, 23.4f), 6f); //Geographical centre of lithuania
        } else {
            LatLngBounds bounds = builder.build();
            cu = CameraUpdateFactory.newLatLngBounds(bounds, 50);
        }

        route_display_googlemap.animateCamera(cu);
    }
}
