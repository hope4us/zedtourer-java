package com.appbuildersworld.zedtourerjava.fragment;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;

import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.appbuildersworld.zedtourerjava.ProductDetailsActivity;
import com.appbuildersworld.zedtourerjava.R;
import com.appbuildersworld.zedtourerjava.activities.customer.ProductDetailsCustomerActivity;
import com.appbuildersworld.zedtourerjava.adapter.AdapterGridDrinkCard;
import com.appbuildersworld.zedtourerjava.data.DataGenerator;
import com.appbuildersworld.zedtourerjava.models.MProductDrinkNonAlc;
import com.appbuildersworld.zedtourerjava.models.MProductDrinkNonAlc;
import com.appbuildersworld.zedtourerjava.utils.Tools;
import com.appbuildersworld.zedtourerjava.widget.SpacingItemDecoration;
import com.google.android.material.snackbar.Snackbar;

import java.util.Collections;
import java.util.List;

public class FragmentDrinkGrid extends Fragment {

    public static List<MProductDrinkNonAlc> items;
    public FragmentDrinkGrid() {
    }

    public static FragmentDrinkGrid newInstance(List<MProductDrinkNonAlc> pList) {
        FragmentDrinkGrid fragment = new FragmentDrinkGrid();
        items = pList;
        return fragment;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        final View root = inflater.inflate(R.layout.fragment_product_grid, container, false);


        RecyclerView recyclerView = (RecyclerView) root.findViewById(R.id.recyclerView);
        recyclerView.setLayoutManager(new GridLayoutManager(getActivity(), 2));
        recyclerView.addItemDecoration(new SpacingItemDecoration(2, Tools.dpToPx(getActivity(), 8), true));
        recyclerView.setHasFixedSize(true);

         
        Collections.shuffle(items);

        //set data and list adapter
        AdapterGridDrinkCard mAdapter = new AdapterGridDrinkCard(getActivity(), items);
        recyclerView.setAdapter(mAdapter);

        // on item list clicked
        mAdapter.setOnItemClickListener(new AdapterGridDrinkCard.OnItemClickListener() {
            @Override
            public void onItemClick(View view, MProductDrinkNonAlc obj, int position) {
                Intent i = new Intent(getActivity(), ProductDetailsCustomerActivity.class);
                i.putExtra("productName", obj.getProductName());
                i.putExtra("category", obj.getCategoryName());
                i.putExtra("imageUrl", obj.getImageUrl());
                i.putExtra("description", obj.getDescription());
                i.putExtra("price", obj.getPrice());
                i.putExtra("productId", obj.getProductId());
                i.putExtra("categoryId", obj.getProductCategoryId());
                startActivity(i);
            }
        });

        mAdapter.setOnMoreButtonClickListener(new AdapterGridDrinkCard.OnMoreButtonClickListener() {
            @Override
            public void onItemClick(View view, MProductDrinkNonAlc obj, MenuItem item) {
                Snackbar.make(root, obj.getProductName() + " (" + item.getTitle() + ") clicked", Snackbar.LENGTH_SHORT).show();
            }
        });

        return root;
    }
}