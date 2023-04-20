package com.sandhya.ecommerce.activities;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.os.Bundle;
import android.text.Html;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.bumptech.glide.Glide;
import com.hishd.tinycart.model.Cart;
import com.hishd.tinycart.util.TinyCartHelper;
import com.sandhya.ecommerce.R;
import com.sandhya.ecommerce.databinding.ActivityProductDetailBinding;
import com.sandhya.ecommerce.models.Product;
import com.sandhya.ecommerce.utilities.Constrants;

import org.json.JSONException;
import org.json.JSONObject;

public class ProductDetailActivity extends AppCompatActivity {

    ActivityProductDetailBinding binding;
    Product currentProduct;

    @SuppressLint({"MissingInflatedId", "SourceLockedOrientationActivity", "SetTextI18n"})
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityProductDetailBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        //portrait mode true
        this.setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);

        String name = getIntent().getStringExtra("name");
        String image = getIntent().getStringExtra("image");
        Double price = getIntent().getDoubleExtra("price",0);
        int id = getIntent().getIntExtra("id",0);
        getProductDetail(id);
        binding.productPrice.setText(price.toString());

        Glide.with(this).load(image).into(binding.productImg);
        getSupportActionBar().setTitle(name);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        binding.addToCardBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Cart cart = TinyCartHelper.getCart();
                cart.addItem(currentProduct,1);
                Toast.makeText(ProductDetailActivity.this, "Add Item Successfully", Toast.LENGTH_SHORT).show();
                binding.addToCardBtn.setEnabled(false);
                binding.addToCardBtn.setText("added in cart");
            }
        });


    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.cart,menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        if (item.getItemId()==R.id.cart){
            startActivity(new Intent(this,CartActivity.class));
        }
        return super.onOptionsItemSelected(item);
    }

    void getProductDetail(int id){
        RequestQueue queue = Volley.newRequestQueue(this);
        String url = Constrants.GET_PRODUCTS_DETAILS_URL+id;
        StringRequest request = new StringRequest(Request.Method.GET, url, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                try {
                    JSONObject object = new JSONObject(response);
                    if (object.getString("status").equals("success")){
                        JSONObject productDetail = object.getJSONObject("product");
                        String description = productDetail.getString("description");
                        binding.productDes.setText(Html.fromHtml(description));
                        currentProduct= new Product(
                                productDetail.getString("name"),
                                Constrants.PRODUCTS_IMAGE_URL+productDetail.getString("image"),
                                productDetail.getString("status"),
                                productDetail.getDouble("price"),
                                productDetail.getDouble("price_discount"),
                                productDetail.getInt("id"),
                                productDetail.getInt("stock")
                        );
                    }

                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {

            }
        });queue.add(request);
    }

    @Override
    public boolean onSupportNavigateUp() {
        finish();
        return super.onSupportNavigateUp();
    }
}