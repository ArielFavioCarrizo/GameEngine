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
package com.esferixis.gameengine.renderengine.frontend.plane.kinematics.containers;

import java.io.Serializable;

import com.esferixis.gameengine.renderengine.frontend.plane.kinematics.InstantKinematicRenderer;
import com.esferixis.gameengine.renderengine.frontend.plane.kinematics.objects.KinematicRenderPlaneObject;
import com.esferixis.geometry.plane.finite.FiniteProportionalHolomorphicShape;

/**
 * Contenedor de objetos cinemáticos de renderización de plano
 * 
 * @author ariel
 *
 */
public abstract class KinematicRenderPlaneObjectContainer implements Serializable {
	/**
	 * 
	 */
	private static final long serialVersionUID = 7738433855469854033L;
	
	public interface Factory<C extends KinematicRenderPlaneObjectContainer> {
		public C create();
	}

	/**
	 * @post Crea el objeto contenedor especificado
	 */
	public KinematicRenderPlaneObjectContainer() {
		
	}
	
	/**
	 * @pre El objeto no puede ser nulo
	 * @post Agrega un objeto.
	 * 		 Si está agregado no hace nada
	 */
	public final void addObject(KinematicRenderPlaneObject kinematicRenderPlaneObject) {
		if ( kinematicRenderPlaneObject != null ) {
			this.addObject_checked(kinematicRenderPlaneObject);
		}
		else {
			throw new NullPointerException();
		}
	}
	
	/**
	 * @pre Se asegura que el objeto no es nulo, y que no está agregado
	 * @post Agrega un objeto.
	 * 		 Si está agregado no hace nada
	 */
	protected abstract void addObject_checked(KinematicRenderPlaneObject kinematicRenderPlaneObject);

	/**
	 * @pre El objeto no puede ser nulo
	 * @post Quita un objeto.
	 * 		 Si no está no hace nada
	 */
	public final void removeObject(KinematicRenderPlaneObject kinematicRenderPlaneObject) {
		if ( kinematicRenderPlaneObject != null ) {
			this.removeObject_checked(kinematicRenderPlaneObject);
		}
		else {
			throw new NullPointerException();
		}
	}
	
	/**
	 * @pre Se asegura que el objeto no es nulo
	 * @post Quita un objeto.
	 * 		 Si no está no hace nada
	 */
	protected abstract void removeObject_checked(KinematicRenderPlaneObject kinematicRenderPlaneObject);
	
	/**
	 * @pre El objeto no puede ser nulo
	 * @post Devuelve si el objeto está presente
	 */
	public final boolean isObjectPresent(KinematicRenderPlaneObject kinematicRenderPlaneObject) {
		if ( kinematicRenderPlaneObject != null ) {
			return this.isObjectPresent_checked(kinematicRenderPlaneObject);
		}
		else {
			throw new NullPointerException();
		}
	}
	
	/**
	 * @pre Se asegura que el objeto no es nulo
	 * @post Devuelve si el objeto está presente
	 */
	protected abstract boolean isObjectPresent_checked(KinematicRenderPlaneObject kinematicRenderPlaneObject);
}
