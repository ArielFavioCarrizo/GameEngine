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

package com.esferixis.gameengine.tests.physics.plane.kinematics.containers;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import com.esferixis.gameengine.physics.plane.kinematics.CollisionResponse;
import com.esferixis.gameengine.physics.plane.kinematics.CollisionTestBodyPairContainer;
import com.esferixis.gameengine.physics.plane.kinematics.CollisionTestBodyPairContainerAttacher;
import com.esferixis.gameengine.physics.plane.kinematics.PlaneKinematicBody;
import com.esferixis.gameengine.physics.plane.kinematics.StaticShapePlaneKinematicBody;
import com.esferixis.gameengine.physics.plane.kinematics.collisionDetection.CollisionDetectionConfig;
import com.esferixis.gameengine.physics.plane.kinematics.collisionDetection.DistancePairCollisionDetector;
import com.esferixis.gameengine.physics.plane.kinematics.collisionDetection.PairCollisionDetector;
import com.esferixis.gameengine.physics.plane.kinematics.containers.LinearBodyPairCollisionTestPairContainer;
import com.esferixis.gameengine.physics.plane.kinematics.mapper.PlaneKinematicMapper;
import com.esferixis.gameengine.physics.plane.kinematics.mapper.StaticAffinePlaneKinematicMapper;
import com.esferixis.gameengine.physics.plane.kinematics.mapper.translation.LinearTrajectory;
import com.esferixis.gameengine.physics.plane.kinematics.mapper.translation.TranslationPlaneKinematicMapper;
import com.esferixis.gameengine.platform.PlatformServiceManager;
import com.esferixis.gameengine.renderengine.backend.plane.shape.ShapeObject;
import com.esferixis.gameengine.renderengine.frontend.plane.kinematics.objects.KinematicShapeObject;
import com.esferixis.gameengine.renderengine.frontend.renderingFrame.RenderingFrame;
import com.esferixis.gameengine.renderengine.plane.shape.UniformColorDistribution;
import com.esferixis.gameengine.tests.GameEngineTestRunnable;
import com.esferixis.gameengine.tests.testscene.TestScene;
import com.esferixis.gameengine.tests.testscene.TestSceneRunner;
import com.esferixis.geometry.plane.Shape.NearestNormal;
import com.esferixis.geometry.plane.finite.Circle;
import com.esferixis.geometry.plane.finite.Circumference;
import com.esferixis.geometry.plane.finite.FiniteProportionalHolomorphicShape;
import com.esferixis.geometry.plane.finite.Point;
import com.esferixis.math.ExtraMath;
import com.esferixis.math.Matrix3f;
import com.esferixis.math.Vector2f;
import com.esferixis.math.Vector4f;
import com.esferixis.math.intervalarithmetic.FloatClosedInterval;
import com.esferixis.misc.collection.set.BinarySet;

/**
 * Prueba de animación de rotación de triángulo
 * 
 * @author Ariel Favio Carrizo
 *
 */
public final class KinematicContainerTest extends GameEngineTestRunnable {
	private static final ShapeObject resultShapeObject = new ShapeObject(new Point(Vector2f.ZERO), new UniformColorDistribution(new Vector4f(0.0f, 1.0f, 0.0f, 0.5f)));
	private static final ShapeObject pointsShapeObject = new ShapeObject(new Point(Vector2f.ZERO), new UniformColorDistribution(new Vector4f(0.0f, 0.0f, 1.0f, 0.5f)));
	
	public KinematicContainerTest() {
		super("Kinematic container test");
	}
	
	private static void processCollisionByNormal(final StaticShapePlaneKinematicBody<? extends FiniteProportionalHolomorphicShape<?>, TranslationPlaneKinematicMapper<LinearTrajectory>> body, final float time, final Vector2f normal) {
		if ( ( body != null) && ( normal != null ) ) {
			Vector2f velocity = body.getKinematicMapper().getTrajectory().getVelocity();
			
			if ( normal.scalarProjection(velocity) > 0.0f ) {
			
				velocity = velocity.sub(normal.vectorProjection(velocity).scale(2.0f));
			
				body.getKinematicMapper().setTrajectory(new LinearTrajectory(time, body.getKinematicMapper().getTrajectory().getInstantPosition(time), velocity));
				
			}
		}
	}
	
	/**
	 * @post Devuelve la velocidad del cuerpo
	 */
	private static Vector2f getVelocity(final StaticShapePlaneKinematicBody<? extends FiniteProportionalHolomorphicShape<?>, TranslationPlaneKinematicMapper<LinearTrajectory>> body) {
		return body.getKinematicMapper().getTrajectory().getVelocity();
	}
	
	/**
	 * @post Especifica la velocidad del cuerpo en el instante de tiempo especificado
	 */
	private static void setVelocity(final StaticShapePlaneKinematicBody<? extends FiniteProportionalHolomorphicShape<?>, TranslationPlaneKinematicMapper<LinearTrajectory>> body, float time, Vector2f velocity) {
		body.getKinematicMapper().setTrajectory(new LinearTrajectory(time, body.getKinematicMapper().getTrajectory().getInstantPosition(time), velocity));
	}
	
	/**
	 * @post Ejecuta una instancia del juego con el administrador
	 * 		 de servicio de plataforma especificado
	 */
	public void run(PlatformServiceManager platformServiceManager) {
		final int quantityOfCirclesX = 3;
		final int quantityOfCirclesY = 3;
		final float circleRadius = 0.1f;
		final float speed = 0.4f;
		
		final Random rng = new Random(3343);
		
		final FloatClosedInterval collisionDistanceInterval = new FloatClosedInterval(0.0001f, 0.01f);
		
		final PairCollisionDetector pairCollisionDetector = new DistancePairCollisionDetector();
		
		TestSceneRunner.run(platformServiceManager, new TestScene("Balls colision test") {

			@Override
			protected Instance create_checked(InstanceData instanceData) {
				return new Instance(instanceData) {

					private CollisionTestBodyPairContainerAttacher collisionTestBodyPairContainerAttacher;
					
					@Override
					protected void load() {
						final StaticShapePlaneKinematicBody<FiniteProportionalHolomorphicShape<?>, PlaneKinematicMapper> perimeterBody = new StaticShapePlaneKinematicBody<FiniteProportionalHolomorphicShape<?>, PlaneKinematicMapper>(RenderingFrame.SHAPE.getPerimeter(), new StaticAffinePlaneKinematicMapper(Matrix3f.IDENTITY));
						
						final List<StaticShapePlaneKinematicBody<Circle, TranslationPlaneKinematicMapper<LinearTrajectory>>> circleBodies = new ArrayList<StaticShapePlaneKinematicBody<Circle, TranslationPlaneKinematicMapper<LinearTrajectory>>>();
						
						final Circle circleShape = new Circle(new Circumference(Vector2f.ZERO, circleRadius));
						
						final CollisionTestBodyPairContainer collisionTestBodyPairContainer = new LinearBodyPairCollisionTestPairContainer(collisionDistanceInterval);
						
						for ( int j = 0 ; j<quantityOfCirclesY; j++ ) {
							for ( int i = 0 ; i<quantityOfCirclesX; i++ ) {
								final Vector2f initialPosition = new Vector2f( (2.0f - circleRadius) * (i+1) / (float) (quantityOfCirclesX+1) - 1.0f, (2.0f - circleRadius) * (j+1) / (float) (quantityOfCirclesY+1) - 1.0f);
								final Vector2f initialSpeed = Vector2f.unitPolar(ExtraMath.doublePI * rng.nextFloat()).scale(speed);
								
								final StaticShapePlaneKinematicBody<Circle, TranslationPlaneKinematicMapper<LinearTrajectory>> eachCircleBody = new StaticShapePlaneKinematicBody<Circle, TranslationPlaneKinematicMapper<LinearTrajectory>>(circleShape, new TranslationPlaneKinematicMapper<LinearTrajectory>(new LinearTrajectory(0.0f, initialPosition, initialSpeed)));
								
								circleBodies.add(eachCircleBody);
								
								this.kinematicRenderPlaneObjectContainer.addObject(new KinematicShapeObject(eachCircleBody.getShape(), eachCircleBody.getKinematicMapper(), new UniformColorDistribution(new Vector4f(1.0f, 1.0f, 1.0f, 1.0f))));
							}
						}
						
						int i = 0;
						for ( final StaticShapePlaneKinematicBody<Circle, TranslationPlaneKinematicMapper<LinearTrajectory>> eachCircleBody : circleBodies ) {
							final int circleIndex = i++;
							
							collisionTestBodyPairContainer.setCollisionResponse(new BinarySet<PlaneKinematicBody<? extends PlaneKinematicMapper>>(eachCircleBody, perimeterBody), new CollisionResponse() {
								/**
								 * 
								 */
								private static final long serialVersionUID = 8428262934952164962L;

								@Override
								public boolean notifyUpperBoundaryCollision(float time) {
									return false;
								}

								@Override
								public boolean notifyLowerBoundaryCollision(float time) {
									final NearestNormal normal = eachCircleBody.instantShape(time).perimetralDilate(perimeterBody.instantShape(time).opposite()).nearestNormalToOrigin();
									
									processCollisionByNormal(eachCircleBody, time, normal.getValue());
									
									return true;
								}

								@Override
								public void notifyIntermediateRegionCollision(float time) {
									// TODO Auto-generated method stub
									
								}
								
							});
							
							for ( final StaticShapePlaneKinematicBody<Circle, TranslationPlaneKinematicMapper<LinearTrajectory>> eachCircleBody2 : circleBodies ) {
								if ( eachCircleBody2 != eachCircleBody ) {
									collisionTestBodyPairContainer.setCollisionResponse(new BinarySet<PlaneKinematicBody<? extends PlaneKinematicMapper>>(eachCircleBody, eachCircleBody2), new CollisionResponse() {
										/**
										 * 
										 */
										private static final long serialVersionUID = 8428262934952164962L;

										@Override
										public boolean notifyUpperBoundaryCollision(float time) {
											return false;
										}

										@Override
										public boolean notifyLowerBoundaryCollision(float time) {
											final NearestNormal normal = eachCircleBody.instantShape(time).perimetralDilate(eachCircleBody2.instantShape(time).opposite()).nearestNormalToOrigin();
											
											Vector2f velocity1 = getVelocity(eachCircleBody);
											Vector2f velocity2 = getVelocity(eachCircleBody2);
											
											if ( normal.getValue().scalarProjection(velocity1.sub(velocity2)) > 0.0f ) {
											
												final Vector2f originalNormalVelocity1 = normal.getValue().vectorProjection(velocity1);
												final Vector2f originalNormalVelocity2 = normal.getValue().vectorProjection(velocity2);
												
												velocity1 = velocity1.sub(originalNormalVelocity1).add(originalNormalVelocity2);
												velocity2 = velocity2.sub(originalNormalVelocity2).add(originalNormalVelocity1);
													
												setVelocity(eachCircleBody, time, velocity1);
												setVelocity(eachCircleBody2, time, velocity2);
												
											}
											
											return true;
										}

										@Override
										public void notifyIntermediateRegionCollision(float time) {
											// TODO Auto-generated method stub
											
										}
										
									});
								}
							}
						}
						
						this.collisionTestBodyPairContainerAttacher = new CollisionTestBodyPairContainerAttacher(this.temporalEventsManager, pairCollisionDetector, collisionTestBodyPairContainer);
						
						this.lockedCamera = true;
					}

					@Override
					protected void destroy_checked() {
						this.collisionTestBodyPairContainerAttacher.detach();
					}
					
				};
			}
			
		}, false);
	}
}
