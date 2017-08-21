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

package com.esferixis.gameengine.renderengine.backend;

import java.io.IOException;

import com.esferixis.gameengine.platform.PlatformServiceManagerException;
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
import com.esferixis.gameengine.renderengine.backend.renderingFrame.RenderingFrame;
import com.esferixis.gameengine.renderengine.backend.texture.Texture;
import com.esferixis.gameengine.renderengine.picture.RasterPicture;
import com.esferixis.math.Vectorf;
import com.esferixis.misc.loader.DataLoadingErrorException;

/**
 * Backend del engine de rendering (Dependiente de la plataforma)
 * 
 * El back-end renderiza las mallas sin optimización alguna,
 * aplicando las luces y sombras sobre las mallas, en forma selectiva.
 * 
 * @author Ariel Favio Carrizo
 *
 */
public abstract class RenderEngineBackend {
	private RenderingFrame screenRenderingFrame;
	
	/**
	 * @post Crea el backend del engine de renderización
	 */
	protected RenderEngineBackend() {
		this.screenRenderingFrame = null;
	}
	
	/**
	 * @pre El cuadro de renderización de pantalla no tiene que haber sido inicializado, y no puede ser nulo
	 * @post Especifica el cuadro de renderización de pantalla
	 */
	protected final void initScreenRenderingFrame(RenderingFrame screenRenderingFrame) {
		if ( this.screenRenderingFrame == null ) {
			if ( screenRenderingFrame != null ) {
				this.screenRenderingFrame = screenRenderingFrame;
			}
			else {
				throw new NullPointerException();
			}
		}
		else {
			throw new IllegalStateException("Attemped to initialize screen rendering frame when it has been initialized");
		}
	}
	
	/**
	 * @pre El cuadro de renderización de pantalla no puede ser nulo
	 * @post Crea el backend del engine de renderización con el cuadro de renderización
	 * 		 de pantalla especificado
	 */
	protected RenderEngineBackend(RenderingFrame screenRenderingFrame) {
		if ( screenRenderingFrame != null ) {
			this.screenRenderingFrame = screenRenderingFrame;
		}
		else {
			throw new NullPointerException();
		}
	}
	
	/**
	 * @pre La malla puede ser nula, no tiene que estar cargada, en caso de que haya mapeo de texturas,
	 * 		la configuración de mapeo correspondiente, tiene que estar cargada
	 * @post Carga la malla especificada
	 * @throws NullPointerException, IOException, PlatformServiceManagerException, MissingTextureMappingConfigLoadException, LoadedMeshLoadAttemptException, OutOfMemoryException
	 */
	public abstract <V extends Vectorf> void load(Mesh<V, ? extends Mesh.Data<V, ? extends MeshTriangleVertex<V>>> mesh) throws NullPointerException, DataLoadingErrorException, PlatformServiceManagerException, MissingTextureMappingConfigLoadException, LoadedMeshLoadAttemptException, OutOfMemoryException;
	
	/**
	 * @pre La malla no puede ser nula y tiene que estar cargada
	 * @post Descarga la malla especificada
	 * @throws NullPointerException, MissingMeshLoadException
	 */
	public abstract <V extends Vectorf> void unload(Mesh<V, ? extends Mesh.Data<V, ? extends MeshTriangleVertex<V>>> mesh) throws NullPointerException, MissingMeshLoadException;
	
	/**
	 * @pre La malla no puede ser nula
	 * @post Devuelve si está cargada la malla especificada
	 */
	public abstract <V extends Vectorf> boolean isLoaded(Mesh<V, ? extends Mesh.Data<V, ? extends MeshTriangleVertex<V>>> mesh) throws NullPointerException;
	
	/**
	 * @pre La textura no puede ser nula y no tiene que estar cargada
	 * @post Carga la textura especificada
	 * @throws NullPointerException, IOException, PlatformServiceManagerException, LoadedTextureLoadAttemptException, OutOfMemoryException
	 */
	public abstract <V extends Vectorf, P extends RasterPicture<V>> void load(Texture<P> texture) throws NullPointerException, DataLoadingErrorException, PlatformServiceManagerException, LoadedTextureLoadAttemptException, OutOfMemoryException;
	
	/**
	 * @pre La textura no puede ser nula y tiene que estar cargada y no tiene que haber configuraciones de mapeo de texturas
	 * 		cargadas que usen ésta textura
	 * @post Descarga la textura especificada
	 * @throws NullPointerException, TextureMappingConfigDependencyException, MissingTextureLoadException
	 */
	public final <V extends Vectorf, P extends RasterPicture<V>> void unload(Texture<P> texture) throws NullPointerException, MissingTextureLoadException, MeshLayersConfigDependencyException {
		if ( texture != null ) {
			if ( ( texture != Texture.BLANKTEXTURE2D ) || ( texture != Texture.BLANKTEXTURE3D ) ) {
				this.unload_internal(texture);
			}
			else {
				throw new IllegalArgumentException("Cannot unload BLANKTEXTURE2D or BLANKTEXTURE3D");
			}
		}
		else {
			throw new NullPointerException();
		}
	}
	
	/**
	 * @pre La textura no puede ser nula y tiene que estar cargada y no tiene que haber configuraciones de mapeo de texturas
	 * 		cargadas que usen ésta textura
	 * @post Descarga la textura especificada
	 * @throws NullPointerException, TextureMappingConfigDependencyException, MissingTextureLoadException
	 */
	protected abstract <V extends Vectorf, P extends RasterPicture<V>> void unload_internal(Texture<P> texture) throws NullPointerException, MissingTextureLoadException, MeshLayersConfigDependencyException;
	
	/**
	 * @pre La textura no puede ser nula
	 * @post Devuelve si la textura está cargada
	 * @param texture
	 * @return
	 */
	public abstract <V extends Vectorf, P extends RasterPicture<V>> boolean isLoaded(Texture<P> texture);
	
	/**
	 * @pre La configuración de mapeo de textura no puede ser nula y no puede estar cargada
	 * @post Carga la configuración de mapeo de textura especificada
	 * @throws NullPointerException, PlatformServiceManagerException, LoadedTextureMappingConfigLoadAttempt, OutOfMemoryException
	 */
	public abstract void load(MeshLayersConfig meshLayersConfig) throws NullPointerException, PlatformServiceManagerException, LoadedTextureMappingConfigLoadAttempt, OutOfMemoryException;
	
	/**
	 * @pre La configuración de mapeo de textura no puede ser nula y no tiene que estar usada por mallas
	 * 		cargadas
	 * @post Descarga la configuración de mapeo de textura especificada
	 * @throws NullPointerException, MeshDependencyException, MissingTextureMappingConfigLoadException
	 */
	public abstract void unload(MeshLayersConfig meshLayersConfig) throws NullPointerException, MeshDependencyException, MissingTextureMappingConfigLoadException;
	
	/**
	 * @pre La configuración de capas de malla no puede ser nula
	 * @post Devuelve si la configuración de capas de malla está cargada
	 */
	public abstract boolean isLoaded(MeshLayersConfig meshLayersConfig) throws NullPointerException;
	
	/**
	 * @post Devuelve la ventana de renderización de pantalla
	 */
	public final RenderingFrame getScreenRenderingFrame() {
		return this.screenRenderingFrame;
	}
}
