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
package com.esferixis.gameengine.renderengine.frontend.implementation.texture;

import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

import com.esferixis.gameengine.renderengine.backend.RenderEngineBackend;
import com.esferixis.gameengine.renderengine.frontend.RenderEngineFrontendLoadableObject;
import com.esferixis.gameengine.renderengine.frontend.implementation.core.Core;
import com.esferixis.gameengine.renderengine.frontend.texture.Texture;
import com.esferixis.gameengine.renderengine.picture.RasterPicture;
import com.esferixis.math.Vectorf;
import com.esferixis.misc.accesor.AccesorHolder;
import com.esferixis.misc.accesor.AccesorWhiteList;
import com.esferixis.misc.loader.AbstractDataLoader;
import com.esferixis.misc.loadingmanager.LoadingStrategy;

/**
 * @author ariel
 *
 */
public final class TextureCore<P extends RasterPicture<? extends Vectorf>> extends Core<TextureCore<P>, TextureRenderEngineBackendProfile<P>> {
	private final com.esferixis.gameengine.renderengine.backend.texture.Texture<P> backend;
	
	private final Map<com.esferixis.gameengine.renderengine.backend.RenderEngineBackend, TextureRenderEngineBackendProfile<P>> renderEngineBackendProfileByEngine;
	
	// Clase de acceso de implementación
	public static final class Accesor {
		private Accesor() {}
		
		@AccesorWhiteList
		private static final Class<?>[] packageAccesors = new Class[] {
			com.esferixis.gameengine.renderengine.frontend.texture.PackageAccesors.class
		};
		
		/**
		 * @post Crea un núcleo de textura del cargador de textura especificado
		 */
		public <P extends RasterPicture<? extends Vectorf>> TextureCore<P> create(Texture<P> texture) {
			return new TextureCore<P>(texture);
		}
	};
	
	/**
	 * @post Crea el núcleo de la textura especificada
	 */
	private TextureCore(Texture<P> texture) {
		if ( texture != null ) {
			this.backend = new com.esferixis.gameengine.renderengine.backend.texture.Texture<P>(texture.getLoader());
			
			this.renderEngineBackendProfileByEngine = new HashMap<com.esferixis.gameengine.renderengine.backend.RenderEngineBackend, TextureRenderEngineBackendProfile<P>>(1);
		}
		else {
			throw new NullPointerException();
		}
	}
	
	/**
	 * @post Devuelve el backend
	 */
	public com.esferixis.gameengine.renderengine.backend.texture.Texture<P> getBackend() {
		return this.backend;
	}

	/* (non-Javadoc)
	 * @see com.esferixis.gameengine.renderengine.frontend.implementation.core.Core#createProfile(com.esferixis.gameengine.renderengine.backend.RenderEngineBackend)
	 */
	@Override
	protected final TextureRenderEngineBackendProfile<P> createProfile(RenderEngineBackend renderEngineBackend) {
		return new TextureRenderEngineBackendProfile<P>(renderEngineBackend, this);
	}

	/* (non-Javadoc)
	 * @see com.esferixis.gameengine.renderengine.frontend.implementation.core.Core#createDependienciesCollection()
	 */
	@Override
	protected Collection<RenderEngineFrontendLoadableObject> createDependienciesCollection() {
		return Collections.emptyList();
	}
}
