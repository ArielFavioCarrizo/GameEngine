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

package com.esferixis.gameengine.platform.lwjglenvironment;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import com.esferixis.gameengine.platform.input.Axis;
import com.esferixis.gameengine.platform.input.Button;
import com.esferixis.gameengine.platform.input.Controller;

/**
 * Acceso a joystick implementado con Lwjgl
 * 
 * @author Ariel Favio Carrizo
 *
 */
final class LwjglController extends Controller {
	private final org.lwjgl.input.Controller targetController;
	
	private final class InternalAxis extends Axis {
		private final int index; // Índice representante
		
		/**
		 * @post Crea el representador del eje con el índice especificado
		 */
		public InternalAxis(int index) {
			this.index = index;
		}
		
		/**
		 * @post Notifica el nuevo estado
		 */
		@Override
		protected void notifyNewState(Float position) {
			super.notifyNewState(position);
		}
	}
	
	private final class InternalButton extends Button {
		private final int index; // Índice representante
		
		/**
		 * @post Crea el representador del botón con el índice especificado
		 */
		public InternalButton(int index) {
			this.index = index;
		}
		
		/**
		 * @post Notifica el nuevo estado
		 */
		@Override
		protected void notifyNewState(Boolean position) {
			super.notifyNewState(position);
		}
	}
	
	private List<InternalAxis> axles;
	private List<InternalButton> buttons;
	
	/**
	 * @post Crea el representador del controlador especificado
	 */
	public LwjglController(org.lwjgl.input.Controller targetController) {
		this.targetController = targetController;
		InternalAxis[] axisArray = new InternalAxis[this.targetController.getAxisCount()];
		for ( int i = 0 ; i < axisArray.length ; i++ ) {
			axisArray[i] = new InternalAxis(i);
		}
		this.axles = Collections.unmodifiableList(Arrays.asList(axisArray));
		
		InternalButton[] buttonArray = new InternalButton[this.targetController.getAxisCount()];
		for ( int i = 0 ; i < axisArray.length ; i++ ) {
			buttonArray[i] = new InternalButton(i);
		}
		this.buttons = Collections.unmodifiableList(Arrays.asList(buttonArray));
	}
	
	/**
	 * @post Devuelve la lista de ejes
	 */
	@Override
	public List<Axis> getAxles() {
		return (List<Axis>) (List<?>) this.axles;
	}
	
	/**
	 * @post Devuelve la lista de botones
	 */
	@Override
	public List<Button> getButtons() {
		return (List<Button>) (List<?>) this.buttons;
	}
	
	/**
	 * @post Devuelve la lista de vibradores
	 */
	@Override
	public List<Controller.Rumbler> getRumblers() {
		// Implementación temporal, ya que ésta versión
		// de la librería no soporta vibradores
		return Collections.emptyList();
	}
	
	/**
	 * @post Procesa los eventos
	 */
	void processEvents() {
		this.targetController.poll();
		for ( InternalAxis eachAxis : this.axles ) {
			final float oldValue = eachAxis.getState();
			final float newValue = this.targetController.getAxisValue(eachAxis.index);
			if ( newValue != oldValue ) {
				eachAxis.notifyNewState(newValue);
			}
		}
		for ( InternalButton eachButton : this.buttons ) {
			final boolean oldValue = eachButton.getState();
			final boolean newValue = this.targetController.isButtonPressed(eachButton.index);
			if ( newValue != oldValue ) {
				eachButton.notifyNewState(newValue);
			}
		}
	}
}
