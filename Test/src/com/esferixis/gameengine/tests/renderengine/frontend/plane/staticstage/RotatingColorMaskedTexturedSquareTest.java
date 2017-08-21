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

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import com.esferixis.gameengine.physics.plane.statics.ProportionalAffineMapper2d;
import com.esferixis.gameengine.physics.space.statics.AffineMapper;
import com.esferixis.gameengine.platform.PlatformServiceManager;
import com.esferixis.gameengine.platform.PlatformServiceManagerException;
import com.esferixis.gameengine.platform.display.ScreenConfig;
import com.esferixis.gameengine.renderengine.frontend.RenderEngineFrontend;
import com.esferixis.gameengine.renderengine.frontend.RenderEngineFrontendConfiguration;
import com.esferixis.gameengine.renderengine.frontend.meshLayers.ColorLayerDataField;
import com.esferixis.gameengine.renderengine.frontend.meshLayers.LayerDataFieldValue;
import com.esferixis.gameengine.renderengine.frontend.meshLayers.MeshLayersConfig;
import com.esferixis.gameengine.renderengine.frontend.meshLayers.MeshLayersConfigProfile;
import com.esferixis.gameengine.renderengine.frontend.meshLayers.SimpleTextureLayer;
import com.esferixis.gameengine.renderengine.frontend.meshLayers.SimpleTextureLayerVertexData;
import com.esferixis.gameengine.renderengine.frontend.meshLayers.TextureObject2dLayerDataField;
import com.esferixis.gameengine.renderengine.frontend.meshLayers.VertexLayersData;
import com.esferixis.gameengine.renderengine.frontend.misc.mesh.Mesh;
import com.esferixis.gameengine.renderengine.frontend.misc.mesh.MeshTriangle;
import com.esferixis.gameengine.renderengine.frontend.misc.mesh.colored.ColoredMeshData;
import com.esferixis.gameengine.renderengine.frontend.misc.mesh.colored.ColoredMeshVertex;
import com.esferixis.gameengine.renderengine.frontend.plane.staticstage.PlaneRendererEmmiter;
import com.esferixis.gameengine.renderengine.frontend.plane.staticstage.StaticPlaneObjectRenderer;
import com.esferixis.gameengine.renderengine.frontend.plane.staticstage.mesh.StaticMeshObject2d;
import com.esferixis.gameengine.renderengine.frontend.renderingFrame.RenderingFrameEmmiter;
import com.esferixis.gameengine.renderengine.frontend.renderingFrame.RenderingFrameRenderer;
import com.esferixis.gameengine.renderengine.frontend.texture.Texture;
import com.esferixis.gameengine.renderengine.frontend.texture.TextureObject2d;
import com.esferixis.gameengine.renderengine.misc.colorObject.ColorObject;
import com.esferixis.gameengine.renderengine.picture.ProceduralPicture2d;
import com.esferixis.gameengine.renderengine.picture.RasterPicture2d;
import com.esferixis.gameengine.renderengine.space.camera.Camera3d;
import com.esferixis.gameengine.renderengine.texture.CoordinateWrap;
import com.esferixis.gameengine.renderengine.texture.TextureQualitySettings;
import com.esferixis.gameengine.renderengine.texture.TextureQualitySettings.MagFilter;
import com.esferixis.gameengine.renderengine.texture.TextureQualitySettings.MinFilter;
import com.esferixis.gameengine.tests.GameEngineTestRunnable;
import com.esferixis.math.Matrix3f;
import com.esferixis.math.Matrix4f;
import com.esferixis.math.ProportionalMatrix3f;
import com.esferixis.math.Vector2f;
import com.esferixis.math.Vector3f;
import com.esferixis.math.Vector4f;
import com.esferixis.math.intervalarithmetic.FloatClosedInterval;
import com.esferixis.misc.ElementCallback;
import com.esferixis.misc.loader.DataLoadingErrorException;
import com.esferixis.misc.loader.MemoryLoader;
import com.esferixis.misc.reference.DynamicReference;

/**
 * Prueba de animación de rotación de triángulo
 * 
 * @author Ariel Favio Carrizo
 *
 */
public final class RotatingColorMaskedTexturedSquareTest extends GameEngineTestRunnable {
	/**
	 * @param title
	 */
	public RotatingColorMaskedTexturedSquareTest() {
		super("Rotating color masked textured square test (Frontend, static phase)");
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
				final Texture<RasterPicture2d> texture;
				{
					final Matrix3f transformMatrix = Matrix3f.IDENTITY.translate(new Vector2f(-0.5f, -0.5f)).scale(2.0f);
					texture = new Texture<RasterPicture2d>( (new ProceduralPicture2d<Vector4f>() {
			
						@Override
						public Vector4f getValue(Vector2f position) {
							float value = 1.0f - transformMatrix.transformPoint(position).lengthSquared();
							
							if ( value < 0.0f ) value = 0.0f;
							
							return new Vector4f(1.0f, 1.0f, 1.0f, value);
						}
						
					}).createRasterLoader(256, 256) );
				}
				
				TextureQualitySettings textureQualitySettings = new TextureQualitySettings();
				textureQualitySettings.setMinFilter(MinFilter.LINEAR);
				textureQualitySettings.setMagFilter(MagFilter.LINEAR);
				
				final TextureObject2d textureObject;
				{
					final TextureObject2d.Essence essence = new TextureObject2d.Essence(texture, textureQualitySettings);
					essence.setCoordinateWrapS(CoordinateWrap.REPEAT);
					essence.setCoordinateWrapT(CoordinateWrap.REPEAT);
					textureObject = new TextureObject2d(essence);
				}
				
				final TextureObject2dLayerDataField textureObject2dLayerDataField = new TextureObject2dLayerDataField();
				final ColorLayerDataField maskColorLayerDataField = new ColorLayerDataField();
				
				final MeshLayersConfig meshLayersConfig = new MeshLayersConfig(new SimpleTextureLayer<RasterPicture2d>(textureObject2dLayerDataField, maskColorLayerDataField));
				
				final float cMin = -1.0f, cMax = 1.0f;
				
				final Mesh<Vector2f, ColoredMeshData<Vector2f>> mesh = new Mesh<Vector2f, ColoredMeshData<Vector2f>>(new MemoryLoader<ColoredMeshData<Vector2f>>( new ColoredMeshData<Vector2f>( Arrays.asList(
						new MeshTriangle<Vector2f, ColoredMeshVertex<Vector2f>>(
								new ColoredMeshVertex<Vector2f>( new Vector2f(1.0f, -1.0f), new VertexLayersData( new SimpleTextureLayerVertexData<Vector2f>(new Vector2f(cMax, cMin))) ),
								new ColoredMeshVertex<Vector2f>( new Vector2f(1.0f, 1.0f), new VertexLayersData(new SimpleTextureLayerVertexData<Vector2f>(new Vector2f(cMax, cMax))) ),
								new ColoredMeshVertex<Vector2f>( new Vector2f(-1.0f, -1.0f), new VertexLayersData(new SimpleTextureLayerVertexData<Vector2f>(new Vector2f(cMin, cMin))) )
						),
						new MeshTriangle<Vector2f, ColoredMeshVertex<Vector2f>>(
								new ColoredMeshVertex<Vector2f>( new Vector2f(-1.0f, -1.0f), new VertexLayersData(new SimpleTextureLayerVertexData<Vector2f>(new Vector2f(cMin, cMin))) ),
								new ColoredMeshVertex<Vector2f>( new Vector2f(1.0f, 1.0f), new VertexLayersData(new SimpleTextureLayerVertexData<Vector2f>(new Vector2f(cMax, cMax))) ),
								new ColoredMeshVertex<Vector2f>( new Vector2f(-1.0f, 1.0f), new VertexLayersData(new SimpleTextureLayerVertexData<Vector2f>(new Vector2f(cMin, cMax))) )
						)
						), meshLayersConfig)));
				
				final ColorObject maskColorObject = new ColorObject(new Vector4f(0.0f, 0.0f, 0.0f, 0.0f));
				
				final ProportionalAffineMapper2d meshObjectAffineMapper = new ProportionalAffineMapper2d();
				final MeshLayersConfigProfile meshLayersConfigProfile = new MeshLayersConfigProfile(meshLayersConfig, new LayerDataFieldValue<TextureObject2d>(textureObject2dLayerDataField, textureObject), new LayerDataFieldValue<ColorObject>(maskColorLayerDataField, maskColorObject));
				final StaticMeshObject2d meshObject = new StaticMeshObject2d(mesh, meshLayersConfigProfile, meshObjectAffineMapper);
				final ProportionalMatrix3f globalTransformMatrix = Matrix3f.IDENTITY.scale(0.5f);
				
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
					transformMatrix = transformMatrix.rotate( t * 0.1f * (float) Math.PI);
					meshObjectAffineMapper.setTransformMatrix(globalTransformMatrix.mul(transformMatrix));
					
					int frame = (int) t;
					
					maskColorObject.setColor(new Vector4f(((float) Math.sin(t * 2.0f * Math.PI / 2.0f) + 1.0f) / 2.0f, ((float) Math.sin(t * 2.0f * Math.PI / 1.0f) + 1.0f) / 2.0f, ((float) Math.sin(t * 2.0f * Math.PI / 2.0f) + 1.0f) / 2.0f, ((float) Math.sin(t * 2.0f * Math.PI / 4.0f) + 1.0f) / 2.0f));
					
					renderEngineFrontend.getScreenRenderingFrame().render(new RenderingFrameEmmiter(){

						@Override
						protected void render_internal(RenderingFrameRenderer renderer) {
							renderer.clear(new Vector4f(0.0f, 0.0f, 0.25f, 1.0f));
							renderer.render(planeRendererEmmiter);
						}
						
					});
					
					serviceManager.newFrame();
				}
			}
			
		});
	}
}
