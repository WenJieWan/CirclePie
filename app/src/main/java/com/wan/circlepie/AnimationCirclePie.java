package com.wan.circlepie;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Rect;
import android.graphics.RectF;
import android.util.AttributeSet;
import android.util.TypedValue;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.Transformation;

import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.List;
/**
 * Created by 万文杰 on 2017/2/17.
 */

/**
 * 带动画的自定义饼形图
 * 作者：cg
 * 时间：2016/9/6 0006 下午 2:48
 */
public class AnimationCirclePie extends View {

    private List<CirclePieValue> circlePieValues;

    private int StrokeWidth =  120;                                //弧的宽度
    private int TextSize = 48;                                     //文字大小
    private boolean isAnimation =true;                           //是否使用动画效果
    private int IntervalWidth =1;                                  //区间宽度
    private int AnimationSpeed = 4;                                //动画的速度
    private int circleCenterColor = Color.parseColor("#ffffff");   //圆心颜色，默认是白色



    private Paint mPaint;                                          //画笔
    private RectF mRectf;                                          //外圈圆的方形
    private Rect mBound;                                           //文字外框

    private String txtNumPre;                                      //显示所占百分比
    float x_offset;                                                 //记录弧度最外边中心点的X坐标
    float y_offset;                                                 //记录弧度最外边中心点的Y坐标
    float line_x_offset;                                           //记录弧度最外边中心点的X坐标
    float line_y_offset;                                           //记录弧度最外边中心点的Y坐标

    private int adjustDist = 40;                                   //在弧度上面的线，为了调整其最好的显示位置设置一个调整的距离数
    private int circleOutLineDist = 100;                         //第一段偏移线段的长度

    private float txtBeginX = 0;                                   //线上文字开始的位置的X坐标值
    private float txtBeginY = 0;                                   //线上文字开始的位置的y坐标值
    private float txtEndX =0;                                      //线上文字结束的位置的x坐标值


    //计算总数
    private float sum;

    //动画初始值
    private float per = 0;

    public class BarAnimation extends Animation {

        public BarAnimation() {
        }
        @Override
        protected void applyTransformation(float interpolatedTime, Transformation t) {
            super.applyTransformation(interpolatedTime, t);
            if (interpolatedTime < 1.0f) {
                per =  interpolatedTime;
                postInvalidate();
            } else {
                per = 1.0f;
            }
        }
    }

    public AnimationCirclePie(Context context) {
        this(context,null);
    }

    public AnimationCirclePie(Context context, AttributeSet attrs) {
        this(context, attrs,0);
    }

    public AnimationCirclePie(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        circlePieValues= new ArrayList<>();
        circlePieValues.add(new CirclePieValue("  ",1,Color.WHITE));
        TypedArray array = getContext().getTheme().obtainStyledAttributes(attrs, R.styleable.MyPre, defStyleAttr, 0);
        int n = array.getIndexCount();
        for(int i=0;i<n;i++)
        {
            int arr = array.getIndex(i);
            switch (arr)
            {
                case R.styleable.MyPre_StrokeWidth:
                    StrokeWidth = array.getInt(arr, 200);
                    break;
                case R.styleable.MyPre_textSizes:
                    TextSize = array.getDimensionPixelSize(arr, (int) TypedValue.applyDimension(
                            TypedValue.COMPLEX_UNIT_SP, 12, getResources().getDisplayMetrics()));
                    break;
                case R.styleable.MyPre_isAnimation:
                    isAnimation = array.getBoolean(arr,false);
                    break;
                case R.styleable.MyPre_AnimationSpeed:
                    AnimationSpeed = array.getInt(arr,2);
                    break;
                case R.styleable.MyPre_circleCenterColor:
                    circleCenterColor = array.getColor(arr,Color.parseColor("#ffffff"));
                    break;
            }
        }

        array.recycle();

        //初始化画笔
        mPaint = new Paint();
        mPaint.setAntiAlias(true);
        mPaint.setStrokeWidth(5);
        mPaint.setStyle(Paint.Style.FILL);

        mRectf = new RectF();
        mBound = new Rect();
        calculateData();

    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
        int widthMode = MeasureSpec.getMode(widthMeasureSpec);
        int widthSize = MeasureSpec.getSize(widthMeasureSpec);

        int heightMode = MeasureSpec.getMode(heightMeasureSpec);
        int heightSize = MeasureSpec.getSize(heightMeasureSpec);

        int width ;
        int height ;

        if(widthMode==MeasureSpec.EXACTLY)
        {
            width = widthSize;
        }else
        {
            width = getPaddingLeft() + getWidth() + getPaddingRight();
        }

        if(heightMode==MeasureSpec.EXACTLY)
        {
            height = heightSize;
        }else
        {
            height = getPaddingTop() + getHeight() + getPaddingBottom();
        }

        setMeasuredDimension(width, height);
    }

    @Override
    protected void onDraw(final Canvas canvas) {
        super.onDraw(canvas);

        float radius;        //定义半径
        float left;
        float top;
        float right;
        float bottom;

        //圆环的半径取控件的高度一半，减去60,是为了给上下各留出30显示文字的空间
        radius = getHeight()/2 - 110;
        left = getWidth()/2 - radius  ;
        top = getHeight()/2 -radius  ;
        right = getWidth()/2 + radius;
        bottom = getHeight()/2 +  radius;
        mRectf.set(left, top, right, bottom);

        if(isAnimation) {
            /**
             * 画出扇面
             */
            float startAngle=270;
            for (CirclePieValue cpv:circlePieValues) {
                mPaint.setColor(cpv.getColor());
                float sweepAngle =cpv.getAngle();
                canvas.drawArc(mRectf, startAngle+IntervalWidth,(sweepAngle-IntervalWidth)* per, true, mPaint);
                startAngle+=sweepAngle;
/*
                per = per + AnimationSpeed;
                if (per <= sweepAngle-IntervalWidth) {
                    postInvalidate();
                }*/

            }
            DrawDetail(canvas, radius);

        }else {
            /**
             * 画出扇面
             */
            float startAngle=270;
            for (CirclePieValue cpv:circlePieValues) {
                mPaint.setColor(cpv.getColor());
                float sweepAngle =cpv.getAngle();
                canvas.drawArc(mRectf, startAngle+IntervalWidth, sweepAngle-IntervalWidth, true, mPaint);
                startAngle+=sweepAngle;
            }
            DrawDetail(canvas, radius);
        }
        /**
         * 画出中间的空白区域
         */
        mPaint.setColor(circleCenterColor);
        canvas.drawCircle(getWidth() / 2, getHeight() / 2, radius - StrokeWidth, mPaint);
    }

    public void DrawDetail(Canvas canvas,float radius)
    {
        float sumAngle=0;
        for (CirclePieValue cpv:circlePieValues) {
        if(cpv.getNumber()>0)
        {
            //设置显示的数量，在横线上显示的文字
            mPaint.setColor(cpv.getColor());
            mPaint.setTextSize(TextSize);
            String text = cpv.getText();
            mPaint.getTextBounds(text, 0, text.length(), mBound);
            //计算当前角度的一半,累加之前的角度，再减去90度算X、Y的值
            float currentAngle=sumAngle+cpv.getAngle()/ 2;
            double radian= Math.PI * (Math.abs(90 - currentAngle)) / 180;
            if(currentAngle>180) radian= Math.PI * (Math.abs(270 - currentAngle)) / 180;
            //计算弧度的中心点
            x_offset = (float)(radius * Math.cos(radian));//记录弧度最外边中心点的X坐标
            y_offset = (float)(radius * Math.sin(radian));//记录弧度最外边中心点的Y坐标
            //计算增量线段偏移距离
            line_x_offset=(float)((radius+circleOutLineDist) * Math.cos(radian));
            line_y_offset=(float)((radius+circleOutLineDist) * Math.sin(radian));
            //角度累加保存,下一个计算使用
            sumAngle+=cpv.getAngle();
            //画弧上的线和文字
            drawLine(canvas, currentAngle, radius, 1,text);
        }
        }
    }

    /**
     * 根据弧度值，来画出弧度中心点伸出的线
     * @param canvas    画板
     * @param sumAngle    弧度之和
     * @param radius    中心半径
     * @param flag      标识位，1:左侧流出单　2:右侧注入单
     */
    public void drawLine(Canvas canvas,float sumAngle,float radius,int flag,String text)
    {
        //画线后的矩形
        txtNumPre = getProValText(circlePieValues.get(0).getPrecent());
        mPaint.getTextBounds(txtNumPre, 0, txtNumPre.length(), mBound);
        mPaint.setStyle(Paint.Style.FILL);

        if(sumAngle>270){
            drawMore270(canvas,text);
              /*=====  此写法兼容api 19 =====*/
            canvas.drawRect(new RectF(txtEndX - 30, txtBeginY - mBound.height() / 2 +10, txtEndX - 30 + adjustDist/2 ,
                    txtBeginY - mBound.height() / 2 +10 + adjustDist/2), mPaint);
        }else if(sumAngle>180){
            drawMore180(canvas,text);
            canvas.drawRect(new RectF(txtEndX - 30, txtBeginY - mBound.height() / 2 +10, txtEndX - 30 + adjustDist/2 ,
                    txtBeginY - mBound.height() / 2 +10 + adjustDist/2), mPaint);
        }else if(sumAngle>90){
            drawMore90(canvas,text);
            canvas.drawRect(new RectF(txtEndX + 10, txtBeginY - mBound.height() / 2 +10, txtEndX + 10 + adjustDist/2 ,
                    txtBeginY - mBound.height() / 2 +10 + adjustDist/2), mPaint);
        }else {
            drawMore0(canvas,text);
            canvas.drawRect(new RectF(txtEndX + 10, txtBeginY - mBound.height() / 2 +10, txtEndX + 10 + adjustDist/2 ,
                    txtBeginY - mBound.height() / 2 +10 + adjustDist/2), mPaint);
        }
    }

    /**
     * 画出弧度中心点小于90弧度的上面的线与值
     * 这里的弧度中心点小于90,是它前面的所有弧度加上它本身的总度数的一半，如果是第一个弧，则为其本身的一半，此处画出的是90度以下，带有向下折线的两根线
     * @param canvas   画板
     */
    public void drawMore0(Canvas canvas,String text)
    {
        txtBeginX = getWidth()/2 + line_x_offset;
        txtBeginY = getHeight()/2 - line_y_offset;
        //第一段线
        canvas.drawLine(getWidth()/2 + x_offset, getHeight()/2 - y_offset, txtBeginX, txtBeginY,mPaint);
        //第二段线
        txtEndX = txtBeginX + mBound.width()*1.3f;
        canvas.drawLine(txtBeginX,txtBeginY, txtEndX,txtBeginY,mPaint);
        canvas.drawText(text, txtBeginX+20, txtBeginY - 20, mPaint);
    }


    /**
     * 画出弧度中心点大于100小于170弧度上面的线与值
     * 这里的弧度中心点大于100小于170,是它前面的所有弧度加上它本身的总度数的一半，如果是第一个弧，则为其本身的一半,此处画出的是左侧100-170度之间，带有向下折线的两根线
     * @param canvas　　　画板
     */
    public void drawMore90(Canvas canvas,String text)
    {
        txtBeginX = getWidth()/2 + line_x_offset;
        txtBeginY = getHeight()/2 + line_y_offset;
        canvas.drawLine(getWidth()/2 + x_offset, getHeight()/2 + y_offset, txtBeginX, txtBeginY, mPaint);
        txtEndX = txtBeginX + mBound.width()*1.3f;
        canvas.drawLine(txtBeginX, txtBeginY, txtEndX, txtBeginY, mPaint);
        canvas.drawText(text, txtBeginX+20, txtBeginY - 20, mPaint);
    }

    /**
     * 画出弧度中心点大于180,小于270弧度上面的线与值
     * 这里的弧度中心点大于180小于270,是它前面的所有弧度加上它本身的总度数的一半，此处画出的是左侧180-270度之间，右边带有垂直向下折线的两根线
     * @param canvas　　　画板
     */
    public void drawMore180(Canvas canvas,String text)
    {
        txtBeginX = getWidth() / 2 -line_x_offset;
        txtBeginY = getHeight() / 2 +line_y_offset;
        canvas.drawLine(getWidth() / 2 - x_offset, getHeight() / 2 + y_offset, txtBeginX, txtBeginY, mPaint);
        txtEndX = txtBeginX - mBound.width()*1.3f;
        canvas.drawLine(txtBeginX, txtBeginY, txtEndX, txtBeginY, mPaint);
        canvas.drawText(text, txtEndX+20, txtBeginY - 20, mPaint);
    }


    /**
     * 画出弧度中心点大于270,小于360弧度上面的线与值
     * 这里的弧度中心点大于270小于360,是它前面的所有弧度加上它本身的总度数的一半，此处画出的是左侧270-360度之间，右边向上折的两根直线
     * @param canvas     画板
     */
    public void drawMore270(Canvas canvas,String text)
    {
        txtBeginX = getWidth()/2 - line_x_offset ;
        txtBeginY = getHeight()/2 - line_y_offset;
        canvas.drawLine(getWidth() / 2 - x_offset, getHeight() / 2 - y_offset, txtBeginX, txtBeginY, mPaint);
        txtEndX = txtBeginX - mBound.width()*1.3f;
        canvas.drawLine(txtBeginX, txtBeginY, txtEndX, txtBeginY, mPaint);
        canvas.drawText(text, txtEndX+20, txtBeginY - 20, mPaint);
    }


    /**
     * 格式化显示的百分比
     * @param proValue   传入的数值一般是0.1234这种格式的
     * @return           返回一个小数点后一位的百分比字符串
     */
    private String getProValText(float proValue)
    {
        DecimalFormat format = new DecimalFormat("#0.0");
        return format.format(proValue * 100) + "%";
    }

    public void setPieData(List<CirclePieValue> circlePieValue)
    {
        this.circlePieValues=circlePieValue;
        calculateData();
        per = 0f;
        BarAnimation animation=new BarAnimation();
        animation.setDuration(1000);
        this.startAnimation(animation);
        postInvalidate();
    }
    public void calculateData(){
        //计算总数
        sum = 0;
        for (CirclePieValue cpv:circlePieValues) {
            sum+=cpv.getNumber();
        }
        //计算值的百分比、所占圆的弧度数
        for(CirclePieValue cpv:circlePieValues){
            float precent=cpv.getNumber()/ sum;
            cpv.setPrecent(precent);
            cpv.setRadian(precent*360);
        }
    }
}