package com.borakafadar.roamio;

import android.os.Bundle;

import androidx.appcompat.app.AlertDialog;
import androidx.fragment.app.Fragment;

import android.os.Handler;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import com.borakafadar.roamio.App.Save.SaveManager;
import com.borakafadar.roamio.App.User;
import com.google.android.material.dialog.MaterialAlertDialogBuilder;
import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link SettingsFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class SettingsFragment extends Fragment {

    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    private User user;

    public SettingsFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment SettingsFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static SettingsFragment newInstance(String param1, String param2) {
        SettingsFragment fragment = new SettingsFragment();
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


        getUser();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_settings, container, false);

        Button backButton = view.findViewById(R.id.settingsBackButton);

        backButton.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                Fragment fragment = new AccountFragment();
                getActivity().getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container, fragment).commit();
            }
        });

        Handler handler = new Handler();
        Runnable runnable = (() ->{
            TextView userNameTextView = view.findViewById(R.id.userNameTextView);
            userNameTextView.setText(user.getName());
        });
        handler.postDelayed(runnable,1000);



        View changeAccountNameButton = view.findViewById(R.id.changeAccountNameButton);
        TextView changeAccountNameTitleTextView = changeAccountNameButton.findViewById(R.id.settingsCardTitleTextView);
        TextView changeAccountNameDetailsTextView = changeAccountNameButton.findViewById(R.id.settingsCardDetailsTextView);

        changeAccountNameTitleTextView.setText(R.string.change_user_name_text);
        changeAccountNameDetailsTextView.setText(R.string.change_user_name_details);
        changeAccountNameButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                View dialogView = LayoutInflater.from(getContext()).inflate(R.layout.dialog_input, null);
                TextInputEditText editText = dialogView.findViewById(R.id.tripDialogEditText);
                TextInputLayout textInputLayout = dialogView.findViewById(R.id.tripDialogTextInputLayout);
                textInputLayout.setHint("Enter User Name");
                editText.setText(user.getName());

                AlertDialog alertDialog = new MaterialAlertDialogBuilder(SettingsFragment.this.getContext())
                        .setTitle("Change User Name")
                        .setView(dialogView).setPositiveButton("OK", (dialog, which) -> {
                            user.setName(editText.getText().toString());
                            SaveManager.updateUser(SettingsFragment.this.getContext(), user);
                        }).setNegativeButton("Cancel", (dialog, which) -> dialog.dismiss())
                        .create();
                alertDialog.show();
            }
        });






        return view;
    }



    public void getUser(){
        SaveManager.getUser(this.getContext(), new SaveManager.UserCallback() {
            @Override
            public void onUserLoaded(User user) {
                //user.setTripEntitiesFromJson();
                user.setTripEntitiesFromTripsTable(SettingsFragment.this.getContext());
                SettingsFragment.this.user = user;
            }

            @Override
            public void onUserCountLoaded(int count) {
                //nothing
            }
        });

    }
}