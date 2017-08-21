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
package com.esferixis.gameengine.renderengine.frontend.implementation.meshLayers;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.esferixis.gameengine.renderengine.backend.RenderEngineBackend;
import com.esferixis.gameengine.renderengine.frontend.RenderEngineFrontendLoadableObject;
import com.esferixis.gameengine.renderengine.frontend.implementation.core.Core;
import com.esferixis.gameengine.renderengine.frontend.implementation.mesh.MeshCore;
import com.esferixis.gameengine.renderengine.frontend.meshLayers.LayerDataField;
import com.esferixis.gameengine.renderengine.frontend.meshLayers.MeshLayer;
import com.esferixis.gameengine.renderengine.frontend.meshLayers.MeshLayersConfig;
import com.esferixis.gameengine.renderengine.frontend.meshLayers.SimpleTextureLayer;
import com.esferixis.gameengine.renderengine.frontend.meshLayers.TextureObject2dLayerDataField;
import com.esferixis.gameengine.renderengine.frontend.meshLayers.TextureObject3dLayerDataField;
import com.esferixis.gameengine.renderengine.frontend.meshLayers.TextureObjectLayerDataField;
import com.esferixis.gameengine.renderengine.frontend.meshLayers.UniformColoredMeshLayer;
import com.esferixis.gameengine.renderengine.frontend.meshLayers.VertexColoredMeshLayer;
import com.esferixis.gameengine.renderengine.frontend.misc.mesh.Mesh;
import com.esferixis.gameengine.renderengine.misc.colorObject.ColorObject;
import com.esferixis.gameengine.renderengine.texture.TextureQualitySettings;
import com.esferixis.math.Vector4f;
import com.esferixis.math.Vectorf;
import com.esferixis.misc.accesor.AccesorHolder;
import com.esferixis.misc.accesor.AccesorWhiteList;
import com.esferixis.misc.observer.Observer;
import com.esferixis.misc.reference.DynamicReference;
import com.esferixis.misc.reference.DynamicReferenceObserver;

/**
 * @author ariel
 *
 */
public final class MeshLayersConfigCore extends Core<MeshLayersConfigCore, MeshLayersConfigRenderEngineBackendProfile> {
	private final com.esferixis.gameengine.renderengine.backend.meshLayers.MeshLayersConfig backend;
	
	// Clase de acceso de implementación
	public static final class Accesor {
		private Accesor() {}
		
		@AccesorWhiteList
		private static final Class<?>[] packageAccesors = new Class[] {
			com.esferixis.gameengine.renderengine.frontend.meshLayers.PackageAccesors.class
		};
		
		/**
		 * @post Crea un núcleo de textura del cargador de textura especificado
		 */
		public MeshLayersConfigCore create(MeshLayersConfig meshLayersConfig) {
			return new MeshLayersConfigCore(meshLayersConfig);
		}
	};
	
	private final Collection<DynamicReference<RenderEngineFrontendLoadableObject>> attachedProfileDependenciesDynamicReferences;
	private final Collection<RenderEngineFrontendLoadableObject> attachedProfileDependencies;
	
	private boolean attachedProfileDependenciesChangeFlag;
	
	/**
	 * @post Crea el núcleo con la configuración de capas de malla especificada
	 */
	private MeshLayersConfigCore(MeshLayersConfig meshLayersConfig) {
		final Map<LayerDataField<ColorObject>, com.esferixis.gameengine.renderengine.misc.colorObject.ColorObject> colorObjectByColorField = new HashMap<LayerDataField<ColorObject>, com.esferixis.gameengine.renderengine.misc.colorObject.ColorObject>();
		final List<com.esferixis.gameengine.renderengine.backend.meshLayers.MeshLayer<?>> backendMeshLayers = new ArrayList<com.esferixis.gameengine.renderengine.backend.meshLayers.MeshLayer<?>>(meshLayersConfig.getLayers().size());
		
		this.attachedProfileDependenciesDynamicReferences = new ArrayList<DynamicReference<RenderEngineFrontendLoadableObject>>();
		this.attachedProfileDependencies = DynamicReference.createElementsCollection(this.attachedProfileDependenciesDynamicReferences);
		
		for ( MeshLayer<?> eachLayer : meshLayersConfig.getLayers() ) {
			try {
				final com.esferixis.gameengine.renderengine.misc.colorObject.ColorObject maskColorObject;
				if ( eachLayer.getMaskColorField() != null ){
					com.esferixis.gameengine.renderengine.misc.colorObject.ColorObject colorObject_inner = colorObjectByColorField.get( eachLayer.getMaskColorField() );
					{
						if ( colorObject_inner == null ) {
							colorObject_inner = new com.esferixis.gameengine.renderengine.misc.colorObject.ColorObject(Vector4f.ZERO);
							colorObjectByColorField.put(eachLayer.getMaskColorField(), colorObject_inner);
						}
					}
					maskColorObject = colorObject_inner;
				}
				else {
					maskColorObject = null;
				}
				
				final com.esferixis.gameengine.renderengine.backend.meshLayers.MeshLayer<?> backendMeshLayer = eachLayer.accept(
						new MeshLayer.Visitor<com.esferixis.gameengine.renderengine.backend.meshLayers.MeshLayer<?>, RuntimeException>() {
	
					@Override
					public com.esferixis.gameengine.renderengine.backend.meshLayers.MeshLayer<?> visit(
							SimpleTextureLayer<?> layer) throws RuntimeException {
						com.esferixis.gameengine.renderengine.backend.texture.TextureObject<?> backendTextureObject = layer.getTextureObjectField().accept(new TextureObjectLayerDataField.Visitor<com.esferixis.gameengine.renderengine.backend.texture.TextureObject<?>>() {

							@Override
							public com.esferixis.gameengine.renderengine.backend.texture.TextureObject<?> visit(TextureObject2dLayerDataField textureObject2dLayerDataField) {
								com.esferixis.gameengine.renderengine.backend.texture.TextureObject2d.Essence essence = new com.esferixis.gameengine.renderengine.backend.texture.TextureObject2d.Essence( com.esferixis.gameengine.renderengine.backend.texture.Texture.BLANKTEXTURE2D, new TextureQualitySettings());
								return new com.esferixis.gameengine.renderengine.backend.texture.TextureObject2d(essence);
							}

							@Override
							public com.esferixis.gameengine.renderengine.backend.texture.TextureObject<?> visit(TextureObject3dLayerDataField textureObject3dLayerDataField) {
								com.esferixis.gameengine.renderengine.backend.texture.TextureObject3d.Essence essence = new com.esferixis.gameengine.renderengine.backend.texture.TextureObject3d.Essence(com.esferixis.gameengine.renderengine.backend.texture.Texture.BLANKTEXTURE3D, new TextureQualitySettings());
								return new com.esferixis.gameengine.renderengine.backend.texture.TextureObject3d(essence);
							}

						});
						
						com.esferixis.gameengine.renderengine.backend.meshLayers.MeshLayer<?> backendMeshLayer = new com.esferixis.gameengine.renderengine.backend.meshLayers.SimpleTextureLayer(backendTextureObject, maskColorObject);
						
						return backendMeshLayer;
					}
	
					@Override
					public com.esferixis.gameengine.renderengine.backend.meshLayers.MeshLayer<?> visit(
							VertexColoredMeshLayer layer) throws RuntimeException {
						return new com.esferixis.gameengine.renderengine.backend.meshLayers.VertexColoredMeshLayer(maskColorObject);
					}
	
					@Override
					public com.esferixis.gameengine.renderengine.backend.meshLayers.MeshLayer<?> visit(
							UniformColoredMeshLayer layer) throws RuntimeException {
						return new com.esferixis.gameengine.renderengine.backend.meshLayers.UniformColoredMeshLayer(maskColorObject);
					}
				});
				
				backendMeshLayers.add(backendMeshLayer);
			} catch (Exception e) {
				throw new RuntimeException(e);
			}
		}
		
		this.backend = new com.esferixis.gameengine.renderengine.backend.meshLayers.MeshLayersConfig(backendMeshLayers);
	}
	
	/**
	 * @post Limpia el flag de cambio de dependencias de perfil asociado
	 */
	public void clearAttachedProfileDependenciesChangeFlag() {
		this.attachedProfileDependenciesChangeFlag = false;
	}
	
	/**
	 * @post Devuelve el valor del flag de cambio de dependencias de perfil asociado
	 */
	public boolean getAttachedProfileDependenciesChangeFlagValue() {
		return this.attachedProfileDependenciesChangeFlag;
	}
	
	/**
	 * @post Agrega una referencia de dependencia
	 */
	public void addDependencyDynamicReference(DynamicReference<RenderEngineFrontendLoadableObject> dependencyDynamicReference) {
		this.attachedProfileDependenciesDynamicReferences.add(dependencyDynamicReference);
		new DynamicReferenceObserver<RenderEngineFrontendLoadableObject>(Observer.Type.STRONG) {

			@Override
			protected void notifyValueChange(RenderEngineFrontendLoadableObject newValue) {
				MeshLayersConfigCore.this.attachedProfileDependenciesChangeFlag = true;
			}
			
		}.attach(dependencyDynamicReference);
	}
	
	/**
	 * @post Devuelve la colección de dependencias del perfil asociado
	 */
	public Collection<RenderEngineFrontendLoadableObject> getAttachedProfileDependencies() {
		return this.attachedProfileDependencies;
	}
	
	/**
	 * @post Devuelve el backend
	 */
	public com.esferixis.gameengine.renderengine.backend.meshLayers.MeshLayersConfig getBackend() {
		return this.backend;
	}

	/* (non-Javadoc)
	 * @see com.esferixis.gameengine.renderengine.frontend.implementation.core.Core#createProfile(com.esferixis.gameengine.renderengine.backend.RenderEngineBackend)
	 */
	@Override
	protected MeshLayersConfigRenderEngineBackendProfile createProfile(RenderEngineBackend renderEngineBackend) {
		return new MeshLayersConfigRenderEngineBackendProfile(renderEngineBackend, this);
	}

	/* (non-Javadoc)
	 * @see com.esferixis.gameengine.renderengine.frontend.implementation.core.Core#createDependienciesCollection()
	 */
	@Override
	protected Collection<RenderEngineFrontendLoadableObject> createDependienciesCollection() {
		return Collections.emptyList();
	}
}
