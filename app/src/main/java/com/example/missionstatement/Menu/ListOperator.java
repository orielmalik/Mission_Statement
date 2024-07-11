package com.example.missionstatement.Menu;
import android.os.Bundle;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.PopupMenu;
import android.widget.Toast;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.SearchView;
import androidx.recyclerview.widget.RecyclerView;

import com.example.missionstatement.R;

public class ListOperator extends AppCompatActivity {

    private RecyclerView recyclerView;
    private SearchView searchView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_list_operator);

        recyclerView = findViewById(R.id.lst_operator);
        searchView = findViewById(R.id.searchView_LST);

        // Set up SearchView
        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                performSearch(query);
                return true;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                return false;
            }
        });

        // Show popup menu when the SearchView is clicked
        searchView.setOnSearchClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showPopupMenu(v);
            }
        });

        searchView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showPopupMenu(v);
            }
        });
    }

    private void performSearch(String query) {
        // Perform search action here
        // For example, filter the RecyclerView based on the query
        Toast.makeText(this, "Searching for: " + query, Toast.LENGTH_SHORT).show();
    }

    private void showPopupMenu(View view) {
        PopupMenu popup = new PopupMenu(this, view);
        MenuInflater inflater = popup.getMenuInflater();
        inflater.inflate(R.menu.popu_menu, popup.getMenu());
        int[]id={R.id.menu_item1,R.id.menu_item2,R.id.menu_item3,R.id.menu_item4,R.id.menu_item5};

        int[]Operatoricon={R.drawable.ic_locat,R.drawable.ic_heartt,R.drawable.ic_validate,R.drawable.ic_google,R.drawable.ic_smiley,R.drawable.ic_operatorseven};
        for (int i = 0; i <id.length ; i++) {
            popup.getMenu().findItem(id[i]).setIcon(Operatoricon[i]);
        }

        popup.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(MenuItem item) {
                boolean ok=false;
getIconAction(id,item,Operatoricon);










                return  ok;
            }
        });
        popup.show();
    }

    private void getIconAction(int[] id, MenuItem item,int[]op) {
        for (int i = 0; i < id.length; i++) {
            if(id[i]==item.getItemId())
            {
                doSwitch(op[i],id);
            }
        }

    }

    private void doSwitch(int i,int[]id) {
        switch (i)
        {
            case id[0]:

        }

    }


}
