package com.honeywell.hch.airtouchv3.lib.weather.initializers.modifiers;


import com.honeywell.hch.airtouchv3.lib.weather.initializers.Particle;

public interface ParticleModifier {

	/**
	 * modifies the specific value of a particle given the current miliseconds
	 * @param particle
	 * @param miliseconds
	 */
	void apply(Particle particle, long miliseconds);

}
