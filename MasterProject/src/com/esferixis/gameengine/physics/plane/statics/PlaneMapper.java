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

import java.io.Serializable;

import com.esferixis.gameengine.physics.misc.statics.GeometryMapper;
import com.esferixis.geometry.plane.finite.FiniteProportionalHolomorphicShape;
import com.esferixis.math.Vector2f;

/**
 * Mapeador de rendering
 * 
 * @author ariel
 *
 */
public abstract class PlaneMapper extends GeometryMapper<Vector2f> implements Serializable {
	/**
	 * 
	 */
	private static final long serialVersionUID = -6046676667677190441L;

	public static interface Visitor<V, T extends Throwable> {
		public V visit(AffineMapper2d affineMapper) throws T;
		public V visit(ProportionalAffineMapper2d affineMapper) throws T;
		public V visit(IdentityMapper2d identityMapper) throws T;
		public V visit(TransformedMapper2d transformedMapper) throws T;
		public V visit(ProportionalTransformedMapper2d transformedMapper) throws T;
	}
	
	/**
	 * @post Crea el mapeador de rendering
	 */
	PlaneMapper() {
		
	}
	
	/**
	 * @post Devuelve la transformación del punto
	 * @param original
	 * @return
	 */
	public abstract Vector2f transform(Vector2f original);
	
	/**
	 * @post Visita el mapeador con el visitor especificado
	 * 		 y devuelve el resultado
	 */
	public abstract <V, T extends Throwable> V accept(Visitor<V, T> visitor) throws T;
}
