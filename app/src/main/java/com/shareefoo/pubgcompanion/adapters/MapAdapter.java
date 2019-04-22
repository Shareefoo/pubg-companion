package com.shareefoo.pubgcompanion.adapters;

import android.content.Context;
import android.database.Cursor;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.shareefoo.pubgcompanion.R;
import com.shareefoo.pubgcompanion.model.Map;
import com.shareefoo.pubgcompanion.provider.MatchContract;

import java.text.DecimalFormat;
import java.util.List;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import butterknife.BindView;
import butterknife.ButterKnife;

public class MapAdapter extends RecyclerView.Adapter<MapAdapter.ViewHolder> {

    public interface OnItemClickListener {
        void onItemClick(Map map);
    }

    private List<Map> mMaps;
    private OnItemClickListener mListener;

    public MapAdapter(List<Map> maps, OnItemClickListener listener) {
        mMaps = maps;
        mListener = listener;
    }

    @NonNull
    @Override
    public MapAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        Context context = parent.getContext();
        LayoutInflater inflater = LayoutInflater.from(context);

        // Inflate the custom layout
        View matchView = inflater.inflate(R.layout.item_map, parent, false);

        // Returns a new holder instance
        return new ViewHolder(matchView);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        // Get the data model based on position
        final Map map = mMaps.get(position);

        holder.imageViewMapPicture.setImageBitmap(map.getMapPicture());
        holder.textViewMapName.setText(map.getMapName());

        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mListener.onItemClick(map);
            }
        });
    }

    @Override
    public int getItemCount() {
        return mMaps.size();
    }

    static class ViewHolder extends RecyclerView.ViewHolder {

        @BindView(R.id.iv_map_picture)
        ImageView imageViewMapPicture;

        @BindView(R.id.tv_map_name)
        TextView textViewMapName;

        ViewHolder(View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);
        }
    }

}
