package com.sandhya.ecommerce.activities;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.GridLayoutManager;

import android.annotation.SuppressLint;
import android.content.pm.ActivityInfo;
import android.os.Bundle;
import android.util.Log;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.sandhya.ecommerce.R;
import com.sandhya.ecommerce.adapter.ProductAdapter;
import com.sandhya.ecommerce.databinding.ActivityCategoryBinding;
import com.sandhya.ecommerce.models.Product;
import com.sandhya.ecommerce.utilities.Constrants;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

public class CategoryActivity extends AppCompatActivity {

    ActivityCategoryBinding binding;
    ProductAdapter productAdapter;
    ArrayList<Product> products;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityCategoryBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);

        initProductsCategory();
    }
    void initProductsCategory(){
        products = new ArrayList<>();
        //products api class 2
        int catId = getIntent().getIntExtra("catId",0);
        String categoryName = getIntent().getStringExtra("categoryName");
        getSupportActionBar().setTitle(categoryName);
        getProductsCategory(catId);
//        products.add(new Product("Abhay","data:image/png;base64,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","",12,21,1,10));
//        products.add(new Product("Abhay","data:image/png;base64,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","",12,21,2,10));
        productAdapter = new ProductAdapter(this,products);
        GridLayoutManager layoutManager = new GridLayoutManager(this,2);
        binding.productRecyclerView.setLayoutManager(layoutManager);
        binding.productRecyclerView.setAdapter(productAdapter);
    }

    void getProductsCategory(int catId){
        RequestQueue queue = Volley.newRequestQueue(this);
        String url = Constrants.GET_PRODUCTS_URL+"?category_id="+catId;
        StringRequest request = new StringRequest(Request.Method.GET, url, new Response.Listener<String>() {
            @SuppressLint("NotifyDataSetChanged")
            @Override
            public void onResponse(String response) {
                try {
                    Log.e("err",response);
                    JSONObject object = new JSONObject(response);
                    if (object.getString("status").equals("success")){
                        JSONArray productsArray = object.getJSONArray("products");
                        for (int a=0;a<productsArray.length();a++){
                            JSONObject jsonObject = productsArray.getJSONObject(a);
                            Product product = new Product(
                                    jsonObject.getString("name"),
                                    Constrants.PRODUCTS_IMAGE_URL+jsonObject.getString("image"),
                                    jsonObject.getString("status"),
                                    jsonObject.getDouble("price"),
                                    jsonObject.getDouble("price_discount"),
                                    jsonObject.getInt("id"),
                                    jsonObject.getInt("stock")
                            );products.add(product);
                        }
                        productAdapter.notifyDataSetChanged();
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {

            }
        });
        queue.add(request);
    }


    @Override
    public boolean onSupportNavigateUp() {
        finish();
        return super.onSupportNavigateUp();
    }
}