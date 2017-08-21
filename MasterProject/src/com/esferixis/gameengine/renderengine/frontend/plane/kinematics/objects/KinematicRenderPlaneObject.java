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
package com.esferixis.gameengine.renderengine.frontend.plane.kinematics.objects;

import java.io.Serializable;

import com.esferixis.gameengine.physics.plane.kinematics.mapper.PlaneKinematicMapper;
import com.esferixis.gameengine.physics.plane.statics.PlaneMapper;
import com.esferixis.gameengine.renderengine.frontend.plane.staticstage.StaticPlaneObjectRenderer;
import com.esferixis.geometry.plane.finite.FiniteAffineHolomorphicShape;
import com.esferixis.misc.ElementCallback;
import com.esferixis.misc.accesor.AccesorWhiteList;
import com.esferixis.misc.dynamicFields.DynamicFieldsContainer;

/**
 * Objeto cinemático de renderización de plano
 * 
 * @author ariel
 *
 */
public abstract class KinematicRenderPlaneObject implements Serializable {
	public interface Visitor<R, T> {
		public R visit(KinematicMeshObject2d kinematicMeshObject2d);
		public R visit(KinematicShapeObject kinematicShapeObject);
	}
	
	/**
	 * 
	 */
	private static final long serialVersionUID = -8865975355498869543L;
	
	private PlaneKinematicMapper kinematicMapper;
	
	private transient FiniteAffineHolomorphicShape<?> boundingShape = null;
	
	public static final class Accesor {
		private Accesor() {}
		
		@AccesorWhiteList
		private static final Class<?>[] packageAccesors = new Class[] {
			com.esferixis.gameengine.renderengine.frontend.plane.kinematics.PackageAccesors.class
		};
		
		/**
		 * @pre El objeto cinemático de renderización de plano y el renderizador
		 * 		no pueden ser nulos.
		 * 		El tiempo tiene que ser válido
		 * @post Renderiza el objeto con el renderizador y el instante de tiempo
		 * 		 especificado
		 */
		public void render(KinematicRenderPlaneObject kinematicRenderPlaneObject, StaticPlaneObjectRenderer objectRenderer, float time) {
			if ( ( kinematicRenderPlaneObject != null ) && ( objectRenderer != null ) ){
				kinematicRenderPlaneObject.render(objectRenderer, time);
			}
			else {
				throw new NullPointerException();
			}
		}
	}
	
	/**
	 * @post Crea el objeto con el mapeador cinemático especificado
	 */
	public KinematicRenderPlaneObject(PlaneKinematicMapper kinematicMapper) {
		this.kinematicMapper = kinematicMapper;
	}
	
	/**
	 * @post Devuelve el mapeador cinemático
	 */
	public PlaneKinematicMapper getKinematicMapper() {
		return this.kinematicMapper;
	}
	
	/**
	 * @post Especifica el mapeador cinemático
	 */
	public void setKinematicMapper(final PlaneKinematicMapper newKinematicMapper) {
		this.notifyObservers(new ElementCallback<KinematicRenderPlaneObjectObserver<? extends KinematicRenderPlaneObject>>() {

			@Override
			public void run(KinematicRenderPlaneObjectObserver<? extends KinematicRenderPlaneObject> observer) {
				observer.notifyKinematicMapperChange(newKinematicMapper);
			}
			
		});
		this.kinematicMapper = newKinematicMapper;
	}
	
	/**
	 * @pre El renderizador no puede ser nulo
	 * @post Renderiza el objeto con el renderizador estático y el instante de tiempo
	 * 		 especificado
	 * 		 Si no hay mapeador especificado, no hace nada
	 */
	protected final void render(StaticPlaneObjectRenderer objectRenderer, float time) {
		if ( objectRenderer != null ) {
			if ( this.kinematicMapper != null ) {
				this.render_internal(objectRenderer, this.kinematicMapper.instantPlaneMapper(time), time);
			}
		}
		else {
			throw new NullPointerException();
		}
	}
	
	/**
	 * @pre Asegura que el renderizador no puede ser nulo
	 * @post Renderiza el objeto con el renderizador, el mapeador de plano instantáneo, y el
	 * 		 instante de tiempo especificado
	 */
	protected abstract void render_internal(StaticPlaneObjectRenderer objectRenderer, PlaneMapper instantPlaneMapper, float time);
	
	/**
	 * @post Carga la figura envolvente
	 */
	protected abstract FiniteAffineHolomorphicShape<?> loadBoundingShape();
	
	/**
	 * @post Devuelve la figura envolvente
	 */
	public final FiniteAffineHolomorphicShape<?> getBoundingShape() {
		if ( this.boundingShape == null ) {
			this.boundingShape = this.loadBoundingShape();
		}
		
		return this.boundingShape;
	}
	
	/**
	 * @post Notifica a los observadores
	 */
	protected abstract void notifyObservers(ElementCallback< ? super KinematicRenderPlaneObjectObserver<?> > elementCallBack);
	
	/**
	 * @pre El visitor no puede ser nulo
	 * @post Visita con el visitor especificado
	 */
	public abstract <R, T extends Throwable> R accept(Visitor<R, T> visitor) throws T;
}
