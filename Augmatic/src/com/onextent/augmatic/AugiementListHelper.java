package com.onextent.augmatic;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Set;

import android.app.Dialog;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.CompoundButton.OnCheckedChangeListener;
import android.widget.TextView;

import com.actionbarsherlock.app.SherlockDialogFragment;
import com.actionbarsherlock.app.SherlockFragmentActivity;
import com.onextent.android.codeable.Codeable;
import com.onextent.android.codeable.CodeableName;
import com.onextent.augie.AugLog;
import com.onextent.augie.AugieActivity;
import com.onextent.augie.AugieException;
import com.onextent.augie.Augiement;
import com.onextent.augie.Augiement.Meta;
import com.onextent.augie.AugiementFactory;
import com.onextent.augie.Mode;
import com.onextent.augie.ModeManager;
import com.onextent.augmatic.camera.EmptySettingsDialog;

public class AugiementListHelper {

    protected boolean isDualPane;
    protected Map<CodeableName, Augiement> modeAugiements;
    protected Map<CodeableName, Augiement.Meta> allAugiements;
    protected List<CodeableName> cnList;
    protected String[] items;
    private final SherlockFragmentActivity activity;

    public AugiementListHelper(SherlockFragmentActivity activity) {
        this.activity = activity;
    }

    public void init() {
        
        // Check to see if we have a frame in which to embed the details
        // fragment directly in the containing UI.
        View detailsFrame = activity.findViewById(R.id.module_details);
        isDualPane = detailsFrame != null;

        ModeManager modeManager = ((AugieActivity) activity).getModeManager();
        Mode mode = modeManager.getCurrentMode();
        modeAugiements = mode.getAugiements();
        allAugiements = modeManager.getAugiementFactory().getAllMeta();

        List<String> itemList = new ArrayList<String>();
        cnList = new ArrayList<CodeableName>();
        for (Augiement.Meta m : allAugiements.values()) {
            itemList.add(m.getUIName());
            cnList.add(m.getCodeableName());
        }

        items = new String[itemList.size()];
        itemList.toArray(items);
    }

    public void initDialogs(final int position) {

        DialogFragment f = new SherlockDialogFragment() {

            @Override
            public View onCreateView(LayoutInflater inflater,
                    ViewGroup container, Bundle savedInstanceState) {

                Dialog d = getDialog();
                if (d != null) d.setTitle("Enable Augiement");
                View v = inflater.inflate(R.layout.module_status, container, false);
                CheckBox cbox = (CheckBox) v.findViewById(R.id.module_enabled);

                final CodeableName cn = cnList.get(position);

                boolean isEnabled = modeAugiements.containsKey(cn);
                cbox.setChecked(isEnabled);
                updateButtonText(cbox, cn, isEnabled);
                updateStatusText(v, cn);
                boolean isRequired = updateDepText(v, cn);
                if (isEnabled && isRequired)
                	cbox.setEnabled(false);

                cbox.setOnCheckedChangeListener(new OnCheckedChangeListener() {

                    @Override
                    public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {

                        ModeManager modeManager = ((AugieActivity) getActivity()).getModeManager();
                        AugiementFactory af = modeManager.getAugiementFactory();
                        Mode mode = modeManager.getCurrentMode();
                        if (isChecked) {
                            Augiement a = af.newInstance(cn);
                            mode.addAugiement(a);
                            updateButtonText(buttonView, cn, isChecked);
                        } else {
                            Augiement a = modeAugiements.get(cn);
                            mode.removeAugiement(a);
                        }
                        try {
                            modeManager.setCurrentMode(mode);
                        } catch (AugieException e) {
                            AugLog.e( e.toString(), e);
                        } //reset everything with new a
                    }
                });

                return v;
            }

        };
        
        FragmentManager fm = ((SherlockFragmentActivity)activity).getSupportFragmentManager();
        if (f != null) {
            if (isDualPane) {
                FragmentTransaction ft = activity.getSupportFragmentManager().beginTransaction();
                ft.replace(R.id.module_status, f);
                ft.setTransition(FragmentTransaction.TRANSIT_FRAGMENT_FADE);
                ft.commit();
                showDetails(position);

            } else {
                f.show(fm, "Module Status");
                f.show((activity).getSupportFragmentManager(), "Module Status");
                //todo: add details button to show details
            }
        }
    }

    private boolean updateDepText(View v, CodeableName cn) {

        TextView deps = (TextView) v.findViewById(R.id.module_dependencies);
        TextView reqs = (TextView) v.findViewById(R.id.module_required_by);
        Augiement.Meta m = allAugiements.get(cn);
        deps.setText(getDepsUINames(m));
        List<Meta> rm = getRequiredByMeta(m);
        reqs.setText(getRequiredByUINames(rm));
        return rm != null;
    }
    
    private void updateStatusText(View v, CodeableName cn) {

        TextView desc = (TextView) v.findViewById(R.id.module_description);
        Augiement.Meta m = allAugiements.get(cn);
        desc.setText(m.getDescription());
    }
    
    private List<Meta> getRequiredByMeta(Meta m) {
    	
    	if (m == null) return null;
        
    	CodeableName cn = m.getCodeableName();
    	
    	List<Meta> ret = new ArrayList<Meta>();
        
        for (Meta am : allAugiements.values()) {
           
        	Set<CodeableName> cnames = am.getDependencyNames();
        	if (cnames == null) continue;
            for (CodeableName dm : cnames)
            	if (dm.equals(cn)) ret.add(am);
        }
        if (ret.size() == 0) return null;
    
        return ret;
    }
    
    private CharSequence getRequiredByUINames(List<Meta> mlist) {
        
    	if (mlist == null) return null;
    	
        String ret = "";
        
        for (Meta am : mlist) {
            
            if (ret.length() > 0) ret += ", ";
          
            ret += am.getUIName();
        }
        if (ret.length() == 0) return null;
    
        return "required by " + ret;
    }
    
    private CharSequence getDepsUINames(Meta m) {
        
        Set<CodeableName> cnames = m.getDependencyNames();
        if (cnames == null) return null;
        
        String ret = "";
        
        for (CodeableName cn : cnames) {
            
            Meta dm = allAugiements.get(cn);

            if (dm == null) {
            	Log.w(Codeable.TAG, "getDepsDesc looking for unknown augiement: " + dm);
            	continue;
            }
            
            if (ret.length() > 0) ret += ", ";
            
            ret += dm.getUIName();
        }
        
        if (ret.length() == 0) return null;
    
        return "depends on " + ret;
    }

    private void showEmptySettingsDialog() {
    	FragmentTransaction ft = activity.getSupportFragmentManager().beginTransaction();
        ft.replace(R.id.module_details, new EmptySettingsDialog());
        ft.setTransition(FragmentTransaction.TRANSIT_FRAGMENT_FADE);
        ft.commit();
    }
    private void showDetails(final int position) {
        
        CodeableName cn = cnList.get(position);

        Augiement a = modeAugiements.get(cn);       
        if (a == null) {
        	return;
        }
        
        DialogFragment f = a.getUI();
        if (f == null) {
        	showEmptySettingsDialog();
        	return;
        }
        
        FragmentManager fm = ((SherlockFragmentActivity)activity).getSupportFragmentManager();
        if (f != null) {
            if (isDualPane) {
                FragmentTransaction ft = activity.getSupportFragmentManager().beginTransaction();
                ft.replace(R.id.module_details, f);
                ft.setTransition(FragmentTransaction.TRANSIT_FRAGMENT_FADE);
                ft.commit();

            } else {
                f.show(fm, "Module Settings");
                f.show((activity).getSupportFragmentManager(), "Module Settings");
            }
        }
    }

    private void updateButtonText(CompoundButton b, CodeableName cn, boolean isEnabled) {
            
        String n = allAugiements.get(cn).getUIName();
        if (isEnabled) {
            b.setText(n + " is enabled");
        } else {
            b.setText(n + " is disabled");
        }
    }

    public String[] getItems() {

        return items;
    }

    public boolean isDualPane() {
        
        return isDualPane;
    }
}
