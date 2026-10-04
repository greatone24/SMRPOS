package com.dee.smrpos;

import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.activity.OnBackPressedCallback;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.dee.smrpos.entity.CartItem;

import java.util.List;

public class CartAdapter extends RecyclerView.Adapter<CartAdapter.CartViewHolder> {
    private List<CartItem> cartItems;

    private OnCartChangeListener listener;
    public interface OnCartChangeListener {
        void onCartUpdated(List<CartItem> cartItems);
        void removeCartItem(int id);
    }
    public CartAdapter(List<CartItem> cartItems, OnCartChangeListener listener) {
        this.cartItems = cartItems;
        this.listener = listener;
    }

    @NonNull
    @Override
    public CartViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.cart_item, parent, false);

        return new CartViewHolder(view);
    }


    @Override
    public void onBindViewHolder(@NonNull CartViewHolder holder, int position) {
        CartItem item = cartItems.get(position);

        holder.name.setText(item.getName());
        holder.quantity.setText(String.valueOf(item.getQuantity()));
        holder.price.setText(String.format("₱%.2f", (item.getQuantity() * item.getPrice())));
        holder.origPrice.setText(String.format("₱%.2f", item.getPrice()));
        holder.removeBtn.setOnClickListener(v -> {
            cartItems.remove(position);
            notifyItemRemoved(position);
            listener.onCartUpdated(cartItems); // notify activity
            listener.removeCartItem(item.getId());
        });
        holder.btnArrowUp.setOnClickListener(v -> {
            item.setQuantity(item.getQuantity() + 1);
            holder.quantity.setText(String.valueOf(item.getQuantity()));
            holder.price.setText(String.format("₱%.2f", item.getPrice() * item.getQuantity()));
            listener.onCartUpdated(cartItems);
        });
        holder.btnArrowDown.setOnClickListener(view -> {
            if (item.getQuantity() > 1) {
                item.setQuantity(item.getQuantity() - 1);
                holder.quantity.setText(String.valueOf(item.getQuantity()));
                holder.price.setText(String.format("₱%.2f", item.getPrice() * item.getQuantity()));
                listener.onCartUpdated(cartItems);
            }
        });
    }

    @Override
    public int getItemCount() {
        return cartItems.size();
    }

    static class CartViewHolder extends RecyclerView.ViewHolder {
        TextView name, quantity, price, origPrice;
        Button removeBtn;
        ImageButton btnArrowUp, btnArrowDown;
        ImageView imgProduct;

        CartViewHolder(View itemView) {
            super(itemView);
            name = itemView.findViewById(R.id.txtProductName);
            quantity = itemView.findViewById(R.id.txtQuantity);
            price = itemView.findViewById(R.id.txtPrice);
            removeBtn = itemView.findViewById(R.id.btnRemove);
            imgProduct = itemView.findViewById(R.id.imgProduct);
            btnArrowUp = itemView.findViewById(R.id.btnArrowUp);
            btnArrowDown = itemView.findViewById(R.id.btnArrowDown);
            origPrice = itemView.findViewById(R.id.txtOriginalPrice);
        }
    }


}

