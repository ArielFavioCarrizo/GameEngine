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

import java.util.EnumMap;
import java.util.List;
import java.util.Map;

import com.arielcarrizo.gameengine.renderengine.backend.opengl.gl21.GL21;
import com.arielcarrizo.gameengine.renderengine.backend.opengl.gl21.Gl21RenderEngineBackendObject;
import com.arielcarrizo.gameengine.renderengine.backend.opengl.gl21.Gl21RenderEngineBackendSystem;
import com.esferixis.gameengine.renderengine.backend.exception.OutOfMemoryException;
import com.esferixis.gameengine.renderengine.backend.texture.Texture;
import com.esferixis.gameengine.renderengine.backend.texture.TextureObject;
import com.esferixis.gameengine.renderengine.backend.texture.TextureObject2d;
import com.esferixis.gameengine.renderengine.backend.texture.TextureObject2dObserver;
import com.esferixis.gameengine.renderengine.backend.texture.TextureObject3d;
import com.esferixis.gameengine.renderengine.backend.texture.TextureObject3dObserver;
import com.esferixis.gameengine.renderengine.backend.texture.TextureObjectObserver;
import com.esferixis.gameengine.renderengine.backend.texture.TextureObject.Visitor;
import com.esferixis.gameengine.renderengine.picture.RasterPicture;
import com.esferixis.gameengine.renderengine.picture.RasterPicture2d;
import com.esferixis.gameengine.renderengine.picture.RasterPicture3d;
import com.esferixis.gameengine.renderengine.texture.CoordinateWrap;
import com.esferixis.gameengine.renderengine.texture.TextureQualitySettings;
import com.esferixis.gameengine.renderengine.texture.TextureQualitySettingsObserver;
import com.esferixis.gameengine.renderengine.texture.TextureQualitySettings.MagFilter;
import com.esferixis.gameengine.renderengine.texture.TextureQualitySettings.MinFilter;
import com.esferixis.math.Vector2f;
import com.esferixis.math.Vector3f;
import com.esferixis.math.Vector4f;
import com.esferixis.math.Vectorf;
import com.esferixis.misc.counter.OverflowCounterException;
import com.esferixis.misc.observer.Observer;
import com.esferixis.misc.observer.Observer.Type;

/**
 * Backer de objeto de textura
 */
public class TextureObjectBacker<V extends Vectorf, P extends RasterPicture<V>> extends Gl21RenderEngineBackendObject {
	private final TextureObject<P> textureObject;
	private TextureBacker<P> textureBacker;
	
	private final TextureQualitySettingsObserver textureQualitySettingsObserver;
	private final TextureObjectObserver<P, ? extends TextureObject<P>> textureObjectObserver;
	
	private final MeshLayersConfigBacker meshLayersConfigBacker;
	
	/**
	 * @post Crea el "backer" con el backer de configuración de capas de malla, el objeto de textura y el backer de textura especificados
	 */
	public TextureObjectBacker(MeshLayersConfigBacker meshLayersConfigBacker, TextureObject<P> textureObject, TextureBacker<P> textureBacker) throws OutOfMemoryException {
		super(meshLayersConfigBacker.getRenderEngineBackend());
		if ( ( textureObject != null ) && ( textureBacker != null ) ) {
			this.meshLayersConfigBacker = meshLayersConfigBacker;
			
			this.textureObject = textureObject;
			this.textureBacker = textureBacker;
			
			textureBacker.notifyNewDependency();
			
			if ( !textureBacker.isLoaded() ) {
				meshLayersConfigBacker.notifyDirtyState();
			}
			
			this.textureQualitySettingsObserver = new TextureQualitySettingsObserver(com.esferixis.misc.observer.Observer.Type.STRONG) {

				@Override
				protected void notifyMagFilterChange(MagFilter newMagFilter) {
					TextureObjectBacker.this.invalidateParametersCache();
				}

				@Override
				protected void notifyMinFilterChange(MinFilter newMinFilter) {
					TextureObjectBacker.this.invalidateParametersCache();
				}
				
			};
			
			this.textureObjectObserver = textureObject.visit(new TextureObject.UncheckedVisitor<TextureObjectObserver<P, ? extends TextureObject<P>> >() {

				@Override
				public TextureObjectObserver<P, ? extends TextureObject<P>> visit(TextureObject2d textureObject) {
					return (TextureObjectObserver) new TextureObject2dObserver(Type.STRONG) {

						@Override
						protected void notifyTextureObjectChange(Texture<RasterPicture2d> texture) {
							TextureObjectBacker.this.changeTexture( (Texture<P>) texture);
						}

						@Override
						protected void notifyCoordinateWrapSChange(CoordinateWrap newCoordinateWrap) {
							TextureObjectBacker.this.invalidateParametersCache();
						}

						@Override
						protected void notifyCoordinateWrapTChange(CoordinateWrap newCoordinateWrap) {
							TextureObjectBacker.this.invalidateParametersCache();
						}

						@Override
						protected void notifyBorderColorChange(Vector4f borderColor) {
							TextureObjectBacker.this.invalidateParametersCache();
						}

						@Override
						protected void notifyTextureQualitySettingsChange(TextureQualitySettings qualitySettings) {
							TextureObjectBacker.this.invalidateParametersCache();
							TextureObjectBacker.this.textureQualitySettingsObserver.detach();
							TextureObjectBacker.this.textureQualitySettingsObserver.attach(qualitySettings);
						}
						
					};
				}

				@Override
				public TextureObjectObserver<P, ? extends TextureObject<P>> visit(TextureObject3d textureObject) {
					return (TextureObjectObserver) new TextureObject3dObserver(Type.STRONG) {

						@Override
						protected void notifyTextureObjectChange(Texture<RasterPicture3d> texture) {
							TextureObjectBacker.this.changeTexture( (Texture<P>) texture);
						}

						@Override
						protected void notifyCoordinateWrapSChange(CoordinateWrap newCoordinateWrap) {
							TextureObjectBacker.this.invalidateParametersCache();
						}

						@Override
						protected void notifyCoordinateWrapTChange(CoordinateWrap newCoordinateWrap) {
							TextureObjectBacker.this.invalidateParametersCache();
						}

						@Override
						protected void notifyCoordinateWrapRChange(CoordinateWrap newCoordinateWrap) {
							TextureObjectBacker.this.invalidateParametersCache();
						}

						@Override
						protected void notifyBorderColorChange(Vector4f borderColor) {
							TextureObjectBacker.this.invalidateParametersCache();
						}

						@Override
						protected void notifyTextureQualitySettingsChange(TextureQualitySettings qualitySettings) {
							TextureObjectBacker.this.invalidateParametersCache();
							TextureObjectBacker.this.textureQualitySettingsObserver.detach();
							TextureObjectBacker.this.textureQualitySettingsObserver.attach(qualitySettings);
						}
						
					};
				}
			});
			
			( (TextureObjectObserver<P, TextureObject<P>>) this.textureObjectObserver).attach( (TextureObject<P>) this.textureObject);
			this.textureQualitySettingsObserver.attach(this.textureObject.getQualitySettings());
		}
		else {
			throw new NullPointerException();
		}
	}
	
	/**
	 * @post Invalida la caché de parámetros
	 */
	private void invalidateParametersCache() {
		if ( this.textureBacker.getCachedTextureObject() == this.getTextureObject() ) {
			this.textureBacker.setCachedTextureObject(null);
		}
	}
	
	/**
	 * @post Cambia la textura
	 */
	private void changeTexture(Texture<P> newTexture) throws OutOfMemoryException {
		final TextureBacker<P> newTextureBacker = this.renderEngineBackend.getTextureBacker(newTexture);
		
		newTextureBacker.notifyNewDependency();
		
		this.invalidateParametersCache();
		this.textureBacker.notifyRemovedDependency();
		
		this.textureBacker = newTextureBacker;
	}
	
	/**
	 * @post Devuelve el objeto textura
	 */
	public TextureObject<P> getTextureObject() {
		return this.textureObject;
	}
	
	/**
	 * @post Devuelve el backer de textura
	 */
	public TextureBacker<P> getTextureBacker() {
		return this.textureBacker;
	}
	
	/**
	 * @post Asigna los parámetros del objeto textura al componente especificado
	 */
	public void setTo(final LayersRenderingShadersUnit.TextureRenderLayer<V>.Component component) {
		if ( component != null ) {
			if ( this.textureBacker.isLoaded() ) {
				final GL21 gl = this.gl;
				final LayeredGeometryRendererSubsystem texturedGeometryRendererSubsystem = this.renderEngineBackend.getLayeredGeometryRendererSubsystem();
				
				boolean setParameters = ( this.getTextureObject() != this.getTextureBacker().getCachedTextureObject() );
				
				final int textureUnit = texturedGeometryRendererSubsystem.allocateTextureUnit(this.getTextureBacker(), setParameters);
				
				if ( setParameters ) {
					final int glTextureType = this.getTextureBacker().getGlTextureType();
					final TextureObject<P> textureObject = this.getTextureObject();
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
					
					this.getTextureBacker().setCachedTextureObject(textureObject);
				}
				
				component.setTextureUnit(textureUnit);
				
				final List<Integer> pictureDimensions = textureBacker.getPictureDimensions();
				
				textureObject.visit(new TextureObject.UncheckedVisitor<Void>() {
					
					@Override
					public Void visit(TextureObject2d textureObject) {
						component.setMirrorSCoordinateGapPictureWidth((textureObject.getCoordinateWrapS() == CoordinateWrap.MIRROR_CLAMP_TO_BORDER) ? pictureDimensions.get(0) : 0);
						component.setMirrorTCoordinateGapPictureWidth((textureObject.getCoordinateWrapT() == CoordinateWrap.MIRROR_CLAMP_TO_BORDER) ? pictureDimensions.get(1) : 0);
						return null;
					}
	
					@Override
					public Void visit(TextureObject3d textureObject) {
						component.setMirrorSCoordinateGapPictureWidth((textureObject.getCoordinateWrapS() == CoordinateWrap.MIRROR_CLAMP_TO_BORDER) ? pictureDimensions.get(0) : 0);
						component.setMirrorTCoordinateGapPictureWidth((textureObject.getCoordinateWrapT() == CoordinateWrap.MIRROR_CLAMP_TO_BORDER) ? pictureDimensions.get(1) : 0);
						component.setMirrorRCoordinateGapPictureWidth((textureObject.getCoordinateWrapR() == CoordinateWrap.MIRROR_CLAMP_TO_BORDER) ? pictureDimensions.get(2) : 0);
						return null;
					}
					
				});
			}
			else {
				throw new IllegalStateException("Missing texture load");
			}
		}
		else {
			throw new NullPointerException();
		}
		
	}
	
	/**
	 * @post Destruye el "backer"
	 */
	public void destroy() {
		this.textureObjectObserver.detach();
		
		this.textureQualitySettingsObserver.detach();
		this.textureBacker.notifyRemovedDependency();
	}
}
