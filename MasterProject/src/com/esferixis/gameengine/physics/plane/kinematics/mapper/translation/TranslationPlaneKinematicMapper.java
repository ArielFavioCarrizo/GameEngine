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

package com.esferixis.gameengine.physics.plane.kinematics.mapper.translation;

import com.esferixis.gameengine.physics.plane.kinematics.mapper.PlaneKinematicMapper;
import com.esferixis.gameengine.physics.plane.kinematics.mapper.StaticAffinePlaneKinematicMapper;
import com.esferixis.gameengine.physics.plane.statics.ProportionalAffineMapper2d;
import com.esferixis.gameengine.physics.plane.statics.ProportionalPlaneMapper;
import com.esferixis.geometry.plane.finite.FiniteProportionalHolomorphicShape;
import com.esferixis.math.Matrix3f;
import com.esferixis.math.ProportionalMatrix3f;
import com.esferixis.math.intervalarithmetic.FloatClosedInterval;
import com.esferixis.misc.ElementCallback;
import com.esferixis.misc.observer.ObserverManager;

/**
 * @author ariel
 *
 */
public final class TranslationPlaneKinematicMapper<T extends Trajectory> extends PlaneKinematicMapper {
	/**
	 * 
	 */
	private static final long serialVersionUID = -712058495780771095L;

	final ObserverManager<TranslationPlaneKinematicMapper<T>, TranslationPlaneKinematicMapperObserver<T>> observerManager = new ObserverManager<TranslationPlaneKinematicMapper<T>, TranslationPlaneKinematicMapperObserver<T>>(this, (Class) TranslationPlaneKinematicMapperObserver.class);
	
	private T trajectory;
	
	/**
	 * @pre La trayectoria no puede ser nula
	 * @post Crea el mapeador con la trayectoria especificada
	 */
	public TranslationPlaneKinematicMapper(T trajectory) {
		this.setTrajectory(trajectory);
	}
	
	/**
	 * @post Devuelve la trayectoria
	 */
	public T getTrajectory() {
		return this.trajectory;
	}
	
	/**
	 * @pre La trayectoria no puede ser nula
	 * @post Especifica la trayectoria
	 */
	public void setTrajectory(final T trajectory) {
		if ( trajectory != null ) {
			this.observerManager.notifyObservers(new ElementCallback<TranslationPlaneKinematicMapperObserver<T>>() {

				@Override
				public void run(TranslationPlaneKinematicMapperObserver<T> observer) {
					observer.notifyTrajectoryChange(trajectory);
				}
				
			});
			this.trajectory = trajectory;
		}
		else {
			throw new NullPointerException();
		}
	}
	
	/* (non-Javadoc)
	 * @see com.esferixis.gameengine.physics.plane.kinematics.KinematicMapper#instantPlaneMapper(float)
	 */
	@Override
	public ProportionalPlaneMapper instantPlaneMapper(float time) {
		return new ProportionalAffineMapper2d(Matrix3f.IDENTITY.translate(this.getTrajectory().getInstantPosition(time)));
	}
	
	/* (non-Javadoc)
	 * @see com.esferixis.gameengine.physics.plane.kinematics.KinematicMapper#boundingRegion_internal(com.esferixis.geometry.plane.ProportionalHolomorphicShape, com.esferixis.math.intervalarithmetic.FloatClosedInterval)
	 */
	@Override
	protected FiniteProportionalHolomorphicShape<?> shapeWithBoundingPerimeter_internal(FiniteProportionalHolomorphicShape<?> originalShape,
			FloatClosedInterval timeInterval) {
		return this.getTrajectory().shapeWithBoundingPerimeter(timeInterval).perimetralDilate(originalShape);
	}

	/* (non-Javadoc)
	 * @see com.arielcarrizo.gameengine.physics.plane.kinematics.KinematicMapper#boundingRegion_internal(com.arielcarrizo.geometry.plane.ProportionalHolomorphicShape, com.arielcarrizo.math.intervalarithmetic.FloatClosedInterval)
	 */
	@Override
	protected FiniteProportionalHolomorphicShape<?> boundingRegion_internal(FiniteProportionalHolomorphicShape<?> originalShape,
			FloatClosedInterval timeInterval) {
		return this.getTrajectory().boundingRegion(timeInterval).getBoundingAffineHolomorphicShape().dilate(originalShape.getBoundingAffineHolomorphicShape());
	}
	
	/**
	 * @pre La cadena no puede ser nula
	 * @post Parsea un mapeador con la cadena especificada, que consiste
	 * 		 en una matriz 3x3 proporcional
	 */
	public static TranslationPlaneKinematicMapper<Trajectory> parse(String string) {
		if ( string != null ) {
			return new TranslationPlaneKinematicMapper<Trajectory>(Trajectory.parse(string));
		}
		else {
			throw new NullPointerException();
		}
	}
	
	/**
	 * @post Devuelve una representación en cadena de carácteres
	 */
	@Override
	public String toString() {
		return "TranslationKinematicMapper@" + Integer.toHexString(System.identityHashCode(this)) + "(" + this.trajectory + ")";
	}

	/* (non-Javadoc)
	 * @see com.arielcarrizo.gameengine.physics.plane.kinematics.KinematicMapper#getMaxDistanceTraveled(com.arielcarrizo.geometry.plane.finite.FiniteProportionalHolomorphicShape, com.arielcarrizo.math.intervalarithmetic.FloatClosedInterval)
	 */
	@Override
	public float getMaxDistanceTraveled_internal(FiniteProportionalHolomorphicShape<?> originalShape,
			FloatClosedInterval timeInterval) {
		return this.trajectory.getMaxDistanceTraveled(timeInterval);
	}

	/* (non-Javadoc)
	 * @see com.arielcarrizo.gameengine.physics.plane.kinematics.kinematicMapper.PlaneKinematicMapper#getMaxDistanceTraveledByBoundedTransformedShape_internal(com.arielcarrizo.geometry.plane.finite.FiniteProportionalHolomorphicShape, com.arielcarrizo.math.intervalarithmetic.FloatClosedInterval, float)
	 */
	@Override
	protected float getMaxDistanceTraveledWithBoundedTransformation_internal(
			FiniteProportionalHolomorphicShape<?> originalShape, FloatClosedInterval timeInterval,
			float maxDistanceTraveled_originalTransformation) {
		return this.getMaxDistanceTraveled(originalShape, timeInterval) + maxDistanceTraveled_originalTransformation;
	}

	/* (non-Javadoc)
	 * @see com.arielcarrizo.gameengine.physics.plane.kinematics.kinematicMapper.PlaneKinematicMapper#accept(com.arielcarrizo.gameengine.physics.plane.kinematics.kinematicMapper.PlaneKinematicMapper.Visitor)
	 */
	@Override
	public <V, E extends Throwable> V accept(Visitor<V, E> visitor) throws E {
		return visitor.visit(this);
	}

	/* (non-Javadoc)
	 * @see com.arielcarrizo.gameengine.physics.plane.kinematics.kinematicMapper.PlaneKinematicMapper#getStartTime()
	 */
	@Override
	public float getStartTime() {
		return this.trajectory.getStartTime();
	}
}

