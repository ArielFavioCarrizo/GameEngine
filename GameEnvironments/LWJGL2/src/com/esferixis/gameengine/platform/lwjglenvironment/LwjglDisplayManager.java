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

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import org.lwjgl.LWJGLException;
import org.lwjgl.opengl.ContextAttribs;
import org.lwjgl.opengl.Display;
import org.lwjgl.opengl.DisplayMode;
import org.lwjgl.opengl.PixelFormat;

import com.arielcarrizo.gameengine.renderengine.backend.opengl.GlRenderEngineBackendSystem;
import com.esferixis.gameengine.platform.PlatformServiceManagerException;
import com.esferixis.gameengine.platform.display.DisplayManager;
import com.esferixis.gameengine.platform.display.ScreenConfig;
import com.esferixis.gameengine.renderengine.backend.RenderEngineBackend;

final class LwjglDisplayManager extends DisplayManager {
	private GlRenderEngineBackendSystem renderEngineSystem = null;
	
	/**
	 * @post Crea una configuración de pantalla en ventana
	 */
	@Override
	public ScreenConfig createWindowedScreenConfig(int width, int height) {
		return new LwjglScreenConfig(new DisplayMode(width, height), false);
	}

	/**
	 * @post Devuelve los modos de pantalla completa
	 * @throws ServiceManagerException
	 */
	@Override
	public List<ScreenConfig> getFullScreenScreenConfigs() throws PlatformServiceManagerException {
		DisplayMode[] availableDisplayModes;
		try {
			availableDisplayModes = Display.getAvailableDisplayModes();
		} catch (LWJGLException e) {
			throw new PlatformServiceManagerException(e);
		}
		
		List<ScreenConfig> screenConfigs = new ArrayList<ScreenConfig>(availableDisplayModes.length);
		for ( DisplayMode eachDisplayMode : availableDisplayModes ) {
			screenConfigs.add( new LwjglScreenConfig(eachDisplayMode, true) );
		}
		return Collections.unmodifiableList(screenConfigs);
	}

	/**
	 * @pre El modo de pantalla tiene que estar creado por éste administrador
	 * 		de servicio
	 * @post Especifica el modo de pantalla interno
	 * @throws NullPointerException, ServiceManagerException
	 */
	@Override
	protected void setInternalScreenConfig(ScreenConfig screenConfig) throws PlatformServiceManagerException {
		try {
			if ( screenConfig != null ) {
				LwjglScreenConfig lwjglScreenConfig = (LwjglScreenConfig) screenConfig;
				Display.setDisplayMode(lwjglScreenConfig.getDisplayMode());
				Display.setFullscreen(lwjglScreenConfig.isFullScreen());
				
				if ( !Display.isCreated() ) {
					ContextAttribs contextAttributes = new ContextAttribs(2, 1);
					contextAttributes.withForwardCompatible(true);
					Display.create(new PixelFormat(), contextAttributes);
					this.renderEngineSystem = new com.arielcarrizo.gameengine.renderengine.backend.opengl.gl21.Gl21RenderEngineBackendSystem(new LwjglGL21(), this);
					
					this.setRenderEngineBackend(this.renderEngineSystem.getRenderEngineBackend());
				}
			}
			else {
				if ( this.renderEngineSystem != null ) {
					this.setRenderEngineBackend(null);
					this.renderEngineSystem.destroy();
					this.renderEngineSystem = null;
					Display.destroy();
				}
			}
		} catch (LWJGLException e) {
			throw new PlatformServiceManagerException(e);
		}
	}

	/**
	 * @post Devuelve el modo de pantalla
	 */
	@Override
	public ScreenConfig getScreenConfig() {
		if ( Display.isCreated() ) {
			return new LwjglScreenConfig(Display.getDisplayMode(), Display.isFullscreen());
		}
		else {
			return null;
		}
	}

	/**
	 * @post Devuelve el modo de pantalla original
	 */
	@Override
	public ScreenConfig getOriginalScreenConfig() {
		return new LwjglScreenConfig(Display.getDesktopDisplayMode(), true);
	}
	
	/**
	 * @post Devuelve el título de la ventana (Puede ser ignorado por el sistema operativo)
	 */
	@Override
	public String getWindowTitle() {
		return Display.getTitle();
	}
	
	/**
	 * @post Especifica el título de la ventana (Puede ser ignorado por el sistema operativo)
	 */
	@Override
	public void setWindowTitle(String title) {
		Display.setTitle(title);
	}

	/**
	 * @post Devuelve el sistema de rendering
	 */
	GlRenderEngineBackendSystem getRenderEngineSystem() {
		return this.renderEngineSystem;
	}
	
	/* (non-Javadoc)
	 * @see com.esferixis.gameengine.platform.display.DisplayManager#isWindowedModeSupported()
	 */
	@Override
	public boolean isWindowedModeSupported() {
		return true;
	}
	
	public void destroy() {
		if ( this.renderEngineSystem != null ) {
			this.renderEngineSystem.destroy();
		}
	}
}
