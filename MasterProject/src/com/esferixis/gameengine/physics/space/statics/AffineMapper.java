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
package com.esferixis.gameengine.physics.space.statics;

import com.esferixis.math.Matrix4f;
import com.esferixis.math.Vector3f;
import com.esferixis.misc.ElementCallback;
import com.esferixis.misc.observer.ObserverManager;

/**
 * @author ariel
 *
 */
public final class AffineMapper extends SpatialMapper {
	/**
	 * 
	 */
	private static final long serialVersionUID = -4191923433945616413L;
	
	private Matrix4f transformMatrix;
	ObserverManager<AffineMapper, AffineMapperObserver> observerManager = new ObserverManager<AffineMapper, AffineMapperObserver>(this, AffineMapperObserver.class);
	
	/**
	 * @post Crea el mapeador afin
	 */
	public AffineMapper() {
		this.transformMatrix = Matrix4f.IDENTITY;
	}
	
	/**
	 * @post Crea el mapeador afín con la matriz especificada
	 */
	public AffineMapper(Matrix4f transformMatrix) {
		if ( transformMatrix != null ) {
			this.transformMatrix = transformMatrix;
		}
		else {
			throw new NullPointerException();
		}
	}
	
	/**
	 * @post Especifica la matriz de transformación
	 */
	public void setTransformMatrix(final Matrix4f transformMatrix) {
		if ( transformMatrix != null ) {
			this.observerManager.notifyObservers(new ElementCallback<AffineMapperObserver>(){

				@Override
				public void run(AffineMapperObserver element) {
					element.registerTransformMatrixChange(transformMatrix);
				}
				
			});
			this.transformMatrix = transformMatrix;
		}
		else {
			throw new NullPointerException();
		}
	}
	
	/**
	 * @post Devuelve la matriz de transformación
	 */
	public Matrix4f getTransformMatrix() {
		return this.transformMatrix;
	}

	/* (non-Javadoc)
	 * @see com.esferixis.gameengine.renderengine.mesh.mapper.RenderingMapper#transform(com.esferixis.math.Vector3f)
	 */
	@Override
	public Vector3f transform(Vector3f original) {
		if ( original != null ) {
			return this.transformMatrix.transformPoint(original);
		}
		else {
			throw new NullPointerException();
		}
	}

	/* (non-Javadoc)
	 * @see com.esferixis.gameengine.renderengine.mesh.mapper.RenderingMapper#accept(com.esferixis.gameengine.renderengine.mesh.mapper.RenderingMapper.Visitor)
	 */
	@Override
	public <V> V accept(
			com.esferixis.gameengine.physics.space.statics.SpatialMapper.Visitor<V> visitor) {
		return visitor.visit(this);
	}
}
