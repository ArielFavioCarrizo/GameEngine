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

package com.esferixis.gameengine.physics.plane.kinematics.collisionDetection;

import com.esferixis.gameengine.physics.plane.kinematics.PlaneKinematicBody;
import com.esferixis.gameengine.physics.plane.kinematics.mapper.PlaneKinematicMapper;
import com.esferixis.math.intervalarithmetic.FloatClosedInterval;

/**
 * @author ariel
 *
 */
public final class DistancePairCollisionDetector extends PairCollisionDetector {
	/**
	 * 
	 */
	private static final long serialVersionUID = 5267808946363848077L;
	
	/**
	 * @post Crea un detector de colisiones
	 */
	public DistancePairCollisionDetector() {
		
	}
	
	/**
	 * @pre La cadena no puede ser nula
	 * @post Parsea la cadena especificada
	 */
	public static DistancePairCollisionDetector parse(String string) {
		return new DistancePairCollisionDetector();
	}

	/* (non-Javadoc)
	 * @see com.arielcarrizo.gameengine.physics.plane.kinematics.collisionDetection.PairCollisionDetector#testCollision_internal(com.arielcarrizo.math.intervalarithmetic.FloatClosedInterval, com.arielcarrizo.math.intervalarithmetic.FloatClosedInterval, boolean, com.arielcarrizo.gameengine.physics.plane.kinematics.PlaneKinematicBody, com.arielcarrizo.gameengine.physics.plane.kinematics.PlaneKinematicBody)
	 */
	@Override
	protected Float testCollision_internal(FloatClosedInterval timeInterval,
			FloatClosedInterval collisionDistanceInterval, boolean inclusiveResult,
			PlaneKinematicBody<? extends PlaneKinematicMapper> body1,
			PlaneKinematicBody<? extends PlaneKinematicMapper> body2) {
		
		if ( ( timeInterval != null ) && ( body1 != null ) && ( body2 != null ) && ( collisionDistanceInterval != null ) ) {
			Float result = null;
			boolean terminate = false;
			
			final FloatClosedInterval initialTimeInterval = timeInterval;
			
			float candidateTimeDelta = timeInterval.length();
			
			boolean firstCycle = true;
			
			float firstDistance = 0.0f;
			
			do {
				final float eachDistance = body1.instantShape(timeInterval.getMin()).perimetralDistance(body2.instantShape(timeInterval.getMin()));
				
				if ( firstCycle ) {
					firstDistance = eachDistance;
					firstCycle = false;
				}
				
				if ( collisionDistanceInterval.contains(eachDistance) ) {
					result = timeInterval.getMin();
					terminate = true;
				}
				else {
					if ( timeInterval.length() > 0 ) {
					
						final float maxAcceptedTraveledDistance;
						if ( eachDistance > collisionDistanceInterval.getMax() ) {
							maxAcceptedTraveledDistance = eachDistance - collisionDistanceInterval.getMin();
						}
						else {
							maxAcceptedTraveledDistance = collisionDistanceInterval.getMax() - eachDistance;
						}
					
						boolean foundedAcceptableTimeDelta = false;
						boolean firstTime = true;
						
						FloatClosedInterval advanceInterval;
						
						do {
							advanceInterval = new FloatClosedInterval(timeInterval.getMin(), timeInterval.getMin()+candidateTimeDelta);
							final float maxTraveledDistance = body1.getMaxDistanceTraveled(advanceInterval) + body2.getMaxDistanceTraveled(advanceInterval);
							
							if ( maxTraveledDistance < maxAcceptedTraveledDistance ) {
								foundedAcceptableTimeDelta = true;
							}
							else {
								candidateTimeDelta /= 2.0f;
								firstTime = false;
							}
						} while ( !foundedAcceptableTimeDelta );
						
						timeInterval = new FloatClosedInterval(advanceInterval.getMax(), timeInterval.getMax());
						
						if ( firstTime ) {
							candidateTimeDelta *= 2.0f;
						}
						
						candidateTimeDelta = Math.min(candidateTimeDelta, timeInterval.length());
					}
					else {
						terminate = true;
					}
				}
			} while ( !terminate );
			
			if ( ( result != null ) && ( !inclusiveResult ) ) {
				if ( firstDistance > collisionDistanceInterval.getMax()  ) {
					candidateTimeDelta = result - initialTimeInterval.getMin();
					
					result = null;
					
					do {
						timeInterval = new FloatClosedInterval(initialTimeInterval.getMin(), initialTimeInterval.getMin() + candidateTimeDelta);
						final float maxTraveledDistance = body1.getMaxDistanceTraveled(timeInterval) + body2.getMaxDistanceTraveled(timeInterval);
						
						if ( firstDistance - maxTraveledDistance <= collisionDistanceInterval.getMax() ) {
							candidateTimeDelta /= 2.0f;
						}
						else {
							result = timeInterval.getMax();
						}
					} while ( result == null );
				}
			}
			
			return result;
		}
		else {
			throw new NullPointerException();
		}
	}
}
