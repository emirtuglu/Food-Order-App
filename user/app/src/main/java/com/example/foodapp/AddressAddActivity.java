package com.example.foodapp;

import androidx.appcompat.app.AppCompatActivity;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.google.gson.Gson;

public class AddressAddActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_address_add);
        Gson gson = new Gson();

        String userJson = getIntent().getStringExtra("user");
        User user = gson.fromJson(userJson, User.class);

        Button buttonAddAddress = findViewById(R.id.buttonAddAddress);
        buttonAddAddress.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                RequestManager requestManager = new RequestManager();

                EditText title = findViewById(R.id.title);
                EditText city = findViewById(R.id.city);
                EditText district = findViewById(R.id.district);
                EditText fullAddress = findViewById(R.id.fullAddress);
                Address address = new Address(title.getText().toString(), city.getText().toString(), district.getText().toString(), fullAddress.getText().toString());
                String addressJson = gson.toJson(address, Address.class);

                String request = RequestManager.requestBuild("POST", "/user-add-address", "userId", String.valueOf(user.getId()), addressJson);
                String response = null;
                try {
                    response = requestManager.execute(request).get();
                } catch (Exception e) {

                }

                if (response.contains("201 Created")) {
                    Toast.makeText(view.getContext(), "Address added successfully", Toast.LENGTH_LONG).show();
                    user.getAddresses().add(address);
                    String userJson = gson.toJson(user, User.class);
                    Intent returnIntent = new Intent();
                    returnIntent.putExtra("user", userJson);
                    setResult(Activity.RESULT_OK,returnIntent);
                    finish();
                }
                else {
                    Toast.makeText(view.getContext(), RequestManager.getBody(response), Toast.LENGTH_LONG).show();
                    title.getText().clear();
                    city.getText().clear();
                    district.getText().clear();
                    fullAddress.getText().clear();
                }
            }
        });
    }
}