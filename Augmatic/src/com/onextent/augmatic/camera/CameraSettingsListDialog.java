package com.onextent.augmatic.camera;

import android.app.AlertDialog;
import android.app.Dialog;
import android.support.v4.app.FragmentManager;
import android.content.DialogInterface;
import android.os.Bundle;
import com.actionbarsherlock.app.SherlockDialogFragment;
import com.actionbarsherlock.app.SherlockFragmentActivity;

public class CameraSettingsListDialog extends SherlockDialogFragment {
   
    final String[] items = {"Processing", "Image File", "Shooting"};
    
    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        builder.setTitle("Choose Catagory").setItems(items, new Dialog.OnClickListener() {
            
            @Override
            public void onClick(DialogInterface dialog, int which) {
              
                FragmentManager fm = ((SherlockFragmentActivity)getActivity()).getSupportFragmentManager();
                SherlockDialogFragment f = null;
                switch (which) {
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
                    f.show(fm, "Camera Settings");
                }
            }
        });
       
        return builder.create();
    }
}