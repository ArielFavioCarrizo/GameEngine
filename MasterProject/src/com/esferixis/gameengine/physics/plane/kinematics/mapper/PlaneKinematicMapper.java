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

package com.esferixis.gameengine.physics.plane.kinematics.mapper;

import java.io.Serializable;

import com.esferixis.gameengine.physics.plane.kinematics.mapper.rotation.Rotation;
import com.esferixis.gameengine.physics.plane.kinematics.mapper.rotation.RotationPlaneKinematicMapper;
import com.esferixis.gameengine.physics.plane.kinematics.mapper.translation.Trajectory;
import com.esferixis.gameengine.physics.plane.kinematics.mapper.translation.TranslationPlaneKinematicMapper;
import com.esferixis.gameengine.physics.plane.statics.ProportionalPlaneMapper;
import com.esferixis.geometry.plane.finite.FiniteProportionalHolomorphicShape;
import com.esferixis.math.intervalarithmetic.FloatClosedInterval;
import com.esferixis.misc.strings.parser.ExpressionParser;
import com.esferixis.misc.strings.parser.FunctionParser;
import com.esferixis.misc.strings.parser.ParseException;

/**
 * Mapeador cinemático
 * 
 * @author ariel
 *
 */
public abstract class PlaneKinematicMapper implements Serializable {
	private static final long serialVersionUID = -8367152282062459687L;
	
	private static final ExpressionParser<PlaneKinematicMapper> PARSER = new ExpressionParser<PlaneKinematicMapper>(
		new FunctionParser<StaticAffinePlaneKinematicMapper>("StaticAffineKinematicMapper") {

			@Override
			public StaticAffinePlaneKinematicMapper parse(String string) throws ParseException {
				return StaticAffinePlaneKinematicMapper.parse(string);
			}
			
		},
		new FunctionParser<TranslationPlaneKinematicMapper<Trajectory>>("TranslationPlaneKinematicMapper") {

			@Override
			public TranslationPlaneKinematicMapper<Trajectory> parse(String string) throws ParseException {
				return TranslationPlaneKinematicMapper.parse(string);
			}
			
		},
		new FunctionParser<RotationPlaneKinematicMapper<Rotation>>("RotationPlaneKinematicMapper") {

			@Override
			public RotationPlaneKinematicMapper<Rotation> parse(String string) throws ParseException {
				return RotationPlaneKinematicMapper.parse(string);
			}
			
		},
		new FunctionParser<StaticAffinePlaneKinematicMapper>("StaticAffinePlaneKinematicMapper") {

			@Override
			public StaticAffinePlaneKinematicMapper parse(String string) throws ParseException {
				return StaticAffinePlaneKinematicMapper.parse(string);
			}
			
		},
		new FunctionParser<TransformedPlaneKinematicMapper<PlaneKinematicMapper, PlaneKinematicMapper>>("TransformedPlaneKinematicMapper") {

			@Override
			public TransformedPlaneKinematicMapper<PlaneKinematicMapper, PlaneKinematicMapper> parse(String string) throws ParseException {
				return TransformedPlaneKinematicMapper.parse(string);
			}
			
		}
	);
	
	public interface Visitor<V, E extends Throwable> {
		public V visit(TranslationPlaneKinematicMapper<? extends Trajectory> translationKinematicMapper) throws E;
		public V visit(RotationPlaneKinematicMapper<? extends Rotation> rotationKinematicMapper) throws E;
		public V visit(StaticAffinePlaneKinematicMapper staticAffinePlaneKinematicMapper) throws E;
		public V visit(TransformedPlaneKinematicMapper<? extends PlaneKinematicMapper, ? extends PlaneKinematicMapper> transformedPlaneKinematicMapper) throws E;
		public V visit(MirrorPlaneKinematicMapper<? extends PlaneKinematicMapper> mirrorPlaneKinematicMapper) throws E;
	}
	
	/**
	 * @post Devuelve el mapeador de plano para el instante de tiempo
	 * 		 especificado
	 */
	public abstract ProportionalPlaneMapper instantPlaneMapper(float time);
	
	/**
	 * @post Devuelve una figura que tenga un perímetro envolvente para la figura
	 * 		 especificada, en el intervalo de tiempo
	 * 		 especificado.
	 * 		 Garantiza que cuando la longitud de intervalo de tiempo tiende a cero,
	 * 		 la figura resultante tiende al perímetro de la figura especificada.
	 */
	public FiniteProportionalHolomorphicShape<?> shapeWithBoundingPerimeter(FiniteProportionalHolomorphicShape<?> originalShape, FloatClosedInterval timeInterval) {
		if ( ( originalShape != null ) && ( timeInterval != null ) ) {
			if ( timeInterval.length() == 0.0f ) {
				return PlaneKinematicMapper.this.instantPlaneMapper(timeInterval.getMin()).transform(originalShape);
			}
			else {
				return this.shapeWithBoundingPerimeter_internal(originalShape, timeInterval);
			}
		}
		else {
			throw new NullPointerException();
		}
	}
	
	/**
	 * @post Devuelve una figura que tenga un perímetro envolvente para la figura
	 * 		 especificada, en el intervalo de tiempo
	 * 		 especificado.
	 * 		 Garantiza que cuando la longitud de intervalo de tiempo tiende a cero,
	 * 		 la figura resultante tiende al perímetro de la figura especificada.
	 */
	protected abstract FiniteProportionalHolomorphicShape<?> shapeWithBoundingPerimeter_internal(FiniteProportionalHolomorphicShape<?> originalShape, FloatClosedInterval timeInterval);
	
	/**
	 * @post Devuelve una región envolvente envolvente para la figura
	 * 		 especificada, en el intervalo de tiempo
	 * 		 especificado
	 * 		 No garantiza que cuando la longitud de intervalo de tiempo tiende a cero,
	 * 		 la figura resultante tienda a la región ocupada por la figura especificada.
	 */
	public FiniteProportionalHolomorphicShape<?> boundingRegion(FiniteProportionalHolomorphicShape<?> originalShape, FloatClosedInterval timeInterval) {
		if ( ( originalShape != null ) && ( timeInterval != null ) ) {
			return this.boundingRegion_internal(originalShape, timeInterval);
		}
		else {
			throw new NullPointerException();
		}
	}
	
	/**
	 * @post Devuelve una región envolvente envolvente para la figura
	 * 		 especificada, en el intervalo de tiempo
	 * 		 especificado
	 * 		 No garantiza que cuando la longitud de intervalo de tiempo tiende a cero,
	 * 		 la figura resultante tienda a la región ocupada por la figura especificada.
	 */
	protected abstract FiniteProportionalHolomorphicShape<?> boundingRegion_internal(FiniteProportionalHolomorphicShape<?> originalShape, FloatClosedInterval timeInterval);
	
	/**
	 * @pre La figura original y el intervalo de tiempo no pueden ser nulos
	 * @post Devuelve la máxima distancia recorrida
	 */
	public final float getMaxDistanceTraveled(FiniteProportionalHolomorphicShape<?> originalShape, FloatClosedInterval timeInterval) {
		if ( ( originalShape != null ) && ( timeInterval != null ) ) {
			return this.getMaxDistanceTraveled_internal(originalShape, timeInterval);
		}
		else {
			throw new NullPointerException();
		}
	}
	
	/**
	 * @post Devuelve la máxima distancia recorrida
	 */
	protected abstract float getMaxDistanceTraveled_internal(FiniteProportionalHolomorphicShape<?> originalShape, FloatClosedInterval timeInterval);
	
	/**
	 * @pre La figura, el intervalo de tiempo no pueden ser nulos,
	 * 		y la máxima distancia recorrida por la transformación original no puede ser negativa
	 * @post Devuelve la máxima distancia recorrida, considerando
	 * 		 la máxima distancia recorrida especificada, por la figura
	 * 		 especificada, en el intervalo de tiempo
	 * 		 especificado
	 */
	public float getMaxDistanceTraveledWithBoundedTransformation(FiniteProportionalHolomorphicShape<?> originalShape, FloatClosedInterval timeInterval, float maxDistanceTraveled_originalTransformation) {
		if ( ( originalShape != null ) && ( timeInterval != null ) ) {
			if ( maxDistanceTraveled_originalTransformation >= 0.0f ) {
				return this.getMaxDistanceTraveledWithBoundedTransformation_internal(originalShape, timeInterval, maxDistanceTraveled_originalTransformation);
			}
			else {
				throw new IllegalArgumentException("Expected positive max distance traveled");
			}
		}
		else {
			throw new NullPointerException();
		}
	}
	
	/**
	 * @pre La figura, el intervalo de tiempo y la máxima distancia recorrida
	 * 		por la transformación original no son nulas
	 * @post Devuelve la máxima distancia recorrida, considerando
	 * 		 la máxima distancia recorrida especificada, por la figura
	 * 		 especificada, en el intervalo de tiempo
	 * 		 especificado
	 */
	protected abstract float getMaxDistanceTraveledWithBoundedTransformation_internal(FiniteProportionalHolomorphicShape<?> originalShape, FloatClosedInterval timeInterval, float maxDistanceTraveled_originalTransformation);
	
	/**
	 * @post Devuelve el tiempo de comienzo
	 */
	public abstract float getStartTime();
	
	/**
	 * @pre La cadena no puede ser nula
	 * @post Parsea un mapeador cinemático en la cadena especificada
	 */
	public static PlaneKinematicMapper parse(String string) {
		if ( string != null ) {
			return PARSER.parse(string, PlaneKinematicMapper.class);
		}
		else {
			throw new NullPointerException();
		}
	}
	
	/**
	 * @pre El visitor no puede ser nulo
	 * @post Visita el mapeador cinemático de plano con el visitor especificado
	 */
	public abstract <V, E extends Throwable> V accept(Visitor<V, E> visitor) throws E;
}
