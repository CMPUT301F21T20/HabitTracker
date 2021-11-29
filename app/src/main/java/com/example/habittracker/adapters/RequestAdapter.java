package com.example.habittracker.adapters;

import android.content.Context;
import android.content.Intent;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.example.habittracker.R;
import com.example.habittracker.ViewHabitActivity;
import com.example.habittracker.controllers.SocialController;
import com.example.habittracker.models.Habit;
import com.example.habittracker.models.Request;
import com.example.habittracker.models.RequestMap;

public class RequestAdapter extends ArrayAdapter<Request> {
    private Context context;
    private RequestMap requestMap;

    public RequestAdapter(@NonNull Context context, RequestMap requestMap) throws Exception {
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
            Log.d("TESTINGREQUESTESds", request.getUserId());
            TextView username = view.findViewById(R.id.username);
            username.setText(request.getUserName());
            Button acceptRequestButton = view.findViewById(R.id.acceptRequestButton);
            Button refuseRequestButton = view.findViewById(R.id.refuseRequestButton);
            refuseRequestButton.setVisibility(View.VISIBLE);
            acceptRequestButton.setOnClickListener(v -> {
                try {
                    request.setStatus(request.accepted);
                    SocialController controller = SocialController.getInstance();
                    controller.saveRequest("incoming", request);
                } catch (Exception e) {
                    e.printStackTrace();
                }
                acceptRequestButton.setText("Accepted");
                refuseRequestButton.setVisibility(View.GONE);
            });

            refuseRequestButton.setOnClickListener(v -> {
                try {
                    request.setStatus(request.rejected);
                    SocialController controller = SocialController.getInstance();
                    controller.saveRequest("incoming", request);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            });
        } catch (Exception e) {
            e.printStackTrace();
        }

        return view;
    }
}
