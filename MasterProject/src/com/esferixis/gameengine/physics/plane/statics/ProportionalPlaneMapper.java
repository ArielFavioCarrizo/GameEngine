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

import com.esferixis.geometry.plane.finite.FiniteProportionalHolomorphicShape;

/**
 * @author ariel
 *
 */
public abstract class ProportionalPlaneMapper extends PlaneMapper {
	public static interface Visitor<V, T extends Throwable> {
		public V visit(ProportionalAffineMapper2d affineMapper) throws T;
		public V visit(IdentityMapper2d identityMapper) throws T;
		public V visit(ProportionalTransformedMapper2d transformedMapper) throws T;
	}
	
	/**
	 * 
	 */
	private static final long serialVersionUID = 8023434444204135342L;

	public final <V, T extends Throwable> V accept(final PlaneMapper.Visitor<V, T> visitor) throws T {
		return this.accept(new Visitor<V, T>() {

			@Override
			public V visit(ProportionalAffineMapper2d affineMapper) throws T {
				return visitor.visit(affineMapper);
			}

			@Override
			public V visit(IdentityMapper2d identityMapper) throws T {
				return visitor.visit(identityMapper);
			}

			@Override
			public V visit(ProportionalTransformedMapper2d transformedMapper) throws T {
				return visitor.visit(transformedMapper);
			}
			
		});
	}
	
	
	/**
	 * @post Devuelve la transformación de la figura especificada
	 */
	public abstract FiniteProportionalHolomorphicShape<?> transform(FiniteProportionalHolomorphicShape<?> shape);
	
	/* (non-Javadoc)
	 * @see com.esferixis.gameengine.renderengine.mesh.mapper.RenderingMapper#accept(com.esferixis.gameengine.renderengine.mesh.mapper.RenderingMapper.Visitor)
	 */
	public abstract <V, T extends Throwable> V accept(Visitor<V, T> visitor) throws T;
}
