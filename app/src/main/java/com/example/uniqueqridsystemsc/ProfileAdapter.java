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

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;

public class ProfileAdapter extends RecyclerView.Adapter<ProfileAdapter.LogViewHolder> {
    private List<ProfileItem> profileItemList;
    private List<ProfileItem> profileItemListFull;


    private SwipeActionListener swipeActionListener;
    private Context context;

    public ProfileAdapter(List<ProfileItem> profileItemList, Context context, SwipeActionListener swipeActionListener) {
        this.profileItemList = profileItemList;
        this.context = context;
        this.swipeActionListener = swipeActionListener;
        this.profileItemListFull = new ArrayList<>(profileItemList); // Initialize profileItemListFull
    }


    @NonNull
    @Override
    public LogViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.profile_item, parent, false);
        return new LogViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull LogViewHolder holder, int position) {
        ProfileItem profileItem = profileItemList.get(position);
        String uniqueID= profileItem.getUniqueID();
        String firstName = profileItem.getFirstName();
        String lastName = profileItem.getLastName();
        String middleName = profileItem.getMiddleName();
        String school = profileItem.getSchool();
        String personalEmail = profileItem.getPersonalEmail();
        String phoneNumber = profileItem.getPhoneNumber();
        String registerDate = profileItem.getRegisterDate();

        String boldFirstName = "<b>" + firstName + "</b>";
        String boldLastName = "<b>" + lastName + "</b>";
        String message = "Are you sure you want to delete student: " + boldFirstName + " " + boldLastName + "?";

        // Combine "Name:" with the full name and set it to the TextView
        String fullNameLabel = "Full Name: " + profileItem.getFirstName() + " " + profileItem.getLastName();
        holder.fullNameTextView.setText(fullNameLabel);

        String schoolLabel = "School: " + profileItem.getSchool();
        holder.schoolTextView.setText(schoolLabel);

        String personalEmailLabel = "Email: " + profileItem.getPersonalEmail();
        holder.personalEmailTextView.setText(personalEmailLabel);

        String phoneNumberLabel = "Contact Number: " + profileItem.getPhoneNumber();
        holder.phoneNumberTextView.setText(phoneNumberLabel);

        String registerLabel = "Register Date: " + profileItem.getRegisterDate();
        holder.registerDateTextView.setText(registerLabel);

        holder.phoneNumberTextView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String phoneNumber = profileItem.getPhoneNumber();

                // Create an Intent to display the contact options
                Intent intent = new Intent(Intent.ACTION_VIEW);
                Uri data = Uri.parse("tel:" + phoneNumber);
                intent.setData(data);

                // Start the Intent to open the phone options (call, message)
                context.startActivity(intent);
            }
        });

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
        holder.deleteStudentBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Display a confirmation dialog using the provided context
                AlertDialog.Builder builder = new AlertDialog.Builder(context);
                builder.setTitle("Confirm Delete Student?");
                builder.setMessage(HtmlCompat.fromHtml(message, HtmlCompat.FROM_HTML_MODE_COMPACT));
                builder.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {

                        // Remove the item from SC_Log the Firebase database
                        DatabaseReference databaseReference_scLog = FirebaseDatabase.getInstance()
                                .getReference()
                                .child("SC_Permanent")
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
        return profileItemList.size();
    }

    public Filter getFilter() {
        return profileFilter;
    }
    public void removeItem(int position) {
        if (position >= 0 && position < profileItemList.size()) {
            profileItemList.remove(position);
            notifyItemRemoved(position);
        }
    }


    private Filter profileFilter = new Filter() {
        @Override
        protected FilterResults performFiltering(CharSequence constraint) {
            List<ProfileItem> filteredList = new ArrayList<>();
            if (constraint == null || constraint.length() == 0) {
                filteredList.addAll(profileItemList);
            } else {
                String filterPattern = constraint.toString().toLowerCase().trim();

                for (ProfileItem item : profileItemListFull) {
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
            profileItemList.clear();
            profileItemList.addAll((List<ProfileItem>) results.values);
            notifyDataSetChanged();
        }
    };

    public void clear() {
        profileItemList.clear();
        profileItemListFull.clear();
        notifyDataSetChanged();
    }

    public void add(ProfileItem profileItem) {
        profileItemList.add(profileItem);
        profileItemListFull.add(profileItem);
        notifyDataSetChanged();
    }
    

    public interface SwipeActionListener {
        void onSwipeRight(int position);

        // You can add a method for check-out action here if needed
        // void onCheckOut(int position);
    }

    public static class LogViewHolder extends RecyclerView.ViewHolder {
        TextView uniqueIDTextView, fullNameTextView, middleNameTextView, schoolTextView, personalEmailTextView, phoneNumberTextView, registerDateTextView;
        Button deleteStudentBtn;

        public LogViewHolder(@NonNull View itemView) {
            super(itemView);

            // Initialize TextViews and other views from the log_item.xml layout
            fullNameTextView = itemView.findViewById(R.id.fullNameTextView);
            schoolTextView = itemView.findViewById(R.id.schoolTextView);
            personalEmailTextView = itemView.findViewById(R.id.personalEmailTextView);
            phoneNumberTextView = itemView.findViewById(R.id.phoneNumberTextView);
            registerDateTextView = itemView.findViewById(R.id.registerDateTextView);
            deleteStudentBtn = itemView.findViewById(R.id.deleteStudentBtn);
        }
    }
}
