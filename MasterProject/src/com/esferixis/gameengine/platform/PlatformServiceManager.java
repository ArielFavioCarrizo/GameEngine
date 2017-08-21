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

package com.esferixis.gameengine.platform;

import com.esferixis.gameengine.platform.display.DisplayManager;
import com.esferixis.gameengine.platform.input.InputManager;

/**
 * Provee un acceso unificado a los servicios esenciales de plataforma para
 * un juego electrónico tales como engine de rendering, sonido, engine físico,
 * acceso a información del juego y almacenamiento, y entrada
 * tales como joysticks, mouse, y/o teclado
 * 
 * @author Ariel Favio Carrizo
 *
 */
public abstract class PlatformServiceManager {
	/**
	 * @post Devuelve el administrador de entradas
	 */
	public abstract InputManager getInputManager();
	
	/**
	 * @post Devuelve el administrador de pantalla
	 */
	public abstract DisplayManager getDisplayManager();
	
	/**
	 * @post Actualiza los servicios para un nuevo cuadro
	 */
	public abstract void newFrame();
	
	/**
	 * @post Devuelve si se pide el cierre del juego
	 */
	public abstract boolean isCloseRequested();
	
	/**
	 * @post Cierra el entorno liberando todos los recursos ocupados
	 */
	protected abstract void destroy();
}
