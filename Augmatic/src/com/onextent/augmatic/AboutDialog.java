package com.onextent.augmatic;

import java.text.SimpleDateFormat;
import java.util.zip.ZipEntry;
import java.util.zip.ZipFile;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.app.DialogFragment;
import android.content.DialogInterface;
import android.content.pm.ApplicationInfo;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.TextView;

public class AboutDialog extends DialogFragment {

    //test comment

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {

        String buildtime = "unknown";
        try {
            Activity a = getActivity();
            ApplicationInfo ai = a.getPackageManager().getApplicationInfo(a.getPackageName(), 0);
            ZipFile zf = new ZipFile(ai.sourceDir);
            ZipEntry ze = zf.getEntry("classes.dex");
            long time = ze.getTime();
            buildtime = SimpleDateFormat.getInstance().format(new java.util.Date(time));
        } catch(Exception e){
        }

        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());

        builder.setTitle("About");

        LayoutInflater inflater = getActivity().getLayoutInflater();

        View v = inflater.inflate(com.onextent.augmatic.R.layout.about_layout, null);
        builder.setView(v);

        builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {
            }
        });

        TextView gitinfo = (TextView) v.findViewById(R.id.buildltime);
        if (gitinfo != null) gitinfo.setText(buildtime);
        
        return builder.create();
    }
}
