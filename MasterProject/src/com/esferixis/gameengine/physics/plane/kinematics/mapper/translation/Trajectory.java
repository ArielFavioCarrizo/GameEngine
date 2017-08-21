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

import java.io.Serializable;

import com.esferixis.geometry.plane.finite.FiniteProportionalHolomorphicShape;
import com.esferixis.math.Vector2f;
import com.esferixis.math.intervalarithmetic.FloatClosedInterval;
import com.esferixis.misc.strings.parser.ExpressionParser;
import com.esferixis.misc.strings.parser.FunctionParser;
import com.esferixis.misc.strings.parser.ParseException;

/**
 * @author ariel
 *
 */
public abstract class Trajectory implements Serializable {
	/**
	 * 
	 */
	private static final long serialVersionUID = 8387104293987653539L;
	private final float startTime;
	
	private static final ExpressionParser<Trajectory> PARSER = new ExpressionParser<Trajectory>(
		new FunctionParser<Trajectory>("LinearTrajectory") {

			@Override
			public Trajectory parse(String string) throws ParseException {
				return LinearTrajectory.parse(string);
			}
			
		}
	);
	
	/**
	 * @post Crea la trayectoria con el tiempo inicial
	 * 		 especificado
	 */
	public Trajectory(float startTime) {
		this.startTime = startTime;
	}
	
	/**
	 * @post Devuelve el tiempo inicial
	 */
	public float getStartTime() {
		return this.startTime;
	}
	
	/**
	 * @post Devuelve la posición inicial
	 */
	public Vector2f getStartPosition() {
		return this.getInstantPosition(this.getStartTime());
	}
	
	/**
	 * @pre El instante de tiempo no puede ser menor al tiempo
	 * 	    de comienzo de la trayectoria
	 * @post Devuelve la posición para el tiempo especificado.
	 * 		 Si no se cumple la precondición el resultado es indeterminado.
	 */
	public abstract Vector2f getInstantPosition(float time);
	
	/**
	 * @pre El instante de tiempo no puede ser menor al tiempo
	 * 	    de comienzo de la trayectoria
	 * @post Devuelve la velocidad para el tiempo especificado.
	 * 		 Si no se cumple la precondición el resultado es indeterminado.
	 */
	public abstract Vector2f getInstantVelocity(float time);
	
	/**
	 * @pre El intervalo de tiempo no puede ser nulo y no puede comenzar
	 * 		antes que la trayectoria
	 * 
	 * @post Devuelve la figura cuyo perímetro envuelve la trayectoria en el intervalo
	 * 		 de tiempo especificado.
	 * 		 Garantiza que cuando la longitud de intervalo de tiempo tiende a cero,
	 * 		 el perímetro de la figura resultante tiende al perímetro de la figura especificada.
	 * 
	 * 		 Si el intervalo de tiempo comienza antes que la trayectoria,
	 * 		 el resultado es indeterminado.
	 * 
	 */
	public abstract FiniteProportionalHolomorphicShape<?> shapeWithBoundingPerimeter(FloatClosedInterval timeInterval);
	
	/**
	 * @pre El intervalo de tiempo no puede ser nulo y no puede comenzar
	 * 		antes que la trayectoria
	 * 
	 * @post Devuelve la región que envuelve la trayectoria en el intervalo
	 * 		 de tiempo especificado.
	 * 
	 * 		 Si el intervalo de tiempo comienza antes que la trayectoria,
	 * 		 el resultado es indeterminado.
	 */
	public abstract FiniteProportionalHolomorphicShape<?> boundingRegion(FloatClosedInterval timeInterval);
	
	/**
	 * @pre El intervalo de tiempo no puede ser nulo
	 * @post Devuelve la máxima distancia recorrida en el intervalo de tiempo especificado
	 */
	public abstract float getMaxDistanceTraveled(FloatClosedInterval timeInterval);	
	
	/**
	 * @pre El intervalo de tiempo no puede ser nulo
	 * @post Devuelve la distancia recorrida en el intervalo de tiempo especificado
	 */
	public static Trajectory parse(String string) {
		if ( string != null ) {
			return PARSER.parse(string, Trajectory.class);
		}
		else {
			throw new NullPointerException();
		}
	}
}
