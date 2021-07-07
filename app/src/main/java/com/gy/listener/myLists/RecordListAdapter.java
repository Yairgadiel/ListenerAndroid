package com.gy.listener.myLists;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.ImageButton;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.gy.listener.R;
import com.gy.listener.myLists.items.CheckedRecord;
import com.gy.listener.myLists.items.ListType;
import com.gy.listener.myLists.items.Record;
import com.gy.listener.myLists.items.RecordsList;

import java.util.List;

public class RecordListAdapter extends RecyclerView.Adapter<RecordListAdapter.RecordViewHolder> {

    private final List<Record> _data;
    private final ListType _listType;

    public RecordListAdapter(@NonNull RecordsList recordsList) {
        _data = recordsList.getRecords();
        _listType = recordsList.getListType();
    }

    @NonNull
    @Override
    public RecordViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        // Determine the todo

        // Create a new view, which defines the UI of the list item
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.checked_record_item, parent, false);

        return new RecordViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull RecordViewHolder holder, int position) {
        CheckedRecord currRecord = (CheckedRecord) _data.get(position);

        holder.setText(currRecord.getText());
        holder.setIsChecked(currRecord.getIsChecked());
        holder.setCheckedChangedListener((buttonView, isChecked) -> currRecord.setIsChecked(isChecked));
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

        public RecordViewHolder(View view) {
            super(view);

            _recordText = view.findViewById(R.id.record_text);
            _recordIsChecked = view.findViewById(R.id.record_is_checked);
            _attachmentBtn = view.findViewById(R.id.record_attachment);
        }

        public void setText(String text) {
            _recordText.setText(text);
        }

        public void setIsChecked(boolean isChecked) {
            _recordIsChecked.setChecked(isChecked);
        }

        public void setCheckedChangedListener(CompoundButton.OnCheckedChangeListener listener) {
            _recordIsChecked.setOnCheckedChangeListener(listener);
        }

        public void setImage(int imgId) {
            _attachmentBtn.setImageDrawable(_attachmentBtn.getContext().getResources().getDrawable(R.drawable.todo_list));
        }
    }
}
