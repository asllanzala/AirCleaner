package com.honeywell.hch.airtouchv2.app.airtouch.controller.emotion;

import android.animation.Animator;
import android.animation.AnimatorSet;
import android.animation.ObjectAnimator;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Point;
import android.graphics.Rect;
import android.graphics.drawable.BitmapDrawable;
import android.media.MediaPlayer;
import android.text.Html;
import android.util.AttributeSet;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.AccelerateInterpolator;
import android.view.animation.AlphaAnimation;
import android.view.animation.Animation;
import android.view.animation.BounceInterpolator;
import android.view.animation.LinearInterpolator;
import android.widget.ImageView;
import android.widget.PopupWindow;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.honeywell.hch.airtouchv2.R;
import com.honeywell.hch.airtouchv2.framework.global.AirTouchConstants;
import com.honeywell.hch.airtouchv2.framework.view.CustomHScrollLayout;
import com.honeywell.hch.airtouchv2.lib.util.DensityUtil;
import com.nineoldandroids.view.ViewHelper;

/**
 * Created by wuyuan on 15/6/8.
 */
public class EmotionPagementIndoorView extends RelativeLayout
{


    private Context mContext;

    private ImageView bigBottleImage;

    private ImageView smallBottleImage;

    private ImageView smallBottleCapImage;

    private RelativeLayout bigBottleView;

    private ImageView bigBottleCapImage;

    private RelativeLayout emotionBubbleContentView;

    private ImageView emotionBubbleImage;

    private int bubbleScrollPageIndex = 0;


    //sacle animation scale
    private float startScale = 0;


    /**
     * we need to record the bottle's width and height when the animation start first time
     * because every time when we compute the width and height with bigBottleView.getWidth() * startScale
     * if startScale is less than 0,the value will be less every time
     *
     */
    private boolean isFirstTimeStartAnimation = true;

    private int bigBottleViewWidth = 0;
    private int bigbottleViewHeight = 0;

    private int bigBottleImageWidth = 0;
    private int bigbottleImageHeight = 0;
    private int bigBottleCapImageWidth = 0;
    private int bigBottleCapImageHeight = 0;



    private RelativeLayout tellSomeoneCareView;

    private EmotionPagerParticleView particleView;


    /**
     * Distinguish the click of bigBottle is scaling or small
     */
    private boolean isBigBottle = true;

    /**
     * Distinguish if the bubble becoming small animation is caused by click oparation
     */
    private boolean isClickOpration = false;

    private EmotionPagerMainView mainView;

    private EmotionBubbleView bubbleView;

    /**
     * set emotionpager show status. default status is
     * 1. only one big yesterday is shown (bigYesterdayShown)
     * 2.big bottle is shown (bigBottleShown)
     * 3. share is invisible (shareIsInvisible)
     * every situation has a boolean value ,is the status is not default of one ,set false.
     * otherwise set true
     *
     */
    private boolean bigBottleShown = true;
    private boolean shareIsInvisible = true;

    //content in bubble
    private TextView bubbleTitleText;
    private TextView bubblePathContainText;
    private TextView cigaretesText;
    private TextView bubbleCarLeadText;
    private TextView leadContaintText;

    private CustomHScrollLayout bubbleScroll;
    private ImageView pagerDitectImage;

    private ImageView bubblePathImage;
    private ImageView bubbleLeadImage;

    //popup window
    private PopupWindow popWindow;
    private ImageView popWindowParticleImage;
    private TextView popwindowTitleText;
    private TextView popwindowParticleOrtentationTxt;

    private int closePopCapDuration;
    private int smallToBigDuration;

    private boolean animationIsEnd = false;

    public EmotionPagementIndoorView(Context context)
    {
        super(context);
        mContext = context;
        initView();
    }

    public EmotionPagementIndoorView(Context context, AttributeSet attrs)
    {
        super(context, attrs);
        mContext = context;
        initView();

    }

    public EmotionPagementIndoorView(Context context, AttributeSet attrs, int defStyle)
    {
        super(context, attrs, defStyle);
        mContext = context;
        initView();

    }

    public void setMainView(EmotionPagerMainView thisMainView)
    {
        mainView = thisMainView;
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
    }

    public int getBubbleScrollPageIndex() {
        return bubbleScrollPageIndex;
    }

    public void setBubbleScrollPageIndex(int bubbleScrollPageIndex) {
        this.bubbleScrollPageIndex = bubbleScrollPageIndex;
    }

    private void initView()
    {

        LayoutInflater inflater = (LayoutInflater) mContext.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View view = inflater.inflate(R.layout.emotionpage_indoor, this);

        emotionBubbleContentView = (RelativeLayout)view.findViewById(R.id.emotion_bubble_view);
        emotionBubbleImage = (ImageView)view.findViewById(R.id.emotion_bubble_image);

        bigBottleImage = (ImageView)view.findViewById(R.id.big_bottle_id);
        bigBottleCapImage = (ImageView)view.findViewById(R.id.big_bottle_cap);

        smallBottleImage = (ImageView)view.findViewById(R.id.small_bottle_id);
        smallBottleCapImage = (ImageView)view.findViewById(R.id.small_bottlecap_id);
        bigBottleView = (RelativeLayout)view.findViewById(R.id.big_bottle_view);
        setSmallBottleVisible(View.INVISIBLE);

        tellSomeoneCareView = (RelativeLayout)view.findViewById(R.id.tell_someone_careview);
        tellSomeoneCareView.setVisibility(View.GONE);
        tellSomeoneCareView.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                mainView.socialShare();
                mainView.setOnShareEnd(new EmotionPagerMainView.ShareEndCallback() {
                    @Override
                    public void onEnd() {
                        tellSomeoneCareView.setClickable(true);
                    }
                });
                tellSomeoneCareView.setClickable(false);
            }
        });


        particleView = (EmotionPagerParticleView)view.findViewById(R.id.particle_view);
        view.findViewById(R.id.bubble_scroll_one).setOnTouchListener(new OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                mainView.hideEmotionShareDummyLayout();
                return false;

            }
        });
        view.findViewById(R.id.bubble_scroll_two).setOnTouchListener(new OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                mainView.hideEmotionShareDummyLayout();
                return false;
            }
        });



                /*.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                mainView.hideEmotionShareDummyLayout();
            }
        });*/
        //compute the bottle's height and width
        getBigBottleImageWidthAndHeight();
        getBigBottleCapImageWidthAndHeigh();

        final int emotionBubbleHeight = getImageHeigh(R.drawable.emotion_bubble);
        //first step: hide the emotion bubble

        isClickOpration = false;
        initEmontionBubble();
        hideEmotionBubbleContent(1);

        hideEmotionBubble2(1);
        postDelayed(new Runnable() {

            @Override
            public void run() {
                int bottleTop = (int) (getHeight() * 0.3) + smallBottleImage.getHeight() / 2;
                int bubbleBottom = emotionBubbleHeight + DensityUtil.dip2px(45);
                int height = getHeight() - bottleTop - bubbleBottom;
                bubbleView.setBuildPaths(height);
            }
        }, 400);
        bigBottleImage.setOnClickListener(new OnClickListener()
        {
            @Override
            public void onClick(View v)
            {

                if (isFirstTimeStartAnimation)
                {
                    isFirstTimeStartAnimation = false;
                    bigBottleViewWidth = bigBottleView.getWidth();
                    bigbottleViewHeight = bigBottleView.getHeight();

                }
                bigBottleImage.setClickable(false);
                bigBottleImage.setEnabled(false);

                if (isBigBottle)
                {
                    bigBottleShown = false;
                    bubbleView.setVisibility(View.VISIBLE);
                    particleView.setParticleViewVisible(View.GONE);
                    /**
                     * when click the big bottle
                     * 1.the bottle becomes small
                     * 2.open the bottle cap
                     * 3.the bubble becomes big
                     * meantime,show tellSomeOne text and collection text
                     */
                    setBottleTranslateAndSmall();

                    //show tell someone
                    showTellSomeOneAinimation();

                    mainView.hideTitleCollectionAinimation();

                }
                else
                {
                    initDurationTime(true);
				   // mainView.hideEmotionShareDummyLayout();
                    mainView.hideEmotionShareDummyLayout();
                    bigBottleShown = true;
                    /**
                     * when the bottle is small,cick it
                     * 1.the bubble becomes small
                     * 2.close the bottle cap
                     * 3.the bottle becomes big
                     * meantime,hide tellSomeOne text and collection text
                     */
                    isClickOpration = true;
                    hideEmotionBubbleContent(1000);

                    hideTellSomeOneAinimation(1300);

                    mainView.showTitleCollectionAinimation(800);

                }
            }
        });


        //bubble content
        bubbleTitleText = (TextView)view.findViewById(R.id.bubble_title_txt);
        bubblePathContainText = (TextView)view.findViewById(R.id.path_contain_txt);
        cigaretesText = (TextView)view.findViewById(R.id.cigaretes_txt);
        bubbleCarLeadText = (TextView)view.findViewById(R.id.car_txt);
        leadContaintText = (TextView)view.findViewById(R.id.lead_contain_txt);
        bubblePathImage = (ImageView)view.findViewById(R.id.bubble_path_img);
        bubblePathImage.setOnClickListener(new OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                mainView.hideEmotionShareDummyLayout();
                popWindowParticleImage.setImageResource(R.drawable.icon_pahs_big);
                popwindowTitleText.setText(mContext.getResources().getString(R.string.paths_name));
                popwindowParticleOrtentationTxt.setText(mContext.getResources().getString(R.string.paths_orientation));

                int[] location = new int[2];
                bubblePathImage.getLocationOnScreen(location);
                popWindow.showAtLocation(EmotionPagementIndoorView.this, Gravity.LEFT,
                        location[0] - DensityUtil.dip2px(10), location[1] - getHeight() / 2 -
                                (int)(mContext.getResources().getDimension(R.dimen.popwidow_heightoffer)));
            }
        });

        bubbleLeadImage = (ImageView)view.findViewById(R.id.bubble_lead_img);
        bubbleLeadImage.setOnClickListener(new OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                mainView.hideEmotionShareDummyLayout();
                popWindowParticleImage.setImageResource(R.drawable.icon_lead_en);
                popwindowTitleText.setText(mContext.getResources().getString(R.string.lead_name));
                popwindowParticleOrtentationTxt.setText(mContext.getResources().getString(R
                        .string.lead_orientation));

                int[] location = new int[2];
                bubbleLeadImage.getLocationOnScreen(location);
                popWindow.showAtLocation(EmotionPagementIndoorView.this, Gravity.LEFT,
                        location[0] - DensityUtil.dip2px(10), location[1] - getHeight() / 2 -
                                (int)(mContext.getResources().getDimension(R.dimen.popwidow_heightoffer)));
            }
        });

        setBubbleScrollTextValue(0f, AirTouchConstants.INIT_STR_VALUE, AirTouchConstants.INIT_STR_VALUE, AirTouchConstants.INIT_STR_VALUE,AirTouchConstants.INIT_STR_VALUE);

        initBubbleScroll(view);

        initParticleOrientation();
    }

    private void initDurationTime(boolean isClickEvent){
        closePopCapDuration = isClickEvent ? 100 : 10;
        smallToBigDuration = isClickEvent ? 500 : 10;
    }

    /**
     * set the vlaue of PM2.5,PATH and Lead.
     * @param pmvalue
     * @param pathValue
     * @param leadValue
     */
    public void setBubbleScrollTextValue(float pmvalue,String pathValue,String leadValue,String cigerateStr,String carFumeStr)
    {

        String text = String.format(getResources().getString(R.string.bubble_title_str), ((int)pmvalue));
        bubbleTitleText.setText(Html.fromHtml(text));

        String text2 = String.format(getResources().getString(R.string.bubble_path_contain), pathValue);
        bubblePathContainText.setText(Html.fromHtml(text2));


        //keep two  significant figures
        String text3 = String.format(getResources().getString(R.string.bubble_path_cigarettes),cigerateStr);
        cigaretesText.setText(Html.fromHtml(text3));


        String text4 = String.format(getResources().getString(R.string.bubble_car_lead), carFumeStr);
        bubbleCarLeadText.setText(Html.fromHtml(text4));

        String text5 = String.format(getResources().getString(R.string.bubble_path_contain), leadValue);
        leadContaintText.setText(Html.fromHtml(text5));
    }

    private void initParticleOrientation()
    {
        LayoutInflater inflater = (LayoutInflater) mContext.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View popWindowView = inflater.inflate(R.layout.popwindow_view, null);

        popWindow = new PopupWindow(popWindowView, LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT, true);
        popWindow.setWidth((int)(mContext.getResources().getDimension(R.dimen.popwindow_width)));
        popWindow.setBackgroundDrawable(new BitmapDrawable());
        popWindow.setFocusable(true);
        popWindow.setOutsideTouchable(true);

        popWindowParticleImage = (ImageView)popWindowView.findViewById(R.id.popwindow_particle_img);
        popwindowTitleText = (TextView)popWindowView.findViewById(R.id.popwindow_titlename_text);
        popwindowParticleOrtentationTxt = (TextView)popWindowView.findViewById(R.id.particle_orientation_text);
    }

    private void resetBubbleSrollContent()
    {
        bubbleScroll.setToScreen(0);

        setBubbleScrollTextValue(0f,AirTouchConstants.INIT_STR_VALUE,AirTouchConstants.INIT_STR_VALUE,AirTouchConstants.INIT_STR_VALUE,AirTouchConstants.INIT_STR_VALUE);
    }

    private int getImageHeigh(int drableId)
    {
        Bitmap bitmap = BitmapFactory.decodeResource(getResources(), drableId);
        int height = bitmap.getHeight();
        bitmap.recycle();
        bitmap = null;
        return height;
    }

    private int getImageWith(int drableId)
    {
        Bitmap bitmap = BitmapFactory.decodeResource(getResources(), drableId);
        int width = bitmap.getWidth();
        bitmap.recycle();
        bitmap = null;
        return width;
    }

    private void getBigBottleImageWidthAndHeight()
    {
        Bitmap bitmap = BitmapFactory.decodeResource(getResources(), R.drawable.bottle_big);
        bigBottleImageWidth = bitmap.getWidth();
        bigbottleImageHeight = bitmap.getHeight();
        bitmap.recycle();
        bitmap = null;
    }

    private void getBigBottleCapImageWidthAndHeigh()
    {
        Bitmap bitmap = BitmapFactory.decodeResource(getResources(), R.drawable.cap_1);
        bigBottleCapImageWidth = bitmap.getWidth();
        bigBottleCapImageHeight = bitmap.getHeight();
        bitmap.recycle();
        bitmap = null;
    }


    private void setSmallBottleVisible(int visible)
    {
        smallBottleImage.setVisibility(visible);
        smallBottleCapImage.setVisibility(visible);
    }

    /**
     * -------------------------------------click big bottle animation -------------------------------------
     */

    private void showTellSomeOneAinimation()
    {
        tellSomeoneCareView.setClickable(false);
        AlphaAnimation animation = new AlphaAnimation(0, 1);
        animation.setDuration(1800);
        animation.setInterpolator(new AccelerateInterpolator());
        animation.setAnimationListener(new Animation.AnimationListener()
        {
            @Override
            public void onAnimationStart(Animation animation)
            {
            }

            @Override
            public void onAnimationRepeat(Animation animation)
            {

            }

            @Override
            public void onAnimationEnd(Animation animation)
            {

                tellSomeoneCareView.setClickable(true);
            }
        });
        tellSomeoneCareView.setAnimation(animation);
        animation.start();
        tellSomeoneCareView.setVisibility(View.VISIBLE);
    }

    private void hideTellSomeOneAinimation(int duration)
    {
        tellSomeoneCareView.setClickable(false);
        AlphaAnimation animation = new AlphaAnimation(1, 0);
        animation.setDuration(duration);
        animation.setInterpolator(new AccelerateInterpolator());
        tellSomeoneCareView.setAnimation(animation);
        animation.start();
        tellSomeoneCareView.setVisibility(View.INVISIBLE);
    }


    private void setBottleTranslateAndSmall()
    {

        isBigBottle = false;
        bigBottleView.setClickable(false);

        startScale = 0;
        final Rect startBounds = new Rect();
        final Rect finalBounds = new Rect();
        final Point globalOffset = new Point();

        // The start bounds are the global visible rectangle of the thumbnail,
        // and the final bounds are the global visible rectangle of the container
        // view. Also set the container view's offset as the origin for the
        // bounds, since that's the origin for the positioning animation
        // properties (X, Y).
        bigBottleImage.getGlobalVisibleRect(startBounds);

        smallBottleImage.getGlobalVisibleRect(finalBounds, globalOffset);
        startBounds.offset(-globalOffset.x, -globalOffset.y);
        finalBounds.offset(-globalOffset.x, -globalOffset.y);

        // Adjust the start bounds to be the same aspect ratio as the final
        // bounds using the "center crop" technique. This prevents undesirable
        // stretching during the animation. Also calculate the start scaling
        // factor (the end scaling factor is always 1.0).
        if ((float) finalBounds.width() / finalBounds.height()
                > (float) startBounds.width() / startBounds.height()) {
            // Extend start bounds horizontally
            startScale = (float)finalBounds.width() / startBounds.width();

        } else {
            startScale = (float) finalBounds.height()/startBounds.height()  ;

            // Extend start bounds vertically
        }


        ObjectAnimator scaleXObj = ObjectAnimator.ofFloat(bigBottleView, "scaleX", 1f, startScale);
        ObjectAnimator scaleYObj = ObjectAnimator.ofFloat(bigBottleView, "scaleY", 1f, startScale);
        ObjectAnimator tranlateXObj = ObjectAnimator.ofFloat(bigBottleView, "translationX", 0, DensityUtil.getScreenWidth() / 3);
        ObjectAnimator tranlateYObj = ObjectAnimator.ofFloat(bigBottleView, "translationY", 0, (int)(0.2*getHeight()));



        AnimatorSet animSet = new AnimatorSet();
        animSet.setDuration(500);
        animSet.addListener(new Animator.AnimatorListener()
        {
            @Override
            public void onAnimationStart(Animator animation)
            {

            }

            @Override
            public void onAnimationEnd(Animator animation)
            {
                popBottleCap();
            }

            @Override
            public void onAnimationCancel(Animator animation)
            {

            }

            @Override
            public void onAnimationRepeat(Animator animation)
            {

            }
        });

        animSet.setInterpolator(new LinearInterpolator());
        animSet.playTogether(scaleXObj, scaleYObj, tranlateXObj, tranlateYObj);
        animSet.start();


    }

    private void popBottleCap(){
        //rotation 才是绕着平面去旋转
        //rotationX 是会
        ObjectAnimator capRotate = ObjectAnimator.ofFloat(bigBottleCapImage, "rotation",
                0f, 45f);

        ObjectAnimator capTranlateXObj = ObjectAnimator.ofFloat(bigBottleCapImage,
                "translationX", 0, (bigBottleCapImage.getWidth()) * 1.5f);
        ObjectAnimator capTranlateYObj = ObjectAnimator.ofFloat(bigBottleCapImage,
                "translationY", 0, -bigBottleCapImage.getHeight());

        AnimatorSet animSet = new AnimatorSet();
        animSet.addListener(new Animator.AnimatorListener()
        {
            @Override
            public void onAnimationStart(Animator animation)
            {
                playOpenCapSound();
            }

            @Override
            public void onAnimationEnd(Animator animation)
            {
                showEmotionBubble2();
            }

            @Override
            public void onAnimationCancel(Animator animation)
            {

            }

            @Override
            public void onAnimationRepeat(Animator animation)
            {

            }
        });

        animSet.setInterpolator(new BounceInterpolator());
        animSet.setDuration(400);
        animSet.playTogether(capTranlateXObj,capTranlateYObj,capRotate);
        animSet.start();
    }


    private void showEmotionBubbleContent()
    {
        AlphaAnimation animation = new AlphaAnimation(0, 1);
        animation.setDuration(600);
        animation.setInterpolator(new AccelerateInterpolator());
        emotionBubbleContentView.setAnimation(animation);
        animation.start();
        animation.setAnimationListener(new Animation.AnimationListener()
        {
            @Override
            public void onAnimationStart(Animation animation)
            {

            }

            @Override
            public void onAnimationEnd(Animation animation)
            {
                bigBottleImage.setClickable(true);
                bigBottleImage.setEnabled(true);
            }

            @Override
            public void onAnimationRepeat(Animation animation)
            {

            }
        });
        emotionBubbleContentView.setVisibility(View.VISIBLE);
    }

    private void hideEmotionBubbleContent(final int duration)
    {
        AlphaAnimation animation = new AlphaAnimation(1, 0);
        animation.setDuration(300);
        animation.setInterpolator(new AccelerateInterpolator());
        emotionBubbleContentView.setAnimation(animation);
        animation.start();
        animation.setAnimationListener(new Animation.AnimationListener()
        {
            @Override
            public void onAnimationStart(Animation animation) {

            }

            @Override
            public void onAnimationEnd(Animation animation)
            {
                hideEmotionBubble2(duration);

            }

            @Override
            public void onAnimationRepeat(Animation animation)
            {

            }
        });
        emotionBubbleContentView.setVisibility(View.INVISIBLE);
    }


    /**
     * -------------------------------------click small bottle animation -------------------------------------
     */

    private void hideEmotionBubble2(int duration)
    {
        bubbleView.startBubbleAnimation(false, duration);
    }

    private void showEmotionBubble2()
    {
        bubbleView.startBubbleAnimation(true, 800);
    }

    private void closeBottleCap(){
        ObjectAnimator capRotate = ObjectAnimator.ofFloat(bigBottleCapImage, "rotation",
                45f, 0);

        ObjectAnimator capTranlateXObj = ObjectAnimator.ofFloat(bigBottleCapImage,
                "translationX", (bigBottleCapImage.getWidth()) * 1.5f, 0);
        ObjectAnimator capTranlateYObj = ObjectAnimator.ofFloat(bigBottleCapImage,
                "translationY", -bigBottleCapImage.getHeight(), 0);

        AnimatorSet animSet = new AnimatorSet();
        animSet.addListener(new Animator.AnimatorListener()
        {
            @Override
            public void onAnimationStart(Animator animation)
            {

            }

            @Override
            public void onAnimationEnd(Animator animation)
            {
                setBottleTranslateAndBig();
            }

            @Override
            public void onAnimationCancel(Animator animation)
            {

            }

            @Override
            public void onAnimationRepeat(Animator animation)
            {

            }
        });

        animSet.setInterpolator(new BounceInterpolator());
        animSet.setDuration(closePopCapDuration);
        animSet.playTogether(capRotate,capTranlateXObj,capTranlateYObj);
        animSet.start();
    }



    private void setBottleTranslateAndBig()
    {
        isBigBottle = true;
        bigBottleView.setClickable(false);


        ObjectAnimator scaleXObj = ObjectAnimator.ofFloat(bigBottleView, "scaleX", startScale,1f);
        ObjectAnimator scaleYObj = ObjectAnimator.ofFloat(bigBottleView, "scaleY", startScale,1f);
        ObjectAnimator tranlateXObj = ObjectAnimator.ofFloat(bigBottleView, "translationX", DensityUtil.getScreenWidth() / 3,0 );
        ObjectAnimator tranlateYObj = ObjectAnimator.ofFloat(bigBottleView, "translationY", (int)(0.2*getHeight()),0);

        AnimatorSet animSet = new AnimatorSet();
        animSet.setDuration(smallToBigDuration);
        animSet.addListener(new Animator.AnimatorListener()
        {
            @Override
            public void onAnimationStart(Animator animation)
            {

            }

            @Override
            public void onAnimationEnd(Animator animation)
            {
                bigBottleImage.setClickable(true);
                bigBottleImage.setEnabled(true);
                particleView.setParticleViewVisible(View.VISIBLE);
            }

            @Override
            public void onAnimationCancel(Animator animation)
            {

            }

            @Override
            public void onAnimationRepeat(Animator animation)
            {

            }
        });

        animSet.setInterpolator(new LinearInterpolator());
        animSet.playTogether(scaleXObj, scaleYObj, tranlateXObj, tranlateYObj);
        animSet.start();


    }


    private void initEmontionBubble()
    {
        bubbleView = new EmotionBubbleView(mContext);
        bubbleView.setBubbleContentListener(new EmotionBubbleView.ShowOrHideBubbleContentListener()
        {

            @Override
            public void showBubbleContent()
            {
                showEmotionBubbleContent();

            }

            @Override
            public void afterHideBubble()
            {
                if (isClickOpration)
                {
                    /**
                     * close the bottle cap
                     */
                    bubbleView.setVisibility(View.INVISIBLE);
                    initDurationTime(true);
                    closeBottleCap();
                }
            }
        });


        postDelayed(new Runnable()
        {

            @Override
            public void run()
            {
                //set the bubble position
                RelativeLayout.LayoutParams params = new RelativeLayout.LayoutParams(ViewGroup
                        .LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);


                //use the percentage of the bubble width
                //0.724137 is the percentage of  the sharp corner of the bubble which the position is in the whole bubble
                int leftmargin =  (int)(bubbleView.getBubbleViewWidth() * 0.724137 - (5 * DensityUtil.getScreenWidth())/6);
                params.leftMargin = -leftmargin;


                params.topMargin = (int) (0.7 * getHeight()) - getImageHeigh(R.drawable
                        .small_bottle) / 2 - bubbleView.getBubbleViewHeigh() - DensityUtil.dip2px(10);
                bubbleView.setLayoutParams(params);
                bubbleView.setVisibility(View.INVISIBLE);
                EmotionPagementIndoorView.this.addView(bubbleView);

                //set the bubble content position
                ViewHelper.setTranslationY(emotionBubbleContentView, params.topMargin + DensityUtil.dip2px(5));
            }
        }, 400);

    }


    /**
     * set bottle can be clickable or not.when the emotion request from server is not done
     * should set the bottle can not be clickable.if request is done and successful,set the
     * bottle clickable
     * @param isClickable
     */
    public void setBottleClickable(boolean isClickable)
    {
        if (bigBottleImage != null)
        {
            bigBottleImage.setClickable(isClickable);
            bigBottleImage.setEnabled(isClickable);
        }

    }

    /**
     * set the running status of particle move thread is false.and clear the particleList
     */
    public void stopParticleMoving()
    {
        particleView.stopParticleMove();
    }


    /**
     * set indoor layout alpha between 0.2 to 1 when the title switch
     * @param alpha
     */
    public void setIndoorLayoutAlphaWhenTitleSwitch(float alpha)
    {
        bigBottleView.setAlpha(alpha);
        particleView.setAlpha(alpha);
    }

    /**
     * generate particle according the level from server,call generateParticle method in EmotionParticleView
     * @param level level from server
     */
    public void generateParticleAccordingLevel(int level)
    {
        particleView.generateParticle(level);
    }

    /**
     * when scroll the home ,need to reset the Indoor View status to default status
     * 1. title show Yesterday
     * 2.show big bottle
     */
    public void resetIndoorViewStatusToBack()
    {
        if (!bigBottleShown)
        {

            bigBottleShown = true;

            //hide emotion content View
            emotionBubbleContentView.setVisibility(View.INVISIBLE);

            //mesh bubble
            hideEmotionBubble2(10);
            initDurationTime(false);
            closeBottleCap();

            //hide collection words
            mainView.showTitleCollectionAinimation(0);
            //hide tellsome one
            tellSomeoneCareView.setVisibility(View.INVISIBLE);

            resetBubbleSrollContent();
            //reset collection text
            if (popWindow != null && popWindow.isShowing())
            {
                popWindow.dismiss();
            }
        }
    }

    private void initBubbleScroll(View view)
    {
        bubbleScroll = (CustomHScrollLayout) view.findViewById(R.id.bubble_scroll_layout);
        pagerDitectImage = (ImageView)view.findViewById(R.id.scroll_tag_image);
        pagerDitectImage.setImageResource(R.drawable.path_scrolltag_image);
        bubbleScroll.setOnViewChangeListener(new CustomHScrollLayout.OnViewChangeListener()
        {
            @Override
            public void OnViewChange(int index)
            {
                if (index == 1)
                {
                    pagerDitectImage.setImageResource(R.drawable.lead_scrolltag_image);
                }
                else
                {
                    pagerDitectImage.setImageResource(R.drawable.path_scrolltag_image);
                }
                setBubbleScrollPageIndex(index);
            }
        });
        bubbleScroll.setOnViewTouchListener(new CustomHScrollLayout.OnViewTouchListener() {
            @Override
            public void OnViewTouch(int index) {
            }
        });

    }

    /**
     * set indoor text 0 if request error
     */
    public void resetIndoorTextAfterRequestError()
    {
        setBubbleScrollTextValue(0f, AirTouchConstants.INIT_STR_VALUE, AirTouchConstants
                .INIT_STR_VALUE, AirTouchConstants.INIT_STR_VALUE, AirTouchConstants
                .INIT_STR_VALUE);
    }


    private void playOpenCapSound()
    {
        final MediaPlayer  mp = MediaPlayer.create(mContext, R.raw.cap);

        if (mp != null)
        {
            mp.stop();

            try
            {
                mp.prepare();
                mp.start();
            }
            catch (Exception e)
            {

            }

            mp.setOnCompletionListener(
                    new MediaPlayer.OnCompletionListener()
                    {
                        // @Override
                        public void onCompletion(MediaPlayer arg0)
                        {
                            realseMediaPlayer(mp);
                        }
                    });

            mp.setOnErrorListener(new MediaPlayer.OnErrorListener()
            {
                @Override
                public boolean onError(MediaPlayer arg0, int arg1, int arg2)
                {
                    // TODO Auto-generated method stub

                    realseMediaPlayer(mp);
                    return false;
                }
            });
        }

    }

    private void realseMediaPlayer(MediaPlayer mp)
    {
        try
        {
            mp.release();
        } catch (Exception e)
        {
            e.printStackTrace();
        }
    }

}
