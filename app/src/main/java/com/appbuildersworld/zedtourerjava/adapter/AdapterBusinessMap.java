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
import com.appbuildersworld.zedtourerjava.models.MBusiness;
import com.appbuildersworld.zedtourerjava.models.MCashier;
import com.appbuildersworld.zedtourerjava.utils.Tools;

import java.util.List;

// The adapter class which
// extends RecyclerView Adapter
public class AdapterBusinessMap
        extends RecyclerView.Adapter<AdapterBusinessMap.MyView> {

    // List with String type
    private List<MBusiness> list;
    private Context ctx;
    private AdapterBusinessMap.OnItemClickListener mOnItemClickListener;

    // View Holder class which
    // extends RecyclerView.ViewHolder

    public interface OnItemClickListener {
        void onItemClick(View view, MBusiness obj, int position);
    }

    public void setOnItemClickListener(final AdapterBusinessMap.OnItemClickListener mItemClickListener) {
        this.mOnItemClickListener = mItemClickListener;
    }

    public class MyView
            extends RecyclerView.ViewHolder {

        // Text View
        TextView tvBusinessName;
        TextView tvNatureOfBusiness;
        ImageView ivBusinessBkg;

        // parameterised constructor for View Holder class
        // which takes the view as a parameter
        public MyView(View view) {
            super(view);

            // initialise TextView with id
            tvBusinessName = (TextView) view
                    .findViewById(R.id.tvBusinessName);
            tvNatureOfBusiness = (TextView) view
                    .findViewById(R.id.tvNatureOfBusiness);
            ivBusinessBkg = (ImageView) view
                    .findViewById(R.id.ivBusinessBkg);
        }
    }

    // Constructor for adapter class
    // which takes a list of String type
    public AdapterBusinessMap(List<MBusiness> horizontalList, Context context) {
        ctx = context;
        this.list = horizontalList;
    }

    // Override onCreateViewHolder which deals
    // with the inflation of the card layout
    // as an item for the RecyclerView.
    @Override
    public MyView onCreateViewHolder(ViewGroup parent,
                                     int viewType) {

        // Inflate item.xml using LayoutInflator
        View itemView
                = LayoutInflater
                .from(parent.getContext())
                .inflate(R.layout.dialog_image_center,
                        parent,
                        false);

        // return itemView
        return new MyView(itemView);
    }

    // Override onBindViewHolder which deals
    // with the setting of different data
    // and methods related to clicks on
    // particular items of the RecyclerView.
    @Override
    public void onBindViewHolder(final MyView holder,
                                 @SuppressLint("RecyclerView") final int position) {

        // Set the text of each item of
        // Recycler view with the list items
        holder.tvBusinessName.setText(list.get(position).getBusinessName());
        holder.tvNatureOfBusiness.setText(list.get(position).getNatureOfBusiness());
        Tools.displayImageResized(ctx, holder.ivBusinessBkg, Constant.baseURL + "/files/images/" + list.get(position).getImageUrl());

        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (mOnItemClickListener != null) {
                    mOnItemClickListener.onItemClick(view, list.get(position), position);
                }
            }
        });
    }

    // Override getItemCount which Returns
    // the length of the RecyclerView.
    @Override
    public int getItemCount() {
        return list.size();
    }
}
