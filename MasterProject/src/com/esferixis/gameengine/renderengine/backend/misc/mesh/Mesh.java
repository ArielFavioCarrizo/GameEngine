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
package com.esferixis.gameengine.renderengine.backend.misc.mesh;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import com.esferixis.gameengine.renderengine.backend.LoadableBackendObject;
import com.esferixis.gameengine.renderengine.backend.misc.mesh.colored.ColoredMeshData;
import com.esferixis.gameengine.renderengine.exception.InvalidMeshDataDimensions;
import com.esferixis.math.Vector1f;
import com.esferixis.math.Vector2f;
import com.esferixis.math.Vector3f;
import com.esferixis.math.Vector4f;
import com.esferixis.math.Vectorf;
import com.esferixis.misc.dynamicFields.DynamicFieldsContainer;
import com.esferixis.misc.loader.AbstractDataLoader;
import com.esferixis.misc.loader.DataLoader;
import com.esferixis.misc.loader.DataLoadingErrorException;

/**
 * Malla.
 * 
 * @author ariel
 *
 */
public final class Mesh<V extends Vectorf, D extends Mesh.Data<V, ?>> extends LoadableBackendObject {
	public static abstract class Data<V extends Vectorf, P extends MeshTriangleVertex<V>> {
		public static interface Visitor<V extends Vectorf, R, E extends Throwable> {
			public R visit(ColoredMeshData<V> meshData) throws E;
		}
		
		public static interface DimensionVisitor<R, E extends Throwable> {
			public R visit3d(Mesh.Data<Vector3f, MeshTriangleVertex<Vector3f>> meshData) throws E;
			public R visit2d(Mesh.Data<Vector2f, MeshTriangleVertex<Vector2f>> meshData) throws E;
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
		public final <R, E extends Throwable> R accept(final DimensionVisitor<R, E> dimensionVisitor) throws E {
			return this.triangles.get(0).getPoint1().getPosition().accept(new Vectorf.Visitor<R, E>() {

				@Override
				public R visit(Vector1f vector1f) throws E {
					throw new InvalidMeshDataDimensions();
				}

				@Override
				public R visit(Vector2f vector2f) throws E {
					return dimensionVisitor.visit2d( (Mesh.Data<Vector2f, MeshTriangleVertex<Vector2f>>) Data.this);
				}

				@Override
				public R visit(Vector3f vector3f) throws E {
					return dimensionVisitor.visit3d( (Mesh.Data<Vector3f, MeshTriangleVertex<Vector3f>>) Data.this);
				}

				@Override
				public R visit(Vector4f vector4f) throws E {
					throw new InvalidMeshDataDimensions();
				}
				
			});
		}
		
		/**
		 * @post Visita con el visitor especificado
		 */
		public abstract <R, E extends Throwable> R accept(Visitor<V, R, E> visitor) throws E;
	}
	
	private final DataLoader<D> dataLoader;
	private final DynamicFieldsContainer dynamicFieldsContainer;
	
	/**
	 * @pre El cargador de elementos no puede ser nulo y tiene que dar
	 * 		triángulos con las mismas propiedades
	 * @post Crea la malla con el cargador especificado
	 */
	public Mesh(DataLoader<D> dataLoader) {
		if ( dataLoader != null ) {
			this.dataLoader = dataLoader;
			this.dynamicFieldsContainer = new DynamicFieldsContainer();
		}
		else {
			throw new NullPointerException();
		}
	}
	
	/**
	 * @post Devuelve la información
	 */
	public D getData() throws DataLoadingErrorException {
		return this.dataLoader.get();
	}
	
	/**
	 * @post Devuelve el contenedor de miembros dinámicos
	 */
	public DynamicFieldsContainer dynamicFieldsContainer() {
		return this.dynamicFieldsContainer;
	}

	/* (non-Javadoc)
	 * @see com.esferixis.gameengine.renderengine.backend.LoadableBackendObject#accept(com.esferixis.gameengine.renderengine.backend.LoadableBackendObject.Visitor)
	 */
	@Override
	public <R, E extends Throwable> R accept(Visitor<R, E> visitor) throws E {
		return visitor.visit(this);
	}
}
