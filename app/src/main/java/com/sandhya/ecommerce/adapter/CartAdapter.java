package com.sandhya.ecommerce.adapter;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.content.Context;
import android.graphics.drawable.ColorDrawable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.hishd.tinycart.model.Cart;
import com.hishd.tinycart.util.TinyCartHelper;
import com.sandhya.ecommerce.R;
import com.sandhya.ecommerce.databinding.ItemCartBinding;
import com.sandhya.ecommerce.databinding.QuantityBinding;
import com.sandhya.ecommerce.models.Product;

import java.util.ArrayList;

public class CartAdapter extends RecyclerView.Adapter<CartAdapter.CartViewHolder>{
    Context context;
    ArrayList<Product> products;
    CartListener cartListener;
    Cart cart;

    public interface CartListener{
        public void onQuantityChanged();
    }

    public CartAdapter(Context context, ArrayList<Product> products, CartListener cartListener) {
        this.context = context;
        this.products = products;
        this.cartListener = cartListener;
        cart = TinyCartHelper.getCart();

    }

    @NonNull
    @Override
    public CartViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new CartViewHolder(LayoutInflater.from(context).inflate(R.layout.item_cart,parent,false));
    }

    @SuppressLint("SetTextI18n")
    @Override
    public void onBindViewHolder(@NonNull CartViewHolder holder, int position) {
        Product product = products.get(position);
        Glide.with(context).load(product.getImage()).into(holder.binding.itemImg);
        holder.binding.nameitem.setText(product.getName());
        holder.binding.price.setText("$"+product.getPrice());
        holder.binding.quantitryItem.setText(product.getQuantity()+" item(s)");


        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @SuppressLint({"ResourceAsColor", "SetTextI18n"})
            @Override
            public void onClick(View view) {
                QuantityBinding quantityBinding = QuantityBinding.inflate(LayoutInflater.from(context));
                AlertDialog dialog = new AlertDialog.Builder(context).setView(quantityBinding.getRoot()).create();
                dialog.getWindow().setBackgroundDrawable(new ColorDrawable(android.R.color.transparent));

                quantityBinding.productName.setText(product.getName());
                quantityBinding.productStock.setText("Stock: "+product.getStock());
                quantityBinding.quantitryItem.setText(String.valueOf(product.getQuantity()));

                int stock = product.getStock();
                quantityBinding.positiveBtn.setOnClickListener(new View.OnClickListener() {
                    @SuppressLint("NotifyDataSetChanged")
                    @Override
                    public void onClick(View view) {
                        int quantity = product.getQuantity();
                        quantity++;
                        if (quantity>product.getStock()){
                            Toast.makeText(context, "Max stock available: "+product.getStock(), Toast.LENGTH_SHORT).show();
                            return;
                        }else {
                            product.setQuantity(quantity);
                            quantityBinding.quantitryItem.setText(String.valueOf(quantity));
                        }

                        //product update quantity item
                        notifyDataSetChanged();
                        cart.updateItem(product,product.getQuantity());
                        cartListener.onQuantityChanged();

                    }
                });
                quantityBinding.negativeBtn.setOnClickListener(new View.OnClickListener() {
                    @SuppressLint("NotifyDataSetChanged")
                    @Override
                    public void onClick(View view) {
                        int quantity = product.getQuantity();
                        if (quantity>1)quantity--;
                        product.setQuantity(quantity);
                        quantityBinding.quantitryItem.setText(String.valueOf(quantity));

                        //product update quantity item
                        notifyDataSetChanged();
                        cart.updateItem(product,product.getQuantity());
                        cartListener.onQuantityChanged();

                    }
                });
                quantityBinding.saveBtn.setOnClickListener(new View.OnClickListener() {
                    @SuppressLint("NotifyDataSetChanged")
                    @Override
                    public void onClick(View view) {
                        dialog.dismiss();
                        //product update quantity item
                        notifyDataSetChanged();
                        cart.updateItem(product,product.getQuantity());
                        cartListener.onQuantityChanged();
                    }
                });

                dialog.show();
            }
        });

    }

    @Override
    public int getItemCount() {
        return products.size();
    }

    public class CartViewHolder extends RecyclerView.ViewHolder{

        ItemCartBinding binding;
        public CartViewHolder(@NonNull View itemView) {
            super(itemView);
            binding = ItemCartBinding.bind(itemView);
        }
    }
}
