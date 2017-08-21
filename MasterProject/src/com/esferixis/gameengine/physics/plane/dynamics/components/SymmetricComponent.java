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

/**
 * @author ariel
 *
 */
public abstract class SymmetricComponent<T extends SymmetricComponent<T>> implements Serializable {
	/**
	 * 
	 */
	private static final long serialVersionUID = 6716577515774058336L;
	
	private final Class<T> symmetricComponentClass;
	
	/**
	 * @post Crea el componente simétrico con la clase de componente simétrico
	 * 		 especificada
	 */
	public SymmetricComponent(Class<T> symmetricComponentClass) {
		if ( symmetricComponentClass != null ) {
			this.symmetricComponentClass = symmetricComponentClass;
		}
		else {
			throw new NullPointerException();
		}
	}
	
	/**
	 * @post Devuelve la clase de componente simétrico
	 */
	public Class<T> getSymmetricComponentClass() {
		return this.symmetricComponentClass;
	}
	
	/**
	 * @post Notifica el par de interacción con el componente simétrico especificado,
	 * 		 en el tiempo especificado, para la colisión con la frontera superior.
	 * 		 Devuelve si no es la frontera atravesable.
	 */
	public abstract boolean notifyInteractionPair_upperBoundaryCollision(T other, float time);
	
	/**
	 * @post Notifica el par de interacción con el componente simétrico especificado,
	 * 		 en el tiempo especificado, para la colisión con la región intermedia.
	 * 		 Devuelve si no es la frontera atravesable.
	 */
	public abstract void notifyIntermediateRegionCollision(T other, float time);
	
	/**
	 * @post Notifica el par de interacción con el componente simétrico especificado,
	 * 		 en el tiempo especificado, para la colisión con la frontera inferior.
	 * 		 Devuelve si no es la frontera atravesable.
	 */
	public abstract boolean notifyInteractionPair_lowerBoundaryCollision(T other, float time);
}
