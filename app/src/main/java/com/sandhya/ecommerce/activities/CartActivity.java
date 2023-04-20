package com.sandhya.ecommerce.activities;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.LinearLayoutManager;

import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.os.Bundle;
import android.view.View;

import com.hishd.tinycart.model.Cart;
import com.hishd.tinycart.model.Item;
import com.hishd.tinycart.util.TinyCartHelper;
import com.sandhya.ecommerce.R;
import com.sandhya.ecommerce.adapter.CartAdapter;
import com.sandhya.ecommerce.databinding.ActivityCartBinding;
import com.sandhya.ecommerce.models.Product;

import java.util.ArrayList;
import java.util.Map;
import java.util.Objects;

public class CartActivity extends AppCompatActivity {

    ActivityCartBinding binding;
    CartAdapter adapter;
    ArrayList<Product> products;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityCartBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        Objects.requireNonNull(getSupportActionBar()).setDisplayHomeAsUpEnabled(true);
        this.setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);

        products = new ArrayList<>();
        Cart cart = TinyCartHelper.getCart();
        for (Map.Entry<Item,Integer> item : cart.getAllItemsWithQty().entrySet()){
            Product product = (Product) item.getKey();
            int quantity = item.getValue();
            product.setQuantity(quantity);
            products.add(product);
        }
//        products.add(new Product("mobile phone","https://tutorials.mianasad.com/ecommerce/uploads/news/1671412212744.png","not availwal",1,21,1,2));
//        products.add(new Product("mobile phone","https://firebasestorage.googleapis.com/v0/b/surabhi-college.appspot.com/o/Gallery%2F%5BB%40275f4b.jpg?alt=media&token=eb5a6f03-fc91-47f1-9c0e-237cf500732d","not availwal",1,21,1,2));
//        products.add(new Product("mobile phone","https://tutorials.mianasad.com/ecommerce/uploads/news/1671412212744.png","not availwal",1,21,1,2));
//        products.add(new Product("mobile phone","https://firebasestorage.googleapis.com/v0/b/surabhi-college.appspot.com/o/Gallery%2F%5BB%40275f4b.jpg?alt=media&token=eb5a6f03-fc91-47f1-9c0e-237cf500732d","not availwal",1,21,1,2));
        adapter = new CartAdapter(this, products, new CartAdapter.CartListener() {
            @Override
            public void onQuantityChanged() {
                binding.subToal.setText(String.format("$ %.2f",cart.getTotalPrice()));

            }
        });

        //line show item bottom
//        LinearLayoutManager layoutManager = new LinearLayoutManager(this);
//        DividerItemDecoration itemDecoration = new DividerItemDecoration(this,layoutManager.getOrientation());
//        binding.cartRecyclerView.setLayoutManager(layoutManager);
//        binding.cartRecyclerView.addItemDecoration(itemDecoration);

        binding.cartRecyclerView.setLayoutManager(new LinearLayoutManager(this));
        binding.cartRecyclerView.setAdapter(adapter);

        binding.subToal.setText(String.format("$ %.2f",cart.getTotalPrice()));


        binding.continueBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(CartActivity.this,CheckOutActivity.class));
            }
        });


    }

    @Override
    public boolean onSupportNavigateUp() {
        finish();
        return super.onSupportNavigateUp();
    }
}