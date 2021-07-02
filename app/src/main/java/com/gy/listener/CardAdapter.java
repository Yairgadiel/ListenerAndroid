package com.gy.listener;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.lifecycle.LiveData;
import androidx.recyclerview.widget.RecyclerView;

import com.gy.listener.myLists.RecordsList;

import java.util.List;

public class CardAdapter extends RecyclerView.Adapter<CardAdapter.CardViewHolder> {

    private final LiveData<List<RecordsList>> _data;

    public CardAdapter( LiveData<List<RecordsList>> lists) {
        _data = lists;
    }

    @NonNull
    @Override
    public CardViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        // Create a new view, which defines the UI of the list item
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.fragment_my_lists_preview, parent, false);

        return new CardViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull CardViewHolder holder, int position) {
        RecordsList currList = _data.getValue().get(position);

        holder.setName(currList.getName());
        holder.setDetails(currList.getDetails());
        holder.setImage(1);
    }

    @Override
    public int getItemCount() {
        return _data.getValue() == null ? 0 : _data.getValue().size();
    }

    /**
     * Provide a reference to the type of views that you are using
     * (custom ViewHolder).
     */
    public static class CardViewHolder extends RecyclerView.ViewHolder {
        private final TextView _listName;
        private final TextView _listDetails;
        private final ImageView _listImg;

        public CardViewHolder(View view) {
            super(view);

            _listName = view.findViewById(R.id.list_prev_name);
            _listDetails = view.findViewById(R.id.list_prev_details);
            _listImg = view.findViewById(R.id.list_prev_img);
        }

        public void setName(String name) {
            _listName.setText(name);
        }

        public void setDetails(String details) {
            _listDetails.setText(details);
        }

        public void setImage(int imgId) {
            _listImg.setImageDrawable(_listImg.getContext().getResources().getDrawable(R.drawable.todo_list));
        }
    }
}
