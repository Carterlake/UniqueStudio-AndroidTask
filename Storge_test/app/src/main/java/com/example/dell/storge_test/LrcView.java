package com.example.dell.storge_test;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Typeface;
import android.util.AttributeSet;

import java.util.ArrayList;
import java.util.List;

public class LrcView extends android.support.v7.widget.AppCompatTextView {
    private float width;
    private float height;
    private Paint currentPaint;
    private Paint notCurrentPaint;
    private float textHeight = 80;
    private float textSize = 50;
    private int index = 0;

    private List<LrcContent> mLrcList = new ArrayList<LrcContent>();

    public void setmLrcList(List<LrcContent> l){
        mLrcList = l;
    }

    public LrcView(Context context){
        super(context);
        init();
    }
    public LrcView(Context context, AttributeSet attrs, int defStyle){
        super(context,attrs,defStyle);
        init();
    }
    public LrcView(Context context, AttributeSet attributeSet){
        super(context,attributeSet);
        init();
    }
    public void setIndex(int i){
        index = i;
    }

    public void init(){
        setFocusable(true);//可对焦
        //高亮，正在播放的歌词
        currentPaint = new Paint();
        currentPaint.setAntiAlias(true);//抗锯齿
        currentPaint.setTextAlign(Paint.Align.CENTER);//文本对齐方式

        //非高亮，其他歌词
        notCurrentPaint = new Paint();
        notCurrentPaint.setAntiAlias(true);
        notCurrentPaint.setTextAlign(Paint.Align.CENTER);
    }

    @Override
    protected void onSizeChanged(int w, int h, int oldw, int oldh) {
        super.onSizeChanged(w, h, oldw, oldh);
        this.width = w;
        this.height = h;
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        if (canvas ==null){
            return;
        }

        currentPaint.setColor(Color.argb(210,251,248,29));//混合三原色
        notCurrentPaint.setColor(Color.argb(140,255,255,255));

        currentPaint.setTextSize(75);
        currentPaint.setTypeface(Typeface.SERIF);

        notCurrentPaint.setTextSize(textSize);
        notCurrentPaint.setTypeface(Typeface.DEFAULT);

        try {
            setText("");
            canvas.drawText(mLrcList.get(index).getLrcStr(),width/2,height/2,currentPaint);

            float tempY = height/2;

            for (int i = index-1; i >= 0; i--){
                tempY = tempY - textHeight;
                canvas.drawText(mLrcList.get(i).getLrcStr(),width/2,tempY,notCurrentPaint);
            }
            tempY = height/2;
            for (int i = index + 1; i < mLrcList.size(); i++){
                tempY = tempY + textHeight;
                canvas.drawText(mLrcList.get(i).getLrcStr(),width/2,tempY,notCurrentPaint);
            }
        }catch (Exception e){
            setText("...没有找到歌词呢...");
        }



    }
}
