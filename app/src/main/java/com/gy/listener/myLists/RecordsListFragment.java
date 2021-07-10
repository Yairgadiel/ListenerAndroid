package com.gy.listener.myLists;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.TextView;

import androidx.activity.OnBackPressedCallback;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.widget.Toolbar;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.MutableLiveData;
import androidx.navigation.fragment.NavHostFragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.gy.listener.R;
import com.gy.listener.myLists.models.CheckedRecord;
import com.gy.listener.myLists.models.ListType;
import com.gy.listener.myLists.models.RecordsList;

public class RecordsListFragment extends Fragment {

    // region UI Members

    private Toolbar _toolbar;
    private TextView _details;
    private RecyclerView _records;
    private ImageButton _addRecordBtn;
    private MutableLiveData<Boolean> _isAdding;
    private NavigateBackListener _navigateBackListener;

    private ListType _selectedType = null;

    private RecordListAdapter _adapter;

    // endregion

    @Override
    public View onCreateView(
            LayoutInflater inflater, ViewGroup container,
            Bundle savedInstanceState) {
        setHasOptionsMenu(true);

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

        // Extract the current records list
        final RecordsList recordsList;

        if (getArguments() != null) {
            recordsList = RecordsListFragmentArgs.fromBundle(getArguments()).getRecordsList();
        }
        else {
            recordsList = null;
        }

        if (recordsList == null) {
            System.out.println("No records list was passed!");
            NavHostFragment.findNavController(RecordsListFragment.this).popBackStack();
        }
        else {
            // TODO set action bar title to be list's name
            _toolbar.setTitle(recordsList.getName());

            _isAdding = new MutableLiveData<>(false);

            // Setting the adapter
            _adapter = new RecordListAdapter(recordsList, _isAdding);
            _records.setAdapter(_adapter);

            _details.setText(recordsList.getDetails() == null ? "" : (recordsList.getDetails()));
            _addRecordBtn.setOnClickListener(v -> {
                recordsList.getRecords().add(new CheckedRecord("", false));
                _isAdding.postValue(true);
            });
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
            // TODO save to list
            saveRecords();
        }

        return super.onOptionsItemSelected(item);
    }

    // region Private Methods

    private void initViews(@NonNull View rootView) {
        _details = rootView.findViewById(R.id.list_details);
        _records = rootView.findViewById(R.id.records);
        _addRecordBtn = rootView.findViewById(R.id.add_record_btn);
    }

    private void promptSave() {
        AlertDialog.Builder builder = new AlertDialog.Builder(requireActivity());

        builder.setMessage(R.string.save_changes_prompt);
        builder.setPositiveButton(R.string.save, (dialog, which) -> saveRecords());
        builder.setNegativeButton(R.string.discard, (dialog, which) ->discardChanges());

        builder.create().show();
    }

    private void saveRecords() {
        NavHostFragment.findNavController(RecordsListFragment.this).popBackStack();
    }

    private void discardChanges() {
        NavHostFragment.findNavController(RecordsListFragment.this).popBackStack();
    }

    // endregion

    // region Listeners

    private class NavigateBackListener extends OnBackPressedCallback implements View.OnClickListener {
        public NavigateBackListener() {
            super(true);
        }

        @Override
        public void onClick(View v) {
            promptSave();
        }

        @Override
        public void handleOnBackPressed() {
            promptSave();
        }
    }

    // endregion

}