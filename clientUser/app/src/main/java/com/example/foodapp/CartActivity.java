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
import android.widget.TextView;

import java.util.ArrayList;

public class CartActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_cart);

        TextView restaurantName = findViewById(R.id.restaurantName);
        TextView restaurantAddress = findViewById(R.id.restaurantAddress);
        TextView totalPrice = findViewById(R.id.totalPrice);

        ArrayList<Food> foods = new ArrayList<Food>();
        foods.add(new Food(1, 1, "Food A", "Fried X, Served with Y", 1, 32, true));
        foods.add(new Food(1, 1, "dasdasdhsa", "Fried X, Served with Y", 1, 32, true));
        foods.add(new Food(1, 1, "Ffdjkglfdgfd", "Fried X, Served with Y", 3, 32, true));
        foods.add(new Food(1, 1, "Food A", "Fried X, Served with Y", 5, 32, true));
        foods.add(new Food(1, 1, "Food A", "Fried X, Served with Y", 7, 32, true));
        foods.add(new Food(1, 1, "Food A", "Fried X, Served with Y", 3, 32, true));
        foods.add(new Food(1, 1, "Food A", "Fried X, Served with Y", 3, 32, true));
        foods.add(new Food(1, 1, "Food A", "Fried X, Served with Y", 3, 32, true));
        foods.add(new Food(1, 1, "Food A", "Fried X, Served with Y", 3, 32, true));
        foods.add(new Food(1, 1, "Food A", "Fried X, Served with Y", 3, 32, true));
        foods.add(new Food(1, 1, "Food A", "Fried X, Served with Y", 3, 32, true));
        foods.add(new Food(1, 1, "Food A", "Fried X, Served with Y", 3, 32, true));
        foods.add(new Food(1, 1, "Food A", "Fried X, Served with Y", 3, 32, true));
        foods.add(new Food(1, 1, "Food A", "Fried X, Served with Y", 3, 32, true));
        foods.add(new Food(1, 1, "Food A", "Fried X, Served with Y", 3, 32, true));
        foods.add(new Food(1, 1, "Food A", "Fried X, Served with Y", 3, 32, true));

        RecyclerView recyclerViewFoods = findViewById(R.id.recyclerViewFoods);
        recyclerViewFoods.setLayoutManager(new LinearLayoutManager(this));

        FoodsAdapter foodsAdapter = new FoodsAdapter(foods, this);
        recyclerViewFoods.setAdapter(foodsAdapter);
        foodsAdapter.notifyDataSetChanged(); // Notify when dataset changed

        Button checkoutButton = findViewById(R.id.checkoutButton);
        checkoutButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent orderCompletedActivityIntent = new Intent(view.getContext(), OrderCompletedActivity.class);
                startActivity(orderCompletedActivityIntent);
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
                quantity.setText(String.valueOf(food.getQuantity()));
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