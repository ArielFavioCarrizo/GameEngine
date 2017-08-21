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
import com.esferixis.gameengine.renderengine.frontend.meshLayers.MeshLayersConfigProfile;
import com.esferixis.gameengine.renderengine.frontend.misc.mesh.Mesh;
import com.esferixis.gameengine.renderengine.frontend.misc.mesh.colored.ColoredMeshData;
import com.esferixis.math.Vector2f;
import com.esferixis.misc.observer.ObserverManager;

/**
 * @author ariel
 *
 */
public abstract class KinematicMeshObject2dObserver extends KinematicRenderPlaneObjectObserver<KinematicMeshObject2d> {

	/**
	 * 
	 */
	private static final long serialVersionUID = -8683291380026187205L;

	/**
	 * @param type
	 */
	public KinematicMeshObject2dObserver() {
		super();
	}
	
	/**
	 * @post Notifica un cambio en la malla
	 */
	protected abstract void notifyMeshChange(Mesh<Vector2f, ColoredMeshData<Vector2f>> mesh);
	
	/**
	 * @post Notifica un cambio en la configuración de capas
	 */
	protected abstract void notifyMeshLayersConfigProfileChange(MeshLayersConfigProfile meshLayersConfigProfile);
	
	/* (non-Javadoc)
	 * @see com.esferixis.misc.observer.Observer#getObserverManager()
	 */
	@Override
	protected ObserverManager<KinematicMeshObject2d, ?> getObserverManager() {
		return this.getObservable().observerManager;
	}

	/* (non-Javadoc)
	 * @see com.arielcarrizo.gameengine.renderengine.frontend.plane.kinematics.objects.KinematicRenderPlaneObjectObserver#notifyKinematicMapperChange(com.arielcarrizo.gameengine.physics.plane.kinematics.kinematicMapper.PlaneKinematicMapper)
	 */
	@Override
	protected void notifyKinematicMapperChange(PlaneKinematicMapper newKinematicMapper) {
		// TODO Auto-generated method stub
		
	}

	/* (non-Javadoc)
	 * @see com.arielcarrizo.gameengine.renderengine.frontend.plane.kinematics.objects.KinematicRenderPlaneObjectObserver#update()
	 */
	@Override
	public final void update() {
		this.notifyKinematicMapperChange(this.getObservable().getKinematicMapper());
		this.notifyMeshChange(this.getObservable().getMesh());
		this.notifyMeshLayersConfigProfileChange(this.getObservable().getMeshLayersConfigProfile());
	}
	
}
