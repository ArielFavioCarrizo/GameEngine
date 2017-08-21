/**
 * Copyright (c) 2017 Ariel Favio Carrizo
 * All rights reserved.
 *
 * Redistribution and use in source and binary forms, with or without
 * modification, are permitted provided that the following conditions are
 * met:
 *
 * * Redistributions of source code must retain the above copyright
 *   notice, this list of conditions and the following disclaimer.
 *
 * * Redistributions in binary form must reproduce the above copyright
 *   notice, this list of conditions and the following disclaimer in the
 *   documentation and/or other materials provided with the distribution.
 *
 * * Neither the name of 'esferixis' nor the names of its contributors
 *   may be used to endorse or promote products derived from this software
 *   without specific prior written permission.
 *
 * THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS
 * "AS IS" AND ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED
 * TO, THE IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR
 * PURPOSE ARE DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT OWNER OR
 * CONTRIBUTORS BE LIABLE FOR ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL,
 * EXEMPLARY, OR CONSEQUENTIAL DAMAGES (INCLUDING, BUT NOT LIMITED TO,
 * PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES; LOSS OF USE, DATA, OR
 * PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND ON ANY THEORY OF
 * LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT (INCLUDING
 * NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE OF THIS
 * SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
 */

package com.esferixis.gameengine.physics.plane.kinematics.mapper;

import com.esferixis.gameengine.physics.plane.statics.ProportionalPlaneMapper;
import com.esferixis.geometry.plane.finite.FiniteProportionalHolomorphicShape;
import com.esferixis.math.intervalarithmetic.FloatClosedInterval;
import com.esferixis.misc.ElementCallback;
import com.esferixis.misc.observer.ObserverManager;

/**
 * Mapeador cinemático espejo
 * 
 * Sirve para poder manipular de forma transparente la transformación
 * grupal
 * 
 * @author ariel
 *
 */
public final class MirrorPlaneKinematicMapper<M extends PlaneKinematicMapper> extends PlaneKinematicMapper {
	/**
	 * 
	 */
	private static final long serialVersionUID = -8962089884129714487L;
	
	final ObserverManager<MirrorPlaneKinematicMapper<? extends PlaneKinematicMapper>, MirrorPlaneKinematicMapperObserver> observerManager = new ObserverManager<MirrorPlaneKinematicMapper<? extends PlaneKinematicMapper>, MirrorPlaneKinematicMapperObserver>(this, MirrorPlaneKinematicMapperObserver.class);
	
	private PlaneKinematicMapper mirroredMapper;

	/**
	 * @pre El mapeador espejado no puede ser nulo
	 * @post Crea el mapeador espejo con el mapeador especificado
	 */
	public MirrorPlaneKinematicMapper(PlaneKinematicMapper mirroredMapper) {
		this.setMirroredMapper(mirroredMapper);
	}
	
	/**
	 * @pre El mapeador espejado no puede ser nulo
	 * @post Especifica el mapeador espejado
	 */
	public void setMirroredMapper(final PlaneKinematicMapper mirroredMapper) {
		if ( mirroredMapper != null ) {
			this.observerManager.notifyObservers(new ElementCallback<MirrorPlaneKinematicMapperObserver>() {

				@Override
				public void run(MirrorPlaneKinematicMapperObserver observer) {
					observer.notifyMirroredMapperChange(mirroredMapper);
				}
				
			});
			this.mirroredMapper = mirroredMapper;
		}
		else {
			throw new NullPointerException();
		}
	}
	
	/**
	 * @post Devuelve el mapeador a realizar espejo
	 */
	public PlaneKinematicMapper getMirroredMapper() {
		return this.mirroredMapper;
	}
	
	/* (non-Javadoc)
	 * @see com.arielcarrizo.gameengine.physics.plane.kinematics.kinematicMapper.PlaneKinematicMapper#instantPlaneMapper(float)
	 */
	@Override
	public ProportionalPlaneMapper instantPlaneMapper(float time) {
		return this.mirroredMapper.instantPlaneMapper(time);
	}

	/* (non-Javadoc)
	 * @see com.arielcarrizo.gameengine.physics.plane.kinematics.kinematicMapper.PlaneKinematicMapper#shapeWithBoundingPerimeter_internal(com.arielcarrizo.geometry.plane.finite.FiniteProportionalHolomorphicShape, com.arielcarrizo.math.intervalarithmetic.FloatClosedInterval)
	 */
	@Override
	protected FiniteProportionalHolomorphicShape<?> shapeWithBoundingPerimeter_internal(
			FiniteProportionalHolomorphicShape<?> originalShape, FloatClosedInterval timeInterval) {
		return this.mirroredMapper.shapeWithBoundingPerimeter(originalShape, timeInterval);
	}

	/* (non-Javadoc)
	 * @see com.arielcarrizo.gameengine.physics.plane.kinematics.kinematicMapper.PlaneKinematicMapper#boundingRegion_internal(com.arielcarrizo.geometry.plane.finite.FiniteProportionalHolomorphicShape, com.arielcarrizo.math.intervalarithmetic.FloatClosedInterval)
	 */
	@Override
	protected FiniteProportionalHolomorphicShape<?> boundingRegion_internal(
			FiniteProportionalHolomorphicShape<?> originalShape, FloatClosedInterval timeInterval) {
		return this.mirroredMapper.boundingRegion(originalShape, timeInterval);
	}

	/* (non-Javadoc)
	 * @see com.arielcarrizo.gameengine.physics.plane.kinematics.kinematicMapper.PlaneKinematicMapper#getMaxDistanceTraveled_internal(com.arielcarrizo.geometry.plane.finite.FiniteProportionalHolomorphicShape, com.arielcarrizo.math.intervalarithmetic.FloatClosedInterval)
	 */
	@Override
	protected float getMaxDistanceTraveled_internal(FiniteProportionalHolomorphicShape<?> originalShape,
			FloatClosedInterval timeInterval) {
		return this.mirroredMapper.getMaxDistanceTraveled(originalShape, timeInterval);
	}

	/* (non-Javadoc)
	 * @see com.arielcarrizo.gameengine.physics.plane.kinematics.kinematicMapper.PlaneKinematicMapper#getMaxDistanceTraveledWithBoundedTransformation_internal(com.arielcarrizo.geometry.plane.finite.FiniteProportionalHolomorphicShape, com.arielcarrizo.math.intervalarithmetic.FloatClosedInterval, float)
	 */
	@Override
	protected float getMaxDistanceTraveledWithBoundedTransformation_internal(
			FiniteProportionalHolomorphicShape<?> originalShape, FloatClosedInterval timeInterval,
			float maxDistanceTraveled_originalTransformation) {
		return this.getMaxDistanceTraveledWithBoundedTransformation(originalShape, timeInterval, maxDistanceTraveled_originalTransformation);
	}

	/* (non-Javadoc)
	 * @see com.arielcarrizo.gameengine.physics.plane.kinematics.kinematicMapper.PlaneKinematicMapper#accept(com.arielcarrizo.gameengine.physics.plane.kinematics.kinematicMapper.PlaneKinematicMapper.Visitor)
	 */
	@Override
	public <V, E extends Throwable> V accept(Visitor<V, E> visitor) throws E {
		return this.accept(visitor);
	}

	/* (non-Javadoc)
	 * @see com.arielcarrizo.gameengine.physics.plane.kinematics.kinematicMapper.PlaneKinematicMapper#getStartTime()
	 */
	@Override
	public float getStartTime() {
		return this.mirroredMapper.getStartTime();
	}
	
}
