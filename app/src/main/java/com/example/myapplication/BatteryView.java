package com.example.myapplication;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.CornerPathEffect;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.RectF;
import android.graphics.drawable.VectorDrawable;
import android.util.AttributeSet;
import android.view.View;

/**
 * 电池控件
 */
public class BatteryView extends View {
    private Paint paint;
    private Path headPath;//电池的小尖尖
    private Path topTrPath;//顶上的三角
    private Path bottomTrPath;//底下的三角

    int width, height;
    private RectF outerDefaultRect;
    private RectF inSelectRect;
    private RectF outSelectRect;
    private float outRadius = dp2px(7);
    private float inRadius = dp2px(4);
    private int inPower = 21;
    private int outPower = 66;
    private float outStorkWidth = dp2px(2);//外边线的宽度
    private int outColorDefault = Color.parseColor("#e0e0e0");//线条底色
    private int outColorSelect = Color.parseColor("#FF3000");//选中的线条颜色
    private int inColorDefault = Color.parseColor("#f7f7f7");//填充颜色底色
    private int inColorSelect = Color.parseColor("#02D644");//选中填充颜色
    private VectorDrawable lightning;//闪电

    public BatteryView(Context context) {
        this(context,null);
    }

    public BatteryView(Context context, AttributeSet attrs) {
        this(context, attrs,0);
    }

    public BatteryView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        if (attrs!=null){
            TypedArray attributes = context.obtainStyledAttributes(attrs, R.styleable.BatteryView);
            outRadius=attributes.getDimension(R.styleable.BatteryView_bv_outRadius,dp2px(7));
            inRadius=attributes.getDimension(R.styleable.BatteryView_bv_inRadius,dp2px(4));
            inPower=attributes.getInt(R.styleable.BatteryView_bv_inPower,0);
            outPower=attributes.getInt(R.styleable.BatteryView_bv_outPower,0);

            outStorkWidth=attributes.getDimension(R.styleable.BatteryView_bv_outStorkWidth,dp2px(2));

            outColorSelect=attributes.getColor(R.styleable.BatteryView_bv_outColorSelect,Color.parseColor("#FF3000"));
            outColorDefault=attributes.getColor(R.styleable.BatteryView_bv_outColorDefault,Color.parseColor("#e0e0e0"));
            inColorDefault=attributes.getColor(R.styleable.BatteryView_bv_inColorDefault,Color.parseColor("#f7f7f7"));
            inColorSelect=attributes.getColor(R.styleable.BatteryView_bv_inColorSelect,Color.parseColor("#02D644"));
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
        topTrPath = new Path();
        bottomTrPath = new Path();
        outerDefaultRect = new RectF();
        inSelectRect = new RectF();
        outSelectRect = new RectF();
        setBackgroundColor(Color.WHITE);
        lightning = (VectorDrawable) getResources().getDrawable(R.drawable.ic_lightning);
    }

    private float dp2px(float dp) {
        float scale = getContext().getResources().getDisplayMetrics().density;
        return dp * scale + 0.5f;
    }

    public float getPointWidth() {
        return outStorkWidth * 1.5f;
    }

    public float getPointWithOutGap() {
        return outStorkWidth / 3f;
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

            //绘制小闪电
            if (inPower>=20){
                lightning.draw(canvas);
            }

        }

        //绘制选中的边框
        if (outPower>0){
            canvas.save();
            canvas.clipRect(0, 0, outSelectRect.right, height);
            drawHead(canvas, outColorSelect);
            drawOutLine(canvas);
            canvas.restore();
        }

        //绘制选中边框的圆头
        if (outPower>=10&&outPower<=86){
            paint.setStyle(Paint.Style.FILL);
            canvas.drawCircle(outSelectRect.right,outSelectRect.top, outStorkWidth /2,paint);
            canvas.drawCircle(outSelectRect.right,outSelectRect.bottom, outStorkWidth /2,paint);
        }

        //绘制选中的三角
        paint.setColor(outColorSelect);
        paint.setStyle(Paint.Style.FILL);
        paint.setPathEffect(new CornerPathEffect(outStorkWidth / 2));
        canvas.drawPath(topTrPath, paint);
        canvas.drawPath(bottomTrPath, paint);

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
        int trapezoidWidth = trapezoidHeight / 3; // 梯形宽度
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
        float pointWidth = getPointWidth();
        outerDefaultRect.set(outStorkWidth / 2, outStorkWidth / 2 + pointWidth + getPointWithOutGap(), width - trapezoidWidth, height - outStorkWidth / 2 - pointWidth - getPointWithOutGap());

        // 计算电池内部电量矩形的位置和大小
        float powerPadding = outStorkWidth / 2 + outStorkWidth;
        float rightMax = outerDefaultRect.right - outStorkWidth / 2 - outStorkWidth;
        float rightMin = outerDefaultRect.left + powerPadding;
        inSelectRect.set(outerDefaultRect.left + powerPadding, outerDefaultRect.top + powerPadding, rightMin + (rightMax - rightMin) * inPower / 100, outerDefaultRect.bottom - powerPadding);


        //计算外层框选中的范围
        outSelectRect.set(outerDefaultRect.left, outerDefaultRect.top, outerDefaultRect.left+(outerDefaultRect.right+trapezoidWidth-outerDefaultRect.left)*outPower / 100, outerDefaultRect.bottom);

        //计算顶上的三角形
        float centerX=outSelectRect.right;
        if (centerX+getPointWidth()>width){
            centerX=width-getPointWidth();
        }else if (centerX<getPointWidth()){
            centerX=getPointWidth();
        }
        topTrPath.reset();
        topTrPath.moveTo(centerX, pointWidth);
        topTrPath.lineTo(centerX - pointWidth, 0);
        topTrPath.lineTo(centerX + pointWidth, 0);

        //topTrPath.arcTo(powerRect.right,pointWidth,powerRect.right-pointWidth,0,-180,180,false);
        topTrPath.close();

        //计算底下的三角形
        bottomTrPath.reset();
        bottomTrPath.moveTo(centerX, height - pointWidth);
        bottomTrPath.lineTo(centerX - pointWidth, height);
        bottomTrPath.lineTo(centerX + pointWidth, height);
        bottomTrPath.close();

        //闪电位置
        int height = (int) ((inSelectRect.bottom - inSelectRect.top) / 2);
        int width = height / 14 * 10;
        int cx = (int) (inSelectRect.left + (inSelectRect.right - inSelectRect.left) / 2);
        int cy= (int) (inSelectRect.top + (inSelectRect.bottom - inSelectRect.top) / 2);
        lightning.setBounds(cx-width/2,cy-height/2,cx+width/2,cy+height/2);
    }

    public void setPower(int inPower,int outPower) {
        if (inPower < 0) {
            inPower = 0;
        } else if (inPower > 100) {
            inPower = 100;
        }
       /* if (outPower<12){
            outPower=12;
        }else if(outPower>85){
            outPower=100;
        }*/
        this.inPower = inPower;
        this.outPower=outPower;
        calculationValue();
        invalidate();
    }

    public int getInPower() {
        return inPower;
    }

    public int getOutPower() {
        return outPower;
    }
}
