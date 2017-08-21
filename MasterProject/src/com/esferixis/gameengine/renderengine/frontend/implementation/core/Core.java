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
package com.esferixis.gameengine.renderengine.frontend.implementation.core;

import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

import com.esferixis.gameengine.renderengine.frontend.RenderEngineFrontendLoadableObject;

/**
 * @author ariel
 *
 */
public abstract class Core<C extends Core<C, P>, P extends RenderEngineBackendProfile<C, P>> {
	private final Map<com.esferixis.gameengine.renderengine.backend.RenderEngineBackend, P> renderEngineBackendProfileByEngine;
	private Collection<RenderEngineFrontendLoadableObject> dependencies;
	
	/**
	 * @post Crea el núcleo
	 */
	protected Core() {
		this.renderEngineBackendProfileByEngine = new HashMap<com.esferixis.gameengine.renderengine.backend.RenderEngineBackend, P>(1);
		this.dependencies = null;
	}
	
	/**
	 * @post Devuelve la colección de dependencias
	 */
	protected abstract Collection<RenderEngineFrontendLoadableObject> createDependienciesCollection();
	
	/**
	 * @post Devuelve las dependencias
	 */
	public final Collection<RenderEngineFrontendLoadableObject> getDependencies() {
		if ( this.dependencies == null ) {
			this.dependencies = Collections.unmodifiableCollection( this.createDependienciesCollection() );
		}
		
		return this.dependencies;
	}
	
	/**
	 * @post Registra el perfil
	 */
	void registerProfile(P profile) {
		this.renderEngineBackendProfileByEngine.put(profile.renderEngineBackend, profile);
	}
	
	/**
	 * @post Desregistra el perfil
	 */
	void unregisterProfile(P profile) {
		this.renderEngineBackendProfileByEngine.remove(profile.renderEngineBackend);
	}
	
	/**
	 * @post Crea el perfil con el motor de renderización especificado
	 */
	protected abstract P createProfile(com.esferixis.gameengine.renderengine.backend.RenderEngineBackend renderEngineBackend);
	
	/**
	 * @post Devuelve el perfil asociado al backend del engine de renderización especificado
	 */
	public final P getProfile(com.esferixis.gameengine.renderengine.backend.RenderEngineBackend renderEngineBackend) {
		P profile = this.renderEngineBackendProfileByEngine.get(renderEngineBackend);
		
		if ( profile == null ) {
			profile = this.createProfile(renderEngineBackend);
		}
		
		return profile;
	}
}
