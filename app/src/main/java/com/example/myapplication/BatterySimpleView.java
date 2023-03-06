package com.example.myapplication;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.RectF;
import android.util.AttributeSet;
import android.view.View;

/**
 * 简单电池控件
 */
public class BatterySimpleView extends View {
    private Paint paint;
    private Path headPath;//电池的小尖尖

    int width, height;
    private RectF outerDefaultRect;
    private RectF inSelectRect;
    private float outRadius;//<!--外边框圆角-->
    private float inRadius;//<!--内容圆角-->
    private int inPower;//进度
    private float outStorkWidth;//外边线的宽度
    private int outColorDefault;//线条底色
    private int inColorDefault;//填充颜色底色
    private int inColorSelect;//选中填充颜色

    public BatterySimpleView(Context context) {
        this(context,null);
    }

    public BatterySimpleView(Context context, AttributeSet attrs) {
        this(context, attrs,0);
    }

    public BatterySimpleView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        if (attrs!=null){
            TypedArray attributes = context.obtainStyledAttributes(attrs, R.styleable.BatterySimpleView);
            outRadius=attributes.getDimension(R.styleable.BatterySimpleView_bsv_outRadius,dp2px(7));
            inRadius=attributes.getDimension(R.styleable.BatterySimpleView_bsv_inRadius,dp2px(4));
            inPower=attributes.getInt(R.styleable.BatterySimpleView_bsv_inPower,0);
            outStorkWidth=attributes.getDimension(R.styleable.BatterySimpleView_bsv_outStorkWidth,dp2px(2));
            outColorDefault=attributes.getColor(R.styleable.BatterySimpleView_bsv_outColorDefault,Color.parseColor("#e0e0e0"));
            inColorDefault=attributes.getColor(R.styleable.BatterySimpleView_bsv_inColorDefault,Color.parseColor("#f7f7f7"));
            inColorSelect=attributes.getColor(R.styleable.BatterySimpleView_bsv_inColorSelect,Color.parseColor("#02D644"));
            attributes.recycle();
        }
        init();
    }

    private void init() {

        paint = new Paint();
        paint.setAntiAlias(true);
        paint.setStyle(Paint.Style.FILL);
        paint.setColor(outColorDefault);
        headPath = new Path();
        outerDefaultRect = new RectF();
        inSelectRect = new RectF();
        setBackgroundColor(Color.WHITE);
    }

    private float dp2px(float dp) {
        float scale = getContext().getResources().getDisplayMetrics().density;
        return dp * scale + 0.5f;
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        //绘制底层填充
        paint.setStrokeWidth(outStorkWidth);
        paint.setStyle(Paint.Style.FILL);
        paint.setColor(inColorDefault);
        canvas.drawRoundRect(outerDefaultRect, outRadius, outRadius, paint);

        //绘制电池尖尖
        drawHead(canvas, outColorDefault);

        // 绘制外框
        drawOutLine(canvas);

        // 绘制电池内部电量
        if (inPower > 0) {
            paint.setColor(inColorSelect);
            paint.setStyle(Paint.Style.FILL);
            canvas.drawRoundRect(inSelectRect, inRadius, inRadius, paint);
        }
    }

    private void drawOutLine(Canvas canvas) {
        paint.setStrokeWidth(outStorkWidth);
        paint.setStyle(Paint.Style.STROKE);
        canvas.drawRoundRect(outerDefaultRect, outRadius, outRadius, paint);
    }

    private void drawHead(Canvas canvas, int lineColorDefault) {
        paint.setStrokeWidth(outStorkWidth);
        paint.setColor(lineColorDefault);
        paint.setStyle(Paint.Style.FILL);
        canvas.drawPath(headPath, paint);
    }




    @Override
    protected void onSizeChanged(int w, int h, int oldw, int oldh) {
        super.onSizeChanged(w, h, oldw, oldh);
        width = getWidth() - getPaddingLeft() - getPaddingRight();
        height =getHeight() - getPaddingTop() - getPaddingBottom();
        calculationValue();
    }

    private void calculationValue() {
        //计算电池尖尖
        int trapezoidHeight = height / 5; // 梯形高度
        trapezoidHeight=Math.max(trapezoidHeight,15);
        int trapezoidWidth = trapezoidHeight / 3; // 梯形宽度
        //int trapezoidWidth = 5; // 梯形宽度
        int radius = trapezoidWidth / 2; // 圆角半径
        int p1x = width - trapezoidWidth;//左上角第一个点
        int p1y = height / 2 - trapezoidHeight / 2;
        headPath.reset();
        headPath.moveTo(p1x, p1y);
        headPath.quadTo(p1x + trapezoidWidth, p1y, p1x + trapezoidWidth, p1y + radius);
        headPath.lineTo(p1x + trapezoidWidth, p1y + trapezoidHeight - radius);
        headPath.quadTo(p1x + trapezoidWidth, p1y + trapezoidHeight, p1x, p1y + trapezoidHeight);
        headPath.close();

        // 计算电池外框矩形的位置和大小
        outerDefaultRect.set(outStorkWidth / 2, outStorkWidth / 2 , width - trapezoidWidth, height - outStorkWidth / 2);

        // 计算电池内部电量矩形的位置和大小
        float powerPadding = outStorkWidth / 2 + outStorkWidth;
        float rightMax = outerDefaultRect.right - outStorkWidth / 2 - outStorkWidth;
        float rightMin = outerDefaultRect.left + powerPadding;
        inSelectRect.set(outerDefaultRect.left + powerPadding, outerDefaultRect.top + powerPadding, rightMin + (rightMax - rightMin) * inPower / 100, outerDefaultRect.bottom - powerPadding);
    }

    public void setPower(int inPower) {
        if (inPower < 0) {
            inPower = 0;
        } else if (inPower > 100) {
            inPower = 100;
        }
        this.inPower = inPower;
        calculationValue();
        invalidate();
    }

    public int getInPower() {
        return inPower;
    }
}
