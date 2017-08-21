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

package com.esferixis.gameengine.tests.renderengine.frontend.plane.staticstage;

import java.util.Arrays;
import java.util.Collections;

import com.esferixis.gameengine.physics.plane.statics.ProportionalAffineMapper2d;
import com.esferixis.gameengine.platform.PlatformServiceManager;
import com.esferixis.gameengine.platform.PlatformServiceManagerException;
import com.esferixis.gameengine.platform.display.ScreenConfig;
import com.esferixis.gameengine.renderengine.backend.RenderEngineBackend;
import com.esferixis.gameengine.renderengine.frontend.RenderEngineFrontend;
import com.esferixis.gameengine.renderengine.frontend.RenderEngineFrontendConfiguration;
import com.esferixis.gameengine.renderengine.frontend.meshLayers.MeshLayersConfig;
import com.esferixis.gameengine.renderengine.frontend.meshLayers.MeshLayersConfigProfile;
import com.esferixis.gameengine.renderengine.frontend.meshLayers.VertexColoredMeshLayer;
import com.esferixis.gameengine.renderengine.frontend.meshLayers.VertexColoredMeshLayerVertexData;
import com.esferixis.gameengine.renderengine.frontend.meshLayers.VertexLayersData;
import com.esferixis.gameengine.renderengine.frontend.misc.mesh.Mesh;
import com.esferixis.gameengine.renderengine.frontend.misc.mesh.MeshTriangle;
import com.esferixis.gameengine.renderengine.frontend.misc.mesh.colored.ColoredMeshData;
import com.esferixis.gameengine.renderengine.frontend.misc.mesh.colored.ColoredMeshVertex;
import com.esferixis.gameengine.renderengine.frontend.plane.staticstage.PlaneRendererEmmiter;
import com.esferixis.gameengine.renderengine.frontend.plane.staticstage.StaticPlaneObject;
import com.esferixis.gameengine.renderengine.frontend.plane.staticstage.StaticPlaneObjectRenderer;
import com.esferixis.gameengine.renderengine.frontend.plane.staticstage.mesh.StaticMeshObject2d;
import com.esferixis.gameengine.renderengine.frontend.renderingFrame.RenderingFrameEmmiter;
import com.esferixis.gameengine.renderengine.frontend.renderingFrame.RenderingFrameRenderer;
import com.esferixis.gameengine.tests.GameEngineTestRunnable;
import com.esferixis.math.Matrix3f;
import com.esferixis.math.ProportionalMatrix3f;
import com.esferixis.math.Vector2f;
import com.esferixis.math.Vector4f;
import com.esferixis.misc.ElementCallback;
import com.esferixis.misc.loader.DataLoadingErrorException;
import com.esferixis.misc.loader.MemoryLoader;

/**
 * Prueba de animación de rotación de triángulo
 * 
 * @author Ariel Favio Carrizo
 *
 */
public final class RotatingVertexColoredTriangleTest extends GameEngineTestRunnable {
	public RotatingVertexColoredTriangleTest() {
		super("Rotating vertex colored triangle test (Frontend, static phase)");
	}
	
	/**
	 * @post Ejecuta una instancia del juego con el administrador
	 * 		 de servicio de plataforma especificado
	 */
	public void run(final PlatformServiceManager serviceManager) {
		ScreenConfig screenConfig;
		
		serviceManager.getDisplayManager().setWindowTitle(this.title);
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
				final MeshLayersConfig meshLayersConfig = new MeshLayersConfig(new VertexColoredMeshLayer());
				
				final Mesh<Vector2f, ColoredMeshData<Vector2f>> mesh = new Mesh<Vector2f, ColoredMeshData<Vector2f>>(new MemoryLoader<ColoredMeshData<Vector2f>>( new ColoredMeshData<Vector2f>( Arrays.asList( new MeshTriangle<Vector2f, ColoredMeshVertex<Vector2f>>(
						new ColoredMeshVertex<Vector2f>( new Vector2f(1.0f, -1.0f), new VertexLayersData( new VertexColoredMeshLayerVertexData( new Vector4f(1.0f, 0.0f, 0.0f, 1.0f) ) ) ),
						new ColoredMeshVertex<Vector2f>( new Vector2f(1.0f, 1.0f), new VertexLayersData( new VertexColoredMeshLayerVertexData( new Vector4f(0.0f, 0.0f, 1.0f, 1.0f) ) ) ),
						new ColoredMeshVertex<Vector2f>( new Vector2f(-1.0f, -1.0f), new VertexLayersData( new VertexColoredMeshLayerVertexData( new Vector4f(0.0f, 1.0f, 0.0f, 1.0f) ) ) )
				)), meshLayersConfig)));
				
				final ProportionalAffineMapper2d meshObjectAffineMapper = new ProportionalAffineMapper2d();
				final StaticMeshObject2d meshObject = new StaticMeshObject2d(mesh, new MeshLayersConfigProfile(meshLayersConfig), meshObjectAffineMapper);
				final ProportionalMatrix3f globalTransformMatrix = Matrix3f.IDENTITY.scale(0.3f);
				
				final PlaneRendererEmmiter planeRendererEmmiter = new PlaneRendererEmmiter() {

					@Override
					public void render_internal(StaticPlaneObjectRenderer objectRenderer) {
						objectRenderer.render(meshObject);
					}
					
				};
				
				long refNanoTime = System.nanoTime();
				
				while ( !serviceManager.isCloseRequested() ) {
					final float t = (float) ((System.nanoTime() - refNanoTime) / 1000000000.0d);
					ProportionalMatrix3f transformMatrix = Matrix3f.IDENTITY;
					transformMatrix = transformMatrix.rotate( t * 0.5f * (float) Math.PI);
					meshObjectAffineMapper.setTransformMatrix(globalTransformMatrix.mul(transformMatrix));
					
					renderEngineFrontend.getScreenRenderingFrame().render(new RenderingFrameEmmiter(){

						@Override
						protected void render_internal(RenderingFrameRenderer renderer) {
							renderer.clear(new Vector4f(0.0f, 0.0f, 0.0f, 1.0f));
							renderer.render(planeRendererEmmiter);
						}
						
					});
					
					serviceManager.newFrame();
				}
			}
			
		});
	}
}
