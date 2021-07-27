package com.gy.listener.ui.recordsList;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.recyclerview.widget.RecyclerView;

import com.gy.listener.R;
import com.gy.listener.model.items.users.User;

import java.util.List;

public class UsersAdapter extends RecyclerView.Adapter<UsersAdapter.UserViewHolder> {

    private final List<User> _data;
    private final IUserInviter _userInviter;

    public UsersAdapter(List<User> lists, IUserInviter userInviter) {
        _data = lists;
        _userInviter = userInviter;
    }

    @NonNull
    @Override
    public UserViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        // Create a new view, which defines the UI of the list item
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.user_item, parent, false);

        return new UserViewHolder(view, new UserClickListener());
    }

    @Override
    public void onBindViewHolder(@NonNull UserViewHolder holder, int position) {
        User currUser = _data.get(position);

        holder.setName(currUser.getName());
        holder.setEmail(currUser.getEmail());
        holder.updateListenersPosition(position);
    }

    @Override
    public int getItemCount() {
        return _data == null ? 0 : _data.size();
    }

    /**
     * Provide a reference to the type of views that you are using
     * (custom ViewHolder).
     */
    public static class UserViewHolder extends RecyclerView.ViewHolder {
        private final View _container;
        private final TextView _userName;
        private final TextView _userEmail;

        private final UserClickListener _userClickListener;

        public UserViewHolder(View view, UserClickListener userClickListener) {
            super(view);

            _container = view;
            _userName = view.findViewById(R.id.user_name);
            _userEmail = view.findViewById(R.id.user_email);
            _userClickListener = userClickListener;
            _container.setOnClickListener(_userClickListener);
        }

        public void setName(String name) {
            _userName.setText(name);
        }

        public void setEmail(String emailAddress) {
            _userEmail.setText(emailAddress);
        }

        public void updateListenersPosition(int position) {
            _userClickListener.updatePosition(position);
        }
    }

    private class UserClickListener implements View.OnClickListener {
        private int _position;

        public void updatePosition(int position) {
            this._position = position;
        }

        @Override
        public void onClick(View v) {
            User currUser = _data.get(_position);

            AlertDialog.Builder builder = new AlertDialog.Builder(v.getContext());

            builder.setMessage(R.string.invite_user_prompt);
            builder.setPositiveButton(R.string.yes, (dialog, which) -> {
                _userInviter.inviteUser(currUser.getId());
                _data.remove(_position);
                notifyDataSetChanged();
            });

            builder.setNegativeButton(R.string.no, (dialog, which) -> {
                dialog.dismiss();
            });

            builder.create().show();
        }
    }
}

