package com.borakafadar.roamio;

import android.content.Intent;
import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.os.Handler;
import android.os.Looper;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.borakafadar.roamio.App.Save.SaveManager;
import com.borakafadar.roamio.App.Save.TripEntity;
import com.borakafadar.roamio.App.User;

import java.util.List;
import java.util.Locale;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link HomeFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class HomeFragment extends Fragment {

    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    private TripEntity latestTrip;
    private User user;

    public HomeFragment() {
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
    public static HomeFragment newInstance(String param1, String param2) {
        HomeFragment fragment = new HomeFragment();
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

        //test for permissions



    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_home, container, false);

        view.findViewById(R.id.startTripButton).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(HomeFragment.this.getActivity(),MapsActivity.class);
                startActivity(intent);
                HomeFragment.this.getActivity().finish();
            }
        });

        view.findViewById(R.id.lastTripLinearLayout).setVisibility(View.GONE);

        getLatestTrip();
        getUser();

        Handler handler = new Handler(Looper.getMainLooper());
        Runnable runnable = new Runnable() {
            @Override
            public void run() {
                if(latestTrip != null){
                    view.findViewById(R.id.lastTripLinearLayout).setVisibility(View.VISIBLE);

                    View cardView = view.findViewById(R.id.lastTripCardView);

                    TextView title = cardView.findViewById(R.id.tripCardTitleTextView);
                    TextView distance = cardView.findViewById(R.id.tripCardDistanceTextView);
                    TextView date = cardView.findViewById(R.id.tripCardDateTextView);
                    TextView duration = cardView.findViewById(R.id.tripCardDurationTextView);
                    TextView comments = cardView.findViewById(R.id.tripCardCommentsTextView);

                    title.setText(latestTrip.getTitle());
                    distance.setText(String.format(Locale.ENGLISH,"%.2f", latestTrip.getDistance()) + " km");
                    date.setText(latestTrip.getDate());
                    duration.setText(latestTrip.getDuration());
                    comments.setText(latestTrip.getComments());
                } else {
                    view.findViewById(R.id.lastTripLinearLayout).setVisibility(View.GONE);
                }


                if(user != null){
                    TextView welcomeUser = view.findViewById(R.id.welcomeUserTextView);
                    welcomeUser.setText("Welcome " + user.getName()+ "!");

                }


            }
        };
        handler.postDelayed(runnable, 1000);

        return view;
    }

    public void getLatestTrip(){
        SaveManager.getLatestTrip(this.getContext(), new SaveManager.TripsCallback(){
            @Override
            public void onTripsLoaded(List<TripEntity> trips) {
                //nothing
            }
            @Override
            public void onTripLoaded(TripEntity trip) {
                latestTrip = trip;
            }
        });
    }

    public void getUser(){
        SaveManager.getUser(this.getContext(), new SaveManager.UserCallback(){
            @Override
            public void onUserLoaded(User user) {
                //user.setTripEntitiesFromJson();
                user.setTripEntitiesFromTripsTable(HomeFragment.this.getContext());
                HomeFragment.this.user = user;
            }
            @Override
            public void onUserCountLoaded(int count) {
                //nothing
            }
        });
    }
}