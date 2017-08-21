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

import java.util.Collection;

import com.arielcarrizo.gameengine.renderengine.backend.opengl.GLException;
import com.arielcarrizo.gameengine.renderengine.backend.opengl.gl21.GL21;
import com.arielcarrizo.gameengine.renderengine.backend.opengl.gl21.GLObject;

public final class ShaderProgram extends GLObject {
	private int id;
	
	public static final class Attribute {
		private final String sourceName;
		private Integer location;
		
		/**
		 * @pre El nombre no puede ser nulo
		 * @post Crea un atributo con el nombre de código fuente especificado
		 */
		public Attribute(String sourceName) {
			if ( sourceName != null ) {
				this.sourceName = sourceName;
				this.location = null;
			}
			else {
				throw new NullPointerException();
			}
		}
		
		/**
		 * @post Devuelve el nombre del atributo en el código
		 * 		 fuente
		 */
		public String getSourceName() {
			return this.sourceName;
		}
		
		/**
		 * @post Especifica la ubicación del atributo en el shader
		 */
		private void initLocation(int location) {
			if ( this.location == null ) {
				this.location = location;
			}
			else {
				throw new IllegalStateException("Cannot link an linked attribute");
			}
		}
		
		/**
		 * @pre El atributo tiene que haber sido enlazado
		 * 		en el constructor del shader
		 * @post Devuelve la ubicación del atributo
		 */
		public int getLocation() {
			if ( this.location != null ) {
				return this.location;
			}
			else {
				throw new IllegalStateException("Unlinked attribute");
			}
		}
	}
	
	public static final class Uniform {
		private final String sourceName;
		private Integer location;
		
		/**
		 * @pre El nombre no puede ser nulo
		 * @post Crea un "uniform" con el nombre de código fuente especificado
		 */
		public Uniform(String sourceName) {
			if ( sourceName != null ) {
				this.sourceName = sourceName;
				this.location = null;
			}
			else {
				throw new NullPointerException();
			}
		}
		
		/**
		 * @post Devuelve el nombre del atributo en el código
		 * 		 fuente
		 */
		public String getSourceName() {
			return this.sourceName;
		}
		
		/**
		 * @post Especifica la ubicación del uniform en el shader
		 */
		private void initLocation(int location) {
			if ( this.location == null ) {
				this.location = location;
			}
			else {
				throw new IllegalStateException("Cannot link an linked uniform");
			}
		}
		
		/**
		 * @pre El "uniform" tiene que haber sido enlazado
		 * 		en el constructor del shader
		 * @post Devuelve la ubicación del atributo
		 */
		public int getLocation() {
			if ( this.location != null ) {
				return this.location;
			}
			else {
				throw new IllegalStateException("Unlinked uniform");
			}
		}
	}
	
	/**
	 * @pre El shader de vértices y el de fragmentos no pueden ser nulos
	 * @post Crea un programa con los shaders de vértices y de fragmentos
	 * 		 especificados
	 * 
	 * 		 Si se especifica un array de atributos entonces
	 * 		 las ubicaciones de los atributos serán en las posiciones
	 * 		 del array especificado.
	 */
	public ShaderProgram(VertexShader vertexShader, FragmentShader fragmentShader, Attribute[] attributes, Uniform[] uniforms) throws NullPointerException, IllegalArgumentException, GLException {
		super(vertexShader.getGL());
		if ( fragmentShader != null ) {
			vertexShader.checkGL(fragmentShader);
			
			this.id = this.gl.glCreateProgram();
				
			if ( this.id == 0 ) {
				throw new GLException("Cannot create program");
			}
			
			try {
			
				this.gl.glAttachShader(this.id, vertexShader.getId());
				this.gl.glAttachShader(this.id, fragmentShader.getId());
				
				if ( attributes != null ) {
					for ( int i = 0 ; i<attributes.length ; i++ ) {
						this.gl.glBindAttribLocation(this.id, i, attributes[i].getSourceName());
						attributes[i].initLocation(i);
					}
				}
				
				this.gl.glLinkProgram(this.id);
				
				if ( this.gl.glGetProgram(this.id, GL21.GL_LINK_STATUS) == GL21.GL_FALSE ) {
					throw new GLException("Cannot link program");
				}
				
				this.gl.glValidateProgram(this.id);
				
				if ( uniforms != null ) {
					for ( int i = 0 ; i<uniforms.length ; i++ ) {
						uniforms[i].initLocation(this.getUniformLocation(uniforms[i].getSourceName()));
					}
				}
			}
			catch (GLException e) {
				this.gl.glDeleteProgram(this.id);
				throw e;
			}
		}
		else {
			throw new NullPointerException();
		}
	}
	
	/**
	 * @pre Ningún parámetro puede ser nulo
	 * @post Crea un programa con los shaders de vértices y de fragmentos
	 * 		 especificados
	 */
	public ShaderProgram(VertexShader vertexShader, FragmentShader fragmentShader) throws NullPointerException, IllegalArgumentException, GLException {
		this(vertexShader, fragmentShader, null, null);
	}
	
	/**
	 * @post Devuelve el id
	 */
	int getId() {
		if ( this.id != 0 ) {
			return this.id;
		}
		else {
			throw new IllegalStateException("Attemped to reference to an destroyed program");
		}
	}
	
	/**
	 * @post Usa el programa
	 */
	public void use() {
		this.gl.glUseProgram(this.getId());
	}
	
	/**
	 * @pre El nombre no puede ser nulo
	 * @post Devuelve la ubicación de la variable "uniform"
	 * 		 con el nombre especificado
	 */
	public int getUniformLocation(String name) {
		if ( name != null ) {
			int returnValue = this.gl.glGetUniformLocation(this.getId(), name);
			if ( returnValue != -1 ) {
				return returnValue;
			}
			else {
				throw new IllegalStateException("'" + name + "' uniform doesn't exists");
			}
		}
		else {
			throw new NullPointerException();
		}
	}
	
	/**
	 * @pre El nombre no puede ser nulo
	 * @post Devuelve la ubicación del atributo con el
	 * 		 nombre especificado
	 */
	public int getAttributeLocation(String name) {
		if ( name != null ) {
			int returnValue = this.gl.glGetAttribLocation(this.getId(), name);
			if ( returnValue != -1 ) {
				return returnValue;
			}
			else {
				throw new IllegalStateException("'" + name + "' attribute doesn't exists");
			}
		}
		else {
			throw new NullPointerException();
		}
	}
	
	/**
	 * @pre El shader no tiene que haber sido destruido
	 * @post Libera los recursos ocupados por el shader
	 */
	public void destroy() throws IllegalStateException {
		if ( this.id != 0 ) {
			this.gl.glDeleteProgram(this.id);
			this.id = 0;
		}
		else {
			throw new IllegalStateException("Attemped to destroy an destroyed program");
		}
	}
}
