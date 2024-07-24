package com.example.missionstatement.VP;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.fragment.app.Fragment;
import androidx.viewpager2.widget.ViewPager2;

import com.example.missionstatement.Fragment.FragmentProfile;
import com.example.missionstatement.R;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

import java.io.Serializable;
import java.util.List;

public class ViewPagerFragment extends Fragment {
    private List<FragmentProfile> data;
    private int initialPosition;

    public static ViewPagerFragment newInstance(List<FragmentProfile> data, int initialPosition) {
        ViewPagerFragment fragment = new ViewPagerFragment();
        Bundle args = new Bundle();
        args.putSerializable("data", (Serializable) data);
        args.putInt("initialPosition", initialPosition);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            data = (List<FragmentProfile>) getArguments().getSerializable("data");
            initialPosition = getArguments().getInt("initialPosition");
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.viewpager, container, false);

        ViewPager2 viewPager = view.findViewById(R.id.viewPager);
        FloatingActionButton fabClose = view.findViewById(R.id.VP_fab_close);
        FloatingActionButton fabNext = view.findViewById(R.id.VP_fab_next);

        ViewPagerAdapter adapter = new ViewPagerAdapter(this, data);
        viewPager.setAdapter(adapter);
        viewPager.setCurrentItem(initialPosition);

        fabClose.setOnClickListener(v -> getParentFragmentManager().popBackStack());
        // fabNext.setOnClickListener(v -> viewPager.setCurrentItem(viewPager.getCurrentItem() + 1));

        return view;
    }
}
