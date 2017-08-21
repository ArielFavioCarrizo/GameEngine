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

import java.io.Serializable;

import com.esferixis.gameengine.physics.plane.kinematics.PlaneKinematicBody;
import com.esferixis.gameengine.physics.plane.kinematics.mapper.PlaneKinematicMapper;
import com.esferixis.math.ExtraMath;
import com.esferixis.math.intervalarithmetic.FloatClosedInterval;
import com.esferixis.misc.strings.parser.ExpressionParser;
import com.esferixis.misc.strings.parser.FunctionParser;
import com.esferixis.misc.strings.parser.ParseException;

/**
 * @author ariel
 *
 */
public abstract class PairCollisionDetector implements Serializable {
	/**
	 * 
	 */
	private static final long serialVersionUID = 581237606473316937L;
	
	private static ExpressionParser<PairCollisionDetector> PARSER = new ExpressionParser<PairCollisionDetector>(
		new FunctionParser<PairCollisionDetector>("DistancePairCollisionDetector") {

			@Override
			public PairCollisionDetector parse(String string) throws ParseException {
				if ( string.isEmpty() ) {
					return new DistancePairCollisionDetector();
				}
				else {
					throw new ParseException("Expected no parameters");
				}
			}
			
		}
	);
	
	/**
	 * @pre El intervalo de tiempo, el intervalo de distancia de colisión y los cuerpos no pueden ser nulos
	 * @post Devuelve un instante de tiempo en que las distancias de los
	 * 		 dos cuerpos cinemáticos cumple con el criterio de colisión si el test
	 * 		 es inclusivo.
	 * 		 Caso contrario, un instante de tiempo anterior al que se cumpla el criterio si
	 * 		 es posible.
	 * 		 Todo dentro en el intervalo de tiempo especificado.
	 * 		 Si no están completos, o si no hubo colisión, devuelve null.
	 */
	public final Float testCollision(FloatClosedInterval timeInterval, FloatClosedInterval collisionDistanceInterval, boolean inclusiveResult, PlaneKinematicBody<? extends PlaneKinematicMapper> body1, PlaneKinematicBody<? extends PlaneKinematicMapper> body2) {
		if ( ( timeInterval != null ) && ( collisionDistanceInterval != null ) && ( body1 != null ) && ( body2 != null ) ) {
			final Float result;
			
			if ( body1.isComplete() && body2.isComplete() ) {
				/*
				boolean simplification = false;
				
				PlaneKinematicMapper mapper1 = body1.getKinematicMapper();
				PlaneKinematicMapper mapper2 = body2.getKinematicMapper();
				
				boolean endBrowsing = false;
				
				do {
					if ( ( mapper1 instanceof TransformedPlaneKinematicMapper ) && ( mapper2 instanceof TransformedPlaneKinematicMapper ) ) {
						final TransformedPlaneKinematicMapper<? extends PlaneKinematicMapper, ? extends PlaneKinematicMapper > transformedMapper1 = (TransformedPlaneKinematicMapper<? extends PlaneKinematicMapper, ? extends PlaneKinematicMapper>) mapper1;
						final TransformedPlaneKinematicMapper<? extends PlaneKinematicMapper, ? extends PlaneKinematicMapper > transformedMapper2 = (TransformedPlaneKinematicMapper<? extends PlaneKinematicMapper, ? extends PlaneKinematicMapper>) mapper2;
						
						if ( transformedMapper1.getTransformerMapper() == transformedMapper2.getTransformerMapper() ) {
							mapper1 = transformedMapper1.getOriginalMapper();
							mapper2 = transformedMapper2.getOriginalMapper();
							
							simplification = true;
						}
						else {
							endBrowsing = true;
						}
					}
					else {
						endBrowsing = true;
					}
				} while ( !endBrowsing );
				
				if ( simplification ) {
					body1 = new PlaneKinematicBody<FiniteProportionalHolomorphicShape<?>, PlaneKinematicMapper>(body1.getShape(), mapper1);
					body2 = new PlaneKinematicBody<FiniteProportionalHolomorphicShape<?>, PlaneKinematicMapper>(body2.getShape(), mapper2);
				}
				*/
				
				final float newMinTime = ExtraMath.max(timeInterval.getMin(), body1.getStartTime(), body2.getStartTime());
				
				if ( newMinTime < timeInterval.getMax() ) {
					result = this.testCollision_internal(new FloatClosedInterval(newMinTime, timeInterval.getMax()), collisionDistanceInterval, inclusiveResult, body1, body2);
				}
				else {
					result = null;
				}
				
				/*
				if ( body1.instantShape(timeInterval.getMin()).hasIntersection(body2.instantShape(timeInterval.getMax())) ) {
					result = this.testCollision_internal(timeInterval, body1, body2);
				}
				else {
					result = null;
				}
				*/
			}
			else {
				result = null;
			}
			
			return result;
		}
		else {
			throw new NullPointerException();
		}
	}
	
	/**
	 * @pre El intervalo de tiempo, el intervalo de distancia de colisión y los cuerpos no son nulos
	 * @post Devuelve un instante de tiempo en que las distancias de los
	 * 		 dos cuerpos cinemáticos cumple con el criterio de colisión si el test
	 * 		 es inclusivo.
	 * 		 Caso contrario, un instante de tiempo anterior al que se cumpla el criterio si
	 * 		 es posible.
	 * 		 Todo dentro en el intervalo de tiempo especificado.
	 * 		 Si no están completos, o si no hubo colisión, devuelve null.
	 */
	protected abstract Float testCollision_internal(FloatClosedInterval timeInterval, FloatClosedInterval collisionDistanceInterval, boolean inclusiveResult, PlaneKinematicBody<? extends PlaneKinematicMapper> body1, PlaneKinematicBody<? extends PlaneKinematicMapper> body2);
	
	/**
	 * @pre La cadena de carácteres no puede ser nula
	 * @post Parsea un detector de colisiones de pares en la cadena de carácteres especificada
	 */
	public static PairCollisionDetector parse(String string) {
		return PARSER.parse(string, PairCollisionDetector.class);
	}
}
