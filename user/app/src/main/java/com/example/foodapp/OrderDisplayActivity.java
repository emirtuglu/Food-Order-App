package com.example.foodapp;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Context;
import android.content.DialogInterface;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.google.gson.Gson;

import java.util.ArrayList;

public class OrderDisplayActivity extends AppCompatActivity {

    private ArrayList<Food> foods;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_order_display);

        Gson gson = new Gson();
        String orderJson = getIntent().getStringExtra("order");
        Order order = gson.fromJson(orderJson, Order.class);

        TextView restaurantName = findViewById(R.id.restaurantName);
        TextView orderStatus = findViewById(R.id.orderStatus);
        TextView totalPrice = findViewById(R.id.totalPrice);
        TextView date = findViewById(R.id.date);
        TextView cancelOrder = findViewById(R.id.cancelOrderText);

        restaurantName.setText(order.getRestaurantName());
        orderStatus.setText(order.getStatusString());
        totalPrice.setText("Total Price: ₺" + order.getPrice());
        date.setText("Date: " + order.getTime());
        if (order.getStatus() == Status.ACTIVE) {
            cancelOrder.setVisibility(View.VISIBLE);
        }
        else {
            cancelOrder.setVisibility(View.INVISIBLE);
        }

        foods = new ArrayList<Food>();
        foods.addAll(order.getFoods());

        RecyclerView recyclerViewFoods = findViewById(R.id.recyclerViewFoods);
        recyclerViewFoods.setLayoutManager(new LinearLayoutManager(this));

        FoodsAdapter foodsAdapter = new FoodsAdapter(foods, this);
        recyclerViewFoods.setAdapter(foodsAdapter);
        foodsAdapter.notifyDataSetChanged(); // Notify when dataset changed

        cancelOrder.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                DialogInterface.OnClickListener dialogClickListener = new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        switch (which){
                            case DialogInterface.BUTTON_POSITIVE:
                                // Yes option
                                RequestManager requestManager = new RequestManager();
                                order.setStatus(Status.USER_REQUESTED_CANCEL);
                                String orderJson = gson.toJson(order, Order.class);
                                String request = RequestManager.requestBuild("POST", "/update-order", null, null, orderJson);
                                String response = null;
                                try {
                                    response = requestManager.execute(request).get();

                                    if (response != null && response.contains("200 OK")) {
                                        orderStatus.setText(order.getStatusString());
                                        Toast.makeText(view.getContext(), "Cancel request has been sent to the restaurant", Toast.LENGTH_SHORT).show();
                                    }
                                    else {
                                        Toast.makeText(view.getContext(), RequestManager.getBody(response), Toast.LENGTH_SHORT).show();
                                    }
                                }
                                catch (Exception e) {
                                    Toast.makeText(view.getContext(), "Connection error", Toast.LENGTH_LONG).show();
                                }

                            case DialogInterface.BUTTON_NEGATIVE:
                                //No option
                                break;
                        }
                    }
                };
                AlertDialog.Builder ab = new AlertDialog.Builder(view.getContext());
                ab.setMessage("Are you sure to cancel the order?").setPositiveButton("Yes", dialogClickListener)
                        .setNegativeButton("No", dialogClickListener).show();


            }
        });
    }

    public class FoodsAdapter extends RecyclerView.Adapter<FoodsAdapter.ViewHolder> {

        private ArrayList<Food> dataSet;
        private Context context;

        public class ViewHolder extends RecyclerView.ViewHolder {
            private final TextView foodName;
            private final TextView foodPrice;
            private final TextView quantity;

            public ViewHolder(View view) {
                super(view);
                foodName = (TextView) view.findViewById(R.id.foodName);
                foodPrice = (TextView) view.findViewById(R.id.foodPrice);
                quantity = (TextView) view.findViewById(R.id.quantity);
            }

            public void bind(Food food) {
                foodName.setText(food.getName());
                foodPrice.setText("₺" + String.valueOf(food.getPrice()));
                quantity.setText("x" + String.valueOf(food.getQuantity()));
            }
        }

        public FoodsAdapter (ArrayList<Food> dataSet, Context context) {
            this.dataSet = dataSet;
            this.context = context;
        }

        @Override
        public FoodsAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            View view = LayoutInflater.from(parent.getContext())
                    .inflate(R.layout.item_order_food, parent, false);

            return new FoodsAdapter.ViewHolder(view);
        }

        @Override
        public void onBindViewHolder(@NonNull FoodsAdapter.ViewHolder viewHolder, int position) {
            Food food = dataSet.get(position);
            viewHolder.bind(food);
        }

        @Override
        public int getItemCount() {
            return dataSet.size();
        }
    }
}