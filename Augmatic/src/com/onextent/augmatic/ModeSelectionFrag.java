package com.onextent.augmatic;

import java.util.ArrayList;
import java.util.List;

import com.actionbarsherlock.app.SherlockListFragment;
import com.onextent.augie.AugieActivity;
import com.onextent.augie.AugieException;
import com.onextent.augie.AugieStoreException;
import com.onextent.augie.Mode;
import com.onextent.augie.ModeManager;
import com.onextent.android.codeable.Code;
import com.onextent.android.codeable.Codeable;
import com.onextent.android.codeable.CodeableException;
import com.onextent.android.codeable.CodeableName;

import android.os.Bundle;
import android.support.v4.app.FragmentTransaction;
import android.util.Log;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.ListView;

public class ModeSelectionFrag extends SherlockListFragment {


    List<Code> modeCode = new ArrayList<Code>();

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        try {
            List<String> names = new ArrayList<String>();
            ModeManager modeManager = ((AugieActivity) getActivity()).getModeManager();
            modeCode = modeManager.getAllModeCode();
            String currentModeUIName = modeManager.getCurrentMode().getName();
            Log.d(Codeable.TAG, "ejs cur ui name " + currentModeUIName);
            int currentCameraIdPos = -1;
            for (Code c : modeCode) {
                String n = c.getString(Codeable.UI_NAME_KEY);
                Log.d(Codeable.TAG, "ejs adding name " + n);
                names.add(n);
            }
            currentCameraIdPos = names.indexOf(currentModeUIName); 

            String[] items = new String[names.size()];
            names.toArray(items);

            setListAdapter(
                    new ArrayAdapter<String>(
                            getActivity(), 
                            R.layout.item, 
                            items));
            getListView().setChoiceMode(ListView.CHOICE_MODE_SINGLE);
            getListView().setItemChecked(currentCameraIdPos, true);

            updateUI();
        } catch (CodeableException e) {
            Log.e(Codeable.TAG, e.toString(), e);
        } catch (AugieStoreException e) {
            Log.e(Codeable.TAG, e.toString(), e);
        }
    }

    private void updateUI() {

        FragmentTransaction txn = getActivity().getSupportFragmentManager().beginTransaction();
        AugiementListFrag settingsList = new AugiementListFrag();
        txn.replace(R.id.module_list, settingsList);
        txn.commit();
    }

    @Override
    public void onListItemClick(ListView l, View v, int position, long id) {

        Code code = modeCode.get(position);

        CodeableName modeName;
        try {
            modeName = code.getCodeableName(Codeable.CODEABLE_NAME_KEY);
            setMode(modeName);
            updateUI();
        } catch (CodeableException e) {
            Log.e(Codeable.TAG, e.toString(), e);
        } catch (AugieException e) {
            Log.e(Codeable.TAG, e.toString(), e);
        }
    }

    private void setMode(CodeableName cn) throws CodeableException, AugieException {

        ModeManager modeManager = ((AugieActivity) getActivity()).getModeManager();
        Mode m = modeManager.getMode(cn);
        if (m == null) throw new AugieException("mode not found") {
            private static final long serialVersionUID = -6373280937418946550L;
        };
        modeManager.setCurrentMode(m);
    }
}