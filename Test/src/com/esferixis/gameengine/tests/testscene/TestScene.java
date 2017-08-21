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

package com.esferixis.gameengine.tests.testscene;

import com.esferixis.gameengine.physics.time.TemporalEventsEngine;
import com.esferixis.gameengine.physics.time.TemporalEventsManager;
import com.esferixis.gameengine.platform.input.InputManager;
import com.esferixis.gameengine.renderengine.frontend.plane.kinematics.containers.KinematicRenderPlaneObjectContainer;

public abstract class TestScene {
	private final String name;
	
	public static final class InstanceData {
		private final String name;
		
		private final InputManager inputManager;
		private final TemporalEventsManager temporalEventsManager;
		private final KinematicRenderPlaneObjectContainer kinematicRenderPlaneObjectContainer;
		
		/**
		 * @pre Ninguno de los parámetros puede ser nulo
		 * @post Crea la instancia con el nombre, con el gestor de entradas, el gestor de eventos temporales
		 * 		 y el contenedor de objetos de rendering especificado
		 */
		private InstanceData(String name, InputManager inputManager, TemporalEventsManager temporalEventsManager, KinematicRenderPlaneObjectContainer kinematicRenderPlaneObjectContainer) {
			this.name = name;
			this.inputManager = inputManager;
			this.temporalEventsManager = temporalEventsManager;
			this.kinematicRenderPlaneObjectContainer = kinematicRenderPlaneObjectContainer;
		}
	}
	
	public static abstract class Instance {
		private final String name;
		
		protected final InputManager inputManager;
		protected final TemporalEventsManager temporalEventsManager;
		protected final KinematicRenderPlaneObjectContainer kinematicRenderPlaneObjectContainer;
		
		protected boolean lockedCamera;
		
		private boolean destroyed;
		
		/**
		 * @pre Los datos de instancia no pueden ser nulos
		 * @post Crea la instancia con los datos de instancia especificados
		 * @param name
		 * @param inputManager
		 * @param temporalEventsEngine
		 * @param kinematicRenderPlaneObjectContainer
		 */
		public Instance(InstanceData instanceData) {
			if ( instanceData != null ) {
				this.name = instanceData.name;
				this.inputManager = instanceData.inputManager;
				this.temporalEventsManager = instanceData.temporalEventsManager;
				this.kinematicRenderPlaneObjectContainer = instanceData.kinematicRenderPlaneObjectContainer;
				
				this.lockedCamera = false;
				
				this.destroyed = false;
				
				this.load();
			}
			else {
				throw new NullPointerException();
			}
		}
		
		/**
		 * @post Devuelve el nombre
		 */
		public String getName() {
			return this.name;
		}
		
		/**
		 * @post Devuelve si la cámara está bloqueada
		 */
		public boolean isCameraLocked() {
			return this.lockedCamera;
		}
		
		/**
		 * @post Carga los elementos
		 */
		protected abstract void load();
		
		/**
		 * @pre No tiene que haber sido destruido
		 * @post Descarga y destruye los objetos creados
		 */
		public final void destroy() {
			if ( !this.destroyed ) {
				this.destroy_checked();
			}
			else {
				throw new IllegalStateException("Attemped to destroy when it has been destroyed");
			}
		}
		
		/**
		 * @pre No tiene que haber sido destruido
		 * @post Descarga y destruye los objetos creados
		 */
		protected abstract void destroy_checked();
	}
	
	/**
	 * @pre El nombre no puede ser nulo
	 * @post Crea la escena de testing con el nombre especificado
	 */
	public TestScene(String name) {
		if ( name != null ) {
			this.name = name;
		}
		else {
			throw new NullPointerException();
		}
	}
	
	/**
	 * @post Devuelve el nombre
	 */
	public String getName() {
		return this.name;
	}
	
	/**
	 * @pre Ninguno de los parámetros puede ser nulo
	 * @post Crea una instancia con el gestor de entradas, el gestor de eventos temporales, y el contenedor
	 * 		 de objetos de rendering
	 */
	public Instance create(InputManager inputManager, TemporalEventsManager temporalEventsManager, KinematicRenderPlaneObjectContainer kinematicRenderPlaneObjectContainer) {
		if ( ( inputManager != null) && ( temporalEventsManager != null ) && ( kinematicRenderPlaneObjectContainer != null ) ) {
			return this.create_checked(new InstanceData(this.name, inputManager, temporalEventsManager, kinematicRenderPlaneObjectContainer) );
		}
		else {
			throw new NullPointerException();
		}
	}
	
	/**
	 * @pre Los datos de instancia no son nulos
	 * @post Crea una instancia con los datos de instancia especificados
	 */
	protected abstract Instance create_checked(InstanceData instanceData);
}
