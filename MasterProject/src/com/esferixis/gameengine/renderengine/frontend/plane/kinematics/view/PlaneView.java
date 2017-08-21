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
package com.esferixis.gameengine.renderengine.frontend.plane.kinematics.view;

import java.io.Serializable;

import com.esferixis.gameengine.physics.plane.statics.AffineMapper2d;
import com.esferixis.gameengine.physics.plane.statics.ProportionalAffineMapper2d;
import com.esferixis.gameengine.renderengine.frontend.plane.kinematics.InstantKinematicRenderer;
import com.esferixis.gameengine.renderengine.frontend.plane.kinematics.containers.KinematicRenderPlaneObjectContainer;
import com.esferixis.gameengine.renderengine.frontend.plane.kinematics.containers.RenderizableKinematicRenderPlaneObjectContainer;
import com.esferixis.gameengine.renderengine.frontend.plane.staticstage.StaticPlaneObject;
import com.esferixis.gameengine.renderengine.frontend.plane.staticstage.StaticPlaneObjectRenderer;

/**
 * @author ariel
 *
 */
public final class PlaneView implements Serializable {
	/**
	 * 
	 */
	private static final long serialVersionUID = 8350345604455684864L;
	
	private final RenderizableKinematicRenderPlaneObjectContainer objectContainer;
	private PlaneCamera planeCamera;
	
	/**
	 * @pre El contenedor de objetos cinemático y el renderizador estático, no pueden ser
	 * 		nulos
	 * @post Crea una vista del contenedor de objetos cinemático renderizable especificado,
	 * 		 con la cámara especificada
	 */
	public PlaneView(RenderizableKinematicRenderPlaneObjectContainer objectContainer, PlaneCamera planeCamera) {
		if ( objectContainer != null ) {
			this.objectContainer = objectContainer;
			this.planeCamera = planeCamera;
		}
		else {
			throw new NullPointerException();
		}
	}
	
	/**
	 * @post Devuelve el contenedor de objetos cinemáticos
	 */
	public KinematicRenderPlaneObjectContainer getKinematicRenderPlaneObjectContainer() {
		return this.objectContainer;
	}
	
	/**
	 * @post Devuelve la cámara
	 */
	public PlaneCamera getPlaneCamera() {
		return this.planeCamera;
	}
	
	/**
	 * @post Especifica la cámara
	 */
	public void setPlaneCamera(PlaneCamera planeCamera) {
		this.planeCamera = planeCamera;
	}
	
	/**
	 * @post Renderiza la vista con el renderizador cinemático instantáneo especificado
	 * 		 Si no tiene cámara asignada, no hace nada
	 */
	public void render(final InstantKinematicRenderer instantKinematicRenderer) {
		if ( instantKinematicRenderer != null ) {
			if ( this.planeCamera != null ) {
				this.objectContainer.render(instantKinematicRenderer.transform(new AffineMapper2d(this.planeCamera.getTransformationMatrix())), this.planeCamera.getVisionArea());
			}
		}
		else {
			throw new NullPointerException();
		}
	}
}
