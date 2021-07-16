package com.gy.listener.ui.listsPreviews;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.ViewModelProvider;
import androidx.navigation.Navigation;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.gy.listener.R;
import com.gy.listener.model.items.RecordsList;
import com.gy.listener.ui.RecordsListsViewModel;

import java.util.List;

public class ListsPreviewFragment extends Fragment {

    private RecordsListsViewModel _viewModel;
    private ListPreviewAdapter _adapter;
    private LiveData<List<RecordsList>> _recordsLists;

    @Override
    public View onCreateView(
            LayoutInflater inflater, ViewGroup container,
            Bundle savedInstanceState) {

        _viewModel = new ViewModelProvider(this).get(RecordsListsViewModel.class);

        _recordsLists = _viewModel.getData();
        _recordsLists.observe(getViewLifecycleOwner(),
                (data) -> _adapter.notifyDataSetChanged());

        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_lists_previews, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        view.findViewById(R.id.add_fab).setOnClickListener(
                Navigation.createNavigateOnClickListener(R.id.action_myListsFragment_to_listAdditionFragment));

        // Set lists previews

        RecyclerView previews = view.findViewById(R.id.lists_prevs_rv);
        RecyclerView.LayoutManager layoutManager = new GridLayoutManager(getContext(), 2);
        previews.setLayoutManager(layoutManager);

        _adapter = new ListPreviewAdapter(_viewModel.getData());
        previews.setAdapter(_adapter);
    }
}