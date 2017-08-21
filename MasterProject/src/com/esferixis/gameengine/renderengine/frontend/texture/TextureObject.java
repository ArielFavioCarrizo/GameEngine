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

package com.esferixis.gameengine.renderengine.frontend.texture;

import java.io.Serializable;

import com.esferixis.gameengine.renderengine.picture.RasterPicture;
import com.esferixis.gameengine.renderengine.texture.TextureQualitySettings;
import com.esferixis.math.Vector4f;
import com.esferixis.misc.ElementCallback;
import com.esferixis.misc.dynamicFields.DynamicFieldsContainer;

/**
 * Textura
 * 
 * @author Ariel Favio Carrizo
 *
 */
public abstract class TextureObject<P extends RasterPicture<?>> implements Serializable {
	/**
	 * 
	 */
	private static final long serialVersionUID = -3478766056321626550L;

	public static interface Visitor<R> {
		public R visit(TextureObject2d textureObject) throws Exception;
		public R visit(TextureObject3d textureObject) throws Exception;
	}
	
	public static interface UncheckedVisitor<R> extends Visitor<R> {
		public R visit(TextureObject2d textureObject);
		public R visit(TextureObject3d textureObject);
	}
	
	/**
	 * @post Notifica a los observadores
	 */
	protected abstract void notifyObservers(ElementCallback< TextureObjectObserver<P, ? extends TextureObject<P> > > elementCallBack);
	
	public static class Essence<P extends RasterPicture<?>> {
		private final Texture<P> texture;
		private final TextureQualitySettings qualitySettings;
		
		private Vector4f borderColor;
		
		/**
		 * @pre La textura y la configuración de calidad no pueden ser nulos
		 * @post Crea la esencia con la textura y la configuración de
		 * 		 calidad especificados
		 */
		public Essence(Texture<P> texture, TextureQualitySettings qualitySettings) {
			if ( ( texture != null ) && ( qualitySettings != null ) ) {
				this.texture = texture;
				this.qualitySettings = qualitySettings;
				this.borderColor = new Vector4f(0.0f, 0.0f, 0.0f, 0.0f);
			}
			else {
				throw new NullPointerException();
			}
		}
		
		/**
		 * @pre El color del borde no puede ser nulo
		 * @post Especifica el color de borde
		 */
		public final void setBorderColor(Vector4f borderColor) {
			if ( borderColor != null ) {
				this.borderColor = borderColor;
			}
			else {
				throw new NullPointerException();
			}
		}
	}
	
	private Texture<P> texture;
	private Vector4f borderColor;
	
	private final TextureQualitySettings qualitySettings;
	
	private final DynamicFieldsContainer dynamicFieldsContainer;
	
	/**
	 * @pre La esencia no puede ser nula
	 * @post Crea el objeto de textura con la esencia especificada
	 */
	TextureObject(Essence<P> essence) {
		if ( essence != null ) {
			this.texture = essence.texture;
			this.borderColor = essence.borderColor;
			
			this.qualitySettings = essence.qualitySettings;
		
			this.dynamicFieldsContainer = new DynamicFieldsContainer();
		}
		else {
			throw new NullPointerException();
		}
	}
	
	/**
	 * @post Devuelve la textura
	 */
	public Texture<P> getTexture() {
		return this.texture;
	}
	
	/**
	 * @post Especifica la textura
	 */
	public void setTexture(final Texture<P> texture) {
		if ( texture != null ) {
			final Texture<P> oldTexture = texture;
			
			try {
				this.notifyObservers(new ElementCallback<TextureObjectObserver<P, ? extends TextureObject<P>> >(){
					@Override
					public void run(TextureObjectObserver<P, ? extends TextureObject<P>> observer) {
						observer.notifyTextureObjectChange(texture);
					}
					
				});
			}
			catch (RuntimeException e) {
				this.notifyObservers(new ElementCallback<TextureObjectObserver<P, ? extends TextureObject<P>> >(){
					@Override
					public void run(TextureObjectObserver<P, ? extends TextureObject<P>> observer) {
						observer.notifyTextureObjectChange(oldTexture);
					}
					
				});
				
				throw e;
			}
			
			this.texture = texture;
		}
		else {
			throw new NullPointerException();
		}
	}
	
	/**
	 * @post Especifica el color de borde
	 */
	public final void setBorderColor(final Vector4f borderColor) {
		if ( borderColor != null ) {
			this.borderColor = borderColor;
		}
		else {
			throw new NullPointerException();
		}
	}
	
	/**
	 * @post Devuelve el color del borde
	 */
	public final Vector4f getBorderColor() {
		return this.borderColor;
	}
	
	/**
	 * @post Devuelve la configuración de calidad
	 */
	public TextureQualitySettings getQualitySettings() {
		return this.qualitySettings;
	}
	
	/**
	 * @post Visita con el visitor especificado
	 */
	public abstract <R> R visit(Visitor<R> visitor) throws Exception;
	
	/**
	 * @post Visita con el visitor no chequeado especificado
	 */
	public final <R> R visit(UncheckedVisitor<R> uncheckedVisitor) {
		try {
			return this.visit((Visitor<R>) uncheckedVisitor);
		} catch (Exception e) {
			throw new RuntimeException("Unexpected exception", e);
		}
	}
	
	/**
	 * @post Devuelve el contenedor de miembros dinámicos
	 */
	public final DynamicFieldsContainer dynamicFieldsContainer() {
		return this.dynamicFieldsContainer;
	}
}
