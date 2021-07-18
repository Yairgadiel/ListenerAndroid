package com.gy.listener.ui.listAddition;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.navigation.fragment.NavHostFragment;

import com.google.android.material.progressindicator.CircularProgressIndicator;
import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;
import com.gy.listener.R;
import com.gy.listener.model.events.IValidator;
import com.gy.listener.model.items.ListType;
import com.gy.listener.model.items.RecordsList;
import com.gy.listener.ui.RecordsListsViewModel;
import com.gy.listener.utilities.InputUtils;
import com.gy.listener.utilities.TextUtils;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

public class ListAdditionFragment extends Fragment {

    private RecordsListsViewModel _viewModel;

    // region UI Members

    private TextInputEditText _id;
    private TextInputLayout _idLayout;

    private TextInputEditText _name;
    private TextInputLayout _nameLayout;

    private TextInputEditText _details;
    private TextInputLayout _detailsLayout;

    private AutoCompleteTextView _listType;
    private TextInputLayout _listTypeLayout;
    private ListType _selectedType = null;

    private CircularProgressIndicator _loader;

    // endregion

    @Override
    public View onCreateView(
            LayoutInflater inflater, ViewGroup container,
            Bundle savedInstanceState) {
        setHasOptionsMenu(true);

        _viewModel = new ViewModelProvider(this).get(RecordsListsViewModel.class);

        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_list_addition, container, false);
    }

    @Override
    public void onCreateOptionsMenu(@NonNull Menu menu, @NonNull MenuInflater inflater) {
        menu.clear();
        super.onCreateOptionsMenu(menu, inflater);
    }

    @Override
    public void onViewCreated(@NonNull View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        initViews(view);
        setListTypeAdapter();

        view.findViewById(R.id.cancel_btn).setOnClickListener(v ->
                NavHostFragment.findNavController(ListAdditionFragment.this)
                        .popBackStack());

        view.findViewById(R.id.create_btn).setOnClickListener(v -> validateInput(isValid -> {
            _loader.setVisibility(View.VISIBLE);

            if (isValid) {
                Log.d("LISTENER", "add list");

                _viewModel.setRecordsList(new RecordsList(_id.getText().toString(),
                                _name.getText().toString(),
                                _details.getText().toString(),
                                _selectedType),
                        isSuccess -> {
                            Log.d("LISTENER", "add list is success " + isSuccess);

                            requireActivity().runOnUiThread(() -> {
                                // Stop "loading"
                                _loader.setVisibility(View.GONE);

                                if (!isSuccess) {
                                    Toast.makeText(getContext(), R.string.addition_failed, Toast.LENGTH_SHORT).show();
                                }
                                else {
                                    NavHostFragment.findNavController(ListAdditionFragment.this)
                                            .popBackStack();
                                }
                            });
                        });
            }
            else {
                // Invalid, stop "loading"
                _loader.setVisibility(View.GONE);
            }
        });
    }

    private void initViews(@NonNull View rootView) {
        _id = rootView.findViewById(R.id.list_id);
        _idLayout = rootView.findViewById(R.id.list_id_layout);
        _idLayout.setEndIconMode(TextInputLayout.END_ICON_CLEAR_TEXT);
        _idLayout.setEndIconActivated(true);

        _name = rootView.findViewById(R.id.list_name);
        _nameLayout = rootView.findViewById(R.id.list_name_layout);
        _nameLayout.setEndIconMode(TextInputLayout.END_ICON_CLEAR_TEXT);
        _nameLayout.setEndIconActivated(true);

        _details = rootView.findViewById(R.id.list_details);
        _detailsLayout = rootView.findViewById(R.id.list_details_layout);
        _detailsLayout.setEndIconMode(TextInputLayout.END_ICON_CLEAR_TEXT);
        _detailsLayout.setEndIconActivated(true);

        _listType = rootView.findViewById(R.id.list_type);
        _listTypeLayout = rootView.findViewById(R.id.list_type_layout);

        _loader = rootView.findViewById(R.id.addition_loader);

        InputUtils.addTextValidator(_id, _idLayout);
        InputUtils.addTextValidator(_name, _nameLayout);
    }

    private void setListTypeAdapter() {
        List<String> listTypes;

        if (android.os.Build.VERSION.SDK_INT < android.os.Build.VERSION_CODES.N) {
            listTypes = new ArrayList<>(ListType.values().length);

            for (int i = 0; i < ListType.values().length; i++) {
                listTypes.add(TextUtils.toPascalCase(ListType.values()[i].name()));
            }
        }
        else {
            listTypes = Arrays.stream(ListType.values()).map(e -> TextUtils.toPascalCase(e.name())).collect(Collectors.toList());
        }

        ArrayAdapter<String> typesAdapter = new ArrayAdapter<>(getContext(),
                R.layout.dropdown_menu_popup_item,
                listTypes);
        _listType.setAdapter(typesAdapter);

        _listType.setOnItemClickListener((parent, view, position, id) -> {
            _selectedType = ListType.values()[position];
            _listTypeLayout.setError(null);
        });
    }

    /**
     * This method validates the input and actively sets errors in cases of values yet to be set
     * @param idValidator validator listener for the input validation
     */
    private void validateInput(IValidator idValidator) {
        if ((_name.getText() == null || _name.getText().toString().isEmpty())) {
            _nameLayout.setError(getString(R.string.empty_string_error));
        }

        if (_selectedType == null) {
            _listTypeLayout.setError(getString(R.string.none_selected_error));
        }

        if ((_id.getText() == null || _id.getText().toString().isEmpty())) {
            _idLayout.setError(getString(R.string.empty_string_error));
            idValidator.isValid(isNotError());
        }
        else {
            _viewModel.isIdAvailable(_id.getText().toString(), isValid -> {
                if (!isValid) {
                    _idLayout.setError(getString(R.string.id_taken_error));
                }

                idValidator.isValid(isValid && isNotError());
            });
        }
    }

    /**
     * Method checks if any of the input views are NOT in error state
     */
    private boolean isNotError() {
        return _idLayout.getError() == null &&
                _nameLayout.getError() == null &&
                _listTypeLayout.getError() == null;
    }
}