package com.gy.listener.ui.login;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.navigation.NavController;
import androidx.navigation.fragment.NavHostFragment;

import com.google.android.material.progressindicator.CircularProgressIndicator;
import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;
import com.gy.listener.R;
import com.gy.listener.ui.UsersViewModel;
import com.gy.listener.utilities.InputUtils;

public class SignInFragment extends Fragment {

    // region UI Members

    private TextInputEditText _email;
    private TextInputLayout _emailLayout;

    private TextInputEditText _password;
    private TextInputLayout _passwordLayout;

    private Button _signIn;
    private CircularProgressIndicator _loader;

    // endregion

    // region Members

    private UsersViewModel _viewModel;
    private NavController _navController;

    // endregion

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        _viewModel = new ViewModelProvider(this).get(UsersViewModel.class);

        if (_viewModel.getLoggedUser().getValue() != null) {
            _navController.navigate(R.id.action_LoginFragment_to_ListsPreviewsFragment);
        }

        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_sign_in, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        _navController = NavHostFragment.findNavController(SignInFragment.this);

        initViews(view);

        _signIn.setOnClickListener(v -> {
            _loader.setVisibility(View.VISIBLE);

            if (isNotError()) {
                _viewModel.signInWithEmail(_email.getText().toString(), _password.getText().toString(), isSuccess -> {
                    if (isSuccess) {
                        _navController.navigateUp();
                    }

                    _loader.setVisibility(View.INVISIBLE);
                });
            }
            else {
                _loader.setVisibility(View.INVISIBLE);
            }
        });
    }

    private void initViews(@NonNull View rootView) {
        _loader = rootView.findViewById(R.id.loader);

        _email = rootView.findViewById(R.id.user_email);
        _emailLayout = rootView.findViewById(R.id.user_email_layout);

        _password = rootView.findViewById(R.id.user_pass);
        _passwordLayout = rootView.findViewById(R.id.user_pass_layout);

        _signIn = rootView.findViewById(R.id.sign_in);

        InputUtils.addTextValidator(_email, _emailLayout);
        InputUtils.addTextValidator(_password, _passwordLayout);
    }

    private void validateInput() {

    }

    /**
     * Method checks if any of the input views are NOT in error state
     */
    private boolean isNotError() {
        return _emailLayout.getError() == null &&
                _passwordLayout.getError() == null;
    }
}