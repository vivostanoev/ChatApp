package com.example.pmuchatproject.friendRequest;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.example.pmuchatproject.R;
import com.example.pmuchatproject.commons.Constant;
import com.example.pmuchatproject.commons.NodeNames;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import org.w3c.dom.Node;

import java.util.ArrayList;
import java.util.List;


public class RequestFragment extends Fragment {

    private RecyclerView rvRequest;
    private RequestAdapter adapter;
    private List<RequestModel> requestModelList;
    private TextView tvEmptyRequestList;

    private DatabaseReference databaseReferenceRequest, databaseReferenceUser;
    private FirebaseUser currentUser;

    public RequestFragment()
    {

    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_request, container, false);
    }


    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        rvRequest = view.findViewById(R.id.rvRequests);
        tvEmptyRequestList = view.findViewById(R.id.tvEmptyRequests);

        rvRequest.setLayoutManager(new LinearLayoutManager(getActivity()));
        requestModelList = new ArrayList<>();
        adapter = new RequestAdapter(getActivity(), requestModelList);
        rvRequest.setAdapter(adapter);

        currentUser = FirebaseAuth.getInstance().getCurrentUser();
        databaseReferenceUser = FirebaseDatabase.getInstance().getReference().child(NodeNames.USERS);

        databaseReferenceRequest = FirebaseDatabase.getInstance().getReference().child(NodeNames.FRIEND_REQUEST).child(currentUser.getUid());

        tvEmptyRequestList.setVisibility(View.VISIBLE);

        databaseReferenceRequest.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                requestModelList.clear();

                for (DataSnapshot ds:snapshot.getChildren()) {
                    if (ds.exists())
                    {
                        String requestType = ds.child(NodeNames.REQUEST_TYPE).getValue().toString();
                        if (requestType.equals(Constant.REQUEST_DATA_RECEIVED))
                        {
                            final String userId = ds.getKey();
                            databaseReferenceUser.child(userId).addListenerForSingleValueEvent(new ValueEventListener() {
                                @Override
                                public void onDataChange(@NonNull DataSnapshot snapshot) {
                                    String username = snapshot.child(NodeNames.NAME).getValue().toString();
                                    String photoName = "";
                                    if (snapshot.child(NodeNames.PHOTO).getValue() != null)
                                    {
                                         photoName = snapshot.child(NodeNames.PHOTO).getValue().toString();
                                    }

                                    RequestModel requestModel = new RequestModel(userId, username, photoName);
                                    requestModelList.add(requestModel);
                                    adapter.notifyDataSetChanged();

                                    tvEmptyRequestList.setVisibility(View.GONE);
                                }


                                @Override
                                public void onCancelled(@NonNull DatabaseError error) {
                                    Toast.makeText(getActivity(), getActivity().getString(R.string.failed_fetch_friend_request, error.getMessage()), Toast.LENGTH_SHORT).show();
                                }
                            });
                        }
                    }
                }


            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Toast.makeText(getActivity(), getActivity().getString(R.string.failed_fetch_friend_request, error.getMessage()), Toast.LENGTH_SHORT).show();
            }
        });
    }


}