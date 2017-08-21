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

package com.esferixis.gameengine.physics.plane.kinematics.mapper.rotation;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import com.esferixis.gameengine.physics.plane.kinematics.mapper.PlaneKinematicMapper;
import com.esferixis.gameengine.physics.plane.statics.ProportionalAffineMapper2d;
import com.esferixis.gameengine.physics.plane.statics.ProportionalPlaneMapper;
import com.esferixis.geometry.Geometry;
import com.esferixis.geometry.plane.Line;
import com.esferixis.geometry.plane.finite.Circle;
import com.esferixis.geometry.plane.finite.Circumference;
import com.esferixis.geometry.plane.finite.CircumferenceSegment;
import com.esferixis.geometry.plane.finite.ConvexPolygon;
import com.esferixis.geometry.plane.finite.FiniteAffineHolomorphicShape;
import com.esferixis.geometry.plane.finite.FiniteProportionalHolomorphicShape;
import com.esferixis.geometry.plane.finite.FiniteProportionalHolomorphicShapeGroup;
import com.esferixis.geometry.plane.finite.LineSegment;
import com.esferixis.geometry.plane.finite.Point;
import com.esferixis.geometry.plane.finite.SolidCapsule;
import com.esferixis.math.ExtraMath;
import com.esferixis.math.Matrix3f;
import com.esferixis.math.Vector2f;
import com.esferixis.math.intervalarithmetic.FloatClosedInterval;
import com.esferixis.misc.ElementCallback;
import com.esferixis.misc.observer.ObserverManager;

/**
 * @author ariel
 *
 */
public final class RotationPlaneKinematicMapper<R extends Rotation> extends PlaneKinematicMapper {
	/**
	 * 
	 */
	private static final long serialVersionUID = 4729428828886432685L;
	
	private static final float doublePI = ExtraMath.doublePI;
	
	final ObserverManager<RotationPlaneKinematicMapper<R>, RotationPlaneKinematicMapperObserver<R>> observerManager = new ObserverManager<RotationPlaneKinematicMapper<R>, RotationPlaneKinematicMapperObserver<R>>(this, (Class) RotationPlaneKinematicMapperObserver.class);
	
	private R rotation;

	/**
	 * @pre La rotación no puede ser nula
	 * @post Crea el mapeador con la rotación especificada
	 */
	public RotationPlaneKinematicMapper(R rotation) {
		this.setRotation(rotation);
	}
	
	/**
	 * @post Devuelve la rotación
	 */
	public Rotation getRotation() {
		return this.rotation;
	}
	
	/**
	 * @pre La rotación no puede ser nula
	 * @post Especifica la rotación
	 */
	public void setRotation(final R rotation) {
		if ( rotation != null ) {
			this.observerManager.notifyObservers(new ElementCallback<RotationPlaneKinematicMapperObserver<R>>() {
	
				@Override
				public void run(final RotationPlaneKinematicMapperObserver<R> observer) {
					observer.notifyRotationChange(rotation);
				}
				
			});
			
			this.rotation = rotation;
		}
		else {
			throw new NullPointerException();
		}
	}
	
	/* (non-Javadoc)
	 * @see com.esferixis.gameengine.physics.plane.kinematics.KinematicMapper#instantPlaneMapper(float)
	 */
	@Override
	public ProportionalPlaneMapper instantPlaneMapper(float time) {
		return new ProportionalAffineMapper2d(Matrix3f.IDENTITY.rotate(this.rotation.getAngle(time)));
	}
	
	/* (non-Javadoc)
	 * @see com.esferixis.gameengine.physics.plane.kinematics.KinematicMapper#boundingRegion_internal(com.esferixis.geometry.plane.ProportionalHolomorphicShape, com.esferixis.math.intervalarithmetic.FloatClosedInterval)
	 */
	@Override
	protected FiniteProportionalHolomorphicShape<?> shapeWithBoundingPerimeter_internal(final FiniteProportionalHolomorphicShape<?> originalShape,
		final FloatClosedInterval timeInterval) {
		final FloatClosedInterval angleInterval = this.rotation.getAngleInterval(timeInterval);
		
		return originalShape.accept(new FiniteProportionalHolomorphicShape.Visitor<FiniteProportionalHolomorphicShape<?>, RuntimeException>() {
			/**
			 * @pre Ninguno de los vértices tienen que ser nulos
			 * @post Efectúa el proceso de forma genérica, con la figura y los vértices clave especificados
			 */
			private FiniteProportionalHolomorphicShape<?> calculate(FiniteProportionalHolomorphicShape<?> shape, List<Vector2f> keyVertices) {
				final Point zeroPoint = new Point(Vector2f.ZERO);
				
				if ( ( shape != null ) && ( keyVertices != null ) ) {
					final FiniteProportionalHolomorphicShape<?> result;
					
					if ( angleInterval.length() < doublePI ) {
						final FiniteProportionalHolomorphicShape<?>[] resultShapes = new FiniteProportionalHolomorphicShape[keyVertices.size()+2];
						
						resultShapes[0] = shape.transform(Matrix3f.IDENTITY.rotate(angleInterval.getMin()));
						resultShapes[1] = shape.transform(Matrix3f.IDENTITY.rotate(angleInterval.getMax()));
						
						int i = 2;
						for ( Vector2f eachVertex : keyVertices ) {
							final float eachRadius = eachVertex.length();
							final float eachAngle = eachVertex.getAngle();
							
							resultShapes[i++] = ( eachRadius > 0.0f ? new CircumferenceSegment( new Circumference(Vector2f.ZERO, eachRadius), angleInterval.add(eachAngle) ) : zeroPoint);
						}
						
						result = new FiniteProportionalHolomorphicShapeGroup<FiniteProportionalHolomorphicShape<?>>(resultShapes);
					}
					else {
						float minRadius = Float.POSITIVE_INFINITY;
						float maxRadius = 0.0f;
						
						for ( Vector2f eachVertex : keyVertices ) {
							float eachRadius = eachVertex.length();
							
							minRadius = Math.min(minRadius, eachRadius);
							maxRadius = Math.max(maxRadius, eachRadius);
						}
						
						FiniteProportionalHolomorphicShape<?> maxShape = ( maxRadius > 0.0f ? new Circumference(Vector2f.ZERO, maxRadius) : zeroPoint);
						
						if ( shape.contains(Vector2f.ZERO) ) {
							result = maxShape;
						}
						else {
							final FiniteProportionalHolomorphicShape<?>[] resultShapes = new FiniteProportionalHolomorphicShape[2];
							
							resultShapes[0] = ( minRadius > 0.0f ? new Circumference(Vector2f.ZERO, minRadius) : zeroPoint);
							resultShapes[1] = maxShape;
							
							result = new FiniteProportionalHolomorphicShapeGroup<FiniteProportionalHolomorphicShape<?>>(resultShapes);
						}
					}
					
					return result;
				}
				else {
					throw new NullPointerException();
				}
			}
			
			private FiniteProportionalHolomorphicShape<?> calculate(FiniteProportionalHolomorphicShape<?> shape, Vector2f... keyVertices) {
				return this.calculate(originalShape, Arrays.asList( keyVertices ) );
			}
			
			@Override
			public FiniteProportionalHolomorphicShape<?> visit(Point point) throws RuntimeException {
				final FiniteProportionalHolomorphicShape<?> result;
				final Circumference resultCircumference = new Circumference(Vector2f.ZERO, point.getPosition().length());
				
				if ( angleInterval.length() < doublePI ) {
					result = new CircumferenceSegment(resultCircumference, angleInterval.add(point.getPosition().getAngle()));
				}
				else {
					result = resultCircumference;
				}
				
				return result;
			}

			@Override
			public FiniteProportionalHolomorphicShape<?> visit(LineSegment lineSegment) throws RuntimeException {
				final Line perpendicularLine = new Line(Vector2f.ZERO, lineSegment.getVector12().rotate90AnticlockWise());
				final Float intersectionScalar = perpendicularLine.getRectIntersectionPoint(lineSegment.getRect());
				Vector2f minVertex = null;
				
				if ( intersectionScalar != null ) {
					if ( lineSegment.containsExcludingExtremes(intersectionScalar) ) {
						minVertex = lineSegment.getRect().getPointByProportionalScalar(intersectionScalar);
					}
				}
				
				if ( minVertex != null ) {
					return this.calculate(lineSegment, minVertex, lineSegment.getPoint1(), lineSegment.getPoint2());
				}
				else {
					return this.calculate(lineSegment, lineSegment.getPoint1(), lineSegment.getPoint2());
				}
			}

			@Override
			public FiniteProportionalHolomorphicShape<?> visit(Circumference circumference) throws RuntimeException {
				final Vector2f minRadiusVertex, maxRadiusVertex;
				final Vector2f unitCenterDirection = circumference.getCenter().normalise();
				final float centerRadius = circumference.getCenter().length();
				
				if ( centerRadius > circumference.getRadius() ) {
					minRadiusVertex = unitCenterDirection.scale(centerRadius-circumference.getRadius());
				}
				else {
					minRadiusVertex = null;
				}
				
				maxRadiusVertex = unitCenterDirection.scale(centerRadius+circumference.getRadius());
				
				return (minRadiusVertex != null ? this.calculate(circumference, minRadiusVertex, maxRadiusVertex) : this.calculate(circumference, maxRadiusVertex));
			}

			@Override
			public FiniteProportionalHolomorphicShape<?> visit(CircumferenceSegment circumferenceSegment)
					throws RuntimeException {
				
				final float centerAngle = circumferenceSegment.getCircumference().getCenter().getAngle();
				final float centerOppositeAngle = centerAngle + (float) Math.PI;
				
				final FloatClosedInterval shapeAngleInterval = circumferenceSegment.getAngleInterval();
				
				final List<Vector2f> vertices = new ArrayList<Vector2f>(4);
				
				vertices.add(circumferenceSegment.getCircumference().getPointWithAngle(shapeAngleInterval.getMin()));
				vertices.add(circumferenceSegment.getCircumference().getPointWithAngle(shapeAngleInterval.getMax()));
				
				if ( Geometry.containsAngleExcludingExtremes(shapeAngleInterval, centerOppositeAngle ) ) {
					vertices.add( circumferenceSegment.getCircumference().getPointWithAngle(centerOppositeAngle) );
				}
				
				if ( Geometry.containsAngleExcludingExtremes(shapeAngleInterval, centerAngle ) ) {
					vertices.add( circumferenceSegment.getCircumference().getPointWithAngle(centerAngle) );
				}
				
				return this.calculate(circumferenceSegment, vertices);
			}

			@Override
			public FiniteProportionalHolomorphicShape<?> visit(Circle circle) throws RuntimeException {
				return this.visit(circle.getPerimeter());
			}

			@Override
			public FiniteProportionalHolomorphicShape<?> visit(SolidCapsule capsule) throws RuntimeException {
				return RotationPlaneKinematicMapper.this.shapeWithBoundingPerimeter(capsule.getPerimeter(), timeInterval);
			}
			
			@Override
			public FiniteProportionalHolomorphicShape<?> visit(ConvexPolygon convexPolygon) throws RuntimeException {
				Vector2f minVertex = null;
				
				if ( !convexPolygon.contains(Vector2f.ZERO) ) {
					float minVertexLength = Float.POSITIVE_INFINITY;
					
					for ( LineSegment eachLineSegment : convexPolygon.getPerimetralLines() ) {
						final Line perpendicularLine = new Line(Vector2f.ZERO, eachLineSegment.getVector12().rotate90AnticlockWise());
						final Float intersectionScalar = perpendicularLine.getRectIntersectionPoint(eachLineSegment.getRect());
						
						if ( intersectionScalar != null ) {
							if ( eachLineSegment.containsExcludingExtremes(intersectionScalar) ) {
								final Vector2f eachVertex = eachLineSegment.getRect().getPointByProportionalScalar(intersectionScalar);
								final float eachVertexLength = eachVertex.length();
								
								if ( eachVertexLength < minVertexLength ) {
									minVertex = eachVertex;
									minVertexLength = eachVertexLength;
								}
							}
						}
					}
				}
				
				List<Vector2f> vertices = new ArrayList<Vector2f>(convexPolygon.getVertices().size()+1);
				vertices.addAll(convexPolygon.getVertices());
				
				if ( minVertex != null ) {
					vertices.add(minVertex);
				}
				
				return this.calculate(convexPolygon, vertices);
			}

			@Override
			public <S extends FiniteProportionalHolomorphicShape<?>> FiniteProportionalHolomorphicShape<?> visit(
					FiniteProportionalHolomorphicShapeGroup<S> proportionalHolomorphicShapeGroup)
					throws RuntimeException {
				final List<FiniteProportionalHolomorphicShape<?>> result = new ArrayList<FiniteProportionalHolomorphicShape<?>>(proportionalHolomorphicShapeGroup.getShapes().size());
				
				for ( S eachShape : proportionalHolomorphicShapeGroup.getShapes() ) {
					result.add( RotationPlaneKinematicMapper.this.shapeWithBoundingPerimeter(eachShape, timeInterval) );
				}
				
				return new FiniteProportionalHolomorphicShapeGroup<FiniteProportionalHolomorphicShape<?>>(result);
			}

			@Override
			public <S extends FiniteProportionalHolomorphicShape<S>> FiniteProportionalHolomorphicShape<?> visit(
					FiniteAffineHolomorphicShape<S> finiteAffineHolomorphicShape) throws RuntimeException {
				return finiteAffineHolomorphicShape.getBackingShape().accept(this);
			}
			
		});
	}

	/* (non-Javadoc)
	 * @see com.arielcarrizo.gameengine.physics.plane.kinematics.KinematicMapper#boundingRegion_internal(com.arielcarrizo.geometry.plane.ProportionalHolomorphicShape, com.arielcarrizo.math.intervalarithmetic.FloatClosedInterval)
	 */
	@Override
	protected FiniteProportionalHolomorphicShape<?> boundingRegion_internal(final FiniteProportionalHolomorphicShape<?> originalShape,
			final FloatClosedInterval timeInterval) {
		final FloatClosedInterval angleInterval = this.rotation.getAngleInterval(timeInterval);
		
		return originalShape.getBoundingAffineHolomorphicShape().accept(new FiniteAffineHolomorphicShape.Visitor<FiniteProportionalHolomorphicShape<?>, RuntimeException>(){

			@Override
			public FiniteProportionalHolomorphicShape<?> visitPoint(FiniteAffineHolomorphicShape<Point> point)
					throws RuntimeException {
				return new CircumferenceSegment(new Circumference(Vector2f.ZERO, point.getBackingShape().getPosition().length()), angleInterval.add(point.getBackingShape().getPosition().getAngle()));
			}

			@Override
			public FiniteProportionalHolomorphicShape<?> visitLineSegment(FiniteAffineHolomorphicShape<LineSegment> affineLineSegment)
					throws RuntimeException {
				final LineSegment lineSegment = affineLineSegment.getBackingShape();
				
				/**
				 * Los vértices del interior y exterior, con el mismo índice, tienen el mismo
				 * desplazamiento de ángulo
				 */
				
				final FiniteProportionalHolomorphicShape<?>[] boundingShapes;
				
				if ( angleInterval.length() < doublePI ) {
					
					float[] tangentAnglesDelta = Geometry.partialAngleIntervalSampling(angleInterval);
					Vector2f minVertex1=null, maxVertex1=null;
					Vector2f minVertex2, maxVertex2;
					
					Vector2f unitMaxVertex1=null, unitMaxVertex2;
					
					final float angleBase1 = lineSegment.getPoint1().getAngle();
					final float angleBase2 = lineSegment.getPoint2().getAngle();
					final float radius1 = lineSegment.getPoint1().length();
					final float radius2 = lineSegment.getPoint2().length();
					
					boundingShapes = new FiniteProportionalHolomorphicShape[tangentAnglesDelta.length-1];
					
					for ( int i = 0 ; i<tangentAnglesDelta.length; i++ ) {
						{
							float eachAngleDelta = tangentAnglesDelta[i];
							minVertex2 = Vector2f.getUnitVectorWithAngle(angleBase1+eachAngleDelta).scale(radius1);
							
							unitMaxVertex2 = Vector2f.getUnitVectorWithAngle(angleBase2+eachAngleDelta);
							maxVertex2 = unitMaxVertex2.scale(radius2);
						}
						
						if ( i > 0 ) {
							final Line line1 = new Line(maxVertex1, unitMaxVertex1.rotate90AnticlockWise());
							final Line line2 = new Line(maxVertex2, unitMaxVertex2.rotate90ClockWise());
							
							boundingShapes[i-1] = new ConvexPolygon(
								minVertex1, maxVertex1,
								line1.getPointByProportionalScalar(line1.getRectIntersectionPoint(line2)),
								maxVertex2, minVertex2
							);
						}
						
						minVertex1 = minVertex2;
						unitMaxVertex1 = unitMaxVertex2;
						maxVertex1 = maxVertex2;
					}
				}
				else {
					float minRadius, maxRadius;
					
					if ( lineSegment.getPoint1().length() < lineSegment.getPoint2().length() ) {
						minRadius = lineSegment.getPoint1().length();
						maxRadius = lineSegment.getPoint2().length();
					}
					else {
						minRadius = lineSegment.getPoint2().length();
						maxRadius = lineSegment.getPoint1().length();
					}
					
					Vector2f[] points = new Vector2f[]{
						new Vector2f(minRadius, 0.0f), new Vector2f(maxRadius, 0.0f),
						new Vector2f(maxRadius, maxRadius), new Vector2f(0.0f, maxRadius),
						new Vector2f(0.0f, minRadius)
					};
					
					boundingShapes = new FiniteProportionalHolomorphicShape[4];
					for ( int i = 0 ; i<boundingShapes.length; i++ ) {
						boundingShapes[i] = new ConvexPolygon(points);
						for ( int j = 0 ; j<points.length; j++ ) {
							points[j] = points[j].rotate90AnticlockWise();
						}
					}
				}
				
				return new FiniteProportionalHolomorphicShapeGroup<FiniteProportionalHolomorphicShape<?>>(boundingShapes);
			}

			@Override
			public FiniteProportionalHolomorphicShape<?> visitConvexPolygon(
					FiniteAffineHolomorphicShape<ConvexPolygon> affineConvexPolygon) throws RuntimeException {
				final ConvexPolygon convexPolygon = affineConvexPolygon.getBackingShape();
				return new FiniteProportionalHolomorphicShapeGroup<FiniteProportionalHolomorphicShape<?>>(
					convexPolygon.transform(Matrix3f.IDENTITY.rotate(angleInterval.getMin())),
					convexPolygon.transform(Matrix3f.IDENTITY.rotate(angleInterval.getMax())),
					RotationPlaneKinematicMapper.this.boundingRegion( convexPolygon.getPerimeter(), timeInterval )
				);
			}

			@Override
			public <S extends FiniteProportionalHolomorphicShape<?>> FiniteProportionalHolomorphicShape<?> visitGroup(
					FiniteAffineHolomorphicShape<FiniteProportionalHolomorphicShapeGroup<FiniteAffineHolomorphicShape<? extends S>>> group)
					throws RuntimeException {
				FiniteProportionalHolomorphicShape<?>[] resultShapes = new FiniteProportionalHolomorphicShape[group.getBackingShape().getShapes().size()];
				int i = 0;
				
				for ( FiniteAffineHolomorphicShape<? extends S> eachShape : group.getBackingShape().getShapes() ) {
					resultShapes[i++] = RotationPlaneKinematicMapper.this.boundingRegion(eachShape.getBackingShape(), timeInterval);
				}
				
				return new FiniteProportionalHolomorphicShapeGroup<FiniteProportionalHolomorphicShape<?>>(resultShapes);
			}
			
		});
	}
	
	/**
	 * @pre La cadena no puede ser nula
	 * @post Parsea un mapeador con la cadena especificada, que consiste
	 * 		 en una matriz 3x3 proporcional
	 */
	public static RotationPlaneKinematicMapper<Rotation> parse(String string) {
		if ( string != null ) {
			return new RotationPlaneKinematicMapper<Rotation>(Rotation.parse(string));
		}
		else {
			throw new NullPointerException();
		}
	}
	
	/**
	 * @post Devuelve una representación en cadena de carácteres
	 */
	@Override
	public String toString() {
		return "RotationKinematicMapper@" + Integer.toHexString(System.identityHashCode(this)) + "( " + this.rotation + ")";
	}

	/* (non-Javadoc)
	 * @see com.arielcarrizo.gameengine.physics.plane.kinematics.KinematicMapper#getMaxDistanceTraveled_internal(com.arielcarrizo.geometry.plane.finite.FiniteProportionalHolomorphicShape, com.arielcarrizo.math.intervalarithmetic.FloatClosedInterval)
	 */
	@Override
	protected float getMaxDistanceTraveled_internal(FiniteProportionalHolomorphicShape<?> originalShape,
			FloatClosedInterval timeInterval) {
		float angularDistance = Math.min( this.rotation.getAngleInterval(timeInterval).length(), ExtraMath.doublePI );
		
		return originalShape.maxDistanceToOrigin() * angularDistance;
	}

	/* (non-Javadoc)
	 * @see com.arielcarrizo.gameengine.physics.plane.kinematics.kinematicMapper.PlaneKinematicMapper#getMaxDistanceTraveledByBoundedTransformedShape_internal(com.arielcarrizo.geometry.plane.finite.FiniteProportionalHolomorphicShape, com.arielcarrizo.math.intervalarithmetic.FloatClosedInterval, float)
	 */
	@Override
	protected float getMaxDistanceTraveledWithBoundedTransformation_internal(
			FiniteProportionalHolomorphicShape<?> originalShape, FloatClosedInterval timeInterval,
			float maxDistanceTraveled_originalTransformation) {
		float angularDistance = Math.min( this.rotation.getAngleInterval(timeInterval).length(), ExtraMath.doublePI );
		
		return ( originalShape.maxDistanceToOrigin() + maxDistanceTraveled_originalTransformation ) * angularDistance ;
	}

	/* (non-Javadoc)
	 * @see com.arielcarrizo.gameengine.physics.plane.kinematics.kinematicMapper.PlaneKinematicMapper#accept(com.arielcarrizo.gameengine.physics.plane.kinematics.kinematicMapper.PlaneKinematicMapper.Visitor)
	 */
	@Override
	public <V, E extends Throwable> V accept(Visitor<V, E> visitor) throws E {
		return visitor.visit(this);
	}

	/* (non-Javadoc)
	 * @see com.arielcarrizo.gameengine.physics.plane.kinematics.kinematicMapper.PlaneKinematicMapper#getStartTime()
	 */
	@Override
	public float getStartTime() {
		return this.rotation.getStartTime();
	}
}
