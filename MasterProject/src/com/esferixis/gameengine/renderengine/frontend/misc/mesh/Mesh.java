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
package com.esferixis.gameengine.renderengine.frontend.misc.mesh;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import com.esferixis.gameengine.renderengine.exception.InvalidMeshDataDimensions;
import com.esferixis.gameengine.renderengine.frontend.RenderEngineFrontendLoadableObject;
import com.esferixis.gameengine.renderengine.frontend.implementation.CoreFactory;
import com.esferixis.gameengine.renderengine.frontend.implementation.core.Core;
import com.esferixis.gameengine.renderengine.frontend.implementation.mesh.MeshCore;
import com.esferixis.gameengine.renderengine.frontend.meshLayers.LayerVertexData;
import com.esferixis.gameengine.renderengine.frontend.meshLayers.MeshLayer;
import com.esferixis.gameengine.renderengine.frontend.meshLayers.MeshLayersConfig;
import com.esferixis.gameengine.renderengine.frontend.meshLayers.SimpleTextureLayer;
import com.esferixis.gameengine.renderengine.frontend.meshLayers.SimpleTextureLayerVertexData;
import com.esferixis.gameengine.renderengine.frontend.meshLayers.UniformColoredMeshLayer;
import com.esferixis.gameengine.renderengine.frontend.meshLayers.VertexColoredMeshLayer;
import com.esferixis.gameengine.renderengine.frontend.meshLayers.VertexColoredMeshLayerVertexData;
import com.esferixis.gameengine.renderengine.frontend.misc.mesh.Mesh.Data.DimensionVisitor;
import com.esferixis.gameengine.renderengine.frontend.misc.mesh.Mesh.Data.Visitor;
import com.esferixis.gameengine.renderengine.frontend.misc.mesh.colored.ColoredMeshData;
import com.esferixis.gameengine.renderengine.frontend.misc.mesh.colored.ColoredMeshVertex;
import com.esferixis.geometry.plane.finite.FiniteAffineHolomorphicShape;
import com.esferixis.math.Vector1f;
import com.esferixis.math.Vector2f;
import com.esferixis.math.Vector3f;
import com.esferixis.math.Vector4f;
import com.esferixis.math.Vectorf;
import com.esferixis.misc.accesor.AccesorHolder;
import com.esferixis.misc.accesor.AccesorWhiteList;
import com.esferixis.misc.loader.AbstractDataLoader;
import com.esferixis.misc.loader.DataLoader;
import com.esferixis.misc.loader.DataLoadingErrorException;
import com.esferixis.misc.loader.SerializableDataLoader;
import com.esferixis.misc.loader.SerializableLengthedDataLoader;

/**
 * @author ariel
 *
 */
public final class Mesh<V extends Vectorf, D extends Mesh.Data<V, ?>> extends RenderEngineFrontendLoadableObject implements Serializable {
	/**
	 * 
	 */
	private static final long serialVersionUID = -33958933292890096L;

	public static abstract class Data<V extends Vectorf, P extends MeshTriangleVertex<V>> implements Serializable {
		/**
		 * 
		 */
		private static final long serialVersionUID = 6216082909250224950L;

		public static interface Visitor<V extends Vectorf, R, T extends Throwable> {
			public R visit(ColoredMeshData<V> meshData) throws T;
		}
		
		public static interface DimensionVisitor<R, T extends Throwable> {
			public R visit3d(Mesh.Data<Vector3f, MeshTriangleVertex<Vector3f>> meshData) throws T;
			public R visit2d(Mesh.Data<Vector2f, MeshTriangleVertex<Vector2f>> meshData) throws T;
		}
		
		private final List< MeshTriangle<V, P> > triangles;
		
		/**
		 * @post Crea los datos con los triángulos especificados
		 * @param triangles
		 */
		public Data(List< MeshTriangle<V, P> > triangles) {
			if ( triangles != null ) {
				this.triangles = Collections.unmodifiableList( new ArrayList< MeshTriangle<V, P> >(triangles) );
			}
			else {
				throw new NullPointerException();
			}
		}
		
		/**
		 * @post Devuelve los triángulos
		 */
		public List< MeshTriangle<V, P> > getTriangles() {
			return this.triangles;
		}
		
		/**
		 * @post Visita con el visitor de dimensionalidad especificado
		 */
		public final <R, T extends Throwable> R accept(final DimensionVisitor<R, T> dimensionVisitor) throws T {
			return this.triangles.get(0).getPoint1().getPosition().accept(new Vectorf.Visitor<R, T>() {

				@Override
				public R visit(Vector1f vector1f) throws T {
					throw new InvalidMeshDataDimensions();
				}

				@Override
				public R visit(Vector2f vector2f) throws T {
					return dimensionVisitor.visit2d( (Mesh.Data<Vector2f, MeshTriangleVertex<Vector2f>>) Data.this);
				}

				@Override
				public R visit(Vector3f vector3f) throws T {
					return dimensionVisitor.visit3d( (Mesh.Data<Vector3f, MeshTriangleVertex<Vector3f>>) Data.this);
				}

				@Override
				public R visit(Vector4f vector4f) throws T {
					throw new InvalidMeshDataDimensions();
				}
				
			});
		}
		
		/**
		 * @post Visita con el visitor especificado
		 */
		public abstract <R, T extends Throwable> R accept(Visitor<V, R, T> visitor) throws T;
	}
	
	private final SerializableLengthedDataLoader<D> dataLoader;
	private transient MeshCore<V, D> meshCore;
	
	private static final CoreFactory<Mesh<?, ? extends Mesh.Data<?, ?>>, MeshCore<?, ?> > coreFactory = new CoreFactory<Mesh<?, ? extends Mesh.Data<?, ?>>, MeshCore<?, ?>>() {

		@Override
		protected MeshCore<?, ?> createCore(Mesh<?, ? extends Data<?, ?>> mesh) throws RuntimeException {
			return PackageAccesors.meshCoreAccesor.get().create((Mesh) mesh);
		}
		
	};
	
	public static final class Accesor {
		private Accesor() {}
		
		@AccesorWhiteList
		private static final Class<?>[] packageAccesors = new Class[] {
			com.esferixis.gameengine.renderengine.frontend.PackageAccesors.class,
			com.esferixis.gameengine.renderengine.frontend.renderingFrame.PackageAccesors.class
		};
		
		/**
		 * @post Devuelve el núcleo de la malla especificada
		 */
		public <V extends Vectorf, D extends Mesh.Data<V, ?>> MeshCore<V, D> getCore(Mesh<V, D> mesh) {
			return mesh.getCore();
		}
	}
	
	/**
	 * @pre El cargador de elementos no puede ser nulo y tiene que dar
	 * 		triángulos con las mismas propiedades
	 * @post Crea la malla con el cargador especificado
	 */
	public Mesh(final DataLoader<D> dataLoader) {
		if ( dataLoader != null ) {
			this.meshCore = null;
			this.dataLoader = new SerializableLengthedDataLoader<D>() {
				/**
				 * 
				 */
				private static final long serialVersionUID = -560407964162973782L;
				
				private Integer size=null;

				@Override
				public D get() throws DataLoadingErrorException {
					return dataLoader.get();
				}

				@Override
				public int getDataLength() {
					if ( size == null ) {
						final D meshData;
						
						try {
							meshData = this.get();
						} catch (DataLoadingErrorException e) {
							throw new RuntimeException(e);
						}
						
						size = Float.SIZE * 4 + meshData.accept(new Mesh.Data.DimensionVisitor<Integer, RuntimeException>() {

							@Override
							public Integer visit3d(
									Mesh.Data<Vector3f, MeshTriangleVertex<Vector3f>> meshData)
									throws RuntimeException {
								return meshData.accept(new Mesh.Data.Visitor<Vector3f, Integer, RuntimeException>() {

									@Override
									public Integer visit(
											ColoredMeshData<Vector3f> meshData)
											throws RuntimeException {
										return Float.SIZE * 4 + getLayerDataSpacePerVertex(meshData.getMeshLayersConfig());
									}
									
								});
							}

							@Override
							public Integer visit2d(
									Mesh.Data<Vector2f, MeshTriangleVertex<Vector2f>> meshData)
									throws RuntimeException {
								return meshData.accept(new Mesh.Data.Visitor<Vector2f, Integer, RuntimeException>() {

									@Override
									public Integer visit(
											ColoredMeshData<Vector2f> meshData)
											throws RuntimeException {
										return Float.SIZE * 4 + getLayerDataSpacePerVertex(meshData.getMeshLayersConfig());
									}
									
								});
							}
							
							private int getLayerDataSpacePerVertex(MeshLayersConfig meshLayersConfig) {
								int totalSpace = 0;
								
								for ( MeshLayer<? extends LayerVertexData> eachMeshLayer : meshLayersConfig.getLayers() ) {
									totalSpace += eachMeshLayer.accept( new MeshLayer.Visitor<Long, RuntimeException>() {

										@Override
										public Long visit(SimpleTextureLayer<?> layer) throws RuntimeException {
											return (long) ( Float.SIZE * 4 );
										}

										@Override
										public Long visit(VertexColoredMeshLayer layer) throws RuntimeException {
											return (long) ( Float.SIZE * 4 );
										}

										@Override
										public Long visit(UniformColoredMeshLayer layer) throws RuntimeException {
											return (long) 0;
										}
										
									});
									
									if ( eachMeshLayer.getMaskColorField() != null ) {
										totalSpace += (long) ( Float.SIZE * 4 );
									}
								}
								
								return totalSpace;
							}
							
						});
					}
					
					return size;
				}
				
			};
		}
		else {
			throw new NullPointerException();
		}
	}
	
	/**
	 * @pre El cargador de elementos no puede ser nulo y tiene que dar
	 * 		triángulos con las mismas propiedades
	 * @post Crea la malla con el cargador especificado
	 */
	public Mesh(SerializableLengthedDataLoader<D> dataLoader) {
		if ( dataLoader != null ) {
			this.dataLoader = dataLoader;
			this.meshCore = null;
		}
		else {
			throw new NullPointerException();
		}
	}
	
	/**
	 * @post Devuelve el núcleo
	 */
	@Override
	protected MeshCore<V, D> getCore() {
		if ( this.meshCore == null ) {
			this.meshCore = (MeshCore<V, D>) this.coreFactory.getCore(this);
		}
		
		return this.meshCore;
	}
	
	/**
	 * @post Devuelve el cargador de datos
	 */
	public SerializableLengthedDataLoader<D> getDataLoader() {
		return this.dataLoader;
	}
	
	/**
	 * @post Devuelve el hash
	 */
	@Override
	public int hashCode() {
		return this.dataLoader.hashCode();
	}
	
	/**
	 * @post Devuelve si es igual al objeto especificado
	 */
	@Override
	public boolean equals(Object other) {
		if ( ( other != null ) && ( other instanceof Mesh ) ) {
			return ( (Mesh<?, ?>) other ).dataLoader.equals(this.dataLoader);
		}
		else {
			return false;
		}
	}

	/* (non-Javadoc)
	 * @see com.esferixis.gameengine.renderengine.frontend.RenderEngineFrontendObject#accept(com.esferixis.gameengine.renderengine.frontend.RenderEngineFrontendObject.Visitor)
	 */
	@Override
	public <R, E extends Throwable> R accept(Visitor<R, E> visitor) throws E {
		return visitor.visit(this);
	}
}
