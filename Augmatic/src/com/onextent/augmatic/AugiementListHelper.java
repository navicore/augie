
package com.onextent.augmatic;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Set;

import android.app.DialogFragment;
import android.app.FragmentManager;
import android.app.FragmentTransaction;
import android.view.View;
import android.widget.CompoundButton;
import android.widget.TextView;

import com.onextent.android.codeable.CodeableName;
import com.onextent.augie.AugieActivity;
import com.onextent.augie.Augiement;
import com.onextent.augie.Augiement.Meta;
import com.onextent.augie.Mode;
import com.onextent.augie.ModeManager;

public class AugiementListHelper {

    protected boolean isDualPane;
    protected Map<CodeableName, Augiement> modeAugiements;
    protected Map<CodeableName, Augiement.Meta> allAugiements;
    protected List<CodeableName> cnList;

    protected String[] items;
    private final ControlActivity activity;

    public AugiementListHelper(ControlActivity activity) {
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
        allAugiements = activity.getAugiementFactory().getAllMeta();

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

        activity.setCurrentAugiementIdx(position);
        DialogFragment f = new AugiementStatusFrag();
        
        if (isDualPane) {
            FragmentTransaction ft = activity.getFragmentManager().beginTransaction();
            ft.replace(R.id.module_status, f);
            ft.setTransition(FragmentTransaction.TRANSIT_FRAGMENT_FADE);
            ft.commit();
            showDetails(position);

        } else {
            f.show(activity.getFragmentManager(), "Module Status");
        }
    }

    public boolean updateDepText(View v, CodeableName cn) {

        TextView deps = (TextView) v.findViewById(R.id.module_dependencies);
        TextView reqs = (TextView) v.findViewById(R.id.module_required_by);
        Augiement.Meta m = allAugiements.get(cn);
        deps.setText(getDepsUINames(m));
        List<Meta> rm = getRequiredByMeta(m);
        reqs.setText(getRequiredByUINames(rm));
        return rm != null;
    }

    public void updateStatusText(View v, CodeableName cn) {

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
                AugAppLog.w("getDepsDesc looking for unknown augiement: " + dm);
                continue;
            }

            if (ret.length() > 0) ret += ", ";

            ret += dm.getUIName();
        }

        if (ret.length() == 0) return null;

        return "depends on " + ret;
    }

    private void showEmptySettingsDialog() {
        FragmentTransaction ft = activity.getFragmentManager().beginTransaction();
        ft.replace(R.id.module_details, new EmptySettingsDialog());
        ft.setTransition(FragmentTransaction.TRANSIT_FRAGMENT_FADE);
        ft.commit();
    }
    public void showDetails(final int position) {

        CodeableName cn = cnList.get(position);

        Augiement a = modeAugiements.get(cn);       
        if (a == null) {
            showEmptySettingsDialog();
            return;
        }

        DialogFragment f = a.getUI();
        if (f == null) {
            showEmptySettingsDialog();
            return;
        }

        FragmentManager fm = activity.getFragmentManager();
        if (f != null) {
            if (isDualPane) {
                FragmentTransaction ft = activity.getFragmentManager().beginTransaction();
                ft.replace(R.id.module_details, f);
                ft.setTransition(FragmentTransaction.TRANSIT_FRAGMENT_FADE);
                ft.commit();

            } else {
                f.show(fm, "Augiement Settings");
                f.show((activity).getFragmentManager(), "Augiement Settings");
            }
        }
    }

    public void updateButtonText(CompoundButton b, CodeableName cn, boolean isEnabled) {

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

    public Map<CodeableName, Augiement> getModeAugiements() {
        return modeAugiements;
    }
    
    public List<CodeableName> getCnList() {
        return cnList;
    }

    public void refreshModeAugiements(Mode mode) {
        modeAugiements = mode.getAugiements(); //refresh list
    }
}
