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

package com.esferixis.gameengine.tests.physics.plane.kinematics.kinematicMapper;

import java.util.Scanner;

import com.esferixis.gameengine.physics.plane.kinematics.mapper.PlaneKinematicMapper;
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
import com.esferixis.gameengine.tests.geometry.plane.ShapeMisc;
import com.esferixis.gameengine.tests.geometry.plane.ShapeTestProfile;
import com.esferixis.gameengine.tests.physics.plane.kinematics.TimeIntervalProfile;
import com.esferixis.geometry.plane.ShapeTest;
import com.esferixis.geometry.plane.finite.FiniteProportionalHolomorphicShape;
import com.esferixis.geometry.plane.finite.Point;
import com.esferixis.math.Vector2f;
import com.esferixis.math.Vector4f;
import com.esferixis.math.intervalarithmetic.FloatClosedInterval;
import com.esferixis.misc.exception.NotImplementedException;
import com.esferixis.misc.observer.Observer;
import com.esferixis.misc.reference.DynamicReference;
import com.esferixis.misc.strings.parser.ParseException;

/**
 * Prueba de animación de rotación de triángulo
 * 
 * @author Ariel Favio Carrizo
 *
 */
public final class KinematicMapperTest extends GameEngineTestRunnable {
	private static final ShapeObject resultShapeObject = new ShapeObject(new Point(Vector2f.ZERO), new UniformColorDistribution(new Vector4f(0.0f, 1.0f, 0.0f, 0.5f)));
	private static final ShapeObject pointsShapeObject = new ShapeObject(new Point(Vector2f.ZERO), new UniformColorDistribution(new Vector4f(0.0f, 0.0f, 1.0f, 0.5f)));
	
	private enum Mode {
		SHAPEWITHBOUNDINGPERIMETER,
		BOUNDINGREGION
	}
	
	public KinematicMapperTest() {
		super("Kinematic mapper test");
	}
	
	/**
	 * @post Ejecuta una instancia del juego con el administrador
	 * 		 de servicio de plataforma especificado
	 */
	public void run(PlatformServiceManager platformServiceManager) {
		ScreenConfig screenConfig;
		
		final PlaneKinematicMapper kinematicMapper = new EntityInput<PlaneKinematicMapper>("Kinematic mapper") {

			@Override
			protected PlaneKinematicMapper parse(String string) throws ParseException {
				return PlaneKinematicMapper.parse(string);
			}
			
		}.enter(new Scanner(System.in));
		
		if ( kinematicMapper != null ) {
			
			final FiniteProportionalHolomorphicShape<?> initialShape = ShapeTest.enterShape(new Scanner(System.in), "Shape", FiniteProportionalHolomorphicShape.class);
			
			if ( initialShape != null ) {
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
					
					final DynamicReference<Mode> mode = new DynamicReference<Mode>(Mode.SHAPEWITHBOUNDINGPERIMETER);
					
					final InputUnitObserver<Boolean> shapeWithBoundingPerimeterButtonObserver = new InputUnitObserver<Boolean>(Observer.Type.STRONG) {

						@Override
						protected void notifyStateChange(Boolean newPosition) {
							if ( newPosition ) {
								mode.set(Mode.SHAPEWITHBOUNDINGPERIMETER);
							}
						}
						
					};
					
					final InputUnitObserver<Boolean> boundingRegionButtonObserver = new InputUnitObserver<Boolean>(Observer.Type.STRONG) {

						@Override
						protected void notifyStateChange(Boolean newPosition) {
							if ( newPosition ) {
								mode.set(Mode.BOUNDINGREGION);
							}
						}
						
					};
					
					final Button boundingRegionButton;
					final Button shapeWithBoundingPerimeterButton;
					
					{
						final Keyboard keyboard = platformServiceManager.getInputManager().getKeyboard();
						boundingRegionButton = keyboard.getMapping().get(Keyboard.Key.KEY_9);
						shapeWithBoundingPerimeterButton = keyboard.getMapping().get(Keyboard.Key.KEY_9);
					}
					
					boundingRegionButtonObserver.attach(boundingRegionButton);
					shapeWithBoundingPerimeterButtonObserver.attach(shapeWithBoundingPerimeterButton);
					
					timeIntervalProfile.attachObservers();
					
					final ShapeTestProfile<FiniteProportionalHolomorphicShape<?>> shapeTestProfile = ShapeTestProfile.createForFirstShape(platformServiceManager, initialShape);
					
					shapeTestProfile.attachObservers();
					
					final RenderEngineBackend renderEngineBackend = platformServiceManager.getDisplayManager().getRenderEngineBackend();
					
					final PlaneRendererEmmiter planeRendererEmmiter = new PlaneRendererEmmiter() {
			
						@Override
						protected void render_internal(PlaneObjectComponentRenderer objectRenderer) {
							shapeTestProfile.render(objectRenderer, new Vector4f(1.0f, 0.0f, 0.0f, 0.5f));
					
							final FloatClosedInterval timeInterval = timeIntervalProfile.getTimeInterval();
							
							try {
								switch ( mode.get() ) {
									case SHAPEWITHBOUNDINGPERIMETER:
										resultShapeObject.setShape(kinematicMapper.shapeWithBoundingPerimeter(shapeTestProfile.getActualShape(), timeInterval));
										break;
									case BOUNDINGREGION:
										resultShapeObject.setShape(kinematicMapper.boundingRegion(shapeTestProfile.getActualShape(), timeInterval));
										break;
								}
								objectRenderer.render(resultShapeObject);
							} catch ( NotImplementedException e ) {
								
							} catch ( RuntimeException e ) {
								e.printStackTrace();
							}
							
							for ( int i = 0 ; i<1000; i++ ) {
								FiniteProportionalHolomorphicShape<?> shape = new Point( ShapeMisc.getRandomPoint(shapeTestProfile.getActualShape()) );
								
								shape = kinematicMapper.instantPlaneMapper(timeInterval.linearInterpolation((float) Math.random())).transform(shape);
								
								pointsShapeObject.setShape(shape);
								
								objectRenderer.render(pointsShapeObject);
							}
						}
						
					};
					
					long refNanoTime = System.nanoTime();
					
					while ( !platformServiceManager.isCloseRequested() ) {
						timeIntervalProfile.update();
						
						shapeTestProfile.update();
						
						renderEngineBackend.getScreenRenderingFrame().render(new RenderingFrameEmmiter(){
			
							@Override
							protected void render_internal(RenderingFrameRenderer renderer) {
								renderer.clear(new Vector4f(0.0f, 0.0f, 0.25f, 1.0f));
								renderer.render(planeRendererEmmiter);
							}
							
						});
						
						platformServiceManager.newFrame();
					}
					
					timeIntervalProfile.detachObservers();
					
					shapeWithBoundingPerimeterButtonObserver.detach();
					boundingRegionButtonObserver.detach();
					
					shapeTestProfile.detachObservers();
				}
			}
		}
	}
}
