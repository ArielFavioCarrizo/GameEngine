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
package com.esferixis.gameengine.renderengine.frontend.plane.kinematics.view;

import java.io.Serializable;

import com.esferixis.gameengine.renderengine.frontend.renderingFrame.RenderingFrame;
import com.esferixis.geometry.plane.finite.ConvexPolygon;
import com.esferixis.geometry.plane.finite.FiniteAffineHolomorphicShape;
import com.esferixis.geometry.plane.finite.FiniteProportionalHolomorphicShape;
import com.esferixis.math.Matrix3f;
import com.esferixis.math.ProportionalMatrix3f;
import com.esferixis.math.Vector2f;

/**
 * @author ariel
 *
 */
public final class PlaneCamera implements Serializable {
	/**
	 * 
	 */
	private static final long serialVersionUID = -6300567737961224178L;
	
	private Vector2f size;
	private Vector2f centerPosition;
	private float angle;
	
	/**
	 * @pre El vector de tamaño no puede ser nulo, y sus dos
	 * 		componentes tienen que ser positivos.
	 * 		Tampoco la posición del centro puede ser nula
	 * @post Crea la cámara con el vector de tamaño, la posición del centro
	 * 		 y el ángulo especificados
	 */
	public PlaneCamera(Vector2f size, Vector2f centerPosition, float angle) {
		this.setSize(size);
		this.setCenterPosition(centerPosition);
		this.setAngle(angle);
	}

	/**
	 * @pre El vector de tamaño no puede ser nulo, y sus dos
	 * 		componentes tienen que ser positivos
	 * @post Crea la cámara con el vector de tamaño especificado.
	 * 		 La posición es el origen de coordenadas, y el ángulo es cero
	 */
	public PlaneCamera(Vector2f size) {
		this(size, Vector2f.ZERO, 0.0f);
	}
	
	/**
	 * @post Especifica el tamaño
	 */
	public void setSize(Vector2f size) {
		if ( size != null ) {
			if ( ( size.getX() > 0.0f ) && ( size.getY() > 0.0f ) ) {
				this.size = size;
			}
			else {
				throw new IllegalArgumentException("Expected positive size components");
			}
		}
		else {
			throw new NullPointerException();
		}
	}
	
	/**
	 * @post Devuelve el tamaño
	 */
	public Vector2f getSize() {
		return this.size;
	}
	
	/**
	 * @post Devuelve la posición del centro
	 */
	public Vector2f getCenterPosition() {
		return this.centerPosition;
	}
	
	/**
	 * @pre La posición del centro no puede ser nula
	 * @post Especifica la posición del centro
	 */
	public void setCenterPosition(Vector2f centerPosition) {
		if ( centerPosition != null ) {
			this.centerPosition = centerPosition;
		}
		else {
			throw new NullPointerException();
		}
	}
	
	/**
	 * @post Devuelve el ángulo
	 */
	public float getAngle() {
		return this.angle;
	}
	
	/**
	 * @post Especifica el ángulo
	 */
	public void setAngle(float angle) {
		this.angle = angle;
	}
	
	/**
	 * @post Devuelve la matriz de transformación de la cámara
	 */
	Matrix3f getTransformationMatrix() {
		return Matrix3f.IDENTITY.translate(this.centerPosition.opposite()).rotate(-this.angle).scale(new Vector2f(1.0f / this.size.getX(), 1.0f / this.size.getY()));
	}
	
	/**
	 * @post Devuelve la figura que representa el área de visión de la cámara
	 */
	public FiniteAffineHolomorphicShape<ConvexPolygon> getVisionArea() {
		return RenderingFrame.SHAPE.castToAffine().transform(this.getTransformationMatrix().invert());
	}
}
