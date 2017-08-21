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

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;

import com.esferixis.gameengine.renderengine.backend.RenderEngineBackend;
import com.esferixis.gameengine.renderengine.frontend.RenderEngineFrontendLoadableObject;
import com.esferixis.gameengine.renderengine.frontend.implementation.core.Core;
import com.esferixis.gameengine.renderengine.frontend.meshLayers.MeshLayer;
import com.esferixis.gameengine.renderengine.frontend.meshLayers.MeshLayersConfig;
import com.esferixis.gameengine.renderengine.frontend.meshLayers.SimpleTextureLayer;
import com.esferixis.gameengine.renderengine.frontend.meshLayers.SimpleTextureLayerVertexData;
import com.esferixis.gameengine.renderengine.frontend.meshLayers.UniformColoredMeshLayer;
import com.esferixis.gameengine.renderengine.frontend.meshLayers.VertexColoredMeshLayer;
import com.esferixis.gameengine.renderengine.frontend.meshLayers.VertexColoredMeshLayerVertexData;
import com.esferixis.gameengine.renderengine.frontend.misc.mesh.Mesh;
import com.esferixis.gameengine.renderengine.frontend.misc.mesh.MeshTriangle;
import com.esferixis.gameengine.renderengine.frontend.misc.mesh.MeshTriangleVertex;
import com.esferixis.gameengine.renderengine.frontend.misc.mesh.Mesh.Data;
import com.esferixis.gameengine.renderengine.frontend.misc.mesh.Mesh.Data.DimensionVisitor;
import com.esferixis.gameengine.renderengine.frontend.misc.mesh.Mesh.Data.Visitor;
import com.esferixis.gameengine.renderengine.frontend.misc.mesh.colored.ColoredMeshData;
import com.esferixis.gameengine.renderengine.frontend.misc.mesh.colored.ColoredMeshVertex;
import com.esferixis.math.Vector2f;
import com.esferixis.math.Vector3f;
import com.esferixis.math.Vectorf;
import com.esferixis.misc.accesor.AccesorHolder;
import com.esferixis.misc.accesor.AccesorWhiteList;
import com.esferixis.misc.loader.AbstractDataLoader;
import com.esferixis.misc.loader.DataLoadingErrorException;

/**
 * @author ariel
 *
 */
public final class MeshCore<V extends Vectorf, D extends Mesh.Data<V, ?>> extends Core<MeshCore<V,D>, MeshRenderEngineBackendProfile<V,D>> {
	private final Mesh<V, D> mesh;
	private final com.esferixis.gameengine.renderengine.backend.misc.mesh.Mesh<V, ?> backend;
	
	// Clase de acceso de implementación
	public static final class Accesor {
		private Accesor() {}
		
		@AccesorWhiteList
		private static final Class<?>[] packageAccesors = new Class[]{
			com.esferixis.gameengine.renderengine.frontend.misc.mesh.PackageAccesors.class
		};
		
		/**
		 * @post Crea un núcleo de malla
		 */
		public <V extends Vectorf, D extends Mesh.Data<V, ?>> MeshCore<V, D> create(Mesh<V, D> mesh) {
			return new MeshCore<V, D>(mesh);
		}
	};
	
	/**
	 * @post Crea el núcleo con la malla especificada
	 */
	private MeshCore(final Mesh<V, D> mesh) {
		this.mesh = mesh;
		this.backend = new com.esferixis.gameengine.renderengine.backend.misc.mesh.Mesh(new AbstractDataLoader<com.esferixis.gameengine.renderengine.backend.misc.mesh.Mesh.Data<?, ?>>() {

			@Override
			protected com.esferixis.gameengine.renderengine.backend.misc.mesh.Mesh.Data<?, ?> get_internal()
					throws DataLoadingErrorException {
				return mesh.getDataLoader().get().accept(new DimensionVisitor<com.esferixis.gameengine.renderengine.backend.misc.mesh.Mesh.Data<?, ?>, RuntimeException>() {

					@Override
					public com.esferixis.gameengine.renderengine.backend.misc.mesh.Mesh.Data<?, ?> visit3d(
							Data<Vector3f, MeshTriangleVertex<Vector3f>> meshData) {
						return createDataBacker( (V) Vector3f.ZERO, (Mesh.Data) meshData);
					}

					@Override
					public com.esferixis.gameengine.renderengine.backend.misc.mesh.Mesh.Data<?, ?> visit2d(
							Data<Vector2f, MeshTriangleVertex<Vector2f>> meshData) {
						return createDataBacker( (V) Vector2f.ZERO, (Mesh.Data) meshData);
					}
					
					private com.esferixis.gameengine.renderengine.backend.misc.mesh.Mesh.Data<V, ?> createDataBacker(final V zeroVertex, final Data<V, MeshTriangleVertex<V>> meshData) {
						return meshData.accept(new Visitor<V, com.esferixis.gameengine.renderengine.backend.misc.mesh.Mesh.Data<V, ?>, RuntimeException>() {

							@Override
							public com.esferixis.gameengine.renderengine.backend.misc.mesh.Mesh.Data<V, ?> visit(
									final ColoredMeshData<V> meshData) throws RuntimeException {
								final MeshLayersConfig meshLayersConfig = meshData.getMeshLayersConfig();
								
								final com.esferixis.gameengine.renderengine.backend.meshLayers.MeshLayersConfig meshLayersConfigBacker = PackageAccesors.meshLayerConfigAccesor.get().getCore(meshLayersConfig).getBackend();
								
								final List<com.esferixis.gameengine.renderengine.backend.misc.mesh.MeshTriangle<V, com.esferixis.gameengine.renderengine.backend.misc.mesh.colored.ColoredMeshVertex<V>>> triangleBackers = new ArrayList<com.esferixis.gameengine.renderengine.backend.misc.mesh.MeshTriangle<V, com.esferixis.gameengine.renderengine.backend.misc.mesh.colored.ColoredMeshVertex<V>>>();
								
								for ( MeshTriangle<V, ColoredMeshVertex<V>> eachTriangle : meshData.getTriangles() ) {
									triangleBackers.add(new com.esferixis.gameengine.renderengine.backend.misc.mesh.MeshTriangle<V, com.esferixis.gameengine.renderengine.backend.misc.mesh.colored.ColoredMeshVertex<V>>(createVertexBacker(meshLayersConfig, eachTriangle.getPoint1()), createVertexBacker(meshLayersConfig, eachTriangle.getPoint2()), createVertexBacker(meshLayersConfig, eachTriangle.getPoint3())));
								}
								
								return new com.esferixis.gameengine.renderengine.backend.misc.mesh.colored.ColoredMeshData(triangleBackers, meshLayersConfigBacker);
							}
							
							private com.esferixis.gameengine.renderengine.backend.misc.mesh.colored.ColoredMeshVertex<V> createVertexBacker(final MeshLayersConfig meshLayersConfig, final ColoredMeshVertex<V> coloredMeshVertex) throws RuntimeException {
								final List<com.esferixis.gameengine.renderengine.backend.meshLayers.LayerVertexData> layerVertexDataBacker = new ArrayList<com.esferixis.gameengine.renderengine.backend.meshLayers.LayerVertexData>(coloredMeshVertex.getVertexLayersData().getLayersData().size());
								int i=0;
								
								for ( MeshLayer eachMeshLayer : meshLayersConfig.getLayers() ) {
									final com.esferixis.gameengine.renderengine.frontend.meshLayers.LayerVertexData layerVertexData = coloredMeshVertex.getVertexLayersData().getLayersData().get(i++);
									
									try {
										layerVertexDataBacker.add((com.esferixis.gameengine.renderengine.backend.meshLayers.LayerVertexData) eachMeshLayer.accept( new MeshLayer.Visitor<com.esferixis.gameengine.renderengine.backend.meshLayers.LayerVertexData, RuntimeException>() {

											@Override
											public com.esferixis.gameengine.renderengine.backend.meshLayers.LayerVertexData visit(
													SimpleTextureLayer<?> layer) throws RuntimeException {
												return new com.esferixis.gameengine.renderengine.backend.meshLayers.SimpleTextureLayerVertexData<V>( ( (SimpleTextureLayerVertexData<V>) layerVertexData ).getPosition() );
											}

											@Override
											public com.esferixis.gameengine.renderengine.backend.meshLayers.LayerVertexData visit(
													VertexColoredMeshLayer layer) throws RuntimeException {
												return new com.esferixis.gameengine.renderengine.backend.meshLayers.VertexColoredMeshLayerVertexData( ( (VertexColoredMeshLayerVertexData) layerVertexData ).getColor() );
											}

											@Override
											public com.esferixis.gameengine.renderengine.backend.meshLayers.LayerVertexData visit(
													UniformColoredMeshLayer layer) throws RuntimeException {
												return new com.esferixis.gameengine.renderengine.backend.meshLayers.UniformColoredMeshLayerVertexData();
											}
											
										}) );
									} catch (RuntimeException e) {
										throw e;
									} catch (Throwable e) {
										throw new RuntimeException(e);
									}
								}
								
								return new com.esferixis.gameengine.renderengine.backend.misc.mesh.colored.ColoredMeshVertex<V>(coloredMeshVertex.getPosition(), new com.esferixis.gameengine.renderengine.backend.meshLayers.VertexLayersData(layerVertexDataBacker));
							}
							
						});
					}
				});
			}
			
		});
	}
	
	/**
	 * @post Devuelve el backend
	 */
	public com.esferixis.gameengine.renderengine.backend.misc.mesh.Mesh<V, ?> getBackend() {
		return this.backend;
	}
	
	/* (non-Javadoc)
	 * @see com.esferixis.gameengine.renderengine.frontend.implementation.core.Core#createProfile(com.esferixis.gameengine.renderengine.backend.RenderEngineBackend)
	 */
	@Override
	protected MeshRenderEngineBackendProfile<V, D> createProfile(RenderEngineBackend renderEngineBackend) {
		return new MeshRenderEngineBackendProfile<V, D>(renderEngineBackend, this);
	}

	/* (non-Javadoc)
	 * @see com.esferixis.gameengine.renderengine.frontend.implementation.core.Core#createDependienciesCollection()
	 */
	@Override
	protected Collection<RenderEngineFrontendLoadableObject> createDependienciesCollection() {
		try {
			return mesh.getDataLoader().get().accept(new DimensionVisitor<Collection<RenderEngineFrontendLoadableObject>, RuntimeException>() {

				@Override
				public Collection<RenderEngineFrontendLoadableObject> visit3d(
						Data<Vector3f, MeshTriangleVertex<Vector3f>> meshData) {
					return createDataBacker( (V) Vector3f.ZERO, (Mesh.Data) meshData );
				}

				@Override
				public Collection<RenderEngineFrontendLoadableObject> visit2d(
						Data<Vector2f, MeshTriangleVertex<Vector2f>> meshData) {
					return createDataBacker( (V) Vector2f.ZERO, (Mesh.Data) meshData);
				}
				
				private Collection<RenderEngineFrontendLoadableObject> createDataBacker(final V zeroVertex, final Data<V, MeshTriangleVertex<V>> meshData) {
					return meshData.accept(new Visitor<V, Collection<RenderEngineFrontendLoadableObject>, RuntimeException>() {

						@Override
						public Collection<RenderEngineFrontendLoadableObject> visit(
								final ColoredMeshData<V> meshData) throws RuntimeException {
							return Collections.singleton( (RenderEngineFrontendLoadableObject) meshData.getMeshLayersConfig());
						}
					});
				}
			});
		} catch (DataLoadingErrorException e) {
			throw new RuntimeException(e);
		}
	}
	
}
