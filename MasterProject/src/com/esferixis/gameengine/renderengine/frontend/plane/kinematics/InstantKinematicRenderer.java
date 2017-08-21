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
package com.esferixis.gameengine.renderengine.frontend.plane.kinematics;

import com.esferixis.gameengine.physics.plane.statics.PlaneMapper;
import com.esferixis.gameengine.renderengine.frontend.plane.kinematics.objects.KinematicRenderPlaneObject;
import com.esferixis.gameengine.renderengine.frontend.plane.staticstage.StaticPlaneObject;
import com.esferixis.gameengine.renderengine.frontend.plane.staticstage.StaticPlaneObjectRenderer;
import com.esferixis.misc.accesor.AccesorWhiteList;

/**
 * @author ariel
 *
 */
public final class InstantKinematicRenderer {
	private final StaticPlaneObjectRenderer staticPlaneObjectRenderer;
	private final float time;
	
	public static final class Accesor {
		private Accesor() {}
		
		@AccesorWhiteList
		private static final Class<?>[] packageAccesors = new Class[] {
			com.esferixis.gameengine.renderengine.frontend.plane.kinematics.PackageAccesors.class
		};
		
		/**
		 * @post Devuelve el renderizador estático del renderizador cinemático
		 * 		 instantáneo de objetos especificado
		 */
		public StaticPlaneObjectRenderer getStaticPlaneObjectRenderer(InstantKinematicRenderer instantKinematicRenderer) {
			if ( instantKinematicRenderer != null ) {
				return instantKinematicRenderer.getStaticPlaneObjectRenderer();
			}
			else {
				throw new NullPointerException();
			}
		}
	}
	
	/**
	 * @pre El renderizador estático no puede ser nulo y el instante de tiempo
	 * 	 	tiene que ser válido
	 * @post Crea el renderizador con el renderizador estático y el instante
	 * 		 de tiempo especificados
	 */
	public InstantKinematicRenderer(StaticPlaneObjectRenderer staticPlaneObjectRenderer, float time) {
		if ( staticPlaneObjectRenderer != null ) {
			this.staticPlaneObjectRenderer = staticPlaneObjectRenderer;
			this.time = time;
		}
		else {
			throw new NullPointerException();
		}
	}
	
	/**
	 * @pre El objeto no puede ser nulo, y el contexto tiene que ser válido
	 * @post Renderiza el objeto especificado
	 */
	public void render(KinematicRenderPlaneObject kinematicRenderPlaneObject) {
		if ( kinematicRenderPlaneObject != null ) {
			PackageAccesors.kinematicRenderPlaneObjectAccesor.get().render(kinematicRenderPlaneObject, this.staticPlaneObjectRenderer, this.time);
		}
		else {
			throw new NullPointerException();
		}
	}
	
	/**
	 * @post Devuelve el renderizador estático
	 */
	private StaticPlaneObjectRenderer getStaticPlaneObjectRenderer() {
		return this.staticPlaneObjectRenderer;
	}
	
	/**
	 * @post Devuelve el instante de tiempo
	 */
	public float getTime() {
		return this.time;
	}
	
	/**
	 * @pre El mapeador de plano no puede ser nulo
	 * @post Devuelve una transformación con el mapeador de plano
	 * 		 especificado
	 */
	public InstantKinematicRenderer transform(final PlaneMapper transformerPlaneMapper) {
		if ( transformerPlaneMapper != null ) {
			return new InstantKinematicRenderer(new StaticPlaneObjectRenderer() {

				@Override
				public void render(StaticPlaneObject planeObject) throws NullPointerException {
					InstantKinematicRenderer.this.staticPlaneObjectRenderer.render(planeObject.getTransformedPlaneObjectClone(transformerPlaneMapper));
				}
				
			}, this.getTime());
		}
		else {
			throw new NullPointerException();
		}
	}
}
