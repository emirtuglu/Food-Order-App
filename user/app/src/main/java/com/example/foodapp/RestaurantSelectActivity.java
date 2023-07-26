package com.example.foodapp;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Context;
import android.content.DialogInterface;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.os.Handler;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.content.Intent;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

public class RestaurantSelectActivity extends AppCompatActivity {

    private ArrayList<Restaurant> restaurantsList;
    private ArrayList<Order> ordersList;
    private User user;
    private Address address;
    private TextView lastOrderText;
    private TextView seeAllText;

    private Handler handler;
    private Runnable runnable;
    private Gson gson;
    private String ordersRefreshRequestString;
    private OrderAdapter orderAdapter;
    private int refreshOrdersDelay = 10000; // 10 seconds

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_restaurant_select);

        handler = new Handler();
        RequestManager requestManager = new RequestManager();
        gson = new Gson();
        lastOrderText = findViewById(R.id.lastOrderText);
        seeAllText = findViewById(R.id.seeAllOrdersText);

        String addressJson = getIntent().getStringExtra("address");
        String userJson = getIntent().getStringExtra("user");

        address = gson.fromJson(addressJson, Address.class);
        user = gson.fromJson(userJson, User.class);

        ordersRefreshRequestString = RequestManager.requestBuild("GET", "/user-orders", "userId", String.valueOf(user.getId()), null);
        String request = RequestManager.requestBuild("GET", "/restaurants", "addressId", String.valueOf(address.getId()), addressJson);
        String response = null;
        try {
            response = requestManager.execute(request).get();
        } catch (Exception e) {

        }
        String restaurantsJson = RequestManager.getBody(response);
        restaurantsList = gson.fromJson(restaurantsJson, new TypeToken<List<Restaurant>>(){}.getType());
        ordersList = new ArrayList<Order>();

        RecyclerView recyclerViewOrders = findViewById(R.id.recyclerViewOrders);
        RecyclerView recyclerViewRestaurants = findViewById(R.id.recyclerViewRestaurants);

        recyclerViewOrders.setLayoutManager(new LinearLayoutManager(this));
        recyclerViewRestaurants.setLayoutManager(new LinearLayoutManager(this));

        orderAdapter = new OrderAdapter(ordersList, this);
        RestaurantAdapter restaurantsAdapter = new RestaurantAdapter(restaurantsList, this);

        recyclerViewOrders.setAdapter(orderAdapter);
        recyclerViewRestaurants.setAdapter(restaurantsAdapter);

        restaurantsAdapter.notifyDataSetChanged();

        TextView seeAllOrdersText = findViewById(R.id.seeAllOrdersText);
        seeAllOrdersText.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent ordersActivityIntent = new Intent (view.getContext(), OrdersActivity.class);
                ordersActivityIntent.putExtra("userId", String.valueOf(user.getId()));
                startActivity(ordersActivityIntent);
            }
        });

        ImageView cartImage = findViewById(R.id.cartImage);
        cartImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent cartActivityIntent = new Intent (view.getContext(), CartActivity.class);
                cartActivityIntent.putExtra("user", userJson);
                startActivity(cartActivityIntent);
            }
        });
    }

    @Override
    protected void onResume() {
        // Update cart
        RequestManager requestManager = new RequestManager();
        String request = RequestManager.requestBuild("GET", "/user-cart", "userId", String.valueOf(user.getId()), null);
        String response = null;
        try {
            response = requestManager.execute(request).get();
        } catch (Exception e) {

        }
        String cartJson = RequestManager.getBody(response);
        // Ensure user cart is not null
        if (cartJson.length() > 3) {
            user.setCart(gson.fromJson(cartJson, new TypeToken<List<Food>>(){}.getType()));
        }
        else {
            user.setCart(new ArrayList<Food>());
        }

        // Start handler as activity become visible
        handler.postDelayed( runnable = new Runnable() {
            public void run() {
                RequestManager requestManager2 = new RequestManager();
                String response = null;
                try {
                    response = requestManager2.execute(ordersRefreshRequestString).get();
                } catch (Exception e) {

                }

                String ordersJson = RequestManager.getBody(response);

                // Check if any orders are cancelled
                user.getOrders().sort(Comparator.comparing(Order::getTime, Comparator.reverseOrder()));
                ArrayList<Order> updatedOrders = gson.fromJson(ordersJson, new TypeToken<List<Order>>(){}.getType());
                updatedOrders.sort(Comparator.comparing(Order::getTime, Comparator.reverseOrder()));
                if (user.getOrders().size() > 0 && user.getOrders().size() == updatedOrders.size() && !user.getOrders().equals(updatedOrders)) {
                    // Status of an order is changed
                    for (int i = 0; i < user.getOrders().size(); i++) {
                        if (!user.getOrders().get(i).getStatus().toString().equals(updatedOrders.get(i).getStatus().toString())) {
                            if (updatedOrders.get(i).getStatus() == Status.USER_CANCELLED || updatedOrders.get(i).getStatus() == Status.RESTAURANT_CANCELLED) {
                                // One of the orders is cancelled, notify user by sending dialog
                                sendDialog(updatedOrders.get(i), updatedOrders.get(i).getStatus());
                            }
                        }
                    }
                }
                user.setOrders(updatedOrders);
                ordersList.clear();
                if (user.getLastOrder() != null) {
                    ordersList.add(user.getLastOrder());
                    lastOrderText.setText("Last Order");
                    seeAllText.setVisibility(View.VISIBLE);
                }

                if (ordersList.size() == 0) {
                    lastOrderText.setText("You don't have any order yet");
                    seeAllText.setVisibility(View.INVISIBLE);
                }
                orderAdapter.notifyDataSetChanged(); // Notify when dataset changed

                handler.postDelayed(runnable, refreshOrdersDelay);
            }
        }, 1000);

        super.onResume();
    }

    @Override
    protected void onPause() {
        handler.removeCallbacks(runnable); //stop handler when activity not visible
        super.onPause();
    }

    public void sendDialog(Order order, Status status) {
        MediaPlayer mediaPlayer = MediaPlayer.create(this, R.raw.notification_sound);
        if (status == Status.USER_CANCELLED) {
            AlertDialog.Builder builder = new AlertDialog.Builder(this);
            builder.setTitle("Order Cancelled");
            builder.setMessage("Your cancel request has been confirmed by " + order.getRestaurantName());
            builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    // Perform any action if needed after the user clicks OK
                }
            });
            AlertDialog dialog = builder.create();
            dialog.show();
        }
        else if (status == Status.RESTAURANT_CANCELLED) {
            AlertDialog.Builder builder = new AlertDialog.Builder(this);
            builder.setTitle("Order Cancelled");
            builder.setMessage("Your order has been cancelled by " + order.getRestaurantName());
            builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    // Perform any action if needed after the user clicks OK
                }
            });
            mediaPlayer.start();
            AlertDialog dialog = builder.create();
            dialog.show();
        }
    }


    public class OrderAdapter extends RecyclerView.Adapter<OrderAdapter.ViewHolder> {

        private ArrayList<Order> dataSet;
        private Context context;

        public class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
            private final TextView restaurantNameOrder;
            private final TextView date;
            private final TextView status;
            private final TextView price;
            private final TextView details;

            public ViewHolder(View view) {
                super(view);
                // Define click listener for the ViewHolder's View

                restaurantNameOrder = (TextView) view.findViewById(R.id.restaurantNameOrder);
                date = (TextView) view.findViewById(R.id.date);
                status = (TextView) view.findViewById(R.id.status);
                price = (TextView) view.findViewById(R.id.price);
                details = (TextView) view.findViewById(R.id.details);

                itemView.setOnClickListener(this);
            }

            @Override
            public void onClick(View view) {
                Intent orderDisplayIntent = new Intent (view.getContext(), OrderDisplayActivity.class);
                orderDisplayIntent.putExtra("order", gson.toJson(user.getLastOrder(), Order.class));
                startActivity(orderDisplayIntent);
            }

            public void bind(Order order) {
                restaurantNameOrder.setText(order.getRestaurantName());
                date.setText(order.getTime());
                status.setText(order.getStatusString());
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
                Restaurant clickedRestaurant = restaurantsList.get(position);

                Intent restaurantMenuIntent = new Intent (view.getContext(), RestaurantMenuActivity.class);
                restaurantMenuIntent.putExtra("restaurant", gson.toJson(clickedRestaurant, Restaurant.class));
                restaurantMenuIntent.putExtra("user", gson.toJson(user, User.class));
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



