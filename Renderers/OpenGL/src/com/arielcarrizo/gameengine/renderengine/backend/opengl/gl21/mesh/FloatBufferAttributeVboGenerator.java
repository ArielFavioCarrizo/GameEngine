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
import java.nio.FloatBuffer;
import java.util.List;

import com.arielcarrizo.gameengine.renderengine.backend.opengl.GLException;
import com.arielcarrizo.gameengine.renderengine.backend.opengl.gl21.GL21;
import com.esferixis.gameengine.renderengine.backend.misc.mesh.Mesh;
import com.esferixis.gameengine.renderengine.backend.misc.mesh.MeshTriangle;
import com.esferixis.gameengine.renderengine.backend.misc.mesh.MeshTriangleVertex;
import com.esferixis.math.Vector2f;
import com.esferixis.math.Vectorf;
import com.esferixis.misc.ElementProcessor;
import com.esferixis.misc.nio.BufferUtils;

public abstract class FloatBufferAttributeVboGenerator<V extends Vectorf, P extends MeshTriangleVertex<V>> extends AttributeVboGenerator<V, P> {
	private final ElementProcessor<P, V> pointProcessor;
	
	/**
	 * @pre El procesador de puntos no puede ser nulo
	 * @post Crea el generador con el tamaño por punto y el procesador
	 * 		 de puntos especificado
	 */
	protected FloatBufferAttributeVboGenerator(int sizeperpoint, ElementProcessor<P, V> pointProcessor) {
		super(sizeperpoint, GL21.GL_FLOAT);
		if ( pointProcessor != null ) {
			this.pointProcessor = pointProcessor;
		}
		else {
			throw new NullPointerException();
		}
	}
	
	private final <L extends P> FloatBuffer generateBuffer(List< MeshTriangle<V, L> > list) {
		FloatBuffer buffer = BufferUtils.createFloatBuffer(list.size()*this.sizeperpoint*3);
		for ( MeshTriangle<V, ? extends P> eachTriangle : list ) {
			for ( P eachPoint : eachTriangle.getPoints() ) {
				this.pointProcessor.process(eachPoint).store(buffer);
			}
		}
		
		buffer.flip();
		buffer.rewind();
		
		return buffer;
	}
	
	/**
	 * @pre La implementación de OpenGL ni la malla pueden ser nulas
	 * @post Genera el VBO con los datos de la malla especificada
	 * @param triangles
	 * @return
	 * @throws IOException, GLException, NullPointerException
	 */
	@Override
	public <L extends P> int generateVbo(GL21 gl, Mesh.Data<V, L> meshData) throws GLException, NullPointerException {
		if ( ( gl != null ) && ( meshData != null )) {
			int vboId = gl.glGenBuffers();
			gl.glBindBuffer(GL21.GL_ARRAY_BUFFER, vboId);
			
			gl.glBufferData(GL21.GL_ARRAY_BUFFER, this.generateBuffer(meshData.getTriangles()), GL21.GL_STATIC_DRAW);
			
			gl.glBindBuffer(GL21.GL_ARRAY_BUFFER, 0);
			
			return vboId;
		}
		else {
			throw new NullPointerException();
		}
	}
}
