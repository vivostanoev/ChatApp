package com.example.pmuchatproject.findfriends;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.example.pmuchatproject.R;


public class FindFriendsFragment extends Fragment {

   public FindFriendsFragment()
   {

   }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater,ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_find_friends, container, false);
    }
}