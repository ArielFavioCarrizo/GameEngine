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

package com.esferixis.gameengine.physics.plane.kinematics.containers;

import java.util.HashMap;
import java.util.Map;

import com.esferixis.gameengine.physics.plane.kinematics.CollisionResponse;
import com.esferixis.gameengine.physics.plane.kinematics.CollisionTestBodyPair;
import com.esferixis.gameengine.physics.plane.kinematics.CollisionTestBodyPairContainer;
import com.esferixis.gameengine.physics.plane.kinematics.CollisionTestBodyPairEmmiter;
import com.esferixis.gameengine.physics.plane.kinematics.CollisionTestBodyPairTester;
import com.esferixis.gameengine.physics.plane.kinematics.PlaneKinematicBody;
import com.esferixis.gameengine.physics.plane.kinematics.mapper.PlaneKinematicMapper;
import com.esferixis.math.intervalarithmetic.FloatClosedInterval;
import com.esferixis.misc.collection.set.BinarySet;

/**
 * @author ariel
 *
 */
public final class LinearBodyPairCollisionTestPairContainer extends CollisionTestBodyPairContainer {

	/**
	 * 
	 */
	private static final long serialVersionUID = 213028069705162518L;
	
	private final Map<BinarySet<PlaneKinematicBody<? extends PlaneKinematicMapper>>, CollisionTestBodyPair> collisionTestPairByBodyPair;
	
	/**
	 * @pre El intervalo de distancia de colisión no puede ser nulo
	 * @post Crea el contenedor de pares de colisión con el intervalo de distancia de colisión
	 * 	 	 especificado.
	 */
	public LinearBodyPairCollisionTestPairContainer(FloatClosedInterval collisionDistanceInterval) {
		super(collisionDistanceInterval);
		this.collisionTestPairByBodyPair = new HashMap<BinarySet<PlaneKinematicBody<? extends PlaneKinematicMapper>>, CollisionTestBodyPair>();
	}
	
	/* (non-Javadoc)
	 * @see com.arielcarrizo.gameengine.physics.plane.kinematics.CollisionTestBodyPairContainer#setCollisionResponse_checked(com.arielcarrizo.misc.collection.set.BinarySet, com.arielcarrizo.gameengine.physics.plane.kinematics.CollisionResponse)
	 */
	@Override
	protected CollisionResponse setCollisionResponse_checked(
			BinarySet<PlaneKinematicBody<? extends PlaneKinematicMapper>> pair,
			CollisionResponse response) {
		final CollisionResponse oldResponse;
		
		if ( response == null ) {
			oldResponse = this.collisionTestPairByBodyPair.remove(pair).getResponse();
		}
		else {
			CollisionTestBodyPair oldTestPair = this.collisionTestPairByBodyPair.put(pair, new CollisionTestBodyPair(pair, response));
			oldResponse = (oldTestPair != null ? oldTestPair.getResponse() : null);
		}
		
		return oldResponse;
	}

	/* (non-Javadoc)
	 * @see com.arielcarrizo.gameengine.physics.plane.kinematics.CollisionTestBodyPairContainer#getEmmiter()
	 */
	@Override
	protected CollisionTestBodyPairEmmiter getEmmiter() {
		return new CollisionTestBodyPairEmmiter() {

			@Override
			public void emitTests(CollisionTestBodyPairTester kinematicEngineCollisionTester) {
				for ( CollisionTestBodyPair eachTestPair : LinearBodyPairCollisionTestPairContainer.this.collisionTestPairByBodyPair.values() ) {
					kinematicEngineCollisionTester.test(eachTestPair);
				}
			}
			
		};
	}
	

	/* (non-Javadoc)
	 * @see com.arielcarrizo.gameengine.physics.plane.kinematics.CollisionTestBodyPairContainer#notifyChange(com.arielcarrizo.gameengine.physics.plane.kinematics.PlaneKinematicBody)
	 */
	@Override
	protected void notifyChange(
			PlaneKinematicBody<? extends PlaneKinematicMapper> planeKinematicBody) {
		
	}

	/* (non-Javadoc)
	 * @see com.arielcarrizo.gameengine.physics.plane.kinematics.CollisionTestBodyPairContainer#getCollisionResponse_checked(com.arielcarrizo.misc.collection.set.BinarySet)
	 */
	@Override
	protected CollisionResponse getCollisionResponse_checked(
			BinarySet<PlaneKinematicBody<? extends PlaneKinematicMapper>> pair) {
		final CollisionTestBodyPair collisionTestBodyPair = this.collisionTestPairByBodyPair.get(pair);
		final CollisionResponse collisionResponse;
		
		if ( collisionTestBodyPair != null ) {
			collisionResponse = collisionTestBodyPair.getResponse();
		}
		else {
			collisionResponse = null;
		}
		
		return collisionResponse;
	}
}
