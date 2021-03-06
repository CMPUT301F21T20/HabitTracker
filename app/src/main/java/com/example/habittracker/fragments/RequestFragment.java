package com.example.habittracker.fragments;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;

import androidx.fragment.app.Fragment;

import com.example.habittracker.R;
import com.example.habittracker.adapters.RequestAdapter;
import com.example.habittracker.controllers.SocialController;
import com.example.habittracker.models.Request.Request;
import com.example.habittracker.models.Request.RequestMap;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.FirebaseFirestore;

public class RequestFragment extends Fragment {

    private ListView RequestListView;
    private TextView NoRequest_textView;
    private RequestMap requestMap;
    private ArrayAdapter<Request> requestListAdapter;

    public RequestFragment() {}

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        FirebaseAuth fAuth;
        fAuth = FirebaseAuth.getInstance();
        FirebaseUser fUser = fAuth.getCurrentUser();
        String uid = fUser.getUid();
        FirebaseFirestore db = FirebaseFirestore.getInstance();

        View root = inflater.inflate(R.layout.fragment_request, container, false);

        RequestListView = root.findViewById(R.id.request_listview);
        NoRequest_textView = root.findViewById(R.id.NoRequest_textView);

        requestMap = new RequestMap();
        try {
            requestListAdapter = new RequestAdapter(requireContext(), requestMap);
        } catch (Exception e) {
            e.printStackTrace();
        }
        RequestListView.setAdapter(requestListAdapter);

        RequestListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> arg0, View view, int position, long id) {
            }
        });

        db.collection("Requests").document(uid).addSnapshotListener((docSnapshot, e) -> {
            SocialController.convertToRequestMap(docSnapshot, requestMap);
            try {
                for (int counter = 0; counter < requestMap.getRequestList("incoming").size(); counter++) {
                    if (!requestMap.getRequest("incoming", counter).getStatus().trim().equals("Pending")){
                       requestMap.deleteRequest("incoming", requestMap.getRequest("incoming", counter));
                    }
                }

                for (int counter = 0; counter < requestMap.getRequestList("incoming").size(); counter++) {
                    if (!requestMap.getRequest("incoming", counter).getStatus().trim().equals("Pending")){
                        requestMap.deleteRequest("incoming", requestMap.getRequest("incoming", counter));
                    }
                }
                Log.d(">>>>>>>>>>>>>>>>>>>>>>>>>", requestMap.getRequest("incoming", 0).getStatus());
                NoRequest_textView.setVisibility(View.GONE);
            } catch (Exception exception) {
                exception.printStackTrace();
                NoRequest_textView.setVisibility(View.VISIBLE);
            }
            requestListAdapter.notifyDataSetChanged();
        });
        return root;
    }
}
