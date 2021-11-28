package com.example.habittracker.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.example.habittracker.R;
import com.example.habittracker.controllers.SocialController;
import com.example.habittracker.models.Request;
import com.example.habittracker.models.RequestMap;

public class FollowingAdapter extends ArrayAdapter<Request> {
    private Context context;
    private RequestMap requestMap;

    public FollowingAdapter(@NonNull Context context, RequestMap requestMap) throws Exception {
        super(context, 0, requestMap.getRequestList("incoming"));
        this.context = context;
        this.requestMap = requestMap;
    }

    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
        View view = convertView;

        if (view == null) {
            view = LayoutInflater.from(context).inflate(R.layout.content_request_list, parent, false);
        }

        try {
            Request request = requestMap.getRequest("incoming", position);
            TextView username = view.findViewById(R.id.username);
            username.setText(request.getUserName());
            Button acceptRequestButton = view.findViewById(R.id.acceptRequestButton);
            Button refuseRequestButton = view.findViewById(R.id.refuseRequestButton);
            acceptRequestButton.setOnClickListener(v -> {
                try {
                    request.setStatus(request.accepted);
                    SocialController controller = SocialController.getInstance();
                    controller.saveRequest("incoming", request);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            });

            refuseRequestButton.setOnClickListener(v -> {
                try {
                    SocialController controller = SocialController.getInstance();
                    controller.deleteRequest("incoming", request);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            });
        } catch (Exception e) {
            e.printStackTrace();
        }

        return view;
    }

    @Override
    public int getCount(){
        return super.getCount();
    }
}
