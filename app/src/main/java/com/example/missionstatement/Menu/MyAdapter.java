package com.example.missionstatement.Menu;

import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.fragment.app.FragmentManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.missionstatement.Firebase.Storage;
import com.example.missionstatement.Fragment.FragmentProfile;
import com.example.missionstatement.R;
import com.example.missionstatement.VP.ViewPagerFragment;

import java.util.List;

public class MyAdapter extends RecyclerView.Adapter<MyAdapter.MyViewHolder> {
    private Storage storage;
    private List<FragmentProfile> data;
    private FragmentManager fragmentManager;

    public MyAdapter(List<FragmentProfile> data, FragmentManager fragmentManager) {
        this.data = data;
        this.fragmentManager = fragmentManager;
        this.storage = Storage.getInstance();
    }

    @NonNull
    @Override
    public MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.viewholder_profile, parent, false);
        return new MyViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull MyViewHolder holder, int position) {
        if (holder.container != null && holder.itemView != null) {
            String fragmentTag = "FRAGMENT_PROFILE_" + position;

            // Check if fragment is already added
            if (fragmentManager.findFragmentByTag(fragmentTag) == null) {
                fragmentManager.beginTransaction()
                        .add(holder.container.getId(), data.get(position), fragmentTag)
                        .commit();
            }

            holder.itemView.setOnTouchListener((view, motionEvent) -> {
                holder.bind(data.get(holder.getAdapterPosition()), fragmentManager, holder.getAdapterPosition());

                FragmentProfile fragmentProfile = data.get(holder.getAdapterPosition());
                storage.showImage(fragmentProfile.getProfiler(), fragmentProfile.getDeatils().get("email") + "jpeg.jpg", "OPERATOR");
                if (motionEvent.getAction() == MotionEvent.ACTION_UP) {
                    // Show the image on touch
                    showViewPager(holder.getAdapterPosition(),holder.container);

                }
                return true;
            });


        }
    }

    @Override
    public int getItemCount() {
        return data.size();
    }

    public static class MyViewHolder extends RecyclerView.ViewHolder {
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
                ((ViewGroup) fragmentProfile.getView().getParent()).removeView(fragmentProfile.getView());
            }
            fragmentProfile.putOperatorWithHash(fragmentProfile.getDeatils());
            container.addView(fragmentProfile.getView());
        }
    }

    private void showViewPager(int position,ViewGroup container) {
        String tag = "VIEW_PAGER_FRAGMENT"+position;
        ViewPagerFragment existingFragment = (ViewPagerFragment) fragmentManager.findFragmentByTag(tag);

        if (existingFragment == null || !existingFragment.isAdded()) {
            ViewPagerFragment viewPagerFragment = ViewPagerFragment.newInstance(data, position,container);
            fragmentManager.beginTransaction()
                    .replace(R.id.fragmentContainer, viewPagerFragment, tag)
                    .addToBackStack(null)
                    .commit();

        }

        // Hide the mirror view
    }
}
