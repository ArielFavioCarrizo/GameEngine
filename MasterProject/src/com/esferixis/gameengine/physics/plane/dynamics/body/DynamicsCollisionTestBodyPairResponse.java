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
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;

import com.esferixis.gameengine.physics.plane.dynamics.components.ReceiverComponent;
import com.esferixis.gameengine.physics.plane.dynamics.components.SymmetricComponent;
import com.esferixis.gameengine.physics.plane.dynamics.components.TransmiterComponent;
import com.esferixis.gameengine.physics.plane.kinematics.CollisionResponse;
import com.esferixis.gameengine.physics.plane.kinematics.mapper.PlaneKinematicMapper;
import com.esferixis.misc.collection.set.BinarySet;

/**
 * @author ariel
 *
 */
final class DynamicsCollisionTestBodyPairResponse implements CollisionResponse {
	/**
	 * 
	 */
	private static final long serialVersionUID = 5802018939645429559L;

	private static final class TransmiterReceiverEntry<T extends TransmiterComponent> {
		private final T transmiterComponent;
		private final ReceiverComponent<T> receiverComponent;
		
		public TransmiterReceiverEntry(T transmiterComponent, ReceiverComponent<T> receiverComponent) {
			this.transmiterComponent = transmiterComponent;
			this.receiverComponent = receiverComponent;
		}
		
		/**
		 * @post Realiza la notificación con el tiempo especificado.
		 * 		 Frontera superior.
		 * 		 Devuelve si la frontera no es atravesable.
		 */
		private boolean notifyToReceiver_upperBoundaryCollision(float time) {
			return this.receiverComponent.notifyTransmiter_upperBoundaryCollision(this.transmiterComponent, time);
		}
		
		/**
		 * @post Realiza la notificación con el tiempo especificado.
		 * 		 Región intermedia.
		 */
		private void notifyToReceiver_intermediateRegionCollision(float time) {
			this.receiverComponent.notifyTransmiter_intermediateRegionCollision(transmiterComponent, time);
		}
		
		/**
		 * @post Realiza la notificación con el tiempo especificado.
		 * 		 Frontera inferior.
		 * 		 Devuelve si la frontera no es atravesable.
		 */
		private boolean notifyToReceiver_lowerBoundaryCollision(float time) {
			return this.receiverComponent.notifyTransmiter_lowerBoundaryCollision(this.transmiterComponent, time);
		}
	}
	
	private static final class SymmetricComponentsPairEntry<T extends SymmetricComponent<T>, U extends T> {
		private final T symmetricComponent1;
		private final U symmetricComponent2;
		
		public SymmetricComponentsPairEntry(T symmetricComponent1, U symmetricComponent2) {
			this.symmetricComponent1 = symmetricComponent1;
			this.symmetricComponent2 = symmetricComponent2;
		}
		
		/**
		 * @post Realiza la notificación con el tiempo especificado.
		 * 		 Frontera superior.
		 * 		 Devuelve si la frontera no es atravesable
		 */
		private boolean notifyInteractionPair_upperBoundaryCollision(float time) {
			return this.symmetricComponent1.notifyInteractionPair_upperBoundaryCollision(this.symmetricComponent2, time);
		}
		
		/**
		 * @post Realiza la notificación con el tiempo especificado.
		 * 		 Región intermedia.
		 */
		private void notifyInteractionPair_intermediateRegionCollision(float time) {
			this.symmetricComponent1.notifyIntermediateRegionCollision(this.symmetricComponent2, time);
		}
		
		/**
		 * @post Realiza la notificación con el tiempo especificado.
		 * 		 Frontera inferior.
		 * 		 Devuelve si la frontera no es atravesable
		 */
		private boolean notifyInteractionPair_lowerBoundaryCollision(float time) {
			return this.symmetricComponent1.notifyInteractionPair_lowerBoundaryCollision(this.symmetricComponent2, time);
		}
	}
	
	private final BinarySet<PlaneDynamicsBody<? extends PlaneKinematicMapper>> bodyPair;
	
	private int clientCount;
	
	private transient List<TransmiterReceiverEntry<?>> transmiterReceiverEntries = null;
	private transient List<SymmetricComponentsPairEntry<?, ?>> symmetricComponentEntries = null;
	
	private transient boolean updateEntries;
	
	/**
	 * @pre El par de cuerpo no puede ser nulo, y los cuerpos tienen que ser distintos
	 * @post Crea la respuesta con el par de cuerpos especificados
	 * @param body1
	 * @param body2
	 */
	public DynamicsCollisionTestBodyPairResponse(BinarySet<PlaneDynamicsBody<? extends PlaneKinematicMapper>> bodyPair) {
		if ( bodyPair != null ) {
			if ( bodyPair.getElement1() != bodyPair.getElement2() ) {
				this.bodyPair = bodyPair;
				this.clientCount = Integer.MIN_VALUE;
				this.initTrasients();
			}
			else {
				throw new IllegalArgumentException("Expected distinct bodies");
			}
		}
		else {
			throw new NullPointerException();
		}
	}
	
	private void initTrasients() {
		this.transmiterReceiverEntries = new ArrayList<TransmiterReceiverEntry<?>>();
		this.symmetricComponentEntries = new ArrayList<SymmetricComponentsPairEntry<?, ?>>();
		this.updateEntries = true;
	}
	
	private void readObject(java.io.ObjectInputStream stream) throws ClassNotFoundException, IOException {
		stream.defaultReadObject();
		this.initTrasients();
	}
	
	/**
	 * @post Incrementa la cuenta de clientes
	 */
	protected void incrementClientCount() {
		if ( this.clientCount != Integer.MAX_VALUE ) {
			this.clientCount++;
			
			this.updateEntries = true;
		}
		else {
			throw new IllegalStateException("Client count overflow");
		}
	}
	
	/**
	 * @post Decrementa la cuenta de cliente, devuelve si está en cero
	 */
	protected boolean decrementClientCount() {
		if ( this.clientCount != Integer.MIN_VALUE ) {
			this.clientCount--;
			
			this.updateEntries = true;
			
			return ( this.clientCount == Integer.MIN_VALUE );
		}
		else {
			throw new IllegalStateException("Client count overflow");
		}
	}
	
	/**
	 * @post Agrega las entradas correspondientes entre los transmisores y
	 * 		 receptores especificados
	 */
	private void addTransmiterReceiverEntries(Set<TransmiterComponent> transmiterComponents, Set<ReceiverComponent<?>> receiverComponents) {
		TransmiterComponent[] transmiterComponentsArray = transmiterComponents.toArray(new TransmiterComponent[0]);
		
		for ( ReceiverComponent<?> eachReceiverComponent : receiverComponents ) {
			for ( TransmiterComponent eachTransmiterComponent : transmiterComponentsArray ) {
				if ( eachReceiverComponent.getTransmiterComponentClass().isInstance(eachTransmiterComponent) ) {
					this.transmiterReceiverEntries.add(new TransmiterReceiverEntry(eachTransmiterComponent, eachReceiverComponent));
				}
			}
		}
	}
	
	/**
	 * @post Actualiza las entradas de componentes simétricas
	 */
	private void updateSymmetricComponentEntries(Set<SymmetricComponent<?>> symmetricComponents1, Set<SymmetricComponent<?>> symmetricComponents2) {
		final SymmetricComponent<?>[] symmetricComponentsArray2 = symmetricComponents2.toArray(new SymmetricComponent[0]);
		
		this.symmetricComponentEntries.clear();
		
		for ( SymmetricComponent<?> eachSymmetricComponent1 : symmetricComponents1 ) {
			for ( SymmetricComponent<?> eachSymmetricComponent2 : symmetricComponentsArray2 ) {
				if ( eachSymmetricComponent1.getSymmetricComponentClass().isAssignableFrom(eachSymmetricComponent2.getSymmetricComponentClass()) ) {
					this.symmetricComponentEntries.add(new SymmetricComponentsPairEntry(eachSymmetricComponent1, eachSymmetricComponent2));
				}
				else if ( eachSymmetricComponent2.getSymmetricComponentClass().isAssignableFrom(eachSymmetricComponent1.getSymmetricComponentClass()) ) {
					this.symmetricComponentEntries.add(new SymmetricComponentsPairEntry(eachSymmetricComponent2, eachSymmetricComponent1));
				}
			}
		}
		
	}

	private void checkUpdates() {
		if ( this.updateEntries ) {
			this.transmiterReceiverEntries.clear();
			
			this.addTransmiterReceiverEntries(this.bodyPair.getElement1().getTransmiterComponents(), this.bodyPair.getElement2().getReceiverComponents());
			this.addTransmiterReceiverEntries(this.bodyPair.getElement2().getTransmiterComponents(), this.bodyPair.getElement1().getReceiverComponents());
			
			this.updateSymmetricComponentEntries(this.bodyPair.getElement1().getSymmetricComponents(), this.bodyPair.getElement2().getSymmetricComponents());
			
			this.updateEntries = false;
		}
	}

	/* (non-Javadoc)
	 * @see com.arielcarrizo.gameengine.physics.plane.kinematics.CollisionResponse#notifyUpperBoundaryCollision(float)
	 */
	@Override
	public boolean notifyUpperBoundaryCollision(float time) {
		this.checkUpdates();
		
		boolean noCrossBoundary = false;
		
		for ( TransmiterReceiverEntry<?> eachEntry : this.transmiterReceiverEntries ) {
			noCrossBoundary |= eachEntry.notifyToReceiver_upperBoundaryCollision(time);
		}
		
		for ( SymmetricComponentsPairEntry<?, ?> eachSymmetricComponentsPair : this.symmetricComponentEntries ) {
			noCrossBoundary |= eachSymmetricComponentsPair.notifyInteractionPair_upperBoundaryCollision(time);
		}
		
		return noCrossBoundary;
	}

	/* (non-Javadoc)
	 * @see com.arielcarrizo.gameengine.physics.plane.kinematics.CollisionResponse#notifyLowerBoundaryCollision(float)
	 */
	@Override
	public boolean notifyLowerBoundaryCollision(float time) {
		this.checkUpdates();
		
		boolean noCrossBoundary = false;
		
		for ( TransmiterReceiverEntry<?> eachEntry : this.transmiterReceiverEntries ) {
			noCrossBoundary |= eachEntry.notifyToReceiver_lowerBoundaryCollision(time);
		}
		
		for ( SymmetricComponentsPairEntry<?, ?> eachSymmetricComponentsPair : this.symmetricComponentEntries ) {
			noCrossBoundary |= eachSymmetricComponentsPair.notifyInteractionPair_lowerBoundaryCollision(time);
		}
		
		return noCrossBoundary;
	}

	/* (non-Javadoc)
	 * @see com.arielcarrizo.gameengine.physics.plane.kinematics.CollisionResponse#notifyIntermediateRegionCollision(float)
	 */
	@Override
	public void notifyIntermediateRegionCollision(float time) {
		this.checkUpdates();
		
		for ( TransmiterReceiverEntry<?> eachEntry : this.transmiterReceiverEntries ) {
			eachEntry.notifyToReceiver_intermediateRegionCollision(time);
		}
		
		for ( SymmetricComponentsPairEntry<?, ?> eachSymmetricComponentsPair : this.symmetricComponentEntries ) {
			eachSymmetricComponentsPair.notifyInteractionPair_intermediateRegionCollision(time);
		}
	}
	
	
}
