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
package com.arielcarrizo.gameengine.renderengine.backend.opengl.gl21.plane.shape;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;

import com.arielcarrizo.gameengine.renderengine.backend.opengl.GLException;
import com.arielcarrizo.gameengine.renderengine.backend.opengl.gl21.Gl21RenderEngineBackendObject;
import com.arielcarrizo.gameengine.renderengine.backend.opengl.gl21.Gl21RenderEngineBackendSystem;
import com.arielcarrizo.gameengine.renderengine.backend.opengl.gl21.attributestream.AttributeStream;
import com.arielcarrizo.gameengine.renderengine.backend.opengl.gl21.attributestream.AttributeStreamManager;
import com.arielcarrizo.gameengine.renderengine.backend.opengl.gl21.attributestream.Vector2fAttributeStream;
import com.arielcarrizo.gameengine.renderengine.backend.opengl.gl21.attributestream.AttributeStreamManager.DrawingMode;
import com.esferixis.gameengine.physics.plane.statics.AffineMapper2d;
import com.esferixis.gameengine.physics.plane.statics.IdentityMapper2d;
import com.esferixis.gameengine.physics.plane.statics.PlaneMapper;
import com.esferixis.gameengine.physics.plane.statics.ProportionalAffineMapper2d;
import com.esferixis.gameengine.physics.plane.statics.ProportionalTransformedMapper2d;
import com.esferixis.gameengine.physics.plane.statics.TransformedMapper2d;
import com.esferixis.gameengine.renderengine.backend.plane.shape.ShapeObject;
import com.esferixis.gameengine.renderengine.plane.shape.ColorDistribution;
import com.esferixis.gameengine.renderengine.plane.shape.UniformColorDistribution;
import com.esferixis.geometry.plane.Line;
import com.esferixis.geometry.plane.Shape;
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

public final class ShapeRenderingSubsystem extends Gl21RenderEngineBackendObject {
	private final AttributeStream<Vector2f> positionAttributeStream;
	private final List<ColorDistributionBacker<?>> colorDistributionBackers;
	
	private static final Vector2f extremePoint_11 = new Vector2f(-1.0f, -1.0f);
	private static final Vector2f extremePoint_21 = new Vector2f(1.0f, -1.0f);
	private static final Vector2f extremePoint_22 = new Vector2f(1.0f, 1.0f);
	private static final Vector2f extremePoint_12 = new Vector2f(-1.0f, 1.0f);
	
	private static final ConvexPolygon convexPolygon = new ConvexPolygon(
			extremePoint_11,
			extremePoint_21,
			extremePoint_22,
			extremePoint_12
		);
	
	private final UniformColorDistributionBacker uniformColorDistributionBacker;
	
	/**
	 * @post Crea el subsistema con el sistema del backend del engine de rendering
	 * 		 especificado
	 */
	public ShapeRenderingSubsystem(Gl21RenderEngineBackendSystem renderEngineBackendSystem) throws GLException {
		super(renderEngineBackendSystem);
		this.positionAttributeStream = new Vector2fAttributeStream();
		
		ArrayList< ColorDistributionBacker<?> > colorDistributionBackers = new ArrayList< ColorDistributionBacker<?> >();
		
		this.uniformColorDistributionBacker = new UniformColorDistributionBacker(renderEngineBackendSystem, this.positionAttributeStream);
		colorDistributionBackers.add(this.uniformColorDistributionBacker);
		
		colorDistributionBackers.trimToSize();
		this.colorDistributionBackers = Collections.unmodifiableList( colorDistributionBackers );
	}
	
	/**
	 * @post Devuelve el backer asociado a la distribución de color
	 * 		 especificada
	 */
	private <D extends ColorDistribution> ColorDistributionBacker<D> getColorDistributionBacker(D colorDistribution) {
		return colorDistribution.accept(new ColorDistribution.Visitor< ColorDistributionBacker<D> >() {

			@Override
			public ColorDistributionBacker<D> visit(UniformColorDistribution colorDistribution) {
				return (ColorDistributionBacker<D>) ShapeRenderingSubsystem.this.uniformColorDistributionBacker;
			}
		});
	}
	
	/**
	 * @post Renderiza el objeto de figura especificado
	 */
	public void render(ShapeObject shapeObject) {
		if ( shapeObject != null ) {
			final Matrix3f transformMatrix = shapeObject.getMapper().accept(new PlaneMapper.Visitor<Matrix3f, RuntimeException>() {

				@Override
				public Matrix3f visit(ProportionalAffineMapper2d affineMapper) {
					return affineMapper.getTransformMatrix();
				}

				@Override
				public Matrix3f visit(IdentityMapper2d identityMapper) {
					return Matrix3f.IDENTITY;
				}

				@Override
				public Matrix3f visit(TransformedMapper2d transformedMapper) {
					return transformedMapper.getTransformerMapper().accept(this).mul(transformedMapper.getOriginalMapper().accept(this));
				}

				@Override
				public Matrix3f visit(AffineMapper2d affineMapper) throws RuntimeException {
					return affineMapper.getTransformMatrix();
				}

				@Override
				public Matrix3f visit(ProportionalTransformedMapper2d transformedMapper)
						throws RuntimeException {
					return transformedMapper.getTransformerMapper().accept(this).mul(transformedMapper.getOriginalMapper().accept(this));
				}
			});
			
			final ColorDistributionBacker<? extends ColorDistribution> colorDistributionBacker = this.getColorDistributionBacker(shapeObject.getColorDistribution());
			
			{
				final Matrix3f shaderTransformMatrix;
				if ( shapeObject.getShape() instanceof Line ) {
					shaderTransformMatrix = Matrix3f.IDENTITY;
				}
				else {
					shaderTransformMatrix = transformMatrix;
				}
				
				((ColorDistributionBacker) colorDistributionBacker).prepare(shapeObject.getColorDistribution(), shaderTransformMatrix);
			}
			
			shapeObject.getShape().accept(new Shape.Visitor<Void, RuntimeException>() {
				final AttributeStreamManager attributeStreamManager = ShapeRenderingSubsystem.this.getRenderEngineBackend().getAttributeStreamManager();
				final AttributeStream<Vector2f> positionAttributeStream = ShapeRenderingSubsystem.this.positionAttributeStream;
				
				@Override
				public Void visit(Point point) {
					this.attributeStreamManager.startStreaming(DrawingMode.POINT);
					this.positionAttributeStream.send(point.getPosition());
					this.attributeStreamManager.endVertex();
					this.attributeStreamManager.endStreaming();
					return null;
				}

				@Override
				public Void visit(LineSegment line) {
					this.attributeStreamManager.startStreaming(DrawingMode.LINE);
					this.positionAttributeStream.send(line.getPoint1());
					this.attributeStreamManager.endVertex();
					
					this.positionAttributeStream.send(line.getPoint2());
					this.attributeStreamManager.endVertex();
					
					this.attributeStreamManager.endStreaming();
					return null;
				}
				
				@Override
				public Void visit(Line rect) {					
					final List<Float> intersections = convexPolygon.getPerimeter().getRectIntersection(new Line(transformMatrix.transformPoint(rect.getReferencePoint()), transformMatrix.transformDirection(rect.getDirection())));
					
					if ( intersections.size() >= 2 ) {
						final Vector2f vertex1, vertex2;
						
						vertex1 = rect.getPointByProportionalScalar(ExtraMath.min(intersections));
						vertex2 = rect.getPointByProportionalScalar(ExtraMath.max(intersections));
						this.visit(new LineSegment(vertex1, vertex2));
					}
					/*
					else {
						if ( Math.abs( rect.getDirection().getY() ) < Math.abs( rect.getDirection().getX() ) ) {
							vertex1 = new Vector2f(-1.0f, rect.getReferencePoint().getY());
							vertex2 = new Vector2f(1.0f, rect.getReferencePoint().getY());
						}
						else {
							vertex1 = new Vector2f(rect.getReferencePoint().getX(), -1.0f);
							vertex2 = new Vector2f(rect.getReferencePoint().getX(), 1.0f);
						}
					}
					*/
					
					return null;
				}

				@Override
				public Void visit(Circumference circumference) {
					int maxSegments = 360;
					
					this.attributeStreamManager.startStreaming(DrawingMode.LINE_LOOP);
					for ( int i = 0 ; i<maxSegments; i++ ) {
						final float angle = 2.0f * (float) Math.PI * (float) i / (float) maxSegments;
						
						this.positionAttributeStream.send(circumference.getPointWithAngle(angle));
						this.attributeStreamManager.endVertex();
					}
					this.attributeStreamManager.endStreaming();
					
					return null;
				}

				private void traceCircumferenceSegment(CircumferenceSegment circumferenceSegment) {
					int maxSegments = 360;
					final FloatClosedInterval angleInterval = circumferenceSegment.getAngleInterval();
					
					for ( int i = 0 ; i<maxSegments; i++ ) {
						final float angle = angleInterval.linearInterpolation(i / (float) (maxSegments-1));
						
						this.positionAttributeStream.send(circumferenceSegment.getCircumference().getPointWithAngle(angle));
						this.attributeStreamManager.endVertex();
					}
				}

				@Override
				public Void visit(CircumferenceSegment circumferenceSegment) {
					this.attributeStreamManager.startStreaming(DrawingMode.LINE_STRIP);
					
					this.traceCircumferenceSegment(circumferenceSegment);
					
					this.attributeStreamManager.endStreaming();
					
					return null;
				}
				
				@Override
				public Void visit(Circle circle) {
					int maxSegments = 360;
					
					this.attributeStreamManager.startStreaming(DrawingMode.TRIANGLE_FAN);
					
					//this.positionAttributeStream.send(circle.getPerimeter().getCenter());
					//this.attributeStreamManager.endVertex();
					
					for ( int i = 0 ; i<maxSegments; i++ ) {
						final float angle = 2.0f * (float) Math.PI * (float) i / (float) (maxSegments-1);
						
						this.positionAttributeStream.send(circle.getPerimeter().getPointWithAngle(angle));
						this.attributeStreamManager.endVertex();
					}
					this.attributeStreamManager.endStreaming();
					
					return null;
				}
				
				@Override
				public Void visit(SolidCapsule capsule) {
					List<CircumferenceSegment> circumferenceSegments = capsule.getPerimeterCircumferenceSegments();
					
					this.attributeStreamManager.startStreaming(DrawingMode.TRIANGLE_FAN);
					
					for ( CircumferenceSegment eachCircumferenceSegment : circumferenceSegments ) {
						this.traceCircumferenceSegment(eachCircumferenceSegment);
					}
					
					this.attributeStreamManager.endStreaming();
					
					return null;
				}

				@Override
				public Void visit(ConvexPolygon convexPolygon) {
					this.attributeStreamManager.startStreaming(DrawingMode.TRIANGLE_FAN);
					final List<Vector2f> vertices = convexPolygon.getVertices();
					
					//this.positionAttributeStream.send(convexPolygon.getCenter());
					//this.attributeStreamManager.endVertex();
					
					for ( Vector2f eachVertex : vertices ) {
						this.positionAttributeStream.send(eachVertex);
						this.attributeStreamManager.endVertex();
					}
					
					this.positionAttributeStream.send(vertices.get(0));
					this.attributeStreamManager.endVertex();
					
					this.attributeStreamManager.endStreaming();
					
					return null;
				}

				@Override
				public <S extends FiniteProportionalHolomorphicShape<?>> Void visit(
						FiniteProportionalHolomorphicShapeGroup<S> proportionalHolomorphicShapeGroup)
						throws RuntimeException {
					for ( S eachShape : proportionalHolomorphicShapeGroup.getShapes() ) {
						eachShape.accept(this);
					}

					return null;
				}

				@Override
				public <S extends FiniteProportionalHolomorphicShape<S>> Void visit(
						FiniteAffineHolomorphicShape<S> finiteAffineHolomorphicShape) throws RuntimeException {
					return finiteAffineHolomorphicShape.getBackingShape().accept(this);
				}
			});
		}
		else {
			throw new NullPointerException();
		}
	}
	
	/**
	 * @post Destruye el subsistema
	 */
	public void destroy() {
		for ( ColorDistributionBacker<?> eachColorDistributionBacker : this.colorDistributionBackers ) {
			eachColorDistributionBacker.destroy();
		}
	}
}
