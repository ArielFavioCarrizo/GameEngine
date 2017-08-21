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
package com.esferixis.gameengine.renderengine.frontend.plane.staticstage.shape;

import com.esferixis.gameengine.physics.plane.statics.PlaneMapper;
import com.esferixis.gameengine.renderengine.frontend.plane.staticstage.StaticPlaneObject;
import com.esferixis.gameengine.renderengine.plane.shape.ColorDistribution;
import com.esferixis.geometry.plane.Shape;
import com.esferixis.geometry.plane.finite.FiniteProportionalHolomorphicShape;

/**
 * Representa un objeto renderizable basado en una figura
 * 
 * @author ariel
 *
 */
public final class StaticShapeObject extends StaticPlaneObject {
	/**
	 * 
	 */
	private static final long serialVersionUID = -1721758070822815078L;
	
	private Shape<?> shape;
	
	private ColorDistribution colorDistribution;
	
	/**
	 * @pre Ninguno parámetro puede ser nulo
	 * @post Crea el objeto con la figura, la distribución de color
	 * 		 y el mapeo especificados
	 * 		 especificados
	 */
	public StaticShapeObject(Shape<?> shape, ColorDistribution colorDistribution, PlaneMapper planeMapper) {
		super(planeMapper);
		this.setShape(shape);
		this.setColorDistribution(colorDistribution);
	}
	
	/**
	 * @pre La figura no puede ser nula
	 * @post Especifica la figura
	 */
	public void setShape(final Shape<?> shape) {
		if ( shape != null ) {
			this.shape = shape;
		}
		else {
			throw new NullPointerException();
		}
	}
	
	/**
	 * @post Devuelve la figura
	 */
	public Shape<?> getShape() {
		return this.shape;
	}
	
	/**
	 * @post Especifica la distribución de color
	 */
	public void setColorDistribution(final ColorDistribution colorDistribution) {
		if ( colorDistribution != null ) {
			this.colorDistribution = colorDistribution;
		}
		else {
			throw new NullPointerException();
		}
	}
	
	/**
	 * @post Devuelve la distribución de color
	 */
	public ColorDistribution getColorDistribution() {
		return this.colorDistribution;
	}

	/* (non-Javadoc)
	 * @see com.esferixis.gameengine.renderengine.frontend.plane.PlaneObject#accept(com.esferixis.gameengine.renderengine.frontend.plane.PlaneObject.Visitor)
	 */
	@Override
	public <R, T extends Throwable> R accept(Visitor<R, T> visitor) throws T {
		return visitor.visit(this);
	}

	/* (non-Javadoc)
	 * @see com.arielcarrizo.gameengine.renderengine.frontend.plane.staticstage.StaticPlaneObject#cloneWithNewMapper(com.arielcarrizo.gameengine.physics.plane.statics.PlaneMapper)
	 */
	@Override
	protected StaticPlaneObject cloneWithNewMapper(PlaneMapper newPlaneMapper) {
		return new StaticShapeObject(this.getShape(), this.getColorDistribution(), newPlaneMapper);
	}
}
