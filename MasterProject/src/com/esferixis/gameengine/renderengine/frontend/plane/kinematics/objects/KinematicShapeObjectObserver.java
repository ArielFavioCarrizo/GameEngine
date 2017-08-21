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
import com.esferixis.gameengine.renderengine.plane.shape.ColorDistribution;
import com.esferixis.geometry.plane.finite.FiniteProportionalHolomorphicShape;
import com.esferixis.misc.observer.ObserverManager;

/**
 * @author ariel
 *
 */
public abstract class KinematicShapeObjectObserver extends KinematicRenderPlaneObjectObserver<KinematicShapeObject> {

	/**
	 * 
	 */
	private static final long serialVersionUID = -5214142397098707385L;

	/**
	 * @post Notifica un cambio en la figura
	 */
	protected abstract void notifyShapeChange(FiniteProportionalHolomorphicShape<?> shape);
	
	/**
	 * @post Notifica un cambio en la distribución de color
	 */
	protected abstract void notifyColorDistributionChange(ColorDistribution colorDistribution);
	
	/* (non-Javadoc)
	 * @see com.arielcarrizo.misc.observer.Observer#getObserverManager()
	 */
	@Override
	protected ObserverManager<KinematicShapeObject, ?> getObserverManager() {
		return this.getObservable().observerManager;
	}

	/* (non-Javadoc)
	 * @see com.arielcarrizo.gameengine.renderengine.frontend.plane.kinematics.objects.KinematicRenderPlaneObjectObserver#update()
	 */
	@Override
	public final void update() {
		this.notifyKinematicMapperChange(this.getObservable().getKinematicMapper());
		this.notifyShapeChange(this.getObservable().getShape());
		this.notifyColorDistributionChange(this.getObservable().getColorDistribution());
	}

}
