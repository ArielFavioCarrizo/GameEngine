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

package com.esferixis.gameengine.tests.geometry.plane;

import java.util.Iterator;
import java.util.Scanner;

import com.esferixis.gameengine.physics.plane.statics.ProportionalAffineMapper2d;
import com.esferixis.gameengine.platform.PlatformServiceManager;
import com.esferixis.gameengine.platform.PlatformServiceManagerException;
import com.esferixis.gameengine.platform.display.ScreenConfig;
import com.esferixis.gameengine.platform.input.Axis;
import com.esferixis.gameengine.platform.input.ButtonPairBasedVirtualAxis;
import com.esferixis.gameengine.platform.input.Controller;
import com.esferixis.gameengine.platform.input.Keyboard;
import com.esferixis.gameengine.renderengine.backend.RenderEngineBackend;
import com.esferixis.gameengine.renderengine.backend.meshLayers.MeshLayersConfig;
import com.esferixis.gameengine.renderengine.backend.meshLayers.VertexColoredMeshLayer;
import com.esferixis.gameengine.renderengine.backend.plane.PlaneObjectComponentRenderer;
import com.esferixis.gameengine.renderengine.backend.plane.PlaneRendererEmmiter;
import com.esferixis.gameengine.renderengine.backend.renderingFrame.RenderingFrameEmmiter;
import com.esferixis.gameengine.renderengine.backend.renderingFrame.RenderingFrameRenderer;
import com.esferixis.gameengine.tests.GameEngineTestRunnable;
import com.esferixis.gameengine.tests.geometry.plane.ShapeTestProfile;
import com.esferixis.geometry.plane.Shape;
import com.esferixis.geometry.plane.ShapeTest;
import com.esferixis.math.Matrix3f;
import com.esferixis.math.ProportionalMatrix3f;
import com.esferixis.math.Vector4f;

/**
 * Prueba de animación de rotación de triángulo
 * 
 * @author Ariel Favio Carrizo
 *
 */
public final class ShapeRenderingTest extends GameEngineTestRunnable {
	public ShapeRenderingTest() {
		super("Plane shapes rendering test (Backend)");
	}
	
	/**
	 * @post Ejecuta una instancia del juego con el administrador
	 * 		 de servicio de plataforma especificado
	 */
	public void run(PlatformServiceManager platformServiceManager) {
		ScreenConfig screenConfig;
		
		final Shape<?> initialShape;
		
		if ( ( initialShape = ShapeTest.enterShape(new Scanner(System.in), "Shape", Shape.class)) != null ) {
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
			
			final ShapeTestProfile<Shape<?>> shapeTestProfile;
			
			{
				Axis horizontalSpeedAxis = null, verticalSpeedAxis = null;
				Axis angleSpeedAxis = null;
				
				{
					boolean foundController = false;
					
					if ( !platformServiceManager.getInputManager().getControllers().isEmpty() ) {
						final Iterator<Controller> controllersIterator = platformServiceManager.getInputManager().getControllers().iterator();
						
						while ( controllersIterator.hasNext() && (!foundController) ) {
							final Controller eachController = controllersIterator.next();
							
							if ( eachController.getAxles().size() >= 3 ) {
								horizontalSpeedAxis = eachController.getAxles().get(0);
								verticalSpeedAxis = eachController.getAxles().get(1);
								angleSpeedAxis = eachController.getAxles().get(2);
								
								foundController = true;
							}
						}
					}
					
					if ( ( !foundController ) && ( platformServiceManager.getInputManager().getKeyboard() != null ) ) {
						final Keyboard keyboard = platformServiceManager.getInputManager().getKeyboard();
						horizontalSpeedAxis = new ButtonPairBasedVirtualAxis( keyboard.getMapping().get(Keyboard.Key.KEY_LEFT), keyboard.getMapping().get(Keyboard.Key.KEY_RIGHT) );
						verticalSpeedAxis = new ButtonPairBasedVirtualAxis( keyboard.getMapping().get(Keyboard.Key.KEY_DOWN), keyboard.getMapping().get(Keyboard.Key.KEY_UP) );
						angleSpeedAxis = new ButtonPairBasedVirtualAxis( keyboard.getMapping().get(Keyboard.Key.KEY_A), keyboard.getMapping().get(Keyboard.Key.KEY_Z) );
					}
				}
				shapeTestProfile = new ShapeTestProfile<Shape<?>>(initialShape, horizontalSpeedAxis, verticalSpeedAxis, angleSpeedAxis);
			}
			
			shapeTestProfile.attachObservers();
			
			final RenderEngineBackend renderEngineBackend = platformServiceManager.getDisplayManager().getRenderEngineBackend();
			
			final MeshLayersConfig meshLayersConfig = new MeshLayersConfig(new VertexColoredMeshLayer());
			
			try {
				renderEngineBackend.load(meshLayersConfig);
			} catch ( PlatformServiceManagerException e) {
				throw new RuntimeException(e);
			}
			
			final ProportionalAffineMapper2d meshObjectAffineMapper = new ProportionalAffineMapper2d();
			
			final PlaneRendererEmmiter planeRendererEmmiter = new PlaneRendererEmmiter() {
	
				@Override
				protected void render_internal(PlaneObjectComponentRenderer objectRenderer) {
					shapeTestProfile.render(objectRenderer, new Vector4f(1.0f, 1.0f, 1.0f, 1.0f));
				}
				
			};
			
			long refNanoTime = System.nanoTime();
			
			while ( !platformServiceManager.isCloseRequested() ) {
				final float t = (float) ((System.nanoTime() - refNanoTime) / 1000000000.0d);
				ProportionalMatrix3f transformMatrix = Matrix3f.IDENTITY;
				transformMatrix = transformMatrix.rotate( t * 0.5f * (float) Math.PI);
				meshObjectAffineMapper.setTransformMatrix(transformMatrix);
				
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
			
			shapeTestProfile.detachObservers();
		}
	}
}
