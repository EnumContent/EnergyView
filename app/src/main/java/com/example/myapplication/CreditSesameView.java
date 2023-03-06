package com.example.myapplication;


import android.annotation.SuppressLint;
import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.PaintFlagsDrawFilter;
import android.graphics.RectF;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.VectorDrawable;
import android.text.style.LeadingMarginSpan;
import android.util.AttributeSet;
import android.view.View;

import androidx.core.content.res.ResourcesCompat;


/**
 * 仪表盘控件
 */
public class CreditSesameView extends View {

    //画布抗锯齿
    private PaintFlagsDrawFilter mPaintFlagsDrawFilter;

    // 宽度
    private int width;
    // 高度
    private int height;
    //控件半径
    private int radius;

    //外圆弧进度 不可直接使用,需要用get方法获取
    private int outProgress = 0;
    //内圆弧进度 不可直接使用,需要用get方法获取
    private int inProgress = 0;
    //最大进度
    private int maxProgress = 100;

    //外圆弧背景画笔
    private Paint paintOutCircleBg;
    //外圆弧进度画笔
    private Paint paintOutCircle;
    //外圆弧范围
    private RectF outCircleRcf;
    //外圆弧线宽
    private int outCircleWidth = dp2px(8);

    //外圆弧刻度画笔
    private Paint outCalibrationPaint;
    //外圆弧刻度线和圆弧的边距
    private int outCircleAndCalPad = dp2px(2);
    //外圆弧刻度数量
    private int outCalibrationNumber = 7;
    //外圆弧刻度数量
    private int outCalibrationWith = dp2px(1);

    //内外圆弧间隔
    private int inAndOutPad = dp2px(2);
    //内圆弧进度画笔
    private Paint paintInCircle;
    //内圆弧范围
    private RectF inCircleRcf;
    //内圆弧线宽
    private int inCircleWidth = dp2px(4);

    // 指针图片
    private VectorDrawable pointRes;


    public CreditSesameView(Context context) {
        this(context, null);
    }


    public CreditSesameView(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public CreditSesameView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init(attrs);
    }

    /**
     * 初始化
     *
     * @param attrs
     */
    private void init(AttributeSet attrs) {
        TypedArray array = getContext().obtainStyledAttributes(attrs, R.styleable.CreditSesameView);
        radius = (int) array.getDimension(R.styleable.CreditSesameView_cs_radius, 0);
        outProgress = array.getInteger(R.styleable.CreditSesameView_cs_out_progress, 0);
        inProgress = array.getInteger(R.styleable.CreditSesameView_cs_in_progress, 0);
        array.recycle();
        pointRes = (VectorDrawable) getResources().getDrawable(R.drawable.ic_pointer);

        //设置图片线条的抗锯齿
        mPaintFlagsDrawFilter = new PaintFlagsDrawFilter
                (0, Paint.ANTI_ALIAS_FLAG | Paint.FILTER_BITMAP_FLAG);

        //外圆弧背景画笔
        paintOutCircleBg = new Paint();
        paintOutCircleBg.setColor(Color.parseColor("#07ccd1"));
        paintOutCircleBg.setAlpha((int) (255 * 0.16));
        paintOutCircleBg.setStrokeCap(Paint.Cap.ROUND);
        paintOutCircleBg.setStyle(Paint.Style.STROKE);
        paintOutCircleBg.setStrokeWidth(outCircleWidth);

        //外圆弧进度画笔
        paintOutCircle = new Paint();
        paintOutCircle.setColor(Color.parseColor("#07ccd1"));
        paintOutCircle.setStrokeCap(Paint.Cap.ROUND);
        paintOutCircle.setStyle(Paint.Style.STROKE);
        paintOutCircle.setStrokeWidth(outCircleWidth);

        //内圆弧进度画笔
        paintInCircle = new Paint();
        paintInCircle.setColor(Color.parseColor("#ff3000"));
        paintInCircle.setStrokeCap(Paint.Cap.ROUND);
        paintInCircle.setStyle(Paint.Style.STROKE);
        paintInCircle.setStrokeWidth(inCircleWidth);

        //外圆弧刻度画笔
        outCalibrationPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        outCalibrationPaint.setStrokeCap(Paint.Cap.ROUND);
        outCalibrationPaint.setColor(Color.WHITE);
        outCalibrationPaint.setAlpha((int) (255 * 0.8));
        outCalibrationPaint.setStyle(Paint.Style.STROKE);
        outCalibrationPaint.setStrokeWidth(outCalibrationWith);

    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        setMeasuredDimension(radius * 2, radius + outCircleWidth / 2);
    }

    @Override
    protected void onSizeChanged(int w, int h, int oldw, int oldh) {
        super.onSizeChanged(w, h, oldw, oldh);
        width = w;
        height = h;
        int outCircleRadius = outCircleWidth / 2;
        int inCircleRadius = inCircleWidth / 2;
        outCircleRcf = new RectF(outCircleRadius, outCircleRadius, width - outCircleRadius, width - outCircleRadius);
        inCircleRcf = new RectF(outCircleWidth + inAndOutPad + inCircleRadius, outCircleWidth + inAndOutPad + inCircleRadius, width - (outCircleWidth + inAndOutPad + inCircleRadius), width - (outCircleWidth + inAndOutPad + inCircleRadius));
    }

    @SuppressLint("DrawAllocation")
    @Override
    protected void onDraw(Canvas canvas) {
        //设置画布绘图无锯齿
        canvas.setDrawFilter(mPaintFlagsDrawFilter);
        drawOutCircle(canvas);
        drawInCircle(canvas);
        drawPoint(canvas);
        drawCalibration(canvas);
    }

    /**
     * @param outProgress 外圆进度 0-100
     * @param inProgress  内圆进度 0-100
     */
    public void setProgress(double outProgress, double inProgress) {
        this.outProgress = (int) outProgress;
        this.inProgress = (int) inProgress;
        requestLayout();
        postInvalidate();
    }

    /**
     * 绘制大小刻度线
     */
    private void drawCalibration(Canvas canvas) {

        for (int i = 0; i < outCalibrationNumber; i++) {
            canvas.save();
            canvas.rotate(180 / (outCalibrationNumber - 1) * i, radius, radius);
            canvas.drawLine(outCircleAndCalPad, radius, outCircleWidth - outCalibrationNumber, radius, outCalibrationPaint);
            canvas.restore();
        }
    }

    /**
     * 外圈进度校验
     *
     * @return
     */
    public int getOutProgress() {
        if (outProgress > maxProgress) {
            return maxProgress;
        }

        if (outProgress < 0) {
            return 0;
        }
        return outProgress;
    }

    /**
     * 内圈进度校验
     *
     * @return
     */
    public int getInProgress() {
        if (inProgress > maxProgress) {
            return maxProgress;
        }
        if (inProgress < 0) {
            return 0;
        }
        return inProgress;
    }

    /**
     * 绘制指针
     *
     * @param canvas
     */


    private void drawPoint(Canvas canvas) {
        if (pointRes == null) {
            return;
        }
        int intrinsicWidth = (int) ((inCircleRcf.right - inCircleRcf.left) / 2);
        int intrinsicHeight = intrinsicWidth * 8 / 26;
        int radio = intrinsicHeight / 2;
        int pointX = radius;
        int pointY = radius+outCircleWidth/2 - radio;
        canvas.save();
        canvas.rotate(180 * getOutProgress() / maxProgress, pointX, pointY);
        pointRes.setBounds(pointX - intrinsicWidth + radio, pointY - intrinsicHeight + radio, pointX + radio, pointY + radio);
        pointRes.draw(canvas);
        canvas.restore();

    }

    /**
     * 绘制内层圆弧
     *
     * @param canvas
     */
    private void drawInCircle(Canvas canvas) {
        canvas.save();
        canvas.rotate(180, radius, radius);
        canvas.drawArc(inCircleRcf, 0, 180 * getInProgress() / maxProgress, false, paintInCircle);
        canvas.restore();
    }

    /**
     * 绘制外层圆弧
     *
     * @param canvas
     */
    private void drawOutCircle(Canvas canvas) {
        canvas.save();
        canvas.rotate(180, radius, radius);
        canvas.drawArc(outCircleRcf, 0, 180, false, paintOutCircleBg);
        canvas.drawArc(outCircleRcf, 0, 180 * getOutProgress() / maxProgress, false, paintOutCircle);
        canvas.restore();
    }

    public int dp2px(int values) {

        float density = getResources().getDisplayMetrics().density;
        return (int) (values * density + 0.5f);
    }

}
