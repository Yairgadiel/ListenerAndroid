package com.gy.listener.myLists;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.TextView;

import androidx.annotation.NonNull;
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

    private TextView _details;
    private RecyclerView _records;
    private ImageButton _addRecordBtn;
    private MutableLiveData<Boolean> _isAdding;
    private Button _saveBtn;
    private Button _revertBtn;


    private ListType _selectedType = null;

    private RecordListAdapter _adapter;

    // endregion

    @Override
    public View onCreateView(
            LayoutInflater inflater, ViewGroup container,
            Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_records_list, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        initViews(view);

//        view.findViewById(R.id.cancel_btn).setOnClickListener(v ->
//                NavHostFragment.findNavController(RecordsListFragment.this)
//                        .popBackStack());

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

    private void initViews(@NonNull View rootView) {
        _details = rootView.findViewById(R.id.list_details);
        _records = rootView.findViewById(R.id.records);
        _addRecordBtn = rootView.findViewById(R.id.add_record_btn);
        _saveBtn = rootView.findViewById(R.id.save_btn);
        _revertBtn = rootView.findViewById(R.id.revert_btn);
    }
}