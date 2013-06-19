package com.onextent.augmatic;

import java.util.List;

import android.os.Bundle;
import android.support.v4.app.ListFragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;

import com.onextent.augie.AugieActivity;
import com.onextent.augie.ModeManager;

public class ModeListFrag extends ListFragment {
    
    private ModeManager modeManager;
    private List<String> modeNames;
    private int mNum;

    static ModeListFrag newInstance(int num) {
        
        ModeListFrag f = new ModeListFrag();

        // Supply num input as an argument.
        Bundle args = new Bundle();
        args.putInt("num", num);
        f.setArguments(args);

        return f;
    }
    
    /**
     * When creating, retrieve this instance's number from its arguments.
     */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mNum = getArguments() != null ? getArguments().getInt("num") : 1;
        AugieActivity a = (AugieActivity) getActivity();
        modeManager = a.getModeManager();
        try {
            modeNames = modeManager.getModeNameStrings();
        } catch (Exception e) {
            AugAppLog.e(e);
        }
    }

    /**
     * The Fragment's UI is just a simple text view showing its
     * instance number.
     */
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
            Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_pager_list, container, false);
        try {
            if (modeNames != null) {
                View tv = v.findViewById(R.id.text);
                String n = modeNames.get(mNum);
                ((TextView)tv).setText(n + " Mode");
                
                int len = modeNames.size();
                int lastpos = len -1;
                
                tv = v.findViewById(R.id.text_prev);
                if (mNum > 0) {
                    n = modeNames.get(mNum - 1);
                    ((TextView)tv).setText(n + " Mode");
                } else {
                    ((TextView)tv).setText("");
                }
                
                tv = v.findViewById(R.id.text_post);
                if (mNum < lastpos) {
                    n = modeNames.get(mNum + 1);
                    ((TextView)tv).setText(n + " Mode");
                } else {
                    ((TextView)tv).setText("");
                }
            }
        } catch (Exception e) {
            AugAppLog.e(e);
        }
        return v;
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        
        setListAdapter(new ArrayAdapter<String>(getActivity(),
                android.R.layout.simple_list_item_1, modeNames));
    }

    @Override
    public void onListItemClick(ListView l, View v, int position, long id) {
    }
}
