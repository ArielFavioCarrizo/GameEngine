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
import com.esferixis.gameengine.physics.plane.dynamics.components.ReceiverComponent;
import com.esferixis.gameengine.physics.plane.kinematics.mapper.translation.LinearTrajectory;
import com.esferixis.gameengine.physics.plane.kinematics.mapper.translation.TranslationPlaneKinematicMapper;
import com.esferixis.geometry.plane.Shape.NearestNormal;
import com.esferixis.geometry.plane.finite.Circle;
import com.esferixis.geometry.plane.finite.FiniteProportionalHolomorphicShape;
import com.esferixis.math.Vector2f;

public class InfiniteMassBodyCollisionReceiverComponent extends ReceiverComponent<InfiniteMassBodyCollisionTransmiterComponent> {
	/**
	 * 
	 */
	private static final long serialVersionUID = -2791772034508863674L;
	private final StaticShapePlaneDynamicsBody<? extends FiniteProportionalHolomorphicShape<?>, TranslationPlaneKinematicMapper<LinearTrajectory>> receiverBody;
	
	public InfiniteMassBodyCollisionReceiverComponent(StaticShapePlaneDynamicsBody<? extends FiniteProportionalHolomorphicShape<?>, TranslationPlaneKinematicMapper<LinearTrajectory>> receiverBody) {
		super(InfiniteMassBodyCollisionTransmiterComponent.class);
		if ( receiverBody != null ) {
			this.receiverBody = receiverBody;
		}
		else {
			throw new NullPointerException();
		}
	}

	@Override
	public boolean notifyTransmiter_upperBoundaryCollision(
			InfiniteMassBodyCollisionTransmiterComponent transmiterComponent, float time) {
		return false;
	}

	@Override
	public boolean notifyTransmiter_lowerBoundaryCollision(
			InfiniteMassBodyCollisionTransmiterComponent transmiterComponent, float time) {
		final NearestNormal normal = this.receiverBody.instantShape(time).perimetralDilate(transmiterComponent.getTransmiterBody().instantShape(time).opposite()).nearestNormalToOrigin();
		
		if ( normal != null ) {
			Vector2f velocity = this.receiverBody.getKinematicMapper().getTrajectory().getVelocity();
			
			if ( normal.getValue().scalarProjection(velocity) > 0.0f ) {
			
				velocity = velocity.sub(normal.getValue().vectorProjection(velocity).scale(2.0f));
				
				this.receiverBody.getKinematicMapper().setTrajectory(new LinearTrajectory(time, this.receiverBody.getKinematicMapper().getTrajectory().getInstantPosition(time), velocity));
			
			}
			
			return true;
		}
		else {
			throw new NullPointerException();
		}
	}

	@Override
	public void notifyTransmiter_intermediateRegionCollision(
			InfiniteMassBodyCollisionTransmiterComponent transmiterComponent, float time) {
		// TODO Auto-generated method stub
		
	}

}
