package com.honeywell.hch.airtouchv3.app.airtouch.controller.emotion;

import android.content.Context;
import android.graphics.Canvas;
import android.os.Bundle;
import android.text.Html;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.view.animation.AccelerateInterpolator;
import android.view.animation.AlphaAnimation;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.honeywell.hch.airtouchv3.R;
import com.honeywell.hch.airtouchv3.framework.app.AppManager;
import com.honeywell.hch.airtouchv3.framework.config.AppConfig;
import com.honeywell.hch.airtouchv3.framework.global.AirTouchConstants;
import com.honeywell.hch.airtouchv3.framework.webservice.task.EmotionalBottleTask;
import com.honeywell.hch.airtouchv3.framework.webservice.task.ResponseResult;
import com.honeywell.hch.airtouchv3.lib.http.IActivityReceive;
import com.honeywell.hch.airtouchv3.lib.util.AsyncTaskExecutorUtil;
import com.honeywell.hch.airtouchv3.lib.util.DensityUtil;
import com.honeywell.hch.airtouchv3.wxapi.WXEntryActivity;

/**
 * Created by wuyuan on 15/6/24.
 */
public class EmotionPagerTitleView extends RelativeLayout
{

    private static final int FIRST_TEXT_INDEX = 1;

    private static final int SECOND_TEXT_INDEX = 2;

    private static final int THIRD_TEXT_INDEX = 3;

    private static final int FOURH_TEXT_INDEX = 4;

    private static final float ALPHA_80 = 0.8f;

    private static final float ALPHA_100 = 1.0f;


    private Context mContext;

    private RelativeLayout oneTitleView;
    private TextView oneTitleText;
    private TextView oneTitleText2;
    private TextView oneTitleText3;
    private TextView oneTitleText4;

    private TextView yesterdayText;
    private ImageView yesterdayBg;
    private RelativeLayout yesterdayView;

    private RelativeLayout thisweekView;
    private TextView thisweekText;

    private RelativeLayout thismonthView;
    private TextView thismonthText;

    private RelativeLayout sofarView;
    private TextView sofarText;

    //recode the current background in which text index
    private int preBgInTextIndex = FIRST_TEXT_INDEX;

    private boolean isBigFromSmall = false;

    private float startScaleX = 1;
    private float startScaleY = 1;
    private float imageTranslateX = 0;
    private float imageTranslateY = 0;
    private float textTranslateX = 0;
    private float textTranslateY = 0;

    private int firstTitleMargin;
    private int secondTitleMargin;
    private int thirdTitleMargin;
    private int fourTitleMargin;

    private WXEntryActivity mainView;

    private RelativeLayout connectTextLayout;

    private TextView collectedTextView;

    private int locationId;

    private boolean isRequestBegin = false;

    /**
     * set emotionpager show status. default status is
     * 1. only one big yesterday is shown
     * 2.big bottle is shown
     * 3. share is invisible
     * every situation has a boolean value ,is the status is not default of one ,set false.
     * otherwise set true
     *
     */
    private boolean bigYesterdayShown = true;


    private int requestIdForShare = AirTouchConstants.YESTERDAY_REQUEST;



    public EmotionPagerTitleView(Context context)
    {
        super(context);
        mContext = context;
        setWillNotDraw(false);
        initView();
    }

    public EmotionPagerTitleView(Context context, AttributeSet attrs)
    {
        super(context, attrs);
        mContext = context;
        setWillNotDraw(false);

        initView();

    }

    public EmotionPagerTitleView(Context context, AttributeSet attrs, int defStyle)
    {
        super(context, attrs, defStyle);
        mContext = context;
        setWillNotDraw(false);
        initView();

    }


    public void setEmotionPagerMainView(WXEntryActivity thisMainView)
    {
        mainView = thisMainView;
    }

    /**
     *
     */
    public void setBigYesterdayWhenFirstScroll()
    {
        isBigFromSmall = true;
        smallTitleToBigTitle(yesterdayView, yesterdayText, AirTouchConstants.YESTERDAY_REQUEST);
    }


    private void initView()
    {
        LayoutInflater inflater = (LayoutInflater) mContext.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View view = inflater.inflate(R.layout.emotionpager_titleview, this);

        connectTextLayout = (RelativeLayout)view.findViewById(R.id.collected_view);
        collectedTextView = (TextView)view.findViewById(R.id.collected);
        String text = String.format(getResources().getString(R.string.collection_str_tip), 0);
        collectedTextView.setText(Html.fromHtml(text));

        oneTitleText = (TextView)view.findViewById(R.id.one_title_text);
        oneTitleText2 = (TextView)view.findViewById(R.id.one_title_text2);
        oneTitleText3 = (TextView)view.findViewById(R.id.one_title_text3);
        oneTitleText4 = (TextView)view.findViewById(R.id.one_title_text4);

        oneTitleView = (RelativeLayout)view.findViewById(R.id.one_title_view);


        yesterdayView = (RelativeLayout)view.findViewById(R.id.yesterday_view);
        yesterdayText = (TextView)view.findViewById(R.id.yesterday_text);

        thisweekView = (RelativeLayout)view.findViewById(R.id.thisweek_view);
        thisweekText = (TextView)view.findViewById(R.id.thisweek_text);

        thismonthView = (RelativeLayout)view.findViewById(R.id.thismonth_view);
        thismonthText = (TextView)view.findViewById(R.id.thismonth_text);

        sofarView = (RelativeLayout)view.findViewById(R.id.sofar_view);
        sofarText = (TextView)view.findViewById(R.id.sofar_text);

        initFourTileView(view);

        initTitleBg();
    }

    private void initFourTileView(View view)
    {

        //calculate the device width and traslate the  title
        firstTitleMargin = (DensityUtil.getScreenWidth() - 4 * DensityUtil.dip2px(70))/5;
        secondTitleMargin = firstTitleMargin * 2 + DensityUtil.dip2px(70);
        thirdTitleMargin = firstTitleMargin * 3 + 2 * DensityUtil.dip2px(70);
        fourTitleMargin = firstTitleMargin * 4 + 3 * DensityUtil.dip2px(70);


        initSmallTitlePosition(yesterdayView, firstTitleMargin);
        initSmallTitlePosition(thisweekView, secondTitleMargin);
        initSmallTitlePosition(thismonthView, thirdTitleMargin);
        initSmallTitlePosition(sofarView, fourTitleMargin);
        isBigFromSmall = true;

        oneTitleView.setOnClickListener(new OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                mainView.hideEmotionShareDummyLayout();
                bigYesterdayShown = false;
                isBigFromSmall = false;
                bigTitleTosmallTitle();
            }
        });

        yesterdayView.setOnClickListener(new View.OnClickListener()
        {

            @Override
            public void onClick(View v)
            {
				mainView.hideEmotionShareDummyLayout();
                if (!isBigFromSmall)
                {
                    bigYesterdayShown = true;
                    setTitleViewBg(yesterdayView);
                    isBigFromSmall = true;
                    smallTitleToBigTitle(yesterdayView, yesterdayText, AirTouchConstants
                            .YESTERDAY_REQUEST);
                }

            }
        });

        thisweekView.setOnClickListener(new View.OnClickListener()
        {

            @Override
            public void onClick(View v)
            {
			    mainView.hideEmotionShareDummyLayout();
                if (!isBigFromSmall)
                {
                    bigYesterdayShown = false;
                    setTitleViewBg(thisweekView);
                    isBigFromSmall = true;
                    smallTitleToBigTitle(thisweekView, thisweekText, AirTouchConstants.THIS_WEEK_REQUEST);

                }

            }
        });

        thismonthView.setOnClickListener(new View.OnClickListener()
        {

            @Override
            public void onClick(View v)
            {
                mainView.hideEmotionShareDummyLayout();
                if (!isBigFromSmall)
                {
                    bigYesterdayShown = false;
                    setTitleViewBg(thismonthView);
                    isBigFromSmall = true;
                    smallTitleToBigTitle(thismonthView, thismonthText, AirTouchConstants
                            .THIS_MONTH_REQUEST);

                }

            }
        });

        sofarView.setOnClickListener(new View.OnClickListener()
        {

            @Override
            public void onClick(View v)
            {
                mainView.hideEmotionShareDummyLayout();
                if (!isBigFromSmall)
                {
                    bigYesterdayShown = false;
                    setTitleViewBg(sofarView);
                    isBigFromSmall = true;
                    smallTitleToBigTitle(sofarView, sofarText, AirTouchConstants.SO_FAR_REQUEST);

                }


            }
        });

    }

    private void setSmallTitleBg(RelativeLayout showview1,RelativeLayout view2,RelativeLayout view3,RelativeLayout view4)
    {

          setTitleViewBg(showview1);
          view2.setBackgroundResource(0);
          view3.setBackgroundResource(0);
          view4.setBackgroundResource(0);

    }

    private void initSmallTitlePosition(View imageOrTextView,int marginValue)
    {
        RelativeLayout.LayoutParams params = new RelativeLayout.LayoutParams
                (imageOrTextView.getLayoutParams());
        params.addRule(RelativeLayout.ALIGN_PARENT_TOP, RelativeLayout.TRUE);
        params.leftMargin = marginValue;
        params.topMargin = DensityUtil.dip2px(30);
        imageOrTextView.setLayoutParams(params);

    }

    private void initSmallBackground()
    {

        clearTitleAnimation();

        if (requestIdForShare == AirTouchConstants.YESTERDAY_REQUEST){
            setSmallTitleBg(yesterdayView,thisweekView,thismonthView,sofarView);
        }
        else if (requestIdForShare == AirTouchConstants.THIS_WEEK_REQUEST){
            setSmallTitleBg(thisweekView,yesterdayView,thismonthView,sofarView);
        }
        else if (requestIdForShare == AirTouchConstants.THIS_MONTH_REQUEST){
            setSmallTitleBg(thismonthView,yesterdayView,thisweekView,sofarView);
        }
        else if (requestIdForShare == AirTouchConstants.SO_FAR_REQUEST){
            setSmallTitleBg(sofarView,yesterdayView,thisweekView,thismonthView);
        }

        yesterdayView.setVisibility(View.VISIBLE);
        thisweekView.setVisibility(View.VISIBLE);
        thismonthView.setVisibility(View.VISIBLE);
        sofarView.setVisibility(View.VISIBLE);

        oneTitleView.setVisibility(View.INVISIBLE);

        if (isRequestBegin){
            setTitleAnimation(requestIdForShare);
        }
    }



    private void smallTitleToBigTitle(final RelativeLayout smallTitleView,final TextView textView, final int requestId)
    {

        clearTitleAnimation();
        requestIdForShare = requestId;

        yesterdayView.setVisibility(View.INVISIBLE);
        thisweekView.setVisibility(View.INVISIBLE);
        thismonthView.setVisibility(View.INVISIBLE);
        sofarView.setVisibility(View.INVISIBLE);

        if (textView.getId() == R.id.yesterday_text)
        {
            oneTitleText.setVisibility(View.VISIBLE);
            oneTitleText2.setVisibility(View.GONE);
            oneTitleText3.setVisibility(View.GONE);
            oneTitleText4.setVisibility(View.GONE);
        }
        else if (textView.getId() == R.id.thisweek_text)
        {
            oneTitleText.setVisibility(View.GONE);
            oneTitleText2.setVisibility(View.VISIBLE);
            oneTitleText3.setVisibility(View.GONE);
            oneTitleText4.setVisibility(View.GONE);
        }
        else if (textView.getId() == R.id.thismonth_text)
        {
            oneTitleText.setVisibility(View.GONE);
            oneTitleText2.setVisibility(View.GONE);
            oneTitleText3.setVisibility(View.VISIBLE);
            oneTitleText4.setVisibility(View.GONE);
        }
        else if (textView.getId() == R.id.sofar_text)
        {
            oneTitleText.setVisibility(View.GONE);
            oneTitleText2.setVisibility(View.GONE);
            oneTitleText3.setVisibility(View.GONE);
            oneTitleText4.setVisibility(View.VISIBLE);
        }
        oneTitleView.setVisibility(View.VISIBLE);

        getPMLevelFromServer(requestId);

    }

    private void bigTitleTosmallTitle()
    {

        initSmallBackground();

    }



    @Override
    protected void onDraw(Canvas canvas)
    {
        super.onDraw(canvas);

    }


    /**
     * show collection text when big bottle show
     */
    public void showTitleCollectionAinimation(int duration)
    {
        AlphaAnimation animation = new AlphaAnimation(0, 1);
        animation.setDuration(duration);
        animation.setInterpolator(new AccelerateInterpolator());
        connectTextLayout.setAnimation(animation);
        animation.start();
        connectTextLayout.setVisibility(View.VISIBLE);
    }

    /**
     * hide collection text when big bottle hide
     */
    public void hideTitleCollectionAinimation()
    {
        AlphaAnimation animation = new AlphaAnimation(1, 0);
        animation.setDuration(800);
        animation.setInterpolator(new AccelerateInterpolator());
        connectTextLayout.setAnimation(animation);
        animation.start();
        connectTextLayout.setVisibility(View.INVISIBLE);
    }


    public void setLocationIdFromMainView(int locationId)
    {
        this.locationId = locationId;
    }



    public void getPMLevelFromServer(int requestTimeParamer)
    {

        isRequestBegin =  true;

        //show the animation
        setTitleAnimation(requestTimeParamer);

        String sessionId = AppManager.shareInstance().getAuthorizeApp().getSessionId();
        EmotionalBottleTask requestTask = new EmotionalBottleTask(locationId, requestTimeParamer, sessionId, null, mReceiveResponse);
        AsyncTaskExecutorUtil.executeAsyncTask(requestTask);

    }


    private void setTitleAnimation(int requestTimeParamer){
        //show the animation
        if (oneTitleView.getVisibility() == View.VISIBLE) {
            setTitleStutasAndAlphaBeginRequest(oneTitleView);
        }
        else if (requestTimeParamer == AirTouchConstants.YESTERDAY_REQUEST)
        {
            setTitleStutasAndAlphaBeginRequest(yesterdayView);
        }
        else if (requestTimeParamer == AirTouchConstants.THIS_WEEK_REQUEST)
        {
            setTitleStutasAndAlphaBeginRequest(thisweekView);
        }
        else if (requestTimeParamer == AirTouchConstants.THIS_MONTH_REQUEST)
        {
            setTitleStutasAndAlphaBeginRequest(thismonthView);
        }
        else if (requestTimeParamer == AirTouchConstants.SO_FAR_REQUEST)
        {
            setTitleStutasAndAlphaBeginRequest(sofarView);
        }
    }


    IActivityReceive mReceiveResponse = new IActivityReceive()
    {
        @Override
        public void onReceive(ResponseResult responseResult)
        {
            if (responseResult.isResult())
            {
                switch (responseResult.getRequestId())
                {
                    case EMOTION_BOTTLE:
                        isRequestBegin = false;
                        rebackTitleStatusAndAlphaEndRequest();
                        Bundle response = responseResult.getResponseData();
                        int particleLevel = getParticleLevel(response.getFloat("pm25_value"));
                        String text = String.format(getResources().getString(R.string
                                .collection_str_tip), (int)response.getFloat("pm25_value"));
                        collectedTextView.setText(Html.fromHtml(text));
                        mainView.setSharingData((int) response.getFloat("pm25_value"));
                        mainView.generateParticleAccordingLevel(particleLevel);
                        mainView.setEmotionBubbleContentValue(response.getFloat("pm25_value"),
                                response.getFloat("PAHs"), response.getDouble("cigerate_value"),
                                response.getFloat("lead_value"), response.getFloat("fume_second"));
                        break;
                }
            }
            else
            {
                //
                /**
                 * error request resolution
                 * 1.if there is no network,scroll the emotion to up and show the home cell
                 * 2.if there is any error from server exclude no network,just set the view apla 20%
                 *   and the data in view is all 0.and big bottle and tell someone button can not be clickeable
                 */
                clearTitleAnimation();
                String text = String.format(getResources().getString(R.string.collection_str_tip), 0);
                collectedTextView.setText(Html.fromHtml(text));
                mainView.resetTextAfterRequestError();
                collectedTextView.setAlpha(ALPHA_80);
            }
        }
    };


    /**
     * Suggest New: 0<L1<=1mg<L2<=10mg<L3<=75mg<L4<=150mg<L5<= 1.5g<L6<=15g<L7
     * @param cleanDust
     * @return
     */
    private int getParticleLevel(float cleanDust)
    {
        int originalLevel = AirTouchConstants.PARTICLE_LEVEL_NONE;
        if (cleanDust > 0 && cleanDust <= AirTouchConstants.LEVEL_ONE_MAX)
        {
            originalLevel = AirTouchConstants.PARTICLE_LEVEL_ONE;
        }
        if (cleanDust > AirTouchConstants.LEVEL_ONE_MAX && cleanDust <= AirTouchConstants.LEVEL_TWO_MAX)
        {
            originalLevel = AirTouchConstants.PARTICLE_LEVEL_TWO;
        }
        else if (cleanDust > AirTouchConstants.LEVEL_TWO_MAX && cleanDust <= AirTouchConstants.LEVEL_THREE_MAX)
        {
            originalLevel = AirTouchConstants.PARTICLE_LEVEL_THREE;
        }
        else if (cleanDust > AirTouchConstants.LEVEL_THREE_MAX && cleanDust <= AirTouchConstants.LEVEL_FOUR_MAX)
        {
            originalLevel = AirTouchConstants.PARTICLE_LEVEL_FOUR;
        }
        else if (cleanDust > AirTouchConstants.LEVEL_FOUR_MAX && cleanDust <= AirTouchConstants.LEVEL_FIVE_MAX)
        {
            originalLevel = AirTouchConstants.PARTICLE_LEVEL_FIVE;
        }
        else if (cleanDust > AirTouchConstants.LEVEL_FIVE_MAX && cleanDust <= AirTouchConstants.LEVEL_SIX_MAX)
        {
            originalLevel = AirTouchConstants.PARTICLE_LEVEL_SIX;
        }
        else if (cleanDust > AirTouchConstants.LEVEL_SIX_MAX )
        {
            originalLevel = AirTouchConstants.PARTICLE_LEVEL_SEVEN;
        }
        return originalLevel;
    }

    /**
     *
     * @param titleView RelativeLayout
     */
    private void setTitleStutasAndAlphaBeginRequest(RelativeLayout titleView)
    {
        titleView.startAnimation(AnimationUtils.loadAnimation(mContext, R.anim.emotiontitle_alpha));
        mainView.setIndoorViewStatusWhenRequesting(false);
    }

    private void rebackTitleStatusAndAlphaEndRequest()
    {
        clearTitleAnimation();

        mainView.setIndoorViewStatusWhenRequesting(true);
        collectedTextView.setAlpha(ALPHA_100);
    }

    private void clearTitleAnimation()
    {
        yesterdayView.clearAnimation();
        thismonthView.clearAnimation();
        thisweekView.clearAnimation();
        sofarView.clearAnimation();
        oneTitleView.clearAnimation();
    }


    /**
     * judge which which title is showing
     * @return requestIdForShare
     *       AirTouchConstants.NONE_REQUEST four small title is shown
     *       AirTouchConstants.YESTERDAY_REQUEST yesterday title is only shown
     *       AirTouchConstants.THIS_WEEK_REQUEST this week title is only shown
     *       AirTouchConstants.THIS_MONTH_REQUEST this month title is only shown
     *       AirTouchConstants.SO_FAR_REQUEST sofar title is only shown
     */
    public int getRequestIdForShare()
    {
        return requestIdForShare;
    }


    public void initTitleBg()
    {
        boolean isDaylight = AppConfig.shareInstance().isDaylight();
        int bgResourceId = isDaylight ? R.drawable.yesterday_background : R.drawable.night_background;

        if (requestIdForShare == AirTouchConstants.YESTERDAY_REQUEST)
        {
            yesterdayView.setBackgroundResource(bgResourceId);
        }
        if (requestIdForShare == AirTouchConstants.THIS_WEEK_REQUEST)
        {
            thisweekView.setBackgroundResource(bgResourceId);
        }
        if (requestIdForShare == AirTouchConstants.THIS_MONTH_REQUEST)
        {
            thismonthView.setBackgroundResource(bgResourceId);
        }
        if (requestIdForShare == AirTouchConstants.SO_FAR_REQUEST)
        {
            sofarView.setBackgroundResource(bgResourceId);
        }

    }

    private void setTitleViewBg(RelativeLayout titleView)
    {
        boolean isDaylight = AppConfig.shareInstance().isDaylight();
        int bgResourceId = isDaylight ? R.drawable.yesterday_background : R.drawable.night_background;
        titleView.setBackgroundResource(bgResourceId);
    }

}
