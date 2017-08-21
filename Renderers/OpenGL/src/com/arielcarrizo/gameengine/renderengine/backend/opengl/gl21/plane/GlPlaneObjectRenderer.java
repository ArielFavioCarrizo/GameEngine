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
package com.arielcarrizo.gameengine.renderengine.backend.opengl.gl21.plane;

import com.arielcarrizo.gameengine.renderengine.backend.opengl.gl21.Gl21RenderEngineBackendObject;
import com.arielcarrizo.gameengine.renderengine.backend.opengl.gl21.Gl21RenderEngineBackendSystem;
import com.arielcarrizo.gameengine.renderengine.backend.opengl.gl21.globjects.shader.ShaderProgramContainer;
import com.arielcarrizo.gameengine.renderengine.backend.opengl.gl21.mesh.ColoredMeshBacker;
import com.arielcarrizo.gameengine.renderengine.backend.opengl.gl21.meshLayers.LayeredGeometryRendererCommon;
import com.arielcarrizo.gameengine.renderengine.backend.opengl.gl21.meshLayers.LayersRenderingShadersUnit;
import com.arielcarrizo.gameengine.renderengine.backend.opengl.gl21.meshLayers.LayersVertexDataBacker;
import com.esferixis.gameengine.physics.plane.statics.AffineMapper2d;
import com.esferixis.gameengine.physics.plane.statics.IdentityMapper2d;
import com.esferixis.gameengine.physics.plane.statics.PlaneMapper;
import com.esferixis.gameengine.physics.plane.statics.ProportionalAffineMapper2d;
import com.esferixis.gameengine.physics.plane.statics.ProportionalTransformedMapper2d;
import com.esferixis.gameengine.physics.plane.statics.TransformedMapper2d;
import com.esferixis.gameengine.renderengine.backend.misc.mesh.colored.ColoredMeshData;
import com.esferixis.gameengine.renderengine.backend.misc.mesh.colored.ColoredMeshVertex;
import com.esferixis.gameengine.renderengine.backend.plane.PlaneObjectComponent;
import com.esferixis.gameengine.renderengine.backend.plane.PlaneObjectComponentRenderer;
import com.esferixis.gameengine.renderengine.backend.plane.mesh.MeshObject2d;
import com.esferixis.gameengine.renderengine.backend.plane.shape.ShapeObject;
import com.esferixis.math.Matrix3f;
import com.esferixis.math.ProportionalMatrix3f;
import com.esferixis.math.Vector2f;
import com.esferixis.misc.ElementProcessor;

public final class GlPlaneObjectRenderer extends Gl21RenderEngineBackendObject implements PlaneObjectComponentRenderer {
	private final PlaneRenderingSubsystem planeRenderingSubsystem;
	
	private final LayeredGeometryRendererCommon<Vector2f, ColoredMeshVertex<Vector2f>, ColoredMeshData<Vector2f>, ColoredMeshBacker<Vector2f>, MeshObject2d> rendererCommon;
	
	/**
	 * @pre Ninguno de los parámetros puede ser nulo
	 * @post Crea el renderizador con el backend del motor de rendering especificado,
	 * 		 indicando si tiene haber escrituras en el Z-buffer
	 */
	GlPlaneObjectRenderer(PlaneRenderingSubsystem planeRenderingSubsystem) throws NullPointerException {
		super(planeRenderingSubsystem.getRenderEngineBackend());
		this.planeRenderingSubsystem = planeRenderingSubsystem;
		
		this.rendererCommon = new LayeredGeometryRendererCommon<Vector2f, ColoredMeshVertex<Vector2f>, ColoredMeshData<Vector2f>, ColoredMeshBacker<Vector2f>, MeshObject2d>(renderEngineBackend, new ElementProcessor< ColoredMeshBacker<Vector2f>, LayersVertexDataBacker >() {

			@Override
			public LayersVertexDataBacker process(ColoredMeshBacker<Vector2f> meshBacker) {
				return meshBacker.getLayersVertexDataBacker();
			}
			
		}, false) {

			@Override
			protected LayeredGeometryRendererCommon<Vector2f, ColoredMeshVertex<Vector2f>, ColoredMeshData<Vector2f>, ColoredMeshBacker<Vector2f>, MeshObject2d>.TransformManager createTransformManager(
					MeshObject2d meshObject) {
				
				final Matrix3f transformMatrix = meshObject.getMapper().accept(new PlaneMapper.Visitor<Matrix3f, RuntimeException>() {

					@Override
					public Matrix3f visit(ProportionalAffineMapper2d affineMapper) {
						return affineMapper.getTransformMatrix();
					}

					@Override
					public Matrix3f visit(IdentityMapper2d identityMapper) {
						return Matrix3f.IDENTITY;
					}

					@Override
					public Matrix3f visit(TransformedMapper2d transformedMapper) {
						return transformedMapper.getTransformerMapper().accept(this).mul(transformedMapper.getOriginalMapper().accept(this));
					}

					@Override
					public Matrix3f visit(AffineMapper2d affineMapper) throws RuntimeException {
						return affineMapper.getTransformMatrix();
					}

					@Override
					public Matrix3f visit(ProportionalTransformedMapper2d transformedMapper) throws RuntimeException {
						return transformedMapper.getTransformerMapper().accept(this).mul(transformedMapper.getOriginalMapper().accept(this));
					}
				});
				
				return new TransformManager() {
					@Override
					public void updateUniforms(
							com.arielcarrizo.gameengine.renderengine.backend.opengl.gl21.meshLayers.LayersRenderingShadersUnit.ShaderProgramContainer<Vector2f> shaderProgramContainer) {
						GlPlaneObjectRenderer.this.gl.glUniformMatrix3(shaderProgramContainer.getTransformMatrixUniform().getLocation(), false, transformMatrix.store());
					}
					
				};
			}

			@Override
			protected com.arielcarrizo.gameengine.renderengine.backend.opengl.gl21.meshLayers.LayersRenderingShadersUnit.ShaderProgramContainer<Vector2f> getShaderProgramContainer(
					LayersRenderingShadersUnit shadersUnit) {
				return shadersUnit.getShader2dProgramContainer();
			}
			
		};
	}
	
	
	/**
	 * @post Destruye el renderizador
	 */
	public void destroy() {
		this.rendererCommon.destroy();
	}


	/* (non-Javadoc)
	 * @see com.esferixis.gameengine.renderengine.backend.plane.PlaneObjectRenderer#render(com.esferixis.gameengine.renderengine.backend.plane.PlaneObject)
	 */
	@Override
	public void render(PlaneObjectComponent planeObject) {
		if ( planeObject != null ) {
			planeObject.visit(new PlaneObjectComponent.Visitor<Void>() {

				@Override
				public Void visit(MeshObject2d meshObject) {
					GlPlaneObjectRenderer.this.getRenderEngineBackend().getAttributeStreamManager().setAttributeStreamConfig(null);
					GlPlaneObjectRenderer.this.rendererCommon.render(meshObject);
					return null;
				}

				@Override
				public Void visit(ShapeObject shapeObject) {
					GlPlaneObjectRenderer.this.planeRenderingSubsystem.getShapeRenderingSubsystem().render(shapeObject);
					return null;
				}
			});
			
			this.getRenderEngineBackend().getAttributeStreamManager().setAttributeStreamConfig(null);
		}
		else {
			throw new NullPointerException();
		}
	}
}
