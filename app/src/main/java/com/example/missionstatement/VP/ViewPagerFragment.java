package com.example.missionstatement.VP;


import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.fragment.app.Fragment;
import androidx.viewpager2.widget.ViewPager2;

import com.example.missionstatement.Firebase.Realtime;
import com.example.missionstatement.Firebase.Storage;
import com.example.missionstatement.Fragment.FragmentProfile;
import com.example.missionstatement.R;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

public class ViewPagerFragment extends Fragment {
    private List<FragmentProfile> data;
    private int initialPosition;
    static int currentPage = 0;
    static int counter = 0;
    private FloatingActionButton fabClose,fabNext;


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
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.viewpager, container, false);

        ViewPager2 viewPager = view.findViewById(R.id.viewPager);
        fabClose = view.findViewById(R.id.VP_fab_close);
        fabNext = view.findViewById(R.id.VP_fab_next);
        ViewPagerAdapter adapter = new ViewPagerAdapter(this, data);
        viewPager.setAdapter(adapter);
        viewPager.setCurrentItem(initialPosition);

        viewPager.registerOnPageChangeCallback(new ViewPager2.OnPageChangeCallback() {
            @Override
            public void onPageSelected(int position) {
                super.onPageSelected(position);
                currentPage = position;
                Log.d("ViewPagerFragment", "Current position: " + position);
                Storage.getInstance().showImage(data.get(position).getProfiler(), data.get(position).getDeatils().get("email") + "jpeg.jpg", "OPERATOR");

                // Bring ViewPager to the front
                if (container != null) {
                    container.bringChildToFront(viewPager);
                }
                counter++;
            }
        });

        fabClose.setOnClickListener(v -> {
            getParentFragmentManager().popBackStack();
        });

        fabNext.setOnClickListener(v -> {
            Realtime server = new Realtime(view.getContext());
            List<String> list = new ArrayList<>();
            server.checkDataSnapshotnew("OPERATOR" + "/" + data.get(currentPage).getDeatils().get("PhoneNumber")).thenAccept(big -> {
                if (big.containsKey("clients")) {
                    list.addAll((List<String>) big.get("clients"));
                }
                if(!list.contains(data.get(currentPage).getDeatils().get("user")))
                {
                    list.add(data.get(currentPage).getDeatils().get("user"));
                    server.getmDatabase().child("OPERATOR").child(data.get(currentPage).getDeatils().get("PhoneNumber")).child("clients").setValue(list);
                    Toast.makeText(v.getContext(), "Added to request",Toast.LENGTH_SHORT);
                    Log.d("tag",big.keySet().toString()+" cli "+big.values().toString());
                }else {
                    Toast.makeText(view.getContext(), "COMPLETE",Toast.LENGTH_SHORT);
                }

            });

        });

        return view;
    }

    public FloatingActionButton getFabClose() {
        return fabClose;
    }

    public FloatingActionButton getFabNext() {
        return fabNext;
    }
}
