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
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import com.esferixis.gameengine.physics.space.statics.AffineMapper;
import com.esferixis.gameengine.platform.PlatformServiceManager;
import com.esferixis.gameengine.platform.PlatformServiceManagerException;
import com.esferixis.gameengine.platform.display.ScreenConfig;
import com.esferixis.gameengine.renderengine.backend.RenderEngineBackend;
import com.esferixis.gameengine.renderengine.backend.meshLayers.MeshLayersConfig;
import com.esferixis.gameengine.renderengine.backend.meshLayers.SimpleTextureLayer;
import com.esferixis.gameengine.renderengine.backend.meshLayers.SimpleTextureLayerVertexData;
import com.esferixis.gameengine.renderengine.backend.meshLayers.VertexLayersData;
import com.esferixis.gameengine.renderengine.backend.misc.mesh.Mesh;
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
import com.esferixis.gameengine.renderengine.backend.texture.Texture;
import com.esferixis.gameengine.renderengine.backend.texture.TextureObject2d;
import com.esferixis.gameengine.renderengine.picture.ProceduralPicture2d;
import com.esferixis.gameengine.renderengine.picture.RasterPicture2d;
import com.esferixis.gameengine.renderengine.space.camera.Camera3d;
import com.esferixis.gameengine.renderengine.texture.CoordinateWrap;
import com.esferixis.gameengine.renderengine.texture.TextureQualitySettings;
import com.esferixis.gameengine.renderengine.texture.TextureQualitySettings.MagFilter;
import com.esferixis.gameengine.renderengine.texture.TextureQualitySettings.MinFilter;
import com.esferixis.gameengine.tests.GameEngineTestRunnable;
import com.esferixis.math.Matrix4f;
import com.esferixis.math.Vector2f;
import com.esferixis.math.Vector3f;
import com.esferixis.math.Vector4f;
import com.esferixis.math.intervalarithmetic.FloatClosedInterval;
import com.esferixis.misc.loader.DataLoadingErrorException;
import com.esferixis.misc.loader.MemoryLoader;
import com.esferixis.misc.reference.DynamicReference;

/**
 * Prueba de animación de rotación de triángulo
 * 
 * @author Ariel Favio Carrizo
 *
 */
public final class RotatingTexturedSquareTest extends GameEngineTestRunnable {
	/**
	 * @param title
	 */
	public RotatingTexturedSquareTest() {
		super("Rotating textured square test (Backend)");
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
		
		final Texture<RasterPicture2d> texture = new Texture<RasterPicture2d>( (new ProceduralPicture2d<Vector3f>() {

			@Override
			public Vector3f getValue(Vector2f position) {
				return new Vector3f(position.getX(), position.getY(), 0.0f);
			}
			
		}).createRasterLoader(8, 8) );
		
		try {
			renderEngineBackend.load(texture);
		} catch (DataLoadingErrorException | PlatformServiceManagerException e) {
			throw new RuntimeException(e);
		}
		
		TextureQualitySettings textureQualitySettings = new TextureQualitySettings();
		textureQualitySettings.setMinFilter(MinFilter.LINEAR);
		textureQualitySettings.setMagFilter(MagFilter.LINEAR);
		
		final List<MeshLayersConfig> meshLayersConfigs = new ArrayList<MeshLayersConfig>();
		final List< Mesh<Vector3f, ColoredMeshData<Vector3f>> > meshes = new ArrayList< Mesh<Vector3f, ColoredMeshData<Vector3f>> >();
		final List< MeshObject3d<ColoredMeshData<Vector3f>> > meshObjects = new ArrayList< MeshObject3d< ColoredMeshData<Vector3f> > >();
		
		final AffineMapper meshObjectAffineMapper = new AffineMapper();
		
		for ( int i = 0 ; i < CoordinateWrap.values().length ; i++ ) {
			for ( int j = 0 ; j < CoordinateWrap.values().length ; j++ ) {
				final TextureObject2d textureObject;
				{
					final TextureObject2d.Essence essence = new TextureObject2d.Essence(texture, textureQualitySettings);
					essence.setCoordinateWrapS(CoordinateWrap.values()[i]);
					essence.setCoordinateWrapT(CoordinateWrap.values()[j]);
					essence.setBorderColor(new Vector4f(1.0f, 1.0f, 1.0f, 1.0f));
					textureObject = new TextureObject2d(essence);
				}
				
				final MeshLayersConfig textureMappingConfig = new MeshLayersConfig(new SimpleTextureLayer<RasterPicture2d>(textureObject));
				
				try {
					renderEngineBackend.load(textureMappingConfig);
				} catch ( PlatformServiceManagerException e) {
					throw new RuntimeException(e);
				}
				
				final float cMin = -3.0f, cMax = 4.0f;
				
				final Mesh<Vector3f, ColoredMeshData<Vector3f>> mesh = new Mesh<Vector3f, ColoredMeshData<Vector3f>>(new MemoryLoader<ColoredMeshData<Vector3f>>( new ColoredMeshData<Vector3f>( Arrays.asList(
						new MeshTriangle<Vector3f, ColoredMeshVertex<Vector3f>>(
								new ColoredMeshVertex<Vector3f>( new Vector3f(1.0f, -1.0f, 0.0f), new VertexLayersData( new SimpleTextureLayerVertexData<Vector2f>(new Vector2f(cMax, cMin))) ),
								new ColoredMeshVertex<Vector3f>( new Vector3f(1.0f, 1.0f, 0.0f), new VertexLayersData(new SimpleTextureLayerVertexData<Vector2f>(new Vector2f(cMax, cMax))) ),
								new ColoredMeshVertex<Vector3f>( new Vector3f(-1.0f, -1.0f, 0.0f), new VertexLayersData(new SimpleTextureLayerVertexData<Vector2f>(new Vector2f(cMin, cMin))) )
						),
						new MeshTriangle<Vector3f, ColoredMeshVertex<Vector3f>>(
								new ColoredMeshVertex<Vector3f>( new Vector3f(-1.0f, -1.0f, 0.0f), new VertexLayersData(new SimpleTextureLayerVertexData<Vector2f>(new Vector2f(cMin, cMin))) ),
								new ColoredMeshVertex<Vector3f>( new Vector3f(1.0f, 1.0f, 0.0f), new VertexLayersData(new SimpleTextureLayerVertexData<Vector2f>(new Vector2f(cMax, cMax))) ),
								new ColoredMeshVertex<Vector3f>( new Vector3f(-1.0f, 1.0f, 0.0f), new VertexLayersData(new SimpleTextureLayerVertexData<Vector2f>(new Vector2f(cMin, cMax))) )
						)
						), textureMappingConfig)));
				try {
					renderEngineBackend.load(mesh);
				} catch (DataLoadingErrorException | PlatformServiceManagerException e) {
					throw new RuntimeException(e);
				}
				final MeshObject3d<ColoredMeshData<Vector3f>> meshObject = new MeshObject3d<ColoredMeshData<Vector3f>>(mesh, meshObjectAffineMapper);
				
				meshLayersConfigs.add(textureMappingConfig);
				meshes.add(mesh);
				meshObjects.add(meshObject);
			}
		}
		
		final DynamicReference< MeshObject3d<ColoredMeshData<Vector3f>> > actualMeshObject = new DynamicReference< MeshObject3d<ColoredMeshData<Vector3f>> >();
		
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
						
						geometryRenderer.render(actualMeshObject.get());
					}
					
				};
			}

			@Override
			public SpaceGeometryRendererEmmiter<ColoredMeshData<Vector3f>> getColoredTransparentsEmmiter() {
				// TODO Auto-generated method stub
				return null;
			}

			@Override
			public LightsRendererEmmiter getLightsRendererEmmiter() {
				// TODO Auto-generated method stub
				return null;
			}
			
		};
		
		long refNanoTime = System.nanoTime();
		
		while ( !serviceManager.isCloseRequested() ) {
			final float t = (float) ((System.nanoTime() - refNanoTime) / 1000000000.0d);
			Matrix4f transformMatrix = Matrix4f.IDENTITY;
			transformMatrix = transformMatrix.translate(new Vector3f(0.0f, 0.0f, -5.0f * ( (float) Math.sin( t * 0.2f * Math.PI) + 1.0f) / 2.0f));
			transformMatrix = transformMatrix.rotate( t * 0.1f * (float) Math.PI, new Vector3f(0.0f, 0.0f, 1.0f));
			meshObjectAffineMapper.setTransformMatrix(transformMatrix);
			actualMeshObject.set(meshObjects.get( ((int) t) % meshObjects.size() ));
			
			renderEngineBackend.getScreenRenderingFrame().render(new RenderingFrameEmmiter(){

				@Override
				protected void render_internal(RenderingFrameRenderer renderer) {
					renderer.clear(new Vector4f(0.0f, 0.0f, 0.0f, 1.0f));
					renderer.render(spaceUniverseRendererEmmiter, camera);
				}
				
			});
			
			serviceManager.newFrame();
		}
		
		for ( Mesh<Vector3f, ColoredMeshData<Vector3f>> eachMesh : meshes ) {
			renderEngineBackend.unload(eachMesh);
		}
		
		for ( MeshLayersConfig eachTextureMappingConfig : meshLayersConfigs ) {
			renderEngineBackend.unload(eachTextureMappingConfig);
		}
		
		renderEngineBackend.unload(texture);
	}
}
