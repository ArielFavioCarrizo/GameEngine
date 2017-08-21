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
package com.esferixis.gameengine.renderengine.frontend.plane.kinematics.containers;

import com.esferixis.gameengine.renderengine.frontend.plane.kinematics.InstantKinematicRenderer;
import com.esferixis.geometry.plane.finite.FiniteAffineHolomorphicShape;

/**
 * @author ariel
 *
 */
public abstract class RenderizableKinematicRenderPlaneObjectContainer extends KinematicRenderPlaneObjectContainer {
	/**
	 * 
	 */
	private static final long serialVersionUID = 8985430018341165419L;

	/**
	 * @pre El renderizador cinemático instantáneo y la figura de área de intersección
	 * 		no pueden ser nulos.
	 * 
	 * @post Renderiza con el renderizador cinemático instantáneo y la figura de área
	 * 		 interseccionante especificada
	 * 		 Asegura que todo lo que entre en intersección con la figura de área será
	 * 		 renderizado.
	 * 		 La renderización de lo que no entre en intersección con la figura de área
	 * 		 es indeterminada.
	 */
	public final void render(InstantKinematicRenderer instantKinematicRenderer, FiniteAffineHolomorphicShape<?> intersectingAreaShape) {
		if ( ( instantKinematicRenderer != null ) && ( intersectingAreaShape != null ) ) {
			this.render_checked(instantKinematicRenderer, intersectingAreaShape);
		}
		else {
			throw new NullPointerException();
		}
	}
	
	/**
	 * @pre Se asegura que el renderizador cinemático instantáneo y la figura de área
	 * 		de intersección no son nulos.
	 * 
	 * @post Renderiza con el renderizador cinemático instantáneo y la figura de área
	 * 		 interseccionante especificada
	 * 		 Asegura que todo lo que entre en intersección con la figura de área será
	 * 		 renderizado.
	 * 		 La renderización de lo que no entre en intersección con la figura de área
	 * 		 es indeterminada.
	 */
	protected abstract void render_checked(InstantKinematicRenderer instantKinematicRenderer, FiniteAffineHolomorphicShape<?> intersectingAreaShape);
}
