package com.honeywell.hch.airtouchv3.wxapi;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.text.Html;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.honeywell.hch.airtouchv3.R;
import com.honeywell.hch.airtouchv3.app.airtouch.controller.emotion.EmotionPagementIndoorView;
import com.honeywell.hch.airtouchv3.app.airtouch.controller.emotion.EmotionPagerTitleView;
import com.honeywell.hch.airtouchv3.app.airtouch.controller.enrollment.smartlink.EnrollAccessManager;
import com.honeywell.hch.airtouchv3.app.authorize.AuthorizeApp;
import com.honeywell.hch.airtouchv3.app.authorize.controller.UserLoginActivity;
import com.honeywell.hch.airtouchv3.framework.app.AppManager;
import com.honeywell.hch.airtouchv3.framework.app.activity.BaseHasBackgroundActivity;
import com.honeywell.hch.airtouchv3.framework.config.AppConfig;
import com.honeywell.hch.airtouchv3.framework.global.AirTouchConstants;
import com.honeywell.hch.airtouchv3.framework.share.ShareUtility;
import com.honeywell.hch.airtouchv3.framework.view.MessageBox;
import com.honeywell.hch.airtouchv3.lib.util.DensityUtil;
import com.honeywell.hch.airtouchv3.lib.util.LogUtil;
import com.tencent.mm.sdk.openapi.BaseReq;
import com.tencent.mm.sdk.openapi.BaseResp;
import com.tencent.mm.sdk.openapi.ConstantsAPI;
import com.tencent.mm.sdk.openapi.IWXAPI;
import com.tencent.mm.sdk.openapi.IWXAPIEventHandler;
import com.tencent.mm.sdk.openapi.WXAPIFactory;
import com.umeng.socialize.UMShareAPI;

import java.io.ByteArrayOutputStream;
import java.text.DecimalFormat;

/**
 * The original name is "EmotionalPagerActivity"
 * Created by wuyuan on 15/6/10.
 */
public class WXEntryActivity extends BaseHasBackgroundActivity implements IWXAPIEventHandler {
    private final String TAG = "WXEntryActivity";
    private final String CLASSFULLNAME = "com.honeywell.hch.airtouch.wxapi.WXEntryActivity";
    public static final String LOCATION_ID = "location_id";
    public static final String IS_HAVE_DEVICE = "is_has_device";

    private RelativeLayout indoorBottomView;

    private ImageView indoorpickImageView;

    private TextView indoorBottomTextView;

    private EmotionPagementIndoorView indoorEmotionPagerView;
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

    private ByteArrayOutputStream baos;


    private RelativeLayout mEmotionalRootView;

    private View mHasDeviceLayout;
    private View mNoDeviceLayout;

    private TextView mEnrollNowTextView;
    private TextView mCancelTextView;
    private RelativeLayout mCancelImageView;

    private boolean isHasDevice = false;
    private AuthorizeApp mAuthorizeApp;
    private ImageView mNoDeviceImageView;

    private IWXAPI api;


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        isHasDevice = getIntent().getBooleanExtra(IS_HAVE_DEVICE, false);

        setContentView(R.layout.emotionpage_main);

        initDynamicBackground();

        initWXapi();

        mAuthorizeApp = AppManager.shareInstance().getAuthorizeApp();
        mHasDeviceLayout = findViewById(R.id.has_devicelayoutid);
        mNoDeviceLayout = findViewById(R.id.no_device_layoutid);


        mCancelImageView = (RelativeLayout) findViewById(R.id.cancel_btn);
        mCancelImageView.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
        if (isHasDevice){
            initHasDeviceView();
            mNoDeviceLayout.setVisibility(View.GONE);
            mHasDeviceLayout.setOnTouchListener(new View.OnTouchListener() {
                @Override
                public boolean onTouch(View v, MotionEvent event) {
                    hideEmotionShareDummyLayout();
                    return false;
                }
            });
        }
        else{
            initNoDeviceView();
            mHasDeviceLayout.setVisibility(View.GONE);
        }
    }

    private void initWXapi () {
        api = WXAPIFactory.createWXAPI(mContext, ShareUtility.APPID, false);
        api.registerApp(ShareUtility.APPID);
        api.handleIntent(getIntent(), this);
    }

    @Override
    public void onResume() {
        super.onResume();
    }

    @Override
    public void onDestroy(){
        super.onDestroy();

        stopParticleMoving();
    }


    private void initHasDeviceView() {

        mEmotionalRootView = (RelativeLayout)findViewById(R.id.emotional_main_id);
        indoorEmotionPagerView = (EmotionPagementIndoorView) findViewById(R.id.emotion_indoor_page);

        emotionPagerTitleView = (EmotionPagerTitleView) findViewById(R.id.title_view);
        emotionPagerTitleView.setEmotionPagerMainView(this);
        indoorEmotionPagerView.setMainView(this);
        emotionPagerTitleView.setLocationIdFromMainView(mLocationId);

        indoorBottomView = (RelativeLayout) findViewById(R.id.indoor_btn_view);
        indoorpickImageView = (ImageView) findViewById(R.id.indoor_btn_view_image);
        indoorBottomTextView = (TextView) findViewById(R.id.indoor_btn_view_txt);
        indoorpickImageView.setImageResource(R.drawable.pitch_click);
        indoorBottomTextView.setTextColor(getResources().getColor(R.color.emotion_btn_pick));
        indoorBottomView.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                indoorEmotionPagerView.setVisibility(View.VISIBLE);
                setIndoorBtnViewPicked();

            }
        });

        initSharingView();

        requestYesterdayFirstTime();
    }

    private void initNoDeviceView(){
        mEnrollNowTextView = (TextView)findViewById(R.id.begin_enroll);
        mNoDeviceImageView = (ImageView)findViewById(R.id.bottle_image_id);
        mNoDeviceImageView.setImageDrawable(getResources().getDrawable(R.drawable.bottle_nodevice));

        mEnrollNowTextView.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                decideWhichActivityGo();
            }
        });

        mCancelTextView = (TextView)findViewById(R.id.cancel_id);
        mCancelTextView.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
    }

    private void decideWhichActivityGo(){
        if (!mAuthorizeApp.isLoginSuccess()){
            MessageBox.createTwoButtonDialog(this, null,
                    getString(R.string.not_login), getString(R.string.yes),
                    enrollLoginButton, getString(R.string.no), null);
        }
        else{
            EnrollAccessManager.startIntent(WXEntryActivity.this, CLASSFULLNAME);
            finish();
        }
    }
    private MessageBox.MyOnClick enrollLoginButton = new MessageBox.MyOnClick() {
        @Override
        public void onClick(View v) {
            mAuthorizeApp.setIsUserWantToEnroll(true);
            Intent intent = new Intent();
            intent.setClass(WXEntryActivity.this, UserLoginActivity.class);
            startActivity(intent);
            finish();
        }
    };
    public interface ShareEndCallback {
        void onEnd();
    }

    //When the sharelayout is showing,hide the layout at the outside clicking
    public void hideEmotionShareDummyLayout() {
        if (emotionShareDummyLayout.getVisibility() == View.VISIBLE) {
            emotionShareDummyLayout.setVisibility(View.INVISIBLE);
            emotionShareLayout.startAnimation(translateOutAnimation);
            indoorEmotionPagerView.setTellSomeoneCareView(View.VISIBLE);
            mShareEndCallback.onEnd();
        }

        recycleShareBitmap();

    }


    public void setOnShareEnd(ShareEndCallback callback) {
        mShareEndCallback = callback;
    }

    private class translateInAnimationListener implements Animation.AnimationListener {
        @Override
        public void onAnimationEnd(Animation animation) {
            emotionShareDummyLayout.setVisibility(View.VISIBLE);
            indoorEmotionPagerView.setTellSomeoneCareView(View.GONE);
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
    }

    private void setOutdoorBtnViewPicked() {
        indoorpickImageView.setImageResource(R.drawable.pitch_unclick);
        indoorBottomTextView.setTextColor(getResources().getColor(R.color.emotion_btn_unpick));
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


    /**
     * Umeng social sharing
     */
    private void initSharingView() {
        LayoutInflater inflater = (LayoutInflater) getSystemService(Context.LAYOUT_INFLATER_SERVICE);

        mSharingUtility = ShareUtility.getInstance(this);
        weChatShareImageView = (ImageView) findViewById(R.id.wechat_share_btn_dummy);

        weChatShareImageView.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                if (mWechatSharingThread == null) {
                    mSharingUtility.shareMsgAndPic(mSocialShareBitmap);
                    mSharingUtility.wxShare(api);
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
                    mSharingUtility.umengWBShare();
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
        translateInAnimation = AnimationUtils.loadAnimation(this, R.anim.share_translate_in);
        translateOutAnimation = AnimationUtils.loadAnimation(this, R.anim.share_translate_out);
        translateInAnimation.setAnimationListener(new translateInAnimationListener());
        emotionShareDummyLayout = (LinearLayout) findViewById(R.id.emotion_share_layout_dummy);
        emotionShareDummyLayout.setVisibility(View.INVISIBLE);

        mShareXmlView = inflater.inflate(R.layout.emotional_bottle_share, null);
        mShareCaptureView = (ImageView) mShareXmlView.findViewById(R.id.share_capture_iv);
        backgroundImageviewForShare = (ImageView) mShareXmlView.findViewById(R.id.share_bg);
        mShareCollectedTextView = (TextView) mShareXmlView.findViewById(R.id.share_collected);
    }
    public View shareCaptureScreen() {
        View view = mEmotionalRootView.getRootView();
        view.setDrawingCacheEnabled(true);

        mFullScreenBitmap = view.getDrawingCache();
        mCaptureScreenBitmap = Bitmap.createBitmap(mFullScreenBitmap, 0, 0,
                DensityUtil.getScreenWidth(), indoorEmotionPagerView.getHeight());
        mShareCaptureView.setImageBitmap(mCaptureScreenBitmap);
        return view;
    }

    public void setSharingData(int pm25) {
        String text = String.format(getResources().getString(R.string.collection_str_tip), pm25);
        mShareCollectedTextView.setText(Html.fromHtml(text));
    }

    public void socialShare() {
        emotionShareLayout.startAnimation(translateInAnimation);
        View view = shareCaptureScreen();
        // set sharing picture's background
        RelativeLayout.LayoutParams params
                = new RelativeLayout.LayoutParams(backgroundImageviewForShare.getLayoutParams());
        params.height = DensityUtil.getScreenHeight();
        params.width = DensityUtil.getScreenWidth();
        backgroundImageviewForShare.setLayoutParams(params);
        backgroundImageviewForShare.setImageBitmap(mFullScreenBitmap);
        // set up sharing picture's view
        mShareXmlView.destroyDrawingCache();
        mShareXmlView.setDrawingCacheEnabled(true);

        mShareXmlView.measure(mShareXmlView.getMeasuredWidth(), mShareXmlView.getMeasuredHeight());
        mShareXmlView.layout(0, 0, DensityUtil.getScreenWidth(), DensityUtil.getScreenHeight());

        mSocialShareBitmap = mShareXmlView.getDrawingCache();
        view.setDrawingCacheEnabled(false);
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
            if (bm != null){
                bm.compress(Bitmap.CompressFormat.PNG, 100, baos);
            }
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
     * switch the backgroud when time change
     */
    public void switchBgAccordingTime() {
        boolean isDaylight = AppConfig.shareInstance().isDaylight();
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

        String leadValueStr = AirTouchConstants.INIT_STR_VALUE;
        if (lead != 0) {
            leadValueStr = df2.format(lead);
        }

        //keep two  significant figures
        String carFumeStr = String.valueOf((int) curfume);
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

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        /** attention to this below ,must add this**/
        LogUtil.log(LogUtil.LogLevel.INFO, TAG, "onActivityResult");
        UMShareAPI.get(this).onActivityResult(requestCode, resultCode, data);

    }

    @Override
    public void onReq(BaseReq req) {
        LogUtil.log(LogUtil.LogLevel.INFO, TAG, "onReq");
        switch (req.getType()) {
            case ConstantsAPI.COMMAND_GETMESSAGE_FROM_WX:
                LogUtil.log(LogUtil.LogLevel.INFO, TAG,"COMMAND_GETMESSAGE_FROM_WX");

                break;
            case ConstantsAPI.COMMAND_SHOWMESSAGE_FROM_WX:
                LogUtil.log(LogUtil.LogLevel.INFO, TAG,"COMMAND_SHOWMESSAGE_FROM_WX");

                break;
            default:
                break;
        }
    }

    @Override
    public void onResp(BaseResp resp) {
        int result = 0;
        LogUtil.log(LogUtil.LogLevel.INFO, TAG,"onResp");

        switch (resp.errCode) {
            case BaseResp.ErrCode.ERR_OK:
                result = R.string.errcode_success;
                break;
            case BaseResp.ErrCode.ERR_USER_CANCEL:
                result = R.string.errcode_cancel;
                break;
            case BaseResp.ErrCode.ERR_AUTH_DENIED:
                result = R.string.errcode_deny;
                break;
            default:
                result = R.string.errcode_unknown;
                break;
        }

        Toast.makeText(this, result, Toast.LENGTH_LONG).show();
    }

    @Override
    protected void onNewIntent(Intent intent) {
        super.onNewIntent(intent);

        setIntent(intent);
        api.handleIntent(intent, this);
    }

}
