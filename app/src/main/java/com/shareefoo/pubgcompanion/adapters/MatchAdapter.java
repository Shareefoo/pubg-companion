package com.shareefoo.pubgcompanion.adapters;

import android.content.Context;
import android.database.Cursor;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.shareefoo.pubgcompanion.R;
import com.shareefoo.pubgcompanion.model.match.AttributesStats;
import com.shareefoo.pubgcompanion.provider.MatchContract;

import java.text.DecimalFormat;
import java.util.List;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import butterknife.BindView;
import butterknife.ButterKnife;

public class MatchAdapter extends RecyclerView.Adapter<MatchAdapter.ViewHolder> {

    private Context mContext;
    private Cursor mCursor;

//    private List<AttributesStats> mStats;

    public MatchAdapter(Context context, Cursor cursor) {
        mContext = context;
        mCursor = cursor;
    }

    @NonNull
    @Override
    public MatchAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        Context context = parent.getContext();
        LayoutInflater inflater = LayoutInflater.from(context);

        // Inflate the custom layout
        View matchView = inflater.inflate(R.layout.item_match, parent, false);

        // Returns a new holder instance
        return new ViewHolder(matchView);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        // Get the data model based on position
//        AttributesStats stats = mStats.get(position);

        mCursor.moveToPosition(position);

        int id = mCursor.getInt(mCursor.getColumnIndex(MatchContract.MatchEntry._ID));
        int rank = mCursor.getInt(mCursor.getColumnIndex(MatchContract.MatchEntry.COLUMN_PLACEMENT));
        int total = mCursor.getInt(mCursor.getColumnIndex(MatchContract.MatchEntry.COLUMN_TOTAL));
        int kills = mCursor.getInt(mCursor.getColumnIndex(MatchContract.MatchEntry.COLUMN_KILLS));
        double damage = mCursor.getDouble(mCursor.getColumnIndex(MatchContract.MatchEntry.COLUMN_DAMAGE));
        double distance = mCursor.getDouble(mCursor.getColumnIndex(MatchContract.MatchEntry.COLUMN_DISTANCE));
        String mode = mCursor.getString(mCursor.getColumnIndex(MatchContract.MatchEntry.COLUMN_MODE));

        DecimalFormat df = new DecimalFormat("###.##");

        String placement = "#" + rank;

        if (rank == 1) {
            holder.textViewPlacements.setTextColor(mContext.getResources().getColor(R.color.colorWins));
        } else if (rank > 1 && rank <= 10) {
            holder.textViewPlacements.setTextColor(mContext.getResources().getColor(R.color.colorTop10));
        } else if (rank > 10 && rank <= 100) {
            holder.textViewPlacements.setTextColor(mContext.getResources().getColor(R.color.colorGames));
        }

        // Set item views based on views and data model
        holder.textViewPlacements.setText(placement);
        holder.textViewTotal.setText("/" + total);
        holder.textViewKills.setText(String.valueOf(kills));
        holder.textViewDamage.setText(df.format(damage));
        holder.textViewDistance.setText(String.format("%s km", df.format(distance)));

        switch (mode) {
            case "solo":
            case "solo-fpp":
                holder.imageViewMode.setImageResource(R.drawable.mode_solo);
                break;
            case "duo":
            case "duo-fpp":
                holder.imageViewMode.setImageResource(R.drawable.mode_duo);
                break;
            case "squad":
            case "squad-fpp":
                holder.imageViewMode.setImageResource(R.drawable.mode_squad);
                break;
        }

    }

    @Override
    public int getItemCount() {
        if (mCursor == null) return 0;
        return mCursor.getCount();
    }

    public void swapCursor(Cursor newCursor) {
        if (mCursor != null) {
            mCursor.close();
        }
        mCursor = newCursor;
        if (mCursor != null) {
            // Force the RecyclerView to refresh
            this.notifyDataSetChanged();
        }
    }

    static class ViewHolder extends RecyclerView.ViewHolder {

        @BindView(R.id.textView_placement)
        TextView textViewPlacements;

        @BindView(R.id.textView_total)
        TextView textViewTotal;

        @BindView(R.id.textView_kills)
        TextView textViewKills;

        @BindView(R.id.textView_damage)
        TextView textViewDamage;

        @BindView(R.id.textView_distance)
        TextView textViewDistance;

        @BindView(R.id.imageView_mode)
        ImageView imageViewMode;

        ViewHolder(View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);
        }
    }

}
