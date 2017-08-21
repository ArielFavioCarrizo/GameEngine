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

import java.util.Scanner;

import com.esferixis.gameengine.physics.plane.statics.ProportionalAffineMapper2d;
import com.esferixis.gameengine.platform.PlatformServiceManager;
import com.esferixis.gameengine.platform.PlatformServiceManagerException;
import com.esferixis.gameengine.platform.display.ScreenConfig;
import com.esferixis.gameengine.renderengine.backend.RenderEngineBackend;
import com.esferixis.gameengine.renderengine.backend.plane.PlaneObjectComponentRenderer;
import com.esferixis.gameengine.renderengine.backend.plane.PlaneRendererEmmiter;
import com.esferixis.gameengine.renderengine.backend.plane.shape.ShapeObject;
import com.esferixis.gameengine.renderengine.backend.renderingFrame.RenderingFrameEmmiter;
import com.esferixis.gameengine.renderengine.backend.renderingFrame.RenderingFrameRenderer;
import com.esferixis.gameengine.renderengine.plane.shape.UniformColorDistribution;
import com.esferixis.gameengine.tests.GameEngineTestRunnable;
import com.esferixis.geometry.plane.ShapeTest;
import com.esferixis.geometry.plane.finite.FiniteProportionalHolomorphicShape;
import com.esferixis.geometry.plane.finite.Point;
import com.esferixis.math.Matrix3f;
import com.esferixis.math.ProportionalMatrix3f;
import com.esferixis.math.Vector2f;
import com.esferixis.math.Vector4f;
import com.esferixis.misc.exception.NotImplementedException;
import com.esferixis.misc.reference.DynamicReference;

/**
 * @author ariel
 *
 */
public final class ProportionalHolomorphicShapePerimetralDilateTest extends GameEngineTestRunnable {
	private static final ShapeObject resultShapeObject = new ShapeObject(new Point(Vector2f.ZERO), new UniformColorDistribution(new Vector4f(1.0f, 1.0f, 0.0f, 0.5f)));
	private static final ShapeObject pointsShapeObject = new ShapeObject(new Point(Vector2f.ZERO), new UniformColorDistribution(new Vector4f(1.0f, 1.0f, 1.0f, 0.5f)));
	
	/**
	 * @param title
	 */
	public ProportionalHolomorphicShapePerimetralDilateTest() {
		super("Shape perimetral dilation test");
	}
	
	/* (non-Javadoc)
	 * @see com.esferixis.gameengine.tests.GameEngineTestRunnable#run(com.esferixis.gameengine.platform.PlatformServiceManager)
	 */
	@Override
	public void run(PlatformServiceManager platformServiceManager) {
		ScreenConfig screenConfig;
		
		final FiniteProportionalHolomorphicShape<?> initialShape1 = ShapeTest.enterShape(new Scanner(System.in), "Shape 1", FiniteProportionalHolomorphicShape.class);
		final FiniteProportionalHolomorphicShape<?> initialShape2;
		
		if ( initialShape1 != null ) {
			initialShape2 = ShapeTest.enterShape(new Scanner(System.in), "Shape 2", FiniteProportionalHolomorphicShape.class);
		}
		else {
			initialShape2 = null;
		}
			
		boolean enterTest;
		
		if ( ( initialShape1 != null ) && ( initialShape2 != null ) ) {
			try {
				initialShape1.perimetralDilate(initialShape2);
			} catch ( NotImplementedException e ) {
				System.out.println("Warning: Not implemented");
			} catch ( RuntimeException e ) {
				e.printStackTrace();
			}
			
			enterTest = true;
		}
		else {
			enterTest = false;
		}
		
		if ( enterTest ) {
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
			
			final ShapeTestProfile<FiniteProportionalHolomorphicShape<?>> shapeTestProfile1 = ShapeTestProfile.createForFirstShape(platformServiceManager, initialShape1);
			final ShapeTestProfile<FiniteProportionalHolomorphicShape<?>> shapeTestProfile2 = ShapeTestProfile.createForSecondShape(platformServiceManager, initialShape2);
			
			shapeTestProfile1.attachObservers();
			shapeTestProfile2.attachObservers();
			
			final RenderEngineBackend renderEngineBackend = platformServiceManager.getDisplayManager().getRenderEngineBackend();
			
			final ProportionalAffineMapper2d meshObjectAffineMapper = new ProportionalAffineMapper2d();
			
			final DynamicReference<Integer> nframe = new DynamicReference<Integer>(0);
			
			final PlaneRendererEmmiter planeRendererEmmiter = new PlaneRendererEmmiter() {
	
				@Override
				protected void render_internal(PlaneObjectComponentRenderer objectRenderer) {
					shapeTestProfile1.render(objectRenderer, new Vector4f(1.0f, 0.0f, 0.0f, 0.5f));
					shapeTestProfile2.render(objectRenderer, new Vector4f(0.0f, 1.0f, 0.0f, 0.5f));
					
					try {
						resultShapeObject.setShape( shapeTestProfile1.getActualShape().perimetralDilate(shapeTestProfile2.getActualShape()) );
						
						objectRenderer.render(resultShapeObject);
					} catch ( NotImplementedException e ) {
						
					} catch ( RuntimeException e ) {
						e.printStackTrace();
					}
					
					for ( int i = 0 ; i<1000; i++ ) {
						Point point = new Point( ShapeMisc.getRandomPoint(shapeTestProfile1.getActualShape()).add(ShapeMisc.getRandomPoint(shapeTestProfile2.getActualShape())));
						
						pointsShapeObject.setShape(point);
						
						objectRenderer.render(pointsShapeObject);
					}
				}
				
			};
			
			long refNanoTime = System.nanoTime();
			
			while ( !platformServiceManager.isCloseRequested() ) {
				final float t = (float) ((System.nanoTime() - refNanoTime) / 1000000000.0d);
				ProportionalMatrix3f transformMatrix = Matrix3f.IDENTITY;
				transformMatrix = transformMatrix.rotate( t * 0.5f * (float) Math.PI);
				meshObjectAffineMapper.setTransformMatrix(transformMatrix);
				
				shapeTestProfile1.update();
				shapeTestProfile2.update();
				
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
			
			shapeTestProfile1.detachObservers();
			shapeTestProfile2.detachObservers();
		}
	}
	
}
