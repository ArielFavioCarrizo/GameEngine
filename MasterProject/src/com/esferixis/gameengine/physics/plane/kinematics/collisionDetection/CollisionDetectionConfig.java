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

package com.esferixis.gameengine.physics.plane.kinematics.collisionDetection;

import com.esferixis.math.intervalarithmetic.FloatClosedInterval;

/**
 * @author ariel
 *
 */
public final class CollisionDetectionConfig {
	private final FloatClosedInterval distanceDeltaInterval;
	
	/**
	 * @pre El intervalo de distancia no puede ser nulo y tiene que ser positivo
	 * @post Crea una especificación de precisión con el intervalo de distancia aceptable.
	 */
	public CollisionDetectionConfig(FloatClosedInterval distanceDeltaInterval) {
		if ( distanceDeltaInterval != null ) {
			if ( distanceDeltaInterval.getMin() > 0.0f ) {
				this.distanceDeltaInterval = distanceDeltaInterval;
			}
			else {
				throw new IllegalArgumentException("Expected positive distance delta interval");
			}
		}
		else {
			throw new IllegalArgumentException("Invalid parameters value");
		}
	}
	
	/**
	 * @post Devuelve el intervalo de distancia aceptable
	 */
	public FloatClosedInterval distanceDeltaInterval() {
		return this.distanceDeltaInterval;
	}
	
	/**
	 * @pre La cadena no puede ser nula
	 * @post Parsea la cadena especificada
	 */
	public static CollisionDetectionConfig parse(String string) {
		if ( string != null ) {
			return new CollisionDetectionConfig(FloatClosedInterval.parse(string));
		}
		else {
			throw new NullPointerException();
		}
	}
	
	/**
	 * @post Devuelve una representación en cadena de carácteres
	 */
	@Override
	public String toString() {
		return "CollisionDetectionPrecision(" + this.distanceDeltaInterval + ")";
	}
}
