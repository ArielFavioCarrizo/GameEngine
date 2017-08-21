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

import com.esferixis.gameengine.platform.display.ScreenConfig;
import com.esferixis.gameengine.platform.input.Axis;
import com.esferixis.gameengine.platform.input.Button;
import com.esferixis.gameengine.platform.input.Mouse;
import com.esferixis.gameengine.platform.input.SetteableAxis;

/**
 * Acceso a mouse implementado con Lwjgl
 * 
 * @author Ariel Favio Carrizo
 *
 */
final class LwjglMouse extends Mouse {
	private static class InternalAxis extends SetteableAxis {
		private Float pendingSettedState;
		
		/**
		 * @post Crea el eje
		 */
		public InternalAxis() {
			this.pendingSettedState = null;
		}
		
		@Override
		protected void notifyNewState(Float state) {
			super.notifyNewState(state);
		}
		
		/**
		 * @post Especifica la posición
		 */
		public void setPosition(float position) {
			this.pendingSettedState = new Float(position);
		}
		
		/**
		 * @post Devuelve la asignación pendiente
		 */
		private Float extractPendingSettedState() {
			final Float value = this.pendingSettedState;
			this.pendingSettedState = null;
			return value;
		}
	}
	
	private final InternalAxis xAxis, yAxis;
	
	private class InternalButton extends Button {
		private int index;
		
		/**
		 * @post Crea el objeto representante con el número especificado
		 */
		public InternalButton(int index) {
			this.index = index;
		}
		
		@Override
		protected void notifyNewState(Boolean state) {
			super.notifyNewState(state);
		}
		
		/**
		 * @post Devuelve si es igual al objeto especificado
		 */
		@Override
		public boolean equals(Object other) {
			if ( other != null ) {
				if ( other instanceof InternalButton ) {
					return ( ( (InternalButton) other ) .index == this.index );
				}
				else {
					return false;
				}
			}
			else {
				return false;
			}
		}
	}
	
	private List<InternalButton> buttons;
	
	/**
	 * @post Crea el mouse
	 */
	public LwjglMouse() {
		this.xAxis = new InternalAxis();
		this.yAxis = new InternalAxis();
		
		InternalButton[] buttons = new InternalButton[org.lwjgl.input.Mouse.getButtonCount()];
		for ( int i=0; i < buttons.length; i++ ) {
			buttons[i] = new InternalButton(i);
		}
		this.buttons = Collections.unmodifiableList(Arrays.asList(buttons));
	}
	
	/**
	 * @post Devuelve el eje X
	 */
	@Override
	public SetteableAxis getXAxis() {
		return this.xAxis;
	}
	
	/**
	 * @post Devuelve el eje Y
	 */
	@Override
	public SetteableAxis getYAxis() {
		return this.yAxis;
	}
	
	/**
	 * @post Devuelve la lista de botones
	 */
	public List<Button> getButtons() {
		return (List<Button>) (List<?>) Collections.unmodifiableList( this.buttons );
	}
	
	/**
	 * @post Procesa los eventos
	 */
	void processEvents() {
		final ScreenConfig screenConfig = LwjglServiceManager.getInstance().getDisplayManager().getScreenConfig();
		
		while (org.lwjgl.input.Mouse.next()) {			
			this.xAxis.notifyNewState( org.lwjgl.input.Mouse.getEventX() / screenConfig.getWidth() * 2.0f - 1.0f );
			this.yAxis.notifyNewState( org.lwjgl.input.Mouse.getEventY() / screenConfig.getHeight() * 2.0f - 1.0f );
			
			final int buttonIndex = org.lwjgl.input.Mouse.getEventButton();
			if ( buttonIndex != -1 ) {
				this.buttons.get(buttonIndex).notifyNewState(org.lwjgl.input.Mouse.getEventButtonState());
			}
		}
		
		final Float settedXvalue = this.xAxis.extractPendingSettedState();
		final Float settedYvalue = this.yAxis.extractPendingSettedState();
		if ( ( settedXvalue != null ) || ( settedYvalue != null ) ) {
			int absoluteXvalueToSet, absoluteYvalueToSet;
			if ( settedXvalue != null ) {
				absoluteXvalueToSet = (int) ((settedXvalue + 1.0f) / 2.0f * screenConfig.getWidth());
			}
			else {
				absoluteXvalueToSet = org.lwjgl.input.Mouse.getX();
			}
			
			if ( settedYvalue != null ) {
				absoluteYvalueToSet = (int) ((settedYvalue + 1.0f) / 2.0f * screenConfig.getHeight());
			}
			else {
				absoluteYvalueToSet = org.lwjgl.input.Mouse.getY();
			}
			
			
			org.lwjgl.input.Mouse.setCursorPosition( absoluteXvalueToSet, absoluteYvalueToSet);
		}
	}
}
