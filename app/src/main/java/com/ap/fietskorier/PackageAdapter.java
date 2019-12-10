package com.ap.fietskorier;

import android.content.Context;
import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.icu.text.Transliterator;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
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
        holder.packageDestinationAddress.setText(myCurrent.getDeliveryAddress());
        holder.packageDestinationEmail.setText(myCurrent.getEmailDestination());
        holder.packageStatus.setImageResource(myCurrent.getStatus());
        holder.bind(myPackageList.get(position),myListener);
    }

    @Override
    public int getItemCount() {
        return myPackageList.size();
    }

    class PackageViewHolder extends RecyclerView.ViewHolder {
        public final TextView packageID;
        public final TextView packageDestinationAddress;
        public final TextView packageDestinationEmail;
        public final ImageView packageStatus;
        final PackageAdapter myAdapter;

        public PackageViewHolder(View itemView, PackageAdapter adapter) {
            super(itemView);
            packageID = itemView.findViewById(R.id.packageID);
            packageDestinationAddress = itemView.findViewById(R.id.packageDestinationAddress);
            packageDestinationEmail = itemView.findViewById(R.id.packageDestinationEmail);
            packageStatus = itemView.findViewById(R.id.status);
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
}
