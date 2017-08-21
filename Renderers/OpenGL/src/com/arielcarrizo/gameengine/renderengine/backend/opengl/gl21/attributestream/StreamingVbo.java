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
package com.arielcarrizo.gameengine.renderengine.backend.opengl.gl21.attributestream;

import java.nio.ByteBuffer;

import com.arielcarrizo.gameengine.renderengine.backend.opengl.gl21.GL21;
import com.arielcarrizo.gameengine.renderengine.backend.opengl.gl21.GLObject;
import com.arielcarrizo.gameengine.renderengine.backend.opengl.gl21.errorchecker.GLErrorChecker;
import com.esferixis.misc.nio.BufferUtils;

final class StreamingVbo extends GLObject {
	private int vboId;
	
	private final ByteBuffer byteBuffer;
	
	private final int componentsPerVertex;
	private final int glType;
	
	private static final boolean simpleUpload = false;
	
	/**
	 * @post Crea el vbo de streaming con el tamaño y el tipo especificado
	 * @param size
	 */
	public StreamingVbo(GL21 gl, int sizeInBytes, int componentsPerVertex, int glType) {
		super(gl);
		if ( sizeInBytes > 0 ) {
			this.componentsPerVertex = componentsPerVertex;
			this.glType = glType;
			
			this.byteBuffer = BufferUtils.createByteBuffer(sizeInBytes);
			
			this.vboId = this.gl.glGenBuffers();
			
			if ( !simpleUpload ) {
				this.gl.glBindBuffer(GL21.GL_ARRAY_BUFFER, this.vboId);
				this.gl.glBufferData(GL21.GL_ARRAY_BUFFER, this.byteBuffer.capacity(), GL21.GL_DYNAMIC_DRAW);
				this.checkGLError();
			}
		}
		else {
			throw new IllegalArgumentException("Invalid buffer size");
		}
	}
	
	/**
	 * @post Devuelve el id
	 */
	public int getId() {
		return this.vboId;
	}
	
	/**
	 * @post Actualiza el buffer hasta la posición de escritura,
	 * 		 y "rebobina" la posición de escritura.
	 */
	public void update() {
		this.gl.glBindBuffer(GL21.GL_ARRAY_BUFFER, this.vboId);
		
		this.byteBuffer.flip();
		this.byteBuffer.rewind();
		
		if ( simpleUpload ) {
			this.gl.glBufferData(GL21.GL_ARRAY_BUFFER, this.byteBuffer, GL21.GL_DYNAMIC_DRAW);
			this.checkGLError();
		}
		else {
			this.gl.glBufferData(GL21.GL_ARRAY_BUFFER, this.byteBuffer.capacity(), GL21.GL_DYNAMIC_DRAW);
			this.gl.glBufferSubData(GL21.GL_ARRAY_BUFFER, 0, this.byteBuffer);
		}
		
		this.byteBuffer.clear();
	}
	
	/**
	 * @post Asigna el VBO a la ubicación de atributo especificada
	 */
	public void attachAttribute(int location) {
		this.gl.glBindBuffer(GL21.GL_ARRAY_BUFFER, this.vboId);
		this.gl.glVertexAttribPointer(location, this.componentsPerVertex, this.glType, false, 0, 0);
	}
	
	/**
	 * @post Devuelve el buffer
	 */
	public ByteBuffer getByteBuffer() {
		return this.byteBuffer;
	}
	
	/**
	 * @post Destruye el vbo
	 */
	public void destroy() {
		this.gl.glDeleteBuffers(this.vboId);
		this.vboId = 0;
	}
}
