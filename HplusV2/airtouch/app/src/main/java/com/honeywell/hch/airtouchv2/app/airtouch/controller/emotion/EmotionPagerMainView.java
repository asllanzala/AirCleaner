package com.honeywell.hch.airtouchv2.app.airtouch.controller.emotion;

import android.content.Context;
import android.graphics.Bitmap;
import android.text.Html;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.honeywell.hch.airtouchv2.R;
import com.honeywell.hch.airtouchv2.framework.config.AppConfig;
import com.honeywell.hch.airtouchv2.framework.global.AirTouchConstants;
import com.honeywell.hch.airtouchv2.framework.share.ShareUtility;
import com.honeywell.hch.airtouchv2.lib.util.DensityUtil;
import com.honeywell.hch.airtouchv2.lib.util.LogUtil;

import java.io.ByteArrayOutputStream;
import java.text.DecimalFormat;

/**
 * Created by wuyuan on 15/6/10.
 */
public class EmotionPagerMainView extends RelativeLayout {

    private Context mContext;

    private RelativeLayout indoorBottomView;

    private RelativeLayout outdoorBottonView;

    private ImageView indoorpickImageView;

    private TextView indoorBottomTextView;

    private ImageView outdoorImageView;

    private TextView outdoorBottonTextView;

    private EmotionPagementIndoorView indoorEmotionPagerView;

    private EmotionPagementOutdoorView outdoorEmotionPagerView;

    private EmotionPagerTitleView emotionPagerTitleView;

    // Umeng sharing
    private ShareUtility mSharingUtility;
    private ShareEndCallback mShareEndCallback;
    private LinearLayout emotionShareLayout;
    private LinearLayout emotionShareDummyLayout;
    private Animation translateInAnimation;
    private Animation translateOutAnimation;
    private ImageView weChatShareImageView;
    private ImageView weBoShareImageView;
    private Thread mWechatSharingThread;
    private Thread mWeboSharingThread;
    private LinearLayout shareCancelLayout;
    private View mShareXmlView;
    private TextView mShareCollectedTextView;
    private ImageView mShareCaptureView;
    private Bitmap mFullScreenBitmap;
    private Bitmap mCaptureScreenBitmap;
    private Bitmap mSocialShareBitmap;
    private ImageView backgroundImageviewForShare;

    private int locationId;

    private ImageView backgroundImageview;

    private ByteArrayOutputStream baos;


    /**
     * set emotionpager show status. default status is
     * 1. only one big yesterday is shown (bigYesterdayShown)
     * 2.big bottle is shown (bigBottleShown)
     * 3. share is invisible (shareIsInvisible)
     * every situation has a boolean value ,is the status is not default of one ,set false.
     * otherwise set true
     */
    private boolean bigYesterdayShown = true;
    private boolean bigBottleShown = true;
    private boolean shareIsInvisible = true;


    public EmotionPagerMainView(Context context) {
        super(context);
        mContext = context;
        initView();

    }

    public EmotionPagerMainView(Context context, AttributeSet attrs) {
        super(context, attrs);
        mContext = context;
        initView();

    }

    public EmotionPagerMainView(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        mContext = context;
        initView();

    }

    /**
     * set location id from halfHomeCell.
     *
     * @param localId
     */
    public void setLocationId(int localId) {
        locationId = localId;
        emotionPagerTitleView.setLocationIdFromMainView(localId);
    }

    private void initView() {

        LayoutInflater inflater = (LayoutInflater) mContext.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View view = inflater.inflate(R.layout.emotionpage_main, this);

        backgroundImageview = (ImageView) view.findViewById(R.id.emotion_bg);
        indoorEmotionPagerView = (EmotionPagementIndoorView) view.findViewById(R.id.emotion_indoor_page);
        outdoorEmotionPagerView = (EmotionPagementOutdoorView) view.findViewById(R.id.emotion_outdoor_page);
        emotionPagerTitleView = (EmotionPagerTitleView) view.findViewById(R.id.title_view);
        emotionPagerTitleView.setEmotionPagerMainView(this);
        indoorEmotionPagerView.setMainView(this);


        indoorBottomView = (RelativeLayout) view.findViewById(R.id.indoor_btn_view);
        indoorpickImageView = (ImageView) view.findViewById(R.id.indoor_btn_view_image);
        indoorBottomTextView = (TextView) view.findViewById(R.id.indoor_btn_view_txt);
        indoorpickImageView.setImageResource(R.drawable.pitch_click);
        indoorBottomTextView.setTextColor(getResources().getColor(R.color.emotion_btn_pick));
        indoorBottomView.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                indoorEmotionPagerView.setVisibility(View.VISIBLE);
                outdoorEmotionPagerView.setVisibility(View.GONE);
                setIndoorBtnViewPicked();

            }
        });

        outdoorBottonView = (RelativeLayout) view.findViewById(R.id.outdoor_btn_view);
        outdoorImageView = (ImageView) view.findViewById(R.id.outdoor_btn_view_image);
        outdoorBottonTextView = (TextView) view.findViewById(R.id.outdoor_btn_view_txt);
        outdoorImageView.setImageResource(R.drawable.pitch_unclick);
        outdoorBottonTextView.setTextColor(getResources().getColor(R.color.emotion_btn_unpick));

        outdoorBottonView.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                indoorEmotionPagerView.setVisibility(View.GONE);
                outdoorEmotionPagerView.setVisibility(View.VISIBLE);
                setOutdoorBtnViewPicked();
            }
        });

//        initFourTileView(view);

        initSharingView();
    }

    public interface ShareEndCallback {
        void onEnd();
    }

    //When the sharelayout is showing,hide the layout at the outside clicking
    public void hideEmotionShareDummyLayout() {
        if (emotionShareDummyLayout.getVisibility() == VISIBLE) {
            emotionShareDummyLayout.setVisibility(View.INVISIBLE);
            emotionShareLayout.startAnimation(translateOutAnimation);
            mShareEndCallback.onEnd();
        }

        recycleShareBitmap();

    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        hideEmotionShareDummyLayout();
        return false;
    }

    public void setOnShareEnd(ShareEndCallback callback) {
        mShareEndCallback = callback;
    }

    private class translateInAnimationListener implements Animation.AnimationListener {
        @Override
        public void onAnimationEnd(Animation animation) {
            emotionShareDummyLayout.setVisibility(View.VISIBLE);
        }

        @Override
        public void onAnimationRepeat(Animation arg0) {
            // TODO Auto-generated method stub
        }

        @Override
        public void onAnimationStart(Animation arg0) {
            // TODO Auto-generated method stub
        }
    }


    private void setIndoorBtnViewPicked() {
        indoorpickImageView.setImageResource(R.drawable.pitch_click);
        indoorBottomTextView.setTextColor(getResources().getColor(R.color.emotion_btn_pick));
        outdoorImageView.setImageResource(R.drawable.pitch_unclick);
        outdoorBottonTextView.setTextColor(getResources().getColor(R.color.emotion_btn_unpick));
    }

    private void setOutdoorBtnViewPicked() {
        indoorpickImageView.setImageResource(R.drawable.pitch_unclick);
        indoorBottomTextView.setTextColor(getResources().getColor(R.color.emotion_btn_unpick));
        outdoorImageView.setImageResource(R.drawable.pitch_click);
        outdoorBottonTextView.setTextColor(getResources().getColor(R.color.emotion_btn_pick));

    }

    /**
     * stop the particle moving and clear the particle list
     */
    public void stopParticleMoving() {
        if (indoorEmotionPagerView != null) {
            indoorEmotionPagerView.stopParticleMoving();
        }
    }


    /**
     * set the alpha of collect text of title view and indoor view
     * when title's status switch
     *
     * @param isBottleCanClickable
     */
    public void setIndoorViewStatusWhenRequesting(boolean isBottleCanClickable) {

        indoorEmotionPagerView.setBottleClickable(isBottleCanClickable);
    }

    public void setViewAlphaIfRequestFail(float alpha) {
        indoorEmotionPagerView.setIndoorLayoutAlphaWhenTitleSwitch(alpha);
    }

    /**
     * Umeng social sharing
     */
    private void initSharingView() {
        LayoutInflater inflater = (LayoutInflater) mContext.getSystemService(Context.LAYOUT_INFLATER_SERVICE);

        mSharingUtility = ShareUtility.getInstance(mContext);
        weChatShareImageView = (ImageView) findViewById(R.id.wechat_share_btn_dummy);

        weChatShareImageView.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                if (mWechatSharingThread == null) {
                    mSharingUtility.shareMsgAndPicUseByte(bitmap2Bytes(mSocialShareBitmap));

                    mSharingUtility.weChatShare();
                    mWechatSharingThread = new Thread(new Runnable() {
                        public void run() {
                            try {
                                Thread.sleep(2000);
                                mWechatSharingThread = null;
                            } catch (InterruptedException e) {
                                // TODO Auto-generated catch block
                                e.printStackTrace();
                            }
                        }
                    });
                    mWechatSharingThread.start();
                }
            }
        });
        weBoShareImageView = (ImageView) findViewById(R.id.webo_share_btn_dummy);
        weBoShareImageView.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                mSharingUtility.shareMsgAndPicUseByte(bitmap2Bytes(mSocialShareBitmap));
                if (mWeboSharingThread == null) {
                    mSharingUtility.shareMsgAndPicUseByte(bitmap2Bytes(mSocialShareBitmap));

                    mSharingUtility.weBoShare();
                    mWeboSharingThread = new Thread(new Runnable() {
                        public void run() {
                            try {
                                Thread.sleep(2000);
                                mWeboSharingThread = null;
                            } catch (InterruptedException e) {
                                // TODO Auto-generated catch block
                                e.printStackTrace();
                            }
                        }
                    });
                    mWeboSharingThread.start();

                }
            }
        });
        shareCancelLayout = (LinearLayout) findViewById(R.id.share_cancel_layout_dummy);
        shareCancelLayout.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                hideEmotionShareDummyLayout();

            }
        });
        emotionShareLayout = (LinearLayout) findViewById(R.id.emotion_share_layout);
        emotionShareLayout.setVisibility(View.INVISIBLE);
        translateInAnimation = AnimationUtils.loadAnimation(mContext, R.anim.share_translate_in);
        translateOutAnimation = AnimationUtils.loadAnimation(mContext, R.anim.share_translate_out);
        translateInAnimation.setAnimationListener(new translateInAnimationListener());
        emotionShareDummyLayout = (LinearLayout) findViewById(R.id.emotion_share_layout_dummy);
        emotionShareDummyLayout.setVisibility(View.INVISIBLE);

        mShareXmlView = inflater.inflate(R.layout.emotional_bottle_share, null);
        mShareCaptureView = (ImageView) mShareXmlView.findViewById(R.id.share_capture_iv);
        backgroundImageviewForShare = (ImageView) mShareXmlView.findViewById(R.id.share_bg);
        mShareCollectedTextView = (TextView) mShareXmlView.findViewById(R.id.share_collected);
    }

    public void shareCaptureScreen() {
        View view = this.getRootView();
        view.setDrawingCacheEnabled(true);

        mFullScreenBitmap = view.getDrawingCache();
        mCaptureScreenBitmap = Bitmap.createBitmap(mFullScreenBitmap, 0, 0,
                DensityUtil.getScreenWidth(), indoorEmotionPagerView.getHeight());
        mShareCaptureView.setImageBitmap(mCaptureScreenBitmap);
        view.setDrawingCacheEnabled(false);
    }

    public void setSharingData(int pm25) {
        String text = String.format(getResources().getString(R.string.collection_str_tip), pm25);
        mShareCollectedTextView.setText(Html.fromHtml(text));
    }

    public void socialShare() {
        emotionShareLayout.startAnimation(translateInAnimation);
        shareCaptureScreen();

        // set sharing picture's background
        RelativeLayout.LayoutParams params
                = new RelativeLayout.LayoutParams(backgroundImageviewForShare.getLayoutParams());
        params.height = DensityUtil.getScreenHeight();
        params.width = DensityUtil.getScreenWidth();
        backgroundImageviewForShare.setLayoutParams(params);
        backgroundImageviewForShare.setImageResource(AppConfig.shareInstance().isDaylight() ?
                R.drawable.backround_new_day : R.drawable.emotional_background_night);

        // set up sharing picture's view
        mShareXmlView.destroyDrawingCache();
        mShareXmlView.setDrawingCacheEnabled(true);
//        mShareXmlView.buildDrawingCache();
        mShareXmlView.measure(mShareXmlView.getMeasuredWidth(), mShareXmlView.getMeasuredHeight());
        mShareXmlView.layout(0, 0, DensityUtil.getScreenWidth(), DensityUtil.getScreenHeight());

        mSocialShareBitmap = mShareXmlView.getDrawingCache();
//        mSocialShareBitmap = BitmapUtil.convertViewToBitmap(mShareXmlView);
//        mSocialShareBitmap = BitmapUtil.compress(mSocialShareBitmap, 80);
        mSharingUtility.setOnUmengShareListener(new ShareUtility.UmengShareListener() {
            @Override
            public void onComplete() {
//                recycleShareBitmap();
            }
        });
    }


    private byte[] bitmap2Bytes(Bitmap bm) {
        if (baos == null){
            baos = new ByteArrayOutputStream();
            bm.compress(Bitmap.CompressFormat.PNG, 100, baos);
        }

        return baos.toByteArray();
    }


    public void recycleShareBitmap() {
        if (mFullScreenBitmap != null && !mFullScreenBitmap.isRecycled()) {
            mFullScreenBitmap.recycle();
            mFullScreenBitmap = null;
        }

        if (mCaptureScreenBitmap != null && !mCaptureScreenBitmap.isRecycled()) {
            mCaptureScreenBitmap.recycle();
            mCaptureScreenBitmap = null;
        }

        if (mSocialShareBitmap != null && !mSocialShareBitmap.isRecycled()) {
            mSocialShareBitmap.recycle();
            mSocialShareBitmap = null;
        }
        if (baos != null){
            try{
                baos.close();
            }catch (Exception e){
                LogUtil.log(LogUtil.LogLevel.ERROR,"MainView","close baos = " + e.toString());
            }
            finally
            {
                baos = null;
            }

        }

        if (mShareXmlView != null)
            mShareXmlView.setDrawingCacheEnabled(false);

    }
    // end of Umeng sharing


    public void showTitleCollectionAinimation(int duration) {
        emotionPagerTitleView.showTitleCollectionAinimation(duration);
    }

    public void hideTitleCollectionAinimation() {
        emotionPagerTitleView.hideTitleCollectionAinimation();
    }

    /**
     * generate particle according the level from server,call generateParticleAccordingLevel method in EmotionPagementIndoorView
     *
     * @param level level from server
     */
    public void generateParticleAccordingLevel(int level) {
        indoorEmotionPagerView.generateParticleAccordingLevel(level);
    }

    /**
     * when the first time scroll to emotion pager,should get the Yesterday request
     */
    public void requestYesterdayFirstTime() {
        emotionPagerTitleView.getPMLevelFromServer(AirTouchConstants.YESTERDAY_REQUEST);
    }


    /**
     * when scroll a home to another,we should set the emotionPager to the default status
     */
    public void setEmotionPagerToDefaultSatus() {
        stopParticleMoving();
        emotionPagerTitleView.setTitleShowDefaultStatus();
        indoorEmotionPagerView.resetIndoorViewStatusToBack();
    }

    /**
     * switch the backgroud when time change
     */
    public void switchBgAccordingTime() {
        boolean isDaylight = AppConfig.shareInstance().isDaylight();
        backgroundImageview.setImageResource(isDaylight ? R.drawable.backround_new_day : R
                .drawable.emotional_background_night);
        emotionPagerTitleView.initTitleBg();


    }

    /**
     * set the bubble content value from server
     */
    public void setEmotionBubbleContentValue(float pmvalue, float pathValue, double cigerete, float lead, float curfume) {
        DecimalFormat df2 = new DecimalFormat("0.00");

        String pathValueStr = AirTouchConstants.INIT_STR_VALUE;
        if (pathValue != 0) {
            pathValueStr = df2.format(pathValue);
        }

        String cigreteStr = String.valueOf((int) cigerete);
//        if (cigerete != 0)
//        {
//            cigreteStr = df2.format(cigerete);
//        }

        String leadValueStr = AirTouchConstants.INIT_STR_VALUE;
        if (lead != 0) {
            leadValueStr = df2.format(lead);
        }

        //keep two  significant figures
        String carFumeStr = String.valueOf((int) curfume);
//        if (curfume != 0)
//        {
//            carFumeStr = df2.format(curfume);
//        }

        indoorEmotionPagerView.setBubbleScrollTextValue(pmvalue, pathValueStr, leadValueStr, cigreteStr, carFumeStr);

    }

    /**
     * set textview value 0 if request emotion error and clear particle
     */
    public void resetTextAfterRequestError() {
        indoorEmotionPagerView.setBottleClickable(false);
        stopParticleMoving();
        indoorEmotionPagerView.resetIndoorTextAfterRequestError();

        indoorEmotionPagerView.setAlpha(0.8f);
    }

}
