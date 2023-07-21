package com.example.foodapp;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

public class OrdersActivity extends AppCompatActivity {
    private ArrayList<Order> orders;
    private RecyclerView recyclerViewOrders;
    private OrderAdapter orderAdapter;
    private Handler handler;
    private Runnable runnable;
    private String ordersRefreshRequestString;
    private int refreshOrdersDelay = 10000; // 10 seconds

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_orders);

        String userId = getIntent().getStringExtra("userId");
        handler = new Handler();

        orders = new ArrayList<Order>();
        ordersRefreshRequestString = RequestManager.requestBuild("GET", "/user-orders", "userId", userId, null);

        recyclerViewOrders = findViewById(R.id.recyclerViewOrders);
        recyclerViewOrders.setLayoutManager(new LinearLayoutManager(this));

        orderAdapter = new OrdersActivity.OrderAdapter(orders, this);
        recyclerViewOrders.setAdapter(orderAdapter);
    }

    @Override
    protected void onResume() {
        super.onResume();
        Gson gson = new Gson();

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
                ArrayList<Order> updatedOrders = gson.fromJson(ordersJson, new TypeToken<List<Order>>(){}.getType());

                orders.clear();
                orders.addAll(updatedOrders);
                orders.sort(Comparator.comparing(Order::getTime, Comparator.reverseOrder()));

                orderAdapter.notifyDataSetChanged(); // Notify when dataset changed

                handler.postDelayed(runnable, refreshOrdersDelay);
            }
        }, 1000);
    }

    @Override
    protected void onPause() {
        handler.removeCallbacks(runnable); //stop handler when activity not visible
        super.onPause();
    }

    public class OrderAdapter extends RecyclerView.Adapter<OrdersActivity.OrderAdapter.ViewHolder> {

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
                restaurantName = (TextView) view.findViewById(R.id.restaurantNameOrder);
                date = (TextView) view.findViewById(R.id.date);
                status = (TextView) view.findViewById(R.id.status);
                price = (TextView) view.findViewById(R.id.price);
                details = (TextView) view.findViewById(R.id.details);

                itemView.setOnClickListener(this);
            }

            @Override
            public void onClick(View view) {
                Gson gson = new Gson();
                int position = getAdapterPosition();

                Intent orderDisplayIntent = new Intent (view.getContext(), OrderDisplayActivity.class);
                orderDisplayIntent.putExtra("order", gson.toJson(orders.get(position)));
                startActivity(orderDisplayIntent);
            }

            public void bind(Order order) {
                restaurantName.setText(order.getRestaurantName());
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
        public OrdersActivity.OrderAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            View view = LayoutInflater.from(parent.getContext())
                    .inflate(R.layout.item_order, parent, false);

            return new OrdersActivity.OrderAdapter.ViewHolder(view);
        }

        @Override
        public void onBindViewHolder(@NonNull OrdersActivity.OrderAdapter.ViewHolder viewHolder, int position) {
            Order order = dataSet.get(position);
            viewHolder.bind(order);
        }

        @Override
        public int getItemCount() {
            return dataSet.size();
        }
    }
}