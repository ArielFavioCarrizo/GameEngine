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
package com.arielcarrizo.gameengine.renderengine.backend.opengl.gl21.globjects.shader;

import com.arielcarrizo.gameengine.renderengine.backend.opengl.GLException;
import com.arielcarrizo.gameengine.renderengine.backend.opengl.gl21.GL21;
import com.arielcarrizo.gameengine.renderengine.backend.opengl.gl21.GLObject;
import com.arielcarrizo.gameengine.renderengine.backend.opengl.gl21.globjects.shader.exception.GLShaderCompileException;

public abstract class Shader extends GLObject {
	private int id;
	
	/**
	 * @pre La implementación GL y el código fuente del shader
	 * 		no pueden ser nulos
	 * @post Crea el shader con la implementación de OpenGL, 
	 * 		 el código fuente y el tipo especificados
	 */
	Shader(GL21 gl, CharSequence shaderSource, int type) throws GLException, NullPointerException {
		super(gl);
		if ( shaderSource != null ) {
			this.id = this.gl.glCreateShader(type);
			if ( this.id == 0 ) {
				throw new GLException("Cannot create shader object");
			}
			
			this.gl.glShaderSource(this.id, shaderSource);
			this.gl.glCompileShader(this.id);
			
			if ( this.gl.glGetShaderi(this.id, GL21.GL_COMPILE_STATUS) == GL21.GL_FALSE ) {
				int infoLogLength = this.gl.glGetShaderi(this.id, GL21.GL_INFO_LOG_LENGTH);
				String infoLog = this.gl.glGetShaderInfoLog(this.id, infoLogLength);
				
				this.gl.glDeleteShader(this.id);
				throw new GLShaderCompileException("Cannot compile shader: \"" + infoLog + "\"", this.getClass(), shaderSource.toString());
			}
		}
		else {
			throw new NullPointerException();
		}
	}
	
	/**
	 * @pre El shader no tiene que estar destruido
	 * @post Devuelve el id
	 */
	int getId() {
		if ( this.id != 0 ) {
			return this.id;
		}
		else {
			throw new IllegalStateException("Attemped to reference to an destroyed shader");
		}
	}
	
	/**
	 * @pre El shader no tiene que haber sido destruido
	 * @post Libera los recursos ocupados por el shader
	 */
	public void destroy() throws IllegalStateException {
		if ( this.id != 0 ) {
			this.gl.glDeleteShader(this.id);
			this.id = 0;
		}
		else {
			throw new IllegalStateException("Attemped to destroy an destroyed shader");
		}
	}
}
