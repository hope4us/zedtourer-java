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

import java.util.ArrayList;
import java.util.List;

public class AdapterOrderList extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    private final int VIEW_ITEM = 1;
    private final int VIEW_SECTION = 0;

    private List<MBusinessOrder> items = new ArrayList<>();
    private Context ctx;
    private OnItemClickListener mOnItemClickListener;

    public interface OnItemClickListener {
        void onItemClick(View view, MBusinessOrder obj, int position);
    }

    public void setOnItemClickListener(final OnItemClickListener mItemClickListener) {
        this.mOnItemClickListener = mItemClickListener;
    }

    public AdapterOrderList(Context context, List<MBusinessOrder> items) {
        this.items = items;
        ctx = context;

    }

    public class OriginalViewHolder extends RecyclerView.ViewHolder {
        public ImageView image;
        public TextView name;
        public TextView description;
        public View lyt_parent;

        public OriginalViewHolder(View v) {
            super(v);
            image = (ImageView) v.findViewById(R.id.image);
            name = (TextView) v.findViewById(R.id.name);
            description = (TextView) v.findViewById(R.id.description);
            lyt_parent = (View) v.findViewById(R.id.lyt_parent);
        }
    }

    public static class SectionViewHolder extends RecyclerView.ViewHolder {
        public TextView title_section;

        public SectionViewHolder(View v) {
            super(v);
            title_section = (TextView) v.findViewById(R.id.title_section);
        }
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        RecyclerView.ViewHolder vh;
        /*if (viewType == VIEW_ITEM) {

        } else {
            View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_section, parent, false);
            vh = new SectionViewHolder(v);
        }*/
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_people_chat, parent, false);
        vh = new OriginalViewHolder(v);
        return vh;
    }

    // Replace the contents of a view (invoked by the layout manager)
    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, @SuppressLint("RecyclerView") final int position) {
        MBusinessOrder p = items.get(position);
        if (holder instanceof OriginalViewHolder) {
            OriginalViewHolder view = (OriginalViewHolder) holder;

            view.name.setText("" + p.getCustomerId());
            view.description.setText(p.getStatus());
           // imageRequester.setImageFromUrl(view.image, Constant.baseURL + "/files/images/" + p.getImageUrl());
            Tools.displayImageRoundUrl(ctx, view.image, Constant.baseURL + "/files/images/picture.png");

            view.lyt_parent.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    if (mOnItemClickListener != null) {
                        mOnItemClickListener.onItemClick(view, items.get(position), position);
                    }
                }
            });
        } else {
            SectionViewHolder view = (SectionViewHolder) holder;
            view.title_section.setText(p.getCustomerId());
        }
    }

    @Override
    public int getItemCount() {
        return items.size();
    }


}