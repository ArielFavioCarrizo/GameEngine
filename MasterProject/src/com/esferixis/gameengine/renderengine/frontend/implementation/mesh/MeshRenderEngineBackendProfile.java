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
package com.esferixis.gameengine.renderengine.frontend.implementation.mesh;

import com.esferixis.gameengine.platform.PlatformServiceManagerException;
import com.esferixis.gameengine.renderengine.backend.RenderEngineBackend;
import com.esferixis.gameengine.renderengine.backend.exception.OutOfMemoryException;
import com.esferixis.gameengine.renderengine.frontend.implementation.core.RenderEngineBackendProfile;
import com.esferixis.gameengine.renderengine.frontend.misc.mesh.Mesh;
import com.esferixis.math.Vectorf;
import com.esferixis.misc.loader.DataLoadingErrorException;
import com.esferixis.misc.loadingmanager.LoadingStrategy;
import com.esferixis.misc.loadingmanager.UserCount;

/**
 * @author ariel
 *
 */
public final class MeshRenderEngineBackendProfile<V extends Vectorf, D extends Mesh.Data<V, ?>> extends RenderEngineBackendProfile<MeshCore<V, D>, MeshRenderEngineBackendProfile<V, D>> {	
	/**
	 * @param renderEngineBackend
	 * @param core
	 */
	protected MeshRenderEngineBackendProfile(RenderEngineBackend renderEngineBackend, MeshCore<V, D> core) {
		super(renderEngineBackend, core);
	}

	/* (non-Javadoc)
	 * @see com.esferixis.gameengine.renderengine.frontend.implementation.core.RenderEngineBackendProfile#load()
	 */
	@Override
	public void load_internal() throws PlatformServiceManagerException, DataLoadingErrorException, OutOfMemoryException,
			NullPointerException {
		this.renderEngineBackend.load( (com.esferixis.gameengine.renderengine.backend.misc.mesh.Mesh) this.core.getBackend());
	}

	/* (non-Javadoc)
	 * @see com.esferixis.gameengine.renderengine.frontend.implementation.core.RenderEngineBackendProfile#unload()
	 */
	@Override
	public void unload_internal() {
		this.renderEngineBackend.unload( (com.esferixis.gameengine.renderengine.backend.misc.mesh.Mesh) this.core.getBackend());
	}

	/* (non-Javadoc)
	 * @see com.esferixis.gameengine.renderengine.frontend.implementation.core.RenderEngineBackendProfile#isLoaded()
	 */
	@Override
	public boolean isLoaded() {
		return this.renderEngineBackend.isLoaded( (com.esferixis.gameengine.renderengine.backend.misc.mesh.Mesh) this.core.getBackend());
	}
}
