package com.onextent.augmatic;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.os.Bundle;
import com.actionbarsherlock.app.SherlockDialogFragment;

public class AugiementListDialog extends SherlockDialogFragment {
  
    AugiementListHelper helper;
    
    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
       
        helper = new AugiementListHelper((AugiementSettingsActivity) getSherlockActivity());

        helper.init();

        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        builder.setTitle("Choose Augiement").setItems(helper.getItems(), new Dialog.OnClickListener() {
            
            @Override
            public void onClick(DialogInterface dialog, int which) {
              
                helper.initDialogs(which);
            }
        });
       
        return builder.create();
    }
}
