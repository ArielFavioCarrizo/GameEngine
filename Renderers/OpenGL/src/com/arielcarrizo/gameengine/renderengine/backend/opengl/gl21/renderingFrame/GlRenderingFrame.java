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
package com.arielcarrizo.gameengine.renderengine.backend.opengl.gl21.renderingFrame;

import com.arielcarrizo.gameengine.renderengine.backend.opengl.gl21.GL21;
import com.arielcarrizo.gameengine.renderengine.backend.opengl.gl21.Gl21RenderEngineBackendSystem;
import com.arielcarrizo.gameengine.renderengine.backend.opengl.gl21.mesh.ColoredMeshBacker;
import com.arielcarrizo.gameengine.renderengine.backend.opengl.gl21.meshLayers.LayersVertexDataBacker;
import com.arielcarrizo.gameengine.renderengine.backend.opengl.gl21.plane.GlPlaneObjectRenderer;
import com.arielcarrizo.gameengine.renderengine.backend.opengl.gl21.space.Layered3dGeometryRenderer;
import com.arielcarrizo.gameengine.renderengine.backend.opengl.gl21.universe.GL3dGeometryRenderer;
import com.esferixis.gameengine.renderengine.backend.misc.mesh.colored.ColoredMeshData;
import com.esferixis.gameengine.renderengine.backend.misc.mesh.colored.ColoredMeshVertex;
import com.esferixis.gameengine.renderengine.backend.plane.PlaneRendererEmmiter;
import com.esferixis.gameengine.renderengine.backend.renderingFrame.RenderingFrame;
import com.esferixis.gameengine.renderengine.backend.renderingFrame.RenderingFrameEmmiter;
import com.esferixis.gameengine.renderengine.backend.renderingFrame.RenderingFrameRenderer;
import com.esferixis.gameengine.renderengine.backend.space.universe.SpaceUniverseRendererEmmiter;
import com.esferixis.gameengine.renderengine.space.camera.Camera3d;
import com.esferixis.math.Matrix4f;
import com.esferixis.math.Vector3f;
import com.esferixis.math.Vector4f;
import com.esferixis.math.intervalarithmetic.FloatClosedInterval;
import com.esferixis.misc.ElementProcessor;

/**
 * Rectángulo de renderización del engine de OpenGL
 */
public abstract class GlRenderingFrame extends RenderingFrame {
	protected final Gl21RenderEngineBackendSystem renderEngineSystem;
	protected final GL21 gl;
	
	/**
	 * @pre El engine de rendering no puede ser nulo
	 * @post Crea el cuadro de renderización de pantalla con el sistema de backend de engine de rendering especificado
	 */
	public GlRenderingFrame(Gl21RenderEngineBackendSystem renderEngine) {
		if ( renderEngine != null ) {
			this.renderEngineSystem = renderEngine;
			this.gl = this.renderEngineSystem.getGL();
		}
		else {
			throw new NullPointerException();
		}
	}

	/* (non-Javadoc)
	 * @see com.esferixis.gameengine.renderengine.backend.renderingFrame.RenderingFrame#render(com.esferixis.gameengine.renderengine.backend.renderingFrame.RenderingFrameEmmiter)
	 */
	@Override
	public void render(RenderingFrameEmmiter emmiter) {
		this.gl.glViewport(0, 0, this.getWidth(), this.getHeight());
		
		if ( emmiter != null ) {
			gl.glEnable(GL21.GL_BLEND);
			
			emmiter.render(new RenderingFrameRenderer() {

				@Override
				public void render(SpaceUniverseRendererEmmiter spaceUniverseRendererEmmiter, Camera3d camera) {
					final GL21 gl = GlRenderingFrame.this.gl;
					
					gl.glEnable(GL21.GL_DEPTH_TEST);	
					gl.glEnable(GL21.GL_CULL_FACE);		
					gl.glDepthMask(true);
					gl.glDepthFunc(GL21.GL_LEQUAL);
					gl.glClearDepth(1.0f);
					
					final ElementProcessor<ColoredMeshBacker<Vector3f>, LayersVertexDataBacker> vertexLayersDataBackerReader = new ElementProcessor<ColoredMeshBacker<Vector3f>, LayersVertexDataBacker>() {

						@Override
						public LayersVertexDataBacker process(ColoredMeshBacker<Vector3f> meshBacker) {
							return meshBacker.getLayersVertexDataBacker();
						}
						
					};
										
					FloatClosedInterval[] zClippingInterval = spaceUniverseRendererEmmiter.prepare(camera).clone();
					
					for ( int i = 0 ; i < zClippingInterval.length ; i++ ) {
						gl.glClear(GL21.GL_DEPTH_BUFFER_BIT);
						
						// Renderizar triángulos opacos
						
						if ( spaceUniverseRendererEmmiter.getColoredOpaquesEmmiter() != null ) {
						
							final Matrix4f projectedViewTransformMatrix = camera.getLens().getProjectionMatrix(zClippingInterval[i]).mul(camera.getViewMatrix());
							
							GL3dGeometryRenderer<ColoredMeshData<Vector3f>> renderer = new Layered3dGeometryRenderer<ColoredMeshVertex<Vector3f>, ColoredMeshData<Vector3f>, ColoredMeshBacker<Vector3f>>(GlRenderingFrame.this.renderEngineSystem, vertexLayersDataBackerReader, projectedViewTransformMatrix, true);
							
							spaceUniverseRendererEmmiter.getColoredOpaquesEmmiter().render(projectedViewTransformMatrix, renderer);
							
							renderer.destroy();
						}
					}
					
					gl.glDisable(GL21.GL_DEPTH_TEST);
					gl.glDisable(GL21.GL_CULL_FACE);
				}

				@Override
				public void render(PlaneRendererEmmiter planeRendererEmmiter) {
					final GlPlaneObjectRenderer planeObjectRenderer = GlRenderingFrame.this.renderEngineSystem.getPlaneRenderingSubsystem().getPlaneObjectRenderer();
					planeRendererEmmiter.render(planeObjectRenderer);
					planeObjectRenderer.destroy();
				}

				@Override
				public void clear(Vector4f color) {
					final GL21 gl = GlRenderingFrame.this.gl;
					
					gl.glClearColor(color.getX(), color.getY(), color.getZ(), color.getW());
					gl.glClear(GL21.GL_COLOR_BUFFER_BIT);
				}
				
			});
			
			gl.glDisable(GL21.GL_BLEND);
		}
	}
	
	/**
	 * @post Selecciona el cuadro
	 */
	protected abstract void select();
}
