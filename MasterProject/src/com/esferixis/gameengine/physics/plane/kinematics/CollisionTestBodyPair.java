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

package com.esferixis.gameengine.physics.plane.kinematics;

import java.io.Serializable;

import com.esferixis.gameengine.physics.plane.kinematics.mapper.PlaneKinematicMapper;
import com.esferixis.misc.collection.set.BinarySet;

/**
 * Par de test de colisión
 * 
 * @author ariel
 *
 */
public final class CollisionTestBodyPair implements Serializable  {
	/**
	 * 
	 */
	private static final long serialVersionUID = 8994877699260131715L;
	
	private final BinarySet<PlaneKinematicBody<? extends PlaneKinematicMapper>> bodyPair;
	private CollisionResponse response;
	
	private boolean testUpperBoundaryLimit;
	private boolean testLowerBoundaryLimit;
	
	/**
	 * @pre Ninguno de los parámetros puede ser nulo, y los cuerpos tienen que ser distintos
	 * @post Crea un par de test de colisión con los cuerpos y la respuesta especificada (Puede ser nula)
	 */
	public CollisionTestBodyPair(BinarySet<PlaneKinematicBody<? extends PlaneKinematicMapper>> bodyPair, CollisionResponse response) {
		if ( bodyPair != null ) {
			if ( bodyPair.getElement1() != bodyPair.getElement2() ) {
				this.bodyPair = bodyPair;
				this.setResponse(response);
			}
			else {
				throw new IllegalArgumentException("Expected distinct bodies");
			}
		}
		else {
			throw new NullPointerException();
		}
	}
	
	/**
	 * @post Devuelve el par de cuerpos
	 */
	public BinarySet<PlaneKinematicBody<? extends PlaneKinematicMapper>> getBodyPair() {
		return this.bodyPair;
	}
	
	/**
	 * @post Devuelve si se tiene que probar el límite de frontera
	 */
	boolean getTestUpperBoundaryLimit() {
		return this.testUpperBoundaryLimit;
	}
	
	/**
	 * @post Devuelve si se tiene que probar el límite de frontera
	 */
	boolean getTestLowerBoundaryLimit() {
		return this.testLowerBoundaryLimit;
	}
	
	/**
	 * @post Especifica si se tiene que probar el límite de frontera superior
	 */
	void setTestUpperBoundaryLimit(boolean value) {
		this.testUpperBoundaryLimit = value;
	}
	
	/**
	 * @post Especifica si se tiene que probar el límite de frontera inferior
	 */
	void setTestLowerBoundaryLimit(boolean value) {
		this.testLowerBoundaryLimit = value;
	}
	
	/**
	 * @post Devuelve la respuesta
	 */
	public CollisionResponse getResponse() {
		return this.response;
	}
	
	/**
	 * @post Especifica la respuesta, si es nula se emitará en el contenedor
	 * 		 pero no efectuará ningún test.
	 */
	public void setResponse(CollisionResponse collisionResponse) {
		this.response = collisionResponse;
		this.testUpperBoundaryLimit = true;
		this.testLowerBoundaryLimit = true;
	}
}
