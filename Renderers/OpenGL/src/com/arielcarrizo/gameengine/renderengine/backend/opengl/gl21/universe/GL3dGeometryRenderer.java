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
package com.arielcarrizo.gameengine.renderengine.backend.opengl.gl21.universe;

import com.arielcarrizo.gameengine.renderengine.backend.opengl.gl21.GL21;
import com.arielcarrizo.gameengine.renderengine.backend.opengl.gl21.GLObject;
import com.arielcarrizo.gameengine.renderengine.backend.opengl.gl21.Gl21RenderEngineBackendObject;
import com.arielcarrizo.gameengine.renderengine.backend.opengl.gl21.Gl21RenderEngineBackendSystem;
import com.esferixis.gameengine.renderengine.backend.misc.mesh.Mesh;
import com.esferixis.gameengine.renderengine.backend.space.mesh.GeometryRenderer3d;
import com.esferixis.math.Matrix4f;
import com.esferixis.math.Vector3f;

public abstract class GL3dGeometryRenderer<D extends Mesh.Data<Vector3f, ?>> extends Gl21RenderEngineBackendObject implements GeometryRenderer3d<D> {
	public static abstract class Factory<D extends Mesh.Data<Vector3f, ?>> extends Gl21RenderEngineBackendObject {
		protected final boolean activateDepthWrites;
		
		/**
		 * @post Crea la fábrica con el sistema de backend del engine de renderización especificado e indicando si permite las escrituras
		 * 		 al z-buffer
		 */
		public Factory(Gl21RenderEngineBackendSystem renderEngineBackendSystem, boolean activateDepthWrites) {
			super(renderEngineBackendSystem);
			this.activateDepthWrites = activateDepthWrites;
		}
		
		/**
		 * @post Crea el renderizador con la matriz de proyección y la indicación de si debe activar las escrituras al z-buffer}
		 * 		 especificadas
		 */
		public abstract GL3dGeometryRenderer<D> create(Matrix4f projectedViewTransformMatrix);
	}
	
	/**
	 * @post Crea el renderizador con el backend del motor de rendering especificado
	 * @param renderEngineBackend
	 */
	public GL3dGeometryRenderer(Gl21RenderEngineBackendSystem renderEngineBackend) {
		super(renderEngineBackend);
	}

	/* (non-Javadoc)
	 * @see com.esferixis.gameengine.renderengine.backend.mesh.GeometryRenderer#setCullFace(com.esferixis.gameengine.renderengine.backend.mesh.GeometryRenderer.CullFace)
	 */
	@Override
	public void setCullFace(CullFace cullFace) {
		if ( cullFace != CullFace.NONE ) {
			this.gl.glEnable(GL21.GL_CULL_FACE);
			
			final int mode;
			switch ( cullFace ) {
			case FRONT:
				mode = GL21.GL_FRONT;
				break;
			case BACK:
				mode = GL21.GL_BACK;
				break;
			default:
				throw new RuntimeException("Unexpected value");
			}
			
			this.gl.glCullFace(mode);
		}
		else {
			this.gl.glDisable(GL21.GL_CULL_FACE);
		}
	}
	
	/**
	 * @post Destruye el render
	 */
	public abstract void destroy();
}
