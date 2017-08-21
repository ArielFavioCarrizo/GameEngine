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

package com.esferixis.gameengine.physics.plane.dynamics.body;

import com.esferixis.gameengine.physics.plane.kinematics.PlaneKinematicBody;
import com.esferixis.gameengine.physics.plane.kinematics.StaticShapePlaneKinematicBody;
import com.esferixis.gameengine.physics.plane.kinematics.mapper.PlaneKinematicMapper;
import com.esferixis.geometry.plane.finite.FiniteProportionalHolomorphicShape;

/**
 * @author ariel
 *
 */
public final class StaticShapePlaneDynamicsBody<S extends FiniteProportionalHolomorphicShape<?>, M extends PlaneKinematicMapper> extends PlaneDynamicsBody<M> {
	/**
	 * 
	 */
	private static final long serialVersionUID = 2503465817492047775L;

	private final StaticShapePlaneKinematicBody<S, M> planeKinematicBody;
	
	/**
	 * @post Crea el objeto dinámico con la figura y el mapeador cinemático especificados
	 * 		 Pueden ser nulos, pero si falta uno de ellos no será mapeado
	 */
	public StaticShapePlaneDynamicsBody(S shape, M kinematicMapper) {
		if ( ( shape != null ) && ( kinematicMapper != null ) ) {
			this.planeKinematicBody = new StaticShapePlaneKinematicBody<S, M>(shape, kinematicMapper);
		}
		else {
			throw new NullPointerException();
		}
	}
	
	/**
	 * @post Especifica la figura
	 */
	public void setShape(S shape) {
		this.planeKinematicBody.setShape(shape);
	}
	
	/**
	 * @post Devuelve la figura
	 */
	public S getShape() {
		return this.planeKinematicBody.getShape();
	}

	/* (non-Javadoc)
	 * @see com.arielcarrizo.gameengine.physics.plane.dynamics.body.PlaneDynamicsBody#getPlaneKinematicBody()
	 */
	@Override
	protected PlaneKinematicBody<M> getPlaneKinematicBody() {
		return this.planeKinematicBody;
	}

	/* (non-Javadoc)
	 * @see com.arielcarrizo.gameengine.physics.plane.dynamics.body.PlaneDynamicsBody#setKinematicMapper(com.arielcarrizo.gameengine.physics.plane.kinematics.mapper.PlaneKinematicMapper)
	 */
	@Override
	public void setKinematicMapper(M kinematicMapper) {
		this.planeKinematicBody.setKinematicMapper(kinematicMapper);
	}

	/* (non-Javadoc)
	 * @see com.arielcarrizo.gameengine.physics.plane.dynamics.body.PlaneDynamicsBody#getKinematicMapper()
	 */
	@Override
	public M getKinematicMapper() {
		return this.planeKinematicBody.getKinematicMapper();
	}
}
