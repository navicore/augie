/**
 * copyright Ed Sweeney, 2012, all rights reserved
 */

package com.onextent.augmatic;

import com.onextent.augie.AugieStore;
import com.onextent.android.store.CodeStore;
import com.onextent.android.store.CodeStoreSqliteImpl;

import android.content.Context;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.RelativeLayout;

/**
 * Augmatic is the reference implementation camara for Augie
 * 
 * T H E   A U G I E   A U G M A T I C   1 0 0 0 
 * T H E   A U G I E   A U G M A T I C   1 0 0 0 
 * T H E   A U G I E   A U G M A T I C   1 0 0 0 
 * 
 */
public class AugmaticActivity extends BaseAugmaticActivity {

    private Button menu_btn;
    
    public AugmaticActivity() {
        
    }
   
    @Override
    protected Button getMenuButton() {
        if (menu_btn == null) {
            menu_btn = new Button(this);
        }
        return menu_btn;
    }

    @Override
    protected int getLayoutId() {
        return R.layout.main;
    }

    @Override
    protected int getPreviewId() {
        return R.id.camera_preview;
    }
    
    @Override
    protected void configMenuButton() {
    
        RelativeLayout.LayoutParams params = new RelativeLayout.LayoutParams(RelativeLayout.LayoutParams.WRAP_CONTENT, RelativeLayout.LayoutParams.WRAP_CONTENT);
    
        params.addRule(RelativeLayout.ALIGN_PARENT_RIGHT, RelativeLayout.TRUE);
        
        final Button btn = getMenuButton();
    
        btn.setLayoutParams(params);
    
        btn.setMinimumHeight(30);
    
        btn.setMinimumWidth(30);
    
        btn.setBackgroundResource(R.drawable.abs__ic_menu_moreoverflow_holo_dark);
    
        btn.setOnClickListener(new View.OnClickListener() {
    
            public void onClick(View view) {
    
                getSupportActionBar().show();
    
                btn.setVisibility(View.GONE);
            }
        });
    }
   
    public static void initStore(Context c) {
    
        CodeStore s = AugieStore.getCodeStore();
        if (s == null) {
            s = new CodeStoreSqliteImpl(c.getApplicationContext(), "augiematic_store");
            s.open();
            AugieStore.setCodeStore(s);
        }
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {

        initStore(this);
        super.onCreate(savedInstanceState);

        getSupportActionBar().hide();
    }
    
    @Override
    protected void onDestroy() {
        super.onDestroy();
        //CodeStore s = AugieStore.getCodeStore();
        
        //todo: safe? could settings activity live longer?
        //if (s != null) s.close();
        //AugieStore.setCodeStore(null);
    }
}
