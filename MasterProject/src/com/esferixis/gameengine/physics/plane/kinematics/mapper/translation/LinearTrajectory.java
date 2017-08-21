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

package com.esferixis.gameengine.physics.plane.kinematics.mapper.translation;

import java.util.List;

import com.esferixis.geometry.plane.finite.FiniteProportionalHolomorphicShape;
import com.esferixis.geometry.plane.finite.LineSegment;
import com.esferixis.math.Vector2f;
import com.esferixis.math.intervalarithmetic.FloatClosedInterval;
import com.esferixis.misc.strings.parser.ExpressionParser;
import com.esferixis.misc.strings.parser.ParseException;

/**
 * @author ariel
 *
 */
public final class LinearTrajectory extends Trajectory {
	/**
	 * 
	 */
	private static final long serialVersionUID = -3349903120039043512L;
	private final Vector2f startPosition;
	private final Vector2f velocity;
	
	/**
	 * @pre Ningún parámetro puede ser nulo
	 * @post Crea la trayectoria lineal con el tiempo y la posición inicial, con
	 * 		 la velocidad especificada
	 */
	public LinearTrajectory(float startTime, Vector2f startPosition, Vector2f velocity) {
		super(startTime);
		if ( ( startPosition != null ) && ( velocity != null ) ) {
			this.startPosition = startPosition;
			this.velocity = velocity;
		}
		else {
			throw new NullPointerException();
		}
	}
	
	/**
	 * @post Devuelve la posición inicial
	 */
	@Override
	public Vector2f getStartPosition() {
		return this.startPosition;
	}
	
	/**
	 * @post Devuelve la velocidad
	 */
	public Vector2f getVelocity() {
		return this.velocity;
	}

	/* (non-Javadoc)
	 * @see com.esferixis.gameengine.physics.plane.kinematics.translation.Trajectory#getInstantVelocity(float)
	 */
	@Override
	public Vector2f getInstantVelocity(float time) {
		return this.velocity;
	}

	/* (non-Javadoc)
	 * @see com.esferixis.gameengine.physics.plane.kinematics.translation.Trajectory#getInstantPosition(float)
	 */
	@Override
	public Vector2f getInstantPosition(float time) {
		return this.startPosition.add(this.velocity.scale(time-this.getStartTime()));
	}

	/* (non-Javadoc)
	 * @see com.esferixis.gameengine.physics.plane.kinematics.translation.Trajectory#intervalShape(com.esferixis.math.intervalarithmetic.FloatClosedInterval)
	 */
	@Override
	public FiniteProportionalHolomorphicShape<?> shapeWithBoundingPerimeter(FloatClosedInterval timeInterval) {
		return this.boundingRegion(timeInterval);
	}
	
	/* (non-Javadoc)
	 * @see com.arielcarrizo.gameengine.physics.plane.kinematics.translation.Trajectory#boundingRegion(com.arielcarrizo.math.intervalarithmetic.FloatClosedInterval)
	 */
	@Override
	public FiniteProportionalHolomorphicShape<?> boundingRegion(FloatClosedInterval timeInterval) {
		return new LineSegment(this.getInstantPosition(timeInterval.getMin()), this.getInstantPosition(timeInterval.getMax()));
	}
	
	/**
	 * @pre La cadena no puede ser nula
	 * @post Parsea la trayectoria especificada, indicando la cadena
	 */
	public static LinearTrajectory parse(String string) {
		if ( string != null ) {
			List<String> parameters = ExpressionParser.separateParameters(string);
			
			if ( parameters.size() == 3 ) {
				return new LinearTrajectory(Float.parseFloat(parameters.get(0)), Vector2f.parse(parameters.get(1)), Vector2f.parse(parameters.get(2)));
			}
			else {
				throw new ParseException("Expected 3 parameters");
			}
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
		return "LinearTrajectory(" + this.getStartTime() + ", " + startPosition + ", " + velocity + ")";
	}

	/* (non-Javadoc)
	 * @see com.arielcarrizo.gameengine.physics.plane.kinematics.translation.Trajectory#getMaxDistanceTraveled(com.arielcarrizo.math.intervalarithmetic.FloatClosedInterval)
	 */
	@Override
	public float getMaxDistanceTraveled(FloatClosedInterval timeInterval) {
		if ( timeInterval != null ) {
			return this.velocity.length() * timeInterval.length();
		}
		else {
			throw new NullPointerException();
		}
	}
}
