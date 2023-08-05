package com.example.foodapp;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.content.Intent;
import android.widget.Toast;
import com.google.gson.Gson;

import java.util.ArrayList;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        TextView registerText = findViewById(R.id.registerClickableText);
        registerText.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent registerActivityIntent = new Intent (view.getContext(), RegisterActivity.class);
                startActivity(registerActivityIntent);
            }
        });

        Button loginButton = findViewById(R.id.loginButton);
        loginButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Gson gson = new Gson();
                RequestManager requestManager = new RequestManager();

                EditText mail = findViewById(R.id.email);
                EditText password = findViewById(R.id.password);
                User user = new User(mail.getText().toString(), password.getText().toString());
                String requestBody = gson.toJson(user, User.class);
                String request = RequestManager.requestBuild("POST", "/user-login", null, null, requestBody);
                String response = null;
                try {
                    response = requestManager.execute(request).get();

                    if (response != null && response.contains("200 OK")) {
                        String body = RequestManager.getBody(response);
                        Intent addressActivityIntent = new Intent(view.getContext(), AddressSelectActivity.class);
                        user.setCart(new ArrayList<Food>());
                        addressActivityIntent.putExtra("user", body);
                        startActivity(addressActivityIntent);
                    }
                    else {
                        Toast.makeText(view.getContext(), RequestManager.getBody(response), Toast.LENGTH_LONG).show();
                        mail.getText().clear();
                        password.getText().clear();
                    }
                }
                catch (Exception e) {
                    Toast.makeText(view.getContext(), "Connection error", Toast.LENGTH_LONG).show();
                }
            }
        });
    }

}