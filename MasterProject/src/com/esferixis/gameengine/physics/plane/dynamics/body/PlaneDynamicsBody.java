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
import java.util.Collections;
import java.util.HashSet;
import java.util.Set;

import com.esferixis.gameengine.physics.plane.dynamics.components.ReceiverComponent;
import com.esferixis.gameengine.physics.plane.dynamics.components.SymmetricComponent;
import com.esferixis.gameengine.physics.plane.dynamics.components.TransmiterComponent;
import com.esferixis.gameengine.physics.plane.kinematics.PlaneKinematicBody;
import com.esferixis.gameengine.physics.plane.kinematics.mapper.PlaneKinematicMapper;
import com.esferixis.geometry.plane.finite.FiniteProportionalHolomorphicShape;

/**
 * Cuerpo dinámico, basado en componentes.
 * Cuando colisionan dos cuerpos, interaccionan entré si los recepetores y
 * transmisores compatibles de cada lado.
 * Lo mismo sucede los con los componentes simétricos, que pueden ejecutarse
 * en cualquiera de los dos sentidos, pero no ambos al mismo tiempo.
 * 
 * @author ariel
 *
 */
public abstract class PlaneDynamicsBody<M extends PlaneKinematicMapper> implements Serializable {
	/**
	 * 
	 */
	private static final long serialVersionUID = 2503465817492047775L;
	
	private final Set<TransmiterComponent> transmiterComponents;
	private final Set<ReceiverComponent<?>> receiverComponents;
	private final Set<SymmetricComponent<?>> symmetricComponents;
	
	private PlaneDynamicsBodyContainer planeDynamicsBodyContainer;
	
	/**
	 * @post Crea el objeto dinámico
	 */
	public PlaneDynamicsBody() {
		this.transmiterComponents = new HashSet<TransmiterComponent>();
		this.receiverComponents = new HashSet<ReceiverComponent<?>>();
		this.symmetricComponents = new HashSet<SymmetricComponent<?>>();
		
		this.planeDynamicsBodyContainer = null;
	}
	
	/**
	 * @pre El componente transmisor no puede ser nulo
	 * @post Agrega un componente transmisor
	 */
	public final void addTransmiterComponent(TransmiterComponent transmiterComponent) {
		if ( transmiterComponent != null ) {
			if ( this.transmiterComponents.add(transmiterComponent) ) {
				if ( this.planeDynamicsBodyContainer != null ) {
					this.planeDynamicsBodyContainer.notifyTransmiterComponentAdd(PlaneDynamicsBody.this, transmiterComponent);
				}
			}
		}
		else {
			throw new NullPointerException();
		}
	}
	
	/**
	 * @pre El componente transmisor no puede ser nulo
	 * @post Quita un componente transmisor
	 */
	public final void removeTransmiterComponent(TransmiterComponent transmiterComponent) {
		if ( transmiterComponent != null ) {
			if ( this.transmiterComponents.remove(transmiterComponent) ) {
				if ( PlaneDynamicsBody.this.planeDynamicsBodyContainer != null ) {
					PlaneDynamicsBody.this.planeDynamicsBodyContainer.notifyTransmiterComponentRemove(PlaneDynamicsBody.this, transmiterComponent);
				}
			}
			
			this.transmiterComponents.remove(transmiterComponent);
		}
		else {
			throw new NullPointerException();
		}
	}
	
	/**
	 * @pre El componente receptor no puede ser nulo
	 * @post Agrega un componente receptor
	 */
	public final void addReceiverComponent(ReceiverComponent<?> receiverComponent) {
		if ( receiverComponent != null ) {
			if ( this.receiverComponents.add(receiverComponent) ) {
				if ( this.planeDynamicsBodyContainer != null ) {
					this.planeDynamicsBodyContainer.notifyReceiverComponentAdd(PlaneDynamicsBody.this, receiverComponent);
				}
			}
		}
		else {
			throw new NullPointerException();
		}
	}
	
	/**
	 * @pre El componente receptor no puede ser nulo
	 * @post Quita un componente receptor
	 */
	public final void removeReceiverComponent(ReceiverComponent<?> receiverComponent) {
		if ( receiverComponent != null ) {
			if ( this.receiverComponents.remove(receiverComponent) ) {
				if ( this.planeDynamicsBodyContainer != null ) {
					this.planeDynamicsBodyContainer.notifyReceiverComponentRemove(PlaneDynamicsBody.this, receiverComponent);
				}
			}
		}
		else {
			throw new NullPointerException();
		}
	}
	
	/**
	 * @pre El componente simétrico no puede ser nulo
	 * @post Agrega un componente simétrico
	 */
	public final void addSymmetricComponent(SymmetricComponent<?> symmetricComponent) {
		if ( symmetricComponent != null ) {
			if ( this.symmetricComponents.add(symmetricComponent) ) {
				if ( this.planeDynamicsBodyContainer != null ) {
					this.planeDynamicsBodyContainer.notifySymmetricComponentAdd(PlaneDynamicsBody.this, symmetricComponent);
				}
			}
		}
		else {
			throw new NullPointerException();
		}
	}
	
	/**
	 * @pre El componente simétrico no puede ser nulo
	 * @post Quita un componente simétrico
	 */
	public final void removeSymmetricComponent(SymmetricComponent<?> symmetricComponent) {
		if ( symmetricComponent != null ) {
			if ( this.symmetricComponents.add(symmetricComponent) ) {
				if ( this.planeDynamicsBodyContainer != null ) {
					this.planeDynamicsBodyContainer.notifySymmetricComponentRemove(PlaneDynamicsBody.this, symmetricComponent);
				}
			}
		}
		else {
			throw new NullPointerException();
		}
	}
	
	/**
	 * @post Devuelve los componentes transmisores (Visión de sólo lectura)
	 */
	protected final Set<TransmiterComponent> getTransmiterComponents() {
		return Collections.unmodifiableSet(this.transmiterComponents);
	}
	
	/**
	 * @post Devuelve los componentes receptores (Visión de sólo lectura)
	 */
	protected final Set<ReceiverComponent<?>> getReceiverComponents() {
		return Collections.unmodifiableSet(this.receiverComponents);
	}
	
	/**
	 * @post Devuelve los componentes simétricos (Visión de sólo lectura)
	 */
	protected final Set<SymmetricComponent<?>> getSymmetricComponents() {
		return Collections.unmodifiableSet(this.symmetricComponents);
	}
	
	/**
	 * @post Especifica el mapeador cinemático
	 */
	public abstract void setKinematicMapper(M kinematicMapper);
	
	/**
	 * @post Devuelve el mapeador cinemático
	 */
	public abstract M getKinematicMapper();
	
	/**
	 * @pre El contenedor no puede ser nulo, y no tiene que haber contenedor previo
	 * @post Asocia el contenedor especificado
	 */
	protected final void attachPlaneDynamicsBodyContainer(PlaneDynamicsBodyContainer planeDynamicsBodyContainer) {
		if ( planeDynamicsBodyContainer != null ) {
			if ( this.planeDynamicsBodyContainer == null ) {
				this.planeDynamicsBodyContainer = planeDynamicsBodyContainer;
			}
			else {
				throw new IllegalStateException("Expected body with no container");
			}
		}
		else {
			throw new NullPointerException();
		}
	}
	
	/**
	 * @pre Tiene que estar contenido por algún contenedor
	 * @post Desasocia el contenedor especificado
	 */
	protected final void detachPlaneDynamicsBodyContainer() {
		if ( this.planeDynamicsBodyContainer != null ) {
			this.planeDynamicsBodyContainer = null;
		}
		else {
			throw new NullPointerException();
		}
	}
	
	/**
	 * @post Devuelve el contenedor, si no está en ninguno devuelve null
	 */
	protected final PlaneDynamicsBodyContainer getPlaneDynamicsBodyContainer() {
		return this.planeDynamicsBodyContainer;
	}
	
	/**
	 * @post Devuelve el cuerpo cinemático
	 */
	protected abstract PlaneKinematicBody<M> getPlaneKinematicBody();
	
	/**
	 * @post Devuelve la figura en el instante de tiempo especificado
	 */
	public FiniteProportionalHolomorphicShape<?> instantShape(float time) {
		return this.getPlaneKinematicBody().instantShape(time);
	}
}
