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

import java.util.ArrayList;

public class OrdersActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_orders);

        ArrayList<Order> orders = new ArrayList<Order>();
        orders.add(new Order(1, 1, 1, "Restaurant A", "18.07.2023 / 15:20", 155.55, Status.ACTIVE, null));
        orders.add(new Order(1, 1, 1, "Restaurant A", "18.07.2023 / 15:20", 155.55, Status.ACTIVE, null));
        orders.add(new Order(1, 1, 1, "Restaurant A", "18.07.2023 / 15:20", 155.55, Status.ACTIVE, null));
        orders.add(new Order(1, 1, 1, "Restaurant A", "18.07.2023 / 15:20", 155.55, Status.ACTIVE, null));
        orders.add(new Order(1, 1, 1, "Restaurant A", "18.07.2023 / 15:20", 155.55, Status.ACTIVE, null));
        orders.add(new Order(1, 1, 1, "Restaurant A", "18.07.2023 / 15:20", 155.55, Status.ACTIVE, null));
        orders.add(new Order(1, 1, 1, "Restaurant A", "18.07.2023 / 15:20", 155.55, Status.ACTIVE, null));
        orders.add(new Order(1, 1, 1, "Restaurant A", "18.07.2023 / 15:20", 155.55, Status.ACTIVE, null));
        orders.add(new Order(1, 1, 1, "Restaurant A", "18.07.2023 / 15:20", 155.55, Status.ACTIVE, null));
        orders.add(new Order(1, 1, 1, "Restaurant A", "18.07.2023 / 15:20", 155.55, Status.ACTIVE, null));
        orders.add(new Order(1, 1, 1, "Restaurant A", "18.07.2023 / 15:20", 155.55, Status.ACTIVE, null));
        orders.add(new Order(1, 1, 1, "Restaurant A", "18.07.2023 / 15:20", 155.55, Status.ACTIVE, null));

        RecyclerView recyclerViewOrders = findViewById(R.id.recyclerViewOrders);
        recyclerViewOrders.setLayoutManager(new LinearLayoutManager(this));

        OrderAdapter orderAdapter = new OrdersActivity.OrderAdapter(orders, this);
        recyclerViewOrders.setAdapter(orderAdapter);
        orderAdapter.notifyDataSetChanged(); // Notify when dataset changed
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
                // Move to order activity
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