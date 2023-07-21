package com.example.foodapp;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.app.Activity;
import android.content.Context;
import android.content.DialogInterface;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.content.Intent;
import android.widget.Toast;

import com.google.gson.Gson;
import java.util.ArrayList;

public class AddressSelectActivity extends AppCompatActivity {

    private RecyclerView recyclerViewAddresses;
    private ArrayList<Address> addressList;
    private User user;
    private Button buttonAddAddress;
    private AddressAdapter addressAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_address_select);

        Gson gson = new Gson();
        user = gson.fromJson(getIntent().getStringExtra("user"), User.class);

        recyclerViewAddresses = findViewById(R.id.recyclerViewAddresses);
        recyclerViewAddresses.setLayoutManager(new LinearLayoutManager(this));

        addressList = user.getAddresses();
        addressAdapter = new AddressAdapter(addressList, this);
        recyclerViewAddresses.setAdapter(addressAdapter);
        addressAdapter.notifyDataSetChanged(); // Notify when dataset changed

        TextView addAddressText = findViewById(R.id.addAddressText);
        if (addressList.size() == 0) {
            addAddressText.setText("You haven't added any addresses yet. Click + to add.");
        }

        buttonAddAddress = findViewById(R.id.buttonAddAddress);
        buttonAddAddress.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent addressAddActivityIntent = new Intent(view.getContext(), AddressAddActivity.class);
                addressAddActivityIntent.putExtra("user", gson.toJson(user, User.class));
                startActivityForResult(addressAddActivityIntent, 1);
            }
        });

    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        Gson gson = new Gson();

        // Get added address
        if (requestCode == 1) {
            if(resultCode == Activity.RESULT_OK){
                String userJson = data.getStringExtra("user");
                user = gson.fromJson(userJson, User.class);
                addressList.clear();
                addressList.addAll(user.getAddresses());
                addressAdapter.notifyDataSetChanged();
            }
            if (resultCode == Activity.RESULT_CANCELED) {
                // Write your code if there's no result
            }
        }
    }

    public class AddressAdapter extends RecyclerView.Adapter<AddressAdapter.ViewHolder> {

        private ArrayList<Address> dataSet;
        private Context context;

        public class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
            private final TextView titleView;
            private final TextView cityDistrictView;
            private final TextView fullAddressView;
            private final ImageView deleteIcon;

            public ViewHolder(View view) {
                super(view);
                // Define click listener for the ViewHolder's View
                titleView = (TextView) view.findViewById(R.id.addressTitle);
                cityDistrictView = (TextView) view.findViewById(R.id.cityAndDistrict);
                fullAddressView = (TextView) view.findViewById(R.id.fullAddress);
                deleteIcon = (ImageView) view.findViewById(R.id.deleteIcon);
                itemView.setOnClickListener(this);
                deleteIcon.setOnClickListener(this);
            }

            @Override
            public void onClick(View view) {
                int position = getAdapterPosition();
                Gson gson = new Gson();
                Address clickedAddress = addressList.get(position);
                String addressJson = gson.toJson(clickedAddress, Address.class);

                if (view.getId() == R.id.deleteIcon) {
                    DialogInterface.OnClickListener dialogClickListener = new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            switch (which){
                                case DialogInterface.BUTTON_POSITIVE:
                                    RequestManager requestManager = new RequestManager();
                                    String addressJson = gson.toJson(clickedAddress, Address.class);
                                    String request = RequestManager.requestBuild("POST", "/user-delete-address", null, null, addressJson);
                                    String response = null;
                                    try {
                                        response = requestManager.execute(request).get();
                                    } catch (Exception e) {
                                    }
                                    Toast.makeText(view.getContext(), RequestManager.getBody(response), Toast.LENGTH_SHORT).show();
                                    if (response.contains("200 OK")) {
                                        addressList.remove(clickedAddress);
                                        addressAdapter.notifyDataSetChanged();
                                    }
                                    break;

                                case DialogInterface.BUTTON_NEGATIVE:
                                    //Do your No progress
                                    break;
                            }
                        }
                    };
                    AlertDialog.Builder ab = new AlertDialog.Builder(view.getContext());
                    ab.setMessage("Are you sure to delete the address?").setPositiveButton("Yes", dialogClickListener)
                            .setNegativeButton("No", dialogClickListener).show();
                }
                else {
                    // Move to new activity and display restaurants
                    Intent restaurantSelectActivityIntent = new Intent(view.getContext(), RestaurantSelectActivity.class);
                    restaurantSelectActivityIntent.putExtra("address", addressJson);
                    restaurantSelectActivityIntent.putExtra("user", gson.toJson(user, User.class));
                    startActivity(restaurantSelectActivityIntent);
                }
            }

            public void bind(Address address) {
                String cityAndDistrict = address.getCity() + ", " + address.getDistrict();
                titleView.setText(address.getTitle());
                cityDistrictView.setText(cityAndDistrict);
                fullAddressView.setText(address.getFullAddress());
            }
        }

        public AddressAdapter (ArrayList<Address> dataSet, Context context) {
            this.dataSet = dataSet;
            this.context = context;
        }

        @Override
        public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            View view = LayoutInflater.from(parent.getContext())
                    .inflate(R.layout.item_address, parent, false);

            return new ViewHolder(view);
        }

        @Override
        public void onBindViewHolder(@NonNull ViewHolder viewHolder, int position) {
            Address address = dataSet.get(position);
            viewHolder.bind(address);
        }

        @Override
        public int getItemCount() {
            return dataSet.size();
        }
    }
}



