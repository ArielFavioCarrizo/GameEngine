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
package com.arielcarrizo.gameengine.renderengine.backend.opengl.gl21.space;

import com.arielcarrizo.gameengine.renderengine.backend.opengl.gl21.Gl21RenderEngineBackendSystem;
import com.arielcarrizo.gameengine.renderengine.backend.opengl.gl21.mesh.MeshBacker;
import com.arielcarrizo.gameengine.renderengine.backend.opengl.gl21.meshLayers.LayeredGeometryRendererCommon;
import com.arielcarrizo.gameengine.renderengine.backend.opengl.gl21.meshLayers.LayersRenderingShadersUnit;
import com.arielcarrizo.gameengine.renderengine.backend.opengl.gl21.meshLayers.LayersVertexDataBacker;
import com.arielcarrizo.gameengine.renderengine.backend.opengl.gl21.meshLayers.LayeredGeometryRendererCommon.TransformManager;
import com.arielcarrizo.gameengine.renderengine.backend.opengl.gl21.meshLayers.LayersRenderingShadersUnit.ShaderProgramContainer;
import com.arielcarrizo.gameengine.renderengine.backend.opengl.gl21.universe.GL3dGeometryRenderer;
import com.esferixis.gameengine.physics.plane.statics.IdentityMapper2d;
import com.esferixis.gameengine.physics.plane.statics.PlaneMapper;
import com.esferixis.gameengine.physics.plane.statics.ProportionalAffineMapper2d;
import com.esferixis.gameengine.physics.plane.statics.TransformedMapper2d;
import com.esferixis.gameengine.physics.space.statics.AffineMapper;
import com.esferixis.gameengine.physics.space.statics.IdentityMapper;
import com.esferixis.gameengine.physics.space.statics.SpatialMapper;
import com.esferixis.gameengine.physics.space.statics.TransformedMapper;
import com.esferixis.gameengine.renderengine.backend.exception.MissingMeshLoadException;
import com.esferixis.gameengine.renderengine.backend.misc.mesh.Mesh;
import com.esferixis.gameengine.renderengine.backend.misc.mesh.MeshObject;
import com.esferixis.gameengine.renderengine.backend.misc.mesh.MeshTriangleVertex;
import com.esferixis.gameengine.renderengine.backend.space.mesh.MeshObject3d;
import com.esferixis.math.Matrix3f;
import com.esferixis.math.Matrix4f;
import com.esferixis.math.Vector3f;
import com.esferixis.misc.ElementProcessor;
import com.esferixis.misc.exception.NotImplementedException;

public final class Layered3dGeometryRenderer<P extends MeshTriangleVertex<Vector3f>, D extends Mesh.Data<Vector3f, P>, M extends MeshBacker<Vector3f, P, D> > extends GL3dGeometryRenderer<D> {
	public static final class Factory<P extends MeshTriangleVertex<Vector3f>, D extends Mesh.Data<Vector3f, P>, M extends MeshBacker<Vector3f, P, D> > extends GL3dGeometryRenderer.Factory<D> {
		private final ElementProcessor<M, LayersVertexDataBacker> vertexLayersDataBackerReader;
		
		/**
		 * @pre El sistema de backend de renderización y el lector de datos de vértices de capas no puede ser nulo
		 * @post Crea la fábrica con el sistema de backend de renderización y el lector de datos de vértices de capas
		 * 		 especificados
		 */
		public Factory(Gl21RenderEngineBackendSystem renderEngineBackendSystem, ElementProcessor<M, LayersVertexDataBacker> vertexLayersDataBackerReader, boolean activateDepthWrites) {
			super(renderEngineBackendSystem, activateDepthWrites);
			
			if ( vertexLayersDataBackerReader != null ) {
				this.vertexLayersDataBackerReader = vertexLayersDataBackerReader;
			}
			else {
				throw new NullPointerException();
			}
		}

		/* (non-Javadoc)
		 * @see com.esferixis.gameengine.renderengine.backend.opengl.gl21.universe.GL3dGeometryRenderer.Factory#create(com.esferixis.math.Matrix4f, boolean)
		 */
		@Override
		public GL3dGeometryRenderer<D> create(Matrix4f projectedViewTransformMatrix) {
			return new Layered3dGeometryRenderer<P, D, M>(this.renderEngineBackend, this.vertexLayersDataBackerReader, projectedViewTransformMatrix, this.activateDepthWrites);
		}
		
	}
	
	private final LayeredGeometryRendererCommon<Vector3f, P, D, M, MeshObject3d<D>> rendererCommon;
	
	private final Matrix4f projectedViewTransformMatrix;
	
	/**
	 * @pre Ninguno de los parámetros puede ser nulo
	 * @post Crea el renderizador con el backend del motor de rendering, el lector de datos de vértices de capas,
	 * 		 la matriz de transformación de proyección de vista y la indicación de si tiene que escribir al z-buffer especificados
	 * @param renderEngineBackend, vertexLayersDataReader
	 */
	public Layered3dGeometryRenderer(Gl21RenderEngineBackendSystem renderEngineBackend, ElementProcessor<M, LayersVertexDataBacker> vertexLayersDataBackerReader, Matrix4f projectedViewTransformMatrix, boolean activateDepthWrites) throws NullPointerException {
		super(renderEngineBackend);
		if ( ( vertexLayersDataBackerReader != null ) && ( projectedViewTransformMatrix != null ) ) {
			this.projectedViewTransformMatrix = projectedViewTransformMatrix;
			
			this.rendererCommon = new LayeredGeometryRendererCommon<Vector3f, P, D, M, MeshObject3d<D>>(renderEngineBackend, vertexLayersDataBackerReader, activateDepthWrites) {

				@Override
				protected LayeredGeometryRendererCommon<Vector3f, P, D, M, MeshObject3d<D>>.TransformManager createTransformManager(
						MeshObject3d<D> meshObject) {
					final Matrix4f transformMatrix = meshObject.getMapper().accept(new SpatialMapper.Visitor<Matrix4f>() {

						@Override
						public Matrix4f visit(AffineMapper affineMapper) {
							return affineMapper.getTransformMatrix();
						}

						@Override
						public Matrix4f visit(IdentityMapper identityMapper) {
							return Matrix4f.IDENTITY;
						}

						@Override
						public Matrix4f visit(TransformedMapper transformedMapper) {
							return transformedMapper.getTransformerMapper().accept(this).mul(transformedMapper.getOriginalMapper().accept(this));
						}
					});
					
					return new TransformManager() {

						@Override
						public void updateUniforms(
								com.arielcarrizo.gameengine.renderengine.backend.opengl.gl21.meshLayers.LayersRenderingShadersUnit.ShaderProgramContainer<Vector3f> shaderProgramContainer) {
							Layered3dGeometryRenderer.this.gl.glUniformMatrix4(shaderProgramContainer.getTransformMatrixUniform().getLocation(), false, transformMatrix.store());
						}
						
					};
				}

				@Override
				protected com.arielcarrizo.gameengine.renderengine.backend.opengl.gl21.meshLayers.LayersRenderingShadersUnit.ShaderProgramContainer<Vector3f> getShaderProgramContainer(
						LayersRenderingShadersUnit shadersUnit) {
					return shadersUnit.getShader3dProgramContainer();
				}
				
			};
		}
		else {
			throw new NullPointerException();
		}
	}
	
	/**
	 * @post Especifica si tiene que activar las escrituras al z-buffer
	 */
	public void setActivateDepthWrites(boolean value) {
		this.rendererCommon.setActivateDepthWrites(value);
	}
	
	/**
	 * @post Devuelve si tiene que activar las escrituras al z-buffer
	 */
	public boolean getActivateDepthWrites() {
		return this.rendererCommon.getActivateDepthWrites();
	}
	
	/* (non-Javadoc)
	 * @see com.esferixis.gameengine.renderengine.backend.space.mesh.GeometryRenderer3d#render(com.esferixis.gameengine.renderengine.backend.space.mesh.MeshObject3d)
	 */
	@Override
	public void render(MeshObject3d<D> meshObject) throws NullPointerException, MissingMeshLoadException {
		this.rendererCommon.render(new MeshObject3d<D>(meshObject.getMesh(), new TransformedMapper(meshObject.getMapper(), new AffineMapper(this.projectedViewTransformMatrix))));
	}
	
	
	/**
	 * @post Destruye el render
	 */
	@Override
	public void destroy() {
		this.rendererCommon.destroy();
	}
}
