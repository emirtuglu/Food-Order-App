package com.example.foodapp;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import android.content.DialogInterface;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.google.android.material.snackbar.Snackbar;
import com.google.gson.Gson;

public class RegisterActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        Button registerButton = findViewById(R.id.registerButton);
        registerButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Gson gson = new Gson();
                RequestManager requestManager = new RequestManager();

                EditText name = findViewById(R.id.name);
                EditText surname = findViewById(R.id.surname);
                EditText mail = findViewById(R.id.email);
                EditText password = findViewById(R.id.password);
                EditText phoneNumber = findViewById(R.id.phoneNumber);

                User user = new User(name.getText().toString(), surname.getText().toString(), phoneNumber.getText().toString(), mail.getText().toString(), password.getText().toString());
                String requestBody = gson.toJson(user, User.class);
                String request = RequestManager.requestBuild("POST", "/user-register", null, null, requestBody);

                String response = null;
                try {
                    response = requestManager.execute(request).get();
                } catch (Exception e) {

                }
                if (response.contains("201 Created")) {
                    Toast.makeText(view.getContext(), "Registration successful", Toast.LENGTH_SHORT).show();
                    finish();
                }
                else {
                    Toast.makeText(view.getContext(), RequestManager.getBody(response), Toast.LENGTH_LONG).show();
                    name.getText().clear();
                    surname.getText().clear();
                    mail.getText().clear();
                    password.getText().clear();
                    phoneNumber.getText().clear();
                }
            }
        });
    }
}