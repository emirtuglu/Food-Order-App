package com.example.foodapp;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import android.content.Intent;

import java.util.ArrayList;

public class AddressSelectActivity extends AppCompatActivity {

    private RecyclerView recyclerViewAddresses;
    private ArrayList<Address> addressList;
    private Button buttonAddAddress;
    private AddressAdapter addressAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_address_select);

        recyclerViewAddresses = findViewById(R.id.recyclerViewAddresses);
        buttonAddAddress = findViewById(R.id.buttonAddAddress);

        recyclerViewAddresses.setLayoutManager(new LinearLayoutManager(this));

        // Initialize the address list
        addressList = new ArrayList<Address>();
        addressAdapter = new AddressAdapter(addressList, this);
        recyclerViewAddresses.setAdapter(addressAdapter);
        addressList.add(new Address(1, "title", "city", "distict", "fullll"));
        addressList.add(new Address(2, "title2", "city2", "distict22", "fullll2234324"));
        addressList.add(new Address(3, "title3", "city3", "distict3333", "fullll33333333"));
        addressAdapter.notifyDataSetChanged(); // Notify when dataset changed

        buttonAddAddress.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent addressAddActivityIntent = new Intent(view.getContext(), AddressAddActivity.class);
                startActivity(addressAddActivityIntent);
            }
        });
    }


    public class AddressAdapter extends RecyclerView.Adapter<AddressAdapter.ViewHolder> {

        private ArrayList<Address> dataSet;
        private Context context;

        public class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
            private final TextView titleView;
            private final TextView cityDistrictView;
            private final TextView fullAddressView;

            public ViewHolder(View view) {
                super(view);
                // Define click listener for the ViewHolder's View

                titleView = (TextView) view.findViewById(R.id.addressTitle);
                cityDistrictView = (TextView) view.findViewById(R.id.cityAndDistrict);
                fullAddressView = (TextView) view.findViewById(R.id.fullAddress);
                itemView.setOnClickListener(this);
            }

            @Override
            public void onClick(View view) {
                int position = getAdapterPosition();
                // Move to new activity and display restaurants

                Intent restaurantSelectActivityIntent = new Intent(view.getContext(), RestaurantSelectActivity.class);
                startActivity(restaurantSelectActivityIntent);
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



