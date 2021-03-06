/**
 * copyright Ed Sweeney, 2012, all rights reserved
 */

package com.onextent.augmatic;

import android.content.Context;
import android.os.Bundle;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v4.view.ViewPager.OnPageChangeListener;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnTouchListener;
import android.view.ViewGroup;
import android.widget.Button;

import com.onextent.android.codeable.Code;
import com.onextent.android.codeable.CodeableName;
import com.onextent.android.store.CodeStore;
import com.onextent.android.store.CodeStoreSqliteImpl;
import com.onextent.android.ui.AbstractTwoFingerListener;
import com.onextent.augie.AugieStore;
import com.onextent.augie.AugieStoreException;

/**
 * Augmatic is the reference implementation camara for Augie
 * 
 * T H E   A U G I E   A U G M A T I C   1 0 0 0 
 * T H E   A U G I E   A U G M A T I C   1 0 0 0 
 * T H E   A U G I E   A U G M A T I C   1 0 0 0 
 * 
 */
public class AugmaticActivity extends BaseAugmaticActivity {

    private View controlLayout;

    public AugmaticActivity() {

    }

    @Override
    protected View getControlLayout() {
        if (controlLayout == null) {

            LayoutInflater inflater = getLayoutInflater();
            controlLayout = inflater.inflate(R.layout.main_nav, null);
        }
        return controlLayout;
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
    protected View configMenuButton() {

        View cntl = getControlLayout();
        if (cntl == null) return null;
        final Button btn = (Button) cntl.findViewById(R.id.menuButton);
        if (btn == null) return null;

        btn.setOnClickListener(new View.OnClickListener() {

            public void onClick(View view) {

                getActionBar().show();

                btn.setVisibility(View.GONE);
                activateSwipeNav(true);
            }
        });
        return cntl;
    }

    public static void initStore(Context c) {

        CodeStore s = AugieStore.getCodeStore();
        if (s == null) {
            s = new CodeStoreSqliteImpl(c.getApplicationContext(), "augiematic_store");
            s.open();
            AugieStore.setCodeStore(s);
        }
    }

    private PagerAdapter mAdapter;
    private ViewPager mPager;
    private ViewGroup swipeLayout;

    @Override
    public void onCreate(Bundle savedInstanceState) {

        initStore(this);
        super.onCreate(savedInstanceState);

        getActionBar().setBackgroundDrawable(null);
        getActionBar().hide();

        LayoutInflater inflater = getLayoutInflater();
        swipeLayout = (ViewGroup) inflater.inflate(R.layout.main_swipe_nav, null);
        mPager = (ViewPager)swipeLayout.findViewById(R.id.pager);
        mPager.setOnTouchListener(navTouchListener);
    }

    @Override
    protected void onPause() {
        super.onPause();
    }

    private int currentSwipePos = 0;

    final OnPageChangeListener pageListener = new OnPageChangeListener() {

        @Override
        public void onPageSelected(int position) {

        }

        @Override
        public void onPageScrolled(int position, float positionOffset, int offsetPixels) { 

            //if (positionOffset != 0.0 || offsetPixels != 0) return;
            //if (position == modeManager.getCurrentModeIdx()) return;
            currentSwipePos = position;
        }

        @Override
        public void onPageScrollStateChanged(int arg0) { 

        }
    };
    @Override
    protected void onResume() {
        super.onResume();
        mAdapter = new PagerAdapter(getSupportFragmentManager());
        mPager.setAdapter(mAdapter);
        mPager.setOnPageChangeListener(pageListener);
        currentSwipePos = modeManager.getCurrentModeIdx();
        try {
            mPager.setCurrentItem(modeManager.getCurrentModePos(modeManager.getModeNameStrings()));
        } catch (Exception e) {
            AugAppLog.e(e);
        }
    }

    public void navigate(int position) {

        ((ViewGroup) getControlLayout()).removeView(swipeLayout);
        if (position == modeManager.getCurrentModeIdx()) return;
        Code code = null;
        try {
            code = modeManager.getAllModeCode().get(currentSwipePos);
            if (code == null) {
                AugAppLog.e("mode not found");
                return;
            }
            CodeableName modeName;
            modeName = code.getCodeableName();
            setMode(modeName);
        } catch (Exception e1) {
            AugAppLog.e(e1);
            return;
        }
    } 

    @Override
    protected void activateSwipeNav(boolean activate) {
        if (activate) {
            ((ViewGroup) getControlLayout()).addView(swipeLayout);
        } else {
            navigate(currentSwipePos);
        }
    } 

    public class PagerAdapter extends FragmentStatePagerAdapter {

        public PagerAdapter(FragmentManager fm) {
            super(fm);
        }

        @Override
        public int getCount() {
            try {
                return modeManager.getAllModeCode().size();
            } catch (AugieStoreException e) {
                AugAppLog.e(e);
                return 0;
            }
        }

        @Override
        public android.support.v4.app.Fragment getItem(int position) {
            return ModeListFrag.newInstance(position);
        }
    }
    
    protected final OnTouchListener navTouchListener = new AbstractTwoFingerListener() {
        
        @Override
        protected void doit() {
            boolean navGestureEnabled = sharedPrefs.getBoolean("nav_shift_gesture", false);
            if (navGestureEnabled) leaveNavMode();
        }
    };
}
