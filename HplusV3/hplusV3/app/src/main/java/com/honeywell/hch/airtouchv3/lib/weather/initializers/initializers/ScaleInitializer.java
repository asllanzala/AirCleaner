package com.honeywell.hch.airtouchv3.lib.weather.initializers.initializers;

import com.honeywell.hch.airtouchv3.lib.weather.initializers.Particle;

import java.util.Random;


public class ScaleInitializer implements ParticleInitializer {

	private float mMaxScale;
	private float mMinScale;

	private boolean isExactlyScale = false;
	private float scaleSize = 1.0f;
	public ScaleInitializer(float minScale, float maxScale) {
		mMinScale = minScale;
		mMaxScale = maxScale;
	}

	public ScaleInitializer(float exactlySize) {
		isExactlyScale = true;
		scaleSize = exactlySize;
	}

	@Override
	public void initParticle(Particle p, Random r) {
		if (isExactlyScale){
			p.mScale = scaleSize;
		}
		else{
			float scale = r.nextFloat()*(mMaxScale-mMinScale) + mMinScale;
			p.mScale = scale;
		}

	}

}
