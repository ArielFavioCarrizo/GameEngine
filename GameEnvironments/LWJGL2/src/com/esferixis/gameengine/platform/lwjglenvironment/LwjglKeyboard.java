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

import java.util.Collections;
import java.util.EnumMap;
import java.util.Map;

import com.esferixis.gameengine.platform.input.Button;
import com.esferixis.gameengine.platform.input.Keyboard;

/**
 * Acceso a teclado implementado con Lwjgl
 * 
 * @author Ariel Favio Carrizo
 *
 */
final class LwjglKeyboard extends Keyboard {
	private Map<Key, KeyButton> keyToButtonMapping;
	private KeyButton[] keyButtons;
	
	protected static final class KeyButton extends Button {
		@Override
		protected void notifyNewState(Boolean isPressed) {
			super.notifyNewState(isPressed);
		}
	}
	
	/**
	 * @post Crea la representación de teclado
	 */
	public LwjglKeyboard() {
		this.keyToButtonMapping = new EnumMap<Key, KeyButton>(Key.class);
		this.keyButtons = new KeyButton[org.lwjgl.input.Keyboard.KEYBOARD_SIZE];
		
		// Crea los botones y el mapeo
		for ( Key eachKey : Key.values() ) {
			final KeyButton eachKeyButton = new KeyButton();
			this.keyToButtonMapping.put(eachKey, eachKeyButton);
			try {
				this.keyButtons[org.lwjgl.input.Keyboard.class.getDeclaredField(eachKey.name()).getInt(null)] = eachKeyButton;
			} catch (RuntimeException e) {
				throw e;
			} catch (Exception e) {
				throw new RuntimeException(e);
			}
		}

	}

	/**
	 * @post Devuelve el mapeo de teclas
	 */
	@Override
	public Map<Key, Button> getMapping() {
		return (Map<Key, Button>) (Map<?, ?>) Collections.unmodifiableMap(this.keyToButtonMapping);
	}
	
	/**
	 * @post Procesa los eventos
	 */
	void processEvents() {
		while (org.lwjgl.input.Keyboard.next()) {
			this.keyButtons[org.lwjgl.input.Keyboard.getEventKey()].notifyNewState(org.lwjgl.input.Keyboard.getEventKeyState());
		}
	}
}
