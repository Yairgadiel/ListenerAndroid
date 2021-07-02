package com.gy.listener.myLists;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.navigation.fragment.NavHostFragment;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.gy.listener.CardAdapter;
import com.gy.listener.R;

public class MyListsFragment extends Fragment {

    private MyListsViewModel _viewModel;
    private CardAdapter _adapter;

    @Override
    public View onCreateView(
            LayoutInflater inflater, ViewGroup container,
            Bundle savedInstanceState) {

        _viewModel = new ViewModelProvider(this).get(MyListsViewModel.class);

        _viewModel.getData().observe(getViewLifecycleOwner(),
                (data) -> _adapter.notifyDataSetChanged());

        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_my_lists, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        view.findViewById(R.id.add_fab).setOnClickListener(v ->
                NavHostFragment.findNavController(MyListsFragment.this).navigate(R.id.action_myListsFragment_to_listAdditionFragment));

        // Set lists previews

        RecyclerView previews = view.findViewById(R.id.lists_prevs_rv);
        RecyclerView.LayoutManager layoutManager = new GridLayoutManager(getContext(), 2);
        previews.setLayoutManager(layoutManager);


        _adapter = new CardAdapter(_viewModel.getData());
        previews.setAdapter(_adapter);
    }
}