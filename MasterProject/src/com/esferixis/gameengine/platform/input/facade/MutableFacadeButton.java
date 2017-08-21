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

package com.esferixis.gameengine.platform.input.facade;

import com.esferixis.gameengine.platform.input.Button;
import com.esferixis.gameengine.platform.input.InputUnitObserver;

/**
 * Botón de "fachada" que representa un botón elegido que puede
 * ser cambiado a lo largo de la existencia de ésta fachada.
 * Muy util para cambiar la configuración de entrada en
 * medio del funcionamiento del juego en forma transparente
 * al cliente que lo use.
 * 
 * @author Ariel Favio Carrizo
 *
 */
public abstract class MutableFacadeButton extends Button {
	private Button backingButton;
	
	private class NotifierObserver extends InputUnitObserver<Boolean> {
		/**
		 * @param type
		 */
		public NotifierObserver() {
			super(Type.WEAK);
		}

		/* (non-Javadoc)
		 * @see com.esferixis.gameengine.platform.input.InputUnitObserver#notifyStateChange(java.lang.Object)
		 */
		@Override
		protected void notifyStateChange(Boolean newPosition) {
			MutableFacadeButton.this.notifyNewState(newPosition);
		}
	}
	private NotifierObserver notifierObserver;
	
	/**
	 * @post Crea el botón "fachada" sin especificar
	 * 		 el botón subyacente
	 */
	public MutableFacadeButton() {
		this.backingButton = null;
		this.notifierObserver = new NotifierObserver();
	}
	
	/**
	 * @post Devuelve el botón subyacente,
	 * 		 si no hay devuelve null
	 */
	public Button getBackingButton() {
		return this.backingButton;
	}
	
	/**
	 * @post Cambia el botón subyacente por el especificado,
	 * 		 si es null deja de haber botón subyacente y
	 * 		 así queda siempre en estado de reposo
	 */
	public void setBackingButton(Button newBackingButton) {
		if ( this.backingButton != null ) {
			this.notifierObserver.detach();
		}
		
		this.backingButton = newBackingButton;
		
		if ( this.backingButton != null ) {
			this.notifierObserver.attach(this.backingButton);
		}
		else {
			this.notifyNewState(false);
		}
	}
}
