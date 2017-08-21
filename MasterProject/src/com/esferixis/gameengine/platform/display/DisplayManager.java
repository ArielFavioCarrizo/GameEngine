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

package com.esferixis.gameengine.platform.display;

import java.util.Iterator;
import java.util.List;

import com.esferixis.gameengine.platform.PlatformServiceManagerException;
import com.esferixis.gameengine.renderengine.backend.RenderEngineBackend;
import com.esferixis.gameengine.renderengine.frontend.RenderEngineFrontend;
import com.esferixis.gameengine.renderengine.frontend.RenderEngineFrontendConfiguration;

/**
 * Administrador de pantalla
 * 
 * @author Ariel Favio Carrizo
 *
 */
public abstract class DisplayManager {
	private ProtectedRenderEngineBackend protectedRenderEngineBackend;
	boolean gettedRenderEngineBackend;
	
	/**
	 * @post Crea el administrador de pantalla
	 */
	public DisplayManager() {
		this.protectedRenderEngineBackend = null;
	}
	
	/**
	 * @pre El modo de configuración de pantalla tiene que estar asignado
	 * 		y no se tiene que haber obtenido el frontend del engine
	 * 		de rendering.
	 * 		El backend del engine de rendering sólo puede ser obtenido
	 * 		una sola vez desde que se creó
	 * @post Devuelve el backend del engine de rendering
	 */
	public final RenderEngineBackend getRenderEngineBackend() {
		if ( this.protectedRenderEngineBackend != null ) {
			if ( !this.gettedRenderEngineBackend ) {
				this.gettedRenderEngineBackend = true;
				return this.protectedRenderEngineBackend;
			}
			else {
				throw new IllegalStateException("Render engine backend has been getted");
			}
		}
		else {
			throw new IllegalStateException("Missing screen config");
		}
	}
	
	/**
	 * @post Especifica el backend del engine de rendering
	 */
	protected final void setRenderEngineBackend(RenderEngineBackend renderEngineBackend) {
		if ( this.protectedRenderEngineBackend != null ) {
			this.protectedRenderEngineBackend.exitContext();
		}
		
		if ( renderEngineBackend != null ) {
			this.protectedRenderEngineBackend = new ProtectedRenderEngineBackend(renderEngineBackend);
			this.gettedRenderEngineBackend = false;
		}
		else {
			this.protectedRenderEngineBackend = null;
		}
	}
	
	/**
	 * @post Devuelve si la plataforma soporta una configuración
	 * 		 de modo de display en ventana
	 */
	public abstract boolean isWindowedModeSupported();
	
	/**
	 * @pre La plataforma tiene que soportar la creación de una ventana
	 * 		
	 * @post Crea una configuración de pantalla en ventana.
	 * 
	 * 		 Si la plataforma no soporta la creación de una
	 * 		 ventana lanza UnsupportedOperationException.
	 * 
	 * 		 Si no es posible crear la configuración en ventana
	 * 		 por un error de la plataforma lanza PlatformServiceManagerException
	 * 
	 * @throws UnsupportedOperationException, PlatformServiceManagerException
	 */
	public abstract ScreenConfig createWindowedScreenConfig(int width, int height) throws UnsupportedOperationException, PlatformServiceManagerException;
	
	/**
	 * @post Devuelve los modos de pantalla completa
	 * @throws PlatformServiceManagerException
	 */
	public abstract List<ScreenConfig> getFullScreenScreenConfigs() throws PlatformServiceManagerException;
	
	/**
	 * @pre Si el modo de pantalla no fue creado por éste
	 * 		administrador tiene que poder encontrarse un modo
	 * 		de pantalla igual
	 * @post Especifica el modo de pantalla, si es nulo destruye el motor de renderización
	 * 		 y desasigna el modo de pantalla
	 * @throws NullPointerException, ServiceManagerException
	 */
	public void setScreenConfig(ScreenConfig screenConfig) throws PlatformServiceManagerException {
		ScreenConfig internalScreenConfig;
		
		if ( ( screenConfig != null ) && ( screenConfig != this.getScreenConfig() ) ) {
			// En caso de ser el creador
			if ( screenConfig.getDisplayManager() == this ) {
				// Elegir el especificado
				internalScreenConfig = screenConfig;
			}
			else { // Caso contrario
				// Si está en modo de pantalla completa
				if ( screenConfig.isFullScreen() ) {
					// Busca un modo de pantalla igual interno
					internalScreenConfig = null;
					Iterator<ScreenConfig> fullScreenInternalConfigs = this.getFullScreenScreenConfigs().iterator();
					while ( fullScreenInternalConfigs.hasNext() && ( internalScreenConfig == null ) ) {
						final ScreenConfig eachScreenConfig = fullScreenInternalConfigs.next();
						if ( eachScreenConfig.getWidth() == screenConfig.getWidth() ) {
							if ( eachScreenConfig.getHeight() == screenConfig.getHeight() ) {
								if ( eachScreenConfig.getBitsPerPixel() == screenConfig.getBitsPerPixel() ) {
									internalScreenConfig = eachScreenConfig;
								}
							}
						}
					}
					
					// Si no lo encontró
					if ( internalScreenConfig == null ) {
						// Lanzar la excepción correspondiente
						throw new PlatformServiceManagerException("Unsupported screen configuration");
					}
				}
				else { // Caso contrario
					// Crea una configuración de ventana con las dimensiones de la configuración especificada
					internalScreenConfig = this.createWindowedScreenConfig(screenConfig.getWidth(), screenConfig.getHeight());
				}
			}
		}
		else {
			internalScreenConfig = null;
		}
		
		this.setInternalScreenConfig(internalScreenConfig);
	}
	
	/**
	 * @pre El modo de pantalla tiene que estar creado por éste administrador
	 * 		de servicio
	 * @post Especifica el modo de pantalla interno
	 * @throws NullPointerException, ServiceManagerException
	 */
	protected abstract void setInternalScreenConfig(ScreenConfig screenMode) throws PlatformServiceManagerException;
	
	/**
	 * @post Devuelve el modo de pantalla
	 */
	public abstract ScreenConfig getScreenConfig();
	
	/**
	 * @post Devuelve el modo de pantalla original
	 */
	public abstract ScreenConfig getOriginalScreenConfig();
	
	/**
	 * @post Devuelve el título de la ventana (Puede ser ignorado por el sistema operativo)
	 */
	public abstract String getWindowTitle();
	
	/**
	 * @post Especifica el título de la ventana (Puede ser ignorado por el sistema operativo)
	 */
	public abstract void setWindowTitle(String title);
}
