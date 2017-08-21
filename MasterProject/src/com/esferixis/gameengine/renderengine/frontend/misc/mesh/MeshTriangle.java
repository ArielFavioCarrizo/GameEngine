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
package com.esferixis.gameengine.renderengine.frontend.misc.mesh;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import com.esferixis.geometry.plane.finite.ConvexPolygon;
import com.esferixis.math.Vectorf;

/**
 * @author ariel
 *
 */
public class MeshTriangle<V extends Vectorf, P extends MeshTriangleVertex<V>> implements Serializable {
	/**
	 * 
	 */
	private static final long serialVersionUID = 2531707095864853824L;
	
	private final P p1, p2, p3;
	
	/**
	 * @pre Los puntos no pueden ser nulos
	 * @post Crea el triángulo con los vértices especificados, en el orden antihorario (Positivo)
	 */
	public MeshTriangle(P p1, P p2, P p3) {
		if ( ( p1 != null ) && ( p2 != null ) && ( p3 != null ) ) {
			this.p1 = p1;
			this.p2 = p2;
			this.p3 = p3;
		}
		else {
			throw new NullPointerException();
		}
	}
	
	/**
	 * @post Devuelve el primer punto
	 */
	public P getPoint1() {
		return this.p1;
	}
	
	/**
	 * @post Devuelve el segundo punto
	 */
	public P getPoint2() {
		return this.p2;
	}
	
	/**
	 * @post Devuelve el tercer punto
	 */
	public P getPoint3() {
		return this.p3;
	}
	
	/**
	 * @post Devuelve los tres puntos en una lista inmutable
	 */
	public List<P> getPoints() {
		List<P> points = new ArrayList<P>(3);
		points.add(this.getPoint1());
		points.add(this.getPoint2());
		points.add(this.getPoint3());
		
		return Collections.unmodifiableList(points);
	}
}
