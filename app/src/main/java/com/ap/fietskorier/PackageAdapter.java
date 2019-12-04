package com.ap.fietskorier;

import android.content.Context;
import android.icu.text.Transliterator;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.LinkedList;

public class PackageAdapter extends RecyclerView.Adapter<PackageAdapter.PackageViewHolder> {
    public interface OnItemClickListener {
        void onItemClick(Package myPackage);
    }

    private final LinkedList<Package> myPackageList;
    private LayoutInflater myInflator;
    private final OnItemClickListener myListener;

    public PackageAdapter(Context context, LinkedList<Package> packageList, OnItemClickListener listener) {
        myInflator = LayoutInflater.from(context);
        myPackageList = packageList;
        myListener = listener;
    }

    @NonNull
    @Override
    public PackageViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View myItemView = myInflator.inflate(R.layout.packagelist_item,parent,false);
        return new PackageViewHolder(myItemView,this);
    }

    @Override
    public void onBindViewHolder(@NonNull PackageViewHolder holder, int position) {
        Package myCurrent = myPackageList.get(position);
        holder.packageID.setText(myCurrent.getPackageID());
        holder.packageAddress1.setText(myCurrent.getAddress1());
        holder.packageAddress2.setText(myCurrent.getAddress2());
        holder.bind(myPackageList.get(position),myListener);
    }

    @Override
    public int getItemCount() {
        return myPackageList.size();
    }

    class PackageViewHolder extends RecyclerView.ViewHolder {
        public final TextView packageID;
        public final TextView packageAddress1;
        public final TextView packageAddress2;
        final PackageAdapter myAdapter;

        public PackageViewHolder(View itemView, PackageAdapter adapter) {
            super(itemView);
            packageID = itemView.findViewById(R.id.packageID);
            packageAddress1 = itemView.findViewById(R.id.packageAddress1);
            packageAddress2 = itemView.findViewById(R.id.packageAddress2);
            this.myAdapter = adapter;
        }

        public void bind(final Package myPackage, final OnItemClickListener myListener) {
            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    myListener.onItemClick(myPackage);
                }
            });
        }
    }



    /*private final LinkedList<String> myDataset;
    private LayoutInflater myInflater;

    // referentie naar de data (hier slechts 1 string - kan nog toegevoegd worden
    public static class MyViewHolder extends RecyclerView.ViewHolder {
        //data is just a string
        public TextView textView;
        public MyViewHolder (TextView v) {
            super(v);
            textView = v;
        }
    }
    /*
    // constructor voor de dataset (afhankelijk van type)
    public MyAdapter(Context context, LinkedList<String> myDataset) {
        myInflater = LayoutInflater.from(context);
        this.myDataset = myDataset;
    }

    // new views
    @Override
    public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        //create
        View myItemView = myInflater.inflate(R.layout.packagelist_item, parent, false);

        return new MyViewHolder<>
    }

    //replace the contents of a view
    @Override
    public void onBindViewHolder(MyViewHolder holder, int position) {
        // get element from your dataset at this position
        // replace the contentq of the view with that element
        holder.textView.setText(myDataset[position]);
    }

    //return the size of your dataset
    @Override
    public int getItemCount() {
        return myDataset.size();
    }*/

}
