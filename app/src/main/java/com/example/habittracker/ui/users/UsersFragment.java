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
import android.widget.AdapterView;
import android.widget.ArrayAdapter;

import android.widget.Button;
import android.widget.EditText;
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
    EditText searchBar;
    Button searchButton;

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

        searchBar = root.findViewById(R.id.searchBar);
        searchButton = root.findViewById(R.id.searchButton);
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
        
        UsersList userListCopy = new UsersList();

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


                            for (int i = 0; i < usersList.getCount(); i++){
                                userListCopy.addUser(usersList.getUser(i));
                            }

                            userListAdapter.notifyDataSetChanged();
                        } else {
                            Log.d(TAG, "Error getting documents: ", task.getException());
                        }
                    }
                });

        //System.out.println("user LIst copy size" + userListCopy.getCount());
        searchButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String userName = searchBar.getText().toString().trim();
                UsersList updatedUserList = new UsersList();

                System.out.println("user LIst copy size" + userListCopy.getCount());
                System.out.println("user list size" + usersList.getCount());
                if (usersList.getCount() < userListCopy.getCount() ) {
                    System.out.println("I am here to update user List");
                    for (int i = 0; i < userListCopy.getCount(); i++) {
                        System.out.println("Printing user in copy:" + userListCopy.getUser(i));
                        usersList.addUser(userListCopy.getUser(i));
                    }
                    userListAdapter.notifyDataSetChanged();
                }


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
                        updatedUserList.clearUserList();
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

    /**
     * This function will handle opening the user profile activity
     */
    public void openUserProfileActivity(User user) {
        Intent intent = new Intent(requireContext(), UserProfileActivity.class);
        intent.putExtra("User", user);
        startActivity(intent);
    }
}
