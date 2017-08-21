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
package com.esferixis.gameengine.renderengine.backend.space.mesh;

import com.esferixis.gameengine.renderengine.backend.misc.mesh.Mesh;
import com.esferixis.math.Vector3f;

/**
 * @author ariel
 *
 */
public final class ProtectedGeometryRenderer3d<D extends Mesh.Data<Vector3f, ?>> extends CheckedGeometryRenderer3d<D> {
	private boolean onContext;
	
	/**
	 * @pre El renderizador de geometría no puede ser nulo
	 * @post Crea una visión protegida del renderizador de geometría especificado
	 * @param geometryRenderer
	 */
	public ProtectedGeometryRenderer3d(GeometryRenderer3d<D> geometryRenderer) {
		super(geometryRenderer);
		this.onContext = true;
	}
	
	/**
	 * @post Verifica el contexto
	 */
	protected void checkContext() {
		if ( !this.onContext ) {
			throw new IllegalStateException("Invalid context");
		}
	}
	
	/**
	 * @pre La visión del renderizador de geometría tiene que estar
	 * 		en un contexto válido
	 * @post Sale del contexto
	 */
	public void exitContext() {
		this.onContext = false;
	}
}
