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
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.List;

import com.arielcarrizo.gameengine.renderengine.backend.opengl.GLException;
import com.arielcarrizo.gameengine.renderengine.backend.opengl.gl21.GL21;
import com.arielcarrizo.gameengine.renderengine.backend.opengl.gl21.GLObject;
import com.arielcarrizo.gameengine.renderengine.backend.opengl.gl21.globjects.shader.FragmentShader;
import com.arielcarrizo.gameengine.renderengine.backend.opengl.gl21.globjects.shader.IntegerUniformWriteCache;
import com.arielcarrizo.gameengine.renderengine.backend.opengl.gl21.globjects.shader.ShaderProgram;
import com.arielcarrizo.gameengine.renderengine.backend.opengl.gl21.globjects.shader.Vector4fUniformWriteCache;
import com.arielcarrizo.gameengine.renderengine.backend.opengl.gl21.globjects.shader.VertexShader;
import com.arielcarrizo.gameengine.renderengine.backend.opengl.gl21.globjects.shader.ShaderProgram.Attribute;
import com.arielcarrizo.gameengine.renderengine.backend.opengl.gl21.globjects.shader.ShaderProgram.Uniform;
import com.arielcarrizo.gameengine.renderengine.backend.opengl.gl21.mesh.MeshBacker;
import com.arielcarrizo.gameengine.renderengine.backend.opengl.gl21.meshLayers.LayersRenderingShaderProfile.TextureLayer;
import com.arielcarrizo.gameengine.renderengine.backend.opengl.gl21.meshLayers.LayersRenderingShaderProfile.UniformColoredLayer;
import com.arielcarrizo.gameengine.renderengine.backend.opengl.gl21.meshLayers.LayersRenderingShaderProfile.VertexColoredLayer;
import com.arielcarrizo.gameengine.renderengine.backend.opengl.gl21.meshLayers.LayersVertexDataBacker.LayerVertexDataBacker;
import com.arielcarrizo.gameengine.renderengine.backend.opengl.gl21.meshLayers.LayersVertexDataBacker.TextureLayerVertexDataBacker;
import com.arielcarrizo.gameengine.renderengine.backend.opengl.gl21.meshLayers.LayersVertexDataBacker.VertexColoredLayerVertexDataBacker;
import com.esferixis.gameengine.renderengine.backend.misc.mesh.Mesh;
import com.esferixis.gameengine.renderengine.backend.misc.mesh.MeshTriangleVertex;
import com.esferixis.math.Vector2f;
import com.esferixis.math.Vector3f;
import com.esferixis.math.Vector4f;
import com.esferixis.math.Vectorf;

public final class LayersRenderingShadersUnit extends GLObject {
	private final class SourceCodeBuilder {
		private final StringBuilder vertexShaderSource2dBuilder = new StringBuilder();
		private final StringBuilder vertexShaderSource3dBuilder = new StringBuilder();
		private final StringBuilder fragmentShaderSourceBuilder = new StringBuilder();
		
		private final StringBuilder varyingNamesBuilder = new StringBuilder();
		
		/**
		 * @post Crea el constructor de código fuente
		 */
		public SourceCodeBuilder() {
			
		}
	}
	
	public static interface RenderLayerVisitor<V extends Vectorf, R> {
		public R visit(TextureRenderLayer<V> renderLayer);
		public R visit(VertexColoredRenderLayer<V> renderLayer);
		public R visit(UniformColoredRenderLayer<V> renderLayer);
	}
	
	public abstract class RenderLayer<V extends Vectorf, L extends LayersRenderingShaderProfile.Layer> {
		private final L profileLayer;
		private final int layerId;
		
		private final ShaderProgram.Uniform maskColorUniform;
		private final Vector4fUniformWriteCache maskColorUniformWriteCache;
		
		/**
		 * @post Crea la capa de rendering con la capa de perfil y el id especificados
		 */
		private RenderLayer(L profileLayer, int layerId) {
			if ( profileLayer != null ) {
				this.profileLayer = profileLayer;
				this.layerId = layerId;
				
				if ( profileLayer.getHasMaskColor() ) {
					this.maskColorUniform = new ShaderProgram.Uniform("maskcolor" + "_" + layerId);
					this.maskColorUniformWriteCache = new Vector4fUniformWriteCache(LayersRenderingShadersUnit.this.gl, this.maskColorUniform);
				}
				else {
					this.maskColorUniform = null;
					this.maskColorUniformWriteCache = null;
				}
			}
			else {
				throw new NullPointerException();
			}
		}
		
		/**
		 * @post Devuelve la capa de perfil asociada
		 */
		public final L getProfileLayer() {
			return this.profileLayer;
		}
		
		/**
		 * @post Agrega el atributo de coordenada y los uniforms a la colecciones especificadas
		 */
		protected final void addVariables(List<ShaderProgram.Attribute> attributes, List<ShaderProgram.Uniform> uniforms) {
			if ( this.maskColorUniform != null ) {
				uniforms.add(this.maskColorUniform);
			}
			
			this.addVariables_i1(attributes, uniforms);
		}
		
		/**
		 * @post Agrega el atributo de coordenada y los uniforms a la colecciones especificadas
		 */
		protected abstract void addVariables_i1(List<ShaderProgram.Attribute> attributes, List<ShaderProgram.Uniform> uniforms);
		
		/**
		 * @post Visita con el visitor especificado
		 */
		public abstract <R> R visit(RenderLayerVisitor<V, R> visitor);
		
		/**
		 * @post Construye la declaración de atributos y varyings
		 */
		protected abstract void createAttributesAndVaryingsDeclaration(SourceCodeBuilder sourceCodeBuilder);
		
		/**
		 * @post Construye la asignación de varyings
		 */
		protected abstract void createVaryingsAssignation(SourceCodeBuilder sourceCodeBuilder);
		
		/**
		 * @post Construye las declaraciones específicas del fragment shader
		 */
		protected final void createSpecificFragmentShaderDeclarations(SourceCodeBuilder sourceCodeBuilder) {
			if ( this.maskColorUniform != null ) {
				sourceCodeBuilder.fragmentShaderSourceBuilder.append("uniform vec4 " + this.maskColorUniform.getSourceName() + ";\n");
			}
			
			this.createSpecificFragmentShaderDeclarations_i1(sourceCodeBuilder);
		}
		
		protected abstract void createSpecificFragmentShaderDeclarations_i1(SourceCodeBuilder sourceCodeBuilder);
		
		/**
		 * @post Construye la generación del color de capa y retorna su valor
		 */
		protected final String createLayerColorValueGeneration(SourceCodeBuilder sourceCodeBuilder) {
			String layerColorValue = this.createLayerColorValueGeneration_i1(sourceCodeBuilder);
			
			if ( this.maskColorUniform != null ) {
				layerColorValue = "( " + layerColorValue + " ) * " + this.maskColorUniform.getSourceName();
			}
			
			return layerColorValue;
		}
		
		/**
		 * @post Construye la generación del color de capa y retorna su valor
		 */
		protected abstract String createLayerColorValueGeneration_i1(SourceCodeBuilder sourceCodeBuilder);
		
		/**
		 * @post Envía los atributos en el proceso de rendering
		 */
		public abstract <P extends MeshTriangleVertex<V>, D extends Mesh.Data<V, P>> void sendAttributes(LayerVertexDataBacker layerVertexDataBacker, final MeshBacker<V, P, D>.AttributeSender attributeSender);
		
		private void checkMaskColor() {
			if ( this.maskColorUniform == null ) {
				throw new IllegalStateException("Cannot reference to mask color because it doesn't exists");
			}
		}
		
		/**
		 * @pre El color no puede ser nulo y tiene que haber color de máscara
		 * @post Especifica el color de máscara
		 */
		public void setMaskColor(Vector4f value) {
			this.checkMaskColor();
			if ( value != null ) {
				this.maskColorUniformWriteCache.set(value);
			}
			else {
				throw new NullPointerException();
			}
		}
		
		/**
		 * @pre Tiene que haber color de máscara
		 * @post Devuelve el color de máscara
		 */
		public Vector4f getMaskColor() {
			this.checkMaskColor();
			return this.maskColorUniformWriteCache.get();
		}
	}
	
	public final class UniformColoredRenderLayer<V extends Vectorf> extends RenderLayer<V, LayersRenderingShaderProfile.UniformColoredLayer> {
		/**
		 * @pre La capa de perfil no puede ser nula
		 * @post Crea la capa de renderizado con el perfil y el id especificados
		 */
		public UniformColoredRenderLayer(LayersRenderingShaderProfile.UniformColoredLayer profileLayer, int layerId) {
			super(profileLayer, layerId);
		}

		/* (non-Javadoc)
		 * @see com.esferixis.gameengine.renderengine.backend.opengl.gl21.meshLayers.LayersRenderingShadersUnit.RenderLayer#addVariables_i1(java.util.List, java.util.List)
		 */
		@Override
		protected void addVariables_i1(List<Attribute> attributes, List<Uniform> uniforms) {
			
		}

		/* (non-Javadoc)
		 * @see com.esferixis.gameengine.renderengine.backend.opengl.gl21.meshLayers.LayersRenderingShadersUnit.RenderLayer#createAttributesAndVaryingsDeclaration(com.esferixis.gameengine.renderengine.backend.opengl.gl21.meshLayers.LayersRenderingShadersUnit.SourceCodeBuilder)
		 */
		@Override
		protected void createAttributesAndVaryingsDeclaration(SourceCodeBuilder sourceCodeBuilder) {
			
		}

		/* (non-Javadoc)
		 * @see com.esferixis.gameengine.renderengine.backend.opengl.gl21.meshLayers.LayersRenderingShadersUnit.RenderLayer#createVaryingsAssignation(com.esferixis.gameengine.renderengine.backend.opengl.gl21.meshLayers.LayersRenderingShadersUnit.SourceCodeBuilder)
		 */
		@Override
		protected void createVaryingsAssignation(SourceCodeBuilder sourceCodeBuilder) {
			
		}

		/* (non-Javadoc)
		 * @see com.esferixis.gameengine.renderengine.backend.opengl.gl21.meshLayers.LayersRenderingShadersUnit.RenderLayer#createSpecificFragmentShaderDeclarations_i1(com.esferixis.gameengine.renderengine.backend.opengl.gl21.meshLayers.LayersRenderingShadersUnit.SourceCodeBuilder)
		 */
		@Override
		protected void createSpecificFragmentShaderDeclarations_i1(SourceCodeBuilder sourceCodeBuilder) {
			
		}

		/* (non-Javadoc)
		 * @see com.esferixis.gameengine.renderengine.backend.opengl.gl21.meshLayers.LayersRenderingShadersUnit.RenderLayer#createLayerColorValueGeneration_i1(com.esferixis.gameengine.renderengine.backend.opengl.gl21.meshLayers.LayersRenderingShadersUnit.SourceCodeBuilder)
		 */
		@Override
		protected String createLayerColorValueGeneration_i1(SourceCodeBuilder sourceCodeBuilder) {
			return "vec4(1.0, 1.0, 1.0, 1.0)";
		}
		
		/* (non-Javadoc)
		 * @see com.esferixis.gameengine.renderengine.backend.opengl.gl21.meshLayers.LayersRenderingShadersUnit.RenderLayer#visit(com.esferixis.gameengine.renderengine.backend.opengl.gl21.meshLayers.LayersRenderingShadersUnit.RenderLayerVisitor)
		 */
		@Override
		public <R> R visit(
				com.arielcarrizo.gameengine.renderengine.backend.opengl.gl21.meshLayers.LayersRenderingShadersUnit.RenderLayerVisitor<V, R> visitor) {
			return visitor.visit(this);
		}

		/* (non-Javadoc)
		 * @see com.esferixis.gameengine.renderengine.backend.opengl.gl21.meshLayers.LayersRenderingShadersUnit.RenderLayer#sendAttributes(com.esferixis.gameengine.renderengine.backend.opengl.gl21.meshLayers.LayersVertexDataBacker.LayerVertexDataBacker, com.esferixis.gameengine.renderengine.backend.opengl.gl21.mesh.MeshBacker.AttributeSender)
		 */
		@Override
		public <P extends MeshTriangleVertex<V>, D extends com.esferixis.gameengine.renderengine.backend.misc.mesh.Mesh.Data<V, P>> void sendAttributes(
				LayerVertexDataBacker layerVertexDataBacker, MeshBacker<V, P, D>.AttributeSender attributeSender) {
			
		}
	}
	
	public final class VertexColoredRenderLayer<V extends Vectorf> extends RenderLayer<V, LayersRenderingShaderProfile.VertexColoredLayer> {
		private final ShaderProgram.Attribute colorAttribute;
		
		/**
		 * @pre La capa de perfil no puede ser nula
		 * @post Crea la capa de renderizado con el perfil y el id especificados
		 */
		public VertexColoredRenderLayer(LayersRenderingShaderProfile.VertexColoredLayer profileLayer, int layerId) {
			super(profileLayer, layerId);
			
			this.colorAttribute = new ShaderProgram.Attribute("vertexcolor_" + layerId);
		}
		
		/**
		 * @post Devuelve el atributo de color
		 */
		public ShaderProgram.Attribute getColorAttribute() {
			return this.colorAttribute;
		}

		/* (non-Javadoc)
		 * @see com.esferixis.gameengine.renderengine.backend.opengl.gl21.texture.TextureMappingShadersUnit.RenderLayer#addVariables_i1(java.util.List, java.util.List)
		 */
		@Override
		protected void addVariables_i1(List<Attribute> attributes, List<Uniform> uniforms) {
			attributes.add(this.colorAttribute);
		}

		/* (non-Javadoc)
		 * @see com.esferixis.gameengine.renderengine.backend.opengl.gl21.texture.TextureMappingShadersUnit.RenderLayer#createAttributesAndVaryingsDeclaration(com.esferixis.gameengine.renderengine.backend.opengl.gl21.texture.TextureMappingShadersUnit.SourceCodeBuilder)
		 */
		@Override
		protected void createAttributesAndVaryingsDeclaration(SourceCodeBuilder sourceCodeBuilder) {
			final String colorAttributeName = this.getColorAttribute().getSourceName();
			
			final String attributeDeclaration = "attribute vec4 " + colorAttributeName + ";\n";
			sourceCodeBuilder.vertexShaderSource2dBuilder.append(attributeDeclaration);
			sourceCodeBuilder.vertexShaderSource3dBuilder.append(attributeDeclaration);
			
			sourceCodeBuilder.varyingNamesBuilder.append("varying vec4 f_" + colorAttributeName + ";\n");
		}

		/* (non-Javadoc)
		 * @see com.esferixis.gameengine.renderengine.backend.opengl.gl21.texture.TextureMappingShadersUnit.RenderLayer#createVaryingsAssignation(com.esferixis.gameengine.renderengine.backend.opengl.gl21.texture.TextureMappingShadersUnit.SourceCodeBuilder)
		 */
		@Override
		protected void createVaryingsAssignation(SourceCodeBuilder sourceCodeBuilder) {
			final String colorAttributeName = this.getColorAttribute().getSourceName();
			final String colorVaryingName = "f_" + colorAttributeName;
			
			final String varyingAssignation = "\t" + colorVaryingName + " = " + colorAttributeName + ";\n";
			sourceCodeBuilder.vertexShaderSource2dBuilder.append(varyingAssignation);
			sourceCodeBuilder.vertexShaderSource3dBuilder.append(varyingAssignation);
		}

		/* (non-Javadoc)
		 * @see com.esferixis.gameengine.renderengine.backend.opengl.gl21.texture.TextureMappingShadersUnit.RenderLayer#createSpecificFragmentShaderDeclarations_i1(com.esferixis.gameengine.renderengine.backend.opengl.gl21.texture.TextureMappingShadersUnit.SourceCodeBuilder)
		 */
		@Override
		protected void createSpecificFragmentShaderDeclarations_i1(SourceCodeBuilder sourceCodeBuilder) {
			
		}

		/* (non-Javadoc)
		 * @see com.esferixis.gameengine.renderengine.backend.opengl.gl21.texture.TextureMappingShadersUnit.RenderLayer#createLayerColorValueGeneration_i1(com.esferixis.gameengine.renderengine.backend.opengl.gl21.texture.TextureMappingShadersUnit.SourceCodeBuilder)
		 */
		@Override
		protected String createLayerColorValueGeneration_i1(SourceCodeBuilder sourceCodeBuilder) {
			return "f_" + this.getColorAttribute().getSourceName();
		}

		/* (non-Javadoc)
		 * @see com.esferixis.gameengine.renderengine.backend.opengl.gl21.meshLayers.LayersRenderingShadersUnit.RenderLayer#visit(com.esferixis.gameengine.renderengine.backend.opengl.gl21.meshLayers.LayersRenderingShadersUnit.RenderLayerVisitor)
		 */
		@Override
		public <R> R visit(
				com.arielcarrizo.gameengine.renderengine.backend.opengl.gl21.meshLayers.LayersRenderingShadersUnit.RenderLayerVisitor<V, R> visitor) {
			return visitor.visit(this);
		}

		/* (non-Javadoc)
		 * @see com.esferixis.gameengine.renderengine.backend.opengl.gl21.meshLayers.LayersRenderingShadersUnit.RenderLayer#sendAttributes(com.esferixis.gameengine.renderengine.backend.opengl.gl21.meshLayers.LayersVertexDataBacker.LayerVertexDataBacker, com.esferixis.gameengine.renderengine.backend.opengl.gl21.mesh.MeshBacker.AttributeSender)
		 */
		@Override
		public <P extends MeshTriangleVertex<V>, D extends com.esferixis.gameengine.renderengine.backend.misc.mesh.Mesh.Data<V, P>> void sendAttributes(
				LayerVertexDataBacker layerVertexDataBacker, MeshBacker<V, P, D>.AttributeSender attributeSender) {
			final VertexColoredLayerVertexDataBacker vertexColoredLayerVertexDataBacker = (VertexColoredLayerVertexDataBacker) layerVertexDataBacker;
			
			attributeSender.send(vertexColoredLayerVertexDataBacker.getColorAttribute(), this.getColorAttribute().getLocation());
		}
		
	}
	
	public final class TextureRenderLayer<V extends Vectorf> extends RenderLayer<V, LayersRenderingShaderProfile.TextureLayer> {
		public final class Component {
			private final ShaderProgram.Uniform textureSampler;
			private final ShaderProgram.Attribute coordinatesAttribute;
			
			private final ShaderProgram.Uniform mirrorSCoordinateGapPictureWidthUniform, mirrorTCoordinateGapPictureWidthUniform, mirrorRCoordinateGapPictureWidthUniform;
			
			private final IntegerUniformWriteCache textureSamplerWriteCache;
			
			private final IntegerUniformWriteCache mirrorSCoordinateGapWidthUniformWriteCache, mirrorTCoordinateGapWidthUniformWriteCache, mirrorRCoordinateGapWidthUniformWriteCache;
			
			/**
			 * @pre Todos, excepto el nombre de uniform de espejar coordenadas R, no pueden ser nulos
			 * @post Crea el componente con el nombre del sampler, el nombre del atributo
			 * 		 de coordenada, y el nombre de uniform de espejar coordenadas por cada coordenada especificados
			 * @param attributes
			 * @param textureName
			 */
			private Component(String samplerName, String coordinatesAttributeName, String mirrorSCoordinateGapPictureWidthUniformName, String mirrorTCoordinateGapPictureWidthUniformName, String mirrorRCoordinateGapPictureWidthUniformName) {
				this.textureSampler = new ShaderProgram.Uniform(samplerName);
				this.coordinatesAttribute = new ShaderProgram.Attribute(coordinatesAttributeName);
				
				this.mirrorSCoordinateGapPictureWidthUniform = new ShaderProgram.Uniform(mirrorSCoordinateGapPictureWidthUniformName);
				this.mirrorTCoordinateGapPictureWidthUniform = new ShaderProgram.Uniform(mirrorTCoordinateGapPictureWidthUniformName);
				this.mirrorRCoordinateGapPictureWidthUniform = ( mirrorRCoordinateGapPictureWidthUniformName != null ) ? new ShaderProgram.Uniform(mirrorRCoordinateGapPictureWidthUniformName) : null;
				
				this.textureSamplerWriteCache = new IntegerUniformWriteCache(LayersRenderingShadersUnit.this.gl, this.textureSampler);
				this.mirrorSCoordinateGapWidthUniformWriteCache = new IntegerUniformWriteCache(LayersRenderingShadersUnit.this.gl, this.mirrorSCoordinateGapPictureWidthUniform);
				this.mirrorTCoordinateGapWidthUniformWriteCache = new IntegerUniformWriteCache(LayersRenderingShadersUnit.this.gl, this.mirrorTCoordinateGapPictureWidthUniform);
				this.mirrorRCoordinateGapWidthUniformWriteCache = ( mirrorRCoordinateGapPictureWidthUniformName != null ) ? new IntegerUniformWriteCache(LayersRenderingShadersUnit.this.gl, this.mirrorTCoordinateGapPictureWidthUniform) : null;
			}
			
			/**
			 * @pre Tiene que estar usada la unidad actual, en 2d o 3d,
			 * 		según corresponda
			 * @post Especifica la unidad de textura
			 */
			public void setTextureUnit(int textureUnit) {
				this.textureSamplerWriteCache.set(textureUnit);
			}
			
			/**
			 * @pre Tiene que haber sido inicializada
			 * @post Devuelve la unidad de textura
			 */
			public int getTextureUnit() {
				return this.textureSamplerWriteCache.get();
			}
			
			/**
			 * @post Devuelve el atributo de coordenadas
			 */
			public ShaderProgram.Attribute getCoordinatesAttribute() {
				return this.coordinatesAttribute;
			}
			
			/**
			 * @post Especifica el ancho de la imagen en la coordenada S de textura si se realiza un
			 * 		 espejado o cero, si no se realiza
			 */
			public void setMirrorSCoordinateGapPictureWidth(int value) {
				this.mirrorSCoordinateGapWidthUniformWriteCache.set(value);
			}
			
			/**
			 * @post Devuelve el ancho de la imagen en la coordenada S de textura si se realiza un
			 * 		 espejado o cero, si no se realiza
			 */
			public int getMirrorSCoordinateGapPictureWidth() {
				return this.mirrorSCoordinateGapWidthUniformWriteCache.get();
			}
			
			/**
			 * @post Especifica el ancho de la imagen en la coordenada T de textura si se realiza un
			 * 		 espejado o cero, si no se realiza
			 */
			public void setMirrorTCoordinateGapPictureWidth(int value) {
				this.mirrorTCoordinateGapWidthUniformWriteCache.set(value);
			}
			
			/**
			 * @post Devuelve el ancho de la imagen en la coordenada T de textura si se realiza un
			 * 		 espejado o cero, si no se realiza
			 */
			public int getMirrorTCoordinateGapPictureWidth() {
				return this.mirrorTCoordinateGapWidthUniformWriteCache.get();
			}
			
			private void checkIfRCoordinateExists() {
				if ( this.mirrorRCoordinateGapPictureWidthUniform == null ) {
					throw new IllegalStateException("R coordinate doesn't exists");
				}
			}
			/**
			 * @post Especifica el ancho de la imagen en la coordenada R de textura si se realiza un
			 * 		 espejado o cero, si no se realiza
			 */
			public void setMirrorRCoordinateGapPictureWidth(int value) {
				this.checkIfRCoordinateExists();
				this.mirrorRCoordinateGapWidthUniformWriteCache.set(value);
			}
			
			/**
			 * @post Devuelve el ancho de la imagen en la coordenada R de textura si se realiza un
			 * 		 espejado o cero, si no se realiza
			 */
			public int getMirrorRCoordinateGapPictureWidth() {
				this.checkIfRCoordinateExists();
				return this.mirrorRCoordinateGapWidthUniformWriteCache.get();
			}
			
			/**
			 * @post Agrega el atributo de coordenada y los uniforms a la colección especificada
			 */
			private void addVariables(List<ShaderProgram.Attribute> attributes, List<ShaderProgram.Uniform> uniforms) {
				attributes.add(this.coordinatesAttribute);
				uniforms.add(this.textureSampler);
				uniforms.add(this.mirrorSCoordinateGapPictureWidthUniform);
				uniforms.add(this.mirrorTCoordinateGapPictureWidthUniform);
				if ( this.mirrorRCoordinateGapPictureWidthUniform != null ) uniforms.add(this.mirrorRCoordinateGapPictureWidthUniform);
			}
		}
		
		private final Component colorTexture;
		private final Component alphaTexture;
		
		/**
		 * @post Crea la capa de rendering con la capa de perfil y el id de layer especificados
		 */
		private TextureRenderLayer(LayersRenderingShaderProfile.TextureLayer profileLayer, int layerId) {
			super(profileLayer, layerId);
			
			this.colorTexture = new Component("color" + profileLayer.getType().name() + "_" + layerId, "color" + profileLayer.getType().name() + "_coordinate_" + layerId, "color" + profileLayer.getType().name() + "_mirrorSCoordinateGapPictureWidth_" + layerId, "color" + profileLayer.getType().name() + "_mirrorTCoordinateGapPictureWidth_" + layerId, (profileLayer.getType() == LayersRenderingShaderProfile.TextureLayer.Type.TEXTURE3D) ? "color" + profileLayer.getType().name() + "_mirrorRCoordinateGapPictureWidth_" + layerId : null);
			
			if ( profileLayer.getHasAlphaTexture() ) {
				this.alphaTexture = new Component("alpha" + profileLayer.getType().name() + "_" + layerId, "alpha" + profileLayer.getType().name() + "_coordinate_" + layerId, "alpha" + profileLayer.getType().name() + "_mirrorSCoordinateGapPictureWidth_" + layerId, "alpha" + profileLayer.getType().name() + "_mirrorTCoordinateGapPictureWidth_" + layerId, (profileLayer.getType() == LayersRenderingShaderProfile.TextureLayer.Type.TEXTURE3D) ? "alpha" + profileLayer.getType().name() + "_mirrorRCoordinateGapPictureWidth_" + layerId : null);
			}
			else {
				this.alphaTexture = null;
			}
		}
		
		/**
		 * @post Devuelve el componente de textura de color
		 */
		public Component getColorTexture() {
			return this.colorTexture;
		}
		
		/**
		 * @post Devuelve el componente de textura de alpha,
		 * 		 si no tiene devuelve null
		 */
		public Component getAlphaTexture() {
			return this.alphaTexture;
		}

		/* (non-Javadoc)
		 * @see com.esferixis.gameengine.renderengine.backend.opengl.gl21.texture.TextureMappingShadersUnit.RenderLayer#addVariables_i1(java.util.List, java.util.List)
		 */
		@Override
		protected void addVariables_i1(List<Attribute> attributes, List<Uniform> uniforms) {
			this.getColorTexture().addVariables(attributes, uniforms);
			
			if ( this.getAlphaTexture() != null ) {
				this.getAlphaTexture().addVariables(attributes, uniforms);
			}
		}

		/* (non-Javadoc)
		 * @see com.esferixis.gameengine.renderengine.backend.opengl.gl21.texture.TextureMappingShadersUnit.RenderLayer#createAttributesAndVaryingsDeclaration(com.esferixis.gameengine.renderengine.backend.opengl.gl21.texture.TextureMappingShadersUnit.SourceCodeBuilder)
		 */
		@Override
		protected void createAttributesAndVaryingsDeclaration(SourceCodeBuilder sourceCodeBuilder) {
			{
				final String attributeDeclaration = "attribute " + this.getProfileLayer().getType().getAttributeGLType() + " " + this.getColorTexture().coordinatesAttribute.getSourceName() + ";\n";
				sourceCodeBuilder.vertexShaderSource2dBuilder.append(attributeDeclaration);
				sourceCodeBuilder.vertexShaderSource3dBuilder.append(attributeDeclaration);
			}
			
			sourceCodeBuilder.varyingNamesBuilder.append("varying " + this.getProfileLayer().getType().getAttributeGLType() + " f_" + this.getColorTexture().coordinatesAttribute.getSourceName() + ";\n");
			
			if ( this.getProfileLayer().getHasAlphaTexture() ) {
				final String attributeDeclaration = "attribute " + this.getProfileLayer().getType().getAttributeGLType() + " " + this.getAlphaTexture().coordinatesAttribute.getSourceName() + ";\n";
				sourceCodeBuilder.vertexShaderSource2dBuilder.append(attributeDeclaration);
				sourceCodeBuilder.vertexShaderSource3dBuilder.append(attributeDeclaration);
				sourceCodeBuilder.varyingNamesBuilder.append("varying " + this.getProfileLayer().getType().getAttributeGLType() + " f_" + this.getAlphaTexture().coordinatesAttribute.getSourceName() + ";\n");
			}
		}

		/* (non-Javadoc)
		 * @see com.esferixis.gameengine.renderengine.backend.opengl.gl21.texture.TextureMappingShadersUnit.RenderLayer#createVaryingsAssignation(com.esferixis.gameengine.renderengine.backend.opengl.gl21.texture.TextureMappingShadersUnit.SourceCodeBuilder)
		 */
		@Override
		protected void createVaryingsAssignation(SourceCodeBuilder sourceCodeBuilder) {			
			final String colorAttributeName = this.getColorTexture().coordinatesAttribute.getSourceName();
			final String colorVaryingName = "f_" + colorAttributeName;
			
			{
				final String varyingAssignation = "\t" + colorVaryingName + " = " + colorAttributeName + ";\n";
				sourceCodeBuilder.vertexShaderSource2dBuilder.append(varyingAssignation);
				sourceCodeBuilder.vertexShaderSource3dBuilder.append(varyingAssignation);
			}
			
			if ( this.getProfileLayer().getHasAlphaTexture() ) {
				final String alphaAttributeName = this.getAlphaTexture().coordinatesAttribute.getSourceName();
				final String alphaVaryingName = "f_" + alphaAttributeName;
				
				final String varyingAssignation = "\t" + alphaVaryingName + " = " + alphaAttributeName + ";\n";
				sourceCodeBuilder.vertexShaderSource2dBuilder.append(varyingAssignation);
				sourceCodeBuilder.vertexShaderSource3dBuilder.append(varyingAssignation);
			}
		}

		/* (non-Javadoc)
		 * @see com.esferixis.gameengine.renderengine.backend.opengl.gl21.texture.TextureMappingShadersUnit.RenderLayer#createSpecificFragmentShaderDeclarations_i1(com.esferixis.gameengine.renderengine.backend.opengl.gl21.texture.TextureMappingShadersUnit.SourceCodeBuilder)
		 */
		@Override
		protected void createSpecificFragmentShaderDeclarations_i1(SourceCodeBuilder sourceCodeBuilder) {
			sourceCodeBuilder.fragmentShaderSourceBuilder.append("uniform " + this.getProfileLayer().getType().getSamplerType() + " " + this.getColorTexture().textureSampler.getSourceName() + ";\n");
			sourceCodeBuilder.fragmentShaderSourceBuilder.append("uniform int " + this.getColorTexture().mirrorSCoordinateGapPictureWidthUniform.getSourceName() + ";\n");
			sourceCodeBuilder.fragmentShaderSourceBuilder.append("uniform int " + this.getColorTexture().mirrorTCoordinateGapPictureWidthUniform.getSourceName() + ";\n");
			if ( this.getProfileLayer().getType() == TextureLayer.Type.TEXTURE3D ) {
				sourceCodeBuilder.fragmentShaderSourceBuilder.append("uniform int " + this.getColorTexture().mirrorRCoordinateGapPictureWidthUniform.getSourceName() + ";\n");
			}
			
			if ( this.getProfileLayer().getHasAlphaTexture() ) {
				sourceCodeBuilder.fragmentShaderSourceBuilder.append("uniform " + this.getProfileLayer().getType().getSamplerType() + " " + this.getAlphaTexture().textureSampler.getSourceName() + ";\n");
				sourceCodeBuilder.fragmentShaderSourceBuilder.append("uniform int " + this.getAlphaTexture().mirrorSCoordinateGapPictureWidthUniform.getSourceName() + ";\n");
				sourceCodeBuilder.fragmentShaderSourceBuilder.append("uniform int " + this.getAlphaTexture().mirrorTCoordinateGapPictureWidthUniform.getSourceName() + ";\n");
				if ( this.getProfileLayer().getType() == TextureLayer.Type.TEXTURE3D ) {
					sourceCodeBuilder.fragmentShaderSourceBuilder.append("uniform int " + this.getAlphaTexture().mirrorRCoordinateGapPictureWidthUniform.getSourceName() + ";\n");
				}
			}
		}

		/* (non-Javadoc)
		 * @see com.esferixis.gameengine.renderengine.backend.opengl.gl21.texture.TextureMappingShadersUnit.RenderLayer#createLayerColorValueGeneration_i1(com.esferixis.gameengine.renderengine.backend.opengl.gl21.texture.TextureMappingShadersUnit.SourceCodeBuilder)
		 */
		@Override
		protected String createLayerColorValueGeneration_i1(SourceCodeBuilder sourceCodeBuilder) {
			final String textureFunctionName;
			final String dimensions;
			
			String colorValue, alphaValue;
			
			if ( this.getProfileLayer().getType() == TextureLayer.Type.TEXTURE2D ) {
				textureFunctionName = "texture2D";
				dimensions = "2d";
			}
			else if ( this.getProfileLayer().getType() == TextureLayer.Type.TEXTURE3D ) {
				textureFunctionName = "texture3D";
				dimensions = "3d";
			}
			else {
				throw new RuntimeException("Unexpected texture type");
			}
			
			{
				final String originalCoordinateValue = "f_" + this.getColorTexture().coordinatesAttribute.getSourceName();
				
				final String resultCoordinateVariableName = "resultColor_" + dimensions + "_coordinate";
				sourceCodeBuilder.fragmentShaderSourceBuilder.append("	" + resultCoordinateVariableName + ".x = processCoordinate(" + originalCoordinateValue + ".x, " + this.getColorTexture().mirrorSCoordinateGapPictureWidthUniform.getSourceName() + ");\n");
				sourceCodeBuilder.fragmentShaderSourceBuilder.append("	" + resultCoordinateVariableName + ".y = processCoordinate(" + originalCoordinateValue + ".y, " + this.getColorTexture().mirrorTCoordinateGapPictureWidthUniform.getSourceName() + ");\n");
				if ( this.getProfileLayer().getType() == TextureLayer.Type.TEXTURE3D ) {
					sourceCodeBuilder.fragmentShaderSourceBuilder.append("	" + resultCoordinateVariableName + ".z = processCoordinate(" + originalCoordinateValue + ".z, " + this.getColorTexture().mirrorTCoordinateGapPictureWidthUniform.getSourceName() + ");\n");
				}
				
				colorValue = textureFunctionName + "( " + this.getColorTexture().textureSampler.getSourceName() + ", " + resultCoordinateVariableName + " )";
			}
			
			if ( this.getProfileLayer().getHasAlphaTexture() ) {
				final String originalCoordinateValue = "f_" + this.getAlphaTexture().coordinatesAttribute.getSourceName();
				
				final String resultCoordinateVariableName = "resultAlpha_" + dimensions + "_coordinate";
				sourceCodeBuilder.fragmentShaderSourceBuilder.append("	" + resultCoordinateVariableName + ".x = processCoordinate(" + originalCoordinateValue + ".x, " + this.getAlphaTexture().mirrorSCoordinateGapPictureWidthUniform.getSourceName() + ");\n");
				sourceCodeBuilder.fragmentShaderSourceBuilder.append("	" + resultCoordinateVariableName + ".y = processCoordinate(" + originalCoordinateValue + ".y, " + this.getAlphaTexture().mirrorTCoordinateGapPictureWidthUniform.getSourceName() + ");\n");
				if ( this.getProfileLayer().getType() == TextureLayer.Type.TEXTURE3D ) {
					sourceCodeBuilder.fragmentShaderSourceBuilder.append("	" + resultCoordinateVariableName + ".z = processCoordinate(" + originalCoordinateValue + ".z, " + this.getAlphaTexture().mirrorTCoordinateGapPictureWidthUniform.getSourceName() + ");\n");
				}
				
				alphaValue = textureFunctionName + "( " + this.getAlphaTexture().textureSampler.getSourceName() + ", " + resultCoordinateVariableName + " ).x";
			}
			else {
				alphaValue = "";
			}
			
			String layerColorValue;
			
			if ( alphaValue == "" ) {
				layerColorValue = colorValue;
			}
			else {
				layerColorValue = "( " + colorValue + " ) * ( " + alphaValue + " )";
			}
			
			return layerColorValue;
		}

		/* (non-Javadoc)
		 * @see com.esferixis.gameengine.renderengine.backend.opengl.gl21.meshLayers.LayersRenderingShadersUnit.RenderLayer#visit(com.esferixis.gameengine.renderengine.backend.opengl.gl21.meshLayers.LayersRenderingShadersUnit.RenderLayer.Visitor)
		 */
		@Override
		public <R> R visit(
				com.arielcarrizo.gameengine.renderengine.backend.opengl.gl21.meshLayers.LayersRenderingShadersUnit.RenderLayerVisitor<V, R> visitor) {
			return visitor.visit(this);
		}

		/* (non-Javadoc)
		 * @see com.esferixis.gameengine.renderengine.backend.opengl.gl21.meshLayers.LayersRenderingShadersUnit.RenderLayer#sendAttributes(com.esferixis.gameengine.renderengine.backend.opengl.gl21.meshLayers.LayersVertexDataBacker.LayerVertexDataBacker, com.esferixis.gameengine.renderengine.backend.opengl.gl21.mesh.MeshBacker.AttributeSender)
		 */
		@Override
		public <P extends MeshTriangleVertex<V>, D extends com.esferixis.gameengine.renderengine.backend.misc.mesh.Mesh.Data<V, P>> void sendAttributes(
				LayerVertexDataBacker layerVertexDataBacker, MeshBacker<V, P, D>.AttributeSender attributeSender) {
			final TextureLayerVertexDataBacker textureLayerVertexDataBacker = (TextureLayerVertexDataBacker) layerVertexDataBacker;
			
			// Enviar coordenadas para textura de color
			attributeSender.send(textureLayerVertexDataBacker.getColorCoordinatesAttribute(), this.getColorTexture().getCoordinatesAttribute().getLocation());
			
			if ( this.getAlphaTexture() != null ) {
				// Enviar coordenadas para textura de alpha
				attributeSender.send(textureLayerVertexDataBacker.getAlphaCoordinatesAttribute(), this.getAlphaTexture().getCoordinatesAttribute().getLocation());
			}
		}
	}
	
	public final static class ShaderProgramContainer<V extends Vectorf> extends com.arielcarrizo.gameengine.renderengine.backend.opengl.gl21.globjects.shader.ShaderProgramContainer {
		private final List<RenderLayer<V, ?>> renderLayers;
		
		/**
		 * @post Crea el contenedor
		 */
		private static <V extends Vectorf> ShaderProgramContainer<V> createContainer(VertexShader vertexShader, FragmentShader fragmentShader, RenderLayer<V, ?>[] renderLayers) throws GLException {
			final List<ShaderProgram.Attribute> attributes = new ArrayList<ShaderProgram.Attribute>();
			final List<ShaderProgram.Uniform> uniforms = new ArrayList<ShaderProgram.Uniform>();
			
			for ( RenderLayer<V, ?> eachRenderLayer : renderLayers ) {
				eachRenderLayer.addVariables(attributes, uniforms);
			}
			
			return new ShaderProgramContainer<V>(vertexShader, fragmentShader, attributes, uniforms, Collections.unmodifiableList(Arrays.asList(renderLayers)));
		}
		
		/**
		 * @post Crea el contenedor
		 */
		private ShaderProgramContainer(VertexShader vertexShader, FragmentShader fragmentShader, Collection<ShaderProgram.Attribute> specificAttributes, Collection<ShaderProgram.Uniform> specificUniforms, List<RenderLayer<V, ?>> renderLayers) throws GLException {
			super(vertexShader, fragmentShader, specificAttributes, specificUniforms);
			this.renderLayers = renderLayers;
		}
		
		/**
		 * @post Devuelve las capas de renderizado
		 */
		public List<RenderLayer<V, ?>> getRenderLayers() {
			this.checkDestroyed();
			
			return this.renderLayers;
		}
		
		/**
		 * @post Destruye el contenedor
		 */
		protected void destroy() {
			super.destroy();
		}
	}
	
	private final VertexShader vertex2dShader, vertex3dShader;
	private final FragmentShader fragmentShader;
	private final ShaderProgramContainer<Vector2f> shader2dProgramContainer;
	private final ShaderProgramContainer<Vector3f> shader3dProgramContainer;
	
	private final LayersRenderingShaderProfile shaderProfile;
	
	private boolean isDestroyed;
	
	/**
	 * @post Crea el shader con la implementación de OpenGL y el perfil de shader
	 * 		 especificado
	 */
	LayersRenderingShadersUnit(GL21 gl, LayersRenderingShaderProfile shaderProfile) throws GLException {
		super(gl);
		
		LayersRenderingShaderProfile.Layer[] profileLayers = shaderProfile.getLayers().toArray(new LayersRenderingShaderProfile.Layer[0]);
		
		if ( shaderProfile != null ) {
			this.shaderProfile = shaderProfile;
		}
		else {
			throw new NullPointerException();
		}
		
		this.isDestroyed = false;
		
		final RenderLayer<Vector2f, ?>[] render2dLayers = new RenderLayer[profileLayers.length];
		final RenderLayer<Vector3f, ?>[] render3dLayers = new RenderLayer[profileLayers.length];
		for ( int i_M=0; i_M<profileLayers.length;i_M++ ) {
			final int i = i_M;
			final LayersRenderingShaderProfile.Layer eachProfileLayer = profileLayers[i];
			
			eachProfileLayer.accept(new LayersRenderingShaderProfile.Layer.Visitor<Void>() {

				@Override
				public Void visit(
						TextureLayer textureLayer) {
					render2dLayers[i] = new TextureRenderLayer<Vector2f>(textureLayer, i);
					render3dLayers[i] = new TextureRenderLayer<Vector3f>(textureLayer, i);
					
					return null;
				}

				@Override
				public Void visit(
						VertexColoredLayer vertexColoredLayer) {
					render2dLayers[i] = new VertexColoredRenderLayer<Vector2f>(vertexColoredLayer, i);
					render3dLayers[i] = new VertexColoredRenderLayer<Vector3f>(vertexColoredLayer, i);
					
					return null;
				}

				@Override
				public Void visit(UniformColoredLayer uniformColoredLayer) {
					render2dLayers[i] = new UniformColoredRenderLayer<Vector2f>(uniformColoredLayer, i);
					render3dLayers[i] = new UniformColoredRenderLayer<Vector3f>(uniformColoredLayer, i);
					
					return null;
				}
			});
		}
		
		final SourceCodeBuilder sourceCodeBuilder = new SourceCodeBuilder();
		
		sourceCodeBuilder.vertexShaderSource2dBuilder.append(
				"#version 120\n"
			+	"attribute vec2 vertexPosition;\n\n"
		);
		sourceCodeBuilder.vertexShaderSource3dBuilder.append(
				"#version 120\n"
			+	"attribute vec3 vertexPosition;\n\n"
		);
		
		for ( RenderLayer<Vector3f, ?> each3dRenderLayer : render3dLayers ) {
			each3dRenderLayer.createAttributesAndVaryingsDeclaration(sourceCodeBuilder);
		}
		
		sourceCodeBuilder.vertexShaderSource2dBuilder.append(
				sourceCodeBuilder.varyingNamesBuilder.toString()
			+	"\n"
			+	"uniform mat3 transformMatrix;\n"
			+	"\n"
			+	"void main(void) {\n"
			+	"	gl_Position = vec4( transformMatrix * vec3(vertexPosition, 1.0), 1.0 );\n");
		
		sourceCodeBuilder.vertexShaderSource3dBuilder.append(
				sourceCodeBuilder.varyingNamesBuilder.toString()
			+	"\n"
			+	"uniform mat4 transformMatrix;\n"
			+	"\n"
			+	"void main(void) {\n"
			+	"	gl_Position = transformMatrix * vec4(vertexPosition, 1.0);\n");
		
		for ( RenderLayer<Vector3f, ?> each3dRenderLayer : render3dLayers ) {
			each3dRenderLayer.createVaryingsAssignation(sourceCodeBuilder);
		}
		
		sourceCodeBuilder.vertexShaderSource2dBuilder.append("}\n");
		sourceCodeBuilder.vertexShaderSource3dBuilder.append("}\n");
		
		sourceCodeBuilder.fragmentShaderSourceBuilder.append(
				"#version 120\n"
			+	"\n"
			+	sourceCodeBuilder.varyingNamesBuilder.toString()
		);
		
		for ( RenderLayer<Vector3f, ?> each3dRenderLayer : render3dLayers ) {
			each3dRenderLayer.createSpecificFragmentShaderDeclarations(sourceCodeBuilder);
		}
		
		sourceCodeBuilder.fragmentShaderSourceBuilder.append(
				"\n"
			+	"float processCoordinate(float value, int gapPictureWidth) {"
			+	"	if ( gapPictureWidth != 0 ) {"
			+	"		float gapWidth = 0.5 / float(gapPictureWidth);\n"
			+	"		value = abs(value);\n"
			+	"		if ( value < gapWidth ) value = gapWidth;\n"
			+	"	}\n"
			+	"	return value;\n"
			+	"}\n"
			+	"\n"
			+	"void main(void) {\n"
			+	"	vec4 layerColorValue;\n"
			+	"	vec2 resultColor_2d_coordinate;\n"
			+	"	vec2 resultAlpha_2d_coordinate;\n"
			+	"	vec3 resultColor_3d_coordinate;\n"
			+	"	vec3 resultAlpha_3d_coordinate;\n"
		);
		
		{
			boolean firstLayer = true;
			for ( RenderLayer<Vector3f, ?> each3dRenderLayer : render3dLayers ) {
				final String layerColorValue = each3dRenderLayer.createLayerColorValueGeneration(sourceCodeBuilder);
				
				sourceCodeBuilder.fragmentShaderSourceBuilder.append("	layerColorValue = " + layerColorValue + ";\n");
				
				final String newColorValue;
				
				if ( firstLayer ) {
					newColorValue = "layerColorValue";
					firstLayer = false;
				}
				else {
					newColorValue = "gl_FragColor.xyz * (1.0 - layerColorValue.w) + layerColorValue.xyz * layerColorValue.w";
				}
				
				sourceCodeBuilder.fragmentShaderSourceBuilder.append("	gl_FragColor = " + newColorValue + ";\n");
			}
		}
		
		sourceCodeBuilder.fragmentShaderSourceBuilder.append("}\n");
		
		this.vertex2dShader = new VertexShader(this.gl, sourceCodeBuilder.vertexShaderSource2dBuilder.toString());
		this.vertex3dShader = new VertexShader(this.gl, sourceCodeBuilder.vertexShaderSource3dBuilder.toString());
		this.fragmentShader = new FragmentShader(this.gl, sourceCodeBuilder.fragmentShaderSourceBuilder.toString());
		
		this.shader2dProgramContainer = ShaderProgramContainer.createContainer(this.vertex2dShader, this.fragmentShader, render2dLayers);
		this.shader3dProgramContainer = ShaderProgramContainer.createContainer(this.vertex3dShader, this.fragmentShader, render3dLayers);
	}
	
	/**
	 * @post Verifica que la unidad no esté destruida
	 */
	private void checkDestroyed() {
		if ( this.isDestroyed ) {
			throw new IllegalStateException("Cannot use an destroyed texture mapping shader unit");
		}
	}
	
	/**
	 * @post Devuelve el contenedor de programa de shader 2d
	 */
	public ShaderProgramContainer<Vector2f> getShader2dProgramContainer() {
		this.checkDestroyed();
		
		return this.shader2dProgramContainer;
	}
	
	/**
	 * @post Devuelve el contenedor de programa de shader 3d
	 */
	public ShaderProgramContainer<Vector3f> getShader3dProgramContainer() {
		this.checkDestroyed();
		
		return this.shader3dProgramContainer;
	}
	
	/**
	 * @post Devuelve el perfil de shader
	 */
	public LayersRenderingShaderProfile getShaderProfile() {
		this.checkDestroyed();
		return this.shaderProfile;
	}
	
	/**
	 * @post Destruye la unidad
	 */
	void destroy() {
		this.checkDestroyed();
		
		this.shader2dProgramContainer.destroy();
		this.shader3dProgramContainer.destroy();
		this.fragmentShader.destroy();
		this.vertex2dShader.destroy();
		this.vertex3dShader.destroy();
		
		this.isDestroyed = true;
	}
}

