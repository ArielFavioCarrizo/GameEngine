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

package com.esferixis.gameengine.tests.physics.plane.dynamics.body.dynamicscontainertest;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import com.esferixis.gameengine.physics.plane.dynamics.body.PlaneDynamicsBodyContainer;
import com.esferixis.gameengine.physics.plane.dynamics.body.StaticShapePlaneDynamicsBody;
import com.esferixis.gameengine.physics.plane.dynamics.components.SymmetricComponent;
import com.esferixis.gameengine.physics.plane.dynamics.components.TransmiterComponent;
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
public final class DynamicsContainerTest extends GameEngineTestRunnable {
	public DynamicsContainerTest() {
		super("Dynamics body container test");
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
		
		final FloatClosedInterval collisionDistanceInterval = new FloatClosedInterval(0.00001f, 0.01f);
		
		final PairCollisionDetector pairCollisionDetector = new DistancePairCollisionDetector();
		
		TestSceneRunner.run(platformServiceManager, new TestScene("Balls colision test") {

			@Override
			protected Instance create_checked(InstanceData instanceData) {
				return new Instance(instanceData) {

					private CollisionTestBodyPairContainerAttacher collisionTestBodyPairContainerAttacher;
					
					@Override
					protected void load() {
						final CollisionTestBodyPairContainer collisionTestBodyPairContainer = new LinearBodyPairCollisionTestPairContainer(collisionDistanceInterval);
						
						final PlaneDynamicsBodyContainer bodyContainer = new PlaneDynamicsBodyContainer(collisionTestBodyPairContainer);
						
						final StaticShapePlaneDynamicsBody<FiniteProportionalHolomorphicShape<?>, PlaneKinematicMapper> perimeterBody = new StaticShapePlaneDynamicsBody<FiniteProportionalHolomorphicShape<?>, PlaneKinematicMapper>(RenderingFrame.SHAPE.getPerimeter(), new StaticAffinePlaneKinematicMapper(Matrix3f.IDENTITY));
						perimeterBody.addTransmiterComponent(new InfiniteMassBodyCollisionTransmiterComponent(perimeterBody));
						
						bodyContainer.add(perimeterBody);
												
						final Circle circleShape = new Circle(new Circumference(Vector2f.ZERO, circleRadius));
						
						for ( int j = 0 ; j<quantityOfCirclesY; j++ ) {
							for ( int i = 0 ; i<quantityOfCirclesX; i++ ) {
								final Vector2f initialPosition = new Vector2f( (2.0f - circleRadius) * (i+1) / (float) (quantityOfCirclesX+1) - 1.0f, (2.0f - circleRadius) * (j+1) / (float) (quantityOfCirclesY+1) - 1.0f);
								final Vector2f initialSpeed = Vector2f.unitPolar(ExtraMath.doublePI * rng.nextFloat()).scale(speed);
								
								final StaticShapePlaneDynamicsBody<Circle, TranslationPlaneKinematicMapper<LinearTrajectory>> eachCircleBody = new StaticShapePlaneDynamicsBody<Circle, TranslationPlaneKinematicMapper<LinearTrajectory>>(circleShape, new TranslationPlaneKinematicMapper<LinearTrajectory>(new LinearTrajectory(0.0f, initialPosition, initialSpeed)));
								
								eachCircleBody.addReceiverComponent(new InfiniteMassBodyCollisionReceiverComponent(eachCircleBody));
								eachCircleBody.addSymmetricComponent(new CollisionBetweenCirclesSymmetricComponent(eachCircleBody));
								
								bodyContainer.add(eachCircleBody);
								
								this.kinematicRenderPlaneObjectContainer.addObject(new KinematicShapeObject(eachCircleBody.getShape(), eachCircleBody.getKinematicMapper(), new UniformColorDistribution(new Vector4f(1.0f, 1.0f, 1.0f, 1.0f))));
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
