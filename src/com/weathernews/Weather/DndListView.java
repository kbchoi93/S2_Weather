package com.weathernews.Weather;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.PixelFormat;
import android.graphics.Rect;
import android.util.AttributeSet;
import android.util.Log;
import android.view.Gravity;
import android.view.KeyEvent;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewConfiguration;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.ListView;
import android.view.View.OnLongClickListener;
import android.view.LayoutInflater;
import android.view.ViewGroup.LayoutParams;
import android.os.Handler;
import android.os.Parcel;
import android.os.SystemClock;
import android.os.Parcelable.Creator;

public class DndListView extends ListView implements View.OnLongClickListener {

    private Context mContext;
    private View mDragView;
    private WindowManager mWindowManager;
    private WindowManager.LayoutParams mWindowParams;
    private int mDragPos;      // 드래그 아이템의 위치
    private int mFirstDragPos; // 드래그 아이템의 원래 위치
    private int mDragPoint;
    private int mCoordOffset;  // 스크린에서의 위치와 뷰내에서의 위치의 차이
    protected DragListener mDragListener;
    protected DropListener mDropListener;
    private int mUpperBound;
    private int mLowerBound;
    private int mHeight;
    private Rect mTempRect = new Rect();
    private final int mTouchSlop;
    private int mItemHeightNormal;
    private int mItemHeightExpanded;
    private int dndViewId;
    private int dragImageX = 0;
    protected int dest;
    private Handler buttonHold;
    private int nViewWidth = 0;
    private int nStartY = 0;
    private int nEndY = 0;
    // This is needed because if we implement the normal onLongClick we do not
    // receive the up event. Ever.
    class OnLongClickHandler implements Runnable {
    	public View view;
    	public DndListView parent;
    	OnLongClickHandler(DndListView p) { parent = p; }
    	public void run() {
//    		parent.doExpansion();
//    		buttonHold.postDelayed(new Runnable() { public void run() {parent.doExpansion(); } }, 10);
    		parent.onLongClick(view);
    	}
    }
    private OnLongClickHandler onLongClickHandler;

    public DndListView(Context context, AttributeSet attrs) {
	super(context, attrs);
	mContext = context;
	mTouchSlop = ViewConfiguration.get(context).getScaledTouchSlop();
	buttonHold = new Handler();
	onLongClickHandler = new OnLongClickHandler(this);
    }
    
    @Override
    public boolean onTouchEvent(MotionEvent ev) {
    	int action = ev.getAction();
    	int px = (int)ev.getX();
    	int py = (int)ev.getY();
    	
    	switch (action) {
    	case MotionEvent.ACTION_UP:
    	case MotionEvent.ACTION_CANCEL:
    		buttonHold.removeCallbacks(onLongClickHandler);
    		if (null == mDragView) return super.onTouchEvent(ev);
    		stopDragging();
    		dest = getItemForPosition((int)ev.getY()) + getFirstVisiblePosition();
    		if (dest > getCount()) dest = getCount();
    		if (mDropListener != null && dest >= 0 && dest <= getCount())
    			mDropListener.drop(mFirstDragPos, dest);
    		unExpandViews(false);
    		mDragPos = -1;
//    		return true;
    		break;
	    
    	case MotionEvent.ACTION_MOVE :
    		try {
    			if (null == mDragView) return super.onTouchEvent(ev);
    			dragMove((int) ev.getX(), (int) ev.getY());
    		} catch (ArrayIndexOutOfBoundsException e) {
    			e.printStackTrace();
    		}
    		return true;
	    
    	case MotionEvent.ACTION_DOWN :
    		if(px > (int)(nViewWidth * 0.85)) {
    			View first = getChildAt(0);
    			if (null == first) return true;
    			mItemHeightNormal = first.getHeight();
    			View v = getChildAt(getItemForPosition((int)ev.getY()));
    			if (null == v) return true;

    			onLongClickHandler.view = v;
    			buttonHold.postDelayed(onLongClickHandler, 30);

    			Handler temphandler = new Handler();
    			temphandler.postDelayed(new Runnable() {
					
					@Override
					public void run() {
						// TODO Auto-generated method stub
						ActionUp();
					}
				}, 30);
    	    	
    	    	if (null == mDragView) {
    	    		try {
    	    			return super.onTouchEvent(ev);
    	    		} catch (ArrayIndexOutOfBoundsException e) {
    	    			e.printStackTrace();
    	    			return true;
    	    		}
    			}
    			return true;
    		} else {
    			return super.onTouchEvent(ev);
    		}
    	}
    	return super.onTouchEvent(ev);
    }
    
    private void ActionUp() {
		try{
	    	MotionEvent evt = MotionEvent.obtain(SystemClock.uptimeMillis(), SystemClock.uptimeMillis(), 
	    			MotionEvent.ACTION_UP, 10, 10, 0);
	    	super.onTouchEvent(evt);
			} catch(RuntimeException e){
				e.printStackTrace();
			}
    }

    private void dragMove(int x, int y) {
	dragView(x, y);
	int itemnum = getItemForPosition(y);
	if (itemnum >= 0) {
	    if (itemnum != mDragPos) {
		mDragPos = itemnum;
		doExpansion(); // 처음 드래그한 아이템과 다른 위치에 있을 경우 펼쳐지게 한다.
	    }
	    int speed = 0;
	    adjustScrollBounds(y); // 스크롤 계산
	    if (y > mLowerBound) // 스크롤 최상위
		speed = y > (mHeight + mLowerBound) / 2 ? 16 : 4;
	    else if (y < mUpperBound) // 스크롤 최하위
		speed = y < mUpperBound / 2 ? -16 : -4;
	    if (speed != 0) {
		int ref = getFirstVisiblePosition();
		View v = getChildAt(0);
		if (null != v)
		    {
			int top = v.getTop() - speed;
			int bottom = (getCount() - ref) * mItemHeightNormal + top;
			if (bottom > mHeight)
			    setSelectionFromTop(ref, top);
		    }
	    }
	}
    }

    @Override
    public boolean onLongClick(View item) {
		try{
	    	MotionEvent evt = MotionEvent.obtain(SystemClock.uptimeMillis(), SystemClock.uptimeMillis(), 
	    			MotionEvent.ACTION_UP, 10, 10, 0);
	    	super.onTouchEvent(evt);
			} catch(RuntimeException e){
				e.printStackTrace();
			}
	String s = ((TextView)item.findViewById(R.id.drag_text)).getText().toString();

	if (mDragListener == null || mDropListener == null) return false;
	mItemHeightNormal = item.getHeight(); // 아이템의 높이
	mItemHeightExpanded = mItemHeightNormal * 2; // 아이템이 드래그 할때 벌어지질 높이
	mDragPoint = 0; //y - item.getTop();
	mCoordOffset = mItemHeightNormal / 2;
	int y = item.getTop() + mCoordOffset;
	View dragger = item.findViewById(dndViewId); // 드래그 이벤트를 할 아이템내에서의 뷰
	View first = getChildAt(0);

	int ref = getFirstVisiblePosition();
	if (null != first) setSelectionFromTop(ref, first.getTop() + mItemHeightNormal * ref);

	if (null == dragger) dragger = item;
	startDragging(((TextView)item.findViewById(R.id.drag_text)).getText().toString(), item.getTop() + mCoordOffset);
	if (null == first) {
	    mDragPos = 0;
	}
	else {
	    mDragPos = item.getTop() - first.getTop();
	    mDragPos += (getFirstVisiblePosition() * mItemHeightNormal);
	    mDragPos -= (mDragPos % mItemHeightNormal);
	    mDragPos += mItemHeightNormal / 2;
	    mDragPos /= mItemHeightNormal;
	}

	mFirstDragPos = mDragPos;
	mHeight = getHeight();
	// 스크롤링을 위한 값 획득
	int touchSlop = mTouchSlop;
	mUpperBound = Math.min(y - touchSlop, mHeight / 4);
	mLowerBound = Math.max(y + touchSlop, mHeight * 3 /4);

	mDragListener.drag(mDragPos);

	return true;
    }

    private int getItemForPosition(int y) {
	View v = getChildAt(0);
	if (null == v) return 0;
	y -= v.getTop();
	y -= y % mItemHeightNormal;
	y += mItemHeightNormal / 2;
	return y / mItemHeightNormal;
    }

    private void adjustScrollBounds(int y) {
        if (y >= mHeight / 4)     mUpperBound = mHeight / 4;
        if (y <= mHeight * 3 / 4) mLowerBound = mHeight * 3 / 4;
    }

    private void doExpansion() {
    	View l;
        for (int i = 0; null != (l = getChildAt(i)); ++i) {
        	l.setPadding(0, i == mDragPos ? mItemHeightNormal : 0, 0, 0);
//        	if(i == mDragPos) {
//        		l.setVisibility(View.INVISIBLE);
//        	} else {
//        		l.setVisibility(View.VISIBLE);
//        	}
        }
    }

    private void unExpandViews(boolean deletion) {
    	View l;
    	for (int i = 0; null != (l = getChildAt(i)); ++i) {
    		l.setPadding(0, 0, 0, 0);
    		l.setVisibility(View.VISIBLE);
    	}

    	for (int i = 0;; i++) {
    		View v = getChildAt(i);
    		if (null == v) {
                if (deletion) {
                    int position = getFirstVisiblePosition();
                    int y = getChildAt(0).getTop();
                    setAdapter(getAdapter());
                    setSelectionFromTop(position, y);
                }
                layoutChildren();
                v = getChildAt(i);
                if (null == v) break;
            }
    	}
    }

    // 드래그 시작
    static final LayoutParams FULL_WIDTH = new LayoutParams(LayoutParams.FILL_PARENT, LayoutParams.WRAP_CONTENT);
    private void startDragging(String text, int y) {
        stopDragging();

        mWindowParams = new WindowManager.LayoutParams();
        mWindowParams.gravity = Gravity.TOP;
        mWindowParams.x = dragImageX;
        mWindowParams.y = y - mDragPoint + mCoordOffset;
        
        if(mWindowParams.y > nEndY)
        	mWindowParams.y = nEndY;
        
        mWindowParams.height = WindowManager.LayoutParams.WRAP_CONTENT;
        mWindowParams.width = WindowManager.LayoutParams.FILL_PARENT;
        mWindowParams.flags = WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE
	    | WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE
	    | WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON
	    | WindowManager.LayoutParams.FLAG_LAYOUT_IN_SCREEN; 
        mWindowParams.format = PixelFormat.TRANSLUCENT;
        mWindowParams.windowAnimations = 0;

	View v = ((LayoutInflater)mContext.getSystemService(Context.LAYOUT_INFLATER_SERVICE)).inflate(R.layout.dragndrop_row, null);
	v.setLayoutParams(FULL_WIDTH);
	v.setBackgroundResource(R.drawable.list_selector_background_pressed);
//	int backGroundColor = mContext.getResources().getColor(R.color.dragndrop_background);
//	v.setBackgroundColor(backGroundColor);
	((TextView) v.findViewById(R.id.drag_text)).setText(text);

        mWindowManager = (WindowManager)mContext.getSystemService("window");
        mWindowManager.addView(v, mWindowParams);
        mDragView = v;
    }

    // 드래그를 위해 만들어 준 뷰의 이동
    private void dragView(int x, int y) {
        mWindowParams.y = y - mDragPoint + mCoordOffset;
        if(mWindowParams.y < nStartY)
        	mWindowParams.y = nStartY;
        if(mWindowParams.y > nEndY)
        	mWindowParams.y = nEndY;
        mWindowManager.updateViewLayout(mDragView, mWindowParams);
    }

    // 드래그 종료 처리
    private void stopDragging() {
        if (mDragView != null) {
            WindowManager wm = (WindowManager)mContext.getSystemService("window");
            wm.removeView(mDragView);
            mDragView = null;
        }
    }
    
    public void Pause() {
    	stopDragging();
	    if (dest > getCount()) dest = getCount();
	    if (mDropListener != null && dest >= 0 && dest <= getCount())
		mDropListener.drop(mFirstDragPos, dest);
	    unExpandViews(false);
	    mDragPos = -1;
    }

    /**
     * 드래그 이벤트 리스너 등록
     * @param l 드래그 이벤트 리스너
     */
    public void setDragListener(DragListener l) {
        mDragListener = l;
    }

    /**
     * 드랍 이벤트 리스너 등록
     * @param l 드랍 이벤트 리스너
     */
    public void setDropListener(DropListener l) {
        mDropListener = l;
    }

    /**
     * 리스트 아이템에 있는 뷰들 중 드래그 드랍 이벤트를 발생시킬 뷰의 아이
     * @param id 드래그 드랍 이벤트를 발생시킬 뷰의 아이디
     */
    public void setDndView(int id){
    	dndViewId = id;
    }

    /**
     * 드래그시 표시되는 뷰의 스크린에서의 left padding
     * @param x 스크린에서의 left padding, 설정안하면 0
     */
    public void setDragImageX(int x){
    	dragImageX = x;
    }

    public interface DragListener {
        void drag(int which);
    }
    public interface DropListener {
        void drop(int from, int to);
    }
    
    public void setBoundary(int viewWidth, int startY, int endY) {
    	nViewWidth = viewWidth;
    	nStartY = startY;
    	nEndY = endY;
    }
}
