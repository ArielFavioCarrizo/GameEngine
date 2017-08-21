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

import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import com.esferixis.gameengine.platform.PlatformServiceManagerException;
import com.esferixis.gameengine.renderengine.backend.RenderEngineBackend;
import com.esferixis.gameengine.renderengine.backend.exception.OutOfMemoryException;
import com.esferixis.gameengine.renderengine.frontend.meshLayers.LayerVertexData;
import com.esferixis.gameengine.renderengine.frontend.meshLayers.MeshLayer;
import com.esferixis.gameengine.renderengine.frontend.meshLayers.MeshLayersConfig;
import com.esferixis.gameengine.renderengine.frontend.meshLayers.SimpleTextureLayer;
import com.esferixis.gameengine.renderengine.frontend.meshLayers.UniformColoredMeshLayer;
import com.esferixis.gameengine.renderengine.frontend.meshLayers.VertexColoredMeshLayer;
import com.esferixis.gameengine.renderengine.frontend.misc.mesh.Mesh;
import com.esferixis.gameengine.renderengine.frontend.misc.mesh.MeshTriangleVertex;
import com.esferixis.gameengine.renderengine.frontend.misc.mesh.colored.ColoredMeshData;
import com.esferixis.gameengine.renderengine.frontend.texture.Texture;
import com.esferixis.gameengine.renderengine.picture.RasterPicture;
import com.esferixis.math.Vector2f;
import com.esferixis.math.Vector3f;
import com.esferixis.misc.loader.AbstractDataLoader;
import com.esferixis.misc.loader.DataLoadingErrorException;
import com.esferixis.misc.loadingmanager.LoadingStrategy;

/**
 * @author ariel
 *
 */
class RenderEngineFrontendLoadableObjectLoadingStrategy implements LoadingStrategy<RenderEngineFrontendLoadableObject> {
	private final RenderEngineBackend renderEngineBackend;
	
	private final Map<RenderEngineFrontendLoadableObject, Set<com.esferixis.misc.loadingmanager.LoadingStrategy.Observer>> observersPerElement;
	
	/**
	 * @post Crea la estrategia con el backend de rendering especificado
	 */
	public RenderEngineFrontendLoadableObjectLoadingStrategy(RenderEngineBackend renderEngineBackend) {
		if ( renderEngineBackend != null ) {
			this.renderEngineBackend = renderEngineBackend;
			
			this.observersPerElement = new HashMap<RenderEngineFrontendLoadableObject, Set<com.esferixis.misc.loadingmanager.LoadingStrategy.Observer>>();
		}
		else {
			throw new NullPointerException();
		}
	}
	
	/* (non-Javadoc)
	 * @see com.esferixis.misc.loadingmanager.LoadingStrategy#load(com.esferixis.misc.dynamicFields.DynamicFieldsContainerObject)
	 */
	@Override
	public void load(RenderEngineFrontendLoadableObject element) {
		try {
			element.getCore().getProfile(RenderEngineFrontendLoadableObjectLoadingStrategy.this.renderEngineBackend).load();
		} catch (PlatformServiceManagerException | DataLoadingErrorException e) {
			throw new RuntimeException(e);
		}
	}

	/* (non-Javadoc)
	 * @see com.esferixis.misc.loadingmanager.LoadingStrategy#unload(com.esferixis.misc.dynamicFields.DynamicFieldsContainerObject)
	 */
	@Override
	public void unload(RenderEngineFrontendLoadableObject element) {
		element.getCore().getProfile(RenderEngineFrontendLoadableObjectLoadingStrategy.this.renderEngineBackend).unload();
	}

	/* (non-Javadoc)
	 * @see com.esferixis.misc.loadingmanager.LoadingStrategy#isLoaded(com.esferixis.misc.dynamicFields.DynamicFieldsContainerObject)
	 */
	@Override
	public boolean isLoaded(RenderEngineFrontendLoadableObject element) {
		return element.getCore().getProfile(RenderEngineFrontendLoadableObjectLoadingStrategy.this.renderEngineBackend).isLoaded();
	}

	/* (non-Javadoc)
	 * @see com.esferixis.misc.loadingmanager.LoadingStrategy#attachObserver(com.esferixis.misc.dynamicFields.DynamicFieldsContainerObject, com.esferixis.misc.loadingmanager.LoadingStrategy.Observer)
	 */
	@Override
	public void attachObserver(RenderEngineFrontendLoadableObject element,
			com.esferixis.misc.loadingmanager.LoadingStrategy.Observer observer) {
		
		Set<com.esferixis.misc.loadingmanager.LoadingStrategy.Observer> observers = this.observersPerElement.get(observer);
		
		if ( observers == null ) {
			observers = new HashSet<com.esferixis.misc.loadingmanager.LoadingStrategy.Observer>();
		}
		
		observers.add(observer);
	}

	/* (non-Javadoc)
	 * @see com.esferixis.misc.loadingmanager.LoadingStrategy#detachObserver(com.esferixis.misc.dynamicFields.DynamicFieldsContainerObject, com.esferixis.misc.loadingmanager.LoadingStrategy.Observer)
	 */
	@Override
	public void detachObserver(RenderEngineFrontendLoadableObject element,
			com.esferixis.misc.loadingmanager.LoadingStrategy.Observer observer) {
		Set<com.esferixis.misc.loadingmanager.LoadingStrategy.Observer> observers = this.observersPerElement.get(observer);
		
		if ( observers != null ) {
			observers.remove(observer);
			
			if ( !observers.isEmpty() ) {
				this.observersPerElement.remove(element);
			}
		}
	}

	/* (non-Javadoc)
	 * @see com.esferixis.misc.loadingmanager.LoadingStrategy#getOccupiedSpace(com.esferixis.misc.dynamicFields.DynamicFieldsContainerObject)
	 */
	@Override
	public long getOccupiedSpace(RenderEngineFrontendLoadableObject element) {
		return element.accept(new RenderEngineFrontendLoadableObject.Visitor<Long, RuntimeException>() {

			@Override
			public Long visit(MeshLayersConfig meshLayersConfig) throws RuntimeException {
				return ( 1024 + (long) meshLayersConfig.getLayers().size() * 64 );
			}

			@Override
			public Long visit(Texture<?> texture) throws RuntimeException {
				return (long) ( texture.getLoader().getDataLength() + 512 );
			}

			@Override
			public Long visit(Mesh<?, ?> mesh) throws RuntimeException {
				return (long) mesh.getDataLoader().getDataLength();
			}
			
		} );
	}

	/* (non-Javadoc)
	 * @see com.esferixis.misc.loadingmanager.LoadingStrategy#getDependencies(com.esferixis.misc.dynamicFields.DynamicFieldsContainerObject)
	 */
	@Override
	public Collection<RenderEngineFrontendLoadableObject> getDependencies(
			RenderEngineFrontendLoadableObject element) {
		return element.getCore().getDependencies();
	}

}
