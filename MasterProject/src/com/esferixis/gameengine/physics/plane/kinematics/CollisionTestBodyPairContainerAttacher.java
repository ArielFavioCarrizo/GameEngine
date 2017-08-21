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
package com.esferixis.gameengine.physics.plane.kinematics;

import java.io.Serializable;

import com.esferixis.gameengine.physics.plane.kinematics.collisionDetection.PairCollisionDetector;
import com.esferixis.gameengine.physics.time.TemporalEvent;
import com.esferixis.gameengine.physics.time.TemporalEventsManager;
import com.esferixis.math.ExtraMath;
import com.esferixis.math.intervalarithmetic.FloatClosedInterval;

/**
 * @author ariel
 *
 */
public final class CollisionTestBodyPairContainerAttacher implements Serializable {
	/**
	 * 
	 */
	private static final long serialVersionUID = 4614687582304144231L;
	
	private final CollisionTestBodyPairContainer bodyPairCollisionTestContainer;
	private TemporalEventsManager temporalEventsManager;
	private final PairCollisionDetector pairCollisionDetector;
	
	/**
	 * @post Crea el perfil con el el motor de eventos
	 * 		 temporales, el detector de colisiones, y el contenedor de pares de testeo de colisión especificados
	 */
	public CollisionTestBodyPairContainerAttacher(TemporalEventsManager temporalEventsManager, PairCollisionDetector pairCollisionDetector, CollisionTestBodyPairContainer bodyPairCollisionTestContainer) {
		if ( ( temporalEventsManager != null ) && ( pairCollisionDetector != null ) && ( bodyPairCollisionTestContainer != null ) ) {
			this.bodyPairCollisionTestContainer = bodyPairCollisionTestContainer;
			this.temporalEventsManager = temporalEventsManager;
			this.pairCollisionDetector = pairCollisionDetector;
			this.prepareNewEvent(this.temporalEventsManager.getCurrentTime());
		}
		else {
			throw new NullPointerException();
		}
	}
	
	/**
	 * @post Prepara un nuevo evento en el instante de tiempo
	 * 		 especificado
	 */
	private void prepareNewEvent(float time) {
		this.temporalEventsManager.addEvent(new TemporalEvent(time) {

			@Override
			protected void launch(TemporalEventsManager eventsManager) {
				if ( CollisionTestBodyPairContainerAttacher.this.temporalEventsManager != null) {
					CollisionTestBodyPairContainerAttacher.this.simulate();
				}
			}
			
		});
	}
	
	/**
	 * @post Realiza una simulación hasta el evento temporal
	 * 		 existente.
	 * 		 Creando el evento correspondiente.
	 */
	private void simulate() {
		class CollisionTester implements CollisionTestBodyPairTester {
			private final FloatClosedInterval timeInterval;
			
			private boolean onContext;
			private CollisionTestBodyPairResult collisionTestBodyPairResult;

			public CollisionTester(FloatClosedInterval timeInterval) {
				this.timeInterval = timeInterval;
				this.onContext = true;
				this.collisionTestBodyPairResult = null;
			}
			
			/* (non-Javadoc)
			 * @see com.arielcarrizo.gameengine.physics.plane.kinematics.KinematicsEngineCollisionTester#test(com.arielcarrizo.gameengine.physics.plane.kinematics.BodyPairCollisionTest)
			 */
			@Override
			public void test(CollisionTestBodyPair bodyPairCollisionTest) {
				if ( this.onContext ) {
					if ( bodyPairCollisionTest.getResponse() != null ) {
						final float minTime = ExtraMath.max(this.timeInterval.getMin(), bodyPairCollisionTest.getBodyPair().getElement1().getStartTime(),  bodyPairCollisionTest.getBodyPair().getElement2().getStartTime());
						
						if ( minTime <= this.timeInterval.getMax() ) {
							final FloatClosedInterval selectedTimeInterval = new FloatClosedInterval(minTime, this.timeInterval.getMax());
							
							final float distance = bodyPairCollisionTest.getBodyPair().getElement1().instantShape(selectedTimeInterval.getMin()).perimetralDistance(bodyPairCollisionTest.getBodyPair().getElement2().instantShape(selectedTimeInterval.getMin()));
							final FloatClosedInterval collisionDistanceInterval = CollisionTestBodyPairContainerAttacher.this.bodyPairCollisionTestContainer.getCollisionDistanceInterval();
							Float upperTime = null, intermediateTime = null, lowerTime = null;
							
							CollisionTestBodyPairResult collisionTestBodyPairResult = null;
							
							final float minUpperBoundaryLimit = collisionDistanceInterval.linearInterpolation(4.75f / 5.0f);
							
							final float maxIntermediateRegionLimit = collisionDistanceInterval.linearInterpolation(3.0f / 5.0f);
							final float minIntermediateRegionLimit = collisionDistanceInterval.linearInterpolation(2.0f / 5.0f);
							
							final float maxLowerBoundaryLimit = collisionDistanceInterval.linearInterpolation(0.25f / 5.0f);
							
							if ( bodyPairCollisionTest.getTestUpperBoundaryLimit() && ( distance >= minIntermediateRegionLimit ) && ( distance < minUpperBoundaryLimit ) ) {
								upperTime = CollisionTestBodyPairContainerAttacher.this.pairCollisionDetector.testCollision(selectedTimeInterval, new FloatClosedInterval(minUpperBoundaryLimit, collisionDistanceInterval.getMax()), false, bodyPairCollisionTest.getBodyPair().getElement1(), bodyPairCollisionTest.getBodyPair().getElement2());
							}
							
							if ( ( distance > maxIntermediateRegionLimit ) || ( distance < minIntermediateRegionLimit ) ) {
								intermediateTime = CollisionTestBodyPairContainerAttacher.this.pairCollisionDetector.testCollision(selectedTimeInterval, new FloatClosedInterval(minIntermediateRegionLimit, maxIntermediateRegionLimit), true, bodyPairCollisionTest.getBodyPair().getElement1(), bodyPairCollisionTest.getBodyPair().getElement2());
							}
							
							if ( bodyPairCollisionTest.getTestLowerBoundaryLimit() && ( distance <= maxIntermediateRegionLimit ) && ( distance > maxLowerBoundaryLimit ) ) {
								lowerTime = CollisionTestBodyPairContainerAttacher.this.pairCollisionDetector.testCollision(selectedTimeInterval, new FloatClosedInterval(collisionDistanceInterval.getMin(), maxLowerBoundaryLimit), false, bodyPairCollisionTest.getBodyPair().getElement1(), bodyPairCollisionTest.getBodyPair().getElement2());
							}
							
							if ( ( lowerTime != null ) || (intermediateTime != null) || ( upperTime != null ) ) {
								CollisionTestBodyPairResult[] results = new CollisionTestBodyPairResult[3];
								
								results[0] = ( ( upperTime != null ) ? new CollisionTestBodyPairResult(bodyPairCollisionTest, upperTime, CollisionTestBodyPairResult.Type.UPPER ) : null );
								results[1] = ( ( intermediateTime != null ) ? new CollisionTestBodyPairResult(bodyPairCollisionTest, intermediateTime, CollisionTestBodyPairResult.Type.INTERMEDIATE ) : null );
								results[2] = ( ( lowerTime != null ) ? new CollisionTestBodyPairResult(bodyPairCollisionTest, lowerTime, CollisionTestBodyPairResult.Type.LOWER ) : null );
								
								for ( CollisionTestBodyPairResult eachCollisionTestBodyPairResult : results ) {
									if ( eachCollisionTestBodyPairResult != null ) {
										if ( ( collisionTestBodyPairResult == null ) || ( eachCollisionTestBodyPairResult.getTime() < collisionTestBodyPairResult.getTime() ) ) {
											collisionTestBodyPairResult = eachCollisionTestBodyPairResult;
										}
									}
								}
							}
							
							if ( collisionTestBodyPairResult != null ) {
								if ( ( this.collisionTestBodyPairResult == null ) || ( collisionTestBodyPairResult.getTime() < this.collisionTestBodyPairResult.getTime() ) ) {
									this.collisionTestBodyPairResult = collisionTestBodyPairResult;
								}
							}
						}
					}
				}
				else {
					throw new IllegalStateException("Invalid context");
				}
			}
			
		}
		
		if ( this.temporalEventsManager.remainingEvents() ) {
			final float lastTime = this.temporalEventsManager.getCurrentTime();
			final float nextTime = this.temporalEventsManager.getNearestEventTime();
			
			if ( nextTime != lastTime ) {
				final FloatClosedInterval timeInterval = new FloatClosedInterval(lastTime, nextTime);
				
				final CollisionTester collisionTester = new CollisionTester(timeInterval);
				this.bodyPairCollisionTestContainer.getEmmiter().emitTests(collisionTester);
				collisionTester.onContext = false;
				
				if ( collisionTester.collisionTestBodyPairResult != null  ) {
					this.temporalEventsManager.addEvent(new TemporalEvent(collisionTester.collisionTestBodyPairResult.getTime()) {
	
						@Override
						protected void launch(TemporalEventsManager eventsManager) {
							final CollisionResponse collisionResponse = collisionTester.collisionTestBodyPairResult.getCollisionTestBodyPair().getResponse();
							final float time = collisionTester.collisionTestBodyPairResult.getTime();
							
							switch ( collisionTester.collisionTestBodyPairResult.getType() ) {
								case UPPER:
									collisionTester.collisionTestBodyPairResult.getCollisionTestBodyPair().setTestUpperBoundaryLimit(collisionResponse.notifyUpperBoundaryCollision(time));
									break;
								case INTERMEDIATE:
									collisionResponse.notifyIntermediateRegionCollision(time);
									break;
								case LOWER:
									collisionTester.collisionTestBodyPairResult.getCollisionTestBodyPair().setTestLowerBoundaryLimit(collisionResponse.notifyLowerBoundaryCollision(time));
									break;
							};
							
							CollisionTestBodyPairContainerAttacher.this.simulate();
						}
						
					});
				}
				else {
					this.temporalEventsManager.addEvent(new TemporalEvent(timeInterval.getMax()) {

						@Override
						protected void launch(TemporalEventsManager eventsManager) {
							CollisionTestBodyPairContainerAttacher.this.simulate();
						}
						
					});
				}
			}
			else {
				this.temporalEventsManager.addEvent(new TemporalEvent(nextTime) {

					@Override
					protected void launch(TemporalEventsManager eventsManager) {
						CollisionTestBodyPairContainerAttacher.this.simulate();
					}
					
				});
			}
		}
		else { 
			throw new RuntimeException("Fatal error: No events left");
		}
	}
	
	/**
	 * @post Realiza la desasociación
	 */
	public void detach() {
		CollisionTestBodyPairContainerAttacher.this.temporalEventsManager = null;
	}
}
