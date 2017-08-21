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

package com.esferixis.gameengine.tests.testscene;

import java.util.Map;

import com.esferixis.gameengine.frame.FrameManager;
import com.esferixis.gameengine.physics.time.RootTemporalEventsEngine;
import com.esferixis.gameengine.physics.time.TemporalEventsEngine;
import com.esferixis.gameengine.platform.PlatformServiceManager;
import com.esferixis.gameengine.platform.PlatformServiceManagerException;
import com.esferixis.gameengine.platform.display.ScreenConfig;
import com.esferixis.gameengine.platform.input.Axis;
import com.esferixis.gameengine.platform.input.Button;
import com.esferixis.gameengine.platform.input.ButtonPairBasedVirtualAxis;
import com.esferixis.gameengine.platform.input.Keyboard;
import com.esferixis.gameengine.renderengine.frontend.RenderEngineFrontend;
import com.esferixis.gameengine.renderengine.frontend.RenderEngineFrontendConfiguration;
import com.esferixis.gameengine.renderengine.frontend.plane.kinematics.InstantKinematicRenderer;
import com.esferixis.gameengine.renderengine.frontend.plane.kinematics.containers.KinematicRenderPlaneObjectContainer;
import com.esferixis.gameengine.renderengine.frontend.plane.kinematics.containers.LinearKinematicRenderPlaneObjectContainer;
import com.esferixis.gameengine.renderengine.frontend.plane.kinematics.containers.RenderizableKinematicRenderPlaneObjectContainer;
import com.esferixis.gameengine.renderengine.frontend.plane.kinematics.view.PlaneCamera;
import com.esferixis.gameengine.renderengine.frontend.plane.kinematics.view.PlaneView;
import com.esferixis.gameengine.renderengine.frontend.plane.staticstage.PlaneRendererEmmiter;
import com.esferixis.gameengine.renderengine.frontend.plane.staticstage.StaticPlaneObjectRenderer;
import com.esferixis.gameengine.renderengine.frontend.renderingFrame.RenderingFrameEmmiter;
import com.esferixis.gameengine.renderengine.frontend.renderingFrame.RenderingFrameRenderer;
import com.esferixis.gameengine.tests.GameEngineTestRunnable;
import com.esferixis.gameengine.tests.menu.Menu;
import com.esferixis.gameengine.tests.menu.MenuElement;
import com.esferixis.gameengine.tests.renderengine.frontend.plane.kinematics.scenes.MultipleStaticTrianglesTestScene;
import com.esferixis.gameengine.tests.renderengine.frontend.plane.kinematics.scenes.OneTriangleTestScene;
import com.esferixis.math.Vector2f;
import com.esferixis.math.Vector4f;
import com.esferixis.math.intervalarithmetic.FloatClosedInterval;
import com.esferixis.misc.ElementCallback;
import com.esferixis.misc.ElementProcessor;
import com.esferixis.misc.reference.DynamicReference;

/**
 * Prueba de animación de rotación de triángulo
 * 
 * @author Ariel Favio Carrizo
 *
 */
public final class TestSceneRunner {
	/**
	 * @pre El administrador de servicios de plataforma, y la escena de testing
	 * 		no pueden ser nulos
	 * @post Ejecuta una instancia de la escena de test especificada, con el administrador
	 * 		 de servicio de plataforma especificado.
	 * 		 Indicando si tiene que haber "frame drop"
	 */
	public static void run(final PlatformServiceManager serviceManager, final TestScene testScene, final boolean frameDrop) {
		if ( ( serviceManager != null ) && ( testScene != null ) ) {
			ScreenConfig screenConfig;
			
			serviceManager.getDisplayManager().setWindowTitle("Test scene runner");
			try {
				screenConfig = serviceManager.getDisplayManager().createWindowedScreenConfig(640, 480);
			} catch (PlatformServiceManagerException e) {
				throw new RuntimeException(e);
			}
			
			try {
				serviceManager.getDisplayManager().setScreenConfig(screenConfig);
			} catch (PlatformServiceManagerException e) {
				throw new RuntimeException(e);
			}
			
			final RenderEngineFrontendConfiguration renderEngineFrontendConfiguration;
			{
				final RenderEngineFrontendConfiguration.Essence renderEngineFrontendConfigurationEssence = new RenderEngineFrontendConfiguration.Essence();
				renderEngineFrontendConfigurationEssence.maxMemoryToUse.init(1024l * 1024l * 32l);
				
				renderEngineFrontendConfiguration = new RenderEngineFrontendConfiguration(renderEngineFrontendConfigurationEssence);
			}
			
			RenderEngineFrontend.execute(serviceManager.getDisplayManager().getRenderEngineBackend(), renderEngineFrontendConfiguration, new ElementCallback<RenderEngineFrontend>() {
	
				@Override
				public void run(final RenderEngineFrontend renderEngineFrontend) {
					final RenderizableKinematicRenderPlaneObjectContainer renderObjectContainer = new LinearKinematicRenderPlaneObjectContainer();
					
					final PlaneCamera planeCamera = new PlaneCamera(new Vector2f(1.0f, 1.0f));
					final PlaneView planeView = new PlaneView(renderObjectContainer, planeCamera);
					
					final Axis xAxis, yAxis;
					final Axis angleAxis;
					
					{
						final Map<Keyboard.Key, Button> buttonPerKey = serviceManager.getInputManager().getKeyboard().getMapping();
						
						xAxis = new ButtonPairBasedVirtualAxis(buttonPerKey.get(Keyboard.Key.KEY_LEFT), buttonPerKey.get(Keyboard.Key.KEY_RIGHT));
						yAxis = new ButtonPairBasedVirtualAxis(buttonPerKey.get(Keyboard.Key.KEY_DOWN), buttonPerKey.get(Keyboard.Key.KEY_UP));
						angleAxis = new ButtonPairBasedVirtualAxis(buttonPerKey.get(Keyboard.Key.KEY_Z), buttonPerKey.get(Keyboard.Key.KEY_A));
					}
					
					final RootTemporalEventsEngine temporalEventsEngine = new RootTemporalEventsEngine();
					
					final TestScene.Instance testSceneInstance = testScene.create(serviceManager.getInputManager(), temporalEventsEngine.getEventsManager(), renderObjectContainer);
					
					serviceManager.getDisplayManager().setWindowTitle("Test scene runner: " + testSceneInstance.getName());
					
					final FrameManager frameManager = new FrameManager(temporalEventsEngine.getEventsManager(), 0.0f, 60) {
						/**
						 * 
						 */
						private static final long serialVersionUID = -4094983804341879029L;
						
						Vector2f cameraCenterSpeed = Vector2f.ZERO;
						
						@Override
						protected void executeFrameTask(FloatClosedInterval timeInterval) {
							if ( serviceManager.isCloseRequested() ) {
								temporalEventsEngine.stop();
							}
							
							if ( !testSceneInstance.isCameraLocked() ) {
								{
									float xSpeed = cameraCenterSpeed.getX(), ySpeed = cameraCenterSpeed.getY();
									
									if ( xAxis.getState() == 0.0f ) {
										xSpeed = 0.0f;
									}
									else {
										xSpeed += xAxis.getState() * 0.001f * timeInterval.length() * 30.0f;
									}
									
									if ( yAxis.getState() == 0.0f ) {
										ySpeed = 0.0f;
									}
									else {
										ySpeed += yAxis.getState() * 0.001f * timeInterval.length() * 30.0f;
									}
									
									cameraCenterSpeed = new Vector2f(xSpeed, ySpeed);
								}
								
								planeCamera.setCenterPosition(planeCamera.getCenterPosition().add(cameraCenterSpeed));
								planeCamera.setAngle(planeCamera.getAngle() + angleAxis.getState() * (float) Math.PI * 2.0f * 0.01f * timeInterval.length() * 30.0f);
							}
						}
	
						@Override
						protected void render(final RenderingFrameRenderer renderer, final float time) {
							final PlaneRendererEmmiter planeRendererEmmiter = new PlaneRendererEmmiter() {
	
								@Override
								public void render_internal(StaticPlaneObjectRenderer staticObjectRenderer) {
									planeView.render(new InstantKinematicRenderer(staticObjectRenderer, time));
								}
								
							};
							
							renderer.clear(new Vector4f(0.0f, 0.0f, 0.0f, 1.0f));
							renderer.render(planeRendererEmmiter);
						}
						
					};
					
					frameManager.setPlatformServices(serviceManager, renderEngineFrontend);
					
					frameManager.setFrameDrop(frameDrop);
					
					temporalEventsEngine.run();
					
					testSceneInstance.destroy();
				}
				
			});
			
			try {
				serviceManager.getDisplayManager().setScreenConfig(null);
			} catch (PlatformServiceManagerException e) {
				throw new RuntimeException(e);
			}
		}
		else {
			throw new NullPointerException();
		}
	}
	
	/**
	 * @pre El administrador de servicios de plataforma, y la escena de testing
	 * 		no pueden ser nulos
	 * @post Ejecuta una instancia de la escena de test especificada, con el administrador
	 * 		 de servicio de plataforma especificado
	 */
	public static void run(final PlatformServiceManager serviceManager, final TestScene testScene) {
		run(serviceManager, testScene, true);
	}
	
	/**
	 * @pre Ningún parámetro puede ser nulo
	 * @post Ejecuta un menú con el administrador de servicios de plataforma, y con los tests de escena especificados
	 */
	public static void run(final PlatformServiceManager serviceManager, TestScene... testScenes) {
		if ( ( serviceManager != null ) && ( testScenes != null ) ) {
			
			new Menu("Scene select", testScenes, new ElementProcessor<TestScene, MenuElement>() {
	
				@Override
				public MenuElement process(final TestScene testScene) {
					return new MenuElement(testScene.getName()) {
	
						@Override
						public void run() {
							TestSceneRunner.run(serviceManager, testScene);
						}
						
					};
				}
				
			}).run();
			
		}
		else {
			throw new NullPointerException();
		}
	}
}
