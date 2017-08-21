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

import java.util.ArrayList;
import java.util.List;

import com.esferixis.gameengine.platform.PlatformServiceManagerException;
import com.esferixis.gameengine.renderengine.backend.RenderEngineBackend;
import com.esferixis.gameengine.renderengine.backend.exception.OutOfMemoryException;
import com.esferixis.gameengine.renderengine.frontend.RenderEngineFrontendLoadableObject;
import com.esferixis.misc.loader.DataLoadingErrorException;
import com.esferixis.misc.loadingmanager.LoadingStrategy;
import com.esferixis.misc.loadingmanager.UserCount;

/**
 * @author ariel
 *
 */
public abstract class RenderEngineBackendProfile<C extends Core<C, P>, P extends RenderEngineBackendProfile<C, P>> {
	protected final RenderEngineBackend renderEngineBackend;
	
	protected final C core;
	
	private final UserCount userCount;
	private final LoadingStrategy.Observer localLoadingStrategyObserver;
	
	/**
	 * @post Crea el perfil con el núcleo y el backend de renderización especificados
	 * @param renderEngineBackend
	 */
	protected RenderEngineBackendProfile(com.esferixis.gameengine.renderengine.backend.RenderEngineBackend renderEngineBackend, C core) {
		if ( ( core != null ) && ( renderEngineBackend != null ) ) {
			this.renderEngineBackend = renderEngineBackend;
			this.core = core;
			
			this.userCount = new UserCount();
	
			this.localLoadingStrategyObserver = new LoadingStrategy.Observer() {

				@Override
				public void notifyHasLoadedUsers() {
					RenderEngineBackendProfile.this.core.registerProfile((P) RenderEngineBackendProfile.this);
				}

				@Override
				public void notifyHasNotLoadedUsers() {
					RenderEngineBackendProfile.this.core.unregisterProfile((P) RenderEngineBackendProfile.this);
				}
				
			};
			
			this.userCount.addObserver(this.localLoadingStrategyObserver);
		}
		else {
			throw new NullPointerException();
		}
	}
	
	/**
	 * @post Devuelve la cuenta de usuarios
	 */
	public final UserCount getUserCount() {
		return this.userCount;
	}
	
	/**
	 * @post Carga el objeto, considerando las dependencias
	 */
	public final void load() throws PlatformServiceManagerException, DataLoadingErrorException, OutOfMemoryException, NullPointerException {
		final List<RenderEngineFrontendLoadableObject> incrementedUserCountDependencies = new ArrayList<RenderEngineFrontendLoadableObject>(this.core.getDependencies());
		
		try {
			for ( RenderEngineFrontendLoadableObject eachDependency : this.core.getDependencies() ) {
				PackageAccesors.renderEngineFrontendObjectAccesor.get().getCore(eachDependency).getProfile(this.renderEngineBackend).getUserCount().incrementUserCount();
				incrementedUserCountDependencies.add(eachDependency);
			}
		} catch ( RuntimeException e ) {
			for ( RenderEngineFrontendLoadableObject eachDependency : incrementedUserCountDependencies ) {
				PackageAccesors.renderEngineFrontendObjectAccesor.get().getCore(eachDependency).getProfile(this.renderEngineBackend).getUserCount().decrementUserCount();
			}
			throw e;
		}
		
		this.load_internal();
	}
	
	/**
	 * @post Descarga el objeto, considerando las dependencias
	 */
	public final void unload() {
		final List<RenderEngineFrontendLoadableObject> decrementedUserCountDependencies = new ArrayList<RenderEngineFrontendLoadableObject>(this.core.getDependencies());
		
		for ( RenderEngineFrontendLoadableObject eachDependency : this.core.getDependencies() ) {
			PackageAccesors.renderEngineFrontendObjectAccesor.get().getCore(eachDependency).getProfile(this.renderEngineBackend).getUserCount().decrementUserCount();
		}
		
		this.unload_internal();
	}
	
	/**
	 * @post Carga el objeto
	 */
	protected abstract void load_internal() throws PlatformServiceManagerException, DataLoadingErrorException, OutOfMemoryException, NullPointerException;
	
	/**
	 * @post Descarga la textura
	 */
	protected abstract void unload_internal();
	
	/**
	 * @post Devuelve si está cargado
	 */
	public abstract boolean isLoaded();
}
