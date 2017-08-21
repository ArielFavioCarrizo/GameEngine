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
package com.esferixis.gameengine.tests.physics.plane.kinematics.collisionDetection;

import java.util.Scanner;

import com.esferixis.gameengine.physics.plane.kinematics.StaticShapePlaneKinematicBody;
import com.esferixis.gameengine.physics.plane.kinematics.collisionDetection.PairCollisionDetector;
import com.esferixis.gameengine.physics.plane.kinematics.mapper.PlaneKinematicMapper;
import com.esferixis.gameengine.physics.plane.kinematics.mapper.StaticAffinePlaneKinematicMapper;
import com.esferixis.gameengine.physics.plane.kinematics.mapper.TransformedPlaneKinematicMapper;
import com.esferixis.gameengine.platform.PlatformServiceManager;
import com.esferixis.gameengine.platform.PlatformServiceManagerException;
import com.esferixis.gameengine.platform.display.ScreenConfig;
import com.esferixis.gameengine.platform.input.Button;
import com.esferixis.gameengine.platform.input.InputUnitObserver;
import com.esferixis.gameengine.platform.input.Keyboard;
import com.esferixis.gameengine.renderengine.backend.RenderEngineBackend;
import com.esferixis.gameengine.renderengine.backend.plane.PlaneObjectComponentRenderer;
import com.esferixis.gameengine.renderengine.backend.plane.PlaneRendererEmmiter;
import com.esferixis.gameengine.renderengine.backend.plane.shape.ShapeObject;
import com.esferixis.gameengine.renderengine.backend.renderingFrame.RenderingFrameEmmiter;
import com.esferixis.gameengine.renderengine.backend.renderingFrame.RenderingFrameRenderer;
import com.esferixis.gameengine.renderengine.plane.shape.UniformColorDistribution;
import com.esferixis.gameengine.tests.EntityInput;
import com.esferixis.gameengine.tests.GameEngineTestRunnable;
import com.esferixis.gameengine.tests.geometry.plane.TransformTestProfile;
import com.esferixis.gameengine.tests.physics.plane.kinematics.TimeIntervalProfile;
import com.esferixis.geometry.plane.ShapeTest;
import com.esferixis.geometry.plane.finite.FiniteProportionalHolomorphicShape;
import com.esferixis.geometry.plane.finite.FiniteProportionalHolomorphicShapeGroup;
import com.esferixis.geometry.plane.finite.Point;
import com.esferixis.math.Vector2f;
import com.esferixis.math.Vector4f;
import com.esferixis.math.intervalarithmetic.FloatClosedInterval;
import com.esferixis.misc.observer.Observer;
import com.esferixis.misc.reference.DynamicReference;
import com.esferixis.misc.strings.parser.ParseException;

/**
 * @author ariel
 *
 */
public final class PlaneKinematicBodyPairCollisionDetectionTest extends GameEngineTestRunnable {
	private static final ShapeObject shape1 = new ShapeObject(new Point(Vector2f.ZERO), new UniformColorDistribution(new Vector4f(1.0f, 0.0f, 0.0f, 0.5f)));
	private static final ShapeObject shape2 = new ShapeObject(new Point(Vector2f.ZERO), new UniformColorDistribution(new Vector4f(0.0f, 1.0f, 0.0f, 0.5f)));
	
	private static final ShapeObject boundingPerimetersShapeObject = new ShapeObject(new Point(Vector2f.ZERO), new UniformColorDistribution(new Vector4f(0.0f, 0.0f, 1.0f, 0.5f)));
	
	private static final ShapeObject collisionShapeObject1 = new ShapeObject(new Point(Vector2f.ZERO), new UniformColorDistribution(new Vector4f(1.0f, 1.0f, 0.0f, 0.75f)));
	private static final ShapeObject collisionShapeObject2 = new ShapeObject(new Point(Vector2f.ZERO), new UniformColorDistribution(new Vector4f(1.0f, 1.0f, 1.0f, 0.75f)));
	
	/**
	 * @param title
	 */
	public PlaneKinematicBodyPairCollisionDetectionTest() {
		super("Kinematic body pair collision detection test");
	}
	
	/* (non-Javadoc)
	 * @see com.esferixis.gameengine.tests.GameEngineTestRunnable#run(com.esferixis.gameengine.platform.PlatformServiceManager)
	 */
	@Override
	public void run(PlatformServiceManager platformServiceManager) {
		ScreenConfig screenConfig;
		
		final PairCollisionDetector pairCollisionDetector = new EntityInput<PairCollisionDetector>("Pair collision detector") {
			
			@Override
			protected PairCollisionDetector parse(String string) throws ParseException {
				return PairCollisionDetector.parse(string);
			}
			
		}.enter(new Scanner(System.in));
		
		final FloatClosedInterval collisionDistanceInterval = new EntityInput<FloatClosedInterval>("Collision distance interval") {
			
			@Override
			protected FloatClosedInterval parse(String string) throws ParseException {
				return FloatClosedInterval.parse(string);
			}
			
		}.enter(new Scanner(System.in));
		
		if ( pairCollisionDetector != null ) {
			final FiniteProportionalHolomorphicShape<?> initialShape1 = ShapeTest.enterShape(new Scanner(System.in), "Shape 1", FiniteProportionalHolomorphicShape.class);
			
			if ( initialShape1 != null ) {
			
				final PlaneKinematicMapper kinematicMapper1 = new EntityInput<PlaneKinematicMapper>("Kinematic mapper 1") {
		
					@Override
					protected PlaneKinematicMapper parse(String string) throws ParseException {
						return PlaneKinematicMapper.parse(string);
					}
					
				}.enter(new Scanner(System.in));
				
				if ( kinematicMapper1 != null ) {
				
					final FiniteProportionalHolomorphicShape<?> initialShape2 = ShapeTest.enterShape(new Scanner(System.in), "Shape 2", FiniteProportionalHolomorphicShape.class);
					
					if ( initialShape2 != null ) {
						final PlaneKinematicMapper kinematicMapper2 = new EntityInput<PlaneKinematicMapper>("Kinematic mapper 2") {
							
							@Override
							protected PlaneKinematicMapper parse(String string) throws ParseException {
								return PlaneKinematicMapper.parse(string);
							}
							
						}.enter(new Scanner(System.in));
					
						if ( kinematicMapper2 != null ) {
							final FloatClosedInterval initialTimeInterval = new EntityInput<FloatClosedInterval>("Time interval") {

								@Override
								protected FloatClosedInterval parse(String string) throws ParseException {
									return FloatClosedInterval.parse(string);
								}
								
							}.enter(new Scanner(System.in));
							
							if ( initialTimeInterval != null ) {
								platformServiceManager.getDisplayManager().setWindowTitle(this.title);
								try {
									screenConfig = platformServiceManager.getDisplayManager().createWindowedScreenConfig(640, 480);
								} catch (PlatformServiceManagerException e) {
									throw new RuntimeException(e);
								}
								
								try {
									platformServiceManager.getDisplayManager().setScreenConfig(screenConfig);
								} catch (PlatformServiceManagerException e) {
									throw new RuntimeException(e);
								}
								
								final TimeIntervalProfile timeIntervalProfile = new TimeIntervalProfile(platformServiceManager, initialTimeInterval);
								
								timeIntervalProfile.attachObservers();
								
								final TransformTestProfile transformTestProfile1 = TransformTestProfile.createForFirstShape(platformServiceManager);
								final TransformTestProfile transformTestProfile2 = TransformTestProfile.createForSecondShape(platformServiceManager);
								
								transformTestProfile1.attachObservers();
								transformTestProfile2.attachObservers();
								
								final Button animateButton = platformServiceManager.getInputManager().getKeyboard().getMapping().get(Keyboard.Key.KEY_Q);
								final DynamicReference<CollisionAnimation> animation = new DynamicReference<CollisionAnimation>();
								
								final InputUnitObserver<Boolean> animateButtonObserver = new InputUnitObserver<Boolean>(Observer.Type.STRONG) {

									@Override
									protected void notifyStateChange(Boolean newPosition) {
										if ( newPosition ) {
											if ( animation.get() == null ) {
											
												final StaticShapePlaneKinematicBody<FiniteProportionalHolomorphicShape<?>, PlaneKinematicMapper> body1 = new StaticShapePlaneKinematicBody<FiniteProportionalHolomorphicShape<?>, PlaneKinematicMapper>(initialShape1, new TransformedPlaneKinematicMapper<PlaneKinematicMapper, PlaneKinematicMapper>( kinematicMapper1, new StaticAffinePlaneKinematicMapper(transformTestProfile1.getTransformMatrix())));
												final StaticShapePlaneKinematicBody<FiniteProportionalHolomorphicShape<?>, PlaneKinematicMapper> body2 = new StaticShapePlaneKinematicBody<FiniteProportionalHolomorphicShape<?>, PlaneKinematicMapper>(initialShape2, new TransformedPlaneKinematicMapper<PlaneKinematicMapper, PlaneKinematicMapper>( kinematicMapper2, new StaticAffinePlaneKinematicMapper(transformTestProfile2.getTransformMatrix())));
												
												Float collisionTime = null;
												
												try {
													collisionTime = pairCollisionDetector.testCollision(timeIntervalProfile.getTimeInterval(), collisionDistanceInterval, true, body1, body2);
													
												} catch ( RuntimeException e ) {
													e.printStackTrace();
												}
											
												animation.set(new CollisionAnimation(timeIntervalProfile.getTimeInterval(), collisionTime));
											}
											else {
												animation.set(null);
											}
										}
									}
									
								};
								
								animateButtonObserver.attach(animateButton);
								
								final RenderEngineBackend renderEngineBackend = platformServiceManager.getDisplayManager().getRenderEngineBackend();
								
								final DynamicReference<Integer> nframe = new DynamicReference<Integer>(0);
								
								final PlaneRendererEmmiter planeRendererEmmiter = new PlaneRendererEmmiter() {
						
									@Override
									protected void render_internal(PlaneObjectComponentRenderer objectRenderer) {
										Float time = null;
										
										final StaticShapePlaneKinematicBody<FiniteProportionalHolomorphicShape<?>, PlaneKinematicMapper> body1 = new StaticShapePlaneKinematicBody<FiniteProportionalHolomorphicShape<?>, PlaneKinematicMapper>(initialShape1, new TransformedPlaneKinematicMapper<PlaneKinematicMapper, PlaneKinematicMapper>( kinematicMapper1, new StaticAffinePlaneKinematicMapper(transformTestProfile1.getTransformMatrix())));
										final StaticShapePlaneKinematicBody<FiniteProportionalHolomorphicShape<?>, PlaneKinematicMapper> body2 = new StaticShapePlaneKinematicBody<FiniteProportionalHolomorphicShape<?>, PlaneKinematicMapper>(initialShape2, new TransformedPlaneKinematicMapper<PlaneKinematicMapper, PlaneKinematicMapper>( kinematicMapper2, new StaticAffinePlaneKinematicMapper(transformTestProfile2.getTransformMatrix())));
										
										if ( animation.get() == null ) {
										
											final FloatClosedInterval timeInterval = timeIntervalProfile.getTimeInterval();
											
											shape1.setShape(body1.instantShape(0.0f));
											objectRenderer.render(shape1);
											
											shape2.setShape(body2.instantShape(0.0f));
											objectRenderer.render(shape2);
											
											try {
												boundingPerimetersShapeObject.setShape(new FiniteProportionalHolomorphicShapeGroup<FiniteProportionalHolomorphicShape<?>>(
													body1.getKinematicMapper().shapeWithBoundingPerimeter(body1.getShape(), timeInterval),
													body2.getKinematicMapper().shapeWithBoundingPerimeter(body2.getShape(), timeInterval)
												));
											}
											catch ( RuntimeException e ) {
												e.printStackTrace();
											}
											
											objectRenderer.render(boundingPerimetersShapeObject);
											
											try {
												time = pairCollisionDetector.testCollision(timeIntervalProfile.getTimeInterval(), collisionDistanceInterval, true, body1, body2);
											} catch ( RuntimeException e ) {
												e.printStackTrace();
											}
										}
										else {
											time = animation.get().getTime();
										}
										
										if ( time != null ) {
											collisionShapeObject1.setShape(body1.instantShape(time));
											objectRenderer.render(collisionShapeObject1);
											
											collisionShapeObject2.setShape(body2.instantShape(time));
											objectRenderer.render(collisionShapeObject2);
										}
									}
									
								};
								
								long refNanoTime = System.nanoTime();
								
								while ( !platformServiceManager.isCloseRequested() ) {
									final float t = (float) ((System.nanoTime() - refNanoTime) / 1000000000.0d);
									
									if ( animation.get() != null ) {
										animation.get().setSystemTime(t);
									}
									else {
										timeIntervalProfile.update();
										
										transformTestProfile1.update();
										transformTestProfile2.update();
									}
									
									renderEngineBackend.getScreenRenderingFrame().render(new RenderingFrameEmmiter(){
						
										@Override
										protected void render_internal(RenderingFrameRenderer renderer) {
											renderer.clear(new Vector4f(0.0f, 0.0f, 0.25f, 1.0f));
											renderer.render(planeRendererEmmiter);
										}
										
									});
									
									nframe.set(nframe.get()+1);
									
									platformServiceManager.newFrame();
								}
								
								animateButtonObserver.detach();
								
								transformTestProfile1.detachObservers();
								transformTestProfile2.detachObservers();
								
								timeIntervalProfile.detachObservers();
							}
						}
					}
				}
			}
		}
	}
	
}
