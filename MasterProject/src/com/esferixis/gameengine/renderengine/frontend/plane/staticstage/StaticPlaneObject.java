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
package com.esferixis.gameengine.renderengine.frontend.plane.staticstage;

import java.io.Serializable;

import com.esferixis.gameengine.physics.plane.statics.PlaneMapper;
import com.esferixis.gameengine.physics.plane.statics.TransformedMapper2d;
import com.esferixis.gameengine.renderengine.frontend.plane.staticstage.mesh.StaticMeshObject2d;
import com.esferixis.gameengine.renderengine.frontend.plane.staticstage.shape.StaticShapeObject;

/**
 * Objeto de plano
 * 
 * Si no tiene mapeador cinemático, el objeto se considera desactivado
 * 
 * @author ariel
 *
 */
public abstract class StaticPlaneObject implements Serializable {
	private static final long serialVersionUID = 4541166232934430902L;
	
	public static interface Visitor<R, T extends Throwable> {
		public R visit(StaticMeshObject2d meshObject2d) throws T;
		public R visit(StaticShapeObject shapeObject) throws T;
	}
	
	private PlaneMapper planeMapper;
	
	/**
	 * @post Crea el objeto de plano con el mapeador
	 * 		 especificado
	 */
	public StaticPlaneObject(PlaneMapper planeMapper) {
		this.setMapper(planeMapper);
	}
	
	/**
	 * @post Devuelve el mapeador
	 */
	public PlaneMapper getMapper() {
		return this.planeMapper;
	}
	
	/**
	 * @post Especifica el mapeador
	 */
	public void setMapper(final PlaneMapper planeMapper) {
		if ( planeMapper != null ) {
			this.planeMapper = planeMapper;
		}
		else {
			throw new NullPointerException();
		}
	}
	
	/**
	 * @pre El mapeador de plano de transformación no puede ser nulo
	 * @post Devuelve un clon transformado del objeto con el mapeador
	 * 		 especificado
	 */
	public final StaticPlaneObject getTransformedPlaneObjectClone(PlaneMapper transformerPlaneMapper) {
		if ( transformerPlaneMapper != null ) {
			return this.cloneWithNewMapper(new TransformedMapper2d(this.getMapper(), transformerPlaneMapper));
		}
		else {
			throw new NullPointerException();
		}
	}
	
	/**
	 * @pre Se asegura que el mapeador de plano no es nulo
	 * @post Devuelve una clonación transformada del objeto con el mapeador
	 * 		 especificado
	 */
	protected abstract StaticPlaneObject cloneWithNewMapper(PlaneMapper newPlaneMapper);
	
	/**
	 * @post Procesa el objeto con el visitor especificado
	 */
	public abstract <R, T extends Throwable> R accept(Visitor<R, T> visitor) throws T;
}
