package com.gy.listener.myLists;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.Spinner;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.navigation.Navigation;
import androidx.navigation.fragment.NavHostFragment;

import com.gy.listener.R;

import java.util.Arrays;
import java.util.stream.Collectors;

public class ListAdditionFragment extends Fragment {

    // region UI Members

    private EditText _id;
    private EditText _name;
    private EditText _details;
    private Spinner _listType;

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

        initViews(view);

        view.findViewById(R.id.cancel_btn).setOnClickListener(v ->
                NavHostFragment.findNavController(ListAdditionFragment.this)
                        .popBackStack());

        view.findViewById(R.id.create_btn).setOnClickListener(v -> {
            // TODO validate input

            MyListsRepository.getInstance().addItemsList(new RecordsList(
                    _id.getText().toString(),
                    _name.getText().toString(),
                    _details.getText().toString(),
                    ListType.GROCERIES
            ));

            Navigation.findNavController(v).popBackStack();
        });
    }

    private void initViews(View rootView) {
        _id = rootView.findViewById(R.id.list_id);
        _name = rootView.findViewById(R.id.list_name);
        _details = rootView.findViewById(R.id.list_details);
        _listType = rootView.findViewById(R.id.list_type);

        ArrayAdapter typesAdapter = new ArrayAdapter<>(getContext(),
                android.R.layout.simple_spinner_item,
                Arrays.stream(ListType.values()).map(Enum::name).collect(Collectors.toList()));
        typesAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        _listType.setAdapter(typesAdapter);
    }
}