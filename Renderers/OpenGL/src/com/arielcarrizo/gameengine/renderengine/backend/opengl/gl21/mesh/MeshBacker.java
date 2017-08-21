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
package com.arielcarrizo.gameengine.renderengine.backend.opengl.gl21.mesh;

import java.util.logging.Logger;

import com.arielcarrizo.gameengine.renderengine.backend.opengl.GLException;
import com.arielcarrizo.gameengine.renderengine.backend.opengl.gl21.GL21;
import com.arielcarrizo.gameengine.renderengine.backend.opengl.gl21.GLObject;
import com.arielcarrizo.gameengine.renderengine.backend.opengl.gl21.Gl21RenderEngineBackendObject;
import com.arielcarrizo.gameengine.renderengine.backend.opengl.gl21.Gl21RenderEngineBackendSystem;
import com.esferixis.gameengine.renderengine.backend.misc.mesh.Mesh;
import com.esferixis.gameengine.renderengine.backend.misc.mesh.MeshTriangleVertex;
import com.esferixis.gameengine.renderengine.backend.misc.mesh.Mesh.Data;
import com.esferixis.math.Vector2f;
import com.esferixis.math.Vector3f;
import com.esferixis.math.Vectorf;
import com.esferixis.misc.ElementCallback;
import com.esferixis.misc.ElementProcessor;

/**
 * "Backer" de la malla
 */
public abstract class MeshBacker<V extends Vectorf, P extends MeshTriangleVertex<V>, D extends Mesh.Data<V, P>> extends Gl21RenderEngineBackendObject {
	protected final static Logger LOGGER = Logger.getLogger(Logger.GLOBAL_LOGGER_NAME);
	
	public final class AttributeSender {
		private boolean validContext;
		
		/**
		 * @post Crea el enviador
		 * @param attribute
		 * @param location
		 */
		private AttributeSender() {
			this.validContext = true;
		}
		
		/**
		 * @pre El atributo no tiene que ser nulo
		 * @param attribute
		 * @param location
		 */
		public void send(GLAttribute attribute, int location) {
			if ( this.validContext ) {
				if ( attribute != null ) {
					attribute.send(location);
				}
				else {
					throw new NullPointerException();
				}
			}
			else {
				throw new IllegalStateException("Invalid context");
			}
		}
		
		/**
		 * @post Sale del contexto
		 */
		private void exitContext() {
			this.validContext = false;
		}
	}
	
	private boolean destroyed;
	private final GLAttribute.Manager positionAttributeManager;
	private final int trianglesCount;
	
	/**
	 * @pre La implementación de OpenGL y la malla no pueden ser nulas
	 * @post Crea el "backer" con la malla especificada
	 * @param essence
	 */
	public MeshBacker(Gl21RenderEngineBackendSystem renderEngineBackend, D meshData) throws GLException {
		super(renderEngineBackend);
		if ( meshData != null ) {
			this.destroyed = false;
			
			this.positionAttributeManager = meshData.accept(new Mesh.Data.DimensionVisitor<GLAttribute.Manager, GLException>() {

				@Override
				public GLAttribute.Manager visit3d(Data<Vector3f, MeshTriangleVertex<Vector3f>> meshData) throws GLException {
					return GLAttribute.create(gl, meshData, new Vector3fPropertyFloatBufferAttributeVboGenerator<MeshTriangleVertex<Vector3f>>(new ElementProcessor<MeshTriangleVertex<Vector3f>, Vector3f>() {

						@Override
						public Vector3f process(MeshTriangleVertex<Vector3f> point) {
							return point.getPosition();
						} 
						
					}));
				}

				@Override
				public GLAttribute.Manager visit2d(Data<Vector2f, MeshTriangleVertex<Vector2f>> meshData) throws GLException {
					return GLAttribute.create(gl, meshData, new Vector2fPropertyFloatBufferAttributeVboGenerator<MeshTriangleVertex<Vector2f>>(new ElementProcessor<MeshTriangleVertex<Vector2f>, Vector2f>() {
						
						@Override
						public Vector2f process(MeshTriangleVertex<Vector2f> point) {
							return point.getPosition();
						} 
						
					}));
				}
			});
			
			this.trianglesCount = meshData.getTriangles().size();
		}
		else {
			throw new NullPointerException();
		}
	}
	
	/**
	 * @post Verifica si ha sido destruido
	 */
	protected void checkDestroyed() throws IllegalStateException {
		if ( this.destroyed ) {
			throw new IllegalStateException("Cannot use an destroyed mesh backer");
		}
	}
	
	/**
	 * @post Devuelve la cantidad de triángulos
	 */
	public int getTrianglesCount() {
		this.checkDestroyed();
		return this.trianglesCount;
	}
	
	/**
	 * @post Devuelve el atributo de posición
	 */
	public final GLAttribute getPositionAttribute() {
		this.checkDestroyed();
		return this.positionAttributeManager.getAttribute();
	}
	
	/**
	 * @post Dibuja la malla
	 */
	public final void draw(ElementCallback<AttributeSender> attributeEmmiter) {
		this.checkDestroyed();
		AttributeSender attributeSender = new AttributeSender();
		attributeEmmiter.run(attributeSender);
		attributeSender.exitContext();
		
		this.gl.glDrawArrays(GL21.GL_TRIANGLES, 0, this.trianglesCount * 3);
	}
	
	/**
	 * @post Libera los recursos ocupados por el "backer"
	 */
	public void destroy() throws NullPointerException, IllegalStateException {
		this.checkDestroyed();
		this.positionAttributeManager.destroy();
		this.destroyed = true;
	}
}
