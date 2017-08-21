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

import java.io.IOException;

import com.arielcarrizo.gameengine.renderengine.backend.opengl.GLException;
import com.arielcarrizo.gameengine.renderengine.backend.opengl.gl21.GL21;
import com.esferixis.gameengine.renderengine.backend.misc.mesh.Mesh;
import com.esferixis.gameengine.renderengine.backend.misc.mesh.MeshTriangleVertex;
import com.esferixis.math.Vectorf;

abstract class AttributeVboGenerator<V extends Vectorf, P extends MeshTriangleVertex<V>> {
	protected final int sizeperpoint;
	protected final int glDataType;
	
	/**
	 * @post Crea el generador, indicando el tamaño por punto y el tipo de dato OpenGL
	 */
	protected AttributeVboGenerator(int sizeperpoint, int glDataType) {
		this.sizeperpoint = sizeperpoint;
		this.glDataType = glDataType;
	}
	
	/**
	 * @post Devuelve el tamaño por punto
	 */
	public int getSizePerPoint() {
		return this.sizeperpoint;
	}
	
	/**
	 * @post Devuelve el tipo de dato OpenGL
	 */
	public int getGLDataType() {
		return this.glDataType;
	}
	
	/**
	 * @pre La implementación de OpenGL ni la malla pueden ser nulas
	 * @post Genera el VBO de la malla especificada
	 * @param triangles
	 * @return
	 * @throws IOException, GLException, NullPointerException
	 */
	public abstract <L extends P> int generateVbo(GL21 gl, Mesh.Data<V, L> meshData) throws GLException, NullPointerException;
}
