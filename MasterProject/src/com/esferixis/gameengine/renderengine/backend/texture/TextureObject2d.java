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
package com.esferixis.gameengine.renderengine.backend.texture;

import com.esferixis.gameengine.renderengine.picture.RasterPicture2d;
import com.esferixis.gameengine.renderengine.texture.CoordinateWrap;
import com.esferixis.gameengine.renderengine.texture.TextureQualitySettings;
import com.esferixis.math.Vector2f;
import com.esferixis.misc.ElementCallback;
import com.esferixis.misc.observer.ObserverManager;

/**
 * @author ariel
 *
 */
public final class TextureObject2d extends TextureObject<RasterPicture2d> {
	final ObserverManager<TextureObject2d, TextureObject2dObserver> observerManager = new ObserverManager<TextureObject2d, TextureObject2dObserver>(this, TextureObject2dObserver.class);
	private CoordinateWrap coordinateWrapS, coordinateWrapT;
	
	public static class Essence extends TextureObject.Essence<RasterPicture2d> {
		private CoordinateWrap coordinateWrapS, coordinateWrapT;
		
		/**
		 * @pre Ninguno de los dos puede ser nulo
		 * @post Crea la esencia con la textura y la configuración de calidad
		 * 		 especificados
		 * @param texture
		 * @param qualitySettings
		 */
		public Essence(Texture<RasterPicture2d> texture, TextureQualitySettings qualitySettings) {
			super(texture, qualitySettings);

			this.coordinateWrapS = CoordinateWrap.REPEAT;
			this.coordinateWrapT = CoordinateWrap.REPEAT;
		}
		
		/**
		 * @pre La transformación no puede ser nula
		 * @post Asigna la transformación de coordenada S
		 */
		public void setCoordinateWrapS(CoordinateWrap coordinateWrapS) {
			if ( coordinateWrapS != null ) {
				this.coordinateWrapS = coordinateWrapS;
			}
			else {
				throw new NullPointerException();
			}
		}
		
		/**
		 * @pre La transformación no puede ser nula
		 * @post Asigna la transformación de coordenada T
		 */
		public void setCoordinateWrapT(CoordinateWrap coordinateWrapT) {
			if ( coordinateWrapT != null ) {
				this.coordinateWrapT = coordinateWrapT;
			}
			else {
				throw new NullPointerException();
			}
		}
	}
	
	/**
	 * @pre La esencia no puede ser nula
	 * @post Crea el objeto de textura con la esencia especificada
	 */
	public TextureObject2d(Essence essence) {
		super(essence);
		this.coordinateWrapS = essence.coordinateWrapS;
		this.coordinateWrapT = essence.coordinateWrapT;
	}
	
	/**
	 * @post Especifica la transformación de coordenada S
	 */
	public void setCoordinateWrapS(final CoordinateWrap coordinateWrapS) {
		if ( coordinateWrapS != null ) {
			this.observerManager.notifyObservers(new ElementCallback<TextureObject2dObserver>() {

				@Override
				public void run(TextureObject2dObserver eachObserver) {
					eachObserver.notifyCoordinateWrapSChange(coordinateWrapS);
				}
				
			});
			this.coordinateWrapS = coordinateWrapS;
		}
		else {
			throw new NullPointerException();
		}
	}
	
	/**
	 * @post Devuelve la transformación de coordenada S
	 */
	public CoordinateWrap getCoordinateWrapS() {
		return this.coordinateWrapS;
	}
	
	/**
	 * @post Especifica la transformación de coordenada T
	 */
	public void setCoordinateWrapT(final CoordinateWrap coordinateWrapT) {
		if ( coordinateWrapT != null ) {
			this.observerManager.notifyObservers(new ElementCallback<TextureObject2dObserver>() {

				@Override
				public void run(TextureObject2dObserver eachObserver) {
					eachObserver.notifyCoordinateWrapTChange(coordinateWrapT);
				}
				
			});
			this.coordinateWrapT = coordinateWrapT;
		}
		else {
			throw new NullPointerException();
		}
	}
	
	/**
	 * @post Devuelve la transformación de coordenada T
	 */
	public CoordinateWrap getCoordinateWrapT() {
		return this.coordinateWrapT;
	}

	/* (non-Javadoc)
	 * @see com.esferixis.gameengine.renderengine.texture.TextureObject#visit(com.esferixis.gameengine.renderengine.texture.TextureObject.Visitor)
	 */
	@Override
	public <V> V visit(com.esferixis.gameengine.renderengine.backend.texture.TextureObject.Visitor<V> visitor) throws Exception {
		if ( visitor != null ) {
			return visitor.visit(this);
		}
		else {
			throw new NullPointerException();
		}
	}

	/* (non-Javadoc)
	 * @see com.esferixis.gameengine.renderengine.texture.TextureObject#notifyObservers(com.esferixis.misc.ElementCallBack)
	 */
	@Override
	protected void notifyObservers(
			ElementCallback<TextureObjectObserver<RasterPicture2d, ? extends TextureObject<RasterPicture2d>>> elementCallBack) {
		this.observerManager.notifyObservers(elementCallBack);
	}
}
