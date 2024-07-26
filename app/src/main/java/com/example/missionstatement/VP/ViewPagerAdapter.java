package com.example.missionstatement.VP;

import android.view.View;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.viewpager2.adapter.FragmentStateAdapter;

import com.example.missionstatement.Fragment.FragmentProfile;

import java.util.List;

public class ViewPagerAdapter extends FragmentStateAdapter {
    private List<FragmentProfile> data;

    public ViewPagerAdapter(@NonNull Fragment fragment, List<FragmentProfile> data) {
        super(fragment);
        this.data = data;
    }

    @NonNull
    @Override
    public Fragment createFragment(int position) {
        return FragmentProfile.newInstance(data.get(position));
    }

    @Override
    public int getItemCount() {
        return data.size();
    }
}

