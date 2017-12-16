package com.honeywell.hch.airtouchv3.app.dashboard.controller.location;

import android.view.View;

import com.honeywell.hch.airtouchv3.app.dashboard.controller.MainActivity;
import com.nineoldandroids.view.ViewHelper;

/**
 * create by Stephen,Wu(H127856)
 * add animation to the viewpager
 */
public class DepthPageTransformer implements ViewPager.PageTransformer {
	private static float MIN_SCALE = 0.5f;

	private MainActivity mMainActivity;

	public DepthPageTransformer(MainActivity mainActivity){
		mMainActivity = mainActivity;
	}

	@Override
	public void transformPage(View view, float position) {
		/**
		 * [-1,0]Use the default slide transition when moving to the left page
		 * (0,1] Use the default slide transition when moving to the right page
		 * (1,+Infinity] This page is way off-screen to the right.
		 * [-Infinity,-1) This page is way off-screen to the left.
		 */
		int pageWidth = view.getWidth();
		if (mMainActivity.getCurrentHomeIndex() < mMainActivity.getHomeList().size()){
			if (view == mMainActivity.getHomeList().get(mMainActivity.getCurrentHomeIndex()).getTopView() &&
					(position <= -1 || position >= 1)){
				position = 0;
			}
		}

		if (position >= 0){
			ViewHelper.setTranslationX(view,0);
			ViewHelper.setScaleX(view,1);
			ViewHelper.setScaleY(view,1);
		}
		else if (position >= -1){
			ViewHelper.setTranslationX(view,pageWidth * -position);

			// Scale the page down (between MIN_SCALE and 1)
			float scaleFactor = MIN_SCALE + (1 - MIN_SCALE)
					* (1 - Math.abs(position));

			ViewHelper.setScaleX(view,scaleFactor);
			ViewHelper.setScaleY(view,scaleFactor);
		}

	}

}