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
package com.arielcarrizo.gameengine.renderengine.backend.opengl.gl21.globjects.shader.exception;

import com.arielcarrizo.gameengine.renderengine.backend.opengl.GLException;
import com.arielcarrizo.gameengine.renderengine.backend.opengl.gl21.globjects.shader.Shader;

/**
 * Excepción de compilación de shader
 */
public class GLShaderCompileException extends GLException {
	/**
	 * 
	 */
	private static final long serialVersionUID = 6102136728434223585L;
	
	private final Class<? extends Shader> shaderClass;
	private final String source;
	
	/**
	 * @pre El mensaje, la clase de shader y el código fuente no pueden ser nulos
	 * @post Crea la excepción de shader con el mensaje, la clase de shader y el código fuente especificados
	 * @param message
	 */
	public GLShaderCompileException(String message, Class<? extends Shader> shaderClass, String source) {
		super(message);
		if ( (shaderClass != null) && ( source != null ) ) {
			this.shaderClass = shaderClass;
			this.source = source;
		}
		else {
			throw new NullPointerException();
		}
	}
	
	/**
	 * @post Devuelve la clase de shader
	 */
	public Class<? extends Shader> getShaderClass() {
		return this.shaderClass;
	}
	
	/**
	 * @post Devuelve el código fuente involucrado
	 */
	public String getSource() {
		return this.source;
	}
}
