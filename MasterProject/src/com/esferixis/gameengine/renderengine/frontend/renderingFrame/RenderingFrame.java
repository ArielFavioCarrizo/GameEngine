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
package com.esferixis.gameengine.renderengine.frontend.renderingFrame;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;

import com.esferixis.gameengine.renderengine.backend.RenderEngineBackend;
import com.esferixis.gameengine.renderengine.backend.plane.PlaneObjectComponentRenderer;
import com.esferixis.gameengine.renderengine.frontend.RenderEngineFrontend;
import com.esferixis.gameengine.renderengine.frontend.RenderEngineFrontendLoadableObject;
import com.esferixis.gameengine.renderengine.frontend.implementation.meshLayers.MeshLayersConfigCore;
import com.esferixis.gameengine.renderengine.frontend.meshLayers.MeshLayersConfig;
import com.esferixis.gameengine.renderengine.frontend.meshLayers.MeshLayersConfigProfile;
import com.esferixis.gameengine.renderengine.frontend.misc.mesh.Mesh;
import com.esferixis.gameengine.renderengine.frontend.misc.mesh.colored.ColoredMeshData;
import com.esferixis.gameengine.renderengine.frontend.plane.staticstage.PlaneRendererEmmiter;
import com.esferixis.gameengine.renderengine.frontend.plane.staticstage.StaticPlaneObject;
import com.esferixis.gameengine.renderengine.frontend.plane.staticstage.StaticPlaneObjectRenderer;
import com.esferixis.gameengine.renderengine.frontend.plane.staticstage.mesh.StaticMeshObject2d;
import com.esferixis.gameengine.renderengine.frontend.plane.staticstage.shape.StaticShapeObject;
import com.esferixis.geometry.plane.finite.ConvexPolygon;
import com.esferixis.math.Vector2f;
import com.esferixis.math.Vector4f;
import com.esferixis.misc.accesor.AccesorHolder;
import com.esferixis.misc.accesor.AccesorWhiteList;

/**
 * Rectángulo de renderización
 * 
 * @author ariel
 *
 */
public final class RenderingFrame {
	private final RenderEngineFrontend renderEngineFrontend;
	private final com.esferixis.gameengine.renderengine.backend.renderingFrame.RenderingFrame backend;
	
	public static final ConvexPolygon SHAPE = new ConvexPolygon(
			new Vector2f(-1.0f, -1.0f),
			new Vector2f(1.0f, -1.0f),
			new Vector2f(1.0f, 1.0f),
			new Vector2f(-1.0f, 1.0f)
		);
	
	public static class Accesor {
		private Accesor() {}
		
		@AccesorWhiteList
		private static final Class<?>[] packageAccesors = new Class[] {
			com.esferixis.gameengine.renderengine.frontend.PackageAccesors.class
		};
		
		/**
		 * @post Crea el rectángulo de renderización con el frontend y el backend del
		 * 		 rectángulo de renderización especificado
		 */
		public RenderingFrame create(RenderEngineFrontend renderEngineFrontend, com.esferixis.gameengine.renderengine.backend.renderingFrame.RenderingFrame backend) {
			return new RenderingFrame(renderEngineFrontend, backend);
		}
	}
	
	/**
	 * @pre El backend no puede ser nulo
	 * @post Crea el rectángulo de renderización con el backend especificado
	 */
	private RenderingFrame(RenderEngineFrontend renderEngineFrontend, com.esferixis.gameengine.renderengine.backend.renderingFrame.RenderingFrame backend) {
		if ( ( renderEngineFrontend != null ) && ( backend != null ) ) {
			this.renderEngineFrontend = renderEngineFrontend;
			this.backend = backend;
		}
		else {
			throw new NullPointerException();
		}
	}
	
	/**
	 * @post Devuelve el ancho
	 */
	public int getWidth() {
		return this.backend.getWidth();
	}
	
	/**
	 * @post Devuelve el alto
	 */
	public int getHeight() {
		return this.backend.getHeight();
	}
	
	/**
	 * @pre El emisor de rendering no puede ser nulo
	 * @post Renderiza con el emisor de rendering especificado
	 */
	public final void render(final RenderingFrameEmmiter emmiter) {
		this.backend.render(new com.esferixis.gameengine.renderengine.backend.renderingFrame.RenderingFrameEmmiter() {

			@Override
			protected void render_internal(
					final com.esferixis.gameengine.renderengine.backend.renderingFrame.RenderingFrameRenderer backendRenderer) {
				emmiter.render(new RenderingFrameRenderer() {

					@Override
					public void clear(Vector4f color) {
						backendRenderer.clear(color);
					}

					@Override
					public void render(final PlaneRendererEmmiter planeRendererEmmiter) {
						backendRenderer.render(new com.esferixis.gameengine.renderengine.backend.plane.PlaneRendererEmmiter() {

							@Override
							protected void render_internal(final PlaneObjectComponentRenderer backendObjectRenderer) {
								planeRendererEmmiter.render(new StaticPlaneObjectRenderer() {
									private Mesh<?, ?> lastMesh = null;
									private MeshLayersConfigProfile lastMeshLayersConfigProfile = null;
									
									private com.esferixis.gameengine.renderengine.backend.misc.mesh.Mesh<Vector2f, com.esferixis.gameengine.renderengine.backend.misc.mesh.colored.ColoredMeshData<Vector2f>> lastMeshBackend = null;
									
									@Override
									public void render(final StaticPlaneObject planeObject) throws NullPointerException {
										planeObject.accept(new StaticPlaneObject.Visitor<Void, RuntimeException>() {

											@Override
											public Void visit(final StaticMeshObject2d meshObject2d) {
												final MeshLayersConfigCore meshLayersConfigCore = PackageAccesors.meshLayersConfigAccesor.get().getCore(meshObject2d.getMeshLayersConfigProfile().getMeshLayersConfig());
												
												if ( meshObject2d.getMeshLayersConfigProfile() != lastMeshLayersConfigProfile ) {
													PackageAccesors.meshLayersConfigProfileAccesor.get().prepareForUse(meshObject2d.getMeshLayersConfigProfile());
													lastMeshLayersConfigProfile = meshObject2d.getMeshLayersConfigProfile();
												}
												
												if ( ( meshObject2d.getMesh() != lastMesh ) || meshLayersConfigCore.getAttachedProfileDependenciesChangeFlagValue() ) {
													final Collection<RenderEngineFrontendLoadableObject> dependencies = new ArrayList<RenderEngineFrontendLoadableObject>(meshLayersConfigCore.getAttachedProfileDependencies().size()+1);
													dependencies.add(meshObject2d.getMesh());
													dependencies.addAll(meshLayersConfigCore.getAttachedProfileDependencies());
													
													PackageAccesors.renderEngineFrontendAccesor.get().prepareLoadableObjects(RenderingFrame.this.renderEngineFrontend, dependencies);
													
													meshLayersConfigCore.clearAttachedProfileDependenciesChangeFlag();
												}
												
												if ( meshObject2d.getMesh() != lastMesh ) {
													lastMesh = meshObject2d.getMesh();
													lastMeshBackend = (com.esferixis.gameengine.renderengine.backend.misc.mesh.Mesh<Vector2f, com.esferixis.gameengine.renderengine.backend.misc.mesh.colored.ColoredMeshData<Vector2f>>) PackageAccesors.meshAccesor.get().getCore(meshObject2d.getMesh()).getBackend();
												}
												
												backendObjectRenderer.render(new com.esferixis.gameengine.renderengine.backend.plane.mesh.MeshObject2d(lastMeshBackend, meshObject2d.getMapper()));
												return null;
											}

											@Override
											public Void visit(final StaticShapeObject shapeObject) {
												backendObjectRenderer.render(new com.esferixis.gameengine.renderengine.backend.plane.shape.ShapeObject(shapeObject.getShape(), shapeObject.getColorDistribution(), shapeObject.getMapper()));
												return null;
											}
										});
									}
									
								});
							}
							
						});
					}
					
				});
			}
			
		});
	}
}
