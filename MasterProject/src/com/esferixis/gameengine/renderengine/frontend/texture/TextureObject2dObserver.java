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

import com.esferixis.gameengine.renderengine.picture.RasterPicture2d;
import com.esferixis.gameengine.renderengine.texture.CoordinateWrap;
import com.esferixis.math.Vector2f;
import com.esferixis.misc.observer.ObserverManager;

/**
 * @author ariel
 *
 */
public abstract class TextureObject2dObserver extends TextureObjectObserver<RasterPicture2d, TextureObject2d> {

	/**
	 * @post Crea el observador con el tipo especificado
	 * @param type
	 */
	public TextureObject2dObserver(com.esferixis.misc.observer.Observer.Type type) {
		super(type);
	}

	/**
	 * @post Notifica un cambio en la transformación de la coordenada S
	 */
	protected abstract void notifyCoordinateWrapSChange(CoordinateWrap newCoordinateWrap);

	/**
	 * @post Notifica un cambio en la transformación de la coordenada T
	 */
	protected abstract void notifyCoordinateWrapTChange(CoordinateWrap newCoordinateWrap);
	
	/* (non-Javadoc)
	 * @see com.esferixis.misc.observer.Observer#getObserverManager()
	 */
	@Override
	protected final ObserverManager<TextureObject2d, ?> getObserverManager() {
		return this.getObservable().observerManager;
	}
	
}