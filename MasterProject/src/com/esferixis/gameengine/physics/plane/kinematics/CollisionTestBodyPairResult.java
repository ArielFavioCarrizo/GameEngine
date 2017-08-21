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

/**
 * @author ariel
 *
 */
final class CollisionTestBodyPairResult {
	public enum Type {
		UPPER,
		INTERMEDIATE,
		LOWER
	}
	
	private final CollisionTestBodyPair collisionTestBodyPair;
	
	private final float time;
	
	private final Type type;
	
	/**
	 * @pre El par de test no puede ser nulo
	 * @post Crea el resultado con el par de test, el instante de tiempo especificado, y el tipo
	 */
	public CollisionTestBodyPairResult(CollisionTestBodyPair collisionTestBodyPair, float time, Type type) {
		if ( ( collisionTestBodyPair != null ) && ( type != null ) ){
			this.collisionTestBodyPair = collisionTestBodyPair;
			this.time = time;
			this.type = type;
		}
		else {
			throw new NullPointerException();
		}
	}
	
	/**
	 * @post Devuelve el par de test
	 */
	public CollisionTestBodyPair getCollisionTestBodyPair() {
		return this.collisionTestBodyPair;
	}
	
	/**
	 * @post Devuelve el instante de tiempo
	 */
	public float getTime() {
		return this.time;
	}
	
	/**
	 * @post Devuelve el tipo
	 */
	public Type getType() {
		return this.type;
	}
}
