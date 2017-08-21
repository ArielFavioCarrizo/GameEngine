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

import org.lwjgl.LWJGLException;
import org.lwjgl.opengl.Display;

import com.esferixis.gameengine.platform.input.Controller;
import com.esferixis.gameengine.platform.input.InputManager;
import com.esferixis.gameengine.platform.input.Keyboard;
import com.esferixis.gameengine.platform.input.Mouse;

/**
 * Implementación del administrador de entradas basado en Lwjgl
 * 
 * @author Ariel Favio Carrizo
 *
 */
final class LwjglInputManager extends InputManager {
	private LwjglKeyboard keyboard;
	private LwjglMouse mouse;
	private final List<LwjglController> controllers;
	
	/**
	 * @post Crea el administrador de entrada
	 */
	public LwjglInputManager() {
		LwjglController[] controllers;
		if ( !org.lwjgl.input.Controllers.isCreated() ) {
			try {
				org.lwjgl.input.Controllers.create();
			} catch (LWJGLException e) {
				System.out.println("Warning: Cannot detect controllers because " + e);
			}
		}
		
		if ( org.lwjgl.input.Controllers.isCreated() ) {
			controllers = new LwjglController[org.lwjgl.input.Controllers.getControllerCount()];
			for ( int i = 0; i < controllers.length; i++) {
				controllers[i] = new LwjglController(org.lwjgl.input.Controllers.getController(i));
			}
			this.controllers = Collections.unmodifiableList(Arrays.asList(controllers));	
		}
		else {
			this.controllers = Collections.emptyList();
		}
	}
	
	/**
	 * @pre Tiene que estar inicializado el modo de pantalla
	 * @post Devuelve el control
	 */
	private void checkDisplayInitialization() {
		if ( LwjglServiceManager.getInstance().getDisplayManager().getScreenConfig() == null ) {
			throw new IllegalStateException("Unitialized display");
		}
	}
	
	/**
	 * @pre Tiene que estar inicializado el modo de pantalla
	 * @post Devuelve el teclado
	 */
	@Override
	public Keyboard getKeyboard() {
		this.checkDisplayInitialization();
		if ( this.keyboard == null ) {
			this.keyboard = new LwjglKeyboard();
		}
		return this.keyboard;
	}
	
	/**
	 * @pre Tiene que estar inicializado el modo de pantalla
	 * @post Devuelve el mouse
	 */
	@Override
	public Mouse getMouse() {
		this.checkDisplayInitialization();
		if ( this.mouse == null ) {
			this.mouse = new LwjglMouse();
		}
		return this.mouse;
	}
	
	/**
	 * @post Devuelve los controladores
	 */
	@Override
	public List<Controller> getControllers() {
		return (List<Controller>) (List<?>) this.controllers;
	}
	
	/**
	 * @post Procesa los eventos
	 */
	void processEvents() {
		( (LwjglKeyboard) this.getKeyboard()).processEvents();
		( (LwjglMouse) this.getMouse()).processEvents();
		for ( LwjglController eachController : this.controllers ) {
			eachController.processEvents();
		}
	}
	
	/**
	 * @post Libera los recursos ocupados
	 */
	void destroy() {
		if ( org.lwjgl.input.Controllers.isCreated() ) {
			org.lwjgl.input.Controllers.destroy();
		}
	}
}
