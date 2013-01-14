package com.onextent.augmatic.camera;

import com.actionbarsherlock.app.SherlockDialogFragment;
import com.actionbarsherlock.app.SherlockFragmentActivity;
import com.actionbarsherlock.app.SherlockListFragment;
import com.onextent.augmatic.R;
import com.onextent.android.codeable.Codeable;

import android.os.Bundle;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.util.Log;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.ListView;

public class CameraSettingsListFrag extends SherlockListFragment {

    final String[] items = {"Processing", "Image File", "Shooting"};
    boolean isDualPane;
    int mCurCheckPosition = 0;

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        setListAdapter(new ArrayAdapter<String>(getActivity(), R.layout.item, items));
        // android.R.layout.simple_list_item_activated_1, items));

        // Check to see if we have a frame in which to embed the details
        // fragment directly in the containing UI.
        View detailsFrame = getActivity().findViewById(R.id.module_details);
        isDualPane = detailsFrame != null;

        if (savedInstanceState != null) {
            // Restore last state for checked position.
            mCurCheckPosition = savedInstanceState.getInt("curChoice", 0);
        }

        if (isDualPane) {
            getListView().setChoiceMode(ListView.CHOICE_MODE_SINGLE);
            showDetails(mCurCheckPosition);
        }
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putInt("curChoice", mCurCheckPosition);
    }

    @Override
    public void onListItemClick(ListView l, View v, int position, long id) {
        showDetails(position);
    }

    /**
     * Helper function to show the details of a selected item, either by
     * displaying a fragment in-place in the current UI, or starting a
     * whole new activity in which it is displayed.
     */
    void showDetails(int index) {
        try {
            mCurCheckPosition = index;

            // We can display everything in-place with fragments, so update
            // the list to highlight the selected item and show the data.
            getListView().setItemChecked(index, true);

            FragmentManager fm = ((SherlockFragmentActivity)getActivity()).getSupportFragmentManager();
            SherlockDialogFragment f = null;
            switch (index) {
            case 0:
                f = new ProcessingSettingsDialog();
                break;
            case 1:
                f = new ImageSettingsDialog();
                break;
            case 2:
                f = new ShootingSettingsDialog();
                break;
            default:
            }
            if (f != null) {
                if (isDualPane) {
                    FragmentTransaction ft = getActivity().getSupportFragmentManager().beginTransaction();
                    ft.replace(R.id.module_details, f);
                    ft.setTransition(FragmentTransaction.TRANSIT_FRAGMENT_FADE);
                    ft.commit();

                } else {
                    f.show(fm, "Camera Settings");
                    f.show((getActivity()).getSupportFragmentManager(), "Camera Settings");
                }
            }
        } catch (Throwable err) {
            Log.e(Codeable.TAG, err.toString(), err);
        }
    }
}