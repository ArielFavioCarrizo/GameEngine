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

import java.io.Serializable;
import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.Stack;

import com.esferixis.gameengine.physics.plane.dynamics.components.ReceiverComponent;
import com.esferixis.gameengine.physics.plane.dynamics.components.SymmetricComponent;
import com.esferixis.gameengine.physics.plane.dynamics.components.TransmiterComponent;
import com.esferixis.gameengine.physics.plane.kinematics.CollisionTestBodyPairContainer;
import com.esferixis.gameengine.physics.plane.kinematics.PlaneKinematicBody;
import com.esferixis.gameengine.physics.plane.kinematics.mapper.PlaneKinematicMapper;
import com.esferixis.misc.ElementCallback;
import com.esferixis.misc.MappedCounters;
import com.esferixis.misc.collection.set.BinarySet;

/**
 * @author ariel
 *
 */
public final class PlaneDynamicsBodyContainer implements Serializable {
	/**
	 * 
	 */
	private static final long serialVersionUID = 1552213585328362250L;

	private final CollisionTestBodyPairContainer collisionTestBodyPairContainer;
	
	private static final class TransmiterClassProfile implements Serializable {
		/**
		 * 
		 */
		private static final long serialVersionUID = 6889339204652576309L;
		
		private final MappedCounters<PlaneDynamicsBody<? extends PlaneKinematicMapper>> transmiterBodies;
		private final MappedCounters<PlaneDynamicsBody<? extends PlaneKinematicMapper>> receiverBodies;
		
		public TransmiterClassProfile() {
			this.transmiterBodies = new MappedCounters<PlaneDynamicsBody<? extends PlaneKinematicMapper>>();
			this.receiverBodies = new MappedCounters<PlaneDynamicsBody<? extends PlaneKinematicMapper>>();
		}
	}
	
	private static final class SymmetricComponentProfile implements Serializable {
		/**
		 * 
		 */
		private static final long serialVersionUID = -5684058347631172258L;
		
		private final MappedCounters<PlaneDynamicsBody<? extends PlaneKinematicMapper>> bodiesWithExactClass;
		
		public SymmetricComponentProfile() {
			this.bodiesWithExactClass = new MappedCounters<PlaneDynamicsBody<? extends PlaneKinematicMapper>>();
		}
	}
	
	private final Map<Class<? extends TransmiterComponent>, TransmiterClassProfile> profileByTransmiterComponentClass;
	private final Map<Class<? extends SymmetricComponent<?>>, SymmetricComponentProfile> profileBySymmetricComponentClass;
	
	private final Map<BinarySet<PlaneDynamicsBody<? extends PlaneKinematicMapper>>, DynamicsCollisionTestBodyPairResponse> responseByBodyPair;
	
	/**
	 * @post Crea el contenedor, acoplándose al contenedor de pares de test de colisión
	 * 		 especificado
	 */
	public PlaneDynamicsBodyContainer(CollisionTestBodyPairContainer collisionTestBodyPairContainer) {
		if ( collisionTestBodyPairContainer != null ) {
			this.collisionTestBodyPairContainer = collisionTestBodyPairContainer;
			
			this.profileByTransmiterComponentClass = new HashMap<Class<? extends TransmiterComponent>, TransmiterClassProfile>();
			this.profileBySymmetricComponentClass = new HashMap<Class<? extends SymmetricComponent<?>>, SymmetricComponentProfile>();
			
			this.responseByBodyPair = new HashMap<BinarySet<PlaneDynamicsBody<? extends PlaneKinematicMapper>>, DynamicsCollisionTestBodyPairResponse>();
		}
		else {
			throw new NullPointerException();
		}
	}
	
	/**
	 * @post Devuelve el par de cuerpos cinemáticos para el par de objetos dinámicos
	 * 		 especificados
	 */
	protected static BinarySet<PlaneKinematicBody<? extends PlaneKinematicMapper>> getKinematicBodyPair(BinarySet<PlaneDynamicsBody<? extends PlaneKinematicMapper>> bodyPair) {
		if ( bodyPair != null ) {
			return new BinarySet<PlaneKinematicBody<? extends PlaneKinematicMapper>>(bodyPair.getElement1().getPlaneKinematicBody(), bodyPair.getElement2().getPlaneKinematicBody());
		}
		else {
			throw new NullPointerException();
		}
	}
	
	/**
	 * @pre El cuerpo no puede ser nulo
	 * @post Devuelve si el cuerpo especificado es agregable.
	 * 		 Es agregable cuando el cuerpo no está contenido por ningún contenedor, o está contenido por el mismo
	 */
	public final boolean isAddable(PlaneDynamicsBody<? extends PlaneKinematicMapper> planeDynamicsBody) {
		if ( planeDynamicsBody != null ) {
			return ( ( planeDynamicsBody.getPlaneDynamicsBodyContainer() == null ) || ( planeDynamicsBody.getPlaneDynamicsBodyContainer() == this ) );
		}
		else {
			throw new NullPointerException();
		}
	}
	
	/**
	 * @pre El cuerpo no puede ser nulo, y no tiene que estar contenido por ningún otro contenedor
	 * @post Agrega el cuerpo especificado-
	 * 		 Si ya está agregado no hace nada.
	 * 		 Devuelve true si lo agregó, false en contrario contrario.
	 */
	public final boolean add(PlaneDynamicsBody<? extends PlaneKinematicMapper> planeDynamicsBody) {
		if ( planeDynamicsBody != null ) {
			if ( this.isAddable(planeDynamicsBody) ) {
				final boolean changed;
				
				if ( planeDynamicsBody.getPlaneDynamicsBodyContainer() == null ) {
					for ( TransmiterComponent eachTransmiterComponent : planeDynamicsBody.getTransmiterComponents() ) {
						this.notifyTransmiterComponentAdd(planeDynamicsBody, eachTransmiterComponent);
					}
					for ( ReceiverComponent<?> eachReceiverComponent : planeDynamicsBody.getReceiverComponents() ) {
						this.notifyReceiverComponentAdd(planeDynamicsBody, eachReceiverComponent);
					}
					for ( SymmetricComponent<?> eachSymmetricComponent : planeDynamicsBody.getSymmetricComponents() ) {
						this.notifySymmetricComponentAdd(planeDynamicsBody, eachSymmetricComponent);
					}
					
					planeDynamicsBody.attachPlaneDynamicsBodyContainer(this);
					
					changed = true;
				}
				else {
					changed = false;
				}
				
				return changed;
			}
			else {
				throw new IllegalStateException("Expected body with no container or with the same container");
			}
		}
		else {
			throw new NullPointerException();
		}
	}
	
	/**
	 * @pre El cuerpo no puede ser nulo, y no tiene que estar contenido por ningún otro contenedor
	 * @post Agrega el cuerpo especificado-
	 * 		 Si ya está agregado no hace nada.
	 * 		 Devuelve true si lo agregó, false en contrario contrario.
	 */
	public final boolean remove(PlaneDynamicsBody<? extends PlaneKinematicMapper> planeDynamicsBody) {
		if ( planeDynamicsBody != null ) {
			final boolean changed;
			
			if ( planeDynamicsBody.getPlaneDynamicsBodyContainer() != null ) { 
				if ( planeDynamicsBody.getPlaneDynamicsBodyContainer() == this ) {
					for ( TransmiterComponent eachTransmiterComponent : planeDynamicsBody.getTransmiterComponents() ) {
						this.notifyTransmiterComponentRemove(planeDynamicsBody, eachTransmiterComponent);
					}
					for ( ReceiverComponent<?> eachReceiverComponent : planeDynamicsBody.getReceiverComponents() ) {
						this.notifyReceiverComponentRemove(planeDynamicsBody, eachReceiverComponent);
					}
					for ( SymmetricComponent<?> eachSymmetricComponent : planeDynamicsBody.getSymmetricComponents() ) {
						this.notifySymmetricComponentRemove(planeDynamicsBody, eachSymmetricComponent);
					}
					
					planeDynamicsBody.detachPlaneDynamicsBodyContainer();
					
					changed = true;
				}
				else {
					throw new IllegalStateException("Expected body added to this container");
				}
			}
			else {
				changed = false;
			}
			
			return changed;
		}
		else {
			throw new NullPointerException();
		}
	}
	
	private void processTransmiterClassProfile(Class<? extends TransmiterComponent> transmiterComponentClass, ElementCallback<TransmiterClassProfile> transmiterClassProfileCallBack, boolean superclasses) {
		final Stack<Class<?>> pendingClasses = new Stack<Class<?>>();
		final Set<Class<?>> proccesedClasses = new HashSet<Class<?>>();
		
		pendingClasses.push(transmiterComponentClass);
		
		while (!pendingClasses.isEmpty()) {
			final Class<?> eachClass = pendingClasses.pop();
			
			if ( ( eachClass != null ) && TransmiterComponent.class.isAssignableFrom(eachClass) ) {
				if ( proccesedClasses.add(eachClass) ) {
					transmiterComponentClass = (Class<? extends TransmiterComponent>) eachClass;
					
					TransmiterClassProfile transmiterClassProfile = this.profileByTransmiterComponentClass.get(transmiterComponentClass);
					
					if ( transmiterClassProfile == null ) {
						transmiterClassProfile = new TransmiterClassProfile();
						
						this.profileByTransmiterComponentClass.put(transmiterComponentClass, transmiterClassProfile);
					}
					
					transmiterClassProfileCallBack.run(transmiterClassProfile);
					
					if ( transmiterClassProfile.transmiterBodies.getNonZeroElements().isEmpty() && transmiterClassProfile.receiverBodies.getNonZeroElements().isEmpty() ) {
						this.profileByTransmiterComponentClass.remove(transmiterComponentClass);
					}
					
					if ( superclasses ) {
						pendingClasses.add(transmiterComponentClass.getSuperclass());
						pendingClasses.addAll(Arrays.asList(transmiterComponentClass.getInterfaces()));
					}
				}
			}
		}
	}
	
	private void processSymmetricComponentProfile(Class<? extends SymmetricComponent<?>> symmetricComponentClass, ElementCallback<SymmetricComponentProfile> symmetricComponentProfileCallBack, boolean superclasses) {
		do {
		
			SymmetricComponentProfile symmetricComponentProfile = this.profileBySymmetricComponentClass.get(symmetricComponentClass);
			
			if ( symmetricComponentProfile == null ) {
				symmetricComponentProfile = new SymmetricComponentProfile();
				this.profileBySymmetricComponentClass.put(symmetricComponentClass, symmetricComponentProfile);
			}
			
			symmetricComponentProfileCallBack.run(symmetricComponentProfile);
			
			if ( symmetricComponentProfile.bodiesWithExactClass.getNonZeroElements().isEmpty() ) {
				this.profileBySymmetricComponentClass.remove(symmetricComponentClass);
			}
			
			if ( ( superclasses ) && ( !symmetricComponentClass.equals(SymmetricComponent.class) ) ){
				symmetricComponentClass = (Class<? extends SymmetricComponent<?>>) symmetricComponentClass.getSuperclass();
			}
			else {
				symmetricComponentClass = null;
			}
			
		} while ( symmetricComponentClass != null );
	}
	
	/**
	 * @pre El par no puede ser nulo
	 * @post Incrementa la cuenta de cliente
	 */
	private void incrementClientCount(BinarySet<PlaneDynamicsBody<? extends PlaneKinematicMapper>> bodyPair) {
		if ( bodyPair != null ) {
			DynamicsCollisionTestBodyPairResponse response = this.responseByBodyPair.get(bodyPair);
			
			if ( response == null ) {
				response = new DynamicsCollisionTestBodyPairResponse(bodyPair);
				this.responseByBodyPair.put(bodyPair, response);
				this.collisionTestBodyPairContainer.setCollisionResponse( getKinematicBodyPair(bodyPair), response);
			}
			
			response.incrementClientCount();
		}
		else {
			throw new NullPointerException();
		}
	}
	
	/**
	 * @pre El par no puede ser nulo
	 * @post Decrementa la cuenta de cliente
	 */
	private void decrementClientCount(BinarySet<PlaneDynamicsBody<? extends PlaneKinematicMapper>> bodyPair) {
		if ( bodyPair != null ) {
			DynamicsCollisionTestBodyPairResponse response = this.responseByBodyPair.get(bodyPair);
			
			if ( response != null ) {
				response = new DynamicsCollisionTestBodyPairResponse(bodyPair);
				
				if ( response.decrementClientCount() ) {
					this.collisionTestBodyPairContainer.setCollisionResponse( getKinematicBodyPair(bodyPair), null);
					this.responseByBodyPair.remove(bodyPair);
				}
				
			}
		}
		else {
			throw new NullPointerException();
		}
	}
	
	/**
	 * @pre Ningún parámetro puede ser nulo
	 * @post Notifica que se agregó el componente transmisor especificado al cuerpo especificado
	 */
	protected final void notifyTransmiterComponentAdd(final PlaneDynamicsBody<? extends PlaneKinematicMapper> planeDynamicsBody, final TransmiterComponent transmiterComponent) {
		if ( ( planeDynamicsBody != null ) && ( transmiterComponent != null ) ) {			
			this.processTransmiterClassProfile(transmiterComponent.getClass(), new ElementCallback<TransmiterClassProfile>(){

				@Override
				public void run(TransmiterClassProfile profile) {
					profile.transmiterBodies.increment(planeDynamicsBody);
					
					for ( PlaneDynamicsBody<? extends PlaneKinematicMapper> eachReceiverBody : profile.receiverBodies.getNonZeroElements() ) {
						if ( eachReceiverBody != planeDynamicsBody ) {
							PlaneDynamicsBodyContainer.this.incrementClientCount(new BinarySet<PlaneDynamicsBody<? extends PlaneKinematicMapper>>(planeDynamicsBody, eachReceiverBody));
						}
					}
				}
				
			}, true);
		}
		else {
			throw new NullPointerException();
		}
	}
	
	/**
	 * @pre Ningún parámetro puede ser nulo
	 * @post Notifica que se quitó el componente transmisor especificado al cuerpo especificado
	 */
	protected final void notifyTransmiterComponentRemove(final PlaneDynamicsBody<? extends PlaneKinematicMapper> planeDynamicsBody, final TransmiterComponent transmiterComponent) {
		if ( ( planeDynamicsBody != null ) && ( transmiterComponent != null ) ) {
			this.processTransmiterClassProfile(transmiterComponent.getClass(), new ElementCallback<TransmiterClassProfile>(){

				@Override
				public void run(TransmiterClassProfile profile) {
					profile.transmiterBodies.decrement(planeDynamicsBody);
					
					for ( PlaneDynamicsBody<? extends PlaneKinematicMapper> eachReceiverBody : profile.receiverBodies.getNonZeroElements() ) {
						if ( eachReceiverBody != planeDynamicsBody ) {
							PlaneDynamicsBodyContainer.this.decrementClientCount(new BinarySet<PlaneDynamicsBody<? extends PlaneKinematicMapper>>(planeDynamicsBody, eachReceiverBody));
						}
					}
				}
				
			}, true);
		}
		else {
			throw new NullPointerException();
		}
	}
	
	/**
	 * @pre Ningún parámetro puede ser nulo
	 * @post Notifica que se agregó el componente receptor especificado al cuerpo especificado
	 */
	protected final void notifyReceiverComponentAdd(final PlaneDynamicsBody<? extends PlaneKinematicMapper> planeDynamicsBody, final ReceiverComponent<?> receiverComponent) {
		if ( ( planeDynamicsBody != null ) && ( receiverComponent != null ) ) {
			this.processTransmiterClassProfile(receiverComponent.getTransmiterComponentClass(), new ElementCallback<TransmiterClassProfile>(){

				@Override
				public void run(TransmiterClassProfile profile) {
					profile.receiverBodies.increment(planeDynamicsBody);
					
					for ( PlaneDynamicsBody<? extends PlaneKinematicMapper> eachTransmiterBody : profile.transmiterBodies.getNonZeroElements() ) {
						if ( eachTransmiterBody != planeDynamicsBody ) {
							PlaneDynamicsBodyContainer.this.incrementClientCount(new BinarySet<PlaneDynamicsBody<? extends PlaneKinematicMapper>>(eachTransmiterBody, planeDynamicsBody));
						}
					}
				}
					
			}, false);
		}
		else {
			throw new NullPointerException();
		}
	}
	
	/**
	 * @pre Ningún parámetro puede ser nulo
	 * @post Notifica que se quitó el componente receptor especificado al cuerpo especificado
	 */
	protected final void notifyReceiverComponentRemove(final PlaneDynamicsBody<? extends PlaneKinematicMapper> planeDynamicsBody, final ReceiverComponent<?> receiverComponent) {
		if ( ( planeDynamicsBody != null ) && ( receiverComponent != null ) ) {
			this.processTransmiterClassProfile(receiverComponent.getTransmiterComponentClass(), new ElementCallback<TransmiterClassProfile>(){

				@Override
				public void run(TransmiterClassProfile profile) {
					profile.receiverBodies.decrement(planeDynamicsBody);
					
					for ( PlaneDynamicsBody<? extends PlaneKinematicMapper> eachTransmiterBody : profile.transmiterBodies.getNonZeroElements() ) {
						if ( eachTransmiterBody != planeDynamicsBody ) {
							PlaneDynamicsBodyContainer.this.decrementClientCount(new BinarySet<PlaneDynamicsBody<? extends PlaneKinematicMapper>>(eachTransmiterBody, planeDynamicsBody));
						}
					}
				}
				
			}, false);
		}
		else {
			throw new NullPointerException();
		}
	}
	
	/**
	 * @pre Ningún parámetro puede ser nulo
	 * @post Notifica que se agregó el componente simétrico especificado, al ciuerpo especificado
	 */
	protected final void notifySymmetricComponentAdd(final PlaneDynamicsBody<? extends PlaneKinematicMapper> planeDynamicsBody, final SymmetricComponent<? extends SymmetricComponent<?>> symmetricComponent) {
		if ( ( planeDynamicsBody != null ) && ( symmetricComponent != null ) )  {
			this.processSymmetricComponentProfile(symmetricComponent.getSymmetricComponentClass(), new ElementCallback<SymmetricComponentProfile>() {

				@Override
				public void run(SymmetricComponentProfile symmetricComponentProfile) {					
					symmetricComponentProfile.bodiesWithExactClass.increment(planeDynamicsBody);
				}
				
			}, false);
			
			this.processSymmetricComponentProfile(symmetricComponent.getSymmetricComponentClass(), new ElementCallback<SymmetricComponentProfile>() {

				@Override
				public void run(SymmetricComponentProfile symmetricComponentProfile) {
					for ( PlaneDynamicsBody<? extends PlaneKinematicMapper> eachPlaneKinematicsBody : symmetricComponentProfile.bodiesWithExactClass.getNonZeroElements() ) {
						if ( eachPlaneKinematicsBody != planeDynamicsBody ) {
							PlaneDynamicsBodyContainer.this.incrementClientCount(new BinarySet<PlaneDynamicsBody<? extends PlaneKinematicMapper>>(eachPlaneKinematicsBody, planeDynamicsBody));
						}
					}
				}
				
			}, true);
		}
		else {
			throw new NullPointerException();
		}
	}
	
	/**
	 * @pre Ningún parámetro puede ser nulo
	 * @post Notifica que se agregó el componente simétrico especificado, al ciuerpo especificado
	 */
	protected final void notifySymmetricComponentRemove(final PlaneDynamicsBody<? extends PlaneKinematicMapper> planeDynamicsBody, final SymmetricComponent<? extends SymmetricComponent<?>> symmetricComponent) {
		if ( ( planeDynamicsBody != null ) && ( symmetricComponent != null ) )  {
			this.processSymmetricComponentProfile(symmetricComponent.getSymmetricComponentClass(), new ElementCallback<SymmetricComponentProfile>() {

				@Override
				public void run(SymmetricComponentProfile symmetricComponentProfile) {
					for ( PlaneDynamicsBody<? extends PlaneKinematicMapper> eachPlaneKinematicsBody : symmetricComponentProfile.bodiesWithExactClass.getNonZeroElements() ) {
						if ( eachPlaneKinematicsBody != planeDynamicsBody ) {
							PlaneDynamicsBodyContainer.this.decrementClientCount(new BinarySet<PlaneDynamicsBody<? extends PlaneKinematicMapper>>(eachPlaneKinematicsBody, planeDynamicsBody));
						}
					}
				}
				
			}, true);
			
			this.processSymmetricComponentProfile(symmetricComponent.getSymmetricComponentClass(), new ElementCallback<SymmetricComponentProfile>() {

				@Override
				public void run(SymmetricComponentProfile symmetricComponentProfile) {					
					symmetricComponentProfile.bodiesWithExactClass.decrement(planeDynamicsBody);
				}
				
			}, false);
		}
		else {
			throw new NullPointerException();
		}
	}
}
