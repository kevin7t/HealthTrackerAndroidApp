package kevin.androidhealthtracker.fragments;

import android.app.Fragment;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AbsListView;
import android.widget.ArrayAdapter;
import android.widget.ListAdapter;
import android.widget.ListView;

import java.util.Arrays;

import kevin.androidhealthtracker.R;

public class FragmentHome extends Fragment {
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_home, container, false);
        final FloatingActionButton floatingActionButton = view.findViewById(R.id.fab);

        ListView listView = view.findViewById(R.id.fragment_home_list);
        ListAdapter listAdapter = new ArrayAdapter<>(getActivity(), R.layout.list_item, Arrays.asList(new String[]{"Test item 1 ", "Test item 2", "Test item 3", "Test item 4", "Test item 5", "Test item 6", "Test item 7"}));
        listView.setAdapter(listAdapter);

        //Add new fitness update
        floatingActionButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
                        .setAction("Action", null).show();
            }
        });

        listView.setOnScrollListener(new AbsListView.OnScrollListener() {
            @Override
            public void onScrollStateChanged(AbsListView absListView, int i) {
            }

            @Override
            public void onScroll(AbsListView absListView, int firstVisibleItem, int visibleItemCount, int totalItemCount) {
                if (firstVisibleItem > 0) {
                    floatingActionButton.hide();
                }
                if (firstVisibleItem == 0) {
                    floatingActionButton.show();
                }
            }
        });

        return view;
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {

    }

}
