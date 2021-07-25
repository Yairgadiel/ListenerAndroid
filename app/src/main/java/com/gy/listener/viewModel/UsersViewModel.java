package com.gy.listener.viewModel;

import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.gy.listener.model.UsersRepository;
import com.gy.listener.model.events.IOnCompleteListener;
import com.gy.listener.model.events.IValidator;
import com.gy.listener.model.items.users.User;

public class UsersViewModel extends ViewModel {
    private MutableLiveData<User> _loggedUser;

    public UsersViewModel() {
        _loggedUser = UsersRepository.getInstance().getLoggedUser();
    }

    public MutableLiveData<User> getLoggedUser() {
        return _loggedUser;
    }

    public void signUp(String name, String email, String password, IOnCompleteListener listener) {
        UsersRepository.getInstance().signUp(name, email, password, listener);
    }

    public void signInWithEmail(String email, String password, IOnCompleteListener listener) {
        UsersRepository.getInstance().signIn(email, password, listener);
    }

    public void signOut() {
        UsersRepository.getInstance().signOut();
    }

    public void isEmailAvailable(String email, IValidator validator) {
        UsersRepository.getInstance().isEmailAvailable(email, validator);
    }
}
