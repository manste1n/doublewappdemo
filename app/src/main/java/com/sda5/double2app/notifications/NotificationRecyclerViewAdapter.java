package com.sda5.double2app.notifications;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.sda5.double2app.R;
import com.sda5.double2app.models.Notification;

import java.util.List;

import de.hdodenhof.circleimageview.CircleImageView;

public class NotificationRecyclerViewAdapter extends RecyclerView.Adapter<NotificationRecyclerViewAdapter.ViewHolder> {

    private List<Notification> notificationList;

    private Context context;

    //FirebaseFirestore databaseReference = FirebaseFirestore.getInstance();

    //FirebaseAuth mAuth;


    public NotificationRecyclerViewAdapter(List<Notification> notificationList, Context context) {

        this.notificationList = notificationList;
        this.context = context;
    }


    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.activity_notification_item, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull final NotificationRecyclerViewAdapter.ViewHolder holder, final int position) {

        holder.Message.setText(notificationList.get(position).getMessage());
        holder.Name.setText(notificationList.get(position).getFrom());
        holder.Group.setText(notificationList.get(position).getGroup());
    }


    @Override
    public int getItemCount() {
        return notificationList.size();
    }


    public class ViewHolder extends RecyclerView.ViewHolder {

        private View view;
        private CircleImageView circleImageView;
        private TextView Name;
        private TextView Message;
        private ProgressBar progressBar;
        private TextView Group;

        public ViewHolder(View itemView) {
            super(itemView);
            view = itemView;

            Name = (TextView) view.findViewById(R.id.listview_name);
            Message = (TextView) view.findViewById(R.id.listview_message);
            Group = (TextView) view.findViewById(R.id.listview_group);

        }
    }
}
