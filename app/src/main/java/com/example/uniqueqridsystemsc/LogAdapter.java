package com.example.uniqueqridsystemsc;

import android.app.AlertDialog;
import androidx.core.text.HtmlCompat;
import android.content.Context;
import android.content.DialogInterface;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.Filter;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;

public class LogAdapter extends RecyclerView.Adapter<LogAdapter.LogViewHolder> {
    private List<LogItem> logItemList;
    private List<LogItem> logItemListFull;


    private SwipeActionListener swipeActionListener;
    private Context context;

    public LogAdapter(List<LogItem> logItemList, Context context, SwipeActionListener swipeActionListener) {
        this.logItemList = logItemList;
        this.context = context;
        this.swipeActionListener = swipeActionListener;
        this.logItemListFull = new ArrayList<>(logItemList);
    }


    @NonNull
    @Override
    public LogViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.log_item, parent, false);
        return new LogViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull LogViewHolder holder, int position) {
        LogItem logItem = logItemList.get(position);
        String firstName = logItem.getFirstName();
        String lastName = logItem.getLastName();
        String school = logItem.getSchool();
        String checkInTime = logItem.getCheckInTime();
        String uniqueID = logItem.getUniqueID();
        String boldFirstName = "<b>" + firstName + "</b>";
        String boldLastName = "<b>" + lastName + "</b>";
        String message = "Are you sure you want to time out " + boldFirstName + " " + boldLastName + "?";


        // Combine "Name:" with the full name and set it to the TextView
        String fullNameLabel = "Name: " + logItem.getFirstName() + " " + logItem.getLastName();
        holder.fullNameTextView.setText(fullNameLabel);

        String schoolLabel = "School: " + logItem.getSchool();
        holder.schoolTextView.setText(schoolLabel);

        // Combine "Timed In:" with the check-in time and set it to the TextView
        String checkInTimeLabel = "Timed In: " + logItem.getCheckInTime();
        holder.checkInTimeTextView.setText(checkInTimeLabel);

        // Implement swipe action when swiped to the right
        holder.itemView.setOnTouchListener(new View.OnTouchListener() {
            private float x1, x2;

            @Override
            public boolean onTouch(View v, android.view.MotionEvent event) {
                switch (event.getAction()) {
                    case android.view.MotionEvent.ACTION_DOWN:
                        x1 = event.getX();
                        break;
                    case android.view.MotionEvent.ACTION_UP:
                        x2 = event.getX();
                        float deltaX = x2 - x1;

                        // Adjust the swipe threshold as needed
                        if (deltaX > 100) {
                            // Right swipe detected, perform action
                            swipeActionListener.onSwipeRight(holder.getAdapterPosition());
                            return true; // Consume the event
                        }
                        break;
                }
                return false; // Let other touch events be handled
            }
        });

        // Add a click listener for the check-out button
        holder.checkOutButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Display a confirmation dialog using the provided context
                AlertDialog.Builder builder = new AlertDialog.Builder(context);
                builder.setTitle("Confirm Time Out");
                builder.setMessage(HtmlCompat.fromHtml(message, HtmlCompat.FROM_HTML_MODE_COMPACT));
                builder.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {

                        // Get the current date and time
                        SimpleDateFormat monthFormat = new SimpleDateFormat("MMMM", Locale.getDefault());
                        SimpleDateFormat dateFormat = new SimpleDateFormat("MMMM d, yyyy", Locale.getDefault());
                        SimpleDateFormat timeFormat = new SimpleDateFormat("hh:mm:ss a", Locale.getDefault());

                        // Get the current month, date, and time
                        String currentMonth = monthFormat.format(new Date());
                        String currentDate = dateFormat.format(new Date());
                        String currentTime = timeFormat.format(new Date());

                        // Create a reference to the Firebase database
                        DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference();
                        DatabaseReference logReference = databaseReference.child("SC_Time_Log");

                        // Create a reference to the current date
                        DatabaseReference dateReference = logReference.child(currentMonth).child(currentDate);

                        // Update the data under the unique ID with the checkout time and other attributes
                        DatabaseReference studentReference = dateReference.child(uniqueID);
                        studentReference.child("checkOutTime").setValue(currentTime);
                        studentReference.child("firstName").setValue(firstName);
                        studentReference.child("lastName").setValue(lastName);
                        studentReference.child("school").setValue(school);
                        studentReference.child("checkInTime").setValue(checkInTime);
                        removeItem(position);

                        ///////////////////////////////////////////////////////////////////////////

                        // Remove the item from SC_Log the Firebase database
                        DatabaseReference databaseReference_scLog = FirebaseDatabase.getInstance()
                                .getReference()
                                .child("SC_Log")
                                .child(currentMonth)
                                .child(currentDate)
                                .child(uniqueID);

                        databaseReference_scLog.removeValue().addOnCompleteListener(new OnCompleteListener<Void>() {
                            @Override
                            public void onComplete(@NonNull Task<Void> task) {
                                if (task.isSuccessful()) {
                                    // Item and its children removed from the database
                                } else {
                                    // Handle the error if the item was not removed
                                }
                            }
                        });
                    }

                });
                builder.setNegativeButton("No", null);
                builder.show();
            }
        });

    }

    @Override
    public int getItemCount() {
        return logItemList.size();
    }

    public Filter getFilter() {
        return logFilter;
    }
    public void removeItem(int position) {
        if (position >= 0 && position < logItemList.size()) {
            logItemList.remove(position);
            notifyItemRemoved(position);
        }
    }

    public void addAll(List<LogItem> logItems) {
        logItemList.addAll(0, logItems); // Add items at the beginning of the list
        notifyDataSetChanged();
    }



    private Filter logFilter = new Filter() {
        @Override
        protected FilterResults performFiltering(CharSequence constraint) {
            List<LogItem> filteredList = new ArrayList<>();
            if (constraint == null || constraint.length() == 0) {
                filteredList.addAll(logItemListFull);
            } else {
                String filterPattern = constraint.toString().toLowerCase().trim();

                for (LogItem item : logItemListFull) {
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
            logItemList.clear();
            logItemList.addAll((List<LogItem>) results.values);
            notifyDataSetChanged();
        }
    };

    public void clear() {
        logItemList.clear();
        logItemListFull.clear();
        notifyDataSetChanged();
    }

    public void add(LogItem logItem) {
        logItemList.add(logItem);
        logItemListFull.add(logItem);
        notifyDataSetChanged();
    }
    

    public interface SwipeActionListener {
        void onSwipeRight(int position);

        // You can add a method for check-out action here if needed
        // void onCheckOut(int position);
    }

    public static class LogViewHolder extends RecyclerView.ViewHolder {
        TextView fullNameTextView, schoolTextView, checkInTimeTextView;
        Button checkOutButton;

        public LogViewHolder(@NonNull View itemView) {
            super(itemView);

            // Initialize TextViews and other views from the log_item.xml layout
            fullNameTextView = itemView.findViewById(R.id.fullNameTextView);
            schoolTextView = itemView.findViewById(R.id.schoolTextView);
            checkInTimeTextView = itemView.findViewById(R.id.checkInTimeTextView);
            checkOutButton = itemView.findViewById(R.id.checkOutButton);
        }
    }
}
