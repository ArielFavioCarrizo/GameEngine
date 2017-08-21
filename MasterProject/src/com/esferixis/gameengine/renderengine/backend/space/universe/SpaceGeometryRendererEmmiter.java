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
package com.esferixis.gameengine.renderengine.backend.space.universe;

import com.esferixis.gameengine.renderengine.backend.misc.mesh.Mesh;
import com.esferixis.gameengine.renderengine.backend.misc.mesh.MeshTriangleVertex;
import com.esferixis.gameengine.renderengine.backend.misc.mesh.colored.ColoredMeshData;
import com.esferixis.gameengine.renderengine.backend.space.mesh.GeometryRenderer3d;
import com.esferixis.gameengine.renderengine.backend.space.mesh.ProtectedGeometryRenderer3d;
import com.esferixis.gameengine.renderengine.backend.space.mesh.GeometryRenderer3d.CullFace;
import com.esferixis.gameengine.renderengine.space.camera.Camera3d;
import com.esferixis.math.Matrix4f;
import com.esferixis.math.Vector3f;
import com.esferixis.math.intervalarithmetic.FloatClosedInterval;

/**
 * @author ariel
 *
 */
public abstract class SpaceGeometryRendererEmmiter<D extends Mesh.Data<Vector3f, ?>> {
	/**
	 * @pre La matriz de transformación y proyección de vista, los planos de clipping Z ni el renderizador de geometría pueden ser nulos.
	 * 		El renderizador tiene que ser nuevo.
	 * @post Renderiza con la matriz de transformación y proyección de vista y el renderizador de geometría especificados
	 */
	public void render(Matrix4f projectedViewTransformMatrix, GeometryRenderer3d<D> geometryRenderer) {
		if ( ( projectedViewTransformMatrix != null ) && ( geometryRenderer != null ) ) {
			ProtectedGeometryRenderer3d<D> protectedGeometryRenderer = new ProtectedGeometryRenderer3d<D>(geometryRenderer);
			
			protectedGeometryRenderer.setCullFace(CullFace.FRONT);
			
			this.render_internal(projectedViewTransformMatrix, protectedGeometryRenderer);
			
			protectedGeometryRenderer.exitContext();
		}
		else {
			throw new NullPointerException();
		}
	}
	
	/**
	 * @post Renderiza con la matriz de transformación y el renderizador de geometría especificados (Implementación interna)
	 */
	public abstract void render_internal(Matrix4f projectedViewTransformMatrix, GeometryRenderer3d<D> geometryRenderer);
}
