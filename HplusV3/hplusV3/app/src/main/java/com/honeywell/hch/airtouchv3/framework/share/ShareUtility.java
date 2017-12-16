package com.honeywell.hch.airtouchv3.framework.share;

import android.app.Activity;
import android.content.Context;
import android.graphics.Bitmap;
import android.widget.Toast;

import com.honeywell.hch.airtouchv3.R;
import com.tencent.mm.sdk.openapi.IWXAPI;
import com.tencent.mm.sdk.openapi.SendMessageToWX;
import com.tencent.mm.sdk.openapi.WXImageObject;
import com.tencent.mm.sdk.openapi.WXMediaMessage;
import com.tencent.mm.sdk.platformtools.Util;
import com.umeng.socialize.ShareAction;
import com.umeng.socialize.UMShareListener;
import com.umeng.socialize.bean.SHARE_MEDIA;
import com.umeng.socialize.media.UMImage;

/**
 * Created by Jin Qian on 6/24/2015.
 */
public class ShareUtility {
    private static final String TAG = "AirTouchSharing";
    public static final String APPID = "wx390720e3d10d4b39";
    public static final String APPSECRET = "68217555d75cbf33774c59e048294045";
    private UMImage mUMImage = null;
    private static Context mContext;
    private UmengShareListener mUmengShareListener;

    private static ShareUtility mUtility = null;

    //weibo
    public static final String WB_APPID = "1764920366";
    public static final String WB_APPSECRET = "a477eb74accf7a06159d2112a6761737";

    private Bitmap mShareBitmap;

    private static final int THUMB_SIZE = 150;


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
        }
        return mUtility;
    }

    public void shareMsgAndPic(Bitmap bitmap) {
        mShareBitmap = bitmap;
    }

    public void shareMsgAndPicUseByte(byte[] bitmapByte) {
        mUMImage = new UMImage(mContext, bitmapByte);
    }

    public void wxShare(IWXAPI api) {
        if(mShareBitmap == null) {
            printToast(R.string.errcode_fail);
            return;
        }
        WXImageObject imgObj = new WXImageObject(mShareBitmap);

        WXMediaMessage msg = new WXMediaMessage();
        msg.mediaObject = imgObj;

        Bitmap thumbBmp = Bitmap.createScaledBitmap(mShareBitmap, THUMB_SIZE, THUMB_SIZE, true);
        msg.thumbData = Util.bmpToByteArray(thumbBmp, true);

        SendMessageToWX.Req req = new SendMessageToWX.Req();
        req.transaction = buildTransaction("img");
        req.message = msg;
        req.scene = SendMessageToWX.Req.WXSceneTimeline;
        boolean result = api.sendReq(req);
        if (result == false) {
            printToast(R.string.weixin_nomount);
        }
    }

    public void umengWBShare() {
        new ShareAction((Activity) mContext)
                .setPlatform(SHARE_MEDIA.SINA)
                .setCallback(umShareListener)
                .withMedia(mUMImage)
                .withText("  ")
                .share();
    }

    /**
     * auth callback interface
     **/

    UMShareListener umShareListener = new UMShareListener() {
        @Override
        public void onResult(SHARE_MEDIA platform) {
            printToast(R.string.errcode_success);
        }

        @Override
        public void onError(SHARE_MEDIA platform, Throwable t) {
            printToast(R.string.errcode_fail);
        }

        @Override
        public void onCancel(SHARE_MEDIA platform) {
            printToast(R.string.errcode_cancel);
        }
    };

    private void printToast(int id) {
        Toast.makeText(mContext, id ,Toast.LENGTH_SHORT).show();
    }

    private String buildTransaction(final String type) {
        return (type == null) ? String.valueOf(System.currentTimeMillis()) : type + System.currentTimeMillis();
    }

}
