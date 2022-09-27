package com.appbuildersworld.zedtourerjava.adapter;

import android.annotation.SuppressLint;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;

import com.appbuildersworld.zedtourerjava.R;
import com.appbuildersworld.zedtourerjava.connectivity.Constant;
import com.appbuildersworld.zedtourerjava.models.MBusinessOrder;
import com.appbuildersworld.zedtourerjava.utils.Tools;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

public class AdapterCustomerOrderList extends RecyclerView.Adapter<RecyclerView.ViewHolder> {


    private List<MBusinessOrder> items = new ArrayList<>();
    private Context ctx;
    private OnItemClickListener mOnItemClickListener;

    public interface OnItemClickListener {
        void onItemClick(View view, MBusinessOrder obj, int position);
    }

    public void setOnItemClickListener(final OnItemClickListener mItemClickListener) {
        this.mOnItemClickListener = mItemClickListener;
    }

    public AdapterCustomerOrderList(Context context, List<MBusinessOrder> items) {
        this.items = items;
        ctx = context;

    }

    public class OriginalViewHolder extends RecyclerView.ViewHolder {
        public TextView tvBusinessName, tvDate, tvAmount, tvStatus;
        public View lyt_parent;

        public OriginalViewHolder(View v) {
            super(v);
            tvBusinessName = (TextView) v.findViewById(R.id.tvBusinessName);
            tvDate = (TextView) v.findViewById(R.id.tvDate);
            tvAmount = (TextView) v.findViewById(R.id.tvAmount);
            tvStatus = (TextView) v.findViewById(R.id.tvStatus);
            lyt_parent = (View) v.findViewById(R.id.lyt_parent);
        }
    }


    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        RecyclerView.ViewHolder vh;

        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_customer_order, parent, false);
        vh = new OriginalViewHolder(v);
        return vh;
    }

    // Replace the contents of a view (invoked by the layout manager)
    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, @SuppressLint("RecyclerView") final int position) {
        MBusinessOrder p = items.get(position);
        if (holder instanceof OriginalViewHolder) {
            OriginalViewHolder view = (OriginalViewHolder) holder;
            try {
                JSONObject cart = new JSONObject(p.getOrderCart());
                view.tvAmount.setText("ZMW " + cart.getString("total"));
            } catch (JSONException e) {
                e.printStackTrace();
            }
            view.tvBusinessName.setText(p.getBusinessName());

            view.tvDate.setText(p.getOrderDate());
            view.tvStatus.setText(p.getStatus());

            view.lyt_parent.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    if (mOnItemClickListener != null) {
                        mOnItemClickListener.onItemClick(view, items.get(position), position);
                    }
                }
            });
        } else {

        }
    }

    @Override
    public int getItemCount() {
        return items.size();
    }


}