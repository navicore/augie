/**
 * copyright Ed Sweeney, 2012, 2013 all rights reserved
 */
package com.onextent.android.ui;

import java.util.List;
import android.content.Context;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemSelectedListener;
import android.widget.ArrayAdapter;
import android.widget.Spinner;
import android.widget.Toast;


public abstract class SpinnerUI<E> {

    private final List<E> choices;
    private final Spinner spinner;
    public SpinnerUI(Spinner spinner, List<E> choices) {
        this.spinner = spinner;
        this.choices = choices;
        if (choices ==null) spinner.setEnabled(false);
    }

    public void update(int pos) {
        if (choices == null) return;
        E newMode;
        if (pos == 0) { //<unset>
        newMode = null;
        } else {
            newMode = choices.get(pos);
        }
        setMode(newMode);
    }

    public void init() {
        
        if (choices == null) return;

        spinner.setAdapter(new SpnAdapter<E>(spinner.getContext(), choices));
        spinner.setSelection(calculatePos());
        spinner.setOnItemSelectedListener(new OnItemSelectedListener() {

            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                if (position == calculatePos()) return;
                try {
                    update(position);
                } catch (Exception e) {
                    Toast t = Toast.makeText(spinner.getContext(), e.getMessage(), Toast.LENGTH_SHORT);
                    t.show();
                }
            }
            @Override
            public void onNothingSelected(AdapterView<?> parent) { }
        });
    }

    /**
     * calculate (suggest) the pos of the already-selected item based
     * on the current setting in the camera
     */
    public abstract int calculatePos();

    public abstract void setMode(E m);
    
    private class SpnAdapter<T> extends ArrayAdapter<T> {

        SpnAdapter(Context context, List<T> list) {
            super(context, android.R.layout.simple_spinner_item, list);
        }
    }
}
