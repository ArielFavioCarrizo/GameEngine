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

package com.esferixis.gameengine.tests.renderengine.frontend.plane.kinematics;

import java.util.Arrays;

import com.esferixis.gameengine.frame.FrameManager;
import com.esferixis.gameengine.physics.plane.kinematics.mapper.PlaneKinematicMapper;
import com.esferixis.gameengine.physics.plane.kinematics.mapper.StaticAffinePlaneKinematicMapper;
import com.esferixis.gameengine.physics.plane.kinematics.mapper.TransformedPlaneKinematicMapper;
import com.esferixis.gameengine.physics.plane.kinematics.mapper.rotation.LinearRotation;
import com.esferixis.gameengine.physics.plane.kinematics.mapper.rotation.RotationPlaneKinematicMapper;
import com.esferixis.gameengine.physics.time.RootTemporalEventsEngine;
import com.esferixis.gameengine.platform.PlatformServiceManager;
import com.esferixis.gameengine.platform.PlatformServiceManagerException;
import com.esferixis.gameengine.platform.display.ScreenConfig;
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
import com.esferixis.gameengine.renderengine.frontend.plane.kinematics.InstantKinematicRenderer;
import com.esferixis.gameengine.renderengine.frontend.plane.kinematics.containers.KinematicRenderPlaneObjectContainer;
import com.esferixis.gameengine.renderengine.frontend.plane.kinematics.containers.LinearKinematicRenderPlaneObjectContainer;
import com.esferixis.gameengine.renderengine.frontend.plane.kinematics.containers.RenderizableKinematicRenderPlaneObjectContainer;
import com.esferixis.gameengine.renderengine.frontend.plane.kinematics.objects.KinematicMeshObject2d;
import com.esferixis.gameengine.renderengine.frontend.plane.staticstage.PlaneRendererEmmiter;
import com.esferixis.gameengine.renderengine.frontend.plane.staticstage.StaticPlaneObjectRenderer;
import com.esferixis.gameengine.renderengine.frontend.renderingFrame.RenderingFrame;
import com.esferixis.gameengine.renderengine.frontend.renderingFrame.RenderingFrameEmmiter;
import com.esferixis.gameengine.renderengine.frontend.renderingFrame.RenderingFrameRenderer;
import com.esferixis.gameengine.tests.GameEngineTestRunnable;
import com.esferixis.math.Matrix3f;
import com.esferixis.math.Vector2f;
import com.esferixis.math.Vector4f;
import com.esferixis.math.intervalarithmetic.FloatClosedInterval;
import com.esferixis.misc.ElementCallback;
import com.esferixis.misc.loader.MemoryLoader;
import com.esferixis.misc.reference.DynamicReference;

/**
 * Prueba de animación de rotación de triángulo
 * 
 * @author Ariel Favio Carrizo
 *
 */
public final class KinematicRenderPlaneObjectContainerTest extends GameEngineTestRunnable {
	public KinematicRenderPlaneObjectContainerTest() {
		super("Kinematic Render Plane Object Container Test (Frontend, kinematics)");
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
				
				final MeshLayersConfigProfile meshLayersConfigProfile = new MeshLayersConfigProfile(meshLayersConfig);
				
				final KinematicMeshObject2d meshObject1 = new KinematicMeshObject2d(mesh, meshLayersConfigProfile, new TransformedPlaneKinematicMapper<PlaneKinematicMapper, PlaneKinematicMapper>( new RotationPlaneKinematicMapper<LinearRotation>(new LinearRotation(0.0f, 0.0f, 0.5f * (float) Math.PI)), new StaticAffinePlaneKinematicMapper(Matrix3f.IDENTITY.scale(0.3f))));
				final KinematicMeshObject2d meshObject2 = new KinematicMeshObject2d(mesh, meshLayersConfigProfile, new TransformedPlaneKinematicMapper<PlaneKinematicMapper, PlaneKinematicMapper>( new RotationPlaneKinematicMapper<LinearRotation>(new LinearRotation(0.0f, 0.0f, 0.8f * (float) Math.PI)), new StaticAffinePlaneKinematicMapper(Matrix3f.IDENTITY.translate(new Vector2f(0.3f, 0.0f)).scale(0.3f))));
				
				final RenderizableKinematicRenderPlaneObjectContainer objectContainer = new LinearKinematicRenderPlaneObjectContainer();
				objectContainer.addObject(meshObject1);
				objectContainer.addObject(meshObject2);
				
				final RootTemporalEventsEngine temporalEventsEngine = new RootTemporalEventsEngine();
				
				final FrameManager frameManager = new FrameManager(temporalEventsEngine.getEventsManager(), 0.0f, 60) {
					/**
					 * 
					 */
					private static final long serialVersionUID = -5365045534778666771L;

					@Override
					protected void executeFrameTask(FloatClosedInterval timeInterval) {
						if ( serviceManager.isCloseRequested() ) {
							temporalEventsEngine.stop();
						}
					}

					@Override
					protected void render(final RenderingFrameRenderer renderer, final float time) {
						final PlaneRendererEmmiter planeRendererEmmiter = new PlaneRendererEmmiter() {

							@Override
							public void render_internal(StaticPlaneObjectRenderer staticObjectRenderer) {
								objectContainer.render(new InstantKinematicRenderer(staticObjectRenderer, time), RenderingFrame.SHAPE.castToAffine());
							}
							
						};
						
						renderer.clear(new Vector4f(0.0f, 0.0f, 0.0f, 1.0f));
						renderer.render(planeRendererEmmiter);
					}
					
				};
				
				frameManager.setPlatformServices(serviceManager, renderEngineFrontend);
				
				temporalEventsEngine.run();
			}
			
		});
	}
}
