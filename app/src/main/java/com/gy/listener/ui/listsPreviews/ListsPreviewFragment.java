package com.gy.listener.ui.listsPreviews;

import  android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.widget.Toolbar;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.ViewModelProvider;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import androidx.navigation.fragment.NavHostFragment;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import com.gy.listener.R;
import com.gy.listener.model.events.IOnCompleteListener;
import com.gy.listener.model.items.records.RecordsList;
import com.gy.listener.viewModel.RecordsListsViewModel;
import com.gy.listener.viewModel.UsersViewModel;

import java.util.List;

public class ListsPreviewFragment extends Fragment implements IOnCompleteListener {

    // region Members

    private RecordsListsViewModel _recordsListsViewModel;
    private UsersViewModel _usersViewModel;
    private ListPreviewAdapter _adapter;
    private LiveData<List<RecordsList>> _recordsLists;
    private NavController _navController;


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
        setHasOptionsMenu(true);

        _recordsListsViewModel = new ViewModelProvider(this).get(RecordsListsViewModel.class);
        _usersViewModel = new ViewModelProvider(this).get(UsersViewModel.class);

        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_lists_previews, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        _navController = NavHostFragment.findNavController(ListsPreviewFragment.this);

        view.findViewById(R.id.add_fab).setOnClickListener(
                Navigation.createNavigateOnClickListener(R.id.action_listsPreviewsFragment_to_listAdditionFragment));

        initViews(view);

        // Showing we're loading data and not stopping until received
        setLoadingState(true);

        // Getting tha data
        _recordsLists = _recordsListsViewModel.getData(this);
        _recordsLists.observe(getViewLifecycleOwner(),
                (data) -> _adapter.notifyDataSetChanged());

        // Set lists previews

        RecyclerView.LayoutManager layoutManager = new GridLayoutManager(getContext(), 2);
        _previews.setLayoutManager(layoutManager);

        _adapter = new ListPreviewAdapter(_recordsLists);
        _previews.setAdapter(_adapter);

        // Set swipe refresh
        _refreshLayout.setOnRefreshListener(()-> _recordsListsViewModel.refreshData(this));
    }

    @Override
    public void onStart() {
        super.onStart();

        // Here on onStart since onViewCreated is called pre activity's initialization
        _toolbar = requireActivity().findViewById(R.id.toolbar);

        if (_toolbar != null) {
            _toolbar.setNavigationIcon(null);
        }
    }

    @Override
    public void onCreateOptionsMenu(@NonNull Menu menu, @NonNull MenuInflater inflater) {
        menu.clear();
        inflater.inflate(R.menu.menu_previews, menu);

        super.onCreateOptionsMenu(menu, inflater);
    }


    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.

        if (item.getItemId() == R.id.action_logout) {
            logout();

            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    // region Private Methods

    private void initViews(View root) {
        _previews = root.findViewById(R.id.lists_prevs_rv);
        _refreshLayout = root.findViewById(R.id.previews_refresh_layout);
    }

    private void setLoadingState(boolean isLoading) {
        _refreshLayout.setRefreshing(isLoading);
    }

    private void logout() {
        // TODO clean DB?
        _usersViewModel.signOut();
        Toast.makeText(getContext(), R.string.signed_out, Toast.LENGTH_SHORT).show();
        Log.d("LISTENER", "signed out");
        _navController.navigateUp();
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