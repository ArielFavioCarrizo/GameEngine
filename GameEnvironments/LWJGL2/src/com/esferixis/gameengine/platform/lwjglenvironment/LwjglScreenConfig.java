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

import org.lwjgl.opengl.DisplayMode;

import com.esferixis.gameengine.platform.display.ScreenConfig;

/**
 * Implementación de configuración de pantalla basada en Lwjgl
 * 
 * @author Ariel Favio Carrizo
 *
 */
final class LwjglScreenConfig extends ScreenConfig {
	private DisplayMode displayMode;
	private boolean fullScreen;
	
	protected LwjglScreenConfig(DisplayMode displayMode, boolean fullScreen) {
		super(LwjglServiceManager.getInstance().getDisplayManager());
		this.displayMode = displayMode;
		this.fullScreen = fullScreen;
	}

	@Override
	public int getWidth() {
		return this.displayMode.getWidth();
	}

	@Override
	public int getHeight() {
		return this.displayMode.getHeight();
	}

	@Override
	public int getBitsPerPixel() {
		return this.displayMode.getBitsPerPixel();
	}

	@Override
	public boolean isFullScreen() {
		return this.fullScreen;
	}

	/**
	 * @post Devuelve el modo de pantalla
	 */
	protected DisplayMode getDisplayMode() {
		return this.displayMode;
	}
}
