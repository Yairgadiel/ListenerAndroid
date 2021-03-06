package com.gy.listener.ui.recordsList;

import android.app.Dialog;
import android.content.Context;
import android.content.res.ColorStateList;
import android.os.Build;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.inputmethod.EditorInfo;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.lifecycle.MutableLiveData;
import androidx.recyclerview.widget.RecyclerView;

import com.gy.listener.R;
import com.gy.listener.model.items.records.CheckedRecord;
import com.gy.listener.model.items.records.ListType;
import com.gy.listener.model.items.records.Record;
import com.gy.listener.model.items.records.RecordsList;
import com.squareup.picasso.Picasso;

import java.util.List;

public class RecordsListAdapter extends RecyclerView.Adapter<RecordsListAdapter.RecordViewHolder> {

    private static final int VIEW_TYPE_EDITABLE = 0;
    private static final int VIEW_TYPE_UNEDITABLE = 1;

    private final List<Record> _data;
    private final ListType _listType;
    private final MutableLiveData<Boolean> _isAdding;
    private final MutableLiveData<Boolean> _isChanged;

    private boolean _isEditing;
    private final IImageHelper _imageHelper;

    public RecordsListAdapter(@NonNull RecordsList recordsList,
                              @NonNull MutableLiveData<Boolean> isAdding,
                              @NonNull MutableLiveData<Boolean> isChanged,
                              IImageHelper imageHelper) {
        _data = recordsList.getRecords();
        _listType = recordsList.getListType();
        _isEditing = true;

        _isAdding = isAdding;
        _isChanged = isChanged;
        _isAdding.observeForever(b -> notifyDataSetChanged());
        _imageHelper = imageHelper;
    }

    @NonNull
    @Override
    public RecordViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        // Determine the todo

        // Create a new view, which defines the UI of the list item
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.checked_record_item, parent, false);

        return new RecordViewHolder(
                view,
                new RecordTextWatcher(),
                new RecordCheckedChangeListener(),
                new AttachmentClickListener());
    }

    @Override
    public int getItemViewType(int position) {
        return (_isEditing || (getItemCount() == position + 1 && _isAdding.getValue())) ? VIEW_TYPE_EDITABLE : VIEW_TYPE_UNEDITABLE;
    }

    @Override
    public void onBindViewHolder(@NonNull RecordViewHolder holder, int position) {
        CheckedRecord currRecord = (CheckedRecord) _data.get(position);

        holder.updateListenersPosition(position);
        holder.setText(currRecord.getText());
        holder.setIsChecked(currRecord.getIsChecked());
        holder.setViewType(getItemViewType(position));
    }

    @Override
    public int getItemCount() {
        return _data.size();
    }

    /**
     * Provide a reference to the type of views that you are using
     * (custom ViewHolder).
     */
    public static class RecordViewHolder extends RecyclerView.ViewHolder {
        private final EditText _recordText;
        private final CheckBox _recordIsChecked;
        private final ImageButton _attachmentBtn;

        private final RecordTextWatcher _editTextListener;
        private final RecordCheckedChangeListener _checkedChangeListener;
        private final AttachmentClickListener _attachmentClickListener;

        public RecordViewHolder(View view,
                                RecordTextWatcher editTextListener,
                                RecordCheckedChangeListener checkedChangeListener,
                                AttachmentClickListener clickListener) {
            super(view);

            _recordText = view.findViewById(R.id.record_text);
            _recordIsChecked = view.findViewById(R.id.record_is_checked);
            _attachmentBtn = view.findViewById(R.id.record_attachment);

            _editTextListener = editTextListener;
            _checkedChangeListener = checkedChangeListener;
            _attachmentClickListener = clickListener;

            _recordText.addTextChangedListener(_editTextListener);
            _recordIsChecked.setOnCheckedChangeListener(_checkedChangeListener);
            _attachmentBtn.setOnClickListener(_attachmentClickListener);
        }

        public void setText(String text) {
            _recordText.setText(text);
        }

        public void setIsChecked(boolean isChecked) {
            _recordIsChecked.setChecked(isChecked);
        }

        public void setViewType(int viewType) {
            if (viewType == VIEW_TYPE_EDITABLE) {
                _recordText.setInputType(EditorInfo.TYPE_CLASS_TEXT);

                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                    _recordText.setBackgroundTintList(ColorStateList.valueOf(_recordText.getContext().getColor(android.R.color.holo_blue_dark)));
                }
                else {
                    _recordText.setFocusable(true);
                    _recordText.setClickable(true);
                }

                _recordText.requestFocus();
            }
            else {
                _recordText.setInputType(EditorInfo.TYPE_NULL);

                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                    _recordText.setBackgroundTintList(ColorStateList.valueOf(_recordText.getContext().getColor(android.R.color.transparent)));
                }
                else {
                    _recordText.setFocusable(false);
                    _recordText.setClickable(false);
                }
            }
        }

        public void updateListenersPosition(int position) {
            _editTextListener.updatePosition(position);
            _checkedChangeListener.updatePosition(position);
            _attachmentClickListener.updatePosition(position);
        }
    }

    // region Custom listeners

    private class RecordCheckedChangeListener implements CompoundButton.OnCheckedChangeListener {
        private int _position;

        public void updatePosition(int position) {
            this._position = position;
        }

        @Override
        public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
            CheckedRecord currRecord = (CheckedRecord) _data.get(_position);

            // Checking if checked actually changed
            if (currRecord.getIsChecked() != isChecked) {
                ((CheckedRecord) _data.get(_position)).setIsChecked(isChecked);
                _isChanged.setValue(true);
            }
        }
    }

    // we make TextWatcher to be aware of the position it currently works with
    // this way, once a new item is attached in onBindViewHolder, it will
    // update current position MyCustomEditTextListener, reference to which is kept by ViewHolder
    private class RecordTextWatcher implements TextWatcher {
        private int _position;

        public void updatePosition(int position) {
            this._position = position;
        }

        @Override
        public void beforeTextChanged(CharSequence charSequence, int i, int i2, int i3) {
            // no op
        }

        @Override
        public void onTextChanged(CharSequence charSequence, int i, int i2, int i3) {
            Record currRecord = _data.get(_position);

            // Checking if text actually changed
            if (!charSequence.toString().equals(currRecord.getText())) {
                _data.get(_position).setText(charSequence.toString());
                _isChanged.setValue(true);
            }
        }

        @Override
        public void afterTextChanged(Editable editable) {
            // no op
        }
    }

    private class AttachmentClickListener implements View.OnClickListener {
        private int _position;

        public void updatePosition(int position) {
            this._position = position;
        }

        @Override
        public void onClick(View v) {
            Record currRecord = _data.get(_position);

            // No attachment
            if (currRecord.getImgPath() == null) {
                requestImage(v.getContext());
            }
            else {
                displayAttachmentDialog(v.getContext());
            }
        }

        private void requestImage(Context context) {
            _imageHelper.pickImage(path -> {
                if (path == null) {
                    Toast.makeText(context, R.string.unknown_error, Toast.LENGTH_SHORT).show();
                }
                else {
                    Record currRecord = _data.get(_position);

                    // Save image and display dialog
                    currRecord.setImgPath(path);

                    displayAttachmentDialog(context);

                    _isChanged.setValue(true);
                }
            });
        }

        private void displayAttachmentDialog(Context context) {
            Record currRecord = _data.get(_position);

            _imageHelper.loadImage(currRecord.getImgPath(), loadedImg -> {
                if (loadedImg == null) {
                    Toast.makeText(context, R.string.load_failed, Toast.LENGTH_SHORT).show();

                    // The image path is not null and not loadable. For now - delete it
                    currRecord.setImgPath(null);
                }
                else {
                    Dialog attachmentDialog = new Dialog(context);
                    attachmentDialog.getWindow().requestFeature(Window.FEATURE_NO_TITLE);
                    attachmentDialog.setContentView(((LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE))
                            .inflate(R.layout.dialog_attachment, null));

                    ImageView recordImage = attachmentDialog.findViewById(R.id.attachment_img);
                    recordImage.setImageBitmap(loadedImg);

                    Button removeBtn = attachmentDialog.findViewById(R.id.remove_btn);
                    removeBtn.setOnClickListener(v -> {
                        // Delete old photo
                        _imageHelper.deleteImage(currRecord.getImgPath(), isSuccess -> {
                            if (isSuccess) {
                                currRecord.setImgPath(null);
                                _isChanged.setValue(true);

                                Toast.makeText(context, R.string.remove_success, Toast.LENGTH_SHORT).show();
                            }
                            else {
                                Toast.makeText(context, R.string.remove_failed, Toast.LENGTH_SHORT).show();
                            }
                        });

                        attachmentDialog.dismiss();
                    });

                    Button replaceBtn = attachmentDialog.findViewById(R.id.replace_btn);
                    replaceBtn.setOnClickListener(v -> {
                        // Delete old photo
                        _imageHelper.deleteImage(currRecord.getImgPath(), isSuccess -> {
                            if (isSuccess) {
                                // Request a new image
                                requestImage(context);
                                attachmentDialog.dismiss();

                                currRecord.setImgPath(null);
                                _isChanged.setValue(true);
                            }
                            else {
                                Toast.makeText(context, R.string.remove_failed, Toast.LENGTH_SHORT).show();
                            }
                        });
                    });

                    attachmentDialog.show();
                }
            });
        }
    }

    // endregion
}
