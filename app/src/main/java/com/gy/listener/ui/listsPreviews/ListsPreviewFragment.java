package com.gy.listener.ui.listsPreviews;

import  android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.widget.Toolbar;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.ViewModelProvider;
import androidx.navigation.Navigation;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import com.gy.listener.R;
import com.gy.listener.model.events.IOnCompleteListener;
import com.gy.listener.model.items.records.RecordsList;
import com.gy.listener.ui.RecordsListsViewModel;

import java.util.List;

public class ListsPreviewFragment extends Fragment implements IOnCompleteListener {

    // region Members

    private RecordsListsViewModel _viewModel;
    private ListPreviewAdapter _adapter;
    private LiveData<List<RecordsList>> _recordsLists;

    // endregion

    // region UI Members

    private Toolbar _toolbar;
    private RecyclerView _previews;
    private SwipeRefreshLayout _refreshLayout;

    // endregion

    @Override
    public View onCreateView(
            LayoutInflater inflater, ViewGroup container,
            Bundle savedInstanceState) {

        _toolbar = requireActivity().findViewById(R.id.toolbar);
        _toolbar.setNavigationIcon(null);

        _viewModel = new ViewModelProvider(this).get(RecordsListsViewModel.class);

        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_lists_previews, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        view.findViewById(R.id.add_fab).setOnClickListener(
                Navigation.createNavigateOnClickListener(R.id.action_listsPreviewsFragment_to_listAdditionFragment));

        initViews(view);

        // Showing we're loading data and not stopping until received
        setLoadingState(true);

        // Getting tha data
        _recordsLists = _viewModel.getData(this);
        _recordsLists.observe(getViewLifecycleOwner(),
                (data) -> _adapter.notifyDataSetChanged());

        // Set lists previews

        RecyclerView.LayoutManager layoutManager = new GridLayoutManager(getContext(), 2);
        _previews.setLayoutManager(layoutManager);

        _adapter = new ListPreviewAdapter(_recordsLists);
        _previews.setAdapter(_adapter);

        // Set swipe refresh
        _refreshLayout.setOnRefreshListener(()-> _viewModel.refreshData(this));
    }

    // region Private Methods

    private void initViews(View root) {
        _previews = root.findViewById(R.id.lists_prevs_rv);
        _refreshLayout = root.findViewById(R.id.previews_refresh_layout);
    }

    private void setLoadingState(boolean isLoading) {
        _refreshLayout.setRefreshing(isLoading);
    }

    // endregion

    // region IOnCompleteListener

    @Override
    public void onComplete(boolean isSuccess) {
        setLoadingState(false);

        if (!isSuccess) {
            requireActivity().runOnUiThread(() -> Toast.makeText(getContext(), R.string.fetching_failed, Toast.LENGTH_SHORT).show());
        }
    }

    // endregion
}