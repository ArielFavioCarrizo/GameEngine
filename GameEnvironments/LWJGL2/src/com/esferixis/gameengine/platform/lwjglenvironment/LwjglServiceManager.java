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

import org.lwjgl.opengl.Display;

import com.esferixis.gameengine.platform.PlatformServiceManager;
import com.esferixis.gameengine.platform.display.DisplayManager;
import com.esferixis.gameengine.platform.input.InputManager;

/**
 * Provee un acceso unificado a los servicios esenciales para un juego
 * electrónico tales como OpenGL, OpenAL, y entrada con
 * joysticks, mouse, y/o teclado haciendo uso de LWJGL.
 * 
 * @author Ariel Favio Carrizo
 *
 */
final class LwjglServiceManager extends PlatformServiceManager {
	private static final LwjglServiceManager instance = new LwjglServiceManager();
	
	private static final LwjglInputManager inputManager = new LwjglInputManager();
	private static final LwjglDisplayManager displayManager = new LwjglDisplayManager();
	
	private LwjglServiceManager() {}
	
	public static LwjglServiceManager getInstance() {
		return instance;
	}

	/**
	 * @post Devuelve el administrador de entradas
	 */
	@Override
	public InputManager getInputManager() {
		return inputManager;
	}
	
	/**
	 * @post Devuelve el administrador de pantalla
	 */
	@Override
	public DisplayManager getDisplayManager() {
		return displayManager;
	}

	@Override
	public void newFrame() {
		Display.sync(60);
		Display.update();
		inputManager.processEvents();
	}
	
	/**
	 * @post Devuelve si se pide el cierre del juego
	 */
	public boolean isCloseRequested() {
		return Display.isCreated() && Display.isCloseRequested();
	}
	
	@Override
	protected void destroy() {
		try {
			inputManager.destroy();
			displayManager.destroy();
		}
		finally {
			if ( Display.isCreated() ) {
				Display.destroy();
			}
		}
	}
	
}
