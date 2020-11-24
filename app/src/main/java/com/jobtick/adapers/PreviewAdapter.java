package com.jobtick.adapers;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.jobtick.R;
import com.jobtick.models.PreviewModel;

import java.util.List;

public class PreviewAdapter extends RecyclerView.Adapter<BaseViewHolder> {

    private final List<PreviewModel> items;
    private OnItemClickListener<PreviewModel> mOnItemClickListener;

    public interface OnItemClickListener<PreviewModel> {
        void onItemClick(View view, PreviewModel obj, int position);
    }

    public void setOnItemClickListener(final OnItemClickListener<PreviewModel> mItemClickListener) {
        this.mOnItemClickListener = mItemClickListener;
    }

    public PreviewAdapter(List<PreviewModel> items) {
        this.items = items;
    }

    public class OriginalViewHolder extends BaseViewHolder {

        TextView txtName;
        RelativeLayout lytOuter;

        public OriginalViewHolder(View v) {
            super(v);
            txtName = v.findViewById(R.id.category_text);
            lytOuter = v.findViewById(R.id.category_container);
        }

        @Override
        protected void clear() {

        }

        public void onBind(int position) {
            super.onBind(position);
            txtName.setText(items.get(position).getTitle());

            lytOuter.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (mOnItemClickListener != null) {
                        mOnItemClickListener.onItemClick(v, items.get(position), position);
                    }
                }
            });
        }


    }


    @NonNull
    @Override
    public BaseViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {

        return new PreviewAdapter.OriginalViewHolder(
                LayoutInflater.from(parent.getContext()).inflate(R.layout.item_category_search, parent, false));

    }


    @Override
    public void onBindViewHolder(@NonNull BaseViewHolder holder, int position) {
        holder.onBind(position);
    }


    @Override
    public int getItemCount() {
        return items.size();
    }

    public void addItems(List<PreviewModel> mItems) {
        this.items.addAll(mItems);
        notifyDataSetChanged();
    }


    PreviewModel getItem(int position) {
        return items.get(position);
    }

    public class ProgressHolder extends BaseViewHolder {
        ProgressHolder(View itemView) {
            super(itemView);
        }

        @Override
        protected void clear() {
        }
    }


}