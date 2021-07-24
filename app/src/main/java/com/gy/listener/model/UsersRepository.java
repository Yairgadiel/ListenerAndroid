package com.gy.listener.model;

import android.util.Log;

import androidx.annotation.Nullable;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

import com.gy.listener.model.db.DatabaseHelper;
import com.gy.listener.model.events.IOnCompleteListener;
import com.gy.listener.model.events.IValidator;
import com.gy.listener.model.firebase.FirebaseModel;
import com.gy.listener.model.items.records.Record;
import com.gy.listener.model.items.records.RecordsList;
import com.gy.listener.model.items.users.User;
import com.gy.listener.utilities.Helpers;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class UsersRepository {

    // region Members

    private final ExecutorService _executorService = Executors.newSingleThreadExecutor();
    private final MutableLiveData<User> _loggedUser = new MutableLiveData<>(null);

    // endregion

    // region Singleton

    private static UsersRepository _instance;

    private UsersRepository() {
        _loggedUser.setValue(FirebaseModel.getInstance().getLoggedUser());
    }

    public static UsersRepository getInstance() {
        if (_instance == null) {
            _instance = new UsersRepository();
        }

        return _instance;
    }

    // endregion

    // region Public Methods


    // endregion

    // region Users

    public MutableLiveData<User> getLoggedUser() {
        return _loggedUser;
    }

    // endregion

    // region Authentication

    public void signUp(String name, String email, String password, IOnCompleteListener listener) {
        FirebaseModel.getInstance().signUp(name, email, password, isSuccess -> {
            if (!isSuccess) {
                listener.onComplete(false);
            }
            else {
                User user = FirebaseModel.getInstance().getLoggedUser();

                _executorService.execute(() -> FirebaseModel.getInstance().setUser(user, isAddSuccess -> {
                    if (isAddSuccess) {
                        _loggedUser.postValue(user);
                    }

                    listener.onComplete(isAddSuccess);
                }));
            }
        });
    }

    public void signIn(String email, String password, IOnCompleteListener listener) {
        FirebaseModel.getInstance().signIn(email, password, isSuccess -> {
            if (isSuccess) {
                User user = FirebaseModel.getInstance().getLoggedUser();
                _loggedUser.postValue(user);
            }

            listener.onComplete(isSuccess);
        });
    }

    public void signOut() {
        FirebaseModel.getInstance().signOut();
        _loggedUser.setValue(null);
    }

    public void isEmailAvailable(String email, IValidator validator) {
        FirebaseModel.getInstance().getAllUsersWithField("email", email, data -> validator.isValid(data == null || data.isEmpty()));
    }

    // endregion
}
