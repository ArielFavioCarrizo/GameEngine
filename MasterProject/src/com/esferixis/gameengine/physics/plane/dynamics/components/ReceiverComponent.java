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

package com.esferixis.gameengine.physics.plane.dynamics.components;

import java.io.Serializable;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

/**
 * @author ariel
 *
 */
public abstract class ReceiverComponent<T extends TransmiterComponent> implements Serializable {
	/**
	 * 
	 */
	private static final long serialVersionUID = -8927015960184483516L;
	
	private final Class<T> transmiterComponentClass;
	
	/**
	 * @pre El componente de transmisión no puede ser nulo, y las clases
	 * 		de los componentes excluidos tienen que heredear de la clase transmisora
	 * @post Crea el componente de recepción con la clase de transmisión especificada,
	 * 		 con las clases de componentes excluidas especificadas
	 */
	public ReceiverComponent(Class<T> transmiterComponentClass) {
		if ( transmiterComponentClass != null ) {
			this.transmiterComponentClass = transmiterComponentClass;
		}
		else {
			throw new NullPointerException();
		}
	}
	
	/**
	 * @post Devuelve la clase de componente transmisor
	 */
	public Class<T> getTransmiterComponentClass() {
		return this.transmiterComponentClass;
	}
	
	/**
	 * @post Notifica el recibo del componente de transmisión especificado, en el tiempo
	 * 		 especificado, para la colisión con la frontera superior.
	 * 		 Devuelve si no es atravesable la frontera
	 */
	public abstract boolean notifyTransmiter_upperBoundaryCollision(T transmiterComponent, float time);
	
	/**
	 * @post Notifica el recibo del componente de transmisión especificado, en el tiempo
	 * 		 especificado, para la colisión con la región intermedia.
	 */
	public abstract void notifyTransmiter_intermediateRegionCollision(T transmiterComponent, float time);
	
	/**
	 * @post Notifica el recibo del componente de transmisión especificado, en el tiempo
	 * 		 especificado, para la colisión con la frontera inferior.
	 * 		 Devuelve si no es atravesable la frontera.
	 */
	public abstract boolean notifyTransmiter_lowerBoundaryCollision(T transmiterComponent, float time);
}
