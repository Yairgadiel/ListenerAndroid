package com.gy.listener.ui.recordsList;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.OnBackPressedCallback;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.widget.Toolbar;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModelProvider;
import androidx.navigation.fragment.NavHostFragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.gy.listener.R;
import com.gy.listener.ui.RecordsListsViewModel;
import com.gy.listener.model.items.CheckedRecord;
import com.gy.listener.model.items.RecordsList;
import com.gy.listener.utilities.Helpers;

public class RecordsListFragment extends Fragment {

    // region UI Members

    private Toolbar _toolbar;
    private TextView _id;
    private TextView _dateCreated;
    private TextView _details;
    private RecyclerView _records;
    private ImageButton _addRecordBtn;

    private RecordsListsViewModel _viewModel;
    private RecordsList _currRecordsList;
    private MutableLiveData<Boolean> _isAdding;
    private MutableLiveData<Boolean> _isChanged;

    private NavigateBackListener _navigateBackListener;

    private RecordsListAdapter _adapter;

    // endregion

    @Override
    public View onCreateView(
            LayoutInflater inflater, ViewGroup container,
            Bundle savedInstanceState) {
        setHasOptionsMenu(true);

        _viewModel = new ViewModelProvider(this).get(RecordsListsViewModel.class);
        _navigateBackListener = new NavigateBackListener();

        _toolbar = requireActivity().findViewById(R.id.toolbar);
        _toolbar.setNavigationOnClickListener(_navigateBackListener);

        requireActivity().getOnBackPressedDispatcher().addCallback(getViewLifecycleOwner(), _navigateBackListener);

        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_records_list, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        initViews(view);

        RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(getContext());
        _records.setLayoutManager(layoutManager);
        String recordsListId = null;

        // Extract the current records list
        if (getArguments() != null) {
            recordsListId = RecordsListFragmentArgs.fromBundle(getArguments()).getRecordsListId();
        }

        if (recordsListId == null) {
            System.out.println("No records list was passed!");
            NavHostFragment.findNavController(RecordsListFragment.this).popBackStack();
        }
        else {
            _currRecordsList = _viewModel.getRecordsListById(recordsListId);

            if (_currRecordsList == null) {
                System.out.println("Illegal records list ID was passed!");
                NavHostFragment.findNavController(RecordsListFragment.this).popBackStack();
            }
            else {
                _toolbar.setTitle(_currRecordsList.getName());
                _id.setText(_currRecordsList.getId());
                _dateCreated.setText(Helpers.getDateString(_currRecordsList.getDateCreated()));

                _isAdding = new MutableLiveData<>(false);
                _isChanged = new MutableLiveData<>(false);

                // Setting the adapter
                _adapter = new RecordsListAdapter(_currRecordsList, _isAdding, _isChanged);
                _records.setAdapter(_adapter);

                _details.setText(_currRecordsList.getDetails() == null ? "" : (_currRecordsList.getDetails()));
                _addRecordBtn.setOnClickListener(v -> {
                    // Only adding a record if the previous one isn't empty
                    if (_currRecordsList.getRecords().isEmpty() || !_currRecordsList.getRecords().get(_adapter.getItemCount() - 1).getText().isEmpty()) {
                        _currRecordsList.getRecords().add(new CheckedRecord("", false));
                        _isAdding.postValue(true);
                        _isChanged.setValue(true);
                    }
                });
            }
        }
    }

    @Override
    public void onCreateOptionsMenu(@NonNull Menu menu, @NonNull MenuInflater inflater) {
        menu.clear();
        inflater.inflate(R.menu.menu_save, menu);

        super.onCreateOptionsMenu(menu, inflater);
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        if (item.getItemId() == R.id.action_save) {
            saveRecords();

            return true;
        }
        else if (item.getItemId() == R.id.action_revert) {
            discardChanges();

            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    // region Private Methods

    private void initViews(@NonNull View rootView) {
        _id = rootView.findViewById(R.id.list_id);
        _dateCreated = rootView.findViewById(R.id.list_created);
        _details = rootView.findViewById(R.id.list_details);
        _records = rootView.findViewById(R.id.records);
        _addRecordBtn = rootView.findViewById(R.id.add_record_btn);
    }

    private void promptSaveIfChanged() {
        if (_isChanged.getValue()) {
            AlertDialog.Builder builder = new AlertDialog.Builder(requireActivity());

            builder.setMessage(R.string.save_changes_prompt);
            builder.setPositiveButton(R.string.save, (dialog, which) -> {
                saveRecords();
                NavHostFragment.findNavController(RecordsListFragment.this).popBackStack();
            });

            builder.setNegativeButton(R.string.discard, (dialog, which) -> {
                discardChanges();
                NavHostFragment.findNavController(RecordsListFragment.this).popBackStack();
            });

            builder.create().show();
        }
        else {
            NavHostFragment.findNavController(RecordsListFragment.this).popBackStack();
        }
    }

    private void saveRecords() {
        if (_isChanged.getValue()) {
            // Remove last record if empty
            if (!_currRecordsList.getRecords().isEmpty() && _currRecordsList.getRecords().get(_adapter.getItemCount() - 1).getText().isEmpty()) {
                _currRecordsList.getRecords().remove(_adapter.getItemCount() - 1);
            }

            _viewModel.setRecordsList(_currRecordsList, b->{});

            // No longer in adding mode
            _isAdding.postValue(false);
            _isChanged.setValue(false);

            Toast.makeText(getContext(), R.string.successfully_saved, Toast.LENGTH_SHORT).show();
        }
    }

    /**
     * Reverting changes made since last save
     */
    private void discardChanges() {
        if (_isChanged.getValue()) {
            // No longer in adding mode
            _isAdding.postValue(false);

            // Get the records list once again
            _currRecordsList.getRecords().clear();
            _currRecordsList.getRecords().addAll(_viewModel.getRecordsListById(_currRecordsList.getId()).getRecords());

            _adapter.notifyDataSetChanged();

            _isChanged.setValue(false);

            Toast.makeText(getContext(), R.string.successfully_reverted, Toast.LENGTH_SHORT).show();
        }
    }

    // endregion

    // region Listeners

    private class NavigateBackListener extends OnBackPressedCallback implements View.OnClickListener {
        public NavigateBackListener() {
            super(true);
        }

        @Override
        public void onClick(View v) {
            promptSaveIfChanged();
        }

        @Override
        public void handleOnBackPressed() {
            promptSaveIfChanged();
        }
    }

    // endregion

}