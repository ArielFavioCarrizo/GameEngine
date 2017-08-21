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

package com.esferixis.gameengine.renderengine.space.camera;

import com.esferixis.math.Matrix4f;
import com.esferixis.math.Vector3f;
import com.esferixis.math.intervalarithmetic.FloatClosedInterval;
import com.esferixis.misc.ElementCallback;
import com.esferixis.misc.observer.ObserverManager;
import com.esferixis.misc.observer.Observer.Type;

/**
 * Cámara que puede anclarse a un rectángulo
 * de renderización
 * 
 * @author Ariel Favio Carrizo
 *
 */
public final class Camera3d {
	/**
	 * Lente
	 */
	public final class Lens {
		private float fovAngle, aspectRatio;
		
		/**
		 * @post Crea el lente
		 */
		protected Lens() {
			this.fovAngle = (float) (60.0d / Math.PI);
			this.aspectRatio = 1.0f;
		}
		
		/**
		 * @post Especifica el ángulo de campo visual
		 */
		public void setFovAngle(float fovAngle) {
			this.fovAngle = fovAngle;
			Camera3d.this.registerLensChange();
		}
		
		/**
		 * @post Devuelve el ángulo de campo visual
		 */
		public float getFovAngle() {
			return this.fovAngle;
		}
		
		/**
		 * @post Especifica la relación de aspecto
		 */
		public void setAspectRatio(float aspectRatio) {
			this.aspectRatio = aspectRatio;
			Camera3d.this.registerLensChange();
		}
		
		/**
		 * @post Devuelve la relación de aspecto
		 */
		public float getAspectRatio() {
			return this.aspectRatio;
		}
		
		/**
		 * @pre El intervalo de clipping Z no puede ser nulo
		 * @post Devuelve la matriz de transformación dado el intervalo de clipping
		 * 		 especificado
		 */
		public Matrix4f getProjectionMatrix(FloatClosedInterval zClippingInterval) {
			float y_scale = (float) (1.0d / Math.tan(this.getFovAngle() / 2.0d));
			float x_scale = y_scale / this.getAspectRatio();
			
			return new Matrix4f(new float[][] {
				new float[]{ x_scale, 0.0f, 0.0f, 0.0f },
				new float[]{ 0.0f, y_scale, 0.0f, 0.0f },
				new float[]{ 0.0f, 0.0f, -(zClippingInterval.getMax() + zClippingInterval.getMin()) / zClippingInterval.length(), -(2.0f * zClippingInterval.getMin() * zClippingInterval.getMax()) / zClippingInterval.length()},
				new float[]{ 0.0f, 0.0f, -1.0f, 0.0f }
				
			}).transpose();
		}
	}
	
	final ObserverManager<Camera3d, Camera3dObserver> observerManager = new ObserverManager<Camera3d, Camera3dObserver>(this, Camera3dObserver.class);
	
	private final Lens lens;
	private Vector3f position, centerFocus, up;
	
	/**
	 * @post Crea la cámara
	 */
	public Camera3d() {
		this.lens = new Lens();
		this.position = Vector3f.ZERO;
		this.centerFocus = new Vector3f(0.0f, 0.0f, 1.0f);
		this.up = new Vector3f(0.0f, 1.0f, 0.0f);
	}
	
	private void registerCameraSpatialStateChange() {
		this.observerManager.notifyObservers(new ElementCallback<Camera3dObserver>() {

			@Override
			public void run(Camera3dObserver element) {
				element.registerCameraSpatialStateChange();
			}
			
		});
	}
	
	private void registerLensChange() {
		this.observerManager.notifyObservers(new ElementCallback<Camera3dObserver>() {

			@Override
			public void run(Camera3dObserver element) {
				element.registerLensChange();
			}
			
		});
	}
	
	/**
	 * @post Devuelve el lente
	 */
	public Lens getLens() {
		return this.lens;
	}
	
	/**
	 * @pre La posición no puede ser nula
	 * @post Especifica la posición de la cámara
	 */
	public void setPosition(Vector3f position) {
		if ( position != null ) {
			this.position = position;
			this.registerCameraSpatialStateChange();
		}
		else {
			throw new NullPointerException();
		}
	}
	
	/**
	 * @post Devuelve la posición de la cámara
	 */
	public Vector3f getPosition() {
		return this.position;
	}
	
	/**
	 * @pre El centro de enfoque no puede ser nulo
	 * @post Especifica el centro de enfoque
	 */
	public void setCenterFocus(Vector3f centerFocus) {
		if ( centerFocus != null ) {
			this.centerFocus = centerFocus;
			this.registerCameraSpatialStateChange();
		}
		else {
			throw new NullPointerException();
		}
	}
	
	/**
	 * @post Devuelve el centro de enfoque
	 */
	public Vector3f getCenterFocus() {
		return this.centerFocus;
	}
	
	/**
	 * @pre El vector no puede ser nulo
	 * @post Especifica el vector "arriba"
	 */
	public void setUp(Vector3f up) {
		if ( up != null ) {
			this.up = up;
			this.registerCameraSpatialStateChange();
		}
		else {
			throw new NullPointerException();
		}
	}
	
	/**
	 * @post Devuelve el vector "arriba"
	 */
	public Vector3f getUp() {
		return this.up;
	}
	
	/**
	 * @post Devuelve la matriz de vista
	 */
	public Matrix4f getViewMatrix() {
		Matrix4f matrix = Matrix4f.IDENTITY;
		matrix = matrix.translate(this.position.opposite());
		return matrix;
	}
}
