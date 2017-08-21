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

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.NoSuchElementException;

import com.esferixis.gameengine.renderengine.backend.meshLayers.MeshLayer;
import com.esferixis.gameengine.renderengine.backend.meshLayers.SimpleTextureLayer;
import com.esferixis.gameengine.renderengine.backend.meshLayers.UniformColoredMeshLayer;
import com.esferixis.gameengine.renderengine.backend.meshLayers.VertexColoredMeshLayer;
import com.esferixis.gameengine.renderengine.backend.texture.TextureObject;
import com.esferixis.gameengine.renderengine.backend.texture.TextureObject2d;
import com.esferixis.gameengine.renderengine.backend.texture.TextureObject3d;

public class LayersRenderingShaderProfile {
	public static final class TextureLayer extends Layer {
		public enum Type {
			TEXTURE3D("vec3", "sampler3D"),
			TEXTURE2D("vec2", "sampler2D");
			
			private final String coordinatesGLType;
			private final String samplerType;
			
			private Type(String coordinatesGLType, String samplerType) {
				this.coordinatesGLType = coordinatesGLType;
				this.samplerType = samplerType;
			}
			
			/**
			 * @post Devuelve el tipo GL de atributo
			 */
			public String getAttributeGLType() {
				return this.coordinatesGLType;
			}
			
			/**
			 * @post Devuelve el tipo de sampler
			 */
			public String getSamplerType() {
				return this.samplerType;
			}
		}
		
		private final Type type;
		private final boolean hasAlphaTexture;
		
		/**
		 * @post Crea la capa con el tipo indicando si tiene textura "alpha", y un color de máscara
		 */
		public TextureLayer(Type type, boolean hasAlphaTexture, boolean hasMaskColor) {
			super(hasMaskColor);
			this.type = type;
			this.hasAlphaTexture = hasAlphaTexture;
		}
		
		/**
		 * @post Devuelve el tipo
		 */
		public Type getType() {
			return this.type;
		}
		
		/**
		 * @post Devuelve si tiene textura "alpha"
		 */
		public boolean getHasAlphaTexture() {
			return this.hasAlphaTexture;
		}
		
		/**
		 * @post Devuelve si tiene un color de máscara
		 * @return
		 */
		public boolean getHasMaskColor() {
			return this.hasMaskColor;
		}

		/* (non-Javadoc)
		 * @see com.esferixis.gameengine.renderengine.backend.opengl.gl21.texture.TextureMappingShaderProfile.Layer#getRequiredVertexAttributes()
		 */
		@Override
		public int getRequiredVertexAttributes() {
			return 1;
		}
		
		/* (non-Javadoc)
		 * @see com.esferixis.gameengine.renderengine.backend.opengl.gl21.texture.TextureMappingShaderProfile.Layer#getRequiredVaryings()
		 */
		@Override
		public int getRequiredVaryings() {
			return this.hasAlphaTexture ? 2 : 1;
		}
		
		/* (non-Javadoc)
		 * @see com.esferixis.gameengine.renderengine.backend.opengl.gl21.texture.TextureMappingShaderProfile.Layer#getRequiredSamplers()
		 */
		@Override
		public int getRequiredSamplers() {
			if ( this.type.getSamplerType() != "" ) {
				return this.hasAlphaTexture ? 2 : 1;
			}
			else {
				return 0;
			}
		}
		
		/* (non-Javadoc)
		 * @see com.esferixis.gameengine.renderengine.backend.opengl.gl21.texture.TextureMappingShaderProfile.Layer#getRequiredVertexUniformComponents()
		 */
		@Override
		public int getRequiredVertexUniformComponents() {
			return 4;
		}
		
		private int getMirrorCoordinatesQuantity() {
			return (this.hasAlphaTexture ? 2 : 1) * ((this.type == Type.TEXTURE3D) ? 3 : 2);
		}
		
		/* (non-Javadoc)
		 * @see com.esferixis.gameengine.renderengine.backend.opengl.gl21.texture.TextureMappingShaderProfile.Layer#getRequiredFragmentUniformComponents_i1()
		 */
		@Override
		public int getRequiredFragmentUniformComponents_i1() {
			return this.getMirrorCoordinatesQuantity();
		}
		
		/**
		 * @post Devuelve si es igual al objeto especificado
		 */
		@Override
		public boolean equals(Object other) {
			if ( ( other != null ) && ( other instanceof TextureLayer ) ) {
				TextureLayer otherLayer = (TextureLayer) other;
				return (otherLayer.type.equals(this.type) && (otherLayer.hasAlphaTexture == this.hasAlphaTexture));
			}
			else {
				return false;
			}
		}
		
		/**
		 * @post Devuelve el hash
		 */
		@Override
		public int hashCode() {
			return this.getClass().hashCode() + 31 * this.type.hashCode() + 31 * 31 * Boolean.valueOf(this.hasAlphaTexture).hashCode();
		}

		/* (non-Javadoc)
		 * @see com.esferixis.gameengine.renderengine.backend.opengl.gl21.texture.TextureMappingShaderProfile.Layer#accept(com.esferixis.gameengine.renderengine.backend.opengl.gl21.texture.TextureMappingShaderProfile.Layer.Visitor)
		 */
		@Override
		public <V> V accept(Visitor<V> visitor) {
			return visitor.visit(this);
		}
	}
	
	public static final class VertexColoredLayer extends Layer {
		/**
		 * @param hasMaskColor
		 */
		public VertexColoredLayer(boolean hasMaskColor) {
			super(hasMaskColor);
		}

		/* (non-Javadoc)
		 * @see com.esferixis.gameengine.renderengine.backend.opengl.gl21.texture.TextureMappingShaderProfile.Layer#getRequiredVertexAttributes()
		 */
		@Override
		public int getRequiredVertexAttributes() {
			return 1;
		}

		/* (non-Javadoc)
		 * @see com.esferixis.gameengine.renderengine.backend.opengl.gl21.texture.TextureMappingShaderProfile.Layer#getRequiredVaryings()
		 */
		@Override
		public int getRequiredVaryings() {
			return 1;
		}

		/* (non-Javadoc)
		 * @see com.esferixis.gameengine.renderengine.backend.opengl.gl21.texture.TextureMappingShaderProfile.Layer#getRequiredSamplers()
		 */
		@Override
		public int getRequiredSamplers() {
			return 0;
		}

		/* (non-Javadoc)
		 * @see com.esferixis.gameengine.renderengine.backend.opengl.gl21.texture.TextureMappingShaderProfile.Layer#getRequiredVertexUniformComponents()
		 */
		@Override
		public int getRequiredVertexUniformComponents() {
			return 16;
		}

		/* (non-Javadoc)
		 * @see com.esferixis.gameengine.renderengine.backend.opengl.gl21.texture.TextureMappingShaderProfile.Layer#getRequiredFragmentUniformComponents_i1()
		 */
		@Override
		public int getRequiredFragmentUniformComponents_i1() {
			return 0;
		}

		/* (non-Javadoc)
		 * @see com.esferixis.gameengine.renderengine.backend.opengl.gl21.texture.TextureMappingShaderProfile.Layer#accept(com.esferixis.gameengine.renderengine.backend.opengl.gl21.texture.TextureMappingShaderProfile.Layer.Visitor)
		 */
		@Override
		public <V> V accept(Visitor<V> visitor) {
			return visitor.visit(this);
		}
		
	}
	
	public static class UniformColoredLayer extends Layer {

		/**
		 * @param hasMaskColor
		 */
		public UniformColoredLayer() {
			super(true);
		}

		/* (non-Javadoc)
		 * @see com.esferixis.gameengine.renderengine.backend.opengl.gl21.meshLayers.LayersRenderingShaderProfile.Layer#accept(com.esferixis.gameengine.renderengine.backend.opengl.gl21.meshLayers.LayersRenderingShaderProfile.Layer.Visitor)
		 */
		@Override
		public <V> V accept(Visitor<V> visitor) {
			return visitor.visit(this);
		}

		/* (non-Javadoc)
		 * @see com.esferixis.gameengine.renderengine.backend.opengl.gl21.meshLayers.LayersRenderingShaderProfile.Layer#getRequiredVertexAttributes()
		 */
		@Override
		public int getRequiredVertexAttributes() {
			return 0;
		}

		/* (non-Javadoc)
		 * @see com.esferixis.gameengine.renderengine.backend.opengl.gl21.meshLayers.LayersRenderingShaderProfile.Layer#getRequiredVaryings()
		 */
		@Override
		public int getRequiredVaryings() {
			return 0;
		}

		/* (non-Javadoc)
		 * @see com.esferixis.gameengine.renderengine.backend.opengl.gl21.meshLayers.LayersRenderingShaderProfile.Layer#getRequiredSamplers()
		 */
		@Override
		public int getRequiredSamplers() {
			return 0;
		}

		/* (non-Javadoc)
		 * @see com.esferixis.gameengine.renderengine.backend.opengl.gl21.meshLayers.LayersRenderingShaderProfile.Layer#getRequiredVertexUniformComponents()
		 */
		@Override
		public int getRequiredVertexUniformComponents() {
			return 16;
		}

		/* (non-Javadoc)
		 * @see com.esferixis.gameengine.renderengine.backend.opengl.gl21.meshLayers.LayersRenderingShaderProfile.Layer#getRequiredFragmentUniformComponents_i1()
		 */
		@Override
		public int getRequiredFragmentUniformComponents_i1() {
			return 0;
		}
		
	}
	
	public static abstract class Layer {
		public interface Visitor<V> {
			public V visit(TextureLayer textureLayer);
			public V visit(VertexColoredLayer vertexColoredLayer);
			public V visit(UniformColoredLayer uniformColoredLayer);
		}
		
		protected final boolean hasMaskColor;
		
		/**
		 * @post Crea la capa indicando si lleva color de máscara
		 */
		public Layer(boolean hasMaskColor) {
			this.hasMaskColor = hasMaskColor;
		}
		
		/**
		 * @post Visita con el visitor especificado
		 */
		public abstract <V> V accept(Visitor<V> visitor);
		
		/**
		 * @pre La capa de textura no puede ser nula
		 * @post Obtiene la capa con la capa de textura especificada
		 */
		public static Layer getLayer(MeshLayer<?> meshLayer) {
			if ( meshLayer != null ) {
				try {
					return meshLayer.accept(new MeshLayer.Visitor<Layer, Exception>() {
						@Override
						public Layer visit(final SimpleTextureLayer<?> layer) throws Exception {
							return layer.getTextureObject().visit(new TextureObject.Visitor<Layer>() {

								@Override
								public Layer visit(TextureObject2d textureObject) {
									return new TextureLayer(TextureLayer.Type.TEXTURE2D, false, layer.getMaskColorObject() != null);
								}

								@Override
								public Layer visit(TextureObject3d textureObject) {
									return new TextureLayer(TextureLayer.Type.TEXTURE3D, false, layer.getMaskColorObject() != null);
								}
							});
						}

						@Override
						public Layer visit(VertexColoredMeshLayer layer) throws Exception {
							return new VertexColoredLayer(layer.getMaskColorObject() != null);
						}

						@Override
						public Layer visit(UniformColoredMeshLayer layer) throws Exception {
							return new UniformColoredLayer();
						}
						
					});
				} catch (Exception e) {
					throw new RuntimeException("Unexpected exception", e);
				}
			}
			else {
				throw new NullPointerException();
			}
		}
		
		/**
		 * @post Devuelve si lleva máscara de color
		 */
		public boolean getHasMaskColor() {
			return this.hasMaskColor;
		}
		
		/**
		 * @post Devuelve la cantidad de atributos de vértices requeridos
		 */
		public abstract int getRequiredVertexAttributes();
		
		/**
		 * @post Devuelve la cantidad de "varyings" requeridos
		 */
		public abstract int getRequiredVaryings();
		
		/**
		 * @post Devuelve la cantidad de "samplers" requeridos
		 */
		public abstract int getRequiredSamplers();
		
		/**
		 * @post Devuelve la cantidad de componentes de uniforms del "vertex shader" requeridos
		 */
		public abstract int getRequiredVertexUniformComponents();
		
		/**
		 * @post Devuelve la cantidad de componentes de uniforms del "fragment shader" requeridos
		 */
		public final int getRequiredFragmentUniformComponents() {
			return this.getRequiredFragmentUniformComponents_i1() + (this.hasMaskColor ? 4 : 0 );
		}
		
		/**
		 * @post Devuelve la cantidad de componentes de uniforms del "fragment shader" requeridos,
		 * 		 fase interna 1
		 */
		public abstract int getRequiredFragmentUniformComponents_i1();
	}
	
	public static class Essence {
		private List<Layer> layers;
		private boolean freezed;
		
		private int requiredVertexAttributes, requiredVaryings, requiredSamplers;
		private int requiredVertexUniformComponents, requiredFragmentUniformComponents;
		
		public Essence() {
			this.layers = new ArrayList<Layer>();
			this.freezed = false;
			
			this.requiredVertexAttributes = 1;
			this.requiredVaryings = 1;
			this.requiredSamplers = 0;
			this.requiredVertexUniformComponents = 0;
			this.requiredFragmentUniformComponents = 0;
		}
		
		private void checkFreezed() {
			if ( this.freezed ) {
				throw new IllegalStateException("Cannot modify a essence when it has been used");
			}
		}
		
		/**
		 * @pre La capa no puede ser nula
		 * @post Agrega una capa
		 * @throws NullPointerException
		 */
		public void add(Layer layer) {
			this.checkFreezed();
			if ( layer != null ) {
				this.layers.add(layer);
				
				this.requiredVertexAttributes += layer.getRequiredVertexAttributes();
				this.requiredVaryings += layer.getRequiredVaryings();
				this.requiredSamplers += layer.getRequiredSamplers();
				this.requiredVertexUniformComponents += layer.getRequiredVertexUniformComponents();
				this.requiredFragmentUniformComponents += layer.getRequiredFragmentUniformComponents();
			}
			else {
				throw new NullPointerException();
			}
		}
		
		/**
		 * @pre La cantidad agregada de capas no puede ser cero
		 * @post Quita la última capa agregada
		 * @throws NoSuchElementException
		 */
		public void removeLast() {
			this.checkFreezed();
			if ( this.layers.size() != 0 ) {
				final Layer layer = this.layers.remove(this.layers.size()-1);
				
				this.requiredVertexAttributes -= layer.getRequiredVertexAttributes();
				this.requiredVaryings -= layer.getRequiredVaryings();
				this.requiredSamplers -= layer.getRequiredSamplers();
			}
			else {
				throw new NoSuchElementException();
			}
		}
		
		/**
		 * @post Devuelve la cantidad de atributos de vértices requeridos
		 */
		public int getRequiredVertexAttributes() {
			return this.requiredVertexAttributes;
		}
		
		/**
		 * @post Devuelve la cantidad de "varyings" requeridos
		 */
		public int getRequiredVaryings() {
			return this.requiredVaryings;
		}
		
		/**
		 * @post Devuelve la cantidad de samplers requeridos
		 */
		public int getRequiredSamplers() {
			return this.requiredSamplers;
		}
		
		/**
		 * @post Devuelve la cantidad de componentes de uniforms del "vertex shader" requeridos
		 */
		public int getRequiredVertexUniformComponents() {
			return this.requiredVertexUniformComponents;
		}
		
		/**
		 * @post Devuelve la cantidad de componentes de uniforms del "fragment shader" requeridos
		 */
		public int getRequiredFragmentUniformComponents() {
			return this.requiredFragmentUniformComponents;
		}
	}
	
	private final Essence essence;
	
	/**
	 * @post Crea el perfil con la esencia especificada
	 */
	public LayersRenderingShaderProfile(Essence essence) {
		if ( essence != null ) {
			essence.freezed = true;
			this.essence = essence;
		}
		else {
			throw new NullPointerException();
		}
	}
	
	/**
	 * @post Devuelve las capas (Sólo lectura)
	 */
	public List<Layer> getLayers() {
		return Collections.unmodifiableList(this.essence.layers);
	}
	
	/**
	 * @post Devuelve la cantidad de atributos de vértices requeridos
	 */
	public int getRequiredVertexAttributes() {
		return this.essence.getRequiredVertexAttributes();
	}
	
	/**
	 * @post Devuelve la cantidad de "varyings" requeridos
	 */
	public int getRequiredVaryings() {
		return this.essence.getRequiredVaryings();
	}
	
	/**
	 * @post Devuelve la cantidad de samplers requeridos
	 */
	public int getRequiredSamplers() {
		return this.essence.getRequiredSamplers();
	}
	
	/**
	 * @post Devuelve si es igual al objeto especificado
	 */
	@Override
	public boolean equals(Object other) {
		if ( ( other != null ) && ( other instanceof LayersRenderingShaderProfile ) ) {
			LayersRenderingShaderProfile otherProfile = (LayersRenderingShaderProfile) other;
			return otherProfile.essence.layers.equals(this.essence.layers);
		}
		else {
			throw new NullPointerException();
		}
	}
	
	/**
	 * @post Devuelve el hash
	 */
	@Override
	public int hashCode() {
		return this.essence.layers.hashCode();
	}
}
