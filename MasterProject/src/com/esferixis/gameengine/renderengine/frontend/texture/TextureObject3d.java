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

import com.esferixis.gameengine.renderengine.picture.RasterPicture3d;
import com.esferixis.gameengine.renderengine.texture.CoordinateWrap;
import com.esferixis.gameengine.renderengine.texture.TextureQualitySettings;
import com.esferixis.misc.ElementCallback;
import com.esferixis.misc.observer.ObserverManager;

/**
 * @author ariel
 *
 */
public final class TextureObject3d extends TextureObject<RasterPicture3d> {
	transient final ObserverManager<TextureObject3d, TextureObject3dObserver> observerManager = new ObserverManager<TextureObject3d, TextureObject3dObserver>(this, TextureObject3dObserver.class);
	
	private static final long serialVersionUID = -6798146562284487811L;
	private CoordinateWrap coordinateWrapS, coordinateWrapT, coordinateWrapR;
	
	public static class Essence extends TextureObject.Essence<RasterPicture3d> {
		private CoordinateWrap coordinateWrapS, coordinateWrapT, coordinateWrapR;
		
		/**
		 * @pre Ninguno de los dos puede ser nulo
		 * @post Crea la esencia con la textura y la configuración de calidad
		 * 		 especificados
		 * @param texture
		 * @param qualitySettings
		 */
		public Essence(Texture<RasterPicture3d> texture, TextureQualitySettings qualitySettings) {
			super(texture, qualitySettings);

			this.coordinateWrapS = CoordinateWrap.REPEAT;
			this.coordinateWrapT = CoordinateWrap.REPEAT;
			this.coordinateWrapR = CoordinateWrap.REPEAT;
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
		
		/**
		 * @pre La transformación no puede ser nula
		 * @post Asigna la transformación de coordenada R
		 */
		public void setCoordinateWrapR(CoordinateWrap coordinateWrapR) {
			if ( coordinateWrapR != null ) {
				this.coordinateWrapR = coordinateWrapR;
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
	public TextureObject3d(Essence essence) {
		super(essence);
		this.coordinateWrapS = essence.coordinateWrapS;
		this.coordinateWrapT = essence.coordinateWrapT;
		this.coordinateWrapR = essence.coordinateWrapR;
	}
	
	/**
	 * @post Especifica la transformación de coordenada S
	 */
	public void setCoordinateWrapS(final CoordinateWrap coordinateWrapS) {
		if ( coordinateWrapS != null ) {
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
	
	/**
	 * @post Especifica la transformación de coordenada R
	 */
	public void setCoordinateWrapR(final CoordinateWrap coordinateWrapR) {
		if ( coordinateWrapR != null ) {
			this.coordinateWrapR = coordinateWrapR;
		}
		else {
			throw new NullPointerException();
		}
	}
	
	/**
	 * @post Devuelve la transformación de coordenada R
	 */
	public CoordinateWrap getCoordinateWrapR() {
		return this.coordinateWrapR;
	}

	/* (non-Javadoc)
	 * @see com.esferixis.gameengine.renderengine.frontend.texture.TextureObject#visit(com.esferixis.gameengine.renderengine.frontend.texture.TextureObject.Visitor)
	 */
	@Override
	public <R> R visit(com.esferixis.gameengine.renderengine.frontend.texture.TextureObject.Visitor<R> visitor)
			throws Exception {
		return visitor.visit(this);
	}

	/* (non-Javadoc)
	 * @see com.esferixis.gameengine.renderengine.frontend.texture.TextureObject#notifyObservers(com.esferixis.misc.ElementCallBack)
	 */
	@Override
	protected void notifyObservers(
			ElementCallback<TextureObjectObserver<RasterPicture3d, ? extends TextureObject<RasterPicture3d>>> elementCallBack) {
		this.observerManager.notifyObservers(elementCallBack);
	}
}
