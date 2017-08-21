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
package com.esferixis.gameengine.renderengine.texture;

import java.io.Serializable;

import com.esferixis.misc.ElementCallback;
import com.esferixis.misc.observer.ObserverManager;

/**
 * Configuración de calidad de textura
 * 
 * @author ariel
 *
 */
public final class TextureQualitySettings implements Serializable {
	/**
	 * 
	 */
	private static final long serialVersionUID = -3432208898805827126L;
	final ObserverManager<TextureQualitySettings, TextureQualitySettingsObserver> observerManager = new ObserverManager<TextureQualitySettings, TextureQualitySettingsObserver>(this, TextureQualitySettingsObserver.class);;
	
	// Filtro de magnificación
	public enum MagFilter {
		NEAREST,
		LINEAR;
	}
	
	/**
	 * Especulativo: En algunas implementaciones no se pueden hacer mipmaps
	 * 				 a texturas NPOT, y o ni siquiera en ningún caso.
	 * 				 En tal caso se harán implementaciones en shader, si
	 * 				 no fuera conveniente o no fuera posible, se ignorarán las
	 * 				 opciones x_MIPMAP_x reemplazándose por LINEAR, o
	 * 				 sea sin mipmaps (Penalización de rendimiento).
	 * 				 Ver restricciones de versiones de OpenGL y OpenGL ES
	 */
	// Filtro de disminución
	public enum MinFilter {
		NEAREST,
		LINEAR,
		NEAREST_MIPMAP_NEAREST,
		LINEAR_MIPMAP_NEAREST,
		NEAREST_MIPMAP_LINEAR,
		LINEAR_MIPMAP_LINEAR
	}
	
	private MagFilter magFilter;
	private MinFilter minFilter;
	
	/**
	 * @post Crea la configuración de calidad con el filtro de magnificación "LINEAR" y el
	 * 		 de disminución "NEAREST_MIPMAP_LINEAR"
	 */
	public TextureQualitySettings() {
		this(MinFilter.NEAREST_MIPMAP_LINEAR, MagFilter.LINEAR);
	}
	
	/**
	 * @pre Ninguno de los dos puede ser nulo
	 * @post Crea la configuración de calidad con el filtro de disminución y el de magnificación
	 * 		 especificados
	 */
	public TextureQualitySettings(MinFilter minFilter, MagFilter magFilter) {
		if ( ( minFilter != null ) && ( magFilter != null ) ) {
			this.minFilter = minFilter;
			this.magFilter = magFilter;
		}
		else {
			throw new NullPointerException();
		}
	}
	
	/**
	 * @post Devuelve el filtro de magnificación
	 */
	public final MagFilter getMagFilter() {
		return this.magFilter;
	}
	
	/**
	 * @post Especifica el filtro de magnificación
	 */
	public final void setMagFilter(final MagFilter magFilter) {
		if ( magFilter != null ) {
			this.observerManager.notifyObservers(new ElementCallback<TextureQualitySettingsObserver>() {

				@Override
				public void run(TextureQualitySettingsObserver observer) {
					observer.notifyMagFilterChange(magFilter);
				}
				
			});
			
			this.magFilter = magFilter;
		}
		else {
			throw new NullPointerException();
		}
	}
	
	/**
	 * @post Devuelve el filtro de disminución
	 */
	public final MinFilter getMinFilter() {
		return this.minFilter;
	}
	
	/**
	 * @pre El filtro de disminución no puede ser nulo
	 * @post Especifica el filtro de disminución
	 */
	public final void setMinFilter(final MinFilter minFilter) {
		if ( minFilter != null ) {
			this.observerManager.notifyObservers(new ElementCallback<TextureQualitySettingsObserver>() {

				@Override
				public void run(TextureQualitySettingsObserver observer) {
					observer.notifyMinFilterChange(minFilter);
				}
				
			});
			
			this.minFilter = minFilter;
		}
		else {
			throw new NullPointerException();
		}
	}
}
