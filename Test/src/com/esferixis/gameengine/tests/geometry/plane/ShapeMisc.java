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

package com.esferixis.gameengine.tests.geometry.plane;

import java.util.Random;

import com.esferixis.geometry.plane.finite.Circle;
import com.esferixis.geometry.plane.finite.Circumference;
import com.esferixis.geometry.plane.finite.CircumferenceSegment;
import com.esferixis.geometry.plane.finite.ConvexPolygon;
import com.esferixis.geometry.plane.finite.Curve;
import com.esferixis.geometry.plane.finite.FiniteAffineHolomorphicShape;
import com.esferixis.geometry.plane.finite.FiniteProportionalHolomorphicShape;
import com.esferixis.geometry.plane.finite.FiniteProportionalHolomorphicShapeGroup;
import com.esferixis.geometry.plane.finite.LineSegment;
import com.esferixis.geometry.plane.finite.Point;
import com.esferixis.geometry.plane.finite.SolidCapsule;
import com.esferixis.math.ExtraMath;
import com.esferixis.math.Vector2f;

public class ShapeMisc {
	private ShapeMisc() {}
	
	public static Vector2f getRandomPoint(FiniteProportionalHolomorphicShape<?> proportionalHolomorphicShape) {
		final Random rng = new Random();
		
		return proportionalHolomorphicShape.accept(new FiniteProportionalHolomorphicShape.Visitor<Vector2f, RuntimeException>() {

			@Override
			public Vector2f visit(Point point) {
				return point.getPosition();
			}
			
			private Vector2f getRandomPoint(Curve<?> curve) {
				final Curve.Parametrization parametrization = curve.getParametrization();
				return parametrization.getPoint(parametrization.getParameterInterval().linearInterpolation((float) Math.random()));
			}

			@Override
			public Vector2f visit(LineSegment line) {
				return this.getRandomPoint(line);
			}

			@Override
			public Vector2f visit(Circumference circumference) {
				return this.getRandomPoint(circumference);
			}

			@Override
			public Vector2f visit(CircumferenceSegment circumferenceSegment) {
				return this.getRandomPoint(circumferenceSegment);
			}

			@Override
			public Vector2f visit(Circle circle) {
				return ExtraMath.linearInterpolation( this.getRandomPoint(circle.getPerimeter()), circle.getPerimeter().getCenter(), (float) Math.random() );
			}

			@Override
			public Vector2f visit(SolidCapsule capsule) {
				return ExtraMath.linearInterpolation(capsule.getPerimeter().accept(this), capsule.getCenterLine().getCenter(), (float) Math.random() );
			}

			@Override
			public Vector2f visit(ConvexPolygon convexPolygon) {
				return ExtraMath.linearInterpolation(convexPolygon.getPerimeter().accept(this), convexPolygon.getCenter(), (float) Math.random() );
			}

			@Override
			public <S extends FiniteProportionalHolomorphicShape<?>> Vector2f visit(
					FiniteProportionalHolomorphicShapeGroup<S> proportionalHolomorphicShapeGroup) throws RuntimeException {
				return proportionalHolomorphicShapeGroup.getShapes().get(rng.nextInt(proportionalHolomorphicShapeGroup.getShapes().size())).accept(this);
			}

			@Override
			public <S extends FiniteProportionalHolomorphicShape<S>> Vector2f visit(
					FiniteAffineHolomorphicShape<S> finiteAffineHolomorphicShape) throws RuntimeException {
				return finiteAffineHolomorphicShape.getBackingShape().accept(this);
			}
			
		});
	}
}
