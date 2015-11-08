package com.wallet.utilis;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.List;

public class MenuViewAdapter extends RecyclerView.Adapter<MenuViewAdapter.ViewHolder> {
    private List<MenuItem> menuItems;
    private QuantityChangedListener mListener;

    public void setListener(QuantityChangedListener listener){
        mListener = listener;
    }
    interface QuantityChangedListener {
        void onItemsChanged(List<MenuItem> items);
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        ImageView   imageView;
        TextView    textView;
        TextView    costView;
        int         position;
        TextView    Quantity;
        Button      Positive;
        Button      Negative;
        View        mView;

        public ViewHolder(View view) {
            super(view);
            mView = view;
            imageView = (ImageView) view.findViewById(R.id.menu_image);
            textView = (TextView) view.findViewById(R.id.menu_text);
            costView = (TextView) view.findViewById(R.id.menu_cost);
            Quantity = (TextView) view.findViewById(R.id.menu_quantity);
            Negative = (Button) view.findViewById(R.id.Negative);
            Positive = (Button) view.findViewById(R.id.Positive);
        }
    }

    public MenuViewAdapter(List<MenuItem> dataSource) {
        this.menuItems = dataSource;
    }

    // Create new views (invoked by the layout manager)
    @Override
    public MenuViewAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        // create a new view
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.menu_item, parent, false);
        ViewHolder vh = new ViewHolder(v);
        return vh;
    }

    // Replace the contents of a view (invoked by the layout manager)
    @Override
    public void onBindViewHolder(final ViewHolder holder, int position) {
        // - get element from your dataset at this position
        // - replace the contents of the view with that element
        final MenuItem item = menuItems.get(position);
        holder.imageView.setImageResource(item.imageResource);
        holder.textView.setText(item.title);
        holder.costView.setText(item.cost);
        holder.Quantity.setText(String.valueOf(item.quantity));
        holder.Negative.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(item.quantity>0)
                {
                    item.quantity--;
                    holder.Quantity.setText(String.valueOf(item.quantity));
                    if(mListener!=null)
                        mListener.onItemsChanged(menuItems);
                }
            }

        });
        holder.Positive.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                item.quantity++;
                holder.Quantity.setText(String.valueOf(item.quantity));
                if(mListener!=null)
                    mListener.onItemsChanged(menuItems);
            }
        });
    }

    // Return the size of your dataset (invoked by the layout manager)
    @Override
    public int getItemCount() {
        return menuItems.size();
    }

    /*@Override
    public View getView(int position, View convertView, ViewGroup parent) {
        // Get the data item for this position
        MenuItem menuItem = getItem(position);

        // Check if an existing view is being reused, otherwise inflate the view
        ViewHolder viewHolder;

        if (convertView == null) {
            viewHolder = new ViewHolder();
            LayoutInflater inflater = LayoutInflater.from(getContext());
            convertView = inflater.inflate(R.layout.menu_item, parent, false);
            viewHolder.imageView = (ImageView) convertView.findViewById(R.id.menu_image);
            viewHolder.textView = (TextView) convertView.findViewById(R.id.menu_text);
            convertView.setTag(viewHolder);
        } else {
            viewHolder = (ViewHolder) convertView.getTag();
        }

        viewHolder.position = position;

        viewHolder.textView.setText(menuItem.title);
        viewHolder.imageView.setImageResource(menuItem.imageResource);

        // Populate the imageview with async so that it doesn't bog down UI thread.
        //new AppIconLoader().execute(viewHolder, index, packageName, position);

        *//*} else if (!iconRequested.containsKey(packageName)) {
            iconRequested.put(packageName, true);
            // Populate the imageview with async so that it doesn't bog down UI thread.
            new AppIconLoader().execute(viewHolder, index, packageName, position);
        }*//*

        return convertView;
    }*/


}