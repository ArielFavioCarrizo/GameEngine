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

package com.esferixis.gameengine.tests.renderengine.backend.space;

import java.io.IOException;
import java.util.Arrays;

import com.esferixis.gameengine.physics.space.statics.AffineMapper;
import com.esferixis.gameengine.platform.PlatformServiceManager;
import com.esferixis.gameengine.platform.PlatformServiceManagerException;
import com.esferixis.gameengine.platform.display.ScreenConfig;
import com.esferixis.gameengine.renderengine.backend.RenderEngineBackend;
import com.esferixis.gameengine.renderengine.backend.meshLayers.MeshLayersConfig;
import com.esferixis.gameengine.renderengine.backend.meshLayers.SimpleTextureLayer;
import com.esferixis.gameengine.renderengine.backend.meshLayers.UniformColoredMeshLayer;
import com.esferixis.gameengine.renderengine.backend.meshLayers.UniformColoredMeshLayerVertexData;
import com.esferixis.gameengine.renderengine.backend.meshLayers.VertexColoredMeshLayer;
import com.esferixis.gameengine.renderengine.backend.meshLayers.VertexColoredMeshLayerVertexData;
import com.esferixis.gameengine.renderengine.backend.meshLayers.VertexLayersData;
import com.esferixis.gameengine.renderengine.backend.misc.mesh.Mesh;
import com.esferixis.gameengine.renderengine.backend.misc.mesh.MeshObject;
import com.esferixis.gameengine.renderengine.backend.misc.mesh.MeshTriangle;
import com.esferixis.gameengine.renderengine.backend.misc.mesh.colored.ColoredMeshData;
import com.esferixis.gameengine.renderengine.backend.misc.mesh.colored.ColoredMeshVertex;
import com.esferixis.gameengine.renderengine.backend.renderingFrame.RenderingFrameEmmiter;
import com.esferixis.gameengine.renderengine.backend.renderingFrame.RenderingFrameRenderer;
import com.esferixis.gameengine.renderengine.backend.space.light.LightSourceRenderer;
import com.esferixis.gameengine.renderengine.backend.space.light.LightsRendererEmmiter;
import com.esferixis.gameengine.renderengine.backend.space.mesh.GeometryRenderer3d;
import com.esferixis.gameengine.renderengine.backend.space.mesh.MeshObject3d;
import com.esferixis.gameengine.renderengine.backend.space.mesh.GeometryRenderer3d.CullFace;
import com.esferixis.gameengine.renderengine.backend.space.universe.SpaceGeometryRendererEmmiter;
import com.esferixis.gameengine.renderengine.backend.space.universe.SpaceUniverseRendererEmmiter;
import com.esferixis.gameengine.renderengine.misc.colorObject.ColorObject;
import com.esferixis.gameengine.renderengine.space.camera.Camera3d;
import com.esferixis.gameengine.tests.GameEngineTestRunnable;
import com.esferixis.math.Matrix4f;
import com.esferixis.math.Vector3f;
import com.esferixis.math.Vector4f;
import com.esferixis.math.intervalarithmetic.FloatClosedInterval;
import com.esferixis.misc.loader.DataLoadingErrorException;
import com.esferixis.misc.loader.MemoryLoader;

/**
 * Prueba de animación de rotación de triángulo
 * 
 * @author Ariel Favio Carrizo
 *
 */
public final class RotatingUniformColoredTriangleTest extends GameEngineTestRunnable {
	/**
	 * @param title
	 */
	public RotatingUniformColoredTriangleTest() {
		super("Rotating uniform colored triangle test (Backend)");
	}
	
	/**
	 * @post Ejecuta una instancia del juego con el administrador
	 * 		 de servicio de plataforma especificado
	 */
	public void run(PlatformServiceManager serviceManager) {
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
		
		final RenderEngineBackend renderEngineBackend = serviceManager.getDisplayManager().getRenderEngineBackend();
		
		final Camera3d camera = new Camera3d();
		camera.setPosition(new Vector3f(0.0f, 0.0f, 10.0f));
		
		ColorObject uniformColorObject = new ColorObject(Vector4f.ZERO);
		
		final MeshLayersConfig meshLayersConfig = new MeshLayersConfig(new UniformColoredMeshLayer(uniformColorObject));
		
		try {
			renderEngineBackend.load(meshLayersConfig);
		} catch ( PlatformServiceManagerException e) {
			throw new RuntimeException(e);
		}
		
		final Mesh<Vector3f, ColoredMeshData<Vector3f>> mesh = new Mesh<Vector3f, ColoredMeshData<Vector3f>>(new MemoryLoader<ColoredMeshData<Vector3f>>( new ColoredMeshData<Vector3f>( Arrays.asList( new MeshTriangle<Vector3f, ColoredMeshVertex<Vector3f>>(
				new ColoredMeshVertex<Vector3f>( new Vector3f(1.0f, -1.0f, 0.0f), new VertexLayersData( new UniformColoredMeshLayerVertexData() ) ),
				new ColoredMeshVertex<Vector3f>( new Vector3f(1.0f, 1.0f, 0.0f), new VertexLayersData( new UniformColoredMeshLayerVertexData() ) ),
				new ColoredMeshVertex<Vector3f>( new Vector3f(-1.0f, -1.0f, 0.0f), new VertexLayersData( new UniformColoredMeshLayerVertexData() ) )
		)), meshLayersConfig)));
		try {
			renderEngineBackend.load(mesh);
		} catch (DataLoadingErrorException | PlatformServiceManagerException e) {
			throw new RuntimeException(e);
		}
		
		final AffineMapper meshObjectAffineMapper = new AffineMapper();
		final MeshObject3d<ColoredMeshData<Vector3f>> meshObject = new MeshObject3d<ColoredMeshData<Vector3f>>(mesh, meshObjectAffineMapper);
		
		final SpaceUniverseRendererEmmiter spaceUniverseRendererEmmiter = new SpaceUniverseRendererEmmiter() {

			@Override
			public FloatClosedInterval[] prepare(Camera3d camera) throws NullPointerException {
				return new FloatClosedInterval[]{
						new FloatClosedInterval(0.5f, 300.0f)
				};
			}

			@Override
			public SpaceGeometryRendererEmmiter<ColoredMeshData<Vector3f>> getColoredOpaquesEmmiter() {
				return new SpaceGeometryRendererEmmiter<ColoredMeshData<Vector3f>>() {

					@Override
					public void render_internal(Matrix4f projectedViewTransformMatrix,
							GeometryRenderer3d<ColoredMeshData<Vector3f>> geometryRenderer) {
						geometryRenderer.setCullFace(CullFace.NONE);
						geometryRenderer.render(meshObject);
					}
					
				};
			}

			@Override
			public SpaceGeometryRendererEmmiter<ColoredMeshData<Vector3f>> getColoredTransparentsEmmiter() {
				return null;
			}

			@Override
			public LightsRendererEmmiter getLightsRendererEmmiter() {
				return null;
			}
			
		};
		
		long refNanoTime = System.nanoTime();
		
		while ( !serviceManager.isCloseRequested() ) {
			final float t = (float) ((System.nanoTime() - refNanoTime) / 1000000000.0d);
			Matrix4f transformMatrix = Matrix4f.IDENTITY;
			transformMatrix = transformMatrix.translate(new Vector3f(0.0f, 0.0f, -20.0f * ( (float) Math.sin( t * 0.2f * Math.PI) + 1.0f) / 2.0f));
			transformMatrix = transformMatrix.rotate( t * 0.5f * (float) Math.PI, new Vector3f(0.0f, 0.0f, 1.0f));
			
			uniformColorObject.setColor(new Vector4f(((float) Math.sin(t * 2.0f * Math.PI / 2.0f) + 1.0f) / 2.0f, ((float) Math.sin(t * 2.0f * Math.PI / 1.0f) + 1.0f) / 2.0f, ((float) Math.sin(t * 2.0f * Math.PI / 2.0f) + 1.0f) / 0.5f, 1.0f));;
			
			meshObjectAffineMapper.setTransformMatrix(transformMatrix);
			
			renderEngineBackend.getScreenRenderingFrame().render(new RenderingFrameEmmiter(){

				@Override
				protected void render_internal(RenderingFrameRenderer renderer) {
					renderer.clear(new Vector4f(0.0f, 0.0f, 0.0f, 1.0f));
					renderer.render(spaceUniverseRendererEmmiter, camera);
				}
				
			});
			
			serviceManager.newFrame();
		}
		
		renderEngineBackend.unload(mesh);
		
		renderEngineBackend.unload(meshLayersConfig);
	}
}
