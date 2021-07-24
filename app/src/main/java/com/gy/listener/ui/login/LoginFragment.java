package com.gy.listener.ui.login;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import androidx.navigation.fragment.NavHostFragment;

import com.google.android.material.progressindicator.CircularProgressIndicator;
import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;
import com.gy.listener.R;
import com.gy.listener.model.events.IValidator;
import com.gy.listener.ui.UsersViewModel;
import com.gy.listener.utilities.InputUtils;

public class LoginFragment extends Fragment {

    // region UI Members

    private TextInputEditText _email;
    private TextInputLayout _emailLayout;

    private TextInputEditText _name;
    private TextInputLayout _nameLayout;

    private TextInputEditText _password;
    private TextInputLayout _passwordLayout;

    private Button _signUp;
    private CircularProgressIndicator _loader;

    private Button _signInEmail;

    // endregion

    // region Members

    private UsersViewModel _viewModel;
    private NavController _navController;

    // endregion

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        if (getActivity() != null) {
            getActivity().getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_PAN);
        }

        _viewModel = new ViewModelProvider(this).get(UsersViewModel.class);
        _navController = NavHostFragment.findNavController(LoginFragment.this);

        if (_viewModel.getLoggedUser().getValue() != null) {
            _navController.navigate(R.id.action_LoginFragment_to_ListsPreviewsFragment);
        }

        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_login, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        initViews(view);

        _signUp.setOnClickListener(v -> {
            _loader.setVisibility(View.VISIBLE);

            validateInput(isValid -> {
                if (isValid) {
                    _viewModel.signUp(_name.getText().toString(),
                            _email.getText().toString(),
                            _password.getText().toString(),
                            isSuccess -> {
                                if (isSuccess) {
                                    _navController.navigate(R.id.action_LoginFragment_to_ListsPreviewsFragment);
                                }
                                else {
                                    requireActivity().runOnUiThread(() ->
                                            Toast.makeText(getContext(), R.string.unknown_error, Toast.LENGTH_SHORT).show());
                                }

                                _loader.setVisibility(View.INVISIBLE);
                            });
                }
                else {
                    _loader.setVisibility(View.INVISIBLE);
                }
            });

        });

        _signInEmail.setOnClickListener(
                Navigation.createNavigateOnClickListener(R.id.action_LoginFragment_to_signInFragment));
    }

    private void initViews(@NonNull View rootView) {
        _loader = rootView.findViewById(R.id.loader);
        _email = rootView.findViewById(R.id.user_email);
        _emailLayout = rootView.findViewById(R.id.user_email_layout);

        _name = rootView.findViewById(R.id.user_name);
        _nameLayout = rootView.findViewById(R.id.user_name_layout);

        _password = rootView.findViewById(R.id.user_pass);
        _passwordLayout = rootView.findViewById(R.id.user_pass_layout);

        _signUp = rootView.findViewById(R.id.sign_up);
        _signInEmail = rootView.findViewById(R.id.sign_in_email);

        InputUtils.addTextValidator(_email, _emailLayout);
        InputUtils.addTextValidator(_name, _nameLayout);
        InputUtils.addTextValidator(_password, _passwordLayout);
    }

    /**
     * This method validates the input and actively sets errors in cases of values yet to be set
     * @param validator validator listener for the input validation
     */
    private void validateInput(IValidator validator) {
        if ((_name.getText() == null || _name.getText().toString().isEmpty())) {
            _nameLayout.setError(getString(R.string.empty_string_error));
        }

        if ((_email.getText() == null || _email.getText().toString().isEmpty())) {
            _emailLayout.setError(getString(R.string.empty_string_error));
            validator.isValid(isNotError());
        }
        else {
            _viewModel.isEmailAvailable(_email.getText().toString(), isValid -> {
                if (!isValid) {
                    _emailLayout.setError(getString(R.string.email_taken_error));
                }

                validator.isValid(isValid && isNotError());
            });
        }
    }

    /**
     * Method checks if any of the input views are NOT in error state
     */
    private boolean isNotError() {
        return _emailLayout.getError() == null &&
                _nameLayout.getError() == null &&
                _passwordLayout.getError() == null;
    }
}