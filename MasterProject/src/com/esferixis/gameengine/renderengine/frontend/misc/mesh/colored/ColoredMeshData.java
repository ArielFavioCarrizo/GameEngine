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
package com.esferixis.gameengine.renderengine.frontend.misc.mesh.colored;

import java.util.List;

import com.esferixis.gameengine.renderengine.frontend.meshLayers.MeshLayersConfig;
import com.esferixis.gameengine.renderengine.frontend.misc.mesh.Mesh;
import com.esferixis.gameengine.renderengine.frontend.misc.mesh.MeshTriangle;
import com.esferixis.math.Vectorf;

/**
 * @author ariel
 *
 */
public final class ColoredMeshData<V extends Vectorf> extends Mesh.Data<V, ColoredMeshVertex<V> > {
	/**
	 * 
	 */
	private static final long serialVersionUID = 8052624406544451079L;
	
	private final MeshLayersConfig meshLayersConfig;
	
	/**
	 * @pre Los triángulos y la configuración de mapeado de textura no pueden ser nulos.
	 * 		Los triángulos tienen que tener un mapeado adecuado
	 * @post Crea la malla con los triángulos y la configuración de mapeado de textura
	 * 		 especificados
	 * @param triangles
	 */
	public ColoredMeshData(List<MeshTriangle<V, ColoredMeshVertex<V>>> triangles, MeshLayersConfig textureMappingConfig) {
		super(triangles);
		
		if ( textureMappingConfig != null ) {
			this.meshLayersConfig = textureMappingConfig;
			
			for ( MeshTriangle<V, ColoredMeshVertex<V>> eachTriangle : triangles) {
				this.meshLayersConfig.checkVertexLayersData(eachTriangle.getPoint1().getVertexLayersData());
				this.meshLayersConfig.checkVertexLayersData(eachTriangle.getPoint2().getVertexLayersData());
				this.meshLayersConfig.checkVertexLayersData(eachTriangle.getPoint3().getVertexLayersData());
			}
		}
		else {
			throw new NullPointerException();
		}
	}
	
	/**
	 * @post Devuelve la configuración de capas
	 */
	public MeshLayersConfig getMeshLayersConfig() {
		return this.meshLayersConfig;
	}

	/* (non-Javadoc)
	 * @see com.esferixis.gameengine.renderengine.frontend.misc.mesh.Mesh.Data#accept(com.esferixis.gameengine.renderengine.frontend.misc.mesh.Mesh.Data.Visitor)
	 */
	@Override
	public <R, T extends Throwable> R accept(
			com.esferixis.gameengine.renderengine.frontend.misc.mesh.Mesh.Data.Visitor<V, R, T> visitor) throws T {
		return visitor.visit(this);
	}
}
