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
package com.arielcarrizo.gameengine.renderengine.backend.opengl.gl21.meshLayers;

import java.util.Iterator;

import com.arielcarrizo.gameengine.renderengine.backend.opengl.gl21.Gl21RenderEngineBackendObject;
import com.arielcarrizo.gameengine.renderengine.backend.opengl.gl21.Gl21RenderEngineBackendSystem;
import com.arielcarrizo.gameengine.renderengine.backend.opengl.gl21.globjects.shader.ShaderProgramContainer;
import com.arielcarrizo.gameengine.renderengine.backend.opengl.gl21.mesh.MeshBacker;
import com.arielcarrizo.gameengine.renderengine.backend.opengl.gl21.meshLayers.LayersRenderingShadersUnit.TextureRenderLayer;
import com.arielcarrizo.gameengine.renderengine.backend.opengl.gl21.meshLayers.LayersRenderingShadersUnit.UniformColoredRenderLayer;
import com.arielcarrizo.gameengine.renderengine.backend.opengl.gl21.meshLayers.LayersRenderingShadersUnit.VertexColoredRenderLayer;
import com.arielcarrizo.gameengine.renderengine.backend.opengl.gl21.meshLayers.LayersVertexDataBacker.LayerVertexDataBacker;
import com.esferixis.gameengine.renderengine.backend.exception.MissingMeshLoadException;
import com.esferixis.gameengine.renderengine.backend.exception.MissingTextureLoadException;
import com.esferixis.gameengine.renderengine.backend.misc.mesh.Mesh;
import com.esferixis.gameengine.renderengine.backend.misc.mesh.MeshObject;
import com.esferixis.gameengine.renderengine.backend.misc.mesh.MeshTriangleVertex;
import com.esferixis.gameengine.renderengine.misc.colorObject.ColorObject;
import com.esferixis.math.Vectorf;
import com.esferixis.misc.ElementCallback;
import com.esferixis.misc.ElementProcessor;

public abstract class LayeredGeometryRendererCommon<V extends Vectorf, P extends MeshTriangleVertex<V>, D extends Mesh.Data<V, P>, B extends MeshBacker<V, P, D>, O extends MeshObject<V, D, ?> > extends Gl21RenderEngineBackendObject {
	private final ShaderProgramContainer.Switcher shaderProgramContainerSwitcher;
	private final ElementProcessor<B, LayersVertexDataBacker> vertexLayersDataBackerReader;
	private MeshLayersConfigBacker lastMeshLayersConfigBacker;
	
	private boolean activateDepthWrites;
	
	public abstract class TransformManager {
		/**
		 * @post Especifica valores a los uniforms asociados
		 */
		public abstract void updateUniforms(LayersRenderingShadersUnit.ShaderProgramContainer<V> shaderProgramContainer);
	}
	
	/**
	 * @post Crea la parte común del renderizador basado en layers, indicando el switcher de contenedores de programas de shaders, el lector de backers de datos de vértice de capa y
	 * 		 si tienen que activarse las escrituras al z-buffer
	 */
	public LayeredGeometryRendererCommon(Gl21RenderEngineBackendSystem renderEngineBackend, ElementProcessor<B, LayersVertexDataBacker> vertexLayersDataBackerReader, boolean activateDepthWrites) {
		super(renderEngineBackend);
		
		if ( vertexLayersDataBackerReader != null ) {
			this.vertexLayersDataBackerReader = vertexLayersDataBackerReader;
			
			this.shaderProgramContainerSwitcher = renderEngineBackend.getShaderProgramContainerSwitcher();
			this.lastMeshLayersConfigBacker = null;
			
			this.activateDepthWrites = activateDepthWrites;
		}
		else {
			throw new NullPointerException();
		}
	}
	
	/**
	 * @post Especifica si tiene que activar las escrituras al z-buffer
	 */
	public void setActivateDepthWrites(boolean value) {
		this.activateDepthWrites = value;
	}
	
	/**
	 * @post Devuelve si tiene que activar las escrituras al z-buffer
	 */
	public boolean getActivateDepthWrites() {
		return this.activateDepthWrites;
	}
	
	/**
	 * @post Crea el administrador de transformaciones con el objeto de malla especificado
	 */
	protected abstract TransformManager createTransformManager(O meshObject);
	
	/**
	 * @post Devuelve el contenedor de programa de shader con la unidad de
	 * 		 shader especificada
	 */
	protected abstract LayersRenderingShadersUnit.ShaderProgramContainer<V> getShaderProgramContainer(LayersRenderingShadersUnit shadersUnit);
	
	/**
	 * @pre El objeto de malla no puede ser nulo
	 * @post Renderiza con el objeto de rendering especificado
	 */
	public void render(O meshObject) throws NullPointerException, MissingMeshLoadException {
		if ( meshObject != null ) {
			
			final TransformManager transformManager = this.createTransformManager(meshObject);
			
			final B meshBacker = (B) this.getRenderEngineBackend().getMeshBacker( meshObject.getMesh() );
			
			final LayersVertexDataBacker layersVertexDataBacker = this.vertexLayersDataBackerReader.process(meshBacker);
			
			final MeshLayersConfigBacker meshLayersConfigBacker = layersVertexDataBacker.getTextureMappingConfigBacker();
						
			final Iterator< TextureObjectBacker<?, ?> > textureObjectBackersIterator = meshLayersConfigBacker.getTextureObjectBackers().iterator();
			final Iterator< LayerVertexDataBacker > layerMappingBackersIterator = layersVertexDataBacker.getLayerMappingBackers().iterator();
			final Iterator< ColorObject > colorObjectsIterator = meshLayersConfigBacker.getColorObjects().iterator();
			
			final boolean layersBlendingWithDepthWrites = ( meshLayersConfigBacker.getShadersUnits().size() != 1 ) && activateDepthWrites;
			
			int pendingUnits = meshLayersConfigBacker.getShadersUnits().size();
			
			// Si está en un estado sucio o no es el último "backer", revisa que todas las texturas estén cargadas
			if ( ( meshLayersConfigBacker != this.lastMeshLayersConfigBacker ) || meshLayersConfigBacker.isInDirtyState() ) {
				for ( TextureObjectBacker<?, ?> eachTextureObjectBacker : layersVertexDataBacker.getTextureMappingConfigBacker().getTextureObjectBackers() ) {
					if ( !eachTextureObjectBacker.getTextureBacker().isLoaded() ) {
						throw new MissingTextureLoadException();
					}
				}
				meshLayersConfigBacker.clearDirtyState();
			}
			
			if ( layersBlendingWithDepthWrites ) {
				// Desactivar escrituras al z-buffer
				this.gl.glDepthMask(false);
			}
			
			final boolean updateComponents = !( ( meshLayersConfigBacker.getShadersUnits().size() == 1 ) && ( meshLayersConfigBacker == this.lastMeshLayersConfigBacker ));
			
			for ( LayersRenderingShadersUnit eachShadersUnit : meshLayersConfigBacker.getShadersUnits() ) {
				final LayersRenderingShadersUnit.ShaderProgramContainer<V> shaderProgramContainer = this.getShaderProgramContainer(eachShadersUnit);
				
				this.shaderProgramContainerSwitcher.select(shaderProgramContainer);
				
				/**
				 * Si las escrituras al z-buffer fueron desactivadas, activarlas en
				 * la última unidad de shaders
				 */
				if ( layersBlendingWithDepthWrites && ( pendingUnits == 0 ) ) {
					this.gl.glDepthMask(true);
				}
				
				transformManager.updateUniforms(shaderProgramContainer);
				
				// Asignar parámetros a las capas de renderizado, si es necesario
				if ( updateComponents ) {
					for ( LayersRenderingShadersUnit.RenderLayer<V, ?> eachRenderLayer : shaderProgramContainer.getRenderLayers() ) {
						eachRenderLayer.visit(new LayersRenderingShadersUnit.RenderLayerVisitor<V, Void>() {

							@Override
							public Void visit(TextureRenderLayer<V> eachRenderLayer) {
								{
									final TextureObjectBacker<V, ?> eachTextureObjectBacker = (TextureObjectBacker<V, ?>) textureObjectBackersIterator.next();
									
									eachTextureObjectBacker.setTo(eachRenderLayer.getColorTexture());
								}
								
								if ( eachRenderLayer.getAlphaTexture() != null ) {
									final TextureObjectBacker<V, ?> eachTextureObjectBacker = (TextureObjectBacker<V, ?>) textureObjectBackersIterator.next();
									
									eachTextureObjectBacker.setTo(eachRenderLayer.getAlphaTexture());
								}
								
								return null;
							}

							@Override
							public Void visit(VertexColoredRenderLayer<V> renderLayer) {
								return null;
							}

							@Override
							public Void visit(UniformColoredRenderLayer<V> renderLayer) {
								return null;
							}
						});
						
						if ( eachRenderLayer.getProfileLayer().getHasMaskColor() ) {
							eachRenderLayer.setMaskColor(colorObjectsIterator.next().getColor());
						}
					}
				}
											
				meshBacker.draw(new ElementCallback< MeshBacker<V, P, D>.AttributeSender>() {
				
					@Override
					public void run(
							final MeshBacker<V, P, D>.AttributeSender attributeSender) {
						// Enviar posición de vértices
						attributeSender.send(meshBacker.getPositionAttribute(), shaderProgramContainer.getVertexPositionAttribute().getLocation());
						
						for ( LayersRenderingShadersUnit.RenderLayer<V, ?> eachRenderLayer : shaderProgramContainer.getRenderLayers() ) {
							eachRenderLayer.sendAttributes(layerMappingBackersIterator.next(), attributeSender);
						}
					}
				
				});
			}
			
			this.lastMeshLayersConfigBacker = meshLayersConfigBacker;
		}
		else {
			throw new NullPointerException();
		}
	}
	
	/**
	 * @post Destruye la parte común
	 */
	public void destroy() {
		
	}
}

