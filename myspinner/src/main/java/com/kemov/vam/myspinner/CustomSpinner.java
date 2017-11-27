package com.kemov.vam.myspinner;


import android.content.Context;
import android.graphics.Point;
import android.util.AttributeSet;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.WindowManager;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.TextView;


import java.util.ArrayList;
import java.util.GregorianCalendar;
import java.util.List;

/**
 * Created by RuanJian-GuoYong on 2017/9/9.
 */
/*
* 使用TableRow+PopWindow自定义的Spinner，主要功能是可以默认不选择（留空）
*
* */

public class CustomSpinner extends TableLayout {
    private static final String TAG = "CustomSpinner";
    private Context mCtx;
    private SpinerPopWindow popWindow;
    private List<String> mItems = new ArrayList<String>();
    private int mPosItem = -1;
    private TextView mDetail;
    private View rootView;
    private TableRow mTableRow;

    public CustomSpinner(Context context){
        this(context,null);
    }

    public CustomSpinner(Context context, AttributeSet attrs) {
        super(context, attrs);
        this.mCtx = context;
        //initData();
        initView();
    }

    /*提供给外部的接口*/
    public void setDefault(int pos){
        this.mPosItem = pos;
        updateUI();
    }

    /*提供给外部的接口*/
    public void setDefault(String str){
        if (!mItems.contains(str.trim())) return;
        this.mPosItem = mItems.indexOf(str.trim());
        updateUI();
    }

    public void updateUI(){
        setPortTextByPosition(mPosItem);
    }

    /*提供给外部的接口*/
    public String getValue(){
        return (mPosItem == -1) ? "" : mItems.get(mPosItem);
    }

    /*提供给外部的接口*/
    public int getPosition(){
        return this.mPosItem;
    }

    /*提供给外部的接口*/
    public void setItems(List<String> items){
        this.mItems = items;
        popWindow.refreshData(mItems);
    }

    private void initData() {
        for (int i = 0;i<5;i++)
        mItems.add("item"+i);
    }

    private void initView() {
        final LayoutInflater mInflater = LayoutInflater.from(mCtx);
        rootView = mInflater.inflate(R.layout.custom_spinner, null);
        this.addView(rootView);

        mDetail = (TextView) rootView.findViewById(R.id.comport_text);
        mTableRow = (TableRow) rootView.findViewById(R.id.table_row_spiner_comport);

        mTableRow.setOnClickListener(new OnClickListener() {
            public void onClick(View v) {
                showPortSpinWindow();
            }
        });

        popWindow = new SpinerPopWindow(mCtx);
        popWindow.refreshData(mItems, 0);
        popWindow.setItemListener(new AbstractSpinerAdapter.IOnItemSelectListener() {
            public void onItemClick(int pos) {
                mPosItem = pos;
                updateUI();
            }
        });
    }

    @Override
    public void setEnabled(boolean enabled){
        if (!enabled){
            mTableRow.setOnClickListener(new OnClickListener() {
                @Override
                public void onClick(View v) {

                }
            });
            mTableRow.setEnabled(enabled);
        }
    }

    /*自定义的spinner弹出*/
    private void showPortSpinWindow() {
        popWindow.setWidth(mTableRow.getWidth());
        //popWindow.showAsDropDown(mTableRow);
        //popWindow.showAtLocation(View parent, int gravity, int x, int y)

        //用以获取屏幕的宽高
        WindowManager wm = (WindowManager) getContext()
                .getSystemService(Context.WINDOW_SERVICE);
        Point size = new Point();
        wm.getDefaultDisplay().getSize(size);
        int screenWidth = size.x;
        int screenHeight = size.y;
        Log.d(TAG, "showPortSpinWindow:,screenWidth="+screenWidth+",screenHeight="+screenHeight);

        /*获取控件的宽高和左上角的绝对坐标*/
        int[] location = new int[2] ;
        mTableRow.getLocationInWindow(location); //获取在当前窗口内的绝对坐标
        mTableRow.getLocationOnScreen(location);//获取在整个屏幕内的绝对坐标
        float x = location [0];
        float y = location [1];
        int width = mTableRow.getWidth();
        int height = mTableRow.getHeight();
        Log.d(TAG, "mTableRow(x,y,w,h)=("+x+","+y+","+width+","+height+")");

        //获取 popWindow 的弹出框的宽高
        View popupView = popWindow.getContentView();
        popupView.measure(MeasureSpec.UNSPECIFIED, View.MeasureSpec.UNSPECIFIED);
        int contentWidth = popupView.getMeasuredWidth();
        int contentHeight = popupView.getMeasuredHeight();
        Log.d(TAG, "popupView(contentWidth,contentHeight)=("+contentWidth+","+contentHeight+")");

        //计算 mTableRow跟屏幕顶部/底部的距离
        int space2Top = (int) y;
        int space2Bottom = (int) (screenHeight-y-height);
        Log.d(TAG, "(space2Top,space2Bottom)=("+space2Top+","+space2Bottom+")");

        //偏移量
        int offX = 0, offY = 0;
        offX = (int) x;

        //当不管显示下边还是上边，空间都不够时,需要设置内容的高度！
        if (contentHeight>space2Top && contentHeight>space2Bottom){
            boolean bigger = space2Top>space2Bottom;
            contentHeight = bigger?space2Top:space2Bottom;
            popWindow.setHeight(contentHeight);
            if (bigger){
                offY = (int) (y-contentHeight);
            } else {
                offY = (int) (y+height);
            }

        }//显示在上边
        else if (contentHeight>space2Bottom){
            offY = (int) (y-contentHeight);
        }//显示在下边
        else {
            offY = (int) (y+height);
        }
        Log.d(TAG, "(offX,offY)=("+offX+","+offY+")");

        //popWindow.showAtLocation(mTableRow.getRootView(), Gravity.NO_GRAVITY, offX, offY);
        popWindow.showAtLocation(mTableRow.getRootView(), Gravity.NO_GRAVITY, offX, offY);
    }


    //给Spinner的文本显示赋值
    private void setPortTextByPosition(int pos) {
        if (pos >= 0 && pos < mItems.size()) {
            String value = mItems.get(pos);
            mDetail.setText(value);
            //this.mPosItem = pos;
        }
    }
}

