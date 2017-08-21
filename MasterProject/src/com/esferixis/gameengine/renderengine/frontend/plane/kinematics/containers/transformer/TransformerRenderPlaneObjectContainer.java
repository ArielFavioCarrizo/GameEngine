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
package com.esferixis.gameengine.renderengine.frontend.plane.kinematics.containers.transformer;

import java.util.HashMap;
import java.util.Map;

import com.esferixis.gameengine.physics.plane.kinematics.mapper.MirrorPlaneKinematicMapper;
import com.esferixis.gameengine.physics.plane.kinematics.mapper.PlaneKinematicMapper;
import com.esferixis.gameengine.renderengine.frontend.plane.kinematics.InstantKinematicRenderer;
import com.esferixis.gameengine.renderengine.frontend.plane.kinematics.containers.KinematicRenderPlaneObjectContainer;
import com.esferixis.gameengine.renderengine.frontend.plane.kinematics.objects.KinematicMeshObject2d;
import com.esferixis.gameengine.renderengine.frontend.plane.kinematics.objects.KinematicRenderPlaneObject;
import com.esferixis.gameengine.renderengine.frontend.plane.kinematics.objects.KinematicRenderPlaneObjectObserver;
import com.esferixis.gameengine.renderengine.frontend.plane.kinematics.objects.KinematicShapeObject;
import com.esferixis.geometry.plane.finite.FiniteProportionalHolomorphicShape;

/**
 * @author ariel
 *
 */
public final class TransformerRenderPlaneObjectContainer extends KinematicRenderPlaneObjectContainer {
	/**
	 * 
	 */
	private static final long serialVersionUID = -397348532702048635L;
	
	private final MirrorPlaneKinematicMapper<PlaneKinematicMapper> transformerKinematicMapperMirror;
	
	private Map<KinematicRenderPlaneObject, ObjectBacker<? extends KinematicRenderPlaneObject>> objectBackerPerKinematicRenderPlaneObject;
	
	private KinematicRenderPlaneObjectContainer targetContainer;
	
	/**
	 * @pre Ell mapeador cinemático transformador no puede ser nulo
	 * @post Crea el contenedor transformador con el contenedor objetivo y
	 * 		 el mapeador cinemático de transformación especificado
	 */
	public TransformerRenderPlaneObjectContainer(PlaneKinematicMapper transformerKinematicMapper) {
		if ( transformerKinematicMapper != null ) {
			this.transformerKinematicMapperMirror = new MirrorPlaneKinematicMapper<PlaneKinematicMapper>(transformerKinematicMapper);
			this.targetContainer = null;
			
			this.objectBackerPerKinematicRenderPlaneObject = new HashMap<KinematicRenderPlaneObject, ObjectBacker<? extends KinematicRenderPlaneObject>>();
		}
		else {
			throw new NullPointerException();
		}
	}
	
	/**
	 * @pre El transformador cinemático no puede ser nulo
	 * @post Especifica el mapeador cinemático transformador
	 */
	public void setTransformerMapper(PlaneKinematicMapper transformerKinematicMapper) {
		if ( transformerKinematicMapper != null ) {
			this.transformerKinematicMapperMirror.setMirroredMapper(transformerKinematicMapper);
		}
		else {
			throw new NullPointerException();
		}
	}
	
	/**
	 * @post Devuelve el mapeador transformador
	 */
	public PlaneKinematicMapper getTransformerMapper() {
		return this.transformerKinematicMapperMirror.getMirroredMapper();
	}

	/* (non-Javadoc)
	 * @see com.arielcarrizo.gameengine.renderengine.frontend.plane.kinematics.containers.KinematicRenderPlaneObjectContainer#addObject_checked(com.arielcarrizo.gameengine.renderengine.frontend.plane.kinematics.objects.KinematicRenderPlaneObject)
	 */
	@Override
	protected void addObject_checked(KinematicRenderPlaneObject kinematicRenderPlaneObject) {
		if ( !this.isObjectPresent(kinematicRenderPlaneObject) ) {
			ObjectBacker<? extends KinematicRenderPlaneObject> backer = ObjectBacker.create(kinematicRenderPlaneObject, this.transformerKinematicMapperMirror);
			
			if ( this.targetContainer != null ) {
				backer.attach();
				this.targetContainer.addObject(backer.getTransformedObject());
			}
		}
	}

	/* (non-Javadoc)
	 * @see com.arielcarrizo.gameengine.renderengine.frontend.plane.kinematics.containers.KinematicRenderPlaneObjectContainer#removeObject_checked(com.arielcarrizo.gameengine.renderengine.frontend.plane.kinematics.objects.KinematicRenderPlaneObject)
	 */
	@Override
	public void removeObject_checked(KinematicRenderPlaneObject kinematicRenderPlaneObject) {
		ObjectBacker<? extends KinematicRenderPlaneObject> backer = this.objectBackerPerKinematicRenderPlaneObject.remove(kinematicRenderPlaneObject);
		
		if ( backer != null ) {
			if ( this.targetContainer != null ) {
				this.targetContainer.removeObject(backer.getTransformedObject());
				backer.detach();
			}
			
		}
	}

	/* (non-Javadoc)
	 * @see com.arielcarrizo.gameengine.renderengine.frontend.plane.kinematics.containers.KinematicRenderPlaneObjectContainer#isObjectPresent_checked(com.arielcarrizo.gameengine.renderengine.frontend.plane.kinematics.objects.KinematicRenderPlaneObject)
	 */
	@Override
	protected boolean isObjectPresent_checked(KinematicRenderPlaneObject kinematicRenderPlaneObject) {
		return this.objectBackerPerKinematicRenderPlaneObject.containsKey(kinematicRenderPlaneObject);
	}
	
	/**
	 * @post Asocia un contenedor objetivo, si es nulo considera que no tiene asignado
	 * 		 ninguno
	 */
	public void attachTargetContainer(KinematicRenderPlaneObjectContainer targetContainer) {
		if ( targetContainer != this.targetContainer ) {
			if ( this.targetContainer != null ) {
				for ( ObjectBacker<? extends KinematicRenderPlaneObject> eachObjectBacker : this.objectBackerPerKinematicRenderPlaneObject.values() ) {
					this.targetContainer.removeObject(eachObjectBacker.getTransformedObject());
					eachObjectBacker.detach();
				}
			}
			
			this.targetContainer = targetContainer;
			
			if ( this.targetContainer != null ) {
				for ( ObjectBacker<? extends KinematicRenderPlaneObject> eachObjectBacker : this.objectBackerPerKinematicRenderPlaneObject.values() ) {
					eachObjectBacker.attach();
					this.targetContainer.addObject(eachObjectBacker.getTransformedObject());
				}
			}
		}
	}
}
