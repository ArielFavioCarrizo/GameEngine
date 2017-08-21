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

package com.esferixis.gameengine.renderengine.backend.space.light;

import com.esferixis.math.Vector3f;
import com.esferixis.misc.ElementCallback;
import com.esferixis.misc.observer.ObserverManager;

/**
 * Fuente de luz puntual
 * 
 * @author Ariel Favio Carrizo
 *
 */
public final class PointLightSource extends LightSource {
	private Vector3f position;
	private Vector3f energicColor;
	
	final ObserverManager<PointLightSource, PointLightSourceObserver> observerManager = new ObserverManager<PointLightSource, PointLightSourceObserver>(this, PointLightSourceObserver.class);
	
	/**
	 * @pre La posición y el color enérgico no pueden ser nulos
	 * @post Crea una fuente lumínica puntual con la posición y el
	 * 		 color enérgico especificados
	 */
	public PointLightSource(Vector3f position, Vector3f energicColor) {
		super();
		if ( ( position != null ) && ( energicColor != null ) ) {
			this.position = position;
			this.energicColor = energicColor;
		}
		else {
			throw new NullPointerException();
		}
	}
	
	/* (non-Javadoc)
	 * @see com.esferixis.gameengine.renderengine.light.LightSource#notifyObservers(com.esferixis.misc.ElementCallBack)
	 */
	@Override
	protected void notifyObservers(
			ElementCallback<? super LightSourceObserver<?>> elementCallBack) {
		this.observerManager.notifyObservers(elementCallBack);
	}
	
	/**
	 * @pre La posición no puede ser nula
	 * @post Especifica la posición de la fuente
	 */
	public void setPosition(final Vector3f position) {
		if ( position != null ) {
			this.observerManager.notifyObservers(new ElementCallback<PointLightSourceObserver>() {

				@Override
				public void run(PointLightSourceObserver element) {
					element.registerPositionChange(position);
				}
				
			});
			this.position = position;
		}
		else {
			throw new NullPointerException();
		}
	}
	
	/**
	 * @post Devuelve la posición de la fuente
	 */
	public Vector3f getPosition() {
		return this.position;
	}
	
	/**
	 * @pre El color enérgico no puede ser nulo
	 * @post Especifica el color de la fuente en términos
	 * 		 de energía por cada componente que se mide en
	 * 		 nits (cd/m^2)
	 */
	public void setEnergicColor(final Vector3f energicColor) {
		if ( energicColor != null ) {
			this.observerManager.notifyObservers(new ElementCallback<PointLightSourceObserver>() {
	
				@Override
				public void run(PointLightSourceObserver element) {
					element.registerEnergicColorChange(energicColor);
				}
				
			});
			this.energicColor = energicColor;
		}
		else {
			throw new NullPointerException();
		}
	}
	
	/**
	 * @post Devuelve el color de la fuente en términos
	 * 		 de energía por cada componente que se mide en
	 * 		 nits (cd/m^2)
	 */
	public Vector3f getEnergicColor(Vector3f energicColor) {
		return this.energicColor;
	}

	/* (non-Javadoc)
	 * @see com.esferixis.gameengine.renderengine.light.LightSource#accept(com.esferixis.gameengine.renderengine.light.LightSource.Visitor)
	 */
	@Override
	public <V> V accept(Visitor<V> visitor) {
		return visitor.visit(this);
	}
}
