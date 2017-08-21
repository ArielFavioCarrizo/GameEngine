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
package com.esferixis.gameengine.renderengine.frontend.plane.kinematics.containers;

import java.util.Set;

import com.esferixis.gameengine.renderengine.frontend.plane.kinematics.InstantKinematicRenderer;
import com.esferixis.gameengine.renderengine.frontend.plane.kinematics.objects.KinematicRenderPlaneObject;
import com.esferixis.geometry.plane.finite.FiniteAffineHolomorphicShape;
import com.esferixis.geometry.plane.finite.FiniteProportionalHolomorphicShape;
import com.esferixis.misc.collection.set.ArrayHashSet;
import com.esferixis.misc.collection.set.ArraySet;

/**
 * @author ariel
 *
 */
public final class LinearKinematicRenderPlaneObjectContainer extends RenderizableKinematicRenderPlaneObjectContainer {
	/**
	 * 
	 */
	private static final long serialVersionUID = 2949961686787192380L;
	
	private final Set<KinematicRenderPlaneObject> kinematicRenderPlaneObjects;
	
	public Factory<LinearKinematicRenderPlaneObjectContainer> FACTORY = new Factory<LinearKinematicRenderPlaneObjectContainer>() {

		@Override
		public LinearKinematicRenderPlaneObjectContainer create() {
			return new LinearKinematicRenderPlaneObjectContainer();
		}
		
	};
	
	/**
	 * @post Crea el contenedor
	 */
	public LinearKinematicRenderPlaneObjectContainer() {
		this.kinematicRenderPlaneObjects = new ArrayHashSet<KinematicRenderPlaneObject>();
	}
	
	/* (non-Javadoc)
	 * @see com.arielcarrizo.gameengine.renderengine.frontend.plane.kinematics.containers.RenderizableKinematicRenderPlaneObjectContainer#render_checked(com.arielcarrizo.gameengine.renderengine.frontend.plane.kinematics.InstantKinematicRenderer, com.arielcarrizo.geometry.plane.finite.FiniteAffineHolomorphicShape)
	 */
	@Override
	protected void render_checked(InstantKinematicRenderer instantKinematicRenderer,
			FiniteAffineHolomorphicShape<?> intersectingAreaShape) {
		if ( ( instantKinematicRenderer != null ) && ( intersectingAreaShape != null ) ) {
			for ( KinematicRenderPlaneObject eachObject : this.kinematicRenderPlaneObjects ) {
				instantKinematicRenderer.render(eachObject);
			}
		}
		else {
			throw new NullPointerException();
		}
	}

	/* (non-Javadoc)
	 * @see com.esferixis.gameengine.renderengine.frontend.plane.kinematics.containers.KinematicRenderPlaneObjectContainer#addObject_checked(com.esferixis.gameengine.renderengine.frontend.plane.kinematics.objects.KinematicRenderPlaneObject)
	 */
	@Override
	protected void addObject_checked(KinematicRenderPlaneObject kinematicRenderPlaneObject) {
		this.kinematicRenderPlaneObjects.add(kinematicRenderPlaneObject);
	}

	/* (non-Javadoc)
	 * @see com.esferixis.gameengine.renderengine.frontend.plane.kinematics.containers.KinematicRenderPlaneObjectContainer#removeObject_checked(com.esferixis.gameengine.renderengine.frontend.plane.kinematics.objects.KinematicRenderPlaneObject)
	 */
	@Override
	public void removeObject_checked(KinematicRenderPlaneObject kinematicRenderPlaneObject) {
		this.kinematicRenderPlaneObjects.remove(kinematicRenderPlaneObject);
	}

	/* (non-Javadoc)
	 * @see com.esferixis.gameengine.renderengine.frontend.plane.kinematics.containers.KinematicRenderPlaneObjectContainer#isObjectPresent_checked(com.esferixis.gameengine.renderengine.frontend.plane.kinematics.objects.KinematicRenderPlaneObject)
	 */
	@Override
	protected boolean isObjectPresent_checked(KinematicRenderPlaneObject kinematicRenderPlaneObject) {
		return this.kinematicRenderPlaneObjects.contains(kinematicRenderPlaneObject);
	}
}
