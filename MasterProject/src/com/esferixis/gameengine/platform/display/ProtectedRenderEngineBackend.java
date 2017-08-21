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

import java.io.IOException;

import com.esferixis.gameengine.platform.PlatformServiceManagerException;
import com.esferixis.gameengine.renderengine.backend.RenderEngineBackend;
import com.esferixis.gameengine.renderengine.backend.exception.LoadedMeshLoadAttemptException;
import com.esferixis.gameengine.renderengine.backend.exception.LoadedTextureLoadAttemptException;
import com.esferixis.gameengine.renderengine.backend.exception.LoadedTextureMappingConfigLoadAttempt;
import com.esferixis.gameengine.renderengine.backend.exception.MeshDependencyException;
import com.esferixis.gameengine.renderengine.backend.exception.MeshLayersConfigDependencyException;
import com.esferixis.gameengine.renderengine.backend.exception.MissingMeshLoadException;
import com.esferixis.gameengine.renderengine.backend.exception.MissingTextureLoadException;
import com.esferixis.gameengine.renderengine.backend.exception.MissingTextureMappingConfigLoadException;
import com.esferixis.gameengine.renderengine.backend.exception.OutOfMemoryException;
import com.esferixis.gameengine.renderengine.backend.meshLayers.MeshLayersConfig;
import com.esferixis.gameengine.renderengine.backend.misc.mesh.Mesh;
import com.esferixis.gameengine.renderengine.backend.misc.mesh.MeshTriangleVertex;
import com.esferixis.gameengine.renderengine.backend.misc.mesh.Mesh.Data;
import com.esferixis.gameengine.renderengine.backend.renderingFrame.RenderingFrame;
import com.esferixis.gameengine.renderengine.backend.renderingFrame.RenderingFrameEmmiter;
import com.esferixis.gameengine.renderengine.backend.texture.Texture;
import com.esferixis.gameengine.renderengine.picture.RasterPicture;
import com.esferixis.math.Vectorf;
import com.esferixis.misc.loader.DataLoadingErrorException;

/**
 * @author ariel
 *
 */
final class ProtectedRenderEngineBackend extends RenderEngineBackend {
	private final RenderEngineBackend target;
	private boolean onContext;

	private final class ProtectedRenderingFrame extends RenderingFrame {
		private final RenderingFrame target;
		
		/**
		 * @pre El cuadro de renderización no puede ser nulo
		 * @post Crea el cuadro de renderización protegido con el cuadro de renderización
		 * 		 especificado
		 */
		public ProtectedRenderingFrame(RenderingFrame target) {
			if ( target != null ) {
				this.target = target;
			}
			else {
				throw new NullPointerException();
			}
		}

		/* (non-Javadoc)
		 * @see com.esferixis.gameengine.renderengine.backend.renderingFrame.RenderingFrame#getWidth()
		 */
		@Override
		public int getWidth() {
			ProtectedRenderEngineBackend.this.checkContext();
			return this.target.getWidth();
		}

		/* (non-Javadoc)
		 * @see com.esferixis.gameengine.renderengine.backend.renderingFrame.RenderingFrame#getHeight()
		 */
		@Override
		public int getHeight() {
			ProtectedRenderEngineBackend.this.checkContext();
			return this.target.getHeight();
		}

		/* (non-Javadoc)
		 * @see com.esferixis.gameengine.renderengine.backend.renderingFrame.RenderingFrame#render(com.esferixis.gameengine.renderengine.backend.renderingFrame.RenderingFrameEmmiter)
		 */
		@Override
		public void render(RenderingFrameEmmiter emmiter) {
			ProtectedRenderEngineBackend.this.checkContext();
			this.target.render(emmiter);
		}
		
	}
	
	/**
	 * @pre El backend no puede ser nulo
	 * @post Crea el backend protegido con el backend especificado
	 */
	public ProtectedRenderEngineBackend(RenderEngineBackend target) {
		super();
		
		if ( target != null ) {
			this.initScreenRenderingFrame(new ProtectedRenderingFrame(target.getScreenRenderingFrame()));
			
			this.target = target;
			this.onContext = true;
		}
		else {
			throw new NullPointerException();
		}
	}
	
	/**
	 * @post Verifica el contexto
	 */
	private void checkContext() {
		if ( !this.onContext ) {
			throw new IllegalStateException("Invalid context");
		}
	}
	
	/* (non-Javadoc)
	 * @see com.esferixis.gameengine.renderengine.backend.RenderEngineBackend#load(com.esferixis.gameengine.renderengine.backend.misc.mesh.Mesh)
	 */
	@Override
	public <V extends Vectorf> void load(Mesh<V, ? extends Data<V, ? extends MeshTriangleVertex<V>>> mesh)
			throws NullPointerException, DataLoadingErrorException, PlatformServiceManagerException,
			MissingTextureMappingConfigLoadException, LoadedMeshLoadAttemptException, OutOfMemoryException {
		this.checkContext();
		this.target.load(mesh);
	}

	/* (non-Javadoc)
	 * @see com.esferixis.gameengine.renderengine.backend.RenderEngineBackend#unload(com.esferixis.gameengine.renderengine.backend.misc.mesh.Mesh)
	 */
	@Override
	public <V extends Vectorf> void unload(Mesh<V, ? extends Data<V, ? extends MeshTriangleVertex<V>>> mesh)
			throws NullPointerException, MissingMeshLoadException {
		this.checkContext();
		this.target.unload(mesh);
	}

	/* (non-Javadoc)
	 * @see com.esferixis.gameengine.renderengine.backend.RenderEngineBackend#load(com.esferixis.gameengine.renderengine.texture.Texture)
	 */
	@Override
	public <V extends Vectorf, P extends RasterPicture<V>> void load(Texture<P> texture) throws NullPointerException,
			DataLoadingErrorException, PlatformServiceManagerException, LoadedTextureLoadAttemptException, OutOfMemoryException {
		this.checkContext();
		this.target.load(texture);
	}

	/* (non-Javadoc)
	 * @see com.esferixis.gameengine.renderengine.backend.RenderEngineBackend#unload_internal(com.esferixis.gameengine.renderengine.texture.Texture)
	 */
	@Override
	public <V extends Vectorf, P extends RasterPicture<V>> void unload_internal(Texture<P> texture)
			throws NullPointerException, MissingTextureLoadException, MeshLayersConfigDependencyException {
		this.checkContext();
		this.target.unload(texture);
	}

	/* (non-Javadoc)
	 * @see com.esferixis.gameengine.renderengine.backend.RenderEngineBackend#load(com.esferixis.gameengine.renderengine.backend.meshLayers.MeshLayersConfig)
	 */
	@Override
	public void load(MeshLayersConfig textureMappingConfig)
			throws NullPointerException, PlatformServiceManagerException, MissingTextureLoadException,
			LoadedTextureMappingConfigLoadAttempt, OutOfMemoryException {
		this.checkContext();
		this.target.load(textureMappingConfig);
	}

	/* (non-Javadoc)
	 * @see com.esferixis.gameengine.renderengine.backend.RenderEngineBackend#unload(com.esferixis.gameengine.renderengine.backend.meshLayers.MeshLayersConfig)
	 */
	@Override
	public void unload(MeshLayersConfig textureMappingConfig)
			throws NullPointerException, MeshDependencyException, MissingTextureMappingConfigLoadException {
		this.checkContext();
		this.target.unload(textureMappingConfig);
	}
	
	/**
	 * @post Sale del contexto
	 */
	void exitContext() {
		this.onContext = false;
	}

	/* (non-Javadoc)
	 * @see com.esferixis.gameengine.renderengine.backend.RenderEngineBackend#isLoaded(com.esferixis.gameengine.renderengine.backend.misc.mesh.Mesh)
	 */
	@Override
	public <V extends Vectorf> boolean isLoaded(Mesh<V, ? extends Data<V, ? extends MeshTriangleVertex<V>>> mesh)
			throws NullPointerException {
		this.checkContext();
		return this.target.isLoaded(mesh);
	}

	/* (non-Javadoc)
	 * @see com.esferixis.gameengine.renderengine.backend.RenderEngineBackend#isLoaded(com.esferixis.gameengine.renderengine.backend.texture.Texture)
	 */
	@Override
	public <V extends Vectorf, P extends RasterPicture<V>> boolean isLoaded(Texture<P> texture) {
		this.checkContext();
		return this.target.isLoaded(texture);
	}

	/* (non-Javadoc)
	 * @see com.esferixis.gameengine.renderengine.backend.RenderEngineBackend#isLoaded(com.esferixis.gameengine.renderengine.backend.meshLayers.MeshLayersConfig)
	 */
	@Override
	public boolean isLoaded(MeshLayersConfig meshLayersConfig) throws NullPointerException {
		this.checkContext();
		return this.target.isLoaded(meshLayersConfig);
	}
}
