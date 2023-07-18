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
import android.widget.TextView;
import android.content.Intent;

import java.util.ArrayList;

public class RestaurantSelectActivity extends AppCompatActivity {

    private RecyclerView recyclerViewOrders;
    private RecyclerView recyclerViewRestaurants;
    private ArrayList<Restaurant> restaurantsList;
    private ArrayList<Order> ordersList;
    private TextView seeAllOrdersText;
    private OrderAdapter ordersAdapter;
    private RestaurantAdapter restaurantsAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_restaurant_select);

        recyclerViewOrders = findViewById(R.id.recyclerViewOrders);
        recyclerViewRestaurants = findViewById(R.id.recyclerViewRestaurants);
        seeAllOrdersText = findViewById(R.id.seeAllOrdersText);
        restaurantsList = new ArrayList<Restaurant>();
        ordersList = new ArrayList<Order>();
        restaurantsList.add(new Restaurant(1, new Address(1, "t", "city", "dist", "full"), "Restaurant A", "+535 654 43 54", null, null, null));
        restaurantsList.add(new Restaurant(1, new Address(1, "t", "city", "dist", "full"), "Restaurant B", "+535 654 43 54", null, null, null));
        restaurantsList.add(new Restaurant(1, new Address(1, "t", "city", "dist", "full"), "Restaurant B", "+535 654 43 54", null, null, null));
        restaurantsList.add(new Restaurant(1, new Address(1, "t", "city", "dist", "full"), "Restaurant B", "+535 654 43 54", null, null, null));
        restaurantsList.add(new Restaurant(1, new Address(1, "t", "city", "dist", "full"), "Restaurant B", "+535 654 43 54", null, null, null));

        ordersList.add(new Order(1, 1, 1, "Restaurant A", "18.07.2023 / 15:20", 155.55, Status.ACTIVE, null));

        recyclerViewOrders.setLayoutManager(new LinearLayoutManager(this));
        recyclerViewRestaurants.setLayoutManager(new LinearLayoutManager(this));

        ordersAdapter = new OrderAdapter(ordersList, this);
        restaurantsAdapter = new RestaurantAdapter(restaurantsList, this);

        recyclerViewOrders.setAdapter(ordersAdapter);
        recyclerViewRestaurants.setAdapter(restaurantsAdapter);

        ordersAdapter.notifyDataSetChanged(); // Notify when dataset changed
        restaurantsAdapter.notifyDataSetChanged();

        seeAllOrdersText.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent ordersActivityIntent = new Intent (view.getContext(), OrdersActivity.class);
                startActivity(ordersActivityIntent);
            }
        });
    }


    public class OrderAdapter extends RecyclerView.Adapter<OrderAdapter.ViewHolder> {

        private ArrayList<Order> dataSet;
        private Context context;

        public class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
            private final TextView restaurantName;
            private final TextView date;
            private final TextView status;
            private final TextView price;
            private final TextView details;

            public ViewHolder(View view) {
                super(view);
                // Define click listener for the ViewHolder's View

                restaurantName = (TextView) view.findViewById(R.id.restaurantName);
                date = (TextView) view.findViewById(R.id.date);
                status = (TextView) view.findViewById(R.id.status);
                price = (TextView) view.findViewById(R.id.price);
                details = (TextView) view.findViewById(R.id.details);

                itemView.setOnClickListener(this);
            }

            @Override
            public void onClick(View view) {
                int position = getAdapterPosition();

            }

            public void bind(Order order) {
                restaurantName.setText(order.getRestaurantName());
                date.setText(order.getTime());
                status.setText(order.getStatus().toString());
                price.setText("₺" + String.valueOf(order.getPrice()));
            }
        }

        public OrderAdapter (ArrayList<Order> dataSet, Context context) {
            this.dataSet = dataSet;
            this.context = context;
        }

        @Override
        public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            View view = LayoutInflater.from(parent.getContext())
                    .inflate(R.layout.item_order, parent, false);

            return new ViewHolder(view);
        }

        @Override
        public void onBindViewHolder(@NonNull ViewHolder viewHolder, int position) {
            Order order = dataSet.get(position);
            viewHolder.bind(order);
        }

        @Override
        public int getItemCount() {
            return dataSet.size();
        }
    }


    public class RestaurantAdapter extends RecyclerView.Adapter<RestaurantAdapter.ViewHolder> {

        private ArrayList<Restaurant> dataSet;
        private Context context;

        public class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
            private final TextView restaurantName;
            private final TextView restaurantAddress;

            public ViewHolder(View view) {
                super(view);
                // Define click listener for the ViewHolder's View

                restaurantName = (TextView) view.findViewById(R.id.restaurantName);
                restaurantAddress = (TextView) view.findViewById(R.id.restaurantAddress);

                itemView.setOnClickListener(this);
            }

            @Override
            public void onClick(View view) {
                int position = getAdapterPosition();

                Intent restaurantMenuIntent = new Intent (view.getContext(), RestaurantMenuActivity.class);
                startActivity(restaurantMenuIntent);
            }

            public void bind(Restaurant restaurant) {
                restaurantName.setText(restaurant.getName());
                restaurantAddress.setText(restaurant.getAddress().getFullAddress());
            }
        }

        public RestaurantAdapter (ArrayList<Restaurant> dataSet, Context context) {
            this.dataSet = dataSet;
            this.context = context;
        }

        @Override
        public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            View view = LayoutInflater.from(parent.getContext())
                    .inflate(R.layout.item_restaurant, parent, false);

            return new ViewHolder(view);
        }

        @Override
        public void onBindViewHolder(@NonNull ViewHolder viewHolder, int position) {
            Restaurant restaurant = dataSet.get(position);
            viewHolder.bind(restaurant);
        }

        @Override
        public int getItemCount() {
            return dataSet.size();
        }
    }


}



