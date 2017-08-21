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

import java.io.Serializable;

import com.esferixis.math.intervalarithmetic.FloatClosedInterval;
import com.esferixis.misc.strings.parser.ExpressionParser;
import com.esferixis.misc.strings.parser.FunctionParser;
import com.esferixis.misc.strings.parser.ParseException;

/**
 * Rotación alrededor del origen de coordenadas
 * 
 * @author ariel
 *
 */
public abstract class Rotation implements Serializable {
	/**
	 * 
	 */
	private static final long serialVersionUID = -5693125859104354926L;
	
	private static ExpressionParser<Rotation> PARSER = new ExpressionParser<Rotation>(
		new FunctionParser<Rotation>("LinearRotation") {

			@Override
			public Rotation parse(String string) throws ParseException {
				return LinearRotation.parse(string);
			}
			
		}
	);
	
	private final float startTime;
	
	/**
	 * @post Crea la rotación con el tiempo de comienzo especificado
	 */
	public Rotation(float startTime) {
		this.startTime = startTime;
	}
	
	/**
	 * @post Devuelve el ángulo instantáneo en el instante de tiempo
	 * 		 especificado
	 */
	public abstract float getAngle(float time);
	
	/**
	 * @post Devuelve el intervalo angular en el intervalo de tiempo
	 * 		 especificado
	 */
	public abstract FloatClosedInterval getAngleInterval(FloatClosedInterval timeInterval);
	
	/**
	 * @post Devuelve el tiempo de comienzo
	 */
	public final float getStartTime() {
		return this.startTime;
	}
	
	/**
	 * @pre La cadena de carácteres no puede ser nula
	 * @post Parsea una rotación en la cadena de carácteres especificada
	 */
	public static Rotation parse(String string) {
		if ( string != null ) {
			return PARSER.parse(string, Rotation.class);
		}
		else {
			throw new NullPointerException();
		}
	}
}
