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
import java.util.EnumMap;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import com.arielcarrizo.gameengine.renderengine.backend.opengl.GLException;
import com.arielcarrizo.gameengine.renderengine.backend.opengl.gl21.GL21;
import com.arielcarrizo.gameengine.renderengine.backend.opengl.gl21.Gl21RenderEngineBackendObject;
import com.arielcarrizo.gameengine.renderengine.backend.opengl.gl21.Gl21RenderEngineBackendSystem;
import com.arielcarrizo.gameengine.renderengine.backend.opengl.gl21.errorchecker.GLErrorChecker;
import com.esferixis.gameengine.renderengine.backend.texture.TextureObject;
import com.esferixis.gameengine.renderengine.backend.texture.TextureObject2d;
import com.esferixis.gameengine.renderengine.backend.texture.TextureObject3d;
import com.esferixis.gameengine.renderengine.backend.texture.TextureObject.Visitor;
import com.esferixis.gameengine.renderengine.picture.RasterPicture;
import com.esferixis.gameengine.renderengine.texture.CoordinateWrap;
import com.esferixis.gameengine.renderengine.texture.TextureQualitySettings;
import com.esferixis.math.Vectorf;
import com.esferixis.misc.slotlocator.MRULinkedSlotAllocator;

public final class LayeredGeometryRendererSubsystem extends Gl21RenderEngineBackendObject {
	class ShaderEntry {
		private final LayersRenderingShadersUnit shaderUnit;
		private int referenceOcurrences;
		
		/**
		 * @post Crea la entrada con el shader especificado
		 */
		public ShaderEntry(LayersRenderingShadersUnit shaderUnit) {
			this.shaderUnit = shaderUnit;
			this.referenceOcurrences = 0;
		}
	}
	
	private final Map<LayersRenderingShaderProfile, ShaderEntry> shaderEntryPerShaderProfile;
	private final MRULinkedSlotAllocator<TextureUnitSlot> mruLinkedTextureUnitSlotAllocator;
	
	private final int maxCombinedTextureUnits, maxVertexAttribs, maxVaryingFloats;
	private final int maxVertexUniformComponents, maxFragmentUniformComponents;
	
	private TextureBacker<?> lastBindedTextureBacker;
	
	private boolean isDestroyed;
	
	/**
	 * @pre La implementación de OpenGL no puede ser nula
	 * @post Crea el subsistema de renderización de geometría texturizada con
	 * 		 la implementación de OpenGL especificada
	 * @param gl
	 */
	public LayeredGeometryRendererSubsystem(Gl21RenderEngineBackendSystem gl21RenderEngineBackend) throws GLException {
		super(gl21RenderEngineBackend);
		
		final GLErrorChecker glErrorChecker = new GLErrorChecker(this.gl);
		
		this.maxCombinedTextureUnits = this.gl.glGetInteger(GL21.GL_MAX_COMBINED_TEXTURE_IMAGE_UNITS);
		glErrorChecker.checkGLError();
		
		this.maxVertexAttribs = this.gl.glGetInteger(GL21.GL_MAX_VERTEX_ATTRIBS);
		glErrorChecker.checkGLError();
		
		this.maxVaryingFloats = this.gl.glGetInteger(GL21.GL_MAX_VARYING_FLOATS);
		glErrorChecker.checkGLError();
		
		this.maxVertexUniformComponents = this.gl.glGetInteger(GL21.GL_MAX_VERTEX_UNIFORM_COMPONENTS);
		glErrorChecker.checkGLError();
		
		this.maxFragmentUniformComponents = this.gl.glGetInteger(GL21.GL_MAX_FRAGMENT_UNIFORM_COMPONENTS);
		glErrorChecker.checkGLError();
		
		List<TextureUnitSlot> textureUnitSlots = new ArrayList<TextureUnitSlot>(this.maxCombinedTextureUnits);
		for ( int i = this.maxCombinedTextureUnits-1 ; i >= 0 ; i-- ) {
			textureUnitSlots.add(new TextureUnitSlot(i));
		}
		this.mruLinkedTextureUnitSlotAllocator = new MRULinkedSlotAllocator<TextureUnitSlot>(textureUnitSlots);
		
		this.shaderEntryPerShaderProfile = new HashMap<LayersRenderingShaderProfile, ShaderEntry>();
		
		this.lastBindedTextureBacker = null;
		
		this.isDestroyed = false;
	}
	
	/**
	 * @post Devuelve la cantidad máxima combinada de unidades de textura
	 */
	public int getMaxCombinedTextureUnits() {
		return this.maxCombinedTextureUnits;
	}
	
	/**
	 * @post Devuelve la cantidad máxima de atributos de textura
	 */
	public int getMaxVertexAttribs() {
		return this.maxVertexAttribs;
	}
	
	/**
	 * @post Devuelve la cantidad máxima de varyings "floats"
	 */
	public int getMaxVaryingFloats() {
		return this.maxVaryingFloats;
	}
	
	/**
	 * @post Devuelve la cantidad máxima de componentes de uniforms en el "vertex shader"
	 */
	public int getMaxVertexUniformComponents() {
		return this.maxVertexUniformComponents;
	}
	
	/**
	 * @post Devuelve la cantidad máxima de componentes de uniforms en el "fragment shader"
	 */
	public int getMaxFragmentUniformComponents() {
		return this.maxFragmentUniformComponents;
	}
	
	/**
	 * @post Asocia el perfil de shader con un shader y lo devuelve
	 */
	LayersRenderingShadersUnit attachShader(LayersRenderingShaderProfile shaderProfile) throws GLException {
		if ( shaderProfile != null ) {
			ShaderEntry shaderEntry = this.shaderEntryPerShaderProfile.get(shaderProfile);
			
			if ( shaderEntry == null ) {
				shaderEntry = new ShaderEntry(new LayersRenderingShadersUnit(this.gl, shaderProfile));
				this.shaderEntryPerShaderProfile.put(shaderProfile, shaderEntry);
			}
			
			shaderEntry.referenceOcurrences++;
			
			return shaderEntry.shaderUnit;
		}
		else {
			throw new NullPointerException();
		}
	}
	
	/**
	 * @pre El perfil de shader tiene que estar asociado
	 * @post Desasocia el perfil de shader
	 */
	void detachShader(LayersRenderingShaderProfile shaderProfile) {
		if ( shaderProfile != null ) {
			ShaderEntry shaderEntry = this.shaderEntryPerShaderProfile.get(shaderProfile);
			
			if ( shaderEntry != null ) {
				shaderEntry.referenceOcurrences--;
				
				if ( shaderEntry.referenceOcurrences == 0 ) {
					shaderEntry.shaderUnit.destroy();
					this.shaderEntryPerShaderProfile.remove(shaderProfile);
				}
			}
			else {
				throw new IllegalStateException("Attemped to detach an shader profile when it hasn't associated shaders");
			}
		}
		else {
			throw new NullPointerException();
		}
	}
	
	/**
	 * @pre El backer de textura no puede ser nulo
	 * @post Obtiene una unidad de textura para el backer de textura especificado
	 * 		 y la devuelve.
	 * 		 Si se pide que se seleccione la unidad de textura, la unidad de textura
	 * 		 asignada pasa a ser actual.
	 * 		 Caso contrario, sólo si debe asignarse.
	 */
	public int allocateTextureUnit(TextureBacker<?> textureBacker, boolean selectTextureUnit) {
		if ( textureBacker != null ) {
			final int textureUnit = this.mruLinkedTextureUnitSlotAllocator.get(textureBacker.getTextureLinkedAllocatableElement()).getId();
			
			if ( selectTextureUnit ) {
				if ( this.lastBindedTextureBacker == null ) {
					this.gl.glActiveTexture(GL21.GL_TEXTURE0 + textureUnit);
				}
			}
			
			this.lastBindedTextureBacker = null;
			
			return textureUnit;
		}
		else {
			throw new NullPointerException();
		}
	}
	
	/**
	 * @pre El backer de objeto de textura no puede ser nulo
	 * @post Obtiene la unidad de textura para el backer del objeto textura especificado
	 * @param textureObjectBacker
	 * @return
	 */
	public <V extends Vectorf, P extends RasterPicture<V>> int allocateTextureUnit(TextureObjectBacker<V, P> textureObjectBacker) {
		final GL21 gl = this.gl;
		
		boolean setParameters = ( textureObjectBacker.getTextureObject() != textureObjectBacker.getTextureBacker().getCachedTextureObject() );
			
		final int textureUnit = this.allocateTextureUnit(textureObjectBacker.getTextureBacker(), setParameters);
		
		if ( setParameters ) {
			final int glTextureType = textureObjectBacker.getTextureBacker().getGlTextureType();
			final TextureObject<P> textureObject = textureObjectBacker.getTextureObject();
			final TextureQualitySettings qualitySettings = textureObject.getQualitySettings();
			
			final Map<CoordinateWrap, Integer> glValueByCoordinateWrap = new EnumMap<CoordinateWrap, Integer>(CoordinateWrap.class);
			glValueByCoordinateWrap.put(CoordinateWrap.CLAMP_TO_BORDER, GL21.GL_CLAMP_TO_BORDER);
			glValueByCoordinateWrap.put(CoordinateWrap.MIRROR_CLAMP_TO_BORDER, GL21.GL_CLAMP_TO_BORDER); // Necesita implementación con shader
			glValueByCoordinateWrap.put(CoordinateWrap.MIRRORED_REPEAT, GL21.GL_MIRRORED_REPEAT);
			glValueByCoordinateWrap.put(CoordinateWrap.REPEAT, GL21.GL_REPEAT);
			
			textureObject.visit(new TextureObject.UncheckedVisitor<Void>() {

				@Override
				public Void visit(TextureObject2d textureObject) {
					gl.glTexParameteri(glTextureType, GL21.GL_TEXTURE_WRAP_S, glValueByCoordinateWrap.get(textureObject.getCoordinateWrapS()));
					gl.glTexParameteri(glTextureType, GL21.GL_TEXTURE_WRAP_T, glValueByCoordinateWrap.get(textureObject.getCoordinateWrapT()));
					return null;
				}

				@Override
				public Void visit(TextureObject3d textureObject) {
					gl.glTexParameteri(glTextureType, GL21.GL_TEXTURE_WRAP_S, glValueByCoordinateWrap.get(textureObject.getCoordinateWrapS()));
					gl.glTexParameteri(glTextureType, GL21.GL_TEXTURE_WRAP_T, glValueByCoordinateWrap.get(textureObject.getCoordinateWrapT()));
					gl.glTexParameteri(glTextureType, GL21.GL_TEXTURE_WRAP_R, glValueByCoordinateWrap.get(textureObject.getCoordinateWrapR()));
					return null;
				}
				
			});
			
			gl.glTexParameter(glTextureType, GL21.GL_TEXTURE_BORDER_COLOR, textureObject.getBorderColor().store());
			
			gl.glTexParameteri(glTextureType, GL21.GL_TEXTURE_MAG_FILTER, qualitySettings.getMagFilter().equals(TextureQualitySettings.MagFilter.NEAREST) ? GL21.GL_NEAREST : GL21.GL_LINEAR);
			gl.glTexParameteri(glTextureType, GL21.GL_TEXTURE_MIN_FILTER, qualitySettings.getMinFilter().equals(TextureQualitySettings.MinFilter.NEAREST) ? GL21.GL_NEAREST : GL21.GL_LINEAR);
			
			textureObjectBacker.getTextureBacker().setCachedTextureObject(textureObject);
		}
		
		return textureUnit;
	}
	
	/**
	 * @pre El backer de textura no puede ser nulo
	 * @post Notifica la destrucción del backer de textura especificado
	 */
	void notifyTextureBackerDestroy(TextureBacker<?> textureBacker) {
		if ( textureBacker != null ) {
			this.mruLinkedTextureUnitSlotAllocator.detach(textureBacker.getTextureLinkedAllocatableElement());
		}
		else {
			throw new NullPointerException();
		}
	}
	
	/**
	 * @post Verifica que no haya sido destruido
	 */
	private void checkDestroyed() {
		if ( this.isDestroyed ) {
			throw new IllegalStateException("Cannot use an destroyed textured geometry renderer subsystem");
		}
	}
	
	public void destroy() {
		this.checkDestroyed();
		
		if ( !shaderEntryPerShaderProfile.isEmpty() ) {
			final Set<LayersRenderingShadersUnit> destroyedShaderUnits = new HashSet<LayersRenderingShadersUnit>();
			
			for ( ShaderEntry eachShaderEntry : shaderEntryPerShaderProfile.values() ) {
				final LayersRenderingShadersUnit eachShaderUnit = eachShaderEntry.shaderUnit;
				
				if ( destroyedShaderUnits.add(eachShaderUnit) ) {
					eachShaderUnit.destroy();
				}
			}
		}
		
		this.isDestroyed = true;
	}
}
