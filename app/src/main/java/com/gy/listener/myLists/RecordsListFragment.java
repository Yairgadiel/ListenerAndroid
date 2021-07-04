package com.gy.listener.myLists;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.navigation.Navigation;
import androidx.navigation.fragment.NavHostFragment;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;
import com.gy.listener.R;
import com.gy.listener.utilities.InputUtils;
import com.gy.listener.utilities.TextUtils;

import java.util.Arrays;
import java.util.stream.Collectors;

public class RecordsListFragment extends Fragment {

    // region UI Members

    private TextView _details;
    private RecyclerView _records;
    private Button _saveBtn;
    private Button _revertBtn;


    private ListType _selectedType = null;

    // endregion

    @Override
    public View onCreateView(
            LayoutInflater inflater, ViewGroup container,
            Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_list_addition, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        // TODO set action bar title to be list's name

        initViews(view);

        view.findViewById(R.id.cancel_btn).setOnClickListener(v ->
                NavHostFragment.findNavController(RecordsListFragment.this)
                        .popBackStack());

    }

    private void initViews(@NonNull View rootView) {
        _details = rootView.findViewById(R.id.list_details);
        _records =  rootView.findViewById(R.id.records);
        _saveBtn =  rootView.findViewById(R.id.save_btn);
        _revertBtn =  rootView.findViewById(R.id.revert_btn);

    }

}