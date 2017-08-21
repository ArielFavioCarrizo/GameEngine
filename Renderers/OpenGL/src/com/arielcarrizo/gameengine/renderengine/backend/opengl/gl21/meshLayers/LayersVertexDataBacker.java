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

import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import com.arielcarrizo.gameengine.renderengine.backend.opengl.GLException;
import com.arielcarrizo.gameengine.renderengine.backend.opengl.gl21.GL21;
import com.arielcarrizo.gameengine.renderengine.backend.opengl.gl21.GLObject;
import com.arielcarrizo.gameengine.renderengine.backend.opengl.gl21.Gl21RenderEngineBackendObject;
import com.arielcarrizo.gameengine.renderengine.backend.opengl.gl21.Gl21RenderEngineBackendSystem;
import com.arielcarrizo.gameengine.renderengine.backend.opengl.gl21.mesh.GLAttribute;
import com.arielcarrizo.gameengine.renderengine.backend.opengl.gl21.mesh.Vector2fPropertyFloatBufferAttributeVboGenerator;
import com.arielcarrizo.gameengine.renderengine.backend.opengl.gl21.mesh.Vector4fPropertyFloatBufferAttributeVboGenerator;
import com.arielcarrizo.gameengine.renderengine.backend.opengl.gl21.mesh.GLAttribute.Manager;
import com.esferixis.gameengine.renderengine.backend.exception.MissingTextureMappingConfigLoadException;
import com.esferixis.gameengine.renderengine.backend.exception.OutOfMemoryException;
import com.esferixis.gameengine.renderengine.backend.meshLayers.MeshLayer;
import com.esferixis.gameengine.renderengine.backend.meshLayers.MeshLayersConfig;
import com.esferixis.gameengine.renderengine.backend.meshLayers.SimpleTextureLayer;
import com.esferixis.gameengine.renderengine.backend.meshLayers.SimpleTextureLayerVertexData;
import com.esferixis.gameengine.renderengine.backend.meshLayers.UniformColoredMeshLayer;
import com.esferixis.gameengine.renderengine.backend.meshLayers.VertexColoredMeshLayer;
import com.esferixis.gameengine.renderengine.backend.meshLayers.VertexColoredMeshLayerVertexData;
import com.esferixis.gameengine.renderengine.backend.meshLayers.VertexLayersData;
import com.esferixis.gameengine.renderengine.backend.misc.mesh.Mesh;
import com.esferixis.gameengine.renderengine.backend.misc.mesh.MeshTriangleVertex;
import com.esferixis.gameengine.renderengine.backend.texture.TextureObject;
import com.esferixis.gameengine.renderengine.backend.texture.TextureObject2d;
import com.esferixis.gameengine.renderengine.backend.texture.TextureObject3d;
import com.esferixis.math.Vector2f;
import com.esferixis.math.Vector4f;
import com.esferixis.math.Vectorf;
import com.esferixis.misc.ElementProcessor;
import com.esferixis.misc.counter.OverflowCounterException;

public final class LayersVertexDataBacker extends Gl21RenderEngineBackendObject {
	private final MeshLayersConfigBacker textureMappingConfigBacker;
	
	public abstract class LayerVertexDataBacker {
		protected abstract void destroy();
	}
	
	public final class UniformColoredLayerVertexDataBacker extends LayerVertexDataBacker {

		/* (non-Javadoc)
		 * @see com.esferixis.gameengine.renderengine.backend.opengl.gl21.meshLayers.LayersVertexDataBacker.LayerVertexDataBacker#destroy()
		 */
		@Override
		protected void destroy() {
			
		}
		
	}
	
	public final class VertexColoredLayerVertexDataBacker extends LayerVertexDataBacker {
		private GLAttribute.Manager colorAttributeManager;

		/**
		 * @pre El atributo de color no puede ser nulo
		 * @post Crea el backer con el atributo de color especificado
		 */
		private VertexColoredLayerVertexDataBacker(GLAttribute.Manager colorAttributeManager) {
			if ( colorAttributeManager != null ) {
				this.colorAttributeManager = colorAttributeManager;
			}
			else {
				throw new NullPointerException();
			}
		}
		
		/**
		 * @post Devuelve el atributo de color
		 */
		public GLAttribute getColorAttribute() {
			return this.colorAttributeManager.getAttribute();
		}
		
		
		/* (non-Javadoc)
		 * @see com.esferixis.gameengine.renderengine.backend.opengl.gl21.texture.LayerMappingBacker.LayerDataBacker#destroy()
		 */
		@Override
		protected void destroy() {
			this.colorAttributeManager.destroy();
		}
		
	}
	
	public final class TextureLayerVertexDataBacker extends LayerVertexDataBacker {
		private final GLAttribute.Manager alphaCoordinatesAttributeManager, colorCoordinatesAttributeManager;
		
		/**
		 * @pre El atributo de coordenadas de color no puede ser nulo
		 * @post Crea el backer con el atributo de coordenadas alfa y el de color especificados
		 * 		 El de alfa es opcional
		 * @param alphaCoordinatesAttribute
		 * @param colorCoordinatesAttribute
		 */
		private TextureLayerVertexDataBacker(GLAttribute.Manager alphaCoordinatesAttributeManager, GLAttribute.Manager colorCoordinatesAttributeManager) {
			if ( colorCoordinatesAttributeManager != null ) {
				this.colorCoordinatesAttributeManager = colorCoordinatesAttributeManager;
				this.alphaCoordinatesAttributeManager = alphaCoordinatesAttributeManager;
			}
			else {
				throw new NullPointerException();
			}
		}
		
		/**
		 * @post Crea el backer con el atributo de coordenadas de color especificado
		 * @param alphaCoordinatesAttribute
		 * @param colorCoordinatesAttribute
		 */
		private TextureLayerVertexDataBacker(GLAttribute.Manager colorCoordinatesAttribute) {
			this(null, colorCoordinatesAttribute);
		}
		
		/**
		 * @post Devuelve el atributo de coordenadas de textura alfa
		 */
		public GLAttribute getAlphaCoordinatesAttribute() {
			LayersVertexDataBacker.this.checkDestroyed();
			return this.alphaCoordinatesAttributeManager.getAttribute();
		}
		
		/**
		 * @post Devuelve el atributo de coordenadas de textura de color
		 */
		public GLAttribute getColorCoordinatesAttribute() {
			LayersVertexDataBacker.this.checkDestroyed();
			return this.colorCoordinatesAttributeManager.getAttribute();
		}
		
		@Override
		protected void destroy() {
			if ( this.alphaCoordinatesAttributeManager != null ) {
				this.alphaCoordinatesAttributeManager.destroy();
			}
			
			this.colorCoordinatesAttributeManager.destroy();
		}
	}
	
	private final LayerVertexDataBacker[] layerDataBackers;
	
	private boolean isDestroyed;
	
	/**
	 * @post Crea el "backer" del mapeo con la configuración de mapeo,
	 * 		 la malla y el extractor de mapeos de puntos especificado
	 */
	public <V extends Vectorf, M extends Mesh.Data<V, P>, P extends MeshTriangleVertex<V>> LayersVertexDataBacker(final Gl21RenderEngineBackendSystem renderEngineBackend, final MeshLayersConfig textureMappingConfig, final M meshData, final ElementProcessor<P, VertexLayersData> vertexLayersDataReader) throws GLException, OutOfMemoryException {
		super(renderEngineBackend);
		if ( ( textureMappingConfig != null ) && ( meshData != null ) && ( vertexLayersDataReader != null ) ) {
			this.isDestroyed = false;
			
			this.textureMappingConfigBacker = this.renderEngineBackend.getMeshLayersConfigBacker(textureMappingConfig);
			this.layerDataBackers = new LayerVertexDataBacker[textureMappingConfig.getLayers().size()];
			
			int i = 0;
			
			for ( MeshLayer<?> eachLayer : textureMappingConfig.getLayers() ) {
				final int layerIndex = i;
				try {
				this.layerDataBackers[i++] = eachLayer.accept(new MeshLayer.Visitor<LayerVertexDataBacker, Exception>() {

					@Override
					public LayerVertexDataBacker visit(SimpleTextureLayer<?> layer) throws Exception {
						final GLAttribute.Manager colorCoordinatesAttributeManager = layer.getTextureObject().visit(new TextureObject.Visitor<GLAttribute.Manager>() {

							@Override
							public Manager visit(TextureObject2d textureObject) throws Exception {
								return GLAttribute.create(gl, meshData, new Vector2fPropertyFloatBufferAttributeVboGenerator(new ElementProcessor<P, Vector2f>() {

									@Override
									public Vector2f process(P point) {
										VertexLayersData vertexLayersData = vertexLayersDataReader.process(point);
										return ( (SimpleTextureLayerVertexData<Vector2f>) vertexLayersData.getLayersData().get(layerIndex) ).getPosition();
									}
									
								}));
							}

							@Override
							public Manager visit(TextureObject3d textureObject) throws Exception {
								// TODO Auto-generated method stub
								return null;
							}
						});
						
						return new TextureLayerVertexDataBacker(colorCoordinatesAttributeManager);
					}

					@Override
					public LayerVertexDataBacker visit(VertexColoredMeshLayer layer) throws Exception {
						final GLAttribute.Manager vertexColorAttributeManager = GLAttribute.create(gl, meshData, new Vector4fPropertyFloatBufferAttributeVboGenerator(new ElementProcessor<P, Vector4f>() {

							@Override
							public Vector4f process(P point) {
								VertexLayersData vertexLayersData = vertexLayersDataReader.process(point);
								return ( (VertexColoredMeshLayerVertexData) vertexLayersData.getLayersData().get(layerIndex) ).getColor();
							}
							
						}));
						
						return new VertexColoredLayerVertexDataBacker(vertexColorAttributeManager);
					}

					@Override
					public LayerVertexDataBacker visit(UniformColoredMeshLayer layer) throws Exception {
						return new UniformColoredLayerVertexDataBacker();
					}
				});
				}
				catch (GLException | RuntimeException e) {
					throw e;
				} catch ( Exception e) {
					throw new RuntimeException("Unexpected exception", e);
				}
			}
			
			// Contar dependencia
			try {
				this.textureMappingConfigBacker.getDependencyCounter().increment();
			} catch (OverflowCounterException e) {
				throw new OutOfMemoryException();
			}
		}
		else {
			throw new NullPointerException();
		}
	}
	
	/**
	 * @post Devuelve una lista inmodificable de los "backers" de los datos de capas
	 */
	public List<LayerVertexDataBacker> getLayerMappingBackers() {
		this.checkDestroyed();
		return Collections.unmodifiableList(Arrays.asList(this.layerDataBackers));
	}
	
	/**
	 * @post Devuelve el backer de la configuración de mapeo de textura
	 */
	public MeshLayersConfigBacker getTextureMappingConfigBacker() {
		return this.textureMappingConfigBacker;
	}
	
	/**
	 * @post Verifica que no haya sido destruido
	 */
	private void checkDestroyed() throws IllegalStateException {
		if ( this.isDestroyed ) {
			throw new IllegalStateException("Cannot use an destroyed texture mapping backer");
		}
	}
	
	/**
	 * @post Destruye el "backer"
	 */
	public void destroy() {
		this.checkDestroyed();
		this.isDestroyed = true;
		
		for ( LayerVertexDataBacker eachLayerMappingBacker : this.layerDataBackers ) {
			eachLayerMappingBacker.destroy();
		}
		
		this.textureMappingConfigBacker.getDependencyCounter().decrement();
	}
}
