package com.honeywell.hch.airtouchv3.lib.weather.initializers.initializers;

import com.honeywell.hch.airtouchv3.lib.weather.initializers.Particle;

import java.util.Random;


public interface ParticleInitializer {

	void initParticle(Particle p, Random r);

}
