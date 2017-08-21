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
package com.esferixis.gameengine.renderengine.frontend;

import java.util.Arrays;
import java.util.Collection;

import com.esferixis.gameengine.renderengine.backend.RenderEngineBackend;
import com.esferixis.gameengine.renderengine.frontend.renderingFrame.RenderingFrame;
import com.esferixis.misc.ElementCallback;
import com.esferixis.misc.accesor.AccesorHolder;
import com.esferixis.misc.accesor.AccesorWhiteList;
import com.esferixis.misc.loader.DataLoadingErrorException;
import com.esferixis.misc.loadingmanager.LinkedMruLoadingManager;
import com.esferixis.misc.loadingmanager.LoadingManager;

/**
 * @author ariel
 *
 */
public final class RenderEngineFrontend {
	private final RenderEngineBackend renderEngineBackend;
	
	private final LoadingManager<RenderEngineFrontendLoadableObject> loadingManager;
	
	private boolean destroyed;
	
	public static final class Accesor {
		private Accesor() {}
		
		@AccesorWhiteList
		private static final Class<?>[] packageAccesors = new Class[] {
			com.esferixis.gameengine.renderengine.frontend.renderingFrame.PackageAccesors.class
		};
		
		/**
		 * @post Prepara la carga de los objetos especificados en
		 * 	 	 el frontend especificado
		 */
		public void prepareLoadableObjects(RenderEngineFrontend renderEngineFrontend, RenderEngineFrontendLoadableObject... objects) {
			if ( ( renderEngineFrontend != null ) && ( objects != null ) ) {
				renderEngineFrontend.prepareLoadableObjects(Arrays.asList(objects));
			}
			else {
				throw new NullPointerException();
			}
		}
		
		/**
		 * @post Prepara la carga de los objetos especificados en
		 * 	 	 el frontend especificado
		 */
		public void prepareLoadableObjects(RenderEngineFrontend renderEngineFrontend, Collection<RenderEngineFrontendLoadableObject> objects) {
			if ( ( renderEngineFrontend != null ) && ( objects != null ) ) {
				renderEngineFrontend.prepareLoadableObjects(objects);
			}
			else {
				throw new NullPointerException();
			}
		}
		
		/**
		 * @post Devuelve el backend
		 */
		public RenderEngineBackend getRenderEngineBackend(RenderEngineFrontend renderEngineFrontend) {
			return renderEngineFrontend.getRenderEngineBackend();
		}
	}
	
	private final RenderingFrame screenRenderingFrame;
	
	/**
	 * @post Crea el sistema de frontend con el backend y la configuración
	 * 		 especificada
	 */
	private RenderEngineFrontend(RenderEngineBackend renderEngineBackend, RenderEngineFrontendConfiguration configuration) {
		if ( ( renderEngineBackend != null ) && ( configuration != null ) ) {
			this.renderEngineBackend = renderEngineBackend;
			this.loadingManager = new LinkedMruLoadingManager<RenderEngineFrontendLoadableObject>(new RenderEngineFrontendLoadableObjectLoadingStrategy(this.renderEngineBackend), Long.MAX_VALUE, configuration.getMaxMemoryToUse());
			this.screenRenderingFrame = PackageAccesors.renderingFrameAccesor.get().create(this, this.renderEngineBackend.getScreenRenderingFrame());
			this.destroyed = false;
		}
		else {
			throw new NullPointerException();
		}
	}
	
	/**
	 * @post Ejecuta el callback con el backend del engine de renderización y la configuración especificada
	 */
	public static void execute(RenderEngineBackend renderEngineBackend, RenderEngineFrontendConfiguration configuration, ElementCallback<RenderEngineFrontend> callBack) {
		if ( ( renderEngineBackend != null ) && ( configuration != null ) && ( callBack != null ) ) {
			final RenderEngineFrontend renderEngineFrontend = new RenderEngineFrontend(renderEngineBackend, configuration);
			callBack.run(renderEngineFrontend);
			renderEngineFrontend.destroy();
		}
		else {
			throw new NullPointerException();
		}
	}
	
	/**
	 * @post Chequea que el frontend no esté destruido
	 */
	private void checkNotDestroyed() {
		if ( this.destroyed ) {
			throw new IllegalStateException("Cannot use render engine frontend and its dependencies when it has been destroyed");
		}
	}
	
	/**
	 * @post Devuelve el backend
	 */
	private RenderEngineBackend getRenderEngineBackend() {
		return this.renderEngineBackend;
	}
	
	/**
	 * @throws DataLoadingErrorException 
	 * @throws NullPointerException 
	 * @post Prepara el uso de los objetos especificados
	 */
	private void prepareLoadableObjects(Collection<RenderEngineFrontendLoadableObject> objects) {
		try {
			this.loadingManager.loadElements(objects);
		} catch (DataLoadingErrorException e) {
			throw new RuntimeException(e);
		}
	}
	
	/**
	 * @post Devuelve la ventana de renderización de pantalla
	 */
	public final RenderingFrame getScreenRenderingFrame() {
		this.checkNotDestroyed();
		return this.screenRenderingFrame;
	}
	
	/**
	 * @post Destruye el sistema de frontend
	 */
	private void destroy() {
		this.loadingManager.destroy();
		this.destroyed = true;
	}
}
