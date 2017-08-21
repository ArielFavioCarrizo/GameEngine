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
package com.arielcarrizo.gameengine.renderengine.backend.opengl.gl21;

import com.arielcarrizo.gameengine.renderengine.backend.opengl.gl21.errorchecker.GLErrorChecker;
import com.arielcarrizo.gameengine.renderengine.backend.opengl.gl21.errorchecker.GLErrorException;

public abstract class GLObject {
	protected final GL21 gl;
	
	/**
	 * @pre La implementación de OpenGL no puede ser nula
	 * @post Crea el objeto OpenGL con la implementación especificada
	 */
	public GLObject(GL21 gl) {
		if ( gl != null ) {
			this.gl = gl;
		}
		else {
			throw new NullPointerException();
		}
	}
	
	/**
	 * @post Devuelve la de implementación OpenGL
	 */
	public final GL21 getGL() {
		return this.gl;
	}
	
	/**
	 * @pre El otro objeto no puede ser nulo
	 * @post Comprueba que sea compatible con el objeto especificado,
	 * 		 si no lo es lanza IllegalArgumentException
	 */
	public void checkGL(GLObject other) throws NullPointerException, IllegalArgumentException {
		if ( other != null ) {
			if ( this.getGL() != other.getGL() ) {
				throw new IllegalArgumentException("OpenGL implementation mismatch");
			}
		}
		else {
			throw new NullPointerException();
		}
	}
	
	/**
	 * @post Si hubo un error de OpenGL, lanza una excepción
	 */
	protected final void checkGLError() {
		final int errorCode = this.gl.glGetError();
		
		if ( errorCode != GL21.GL_NO_ERROR ) {
			throw new GLErrorException(errorCode, this.gl.getErrorString(errorCode));
		}
	}
}
