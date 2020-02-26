package com.example.smailratingbar;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.drawable.Drawable;
import android.util.AttributeSet;
import android.view.Gravity;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewTreeObserver;
import android.widget.ImageView;
import android.widget.LinearLayout;

import androidx.annotation.Nullable;

public class RatingBar extends LinearLayout implements View.OnClickListener , ViewTreeObserver.OnGlobalLayoutListener {
    /*
    *填充的图片
     */
    private Drawable starDrawable;
    private Drawable unstarDrawable;

    private int starCount;
    private int star;
    /*
    *图片的宽、高
     */
    private float width,height;
    private float imagePadding;

    private boolean clickable;
    //保存每个view相对于ViewGroup的X轴距离
    private int []viewX;

    public RatingBar(Context context) {
        super(context);
        init(context,null);
    }

    public RatingBar(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        init(context, attrs);
    }

    public RatingBar(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init(context, attrs);
    }
    private void init(Context context, AttributeSet attrs)
    {
        setOrientation(LinearLayout.HORIZONTAL);
        setGravity(Gravity.CENTER);
        getViewTreeObserver().addOnGlobalLayoutListener(this);
        if(attrs!=null)
        {
            TypedArray array=context.obtainStyledAttributes(attrs,R.styleable.smail);
            starDrawable=array.getDrawable(R.styleable.smail_star_img);
            unstarDrawable=array.getDrawable(R.styleable.smail_unstar_img);
            width=array.getDimension(R.styleable.smail_img_width,dip2px(context,36));
            height=array.getDimension(R.styleable.smail_img_height,dip2px(context,36));
            imagePadding=array.getDimension(R.styleable.smail_img_padding,5);
            clickable=array.getBoolean(R.styleable.smail_clickable,true);
            starCount=array.getInt(R.styleable.smail_star_count,5);
            star=array.getInt(R.styleable.smail_starnum,0);

            array.recycle();
        }

        for(int i=0;i<starCount;i++)
        {
            ImageView view=getImageView(context,width,height);
            view.setTag(i);   //设置ImageView的下标
            addView(view);   //把每个子视图加到LinearLayout上,可设置宽高
            if(clickable)     //可以点击评分
                view.setOnClickListener(this);
        }
        if(star!=0)
        {
            if(starCount>=star)
            {   //填充图片
                fillingImage(star-1);
            }else
            {
                throw  new NullPointerException("star 的填充数量不能高于starcount");
            }
        }
    }
    private void fillingImage(int i)
    {
        //首先将所有的背景都设置为默认背景图片
        for(int j=0;j<starCount;j++)
        {
            ImageView view=(ImageView)getChildAt(j);
            if(unstarDrawable==null)    //当资源unstarDrawable不存在时
            {
                throw new NullPointerException("设置默认的填充资源");
            }else{
                view.setImageDrawable(unstarDrawable);
            }
        }
        //填充选中的等级
        for (int j = 0; j <= i; j++) {
            ImageView view = (ImageView) getChildAt(j);
            if (starDrawable == null) {
                throw new NullPointerException("请先设置填充的图片资源!");
            } else {
                view.setImageDrawable(starDrawable);
            }
        }
    }
    //创建默认的ImageView
    private ImageView getImageView(Context context,float width,float height)
    {
        ImageView view =new ImageView(context);
        ViewGroup.LayoutParams params=new ViewGroup.LayoutParams(Math.round(width),Math.round(height));
        view.setLayoutParams(params);
        view.setPadding(dip2px(context,imagePadding),0,0,0);
        if(unstarDrawable==null)
        {
            throw new NullPointerException("先设置默认图片资源");
        }else{
            view.setImageDrawable(unstarDrawable);
        }
        return view;
    }

    private int dip2px(Context context,float dpValue)
    {
        final float scale=context.getResources().getDisplayMetrics().density;
        return (int)(dpValue*scale+0.5f);
    }
    @Override
    public void onClick(View v) {
        Integer integer=(Integer)v.getTag();
        star=integer+1;
        fillingImage(integer);
    }

    @Override
    public void onGlobalLayout() {
        viewX=new int[starCount];
        for(int i=0;i<starCount;i++)
        {
            //view相对于ViewGroup的X轴距离
            int right=getChildAt(i).getRight();
            viewX[i]=right;
        }
    }
    public void setStar(int star)
    {
        if(star>starCount)
        {
            throw new RuntimeException("star 的填充数量不能大于总数starCount");
        }
        this.star=star;
        if(star!=0)
        {
            if(star<=starCount) {
                fillingImage(star - 1);
            }else
            {
                throw new RuntimeException("star 的填充数量不能大于总数starCount");
            }
        }

    }

    @Override
    public boolean onInterceptTouchEvent(MotionEvent ev) {
        switch (ev.getAction())
        {
            case MotionEvent.ACTION_DOWN:
                break;
            case MotionEvent.ACTION_MOVE:
                return true;

            case MotionEvent.ACTION_UP:
            case MotionEvent.ACTION_CANCEL:
                break;
        }
        return super.onInterceptTouchEvent(ev);
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        if(!clickable)
            return false;
        if(event.getAction()==MotionEvent.ACTION_MOVE)
        {
            for(int i=0;i<viewX.length;i++)
            {
                if(event.getX()<viewX[i]&&event.getX()>viewX[i]-width)
                {
                    fillingImage(i);
                    star=i+1;
                }
            }
        }
        return true;
    }
    public void setClickable(boolean clickable) {
        this.clickable = clickable;
    }
    public int getStar() {
        return star;
    }

    /**
     * 设置总数量
     *
     * @param starCount 总数量
     */
    public void setStarCount(int starCount) {
        this.starCount = starCount;
    }
    //    public RatingBar(Context context, AttributeSet attrs, int defStyleAttr, int defStyleRes) {
//        super(context, attrs, defStyleAttr, defStyleRes);
//        }
}
