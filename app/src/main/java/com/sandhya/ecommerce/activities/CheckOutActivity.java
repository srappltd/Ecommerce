package com.sandhya.ecommerce.activities;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.hishd.tinycart.model.Cart;
import com.hishd.tinycart.model.Item;
import com.hishd.tinycart.util.TinyCartHelper;
import com.sandhya.ecommerce.R;
import com.sandhya.ecommerce.adapter.CartAdapter;
import com.sandhya.ecommerce.databinding.ActivityCheckOutBinding;
import com.sandhya.ecommerce.models.Product;
import com.sandhya.ecommerce.utilities.Constrants;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;
import java.util.jar.JarException;

public class CheckOutActivity extends AppCompatActivity {

    ActivityCheckOutBinding binding;
    CartAdapter adapter;
    ArrayList<Product> products;
    ProgressDialog dialog;
    Cart cart;
    double totalPrice = 0;
    final int tax = 10;
    @SuppressLint({"SetTextI18n", "DefaultLocale", "SourceLockedOrientationActivity"})
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityCheckOutBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        Objects.requireNonNull(getSupportActionBar()).setDisplayHomeAsUpEnabled(true);
        this.setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
        dialog = new ProgressDialog(this);
        dialog.setCancelable(false);
        dialog.setMessage("Processing...");

        products = new ArrayList<>();
        cart = TinyCartHelper.getCart();
        for (Map.Entry<Item,Integer> item : cart.getAllItemsWithQty().entrySet()){
            Product product = (Product) item.getKey();
            int quantity = item.getValue();
            product.setQuantity(quantity);
            products.add(product);
        }
        adapter = new CartAdapter(this, products, new CartAdapter.CartListener() {
            @Override
            public void onQuantityChanged() {
                binding.subTotalPrice.setText(String.format("$ %.2f",cart.getTotalPrice()));

            }
        });

        binding.subTotalPrice.setText(String.format("$ %.2f",cart.getTotalPrice()));
        binding.cartList.setLayoutManager(new LinearLayoutManager(this));
        binding.cartList.setAdapter(adapter);
        totalPrice = (cart.getTotalPrice().doubleValue()*tax/100)+cart.getTotalPrice().doubleValue();
        binding.totalPrice.setText("$"+totalPrice);
        binding.checkOutBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                processOrder();
            }
        });

    }

    void processOrder(){
        dialog.show();
        RequestQueue queue = Volley.newRequestQueue(this);
        JSONObject productOrder = new JSONObject();
        JSONObject dataObj = new JSONObject();
        try {
            productOrder.put("address",binding.address.getText().toString());
            productOrder.put("buyer",binding.fullName.getText().toString());
            productOrder.put("comment",binding.comments.getText().toString());
            productOrder.put("created_at", Calendar.getInstance().getTimeInMillis());
            productOrder.put("last_update", Calendar.getInstance().getTimeInMillis());
            productOrder.put("date_ship", Calendar.getInstance().getTimeInMillis());
            productOrder.put("email",binding.emailAddress.getText().toString());
            productOrder.put("phone",binding.mobile.getText().toString());
            productOrder.put("serial","cab8c1a4e4421a3b");
            productOrder.put("shipping","");
            productOrder.put("shipping_location","");
            productOrder.put("shipping_rate","0.0");
            productOrder.put("status","WAITING");
            productOrder.put("tax",tax);
            productOrder.put("total_fees",totalPrice);

            JSONArray product_order_detail = new JSONArray();
            for (Map.Entry<Item,Integer> item : cart.getAllItemsWithQty().entrySet()){
                Product product = (Product) item.getKey();
                int quantity = item.getValue();
                product.setQuantity(quantity);

                JSONObject productObj = new JSONObject();
                productObj.put("amount",quantity);
                productObj.put("price_item",product.getPrice());
                productObj.put("product_id",product.getId());
                productObj.put("product_name",product.getName());
                product_order_detail.put(productObj);
            }
            dataObj.put("product_order",productOrder);
            dataObj.put("product_order_detail",product_order_detail);
        }catch (JSONException e) {
            e.printStackTrace();
        }
        JsonObjectRequest request = new JsonObjectRequest(Request.Method.POST, Constrants.POST_ORDER_URL, dataObj, new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {
                try {
                    if (response.getString("status").equals("success")){
                        Toast.makeText(CheckOutActivity.this, "Success Order.", Toast.LENGTH_SHORT).show();
                        String orderId = response.getJSONObject("data").getString("code");
                        new AlertDialog.Builder(CheckOutActivity.this)
                                .setTitle("Order Successfully")
                                .setMessage("Your Order Id:"+orderId)
                                .setCancelable(false)
                                .setPositiveButton("Pay Now", new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialogInterface, int i) {

                                        Intent intent = new Intent(CheckOutActivity.this,PaymentActivity.class);
                                        intent.putExtra("orderCode",orderId);
                                        startActivity(intent);
                                    }
                                }).show();
                        Log.e("res",response.toString());
                    }else {
                        new AlertDialog.Builder(CheckOutActivity.this)
                                .setTitle("Order Failed")
                                .setMessage("Something went wrong, please try again")
                                .setCancelable(false)
                                .setPositiveButton("Close", new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialogInterface, int i) {


                                    }
                                }).show();
                        Toast.makeText(CheckOutActivity.this, "Error Order.", Toast.LENGTH_SHORT).show();
                        Log.e("res",response.toString());
                    }
                    dialog.dismiss();
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                dialog.dismiss();
            }
        }) {
            @Override
            public Map<String, String> getHeaders() throws AuthFailureError {
                Map<String,String> header = new HashMap<>();
                header.put("Security","secure_code");
                return header;
            }
        };
        queue.add(request);
    }

    @Override
    public boolean onSupportNavigateUp() {
        finish();
        return super.onSupportNavigateUp();
    }
}