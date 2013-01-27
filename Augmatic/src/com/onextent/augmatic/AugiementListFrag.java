package com.onextent.augmatic;

import android.os.Bundle;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import com.actionbarsherlock.app.SherlockListFragment;

public class AugiementListFrag extends SherlockListFragment {

    int mCurCheckPosition = 0;
    
    AugiementListHelper helper;

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        
        helper = new AugiementListHelper((AugiementSettingsActivity) getSherlockActivity());

        helper.init();

        setListAdapter(
                new ArrayAdapter<String>(
                        getActivity(), 
                        android.R.layout.simple_list_item_1, 
                        helper.getItems()
                        ) {
                            @Override
                            public View getView(int position, View convertView,
                                    ViewGroup parent) {

                                View v = super.getView(position, convertView, parent);
                                
                                v.setBackgroundResource(R.drawable.row_selector);
                                
                                return v;
                            }
                        }
                      );

        if (savedInstanceState != null) {
            // Restore last state for checked position.
            mCurCheckPosition = savedInstanceState.getInt("curChoice", 0);
        }

        if (helper.isDualPane()) {
            getListView().setChoiceMode(ListView.CHOICE_MODE_SINGLE);
            helper.initDialogs(mCurCheckPosition);
            //helper.showDetails(mCurCheckPosition);
        }
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putInt("curChoice", mCurCheckPosition);
    }

    @Override
    public void onListItemClick(ListView l, View v, final int position, long id) {
        
        helper.initDialogs(position);

        //helper.showDetails(position);
    }
}
