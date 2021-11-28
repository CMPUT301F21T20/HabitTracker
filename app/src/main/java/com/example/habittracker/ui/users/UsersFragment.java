package com.example.habittracker.ui.users;

import static android.content.ContentValues.TAG;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;

import com.example.habittracker.R;
import com.example.habittracker.UserProfileActivity;
import com.example.habittracker.adapters.UsersListAdapter;
import com.example.habittracker.models.User;
import com.example.habittracker.models.UsersList;
import com.example.habittracker.controllers.UsersListController;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;


public class UsersFragment extends Fragment {

    private ListView UsersListView;
    private UsersList usersList;
    private ArrayAdapter<User> userListAdapter;
    private FirebaseFirestore db;
    private Context thisContext;

    public UsersFragment() {}

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
        FirebaseFirestore db = FirebaseFirestore.getInstance();

        ((AppCompatActivity)getActivity()).getSupportActionBar().setDisplayHomeAsUpEnabled(false);

        View root = inflater.inflate(R.layout.fragment_users, container, false);

        UsersListView = root.findViewById(R.id.users_listview);

        usersList = new UsersList();
        userListAdapter = new UsersListAdapter(requireContext(), usersList);
        UsersListView.setAdapter(userListAdapter);

        UsersListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> arg0, View view, int position, long id) {

                User user = (User) usersList.getUser(position);
                openUserProfileActivity(user);

            }
        });

        db.collection("Users")
                .get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if (task.isSuccessful()) {
                            usersList.clearUserList();

                            for (QueryDocumentSnapshot document : task.getResult()) {
                                UsersListController.convertToUserList(document, usersList);
                                Log.d(TAG, document.getId() + " => " + document.getData());
                            }

                            userListAdapter.notifyDataSetChanged();
                        } else {
                            Log.d(TAG, "Error getting documents: ", task.getException());
                        }
                    }
                });

        return root;
    }

    /**
     * This function will handle opening the user profile activity
     */
    public void openUserProfileActivity(User user) {
        Intent intent = new Intent(requireContext(), UserProfileActivity.class);
        intent.putExtra("User", user);
        startActivity(intent);
    }
}
