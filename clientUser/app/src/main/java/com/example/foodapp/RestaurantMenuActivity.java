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

import java.util.ArrayList;

public class RestaurantMenuActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_restaurant_menu);

        ArrayList<Food> menu = new ArrayList<Food>();
        menu.add(new Food(1, 1, "Food A", "Fried X, Served with Y", 0, 32, true));
        menu.add(new Food(1, 1, "Food A", "Fried X, Served with Y", 0, 32, true));
        menu.add(new Food(1, 1, "Food A", "Fried X, Served with Y", 0, 32, true));
        menu.add(new Food(1, 1, "Food A", "Fried X, Served with Y", 0, 32, true));
        menu.add(new Food(1, 1, "Food A", "Fried X, Served with Y", 0, 32, true));
        menu.add(new Food(1, 1, "Food A", "Fried X, Served with Y", 0, 32, true));
        menu.add(new Food(1, 1, "Food A", "Fried X, Served with Y", 0, 32, true));
        menu.add(new Food(1, 1, "Food A", "Fried X, Served with Y", 0, 32, true));
        menu.add(new Food(1, 1, "Food A", "Fried X, Served with Y", 0, 32, true));

        RecyclerView recyclerViewMenu = findViewById(R.id.recyclerViewMenu);
        recyclerViewMenu.setLayoutManager(new LinearLayoutManager(this));

        MenuAdapter menuAdapter = new MenuAdapter(menu, this);
        recyclerViewMenu.setAdapter(menuAdapter);
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

                if (view.getId() == R.id.minusButton) {

                }
                else if (view.getId() == R.id.plusButton) {

                }
                else {

                }
            }

            public void bind(Food food) {
                foodName.setText(food.getName());
                foodDescription.setText(food.getDescription());
                foodPrice.setText("₺" + String.valueOf(food.getPrice()));
                quantity.setText(String.valueOf(food.getQuantity()));
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