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

import com.arielcarrizo.gameengine.renderengine.backend.opengl.GLException;
import com.arielcarrizo.gameengine.renderengine.backend.opengl.gl21.GL21;
import com.arielcarrizo.gameengine.renderengine.backend.opengl.gl21.GLObject;
import com.esferixis.gameengine.renderengine.backend.misc.mesh.Mesh;
import com.esferixis.gameengine.renderengine.backend.misc.mesh.MeshTriangleVertex;
import com.esferixis.math.Vectorf;

public final class GLAttribute extends GLObject {
	private final int attributeSize;
	private final int dataType;
	
	private int vboId;
	
	public class Manager {
		private Manager() {
			
		}
		
		/**
		 * @post Devuelve el atributo
		 */
		public GLAttribute getAttribute() {
			return GLAttribute.this;
		}
		
		/**
		 * @post Destruye el atributo
		 */
		public void destroy() {
			GLAttribute.this.gl.glDeleteBuffers(GLAttribute.this.vboId);
			GLAttribute.this.vboId = 0;
		}
	}
	
	/**
	 * @post Crea el atributo y devuelve su administrador
	 * @param gl
	 * @param meshData
	 * @param vboGenerator
	 * @param attributeSize
	 * @param dataType
	 * @throws GLException
	 */
	public static <V extends Vectorf, P extends MeshTriangleVertex<V>, D extends Mesh.Data<V, P>> GLAttribute.Manager create(GL21 gl, D meshData, AttributeVboGenerator<V, ? super P> vboGenerator) throws GLException {
		return ( new GLAttribute(gl, meshData, vboGenerator) ).new Manager();
	}
	
	private <V extends Vectorf, P extends MeshTriangleVertex<V>, D extends Mesh.Data<V, P>> GLAttribute(GL21 gl, D meshData, AttributeVboGenerator<V, ? super P> vboGenerator) throws GLException {
		super(gl);
		
		this.attributeSize = vboGenerator.getSizePerPoint();
		this.dataType = vboGenerator.glDataType;
		
		this.vboId = vboGenerator.generateVbo(this.gl, meshData);
	}
	
	/**
	 * @post Envía el atributo con la ubicación especificada
	 */
	protected final void send(int location) {
		if ( this.vboId != 0 ) {
			this.gl.glBindBuffer(GL21.GL_ARRAY_BUFFER, this.vboId);
			this.gl.glVertexAttribPointer(location, this.attributeSize, this.dataType, false, 0, 0);
		}
		else {
			throw new IllegalStateException("Cannot use destroyed attribute");
		}
	}
}
