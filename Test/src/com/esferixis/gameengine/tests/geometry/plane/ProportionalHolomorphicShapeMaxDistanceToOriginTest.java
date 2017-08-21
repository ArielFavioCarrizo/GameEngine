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
import com.esferixis.geometry.plane.finite.Circumference;
import com.esferixis.geometry.plane.finite.FiniteProportionalHolomorphicShape;
import com.esferixis.geometry.plane.finite.Point;
import com.esferixis.math.Matrix3f;
import com.esferixis.math.ProportionalMatrix3f;
import com.esferixis.math.Vector2f;
import com.esferixis.math.Vector4f;

/**
 * @author ariel
 *
 */
public final class ProportionalHolomorphicShapeMaxDistanceToOriginTest extends GameEngineTestRunnable {
	private static final ShapeObject circumferenceShapeObject = new ShapeObject(new Point(Vector2f.ZERO), new UniformColorDistribution(new Vector4f(1.0f, 1.0f, 0.0f, 0.5f)));
	
	/**
	 * @param title
	 */
	public ProportionalHolomorphicShapeMaxDistanceToOriginTest() {
		super("Max distance to origin test");
	}
	
	/* (non-Javadoc)
	 * @see com.esferixis.gameengine.tests.GameEngineTestRunnable#run(com.esferixis.gameengine.platform.PlatformServiceManager)
	 */
	@Override
	public void run(PlatformServiceManager platformServiceManager) {
		ScreenConfig screenConfig;
		
		final FiniteProportionalHolomorphicShape<?> initialShape = ShapeTest.enterShape(new Scanner(System.in), "Target", FiniteProportionalHolomorphicShape.class);
		
		if ( initialShape != null ) {
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
			
			final ShapeTestProfile<FiniteProportionalHolomorphicShape<?>> shapeTestProfile = ShapeTestProfile.createForFirstShape(platformServiceManager, initialShape);
			
			shapeTestProfile.attachObservers();
			
			final RenderEngineBackend renderEngineBackend = platformServiceManager.getDisplayManager().getRenderEngineBackend();
			
			final ProportionalAffineMapper2d meshObjectAffineMapper = new ProportionalAffineMapper2d();
			
			final PlaneRendererEmmiter planeRendererEmmiter = new PlaneRendererEmmiter() {
	
				@Override
				protected void render_internal(PlaneObjectComponentRenderer objectRenderer) {
					shapeTestProfile.render(objectRenderer, new Vector4f(1.0f, 0.0f, 0.0f, 0.5f));
					
					try {
						final float radius = shapeTestProfile.getActualShape().maxDistanceToOrigin();
						final FiniteProportionalHolomorphicShape<?> shape;
						
						if ( radius == 0.0f ) {
							shape = new Point( Vector2f.ZERO );
						}
						else {
							shape = new Circumference(Vector2f.ZERO, radius);
						}
						
						circumferenceShapeObject.setShape(shape);
						objectRenderer.render(circumferenceShapeObject);
					} catch ( RuntimeException e ) {
						e.printStackTrace();
					}
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
