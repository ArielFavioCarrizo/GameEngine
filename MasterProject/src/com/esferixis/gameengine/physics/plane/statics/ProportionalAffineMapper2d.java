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
package com.esferixis.gameengine.physics.plane.statics;

import com.esferixis.geometry.plane.finite.FiniteProportionalHolomorphicShape;
import com.esferixis.math.Matrix3f;
import com.esferixis.math.ProportionalMatrix3f;
import com.esferixis.math.Vector2f;
import com.esferixis.misc.ElementCallback;
import com.esferixis.misc.observer.ObserverManager;

/**
 * @author ariel
 *
 */
public final class ProportionalAffineMapper2d extends ProportionalPlaneMapper {
	/**
	 * 
	 */
	private static final long serialVersionUID = 414466704094920981L;
	private ProportionalMatrix3f transformMatrix;
	ObserverManager<ProportionalAffineMapper2d, ProportionalAffineMapper2dObserver> observerManager = new ObserverManager<ProportionalAffineMapper2d, ProportionalAffineMapper2dObserver>(this, ProportionalAffineMapper2dObserver.class);
	
	/**
	 * @post Crea el mapeador afin
	 */
	public ProportionalAffineMapper2d() {
		this.transformMatrix = Matrix3f.IDENTITY;
	}
	
	/**
	 * @post Crea el mapeador afín con la matriz especificada
	 */
	public ProportionalAffineMapper2d(ProportionalMatrix3f transformMatrix) {
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
	public void setTransformMatrix(final ProportionalMatrix3f transformMatrix) {
		if ( transformMatrix != null ) {
			this.observerManager.notifyObservers(new ElementCallback<ProportionalAffineMapper2dObserver>(){

				@Override
				public void run(ProportionalAffineMapper2dObserver element) {
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
	public ProportionalMatrix3f getTransformMatrix() {
		return this.transformMatrix;
	}

	/* (non-Javadoc)
	 * @see com.esferixis.gameengine.renderengine.mesh.mapper.RenderingMapper#transform(com.esferixis.math.Vector3f)
	 */
	@Override
	public Vector2f transform(Vector2f original) {
		if ( original != null ) {
			return this.transformMatrix.transformPoint(original);
		}
		else {
			throw new NullPointerException();
		}
	}

	/* (non-Javadoc)
	 * @see com.esferixis.gameengine.physics.plane.statics.PlaneMapper#accept(com.esferixis.gameengine.physics.plane.statics.PlaneMapper.Visitor)
	 */
	@Override
	public <V, T extends Throwable> V accept(Visitor<V, T> visitor) throws T {
		return visitor.visit(this);
	}

	/* (non-Javadoc)
	 * @see com.arielcarrizo.gameengine.physics.plane.statics.PlaneMapper#transform(com.arielcarrizo.geometry.plane.ProportionalHolomorphicShape)
	 */
	@Override
	public FiniteProportionalHolomorphicShape<?> transform(FiniteProportionalHolomorphicShape<?> shape) {
		if ( shape != null ) {
			return shape.transform(this.transformMatrix);
		}
		else {
			throw new NullPointerException();
		}
	}
}
