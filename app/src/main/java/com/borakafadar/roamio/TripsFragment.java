package com.borakafadar.roamio;

import android.content.Intent;
import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.room.Room;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.borakafadar.roamio.App.Save.SaveManager;
import com.borakafadar.roamio.App.Save.TripDatabase;
import com.borakafadar.roamio.App.Save.TripEntity;
import com.borakafadar.roamio.View.TripsRecyclerViewAdapter;
import com.borakafadar.roamio.View.TripsRecyclerViewInterface;

import java.util.ArrayList;
import java.util.List;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link TripsFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class TripsFragment extends Fragment implements TripsRecyclerViewInterface {

    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    private ArrayList<TripEntity> trips;
    private RecyclerView recyclerView;


    public TripsFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment TripsFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static TripsFragment newInstance(String param1, String param2) {
        TripsFragment fragment = new TripsFragment();
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

        trips = getTripsFromDatabase();
        Log.d("tripLog","trips are loaded");
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment

        View view = inflater.inflate(R.layout.fragment_trips, container, false);



        recyclerView = view.findViewById(R.id.tripsRecyclerView);


        try {
            Log.d("tripLog",trips.toString());
        } catch (Exception e) {
            Log.e("tripLog", e +"\n error happened in returning trips to string");
        }



        recyclerView.setAdapter(new TripsRecyclerViewAdapter(this.getActivity(), trips, this));
        recyclerView.setLayoutManager(new LinearLayoutManager(this.getActivity()));

        return view;
    }


    public ArrayList<TripEntity> getTripsFromDatabase() {
        SaveManager.getAllTrips(this.getActivity(), new SaveManager.TripsCallback() {
            @Override
            public void onTripsLoaded(List<TripEntity> trips) {
                Log.d("tripLog","Total trips loaded: "+trips.size());
            }
            @Override
            public void onTripLoaded(TripEntity trip) {
                //nothing
            }
        });
        return SaveManager.allTrips;
    }

    @Override
    public void onItemClicked(int position) {
        Intent intent = new Intent(this.getContext(),TripMapsActivity.class);

        intent.putExtra("TRIP_ID", trips.get(position).tripID);

        startActivity(intent);
    }
}
