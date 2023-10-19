package com.example.uniqueqridsystemsc;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.Filter;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.core.text.HtmlCompat;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.ArrayList;
import java.util.List;

public class CheckAdapter extends RecyclerView.Adapter<CheckAdapter.LogViewHolder> {
    private List<CheckItem> checkItemList;
    private List<CheckItem> checkItemListFull;


    private SwipeActionListener swipeActionListener;
    private Context context;

    public void setFilteredData(List<CheckItem> filteredData) {
        checkItemList.clear();
        checkItemList.addAll(filteredData);
        notifyDataSetChanged();
    }

    public CheckAdapter(List<CheckItem> checkItemList, Context context, SwipeActionListener swipeActionListener) {
        this.checkItemList = checkItemList;
        this.context = context;
        this.swipeActionListener = swipeActionListener;
        this.checkItemListFull = new ArrayList<>(checkItemList);
    }

    @NonNull
    @Override
    public LogViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.checkinout_item, parent, false);
        return new LogViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull LogViewHolder holder, int position) {
        CheckItem checkItem = checkItemList.get(position);

        // Set the data to TextViews
        holder.fullNameTextView.setText("Full Name: " + checkItem.getFirstName() + " " + checkItem.getLastName());
        holder.schoolTextView.setText("School: " + checkItem.getSchool());
        holder.timeInTextView.setText("Timed In: " + checkItem.getTimedIn());
        holder.timeOutTextView.setText("Timed Out: " + checkItem.getTimedOut());

        // Update the dateTextView based on the date from CheckItem
        holder.dateTextView.setText("Date: " + checkItem.getDate());

        // Handle swipe action
        handleSwipeAction(holder, position);
    }


    @Override
    public int getItemCount() {
        return checkItemList.size();
    }

    public Filter getFilter() {
        return checkFilter;
    }

    public void removeItem(int position) {
        if (position >= 0 && position < checkItemList.size()) {
            checkItemList.remove(position);
            notifyItemRemoved(position);
        }
    }

    private void handleSwipeAction(LogViewHolder holder, int position) {
        holder.itemView.setOnTouchListener(new View.OnTouchListener() {
            float startX, endX;

            @Override
            public boolean onTouch(View v, android.view.MotionEvent event) {
                switch (event.getAction()) {
                    case android.view.MotionEvent.ACTION_DOWN:
                        startX = event.getX();
                        break;
                    case android.view.MotionEvent.ACTION_UP:
                        endX = event.getX();
                        float deltaX = endX - startX;

                        // Adjust the swipe threshold as needed
                        if (deltaX > 100) {
                            // Right swipe detected, perform action
                            swipeActionListener.onSwipeRight(position);
                            return true; // Consume the event
                        }
                        break;
                }
                return false;
            }
        });
    }

    // Filter and data manipulation methods
    public Filter checkFilter = new Filter() {
        @Override
        protected FilterResults performFiltering(CharSequence constraint) {
            List<CheckItem> filteredList = new ArrayList<>();
            if (constraint == null || constraint.length() == 0) {
                filteredList.addAll(checkItemList);
            } else {
                String filterPattern = constraint.toString().toLowerCase().trim();

                for (CheckItem item : checkItemListFull) {
                    // Modify the filter conditions as needed (e.g., for firstName, lastName, school)
                    if (item.getFirstName().toLowerCase().contains(filterPattern) ||
                            item.getLastName().toLowerCase().contains(filterPattern) ||
                            item.getSchool().toLowerCase().contains(filterPattern)) {
                        filteredList.add(item);
                    }
                }
            }

            FilterResults results = new FilterResults();
            results.values = filteredList;

            return results;
        }

        @Override
        protected void publishResults(CharSequence constraint, FilterResults results) {
            checkItemList.clear();
            checkItemList.addAll((List<CheckItem>) results.values);
            notifyDataSetChanged();
        }
    };

    public void clear() {
        checkItemList.clear();
        checkItemListFull.clear();
        notifyDataSetChanged();
    }

    public void add(CheckItem checkItem) {
        checkItemList.add(checkItem);
        checkItemListFull.add(checkItem);
        notifyDataSetChanged();
    }

    public void setData(List<CheckItem> items) {
        checkItemList.clear();
        checkItemList.addAll(items);
        notifyDataSetChanged();
    }

    public interface SwipeActionListener {
        void onSwipeRight(int position);

        // You can add a method for check-out action here if needed
        // void onCheckOut(int position);
    }

    public static class LogViewHolder extends RecyclerView.ViewHolder {
        TextView fullNameTextView, schoolTextView, dateTextView, timeInTextView, timeOutTextView;

        public LogViewHolder(@NonNull View itemView) {
            super(itemView);

            fullNameTextView = itemView.findViewById(R.id.fullNameTextView);
            schoolTextView = itemView.findViewById(R.id.schoolTextView);
            timeInTextView = itemView.findViewById(R.id.timeInTextView);
            timeOutTextView = itemView.findViewById(R.id.timeOutTextView);
            dateTextView = itemView.findViewById(R.id.dateTextView);
        }
    }
}
