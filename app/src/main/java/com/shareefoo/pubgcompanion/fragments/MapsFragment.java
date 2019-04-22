package com.shareefoo.pubgcompanion.fragments;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.shareefoo.pubgcompanion.R;
import com.shareefoo.pubgcompanion.activities.FullMapActivity;
import com.shareefoo.pubgcompanion.adapters.MapAdapter;
import com.shareefoo.pubgcompanion.model.Map;

import java.util.ArrayList;
import java.util.List;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import butterknife.BindView;
import butterknife.ButterKnife;

public class MapsFragment extends Fragment {

    @BindView(R.id.rv_maps)
    RecyclerView recyclerViewMaps;

    public MapsFragment() {
        // Required empty public constructor
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View rootView = inflater.inflate(R.layout.fragment_maps, container, false);
        ButterKnife.bind(this, rootView);

        Bitmap mapErangel = BitmapFactory.decodeResource(getResources(), R.drawable.map_erangel);
        Bitmap mapMiramar = BitmapFactory.decodeResource(getResources(), R.drawable.map_miramar);
        Bitmap mapSanhok = BitmapFactory.decodeResource(getResources(), R.drawable.map_savage);
        Bitmap mapVikendi = BitmapFactory.decodeResource(getResources(), R.drawable.map_vikendi);

        List<Map> maps = new ArrayList<>();
        maps.add(new Map("Erangel", mapErangel));
        maps.add(new Map("Miramar", mapMiramar));
        maps.add(new Map("Sanhok", mapSanhok));
        maps.add(new Map("Vikendi", mapVikendi));

        recyclerViewMaps.setLayoutManager(new LinearLayoutManager(getContext()));
        recyclerViewMaps.setAdapter(new MapAdapter(maps, new MapAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(Map map) {
                Intent intent = new Intent(getContext(), FullMapActivity.class);
                intent.putExtra("map_name", map.getMapName());
                startActivity(intent);
            }
        }));

        return rootView;
    }

}
