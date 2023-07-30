package com.example.foodapp;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.util.ArrayList;
import java.util.List;

public class CartActivity extends AppCompatActivity {
    private User user;
    private ArrayList<Food> cart;
    private Gson gson;
    private FoodsAdapter foodsAdapter;
    private RecyclerView recyclerViewFoods;
    private TextView restaurantName;
    private TextView totalPrice;
    private ImageView restaurantImage;
    private Button checkoutButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_cart);

        gson = new Gson();

        String userJson = getIntent().getStringExtra("user");
        user = gson.fromJson(userJson, User.class);
        cart = new ArrayList<Food>();

        recyclerViewFoods = findViewById(R.id.recyclerViewFoods);
        recyclerViewFoods.setLayoutManager(new LinearLayoutManager(this));
        foodsAdapter = new FoodsAdapter(cart, this);
        recyclerViewFoods.setAdapter(foodsAdapter);

        Button checkoutButton = findViewById(R.id.checkoutButton);
        checkoutButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Order order = new Order(user.getId(), cart.get(0).getRestaurantId(), cart.get(0).getRestaurantName(), user.getSelectedAddress(), user.getFullName(), user.getPhoneNumber(), user.getTotalPriceOfCart(), cart);
                RequestManager requestManager = new RequestManager();
                String request = RequestManager.requestBuild("POST", "/send-order", null, null, gson.toJson(order, Order.class));
                String response = null;
                try {
                    response = requestManager.execute(request).get();

                    if (response != null && response.contains("200 OK")) {
                        user.setCart(new ArrayList<Food>());
                        cart.clear();
                        foodsAdapter.notifyDataSetChanged();
                        Intent orderCompletedActivityIntent = new Intent(view.getContext(), OrderCompletedActivity.class);
                        startActivity(orderCompletedActivityIntent);
                    }
                    else {
                        Toast.makeText(view.getContext(), RequestManager.getBody(response), Toast.LENGTH_SHORT).show();
                    }

                } catch (Exception e) {
                    Toast.makeText(view.getContext(), "Connection error", Toast.LENGTH_LONG).show();
                }
            }
        });
    }

    @Override
    protected void onResume() {
        super.onResume();

        restaurantName = findViewById(R.id.restaurantName);
        restaurantImage = findViewById(R.id.restaurantImage);
        totalPrice = findViewById(R.id.totalPrice);
        checkoutButton = findViewById(R.id.checkoutButton);

        // Update cart
        RequestManager requestManager = new RequestManager();
        String request = RequestManager.requestBuild("GET", "/user-cart", "userId", String.valueOf(user.getId()), null);
        String response = null;
        try {
            response = requestManager.execute(request).get();
        } catch (Exception e) {
        }
        String cartJson = RequestManager.getBody(response);
        if (cartJson.length() > 3) {
            user.setCart(gson.fromJson(cartJson, new TypeToken<List<Food>>(){}.getType()));
            restaurantName.setText(user.getCart().get(0).getRestaurantName());
            restaurantImage.setVisibility(View.VISIBLE);
            totalPrice.setVisibility(View.VISIBLE);
            totalPrice.setText("Total Price: ₺" + user.getTotalPriceOfCart());
            checkoutButton.setVisibility(View.VISIBLE);
        }
        else { // Cart is empty
            restaurantName.setText("Your cart is empty");
            restaurantImage.setVisibility(View.INVISIBLE);
            totalPrice.setVisibility(View.INVISIBLE);
            checkoutButton.setVisibility(View.INVISIBLE);
            user.setCart(new ArrayList<Food>());
        }
        cart.clear();
        cart.addAll(user.getCart());
        foodsAdapter.notifyDataSetChanged();
    }

    public class FoodsAdapter extends RecyclerView.Adapter<FoodsAdapter.ViewHolder> {

        private ArrayList<Food> dataSet;
        private Context context;

        public class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
            private final TextView foodName;
            private final TextView foodPrice;
            private final TextView quantity;
            private final Button minusButton;
            private final Button plusButton;

            public ViewHolder(View view) {
                super(view);
                foodName = (TextView) view.findViewById(R.id.foodName);
                foodPrice = (TextView) view.findViewById(R.id.foodPrice);

                minusButton = (Button) view.findViewById(R.id.minusButton);
                quantity = (TextView) view.findViewById(R.id.quantity);
                plusButton = (Button) view.findViewById(R.id.plusButton);

                minusButton.setOnClickListener(this);
                plusButton.setOnClickListener(this);
            }

            public void bind(Food food) {
                foodName.setText(food.getName());
                foodPrice.setText("₺" + String.format("%.2f", food.getPrice() * food.getQuantity()));
                quantity.setText(String.valueOf(food.getQuantity()));

            }

            @Override
            public void onClick(View view) {
                int position = getAdapterPosition();
                Food clickedFood = user.getCart().get(position);

                if (view.getId() == R.id.minusButton) {
                    if (clickedFood.getQuantity() <= 0) {
                        Toast.makeText(view.getContext(), "You already have 0 of this food.", Toast.LENGTH_SHORT).show();
                    }
                    else {
                        clickedFood.decrementQuantity();
                        updateCart(view, clickedFood, false);
                    }
                }
                else if (view.getId() == R.id.plusButton) {
                    clickedFood.incrementQuantity();
                    updateCart(view, clickedFood, true);
                }
            }

            public void updateCart(View view, Food clickedFood, boolean isAdd) {
                if (!user.getCart().contains(clickedFood)) {
                    user.getCart().add(clickedFood);
                }
                if (clickedFood.getQuantity() == 0) {
                    user.getCart().remove(clickedFood);
                }
                RequestManager requestManager = new RequestManager();
                String userJson = gson.toJson(user, User.class);
                String request = RequestManager.requestBuild("POST", "/update-cart", null, null, userJson);
                String response = null;
                try {
                    response = requestManager.execute(request).get();
                } catch (Exception e) {

                }
                cart.clear();
                cart.addAll(user.getCart());
                if (cart.isEmpty()) {
                    restaurantName.setText("Your cart is empty");
                    restaurantImage.setVisibility(View.INVISIBLE);
                    totalPrice.setVisibility(View.INVISIBLE);
                    checkoutButton.setVisibility(View.INVISIBLE);
                }
                else {
                    restaurantName.setText(user.getCart().get(0).getRestaurantName());
                    restaurantImage.setVisibility(View.VISIBLE);
                    totalPrice.setVisibility(View.VISIBLE);
                    totalPrice.setText("Total Price: ₺" + user.getTotalPriceOfCart());
                    checkoutButton.setVisibility(View.VISIBLE);
                }
                totalPrice.setText("Total Price: ₺" + user.getTotalPriceOfCart());
                Toast.makeText(view.getContext(), RequestManager.getBody(response), Toast.LENGTH_SHORT).show();
                foodsAdapter.notifyDataSetChanged();
            }
        }

        public FoodsAdapter (ArrayList<Food> dataSet, Context context) {
            this.dataSet = dataSet;
            this.context = context;
        }

        @Override
        public FoodsAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            View view = LayoutInflater.from(parent.getContext())
                    .inflate(R.layout.item_cart_food, parent, false);

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