/*
 * This file is a modified version of the original TabHost.java file
 * in the android SDK version 7.
 * Original copyright notice follows :
 *
 * Copyright (C) 2006 The Android Open Source Project
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.weathernews.Weather;

import android.graphics.Bitmap;
import android.os.Debug;
import android.os.Bundle;
import android.app.Activity;
import android.app.LocalActivityManager;
import android.content.Context;
import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.util.AttributeSet;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.SoundEffectConstants;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewTreeObserver;
import android.view.Window;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.TextView;

import android.view.animation.Animation;
import android.view.animation.AnimationUtils;

import java.util.ArrayList;
import java.util.List;

/**
 * Container for a tabbed window view. This object holds two children: a set of tab labels that the
 * user clicks to select a specific tab, and a FrameLayout object that displays the contents of that
 * page. The individual elements are typically controlled using this container object, rather than
 * setting values on the child elements themselves.
 * It differs from the default implementation in that it allows for hidden tabs too, and to
 * use intents to select the tab.
 * It can't inherit from it because everything is private inside :(
 */
public class TabHostHide extends FrameLayout implements ViewTreeObserver.OnTouchModeChangeListener {

    public static final int DIRECTION_UNKNOWN = -1; // Nothing specified
    public static final int DIRECTION_NONE    =  0; // Specifically no direction
    public static final int DIRECTION_UP      =  1;
    public static final int DIRECTION_DOWN    =  2;
    public static final int DIRECTION_LEFT    =  3;
    public static final int DIRECTION_RIGHT   =  4;
    private static final int[][] DIR_ANIMATIONS = { {},
						    { R.anim.push_up_out,    R.anim.push_up_in },
						    { R.anim.push_down_out,  R.anim.push_down_in },
						    { R.anim.push_right_out, R.anim.push_right_in }, // Yes I know it's reversed :(
						    { R.anim.push_left_out,  R.anim.push_left_in } };

    private TabWidgetHide mTabWidget;
    private FrameLayout mTabContent;
    protected List<TabSpec> mTabSpecs = new ArrayList<TabSpec>(3);
    protected int mCurrentTab = -1;
    protected int mCurrentVisibleTab = -1;
    private View mCurrentView = null;
    protected LocalActivityManager mLocalActivityManager = null;
    private OnTabChangeListener mOnTabChangeListener;
    private OnKeyListener mTabKeyListener;
    private ImageView exitingView;

    public TabHostHide(Context context) {
        super(context);
        initTabHost();
    }

    public TabHostHide(Context context, AttributeSet attrs) {
        super(context, attrs);
        initTabHost();
    }

    private final void initTabHost() {
        setFocusableInTouchMode(true);
        setDescendantFocusability(FOCUS_AFTER_DESCENDANTS);

	mCurrentVisibleTab = mCurrentTab = -1;
        mCurrentView = null;
    }

    /**
     * Get a new {@link TabSpec} associated with this tab host.
     * @param tag required tag of tab.
     */
    public TabSpec newTabSpec(String tag) {
        return new TabSpec(tag);
    }



    /**
     * <p>Call setup() before adding tabs if loading TabHost using findViewById(). <i><b>However</i></b>: You do
     * not need to call setup() after getTabHost() in {@link android.app.TabActivity TabActivity}.
     * Example:</p>
     <pre>mTabHost = (TabHost)findViewById(R.id.tabhost);
     mTabHost.setup();
     mTabHost.addTab(TAB_TAG_1, "Hello, world!", "Tab 1");
    */
    public void setup() {
        mTabWidget = (TabWidgetHide) findViewById(android.R.id.tabs);
        if (null == mTabWidget) throw new RuntimeException("Your TabHost must have a TabWidget whose id attribute is 'R.id.roottabs'");

        // KeyListener to attach to all tabs. Detects non-navigation keys
        // and relays them to the tab content.
        mTabKeyListener = new OnKeyListener() {
		public boolean onKey(View v, int keyCode, KeyEvent event) {
		    switch (keyCode) {
                    case KeyEvent.KEYCODE_DPAD_CENTER:
                    case KeyEvent.KEYCODE_DPAD_LEFT:
                    case KeyEvent.KEYCODE_DPAD_RIGHT:
                    case KeyEvent.KEYCODE_DPAD_UP:
                    case KeyEvent.KEYCODE_DPAD_DOWN:
                    case KeyEvent.KEYCODE_ENTER:
                        return false;
		    }
		    mTabContent.requestFocus(View.FOCUS_FORWARD);
		    return mTabContent.dispatchKeyEvent(event);
		}

	    };

        mTabWidget.setTabSelectionListener(new TabWidgetHide.OnTabSelectionChanged() {
		public void onTabSelectionChanged(int tabIndex, boolean clicked) {
		    setCurrentTab(tabIndex);
		    if (clicked) mTabContent.requestFocus(View.FOCUS_FORWARD);
		}
	    });

        mTabContent = (FrameLayout) findViewById(android.R.id.tabcontent);
        if (null == mTabContent)
            throw new RuntimeException("Your TabHost must have a FrameLayout whose id attribute is 'android.R.id.tabcontent'");

	exitingView = (ImageView) findViewById(R.id.exitingView);
	exitingView.setVisibility(View.GONE);
    }

    /**
     * If you are using {@link TabSpec#setContent(android.content.Intent)}, this
     * must be called since the activityGroup is needed to launch the local activity.
     *
     * This is done for you if you extend {@link android.app.TabActivity}.
     * @param activityGroup Used to launch activities for tab content.
     */
    public void setup(LocalActivityManager activityGroup) {
        setup();
        mLocalActivityManager = activityGroup;
    }


    @Override
	protected void onAttachedToWindow() {
        super.onAttachedToWindow();
        final ViewTreeObserver treeObserver = getViewTreeObserver();
        if (treeObserver != null) treeObserver.addOnTouchModeChangeListener(this);
    }

    @Override
	protected void onDetachedFromWindow() {
        super.onDetachedFromWindow();
        final ViewTreeObserver treeObserver = getViewTreeObserver();
        if (treeObserver != null) treeObserver.removeOnTouchModeChangeListener(this);
    }

    /**
     * {@inheritDoc}
     */
    public void onTouchModeChanged(boolean isInTouchMode) {
        if (!isInTouchMode) {
            // leaving touch mode.. if nothing has focus, let's give it to
            // the indicator of the current tab
            if (!mCurrentView.hasFocus() || mCurrentView.isFocused())
		if (null != mTabWidget && null != mTabWidget.getChildAt(mCurrentVisibleTab))
		    mTabWidget.getChildAt(mCurrentVisibleTab).requestFocus();
        }
    }

    /**
     * Add a tab.
     * @param tabSpec Specifies how to create the indicator and content.
     */
    public void addTab(TabSpec tabSpec) {
        if (null == tabSpec.mIndicatorStrategy) throw new IllegalArgumentException("you must specify a way to create the tab indicator.");
        if (null == tabSpec.mContentStrategy) throw new IllegalArgumentException("you must specify a way to create the tab content");

	// Find where to put it
	if (tabSpec.mHidden) mTabSpecs.add(tabSpec);
	    else
		{
		    int i = 0;
		    for (i = 0; i < mTabSpecs.size(); i++)
			if (mTabSpecs.get(i).mHidden) break;
		    mTabSpecs.add(i, tabSpec);
		    View tabIndicator = tabSpec.mIndicatorStrategy.createIndicatorView();
		    tabIndicator.setOnKeyListener(mTabKeyListener);
		    mTabWidget.addView(tabIndicator);
		}
    }

    /**
     * Removes all tabs from the tab widget associated with this tab host.
     */
    public void clearAllTabs() {
        mTabWidget.removeAllViews();
        initTabHost();
        mTabContent.removeAllViews();
        mTabSpecs.clear();
        requestLayout();
        invalidate();
    }

    public TabWidgetHide getTabWidget() { return mTabWidget; }
    public int getCurrentTab() { return mCurrentTab; }
    public int getCurrentVisibleTab() { return mCurrentVisibleTab; }

    public String getCurrentTabTag()
    {
        if (mCurrentTab >= 0 && mCurrentTab < mTabSpecs.size()) return mTabSpecs.get(mCurrentTab).getTag();
        return null;
    }

    public View getCurrentTabView() {
        if (mCurrentTab >= 0 && mCurrentTab < mTabSpecs.size()) return mTabWidget.getChildAt(mCurrentTab);
        return null;
    }

    public View getCurrentView() { return mCurrentView; }

    public void setCurrentTabByTag(String tag) {
        int i;
        for (i = 0; i < mTabSpecs.size(); i++)
            if (mTabSpecs.get(i).getTag().equals(tag))
		{
		    setCurrentTabInner(i, null, DIRECTION_NONE);
		    break;
		}
    }

    /**
     * Get the FrameLayout which holds tab content
     */
    public FrameLayout getTabContentView() {
        return mTabContent;
    }

    @Override public boolean dispatchKeyEvent(KeyEvent event) {
        final boolean handled = super.dispatchKeyEvent(event);

        // unhandled key ups change focus to tab indicator for embedded activities
        // when there is nothing that will take focus from default focus searching
        if (!handled
	    && (event.getAction() == KeyEvent.ACTION_DOWN)
	    && (event.getKeyCode() == KeyEvent.KEYCODE_DPAD_UP)
	    //	    && (mCurrentView.isRootNamespace())
	    && (mCurrentView.hasFocus())
	    && (mCurrentView.findFocus().focusSearch(View.FOCUS_UP) == null)) {
            mTabWidget.getChildAt(mCurrentVisibleTab).requestFocus();
            playSoundEffect(SoundEffectConstants.NAVIGATION_UP);
            return true;
        }
        return handled;
    }


    @Override public void dispatchWindowFocusChanged(boolean hasFocus)
    {
        mCurrentView.dispatchWindowFocusChanged(hasFocus);
    }

    public Intent getIntent()
    {
	if ((null != mTabSpecs) && (mTabSpecs.size() > mCurrentTab) && (-1 != mCurrentTab)) return mTabSpecs.get(mCurrentTab).getIntent(); else return null;
    }

    public void setCurrentTab(Intent intent, int direction)
    {
        for (int i = 0; i < mTabSpecs.size(); i++)
	    if (intent.filterEquals(mTabSpecs.get(i).getIntent()))
		{
		    if (DIRECTION_UNKNOWN == direction)
			setCurrentTabInner(i, intent, i >= mCurrentTab ? DIRECTION_LEFT : DIRECTION_RIGHT);
		    else
			setCurrentTabInner(i, intent, direction);
		    return;
		}
    }

    public void setCurrentTab(Intent intent) { setCurrentTab(intent, DIRECTION_UNKNOWN); }

    public void setCurrentTab(int index)
    {
        if (index < 0 || index >= mTabSpecs.size()) return;
        if (index == mCurrentTab)
	    {
		int link = mTabSpecs.get(mCurrentTab).link;
		if (-1 != link) setCurrentTabInner(link, null, DIRECTION_UP);
		return;
	    }
	else
	    if (mCurrentTab == mTabSpecs.get(index).link)
		setCurrentTabInner(index, null, -1 == mCurrentTab ? DIRECTION_NONE : DIRECTION_DOWN);
	    else
		setCurrentTabInner(index, null, index >= mCurrentTab ? DIRECTION_LEFT : DIRECTION_RIGHT);
    }

    private void setCurrentTabInner(int index, Intent intent, int direction)
    {
        // notify old tab content
        if (mCurrentTab != -1) mTabSpecs.get(mCurrentTab).mContentStrategy.tabClosed(direction);
        mCurrentTab = index;
        final TabHostHide.TabSpec spec = mTabSpecs.get(index);

        // Call the tab widget's focusCurrentTab(), instead of just
        // selecting the tab.
	if (!spec.mHidden)
	    {
		mTabWidget.focusCurrentTab(mCurrentTab);
		mCurrentVisibleTab = mCurrentTab;
	    }
	else if (-1 != spec.link && !mTabSpecs.get(spec.link).mHidden)
	    {
		mTabWidget.focusCurrentTab(spec.link);
		mCurrentVisibleTab = mCurrentTab;
	    }

        // tab content
        mCurrentView = spec.mContentStrategy.getContentView(intent, direction);
        if (null == mCurrentView.getParent())
            mTabContent.addView(mCurrentView, new ViewGroup.LayoutParams(ViewGroup.LayoutParams.FILL_PARENT,
									 ViewGroup.LayoutParams.FILL_PARENT));
        if (!mTabWidget.hasFocus())
            // if the tab widget didn't take focus (likely because we're in touch mode)
            // give the current tab content view a shot
            mCurrentView.requestFocus();
        //mTabContent.requestFocus(View.FOCUS_FORWARD);
        invokeOnTabChangeListener();
    }

    /**
     * Register a callback to be invoked when the selected state of any of the items
     * in this list changes
     * @param l
     * The callback that will run
     */
    public void setOnTabChangedListener(OnTabChangeListener l) { mOnTabChangeListener = l; }
    private void invokeOnTabChangeListener() { if (mOnTabChangeListener != null) mOnTabChangeListener.onTabChanged(getCurrentTabTag()); }

    /**
     * Interface definition for a callback to be invoked when tab changed
     */
    public interface OnTabChangeListener
    {
        void onTabChanged(String tabId);
    }


    /**
     * Makes the content of a tab when it is selected. Use this if your tab
     * content needs to be created on demand, i.e. you are not showing an
     * existing view or starting an activity.
     */
    public interface TabContentFactory {
        /**
         * Callback to make the tab contents
         *
         * @param tag
         *            Which tab was selected.
         * @return The view to distplay the contents of the selected tab.
         */
        View createTabContent(String tag);
    }


    /**
     * A tab has a tab indictor, content, and a tag that is used to keep
     * track of it.  This builder helps choose among these options.
     *
     * For the tab indicator, your choices are:
     * 1) set a label
     * 2) set a label and an icon
     *
     * For the tab content, your choices are:
     * 1) the id of a {@link View}
     * 2) a {@link TabContentFactory} that creates the {@link View} content.
     * 3) an {@link Intent} that launches an {@link android.app.Activity}.
     */
    public class TabSpec {

        private String mTag;
	public boolean mHidden;
	public int link;

        private IndicatorStrategy mIndicatorStrategy;
        private ContentStrategy mContentStrategy;

        private TabSpec(String tag) { mTag = tag; mHidden = false; link = -1; }

        /**
         * Specify a label as the tab indicator.
         */
        public TabSpec setIndicator(CharSequence label) {
            mIndicatorStrategy = new LabelIndicatorStrategy(label);
	    if (label.equals("")) mHidden = true;
            return this;
        }

        /**
         * Specify a label and icon as the tab indicator.
         */
        public TabSpec setIndicator(CharSequence label, Drawable icon) {
            mIndicatorStrategy = new LabelAndIconIndicatorStategy(label, icon);
	    if (label.equals("")) mHidden = true;
            return this;
        }

        /**
         * Specify the id of the view that should be used as the content
         * of the tab.
         */
        public TabSpec setContent(int viewId) {
            mContentStrategy = new ViewIdContentStrategy(viewId);
            return this;
        }

        /**
         * Specify a {@link android.widget.TabHostHide.TabContentFactory} to use to
         * create the content of the tab.
         */
        public TabSpec setContent(TabContentFactory contentFactory) {
            mContentStrategy = new FactoryContentStrategy(mTag, contentFactory);
            return this;
        }

        /**
         * Specify an intent to use to launch an activity as the tab content.
         */
        public TabSpec setContent(Intent intent) {
            mContentStrategy = new IntentContentStrategy(mTag, intent);
            return this;
        }


        String getTag() { return mTag; }
	public Intent getIntent() { return mContentStrategy.getIntent(); }
    }

    /**
     * Specifies what you do to create a tab indicator.
     */
    private static interface IndicatorStrategy {

        /**
         * Return the view for the indicator.
         */
        View createIndicatorView();
    }

    /**
     * Specifies what you do to manage the tab content.
     */
    private static interface ContentStrategy {

        /**
         * Return the content view.  The view should may be cached locally.
         */
        View getContentView(Intent intent, int direction);

        /**
         * Perhaps do something when the tab associated with this content has
         * been closed (i.e make it invisible, or remove it).
         */
        void tabClosed(int direction);

	/**
	 * Get an intent to match against the one passed.
	 */
	Intent getIntent();
    }

    /**
     * How to create a tab indicator that just has a label.
     */
    private class LabelIndicatorStrategy implements IndicatorStrategy {

        private final CharSequence mLabel;

        private LabelIndicatorStrategy(CharSequence label) {
            mLabel = label;
        }

        public View createIndicatorView() {
            LayoutInflater inflater =
		(LayoutInflater) getContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            View tabIndicator = inflater.inflate(R.layout.tab_indicator_text_only,
						 mTabWidget, // tab widget is the parent
						 false); // no inflate params

            final TextView tv = (TextView) tabIndicator.findViewById(R.id.title);
            tv.setText(mLabel);

            return tabIndicator;
        }
    }

    /**
     * How we create a tab indicator that has a label and an icon
     */
    private class LabelAndIconIndicatorStategy implements IndicatorStrategy {

        private final CharSequence mLabel;
        private final Drawable mIcon;

        private LabelAndIconIndicatorStategy(CharSequence label, Drawable icon) {
            mLabel = label;
            mIcon = icon;
        }

        public View createIndicatorView() {
            LayoutInflater inflater =
		(LayoutInflater) getContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            View tabIndicator = inflater.inflate(R.layout.tab_indicator,
						 mTabWidget, // tab widget is the parent
						 false); // no inflate params

            final TextView tv = (TextView) tabIndicator.findViewById(R.id.title);
            tv.setText(mLabel);

            final ImageView iconView = (ImageView) tabIndicator.findViewById(R.id.icon);
            iconView.setImageDrawable(mIcon);
            if(mIcon != null)
            	mIcon.setCallback(null);

            return tabIndicator;
        }
    }

    /**
     * How to create the tab content via a view id.
     */
    private class ViewIdContentStrategy implements ContentStrategy {

        private final View mView;

        private ViewIdContentStrategy(int viewId)
	{
            mView = mTabContent.findViewById(viewId);
            if (null != mView)
                mView.setVisibility(View.GONE);
	    else
                throw new RuntimeException("Could not create tab content because could not find view with id " + viewId);
        }

        public View getContentView(Intent intent, int direction)
	{
            mView.setVisibility(View.VISIBLE);
            return mView;
        }

        public void tabClosed(int direction) { mView.setVisibility(View.GONE); }

	public Intent getIntent() { return null; }
    }

    /**
     * How tab content is managed using {@link TabContentFactory}.
     */
    private class FactoryContentStrategy implements ContentStrategy {
        private View mTabContent;
        private final CharSequence mTag;
        private TabContentFactory mFactory;

        public FactoryContentStrategy(CharSequence tag, TabContentFactory factory)
	{
            mTag = tag;
            mFactory = factory;
        }

        public View getContentView(Intent intent, int direction)
	{
            if (null == mTabContent) mTabContent = mFactory.createTabContent(mTag.toString());
            mTabContent.setVisibility(View.VISIBLE);
            return mTabContent;
        }

        public void tabClosed(int direction) { mTabContent.setVisibility(View.INVISIBLE); }

	public Intent getIntent() { return null; }
    }

    /**
     * How tab content is managed via an {@link Intent}: the content view is the
     * decorview of the launched activity.
     */
    private class IntentContentStrategy implements ContentStrategy {

        private final String mTag;
        private Intent mIntent;

        private View mLaunchedView;

        private IntentContentStrategy(String tag, Intent intent) {
            mTag = tag;
            mIntent = intent;
        }

        public View getContentView(Intent intent, int direction)
	{
//        	Log.i("myTag", "getContentView");
            if (null == mLocalActivityManager) throw new IllegalStateException("Did you forget to call 'public void setup(LocalActivityManager activityGroup)'?");
	    if (null != intent) mIntent = intent;
	    intent = (Intent)mIntent.clone();
	    if (null != mLaunchedView) mLaunchedView.clearAnimation();

	    Activity a = mLocalActivityManager.getActivity(mTag);
	    if (null != a)
		{
		    Intent oldIntent = a.getIntent();
		    boolean same = true;
		    if (oldIntent.getStringExtra("CityID") != mIntent.getStringExtra("CityID")) same = false;
		    if (oldIntent.getStringExtra("CityName") != mIntent.getStringExtra("CityName")) same = false;
		    if (oldIntent.getIntExtra("Index", Integer.MIN_VALUE) != mIntent.getIntExtra("Index", Integer.MIN_VALUE)) same = false;
		    if (oldIntent.getIntExtra("ThemeID", Integer.MIN_VALUE) != mIntent.getIntExtra("ThemeID", Integer.MIN_VALUE)) same = false;
		    if (oldIntent.getStringExtra("CamID") != mIntent.getStringExtra("CamID")) same = false;
		    if (oldIntent.getIntExtra("Mapid", Integer.MIN_VALUE) != mIntent.getIntExtra("Mapid", Integer.MIN_VALUE)) same = false;

		    int flags = intent.getFlags();
		    if (same) flags = flags & ~Intent.FLAG_ACTIVITY_CLEAR_TOP;
		    else flags = flags | Intent.FLAG_ACTIVITY_CLEAR_TOP;
		    intent.setFlags(flags);
		}

            final Window w = mLocalActivityManager.startActivity(mTag, intent);
            final View wd = w != null ? w.getDecorView() : null;
            if (mLaunchedView != wd && null != mLaunchedView) if (null != mLaunchedView.getParent()) mTabContent.removeView(mLaunchedView);
            mLaunchedView = wd;

            // XXX Set FOCUS_AFTER_DESCENDANTS on embedded activies for now so they can get
            // focus if none of their children have it. They need focus to be able to
            // display menu items.
            //
            // Replace this with something better when Bug 628886 is fixed...
            //
            if (null != mLaunchedView)
		{
		    mLaunchedView.setVisibility(View.VISIBLE);
		    mLaunchedView.setFocusableInTouchMode(true);
		    ((ViewGroup) mLaunchedView).setDescendantFocusability(FOCUS_AFTER_DESCENDANTS);

		    if (DIRECTION_NONE != direction)
			{
			    Animation slide_in = AnimationUtils.loadAnimation(mLocalActivityManager.getActivity(mTag), DIR_ANIMATIONS[direction][1]);
			    mLaunchedView.startAnimation(slide_in);
			}
		}
            return mLaunchedView;
        }

        public void tabClosed(int direction)
	{
//        	Log.e("myTag", "tabClosed");
	    if (null != mLaunchedView)
		{
		    final View v = mLaunchedView;

		    if (DIRECTION_NONE != direction)
			{
			    Bitmap tmpb = null;
			    if (null != v)
				{
				    v.setDrawingCacheEnabled(true);
				    try{
				    	tmpb = v.getDrawingCache();
				    } catch (RuntimeException e) {
				    	v.setVisibility(View.GONE);
				    	if(tmpb != null)
				    		tmpb.recycle();
				    	return;
				    } catch (OutOfMemoryError e) {
				    	v.setVisibility(View.GONE);
				    	if(tmpb != null)
				    		tmpb.recycle();
				    	return;
				    }
				}
			    try{
			    	final Bitmap b = null == tmpb ? null : tmpb.copy(Bitmap.Config.RGB_565, false);
			    	if(null != tmpb)
			    		tmpb.recycle();
	
				    if (null != b && null != exitingView)
					{
					    // We have to do this because if we relaunch the same activity, it will destroy its
					    // views, leading to the android framework trying to draw a destroyed object
					    final Activity act = mLocalActivityManager.getActivity(mTag);
					    exitingView.setImageBitmap(b);
					    exitingView.setVisibility(View.VISIBLE);
					    if(null != act) {
						    final Animation slide_out = AnimationUtils.loadAnimation(act, DIR_ANIMATIONS[direction][0]);
						    slide_out.setAnimationListener(new Animation.AnimationListener()
							{
							    @Override public void onAnimationEnd(Animation a) {
								exitingView.setImageBitmap(null);
								exitingView.setVisibility(View.GONE);
								b.recycle();
								//				Debug.stopMethodTracing();
							    }
							    @Override public void onAnimationRepeat(Animation a) {}
							    @Override public void onAnimationStart(Animation a) {
								//				Debug.startMethodTracing("anim");
							    }
							});
						    exitingView.startAnimation(slide_out);
					    } else {
					    	exitingView.setImageBitmap(null);
							exitingView.setVisibility(View.GONE);
					    	b.recycle();
					    }
					}
			    } catch(OutOfMemoryError e) {
			    	System.gc();
			    }
			}
		    v.setVisibility(View.GONE);
		}
	}
	public Intent getIntent() { return mIntent; }
    }
}
