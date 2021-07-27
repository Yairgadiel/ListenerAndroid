package com.gy.listener.ui.recordsList;

import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.OnBackPressedCallback;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.widget.Toolbar;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModelProvider;
import androidx.navigation.NavController;
import androidx.navigation.fragment.NavHostFragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.progressindicator.CircularProgressIndicator;
import com.gy.listener.R;
import com.gy.listener.model.events.IOnCompleteListener;
import com.gy.listener.model.events.IOnImageLoadedListener;
import com.gy.listener.model.events.IOnImageUploadedListener;
import com.gy.listener.model.items.records.CheckedRecord;
import com.gy.listener.model.items.records.RecordsList;
import com.gy.listener.model.items.users.User;
import com.gy.listener.utilities.Helpers;
import com.gy.listener.viewModel.RecordsListsViewModel;
import com.gy.listener.viewModel.UsersViewModel;

import java.util.ArrayList;
import java.util.List;

public class RecordsListFragment extends Fragment implements IImageHelper {

    // region UI Members

    private TextView _id;
    private TextView _dateCreated;
    private TextView _details;
    private TextView _users;
    private RecyclerView _records;
    private ImageButton _addRecordBtn;
    private CircularProgressIndicator _loader;

    // endregion

    // region Members

    private RecordsListsViewModel _recordsListViewModel;
    private RecordsList _currRecordsList;
    private MutableLiveData<Boolean> _isAdding;
    private MutableLiveData<Boolean> _isChanged;

    private NavigateBackListener _navigateBackListener;

    private RecordsListAdapter _adapter;

    private NavController _navController;

    private UsersViewModel _usersViewModel;

    // region Record Attachment Members

    private String _attachmentName;
    private IOnImageUploadedListener _imageUploadedListener;
    private final ActivityResultLauncher<String> _resultLauncher = registerForActivityResult(new ActivityResultContracts.GetContent(),
            pickedUri -> {
                if (pickedUri != null) {
                    _loader.setVisibility(View.VISIBLE);

                    _recordsListViewModel.saveAttachment(_attachmentName, pickedUri, uploadedUri -> {
                        _loader.setVisibility(View.INVISIBLE);
                        _imageUploadedListener.onUploaded(uploadedUri);
                    });
                }
            });

    // endregion

    // endregion

    @Override
    public View onCreateView(
            LayoutInflater inflater, ViewGroup container,
            Bundle savedInstanceState) {
        setHasOptionsMenu(true);

        _recordsListViewModel = new ViewModelProvider(this).get(RecordsListsViewModel.class);
        _usersViewModel = new ViewModelProvider(this).get(UsersViewModel.class);
        _navigateBackListener = new NavigateBackListener();

        requireActivity().getOnBackPressedDispatcher().addCallback(getViewLifecycleOwner(), _navigateBackListener);

        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_records_list, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        _navController = NavHostFragment.findNavController(RecordsListFragment.this);
        initViews(view);

        RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(getContext());
        _records.setLayoutManager(layoutManager);
        String recordsListId = null;

        // Extract the current records list
        if (getArguments() != null) {
            recordsListId = RecordsListFragmentArgs.fromBundle(getArguments()).getRecordsListId();
        }

        if (recordsListId == null) {
            System.out.println("No records list was passed!");
            NavHostFragment.findNavController(RecordsListFragment.this).popBackStack();
        }
        else {
            _currRecordsList = _recordsListViewModel.getRecordsListById(recordsListId);

            if (_currRecordsList == null) {
                Log.d("LISTENER", "Illegal records list ID was passed!");
                _navController.popBackStack();
            }
            else {
                loadUsers(isSuccess -> {
                    if (isSuccess) {
                         _id.setText(_currRecordsList.getId());
                        _dateCreated.setText(Helpers.getDateString(_currRecordsList.getDateCreated()));

                        _isAdding = new MutableLiveData<>(false);
                        _isChanged = new MutableLiveData<>(false);

                        // Setting the adapter
                        _adapter = new RecordsListAdapter(_currRecordsList, _isAdding, _isChanged, this);
                        _records.setAdapter(_adapter);

                        _details.setText(_currRecordsList.getDetails() == null ? "" : (_currRecordsList.getDetails()));
                        _addRecordBtn.setOnClickListener(v -> {
                            // Only adding a record if the previous one isn't empty
                            if (_currRecordsList.getRecords().isEmpty() || !_currRecordsList.getRecords().get(_adapter.getItemCount() - 1).getText().isEmpty()) {
                                _currRecordsList.getRecords().add(new CheckedRecord("", false));
                                _isAdding.postValue(true);
                                _isChanged.setValue(true);
                            }
                        });
                    }
                });
            }
        }
    }

    @Override
    public void onStart() {
        super.onStart();

        // Here on onStart since onViewCreated is called pre activity's initialization
        Toolbar toolbar = requireActivity().findViewById(R.id.toolbar);

        if (toolbar != null) {
            toolbar.setTitle(_currRecordsList.getName());
            toolbar.setNavigationOnClickListener(_navigateBackListener);
        }
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();

        _navigateBackListener.setEnabled(false);
        _navigateBackListener.remove();
    }

    @Override
    public void onCreateOptionsMenu(@NonNull Menu menu, @NonNull MenuInflater inflater) {
        menu.clear();
        inflater.inflate(R.menu.menu_records_list, menu);

        super.onCreateOptionsMenu(menu, inflater);
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        if (item.getItemId() == R.id.action_save) {
            saveRecords();

            return true;
        }
        else if (item.getItemId() == R.id.action_revert) {
            discardChanges();

            return true;
        }
        else if (item.getItemId() == R.id.action_invite_user) {
            displayUserInviteDialog();

            return true;
        }
        else if (item.getItemId() == R.id.action_leave) {
            leaveRecordsList();

            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    // region Private Methods

    private void initViews(@NonNull View rootView) {
        _id = rootView.findViewById(R.id.list_id);
        _dateCreated = rootView.findViewById(R.id.list_created);
        _details = rootView.findViewById(R.id.list_details);
        _users = rootView.findViewById(R.id.list_users);
        _records = rootView.findViewById(R.id.records);
        _addRecordBtn = rootView.findViewById(R.id.add_record_btn);
        _loader = rootView.findViewById(R.id.loader);
    }

    private void promptSaveIfChanged() {
        if (_isChanged.getValue()) {
            AlertDialog.Builder builder = new AlertDialog.Builder(requireActivity());

            builder.setMessage(R.string.save_changes_prompt);
            builder.setPositiveButton(R.string.save, (dialog, which) -> {
                saveRecords();
                _navController.popBackStack();
            });

            builder.setNegativeButton(R.string.discard, (dialog, which) -> {
                discardChanges();
                _navController.popBackStack();
            });

            builder.create().show();
        }
        else {
            _navController.popBackStack();
        }
    }

    private void saveRecords() {
        if (_isChanged.getValue()) {
            // Remove last record if empty
            if (!_currRecordsList.getRecords().isEmpty() && _currRecordsList.getRecords().get(_adapter.getItemCount() - 1).getText().isEmpty()) {
                _currRecordsList.getRecords().remove(_adapter.getItemCount() - 1);
            }

            _loader.setVisibility(View.VISIBLE);
            _recordsListViewModel.setRecordsList(_currRecordsList, isSuccess -> {
                if (getActivity() != null && isAdded()) {
                    getActivity().runOnUiThread(() -> {
                        _loader.setVisibility(View.GONE);

                        Toast.makeText(getContext(), isSuccess ? R.string.successfully_saved : R.string.save_failed, Toast.LENGTH_SHORT).show();

                        // No longer in adding mode
                        _isAdding.postValue(false);
                        _isChanged.setValue(false);
                    });
                }
            });
        }
    }

    /**
     * Reverting changes made since last save
     */
    private void discardChanges() {
        if (_isChanged.getValue()) {
            // No longer in adding mode
            _isAdding.postValue(false);

            // Get the records list once again
            _currRecordsList.getRecords().clear();
            _currRecordsList.getRecords().addAll(_recordsListViewModel.getRecordsListById(_currRecordsList.getId()).getRecords());

            _adapter.notifyDataSetChanged();

            _isChanged.setValue(false);

            requireActivity().runOnUiThread(() -> Toast.makeText(getContext(), R.string.successfully_reverted, Toast.LENGTH_SHORT).show());
        }
    }

    // region Manage Users

    private void loadUsers(IOnCompleteListener listener) {
        _usersViewModel.getAllUsers(users -> requireActivity().runOnUiThread(() -> {
            _loader.setVisibility(View.INVISIBLE);

            if (users == null || users.isEmpty()) {
                listener.onComplete(false);

                Log.d("LISTENER", "Error no users init");
                Toast.makeText(getContext(), R.string.users_unavailable, Toast.LENGTH_SHORT).show();
            }
            else {
                List<User> invited = new ArrayList<>();

                // Get only uninvited users
                for (User user : users) {
                    if (_currRecordsList.getUserIds().contains(user.getId())) {
                        invited.add(user);
                    }
                }

                setUsersView(invited);

                listener.onComplete(true);
            }
        }));
    }

    /**
     * This method sets the list's users view
     */
    private void setUsersView(List<User> users) {
        StringBuilder stringBuilder = new StringBuilder();

        for (int userIndex = 0; userIndex < users.size(); userIndex++) {
            stringBuilder.append(users.get(userIndex).getName());

            if (userIndex < users.size() - 1) {
                stringBuilder.append(", ");
            }
        }

        _users.setText(stringBuilder.toString());
    }

    private void displayUserInviteDialog() {
        if (getContext() == null) {
            Log.d("LISTENER", "Error no context");
        }
        else {
            _loader.setVisibility(View.VISIBLE);

            _usersViewModel.getAllUsers(users -> requireActivity().runOnUiThread(() -> {
                _loader.setVisibility(View.INVISIBLE);

                if (users == null || users.isEmpty()) {
                    Log.d("LISTENER", "Error no users");
                    Toast.makeText(getContext(), R.string.users_unavailable, Toast.LENGTH_SHORT).show();
                }
                else {
                    List<User> uninvited = new ArrayList<>();
                    List<User> invited = new ArrayList<>();

                    // Get only uninvited users
                    for (User user : users) {
                        if (_currRecordsList.getUserIds().contains(user.getId())) {
                            invited.add(user);
                        }
                        else {
                            uninvited.add(user);
                        }
                    }

                    setUsersView(invited);

                    if (uninvited.isEmpty()) {
                        Toast.makeText(getContext(), R.string.all_users_invited, Toast.LENGTH_SHORT).show();
                    }
                    else {
                        Dialog usersDialog = new Dialog(getContext());
                        usersDialog.getWindow().requestFeature(Window.FEATURE_NO_TITLE);
                        usersDialog.setContentView(((LayoutInflater) getContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE))
                                .inflate(R.layout.dialog_users, null));

                        RecyclerView usersRv = usersDialog.findViewById(R.id.users);
                        usersRv.setLayoutManager(new LinearLayoutManager(getContext()));
                        UsersAdapter adapter = new UsersAdapter(uninvited, id -> {
                            _currRecordsList.getUserIds().add(id);
                            _isChanged.setValue(true);

                            loadUsers(isSuccess -> {});
                        });

                        usersRv.setAdapter(adapter);

                        usersDialog.show();
                    }
                }
            }));
        }
    }

    private void leaveRecordsList() {
        if (getContext() == null) {
            Log.d("LISTENER", "Error no context on leave");
        }
        else {
            AlertDialog.Builder builder = new AlertDialog.Builder(getContext());

            builder.setMessage(R.string.leave_list_prompt);
            builder.setPositiveButton(R.string.yes, (dialog, which) -> {
                _currRecordsList.getUserIds().remove(_usersViewModel.getLoggedUser().getValue().getId());
                _isChanged.setValue(true);

                saveRecords();

                _navController.popBackStack();
            });

            builder.setNegativeButton(R.string.no, (dialog, which) -> dialog.dismiss());

            builder.create().show();
        }
    }

    // endregion

    // endregion

    // region Image Helper

    @Override
    public void pickImage(IOnImageUploadedListener listener) {
        _attachmentName = _currRecordsList.getId() + "-" + System.currentTimeMillis();
        _imageUploadedListener = listener;
        _resultLauncher.launch("image/*");
    }

    @Override
    public void loadImage(String path, IOnImageLoadedListener listener) {
        _loader.setVisibility(View.VISIBLE);

        _recordsListViewModel.loadAttachment(path, loadedImage -> {
            if (getActivity() != null) {
                getActivity().runOnUiThread(() -> {
                    _loader.setVisibility(View.INVISIBLE);

                    listener.onLoaded(loadedImage);
                });
            }
        });
    }

    @Override
    public void deleteImage(String name, IOnCompleteListener listener) {
        _loader.setVisibility(View.VISIBLE);

        _recordsListViewModel.deleteAttachment(name, isSuccess -> {
            _loader.setVisibility(View.INVISIBLE);

            listener.onComplete(isSuccess);
        });
    }

    // endregion

    // region Listeners

    private class NavigateBackListener extends OnBackPressedCallback implements View.OnClickListener {
        public NavigateBackListener() {
            super(true);
        }

        @Override
        public void onClick(View v) {
            promptSaveIfChanged();
        }

        @Override
        public void handleOnBackPressed() {
            promptSaveIfChanged();
        }
    }

    // endregion

}