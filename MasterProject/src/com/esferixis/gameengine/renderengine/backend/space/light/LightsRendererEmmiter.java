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
package com.esferixis.gameengine.renderengine.backend.space.light;

import com.esferixis.math.Matrix4f;
import com.esferixis.math.intervalarithmetic.FloatClosedInterval;

/**
 * @author ariel
 *
 */
public abstract class LightsRendererEmmiter {
	/**
	 * @pre La matriz de transformación y proyección de vista, los planos de clipping Z ni el renderizador de luces pueden ser nulos
	 * 		El renderizador tiene que ser nuevo.
	 * @post Renderiza las luces
	 */
	public final void renderLights(Matrix4f projectedViewTransformMatrix, FloatClosedInterval zClippingPlanes, LightSourceRenderer lightSourceRenderer) throws NullPointerException, IllegalArgumentException {
		if ( ( projectedViewTransformMatrix != null ) && ( zClippingPlanes != null ) && ( lightSourceRenderer != null ) ) {
			ProtectedLightSourceRenderer protectedLightSourceRenderer = new ProtectedLightSourceRenderer(lightSourceRenderer);
			this.renderLights_internal(projectedViewTransformMatrix, zClippingPlanes, protectedLightSourceRenderer);
			protectedLightSourceRenderer.exitContext();
		}
		else {
			throw new NullPointerException();
		}
	}
		
	/**
	 * @pre La matriz de transformación y proyección de vista, los planos de clipping Z ni el renderizador de luces pueden ser nulos
	 * @post Renderiza las luces
	 */
	protected abstract void renderLights_internal(Matrix4f projectedViewTransformMatrix, FloatClosedInterval zClippingPlanes, LightSourceRenderer lightSourceRenderer) throws NullPointerException, IllegalArgumentException;
}
