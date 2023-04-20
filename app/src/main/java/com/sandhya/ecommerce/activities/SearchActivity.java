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
import com.sandhya.ecommerce.databinding.ActivitySearchBinding;
import com.sandhya.ecommerce.models.Product;
import com.sandhya.ecommerce.utilities.Constrants;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

public class SearchActivity extends AppCompatActivity {

    ActivitySearchBinding binding;
    ProductAdapter productAdapter;
    ArrayList<Product> products;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivitySearchBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
        initProductsSearch();
    }
    void initProductsSearch(){
        products = new ArrayList<>();
        //products api class 2
        //int catId = getIntent().getIntExtra("catId",0);
        String query = getIntent().getStringExtra("query");
        getSupportActionBar().setTitle(query);
        getProductsSearch(query);
        productAdapter = new ProductAdapter(this,products);
        GridLayoutManager layoutManager = new GridLayoutManager(this,2);
        binding.searchList.setLayoutManager(layoutManager);
        binding.searchList.setAdapter(productAdapter);
    }
    void getProductsSearch(String query){
        RequestQueue queue = Volley.newRequestQueue(this);
        String url = Constrants.GET_PRODUCTS_URL+"?q="+query;
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