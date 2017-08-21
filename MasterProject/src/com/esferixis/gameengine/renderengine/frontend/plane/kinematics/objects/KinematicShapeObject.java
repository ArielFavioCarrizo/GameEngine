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
package com.esferixis.gameengine.renderengine.frontend.plane.kinematics.objects;

import com.esferixis.gameengine.physics.plane.kinematics.mapper.PlaneKinematicMapper;
import com.esferixis.gameengine.physics.plane.statics.PlaneMapper;
import com.esferixis.gameengine.renderengine.frontend.plane.staticstage.StaticPlaneObjectRenderer;
import com.esferixis.gameengine.renderengine.frontend.plane.staticstage.shape.StaticShapeObject;
import com.esferixis.gameengine.renderengine.plane.shape.ColorDistribution;
import com.esferixis.geometry.plane.finite.FiniteAffineHolomorphicShape;
import com.esferixis.geometry.plane.finite.FiniteProportionalHolomorphicShape;
import com.esferixis.misc.ElementCallback;
import com.esferixis.misc.observer.ObserverManager;

/**
 * @author ariel
 *
 */
public final class KinematicShapeObject extends KinematicRenderPlaneObject {
	/**
	 * 
	 */
	private static final long serialVersionUID = -8195237524471203630L;
	private FiniteProportionalHolomorphicShape<?> shape;
	private ColorDistribution colorDistribution;
	
	final ObserverManager<KinematicShapeObject, KinematicShapeObjectObserver> observerManager = new ObserverManager<>(this, KinematicShapeObjectObserver.class);
	
	/**
	 * @post Crea un objeto de figura sin especificar nada
	 */
	public KinematicShapeObject() {
		this(null, null, null);
	}
	
	/**
	 * @post Crea un objeto de figura, con la figura, la distribución de color
	 * 		 y el mapeador cinemático especificados
	 * @param kinematicMapper
	 */
	public KinematicShapeObject(FiniteProportionalHolomorphicShape<?> shape, PlaneKinematicMapper kinematicMapper, ColorDistribution colorDistribution) {
		super(kinematicMapper);
		this.shape = shape;
		this.colorDistribution = colorDistribution;
	}
	
	/**
	 * @post Devuelve la figura
	 */
	public FiniteProportionalHolomorphicShape<?> getShape() {
		return this.shape;
	}
	
	/**
	 * @post Especifica la figura
	 */
	public void setShape(final FiniteProportionalHolomorphicShape<?> shape) {
		this.observerManager.notifyObservers(new ElementCallback<KinematicShapeObjectObserver>(){

			@Override
			public void run(KinematicShapeObjectObserver observer) {
				observer.notifyShapeChange(shape);
			}
			
		});
		this.shape = shape;
	}
	
	/**
	 * @post Devuelve la distribución de color
	 */
	public ColorDistribution getColorDistribution() {
		return this.colorDistribution;
	}
	
	/**
	 * @post Especifica la distribución de color
	 */
	public void setColorDistribution(final ColorDistribution colorDistribution) {
		this.observerManager.notifyObservers(new ElementCallback<KinematicShapeObjectObserver>(){

			@Override
			public void run(KinematicShapeObjectObserver observer) {
				observer.notifyColorDistributionChange(colorDistribution);
			}
			
		});
		this.colorDistribution = colorDistribution;
	}
	
	/* (non-Javadoc)
	 * @see com.arielcarrizo.gameengine.renderengine.frontend.plane.kinematics.objects.KinematicRenderPlaneObject#render_internal(com.arielcarrizo.gameengine.renderengine.frontend.plane.staticstage.StaticPlaneObjectRenderer, com.arielcarrizo.gameengine.physics.plane.statics.PlaneMapper, float)
	 */
	@Override
	protected void render_internal(StaticPlaneObjectRenderer objectRenderer, PlaneMapper instantPlaneMapper,
			float time) {
		if ( ( this.shape != null ) && ( this.colorDistribution != null ) ) {
			objectRenderer.render(new StaticShapeObject(this.shape, this.colorDistribution, instantPlaneMapper));
		}
	}

	/* (non-Javadoc)
	 * @see com.arielcarrizo.gameengine.renderengine.frontend.plane.kinematics.objects.KinematicRenderPlaneObject#notifyObservers(com.arielcarrizo.misc.ElementCallBack)
	 */
	@Override
	protected void notifyObservers(ElementCallback<? super KinematicRenderPlaneObjectObserver<?>> elementCallBack) {
		this.observerManager.notifyObservers(elementCallBack);
	}
	
	/* (non-Javadoc)
	 * @see com.arielcarrizo.gameengine.renderengine.frontend.plane.kinematics.objects.KinematicRenderPlaneObject#loadBoundingShape()
	 */
	@Override
	protected FiniteAffineHolomorphicShape<?> loadBoundingShape() {
		return this.shape.getBoundingAffineHolomorphicShape();
	}

	/* (non-Javadoc)
	 * @see com.arielcarrizo.gameengine.renderengine.frontend.plane.kinematics.objects.KinematicRenderPlaneObject#accept(com.arielcarrizo.gameengine.renderengine.frontend.plane.kinematics.objects.KinematicRenderPlaneObject.Visitor)
	 */
	@Override
	public <R, T extends Throwable> R accept(Visitor<R, T> visitor) throws T {
		return visitor.visit(this);
	}
}
