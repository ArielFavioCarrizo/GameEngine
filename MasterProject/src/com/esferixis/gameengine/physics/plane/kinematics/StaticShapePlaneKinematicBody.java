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

import java.util.List;

import com.esferixis.gameengine.physics.plane.kinematics.mapper.PlaneKinematicMapper;
import com.esferixis.geometry.plane.finite.FiniteProportionalHolomorphicShape;
import com.esferixis.math.intervalarithmetic.FloatClosedInterval;
import com.esferixis.misc.strings.parser.ExpressionParser;

/**
 * @author ariel
 *
 */
public final class StaticShapePlaneKinematicBody<S extends FiniteProportionalHolomorphicShape<?>, M extends PlaneKinematicMapper> extends PlaneKinematicBody<M> {
	/**
	 * 
	 */
	private static final long serialVersionUID = 8956269745709077445L;
	
	private S shape;
	
	/**
	 * @post Crea el objeto cinemático con la figura y el mapeador cinemático especificados
	 */
	public StaticShapePlaneKinematicBody(S shape, M kinematicMapper) {
		super(kinematicMapper);
		this.setShape(shape);
	}
	
	/**
	 * @post Especifica la figura
	 */
	public void setShape(S shape) {
		final S oldShape = this.shape;
		
		this.shape = shape;
		
		if ( this.shape != oldShape ) {
			this.notifyChange();
		}
	}
	
	/**
	 * @post Devuelve la figura
	 */
	public S getShape() {
		return this.shape;
	}
	
	/**
	 * @pre La cadena de carácteres no puede ser nula
	 * @post Parsea el cuerpo cinemático de plano en la cadena especificada
	 */
	public static StaticShapePlaneKinematicBody<? extends FiniteProportionalHolomorphicShape<?>, ? extends PlaneKinematicMapper> parse(String string) {
		if ( string != null ) {
			final List<String> parameters = ExpressionParser.separateParameters(string);
			ExpressionParser.checkParametersQuantity(parameters, 2);
			
			return new StaticShapePlaneKinematicBody<FiniteProportionalHolomorphicShape<?>, PlaneKinematicMapper>(FiniteProportionalHolomorphicShape.parse(parameters.get(0), FiniteProportionalHolomorphicShape.class), PlaneKinematicMapper.parse(parameters.get(1)));
		}
		else {
			throw new NullPointerException();
		}
	}

	/* (non-Javadoc)
	 * @see com.arielcarrizo.gameengine.physics.plane.kinematics.PlaneKinematicBody#getStartTime()
	 */
	@Override
	public float getStartTime() {
		return this.getKinematicMapper().getStartTime();
	}

	/* (non-Javadoc)
	 * @see com.arielcarrizo.gameengine.physics.plane.kinematics.PlaneKinematicBody#instantShape_checked(float)
	 */
	@Override
	protected FiniteProportionalHolomorphicShape<?> instantShape_checked(float time) {
		return this.getKinematicMapper().instantPlaneMapper(time).transform(this.getShape());
	}

	/* (non-Javadoc)
	 * @see com.arielcarrizo.gameengine.physics.plane.kinematics.PlaneKinematicBody#getMaxDistanceTraveled_checked(com.arielcarrizo.math.intervalarithmetic.FloatClosedInterval)
	 */
	@Override
	protected float getMaxDistanceTraveled_checked(FloatClosedInterval timeInterval) {
		return this.getKinematicMapper().getMaxDistanceTraveled(this.getShape(), timeInterval);
	}

	/* (non-Javadoc)
	 * @see com.arielcarrizo.gameengine.physics.plane.kinematics.PlaneKinematicBody#shapeWithBoundingPerimeter_checked(com.arielcarrizo.math.intervalarithmetic.FloatClosedInterval)
	 */
	@Override
	protected FiniteProportionalHolomorphicShape<?> shapeWithBoundingPerimeter_checked(
			FloatClosedInterval timeInterval) {
		return this.getKinematicMapper().shapeWithBoundingPerimeter(this.getShape(), timeInterval);
	}

	/* (non-Javadoc)
	 * @see com.arielcarrizo.gameengine.physics.plane.kinematics.PlaneKinematicBody#boundingRegion_checked(com.arielcarrizo.math.intervalarithmetic.FloatClosedInterval)
	 */
	@Override
	protected FiniteProportionalHolomorphicShape<?> boundingRegion_checked(FloatClosedInterval timeInterval) {
		return this.getKinematicMapper().boundingRegion(this.getShape(), timeInterval);
	}

	/* (non-Javadoc)
	 * @see com.arielcarrizo.gameengine.physics.plane.kinematics.PlaneKinematicBody#isComplete()
	 */
	@Override
	public boolean isComplete() {
		return ( this.shape != null ) && ( this.getKinematicMapper() != null );
	}
}
