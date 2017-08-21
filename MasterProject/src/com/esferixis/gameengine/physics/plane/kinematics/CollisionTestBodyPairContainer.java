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
import com.esferixis.math.intervalarithmetic.FloatClosedInterval;
import com.esferixis.misc.collection.set.BinarySet;

/**
 * @author ariel
 *
 */
public abstract class CollisionTestBodyPairContainer implements Serializable {
	/**
	 * 
	 */
	private static final long serialVersionUID = -6920238251134510922L;
	
	private final FloatClosedInterval collisionDistanceInterval;
	
	/**
	 * @pre El intervalo de distancia de colisión no puede ser nulo
	 * @post Crea el contenedor de pares de colisión con el intervalo de distancia de colisión
	 * 	 	 especificado.
	 */
	public CollisionTestBodyPairContainer(FloatClosedInterval collisionDistanceInterval) {
		if ( collisionDistanceInterval != null ) {
			this.collisionDistanceInterval = collisionDistanceInterval;
		}
		else {
			throw new NullPointerException();
		}
	}
	
	/**
	 * @post Devuelve el intervalo de distancia de colisión
	 */
	FloatClosedInterval getCollisionDistanceInterval() {
		return this.collisionDistanceInterval;
	}
	
	/**
	 * @pre El contenedor tiene que ser nulo, o el mismo
	 * @post Comprueba que no tenga contenedor o que sea el mismo
	 */
	private void checkContainerForAdd(PlaneKinematicBody<? extends PlaneKinematicMapper> body) {
		if ( !this.isAddable(body) ) {
			throw new IllegalStateException("Expected pair with bodies with no container or with this container");
		}
	}
	
	/**
	 * @pre El cuerpo no puede ser nulo
	 * @post Devuelve si el cuerpo cinemático es agregable
	 */
	public final boolean isAddable(PlaneKinematicBody<? extends PlaneKinematicMapper> body) {
		if ( body != null ) {
			return ( body.getCollisionTestBodyPairContainer() == null ) || ( body.getCollisionTestBodyPairContainer() == this );
		}
		else {
			throw new NullPointerException();
		}
	}
	
	/**
	 * @pre No puede ser nulo el par, y no tiene que contener ningún cuerpo cinemático que esté contenido
	 * 		en otro contenedor.
	 * @post Especifica la respuesta para la colisión entre el par de cuerpos especificados.
	 * 		 Si la respuesta es nula, se considera que no habrá test de colisión.
	 * 		 Devuelve la respuesta anterior
	 */
	public final CollisionResponse setCollisionResponse(BinarySet<PlaneKinematicBody<? extends PlaneKinematicMapper>> pair, CollisionResponse response) {
		if ( pair != null ) {
			this.checkContainerForAdd(pair.getElement1());
			this.checkContainerForAdd(pair.getElement2());
			
			final CollisionResponse oldResponse = this.setCollisionResponse_checked(pair, response);
			
			if ( response != oldResponse ) {
			
				if ( response == null ) {
					pair.getElement1().detachCollisionTestBodyPairContainer();
					pair.getElement2().detachCollisionTestBodyPairContainer();
				}
				else if ( oldResponse == null ) {
					pair.getElement1().attachCollisionTestBodyPairContainer(this);
					pair.getElement2().attachCollisionTestBodyPairContainer(this);
				}
			}
			
			return oldResponse;
		}
		else {
			throw new NullPointerException();
		}
	}
	
	/**
	 * @pre No puede ser nulo
	 * @post Especifica la respuesta para la colisión entre el par de cuerpos especificados.
	 * 		 Si la respuesta es nula, se considera que no habrá test de colisión.
	 * 		 Devuelve la respuesta anterior
	 */
	protected abstract CollisionResponse setCollisionResponse_checked(BinarySet<PlaneKinematicBody<? extends PlaneKinematicMapper>> pair, CollisionResponse response);
	
	/**
	 * @pre El par no puede ser nulo
	 * @post Devuelve la respuesta de colisión del par especificado
	 */
	public final CollisionResponse getCollisionResponse(BinarySet<PlaneKinematicBody<? extends PlaneKinematicMapper>> pair) {
		if ( pair != null ) {
			return this.getCollisionResponse_checked(pair);
		}
		else {
			throw new NullPointerException();
		}
	}
	
	/**
	 * @post Devuelve la respuesta de colisión del par especificado
	 */
	protected abstract CollisionResponse getCollisionResponse_checked(BinarySet<PlaneKinematicBody<? extends PlaneKinematicMapper>> pair);
	
	/**
	 * @post Notifica un cambio en el objeto cinemático especificado
	 */
	protected abstract void notifyChange(PlaneKinematicBody<? extends PlaneKinematicMapper> planeKinematicBody);
	
	/**
	 * @post Devuelve el emisor
	 * 		 Los pares tienen que conservarse.
	 */
	protected abstract CollisionTestBodyPairEmmiter getEmmiter();
}
