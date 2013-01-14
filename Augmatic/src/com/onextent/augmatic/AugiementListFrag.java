package com.onextent.augmatic;

import com.actionbarsherlock.app.SherlockListFragment;

import android.os.Bundle;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.ListView;

public class AugiementListFrag extends SherlockListFragment {

    int mCurCheckPosition = 0;
    
    AugiementListHelper helper;

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        
        helper = new AugiementListHelper(getSherlockActivity());

        helper.init();

        setListAdapter(
                new ArrayAdapter<String>(
                        getActivity(), 
                        R.layout.item, 
                        helper.getItems()
                        )
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