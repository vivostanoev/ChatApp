package com.example.pmuchatproject.findfriends;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.example.pmuchatproject.R;
import com.example.pmuchatproject.commons.NodeNames;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

import org.w3c.dom.Node;

import java.util.ArrayList;
import java.util.List;


public class FindFriendsFragment extends Fragment {

    private RecyclerView rvFindFriends;
    private FindFriendAdapter findFriendAdapter;
    private List<FindFriendModel> findFriendModelList;
    private TextView tvEmptyFrinedsList;

    private DatabaseReference databaseReference;
    private FirebaseUser currentUser;
    private View progressBar;

   public FindFriendsFragment()
   {

   }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater,ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_find_friends, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        rvFindFriends = view.findViewById(R.id.rvFindFriends);
        progressBar = view.findViewById(R.id.progressBar);
        tvEmptyFrinedsList = view.findViewById(R.id.tvEmptyFriendsList);
        rvFindFriends.setLayoutManager(new LinearLayoutManager(getActivity()));

        findFriendModelList = new ArrayList<>();
        findFriendAdapter = new FindFriendAdapter(getActivity(), findFriendModelList);
        rvFindFriends.setAdapter(findFriendAdapter);

        databaseReference = FirebaseDatabase.getInstance().getReference().child(NodeNames.USERS);
        currentUser = FirebaseAuth.getInstance().getCurrentUser();

        tvEmptyFrinedsList.setVisibility(View.VISIBLE);
        progressBar.setVisibility(View.VISIBLE);
        Query query = databaseReference.orderByChild(NodeNames.USERS);

        query.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                findFriendModelList.clear();

                for (DataSnapshot ds : snapshot.getChildren())
                {
                    String userId = ds.getKey();

                    if (userId.equals(currentUser.getUid()))
                    {
                        return;
                    }

                    if (ds.child(NodeNames.NAME).getValue() != null)
                    {
                        String fullName = ds.child(NodeNames.NAME).getValue().toString();
                        String photo = ds.child(NodeNames.PHOTO).getValue().toString();

                        findFriendModelList.add(new FindFriendModel(fullName, photo, userId, false));
                        findFriendAdapter.notifyDataSetChanged();

                        tvEmptyFrinedsList.setVisibility(View.GONE);
                        progressBar.setVisibility(View.GONE);
                    }

                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                progressBar.setVisibility(View.GONE);
                Toast.makeText(getContext(), R.string.failed_to_fetch_friends, Toast.LENGTH_SHORT).show();
            }
        });


   }
}