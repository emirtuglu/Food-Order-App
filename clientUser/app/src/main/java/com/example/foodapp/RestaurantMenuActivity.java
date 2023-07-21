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

public class RestaurantMenuActivity extends AppCompatActivity {

    private ArrayList<Food> menu;
    private User user;
    private Restaurant restaurant;
    private Gson gson;
    private MenuAdapter menuAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_restaurant_menu);

        RequestManager requestManager = new RequestManager();
        gson = new Gson();
        String restaurantJson = getIntent().getStringExtra("restaurant");
        String userJson = getIntent().getStringExtra("user");
        restaurant = gson.fromJson(restaurantJson, Restaurant.class);
        user = gson.fromJson(userJson, User.class);
        if (user.getCart() == null) {
            user.setCart(new ArrayList<Food>());
        }

        TextView restaurantName = findViewById(R.id.restaurantName);
        TextView restaurantAddress = findViewById(R.id.restaurantAddress);

        restaurantName.setText(restaurant.getName());
        restaurantAddress.setText(restaurant.getAddress().getFullAddress());

        // Update menu
        String request2 = RequestManager.requestBuild("GET", "/restaurant-menu", "restaurantId", String.valueOf(restaurant.getId()), null);
        String response2 = null;
        try {
            response2 = requestManager.execute(request2).get();
        } catch (Exception e) {

        }
        String menuJson = RequestManager.getBody(response2);
        menu = gson.fromJson(menuJson, new TypeToken<List<Food>>(){}.getType());

        RecyclerView recyclerViewMenu = findViewById(R.id.recyclerViewMenu);
        recyclerViewMenu.setLayoutManager(new LinearLayoutManager(this));

        menuAdapter = new MenuAdapter(menu, this);
        recyclerViewMenu.setAdapter(menuAdapter);
        menuAdapter.notifyDataSetChanged(); // Notify when dataset changed

        ImageView cartImage = findViewById(R.id.cartImage);
        cartImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent cartActivityIntent = new Intent (view.getContext(), CartActivity.class);
                cartActivityIntent.putExtra("user", gson.toJson(user, User.class));
                startActivity(cartActivityIntent);
            }
        });
    }

    @Override
    protected void onResume() {
        super.onResume();

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
        }
        else {
            user.setCart(new ArrayList<Food>());
        }

        // Update quantities of foods in menu in case they are already in cart.
        for (Food food : menu) {
            food.setQuantity(0);
        }
        if (user.getCart() != null && user.getCart().size() > 0) {
            for (Food menuFood : menu) {
                for (Food cartFood : user.getCart()) {
                    if (cartFood.getId() == menuFood.getId() && cartFood.getQuantity() != menuFood.getQuantity()) {
                        menuFood.setQuantity(cartFood.getQuantity());
                    }
                }
            }
        }
        restaurant.setMenu(menu);
        menuAdapter.notifyDataSetChanged(); // Notify when dataset changed
    }

    public class MenuAdapter extends RecyclerView.Adapter<MenuAdapter.ViewHolder> {

        private ArrayList<Food> dataSet;
        private Context context;

        public class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
            private final TextView foodName;
            private final TextView foodDescription;
            private final TextView foodPrice;
            private final Button minusButton;
            private final TextView quantity;
            private final Button plusButton;

            public ViewHolder(View view) {
                super(view);
                foodName = (TextView) view.findViewById(R.id.foodName);
                foodDescription = (TextView) view.findViewById(R.id.foodDescription);
                foodPrice = (TextView) view.findViewById(R.id.foodPrice);
                minusButton = (Button) view.findViewById(R.id.minusButton);
                quantity = (TextView) view.findViewById(R.id.quantity);
                plusButton = (Button) view.findViewById(R.id.plusButton);

                minusButton.setOnClickListener(this);
                plusButton.setOnClickListener(this);
            }

            @Override
            public void onClick(View view) {
                int position = getAdapterPosition();
                Food clickedFood = menu.get(position);

                if (view.getId() == R.id.minusButton) {
                    if (clickedFood.getQuantity() <= 0) {
                        Toast.makeText(view.getContext(), "You already have 0 of this food.", Toast.LENGTH_SHORT).show();
                    }
                    else {
                        clickedFood.decrementQuantity();
                        user.setQuantityOfFoodInCart(clickedFood.getId(), clickedFood.getQuantity());
                        updateCart(view, clickedFood, false);
                    }
                }
                else if (view.getId() == R.id.plusButton) {
                    clickedFood.incrementQuantity();
                    user.setQuantityOfFoodInCart(clickedFood.getId(), clickedFood.getQuantity());
                    updateCart(view, clickedFood, true);
                }
            }

            public void bind(Food food) {
                foodName.setText(food.getName());
                foodDescription.setText(food.getDescription());
                foodPrice.setText("₺" + String.valueOf(food.getPrice()));
                quantity.setText(String.valueOf(food.getQuantity()));
                if (!food.isEnabled()) {
                    itemView.setAlpha((float) 0.50);
                    itemView.setBackgroundColor(getResources().getColor(R.color.gray_out));
                    foodName.setTextColor(getResources().getColor(R.color.light_Black));
                    foodDescription.setTextColor(getResources().getColor(R.color.light_Black));
                    foodPrice.setTextColor(getResources().getColor(R.color.light_Black));
                    quantity.setTextColor(getResources().getColor(R.color.light_Black));
                    minusButton.setBackgroundColor(getResources().getColor(R.color.light_Black));
                    plusButton.setBackgroundColor(getResources().getColor(R.color.light_Black));
                    minusButton.setClickable(false);
                    plusButton.setClickable(false);
                }
                else {
                    itemView.setAlpha((float) 1);
                    itemView.setBackgroundColor(getResources().getColor(R.color.white));
                    foodName.setTextColor(getResources().getColor(R.color.black));
                    foodDescription.setTextColor(getResources().getColor(R.color.black));
                    foodPrice.setTextColor(getResources().getColor(R.color.black));
                    quantity.setTextColor(getResources().getColor(R.color.black));
                    minusButton.setBackgroundColor(getResources().getColor(R.color.white));
                    plusButton.setBackgroundColor(getResources().getColor(R.color.white));
                    minusButton.setClickable(true);
                    plusButton.setClickable(true);
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
                if (response.contains("200 OK")) {
                    if (isAdd) {
                        Toast.makeText(view.getContext(), "Food added to cart", Toast.LENGTH_SHORT).show();
                    }
                    else {
                        Toast.makeText(view.getContext(), "Food removed from cart", Toast.LENGTH_SHORT).show();
                    }
                }
                else {
                    if (isAdd) {
                        clickedFood.decrementQuantity();
                    }
                    else {
                        clickedFood.incrementQuantity();
                    }
                    Toast.makeText(view.getContext(), RequestManager.getBody(response), Toast.LENGTH_SHORT).show();
                }
                menuAdapter.notifyDataSetChanged();
            }
        }

        public MenuAdapter (ArrayList<Food> dataSet, Context context) {
            this.dataSet = dataSet;
            this.context = context;
        }

        @Override
        public MenuAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            View view = LayoutInflater.from(parent.getContext())
                    .inflate(R.layout.item_menu_food, parent, false);

            return new RestaurantMenuActivity.MenuAdapter.ViewHolder(view);
        }

        @Override
        public void onBindViewHolder(@NonNull MenuAdapter.ViewHolder viewHolder, int position) {
            Food food = dataSet.get(position);
            viewHolder.bind(food);
        }

        @Override
        public int getItemCount() {
            return dataSet.size();
        }
    }
}