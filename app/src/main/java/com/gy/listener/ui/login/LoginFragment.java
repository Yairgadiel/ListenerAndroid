package com.gy.listener.ui.login;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.activity.result.ActivityResultLauncher;
import androidx.fragment.app.Fragment;
import androidx.navigation.fragment.NavHostFragment;

import com.firebase.ui.auth.AuthUI;
import com.firebase.ui.auth.FirebaseAuthUIActivityResultContract;
import com.firebase.ui.auth.IdpResponse;
import com.firebase.ui.auth.data.model.FirebaseAuthUIAuthenticationResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.gy.listener.R;
import com.gy.listener.model.RecordsListsRepository;
import com.gy.listener.model.items.users.User;

import java.util.Arrays;
import java.util.List;

import static android.app.Activity.RESULT_OK;

public class LoginFragment extends Fragment {

    private final ActivityResultLauncher<Intent> _signInLauncher = registerForActivityResult(
            new FirebaseAuthUIActivityResultContract(),
            this::onSignInResult
    );

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        setupAuthentication();

        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_login, container, false);
    }

    private void setupAuthentication() {
        // Choose authentication providers
        List<AuthUI.IdpConfig> providers = Arrays.asList(
                new AuthUI.IdpConfig.EmailBuilder().build(),
                new AuthUI.IdpConfig.GoogleBuilder().build());

        // Create and launch sign-in intent
        Intent signInIntent = AuthUI.getInstance()
                .createSignInIntentBuilder()
                .setAvailableProviders(providers)
                .setLogo(R.mipmap.ic_launcher)
                .build();
        _signInLauncher.launch(signInIntent);
    }

    private void onSignInResult(FirebaseAuthUIAuthenticationResult result) {
        IdpResponse response = result.getIdpResponse();

        if (result.getResultCode() == RESULT_OK) {
            // Successfully signed in
            FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
            if (user == null) {
                Log.d("LISTENER", "auth failed");
            }
            else {
                // Adding the user (if yet to be added)
                RecordsListsRepository.getInstance().addUser(new User(user.getUid(), user.getDisplayName(), user.getEmail()), isSuccess -> {
                    if (isSuccess) {
                        NavHostFragment.findNavController(LoginFragment.this)
                                .navigate(R.id.action_LoginFragment_to_ListsPreviewsFragment);
                    }
                });
            }
        }
        else {
            if (response == null) {
                Log.d("LISTENER", "auth canceled by user");
            }
            else {
                Log.d("LISTENER", "auth error: " + response.getError().getMessage());
            }
        }
    }
}