package com.borakafadar.roamio;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.borakafadar.roamio.App.Save.SaveManager;
import com.borakafadar.roamio.App.User;
import com.google.android.material.dialog.MaterialAlertDialogBuilder;
import com.google.android.material.textfield.TextInputEditText;

public class WelcomeActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_welcome);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        //TODO handle the things
        //thanks for fucks sake what the hell does this mean??

        View dialogView = LayoutInflater.from(this).inflate(R.layout.alert_dialog, null);
        TextView textView = dialogView.findViewById(R.id.alertDialogTextView);
        textView.setText("A user has been created");



        AlertDialog alertDialog = new MaterialAlertDialogBuilder(this).setTitle("Welcome").setView(dialogView)
                .setPositiveButton("OK", (dialog, which) -> {
                    dialog.dismiss();
                }).setOnDismissListener(new DialogInterface.OnDismissListener() {
                    @Override
                    public void onDismiss(DialogInterface dialog) {
                        Intent intent = new Intent(WelcomeActivity.this, MainActivity.class);
                        startActivity(intent);
                        finish();
                    }
                }).create();





        Button button = findViewById(R.id.continueButton);

        button.setOnClickListener((View v) -> {
            TextInputEditText editText = findViewById(R.id.welcomeUserTextEdit);
            SaveManager.saveUser(this,new User(editText.getText().toString(),"settings and stuff"));
            alertDialog.show();
        });



    }
}