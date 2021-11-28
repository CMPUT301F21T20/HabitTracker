package com.example.habittracker.ui.users;

import static android.content.ContentValues.TAG;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;

import com.example.habittracker.LoginActivity;
import com.example.habittracker.R;
import com.example.habittracker.adapters.HabitListAdapter;
import com.example.habittracker.adapters.UsersListAdapter;
import com.example.habittracker.models.Habit;
import com.example.habittracker.models.HabitList;
import com.example.habittracker.models.User;
import com.example.habittracker.models.UsersList;
import com.example.habittracker.controllers.HabitListController;
import com.example.habittracker.controllers.UsersListController;
import com.example.habittracker.ui.profile.ProfileViewModel;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;


public class UsersFragment extends Fragment {

    private ListView UsersListView;
    private UsersList usersList;
    private ArrayAdapter<User> userListAdapter;
    private FirebaseFirestore db;
    private Context thisContext;
    EditText searchBar;
    Button searchButton;

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

        searchBar = root.findViewById(R.id.searchBar);
        searchButton = root.findViewById(R.id.searchButton);
        UsersListView = root.findViewById(R.id.users_listview);

        usersList = new UsersList();
        userListAdapter = new UsersListAdapter(requireContext(), usersList);
        UsersListView.setAdapter(userListAdapter);

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
        searchButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String userName = searchBar.getText().toString().trim();
                UsersList updatedUserList = new UsersList();

                if(TextUtils.isEmpty(userName)){
                    searchBar.setError("Username is required");
                    return;
                }
                System.out.println(usersList.getCount());
                for (int i =0; i < usersList.getCount(); i++) {
                    System.out.println(i);
                    System.out.println(usersList.getUser(i).getUsername());
                    if (userName.equals(usersList.getUser(i).getUsername())) {
                        //usersList.clearUserList();
                        System.out.println("I am about to update updateUserLIst");
                        updatedUserList.addUser(usersList.getUser(i));
                        break;
                    }

                }

                if (updatedUserList.getCount() > 0){
                usersList.clearUserList();
                usersList.addUser(updatedUserList.getUser(0));
                userListAdapter.notifyDataSetChanged();
                }
                else {
                    searchBar.setError("Incorrect username");
                    return;
                }
            }

        });

        return root;
}
}
