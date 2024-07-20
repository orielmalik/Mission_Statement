package com.example.missionstatement.Menu;

import android.annotation.SuppressLint;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;

import androidx.annotation.NonNull;
import androidx.fragment.app.FragmentManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.missionstatement.CallBackType.Callback_profile;
import com.example.missionstatement.Firebase.Storage;
import com.example.missionstatement.Fragment.FragmentProfile;
import com.example.missionstatement.R;

import java.util.HashMap;
import java.util.List;

public class MyAdapter extends RecyclerView.Adapter<MyAdapter.MyViewHolder> {
private Storage storage;
    private List<FragmentProfile> data; // Replace String with your actual data type
    private FragmentManager fragmentManager;
    private RecyclerViewInterface recyclerViewInterface;

    public MyAdapter(List<FragmentProfile> data, FragmentManager fragmentManager) {
        this.data = data;
        this.fragmentManager = fragmentManager;
        storage=Storage.getInstance();
    }

    @NonNull
    @Override
    public MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.viewholder_profile, parent, false);
        return new MyViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull MyViewHolder holder, int position) {
        // Bind data to UI elements in each item
        // For example: holder.textView.setText(data.get(position));
        if (holder.container != null && holder.itemView != null) {

            fragmentManager.beginTransaction()
                    .add(holder.container.getId(), data.get(position))
                    .commit();

            if (holder.itemView != null) {
                holder.itemView.setOnTouchListener(new View.OnTouchListener() {
                    @Override
                    public boolean onTouch(View view, MotionEvent motionEvent) {
                        holder.bind(data.get(holder.getAdapterPosition()), fragmentManager, holder.getAdapterPosition());
                        FragmentProfile fragmentProfile = data.get(holder.getAdapterPosition());
                        storage.showImage(fragmentProfile.getProfiler(),fragmentProfile.getDeatils().get("email")+"jpeg.jpg","OPERATOR");
                        return false;
                    }
                });
            }
            Log.d(null, "onBindViewHolder: " + position);

        }
    }

    @Override
    public int getItemCount() {
        return data.size();
    }

    public View getFragmentView(int position) {

        return data.get(0).getView();
    }


    public static class MyViewHolder extends RecyclerView.ViewHolder {
        // Add references to UI elements in each item
        // For example: TextView textView;
        ViewGroup container;

        public MyViewHolder(@NonNull View itemView) {
            super(itemView);
            container = itemView.findViewById(R.id.fragmentContainer);

        }

        public void bind(FragmentProfile fragmentProfile, FragmentManager fragmentManager, int position) {
            if (fragmentProfile.getView() == null) {
                return;
            }
            if (fragmentProfile.getView().getParent() != null) {
                // If the child view has a parent, remove it from the current parent
                ((ViewGroup) fragmentProfile.getView().getParent()).removeView(fragmentProfile.getView());
            }
            fragmentProfile.putOperatorWithHash(fragmentProfile.getDeatils());
            container.addView(fragmentProfile.getView());
        }
    }


}