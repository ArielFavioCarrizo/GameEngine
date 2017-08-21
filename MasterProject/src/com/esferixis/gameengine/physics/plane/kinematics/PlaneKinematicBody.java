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
import com.esferixis.geometry.plane.finite.FiniteProportionalHolomorphicShape;
import com.esferixis.math.intervalarithmetic.FloatClosedInterval;
import com.esferixis.misc.strings.parser.ExpressionParser;
import com.esferixis.misc.strings.parser.FunctionParser;
import com.esferixis.misc.strings.parser.ParseException;

/**
 * @author ariel
 *
 */
public abstract class PlaneKinematicBody<M extends PlaneKinematicMapper> implements Serializable {
	private static final ExpressionParser<PlaneKinematicBody<? extends PlaneKinematicMapper>> PARSER = new ExpressionParser<PlaneKinematicBody<? extends PlaneKinematicMapper>>(
		new FunctionParser<PlaneKinematicBody<? extends PlaneKinematicMapper>>("StaticShapePlaneKinematicBody") {

			@Override
			public PlaneKinematicBody<? extends PlaneKinematicMapper> parse(String string) throws ParseException {
				return StaticShapePlaneKinematicBody.parse(string);
			}
			
		}
	);
	
	/**
	 * 
	 */
	private static final long serialVersionUID = 8956269745709077445L;
	
	private CollisionTestBodyPairContainer collisionTestBodyPairContainer;
	private int collisionTestBodyPairContainerCounter;
	
	private M kinematicMapper;
	
	/**
	 * @post Crea el objeto cinemático con el mapeador especificado
	 */
	public PlaneKinematicBody(M planeKinematicMapper) {
		this.collisionTestBodyPairContainer = null;
		this.setKinematicMapper(planeKinematicMapper);
	}
	
	/**
	 * @post Especifica el mapeador cinemático
	 */
	public final void setKinematicMapper(M kinematicMapper) {
		final M oldKinematicMapper = this.kinematicMapper;
		
		this.kinematicMapper = kinematicMapper;
		
		if ( this.kinematicMapper != oldKinematicMapper ) {
			this.notifyChange();
		}
	}
	
	/**
	 * @post Devuelve el mapeador cinemático
	 */
	public final M getKinematicMapper() {
		return this.kinematicMapper;
	}
	
	/**
	 * @post Notifica un cambio
	 */
	protected final void notifyChange() {
		if ( this.collisionTestBodyPairContainer != null ) {
			this.collisionTestBodyPairContainer.notifyChange(this);
		}
	}
	
	/**
	 * @pre El contenedor de pares de testeo no puede ser nulo, y no tiene que haber uno agregado previamente.
	 * 		O tiene que ser el mismo.
	 * @post Asocia el contenedor especificado
	 */
	protected final void attachCollisionTestBodyPairContainer(CollisionTestBodyPairContainer collisionTestBodyPairContainer) {
		if ( collisionTestBodyPairContainer != null ) {
			if ( collisionTestBodyPairContainer != this.collisionTestBodyPairContainer ) {
				if ( this.collisionTestBodyPairContainer == null ) {
					this.collisionTestBodyPairContainer = collisionTestBodyPairContainer;
					this.collisionTestBodyPairContainerCounter = Integer.MIN_VALUE;
				}
				else {
					throw new IllegalStateException("Expected with no attached container");
				}
			}
			else {
				if ( this.collisionTestBodyPairContainerCounter != Integer.MAX_VALUE ) {
					this.collisionTestBodyPairContainerCounter++;
				}
				else {
					throw new IllegalStateException("Collision test body pair container count overflow");
				}
			}
		}
		else {
			throw new NullPointerException();
		}
	}
	
	/**
	 * @post Devuelve el contenedor de pares de testeo asociado, si
	 * 		no hay ninguno devuelve null
	 */
	protected final CollisionTestBodyPairContainer getCollisionTestBodyPairContainer() {
		return this.collisionTestBodyPairContainer;
	}
	
	/**
	 * @pre Tiene que haber un contenedor de pares de testeo asociado
	 * @post Desasocia el contenedor de pares de testeo asociado
	 */
	protected final void detachCollisionTestBodyPairContainer() {
		if ( this.collisionTestBodyPairContainer != null ) {
			if ( (this.collisionTestBodyPairContainerCounter--) == Integer.MIN_VALUE ) {
				this.collisionTestBodyPairContainer = null;
			}
		}
		else {
			throw new IllegalStateException("Expected attached container");
		}
	}
	
	/**
	 * @post Devuelve el tiempo de comienzo en que sus movimientos son válidos
	 */
	public abstract float getStartTime();
	
	/**
	 * @pre El cuerpo tiene que estar completo
	 * @post Devuelve la figura en el instante de tiempo especificado
	 */
	public final FiniteProportionalHolomorphicShape<?> instantShape(float time) {
		this.checkComplete();
		
		return this.instantShape_checked(time);
	}
	
	/**
	 * @pre El cuerpo está completo
	 * @post Devuelve la figura en el instante de tiempo especificado
	 */
	protected abstract FiniteProportionalHolomorphicShape<?> instantShape_checked(float time);
	
	/**
	 * @pre El cuerpo tiene que estar completo, y el intervalo de tiempo
	 * 		no puede ser nulo.
	 * @post Devuelve la máxima distancia recorrida en el intervalo de tiempo
	 * 		 especificado.
	 */
	public final float getMaxDistanceTraveled(FloatClosedInterval timeInterval) {
		if ( timeInterval != null ) {
			this.checkComplete();
			
			return this.getMaxDistanceTraveled_checked(timeInterval);
		}
		else {
			throw new NullPointerException();
		}
	}
	
	/**
	 * @pre El cuerpo tiene que estar completo, y el intervalo de tiempo
	 * 		no puede ser nulo.
	 * @post Devuelve la máxima distancia recorrida en el intervalo de tiempo
	 * 		 especificado.
	 */
	protected abstract float getMaxDistanceTraveled_checked(FloatClosedInterval timeInterval);
	
	/**
	 * @pre El cuerpo tiene que estar completo, y el intervalo de tiempo
	 * 		no pueden ser nulo.
	 * @post Devuelve una figura que tenga un perímetro envolvente del cuerpo,
	 * 		 en el intervalo de tiempo especificado.
	 * 		 Garantiza que cuando la longitud de intervalo de tiempo tiende a cero,
	 * 		 la figura resultante tiende al perímetro del cuerpo.
	 */
	public final FiniteProportionalHolomorphicShape<?> shapeWithBoundingPerimeter(FloatClosedInterval timeInterval) {
		if ( timeInterval != null ) {
			this.checkComplete();
			
			return this.shapeWithBoundingPerimeter_checked(timeInterval);
		}
		else {
			throw new NullPointerException();
		}
	}

	/**
	 * @pre El cuerpo está completo, y  el intervalo de tiempo
	 * 		no es nulo.
	 * @post Devuelve una figura que tenga un perímetro envolvente del cuerpo,
	 * 		 en el intervalo de tiempo especificado.
	 * 		 Garantiza que cuando la longitud de intervalo de tiempo tiende a cero,
	 * 		 la figura resultante tiende al perímetro del cuerpo.
	 */
	protected abstract FiniteProportionalHolomorphicShape<?> shapeWithBoundingPerimeter_checked(FloatClosedInterval timeInterval);
	
	/**
	 * @pre El cuerpo tiene que estar completo, y el intervalo de tiempo
	 * 		no puede ser nulo.
	 * @post Devuelve una región envolvente envolvente del cuerpo, en el intervalo de tiempo
	 * 		 especificado
	 * 		 No garantiza que cuando la longitud de intervalo de tiempo tiende a cero,
	 * 		 la figura resultante tienda a la región ocupada por el cuerpo especificado.
	 */
	public final FiniteProportionalHolomorphicShape<?> boundingRegion(FloatClosedInterval timeInterval) {
		if ( timeInterval != null ) {
			return this.boundingRegion_checked(timeInterval);
		}
		else {
			throw new NullPointerException();
		}
	}
	
	/**
	 * @pre El cuerpo está completo, y el intervalo de tiempo
	 * 		no es nulo.
	 * @post Devuelve una región envolvente envolvente del cuerpo, en el intervalo de tiempo
	 * 		 especificado
	 * 		 No garantiza que cuando la longitud de intervalo de tiempo tiende a cero,
	 * 		 la figura resultante tienda a la región ocupada por el cuerpo especificado.
	 */
	protected abstract FiniteProportionalHolomorphicShape<?> boundingRegion_checked(FloatClosedInterval timeInterval);
	
	/**
	 * @post Verifica que esté completo
	 */
	protected final void checkComplete() {
		if ( !this.isComplete() ) {
			throw new IllegalStateException("Expected complete body");
		}
	}
	
	/**
	 * @post Devuelve si está completo
	 */
	public abstract boolean isComplete();
	
	/**
	 * @pre La cadena de carácteres no puede ser nula
	 * @post Parsea el cuerpo cinemático de plano en la cadena especificada
	 */
	public static PlaneKinematicBody<? extends PlaneKinematicMapper> parse(String string) {
		return PARSER.parse(string, PlaneKinematicBody.class);
	}
}
