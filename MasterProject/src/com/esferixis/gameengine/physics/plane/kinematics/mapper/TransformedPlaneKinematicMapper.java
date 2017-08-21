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

import java.util.List;

import com.esferixis.gameengine.physics.plane.statics.ProportionalPlaneMapper;
import com.esferixis.gameengine.physics.plane.statics.ProportionalTransformedMapper2d;
import com.esferixis.geometry.plane.finite.Circumference;
import com.esferixis.geometry.plane.finite.FiniteProportionalHolomorphicShape;
import com.esferixis.math.Vector2f;
import com.esferixis.math.intervalarithmetic.FloatClosedInterval;
import com.esferixis.misc.ElementCallback;
import com.esferixis.misc.observer.ObserverManager;
import com.esferixis.misc.strings.parser.ExpressionParser;

/**
 * Mapeador cinemático transformado
 * 
 * @author ariel
 *
 */
public final class TransformedPlaneKinematicMapper<O extends PlaneKinematicMapper, T extends PlaneKinematicMapper> extends PlaneKinematicMapper {
	private static final long serialVersionUID = -6921188487651976624L;

	final ObserverManager<TransformedPlaneKinematicMapper<? extends PlaneKinematicMapper, ? extends PlaneKinematicMapper>, TransformedPlaneKinematicMapperObserver> observerManager = new ObserverManager<TransformedPlaneKinematicMapper<? extends PlaneKinematicMapper, ? extends PlaneKinematicMapper>, TransformedPlaneKinematicMapperObserver>(this, TransformedPlaneKinematicMapperObserver.class);
	
	private O originalMapper;
	private T transformerMapper;

	/**
	 * @pre Ninguno de los mapeadores puede ser nulo
	 * @post Crea el mapeador con el mapeador original y el mapeador transformador
	 * 		 especificados
	 */
	public TransformedPlaneKinematicMapper(O originalMapper, T transformerMapper) {
		this.setOriginalMapper(originalMapper);
		this.setTransformerMapper(transformerMapper);
	}
	
	/**
	 * @post Devuelve el mapeador original
	 */
	public O getOriginalMapper() {
		return this.originalMapper;
	}
	
	/**
	 * @pre El mapeador original no puede ser nulo
	 * @post Especifica el mapeador original
	 */
	public void setOriginalMapper(final O originalMapper) {
		if ( originalMapper != null ) {
			this.observerManager.notifyObservers(new ElementCallback<TransformedPlaneKinematicMapperObserver>() {

				@Override
				public void run(TransformedPlaneKinematicMapperObserver observer) {
					observer.notifyOriginalMapperChange(originalMapper);
				}
				
			});
			this.originalMapper = originalMapper;
		}
		else {
			throw new NullPointerException();
		}
	}
	
	/**
	 * @post Devuelve el mapeador transformador
	 */
	public T getTransformerMapper() {
		return this.transformerMapper;
	}
	
	/**
	 * @pre El mapeador transformador no puede ser nulo
	 * @post Especifica el mapeador transformador
	 */
	public void setTransformerMapper(final T transformerMapper) {
		if ( transformerMapper != null ) {
			this.observerManager.notifyObservers(new ElementCallback<TransformedPlaneKinematicMapperObserver>() {

				@Override
				public void run(TransformedPlaneKinematicMapperObserver observer) {
					observer.notifyTransformerMapperChange(transformerMapper);
				}
				
			});
			this.transformerMapper = transformerMapper;
		}
		else {
			throw new NullPointerException();
		}
	}
	
	/* (non-Javadoc)
	 * @see com.esferixis.gameengine.physics.plane.kinematics.KinematicMapper#instantPlaneMapper(float)
	 */
	@Override
	public ProportionalPlaneMapper instantPlaneMapper(float time) {
		return new ProportionalTransformedMapper2d(this.originalMapper.instantPlaneMapper(time), this.transformerMapper.instantPlaneMapper(time));
	}

	/* (non-Javadoc)
	 * @see com.esferixis.gameengine.physics.plane.kinematics.KinematicMapper#boundingRegion_internal(com.esferixis.geometry.plane.ProportionalHolomorphicShape, com.esferixis.math.intervalarithmetic.FloatClosedInterval)
	 */
	@Override
	protected FiniteProportionalHolomorphicShape<?> shapeWithBoundingPerimeter_internal(FiniteProportionalHolomorphicShape<?> originalShape,
			FloatClosedInterval timeInterval) {
		return this.transformerMapper.shapeWithBoundingPerimeter( this.originalMapper.shapeWithBoundingPerimeter(originalShape, timeInterval), timeInterval );
	}

	/* (non-Javadoc)
	 * @see com.arielcarrizo.gameengine.physics.plane.kinematics.KinematicMapper#boundingRegion_internal(com.arielcarrizo.geometry.plane.ProportionalHolomorphicShape, com.arielcarrizo.math.intervalarithmetic.FloatClosedInterval)
	 */
	@Override
	protected FiniteProportionalHolomorphicShape<?> boundingRegion_internal(FiniteProportionalHolomorphicShape<?> originalShape,
			FloatClosedInterval timeInterval) {
		return this.transformerMapper.boundingRegion( this.originalMapper.boundingRegion(originalShape, timeInterval), timeInterval );
	}
	
	/**
	 * @pre La cadena no puede ser nula
	 * @post Parsea el mapeador en la cadena especificada
	 */
	public static TransformedPlaneKinematicMapper<PlaneKinematicMapper, PlaneKinematicMapper> parse(String string) {
		if ( string != null ) {
			List<String> parameters = ExpressionParser.separateParameters(string);
			ExpressionParser.checkParametersQuantity(parameters, 2);
			return new TransformedPlaneKinematicMapper<PlaneKinematicMapper, PlaneKinematicMapper>(PlaneKinematicMapper.parse(parameters.get(0)), PlaneKinematicMapper.parse(parameters.get(1)));
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
		return "TransformedKinematicMapper@" + Integer.toHexString(System.identityHashCode(this)) + "( " + this.originalMapper + ", " + this.transformerMapper + ")";
	}

	/* (non-Javadoc)
	 * @see com.arielcarrizo.gameengine.physics.plane.kinematics.KinematicMapper#getMaxDistanceTraveled(com.arielcarrizo.math.intervalarithmetic.FloatClosedInterval)
	 */
	@Override
	public float getMaxDistanceTraveled_internal(FiniteProportionalHolomorphicShape<?> originalShape, FloatClosedInterval timeInterval) {
		return this.transformerMapper.getMaxDistanceTraveledWithBoundedTransformation(originalShape, timeInterval, this.originalMapper.getMaxDistanceTraveled(originalShape, timeInterval));
	}

	/* (non-Javadoc)
	 * @see com.arielcarrizo.gameengine.physics.plane.kinematics.kinematicMapper.PlaneKinematicMapper#getMaxDistanceTraveledByBoundedTransformedShape_internal(com.arielcarrizo.geometry.plane.finite.FiniteProportionalHolomorphicShape, com.arielcarrizo.math.intervalarithmetic.FloatClosedInterval, float)
	 */
	@Override
	protected float getMaxDistanceTraveledWithBoundedTransformation_internal(
			FiniteProportionalHolomorphicShape<?> originalShape, FloatClosedInterval timeInterval,
			float maxDistanceTraveled_originalTransformation) {
		return this.transformerMapper.getMaxDistanceTraveledWithBoundedTransformation(originalShape, timeInterval, this.originalMapper.getMaxDistanceTraveledWithBoundedTransformation(originalShape, timeInterval, maxDistanceTraveled_originalTransformation));
	}

	/* (non-Javadoc)
	 * @see com.arielcarrizo.gameengine.physics.plane.kinematics.kinematicMapper.PlaneKinematicMapper#accept(com.arielcarrizo.gameengine.physics.plane.kinematics.kinematicMapper.PlaneKinematicMapper.Visitor)
	 */
	@Override
	public <V, E extends Throwable> V accept(Visitor<V, E> visitor) throws E {
		return visitor.visit(this);
	}

	/* (non-Javadoc)
	 * @see com.arielcarrizo.gameengine.physics.plane.kinematics.kinematicMapper.PlaneKinematicMapper#getStartTime()
	 */
	@Override
	public float getStartTime() {
		return Math.max(this.originalMapper.getStartTime(), this.transformerMapper.getStartTime());
	}
}
