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

import com.esferixis.gameengine.physics.misc.statics.GeometryMapper;
import com.esferixis.gameengine.physics.misc.statics.MappedObject;
import com.esferixis.math.Vectorf;

/**
 * @author ariel
 *
 */
public abstract class MeshObject<V extends Vectorf, D extends Mesh.Data<V, ?>, M extends GeometryMapper<V>> extends MappedObject<V, M> {
	private Mesh<V, D> mesh;
	
	/**
	 * @pre La malla y tampoco el mapeador espacial
	 * 		no pueden ser nulos
	 * @post Crea el objeto de malla con la malla
	 * 		 y el mapeador espacial especificados
	 */
	public MeshObject(Mesh<V, D> mesh, M mapper) {
		super(mapper);
		this.setMesh(mesh);
	}
	
	/**
	 * @pre La malla no puede ser nula
	 * @post Especifica la malla
	 */
	public void setMesh(Mesh<V, D> mesh) {
		if ( mesh != null ) {
			this.mesh = mesh;
		}
		else {
			throw new NullPointerException();
		}
	}
	
	/**
	 * @post Devuelve la malla
	 */
	public Mesh<V, D> getMesh() {
		return this.mesh;
	}
}
