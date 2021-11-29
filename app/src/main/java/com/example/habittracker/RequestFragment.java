package com.example.habittracker;

import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import androidx.fragment.app.Fragment;
import com.example.habittracker.adapters.RequestAdapter;
import com.example.habittracker.controllers.SocialController;
import com.example.habittracker.models.Request;
import com.example.habittracker.models.RequestMap;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.FieldPath;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.ArrayList;

public class RequestFragment extends Fragment {

    private ListView RequestListView;
    private RequestMap requestMap;
    private ArrayAdapter<Request> requestListAdapter;
    private FirebaseFirestore db;
    private Context thisContext;

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
            } catch (Exception exception) {
                exception.printStackTrace();
            }
            requestListAdapter.notifyDataSetChanged();
        });
        return root;
    }
}
