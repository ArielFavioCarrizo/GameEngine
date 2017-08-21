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
package com.esferixis.gameengine.renderengine.frontend.plane.staticstage.mesh;

import com.esferixis.gameengine.physics.plane.statics.PlaneMapper;
import com.esferixis.gameengine.physics.space.statics.TransformedMapper;
import com.esferixis.gameengine.renderengine.frontend.meshLayers.MeshLayersConfigProfile;
import com.esferixis.gameengine.renderengine.frontend.misc.mesh.Mesh;
import com.esferixis.gameengine.renderengine.frontend.misc.mesh.colored.ColoredMeshData;
import com.esferixis.gameengine.renderengine.frontend.plane.staticstage.StaticPlaneObject;
import com.esferixis.math.Vector2f;

/**
 * @author ariel
 *
 */
public final class StaticMeshObject2d extends StaticPlaneObject {
	private static final long serialVersionUID = 2152975858101552842L;
		
	private Mesh<Vector2f, ColoredMeshData<Vector2f>> mesh;
	private MeshLayersConfigProfile meshLayersConfigProfile;
	
	/**
	 * @pre La malla, el perfil de configuración de capas de malla y el mapeador espacial no pueden ser nulos.
	 * @post Crea el objeto de malla con la malla, el perfil de configuración de capas de malla
	 * 		 y el mapeador espacial especificados
	 */
	public StaticMeshObject2d(Mesh<Vector2f, ColoredMeshData<Vector2f>> mesh, MeshLayersConfigProfile meshLayersConfigProfile, PlaneMapper planeMapper) {
		super(planeMapper);
		
		this.setMesh(mesh);
		this.setMeshLayersConfigProfile(meshLayersConfigProfile);
	}
	
	/**
	 * @pre La malla no puede ser nula
	 * @post Especifica la malla
	 */
	public void setMesh(final Mesh<Vector2f, ColoredMeshData<Vector2f>> mesh) {
		if ( mesh != null ) {
			this.mesh = mesh;
		}
		else {
			throw new NullPointerException();
		}
	}
	
	
	/**
	 * @post Devuelve la malla
	 */
	public Mesh<Vector2f, ColoredMeshData<Vector2f>> getMesh() {
		return this.mesh;
	}
	
	/**
	 * @post Devuelve el perfil de configuración de capas de malla
	 */
	public MeshLayersConfigProfile getMeshLayersConfigProfile() {
		return this.meshLayersConfigProfile;
	}
	
	/**
	 * @pre El perfil de configuración de capas de malla no puede ser nulo
	 * @post Especifica el perfil de configuración de capas de malla
	 */
	public void setMeshLayersConfigProfile(MeshLayersConfigProfile meshLayersConfigProfile) {
		if ( meshLayersConfigProfile != null ) {
			this.meshLayersConfigProfile = meshLayersConfigProfile;
		}
		else {
			throw new NullPointerException();
		}
	}

	/* (non-Javadoc)
	 * @see com.esferixis.gameengine.renderengine.frontend.plane.staticstage.StaticPlaneObject#accept(com.esferixis.gameengine.renderengine.frontend.plane.staticstage.StaticPlaneObject.Visitor)
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
		return new StaticMeshObject2d(this.getMesh(), this.getMeshLayersConfigProfile(), newPlaneMapper);
	}
		
}
