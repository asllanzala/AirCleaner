package com.honeywell.hch.airtouchv2.framework.share;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;

import com.honeywell.hch.airtouchv2.lib.util.LogUtil;
import com.umeng.socialize.bean.SHARE_MEDIA;
import com.umeng.socialize.bean.SocializeEntity;
import com.umeng.socialize.controller.UMServiceFactory;
import com.umeng.socialize.controller.UMSocialService;
import com.umeng.socialize.controller.listener.SocializeListeners;
import com.umeng.socialize.media.UMImage;
import com.umeng.socialize.sso.SinaSsoHandler;
import com.umeng.socialize.sso.UMSsoHandler;
import com.umeng.socialize.weixin.controller.UMWXHandler;

/**
 * Created by Jin Qian on 6/24/2015.
 */
public class ShareUtility {
    private static final String TAG = "AirTouchSharing";
    private static final String APPID = "wx390720e3d10d4b39";
    private static final String APPSECRET = "68217555d75cbf33774c59e048294045";
    private UMImage mUMImage = null;
    private static UMSocialService mController;
    private static Context mContext;
    private UmengShareListener mUmengShareListener;

    private static ShareUtility mUtility = null;

    public interface UmengShareListener {
        void onComplete();
    }

    public void setOnUmengShareListener(UmengShareListener listener) {
        mUmengShareListener = listener;
    }


    public static ShareUtility getInstance(Context context) {
        if (mUtility == null) {
            mUtility = new ShareUtility();

            mContext = context;
            mController = UMServiceFactory.getUMSocialService("com.umeng.share");
            mController.getConfig().setSsoHandler(new SinaSsoHandler());
            mController.getConfig().removePlatform(SHARE_MEDIA.TENCENT, SHARE_MEDIA.WEIXIN);
        }

        return mUtility;
    }

    public void shareMsgAndPic(Bitmap bitmap) {
        mUMImage = new UMImage(mContext, bitmap);
//        mController.setShareMedia(new UMImage(mContext, view.getDrawingCache()));
    }

    public void shareMsgAndPicUseByte(byte[] bitmapByte) {
        mUMImage = new UMImage(mContext, bitmapByte);
//        mController.setShareMedia(new UMImage(mContext, view.getDrawingCache()));
    }

    public void weChatShare() {
        UMWXHandler wxHandler = new UMWXHandler(mContext, APPID, APPSECRET);
        wxHandler.addToSocialSDK();

        UMWXHandler wxCircleHandler = new UMWXHandler(mContext, APPID, APPSECRET);
        wxCircleHandler.setToCircle(true);
        wxCircleHandler.addToSocialSDK();

        mController.setShareMedia(mUMImage);
        mController.postShare(mContext, SHARE_MEDIA.WEIXIN_CIRCLE,
                new SocializeListeners.SnsPostListener() {
                    @Override
                    public void onStart() {
                        LogUtil.log(LogUtil.LogLevel.INFO, TAG, "start to share.");
                    }

                    @Override
                    public void onComplete(SHARE_MEDIA platform, int eCode, SocializeEntity entity) {
                        mUmengShareListener.onComplete();

                        if (eCode == 200) {
                            LogUtil.log(LogUtil.LogLevel.INFO, TAG, "share success.");
                        } else {
                            String eMsg = "";
                            if (eCode == -101) {
                                eMsg = "no authorize";
                            }
                            LogUtil.log(LogUtil.LogLevel.INFO, TAG, "share fail [" + eCode + "] " + eMsg);
                        }
                    }
                });
    }


    public void weBoShare() {
        LogUtil.log(LogUtil.LogLevel.INFO, TAG, "webo to share.");

        mController.setShareMedia(mUMImage);
        mController.postShare(mContext, SHARE_MEDIA.SINA,
                new SocializeListeners.SnsPostListener() {


                    @Override
                    public void onStart() {
                        LogUtil.log(LogUtil.LogLevel.INFO, TAG, "start to share.");

                    }

                    @Override
                    public void onComplete(SHARE_MEDIA platform, int eCode, SocializeEntity entity) {
                        mUmengShareListener.onComplete();

                        if (eCode == 200) {
                            LogUtil.log(LogUtil.LogLevel.INFO, TAG, "share success.");
                        } else {
                            String eMsg = "";
                            if (eCode == -101) {
                                eMsg = "no authorize";
                            }
                            LogUtil.log(LogUtil.LogLevel.INFO, TAG, "share fail [" + eCode + "] " + eMsg);
                        }
                    }
                });
    }

    public void addSinaCallback(int requestCode, int resultCode, Intent data) {
        UMSsoHandler ssoHandler = mController.getConfig().getSsoHandler(requestCode);
        if (ssoHandler != null) {
            ssoHandler.authorizeCallBack(requestCode, resultCode, data);
        }
    }

}
