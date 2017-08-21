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

package com.esferixis.gameengine.physics.plane.kinematics.mapper.rotation;

import java.util.List;

import com.esferixis.geometry.Geometry;
import com.esferixis.math.ExtraMath;
import com.esferixis.math.intervalarithmetic.FloatClosedInterval;
import com.esferixis.misc.strings.parser.ExpressionParser;

/**
 * Rotación alrededor del origen de coordenadas
 * 
 * @author ariel
 *
 */
public final class LinearRotation extends Rotation {
	/**
	 * 
	 */
	private static final long serialVersionUID = -5693369807241774613L;
	
	private final float startAngle;
	private final float angularVelocity;
	
	/**
	 * @post Crea la rotación con el tiempo de comienzo, el ángulo de comienzo y
	 * 		 la velocidad angular especificados
	 */
	public LinearRotation(float startTime, float startAngle, float angularVelocity) {
		super(startTime);
		this.startAngle = startAngle;
		this.angularVelocity = angularVelocity;
	}
	
	/**
	 * @post Devuelve el ángulo de comienzo
	 */
	public float getStartAngle() {
		return this.startAngle;
	}
	
	/**
	 * @post Devuelve la velocidad angular
	 */
	public float getAngularVelocity() {
		return this.angularVelocity;
	}

	/* (non-Javadoc)
	 * @see com.esferixis.gameengine.physics.plane.kinematics.rotation.Rotation#getAngleInterval(com.esferixis.math.intervalarithmetic.FloatClosedInterval)
	 */
	@Override
	public FloatClosedInterval getAngleInterval(FloatClosedInterval timeInterval) {
		float angle1 = this.getAngle(timeInterval.getMin());
		float angle2 = this.getAngle(timeInterval.getMax());
		
		if ( angle1 > angle2 ) {
			float temp = angle1;
			angle1 = angle2;
			angle2 = temp;
		}
		
		if ( ( angle2 - angle1 ) >= ExtraMath.doublePI ) {
			angle2 = angle1 + ExtraMath.doublePI;
		}
		
		final float angleBase = Geometry.angleBase(angle1);
		
		return new FloatClosedInterval(angle1-angleBase, angle2-angleBase);
	}

	/* (non-Javadoc)
	 * @see com.esferixis.gameengine.physics.plane.kinematics.rotation.Rotation#getAngle(float)
	 */
	@Override
	public float getAngle(float time) {
		return this.startAngle + (time - this.getStartTime()) * this.angularVelocity;
	}
	
	/**
	 * @pre La cadena de carácteres no puede ser nula
	 * @post Parsea la rotación lineal en la cadena de carácteres especificada
	 */
	public static LinearRotation parse(String string) {
		if ( string != null ) {
			final List<String> parameters = ExpressionParser.separateParameters(string);
			ExpressionParser.checkParametersQuantity(parameters, 3);
			
			return new LinearRotation(Float.parseFloat(parameters.get(0)), Float.parseFloat(parameters.get(1)), Float.parseFloat(parameters.get(2)));
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
		return "LinearRotation(" + this.getStartTime() + ", " + this.startAngle + ", " + this.angularVelocity + ")";
	}
}
