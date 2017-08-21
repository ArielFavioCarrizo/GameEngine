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

package com.esferixis.gameengine.tests.physics.plane.dynamics.body.dynamicscontainertest;

import com.esferixis.gameengine.physics.plane.dynamics.body.StaticShapePlaneDynamicsBody;
import com.esferixis.gameengine.physics.plane.dynamics.components.SymmetricComponent;
import com.esferixis.gameengine.physics.plane.kinematics.mapper.translation.LinearTrajectory;
import com.esferixis.gameengine.physics.plane.kinematics.mapper.translation.TranslationPlaneKinematicMapper;
import com.esferixis.geometry.plane.Shape.NearestNormal;
import com.esferixis.geometry.plane.finite.Circle;
import com.esferixis.geometry.plane.finite.FiniteProportionalHolomorphicShape;
import com.esferixis.math.Vector2f;

public class CollisionBetweenCirclesSymmetricComponent extends SymmetricComponent<CollisionBetweenCirclesSymmetricComponent>{
	/**
	 * 
	 */
	private static final long serialVersionUID = 6832126980712570628L;
	private final StaticShapePlaneDynamicsBody<Circle, TranslationPlaneKinematicMapper<LinearTrajectory>> body;
	
	public CollisionBetweenCirclesSymmetricComponent(StaticShapePlaneDynamicsBody<Circle, TranslationPlaneKinematicMapper<LinearTrajectory>> body) {
		super(CollisionBetweenCirclesSymmetricComponent.class);
		
		if ( body != null ) {
			this.body = body;
		}
		else {
			throw new NullPointerException();
		}
	}

	/**
	 * @post Devuelve la velocidad del cuerpo
	 */
	private Vector2f getVelocity(final StaticShapePlaneDynamicsBody<? extends FiniteProportionalHolomorphicShape<?>, TranslationPlaneKinematicMapper<LinearTrajectory>> body) {
		return body.getKinematicMapper().getTrajectory().getVelocity();
	}
	
	/**
	 * @post Especifica la velocidad del cuerpo en el instante de tiempo especificado
	 */
	private void setVelocity(final StaticShapePlaneDynamicsBody<? extends FiniteProportionalHolomorphicShape<?>, TranslationPlaneKinematicMapper<LinearTrajectory>> body, float time, Vector2f velocity) {
		body.getKinematicMapper().setTrajectory(new LinearTrajectory(time, body.getKinematicMapper().getTrajectory().getInstantPosition(time), velocity));
	}

	@Override
	public boolean notifyInteractionPair_upperBoundaryCollision(CollisionBetweenCirclesSymmetricComponent other,
			float time) {
		return false;
	}

	@Override
	public boolean notifyInteractionPair_lowerBoundaryCollision(CollisionBetweenCirclesSymmetricComponent other,
			float time) {
		final NearestNormal normal = this.body.instantShape(time).perimetralDilate(other.body.instantShape(time).opposite()).nearestNormalToOrigin();
		
		Vector2f velocity1 = getVelocity(this.body);
		Vector2f velocity2 = getVelocity(other.body);
		
		if ( normal.getValue().scalarProjection(velocity1.sub(velocity2)) > 0.0f ) {
		
			final Vector2f originalNormalVelocity1 = normal.getValue().vectorProjection(velocity1);
			final Vector2f originalNormalVelocity2 = normal.getValue().vectorProjection(velocity2);
			
			velocity1 = velocity1.sub(originalNormalVelocity1).add(originalNormalVelocity2);
			velocity2 = velocity2.sub(originalNormalVelocity2).add(originalNormalVelocity1);
			
			setVelocity(this.body, time, velocity1);
			setVelocity(other.body, time, velocity2);
			
		}
		
		return true;
	}

	@Override
	public void notifyIntermediateRegionCollision(CollisionBetweenCirclesSymmetricComponent other, float time) {
		// TODO Auto-generated method stub
		
	}
}
