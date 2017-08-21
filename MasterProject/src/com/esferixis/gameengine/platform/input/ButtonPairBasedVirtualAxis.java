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

package com.esferixis.gameengine.platform.input;

import com.esferixis.misc.observer.Observer;

/**
 * Eje virtual que va de -1 a 1 implementado con un
 * par de botones
 * 
 * @author Ariel Favio Carrizo
 *
 */
public final class ButtonPairBasedVirtualAxis extends Axis {	
	private final InputUnitObserver<Boolean> downButtonObserver, upButtonObserver;
	private boolean downButtonPressed, upButtonPressed;
	
	/**
	 * @post Crea el eje virtual con los botones especificados
	 */
	public ButtonPairBasedVirtualAxis(Button downButton, Button upButton) {
		this.downButtonObserver = new InputUnitObserver<Boolean>(Observer.Type.WEAK) {
			@Override
			public void notifyStateChange(Boolean isPressed) {
				ButtonPairBasedVirtualAxis.this.downButtonPressed = isPressed;
				ButtonPairBasedVirtualAxis.this.updateState();
			}
		};
		this.downButtonObserver.attach(downButton);
		
		this.upButtonObserver = new InputUnitObserver<Boolean>(Observer.Type.WEAK) {
			@Override
			public void notifyStateChange(Boolean isPressed) {
				ButtonPairBasedVirtualAxis.this.upButtonPressed = isPressed;
				ButtonPairBasedVirtualAxis.this.updateState();
			}
		};
		
		this.upButtonObserver.attach(upButton);
	}
	
	/**
	 * @post Actualiza el estado
	 */
	private void updateState() {
		float state;
		if ( this.downButtonPressed ^ this.upButtonPressed ) {
			if ( this.downButtonPressed ) {
				state = -1.0f;
			}
			else {
				state = 1.0f;
			}
		}
		else {
			state = 0.0f;
		}
		this.notifyNewState(state);
	}
	
	/**
	 * @throws Throwable 
	 * @post Libera los recursos ocupados por el eje,
	 * 		 es necesario invocar éste método para
	 * 		 que quite sus observadores de estado en los botones
	 */
	@Override
	protected void finalize() throws Throwable {
		try {
			this.downButtonObserver.detach();
			this.upButtonObserver.detach();
		} finally {
			super.finalize();
		}
	}
}
