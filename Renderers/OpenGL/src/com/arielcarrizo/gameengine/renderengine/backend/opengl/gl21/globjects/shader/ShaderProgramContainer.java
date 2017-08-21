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

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;

import com.arielcarrizo.gameengine.renderengine.backend.opengl.GLException;
import com.arielcarrizo.gameengine.renderengine.backend.opengl.gl21.GL21;
import com.arielcarrizo.gameengine.renderengine.backend.opengl.gl21.GLObject;

public abstract class ShaderProgramContainer {
	private boolean isDestroyed;
	
	private final ShaderProgram shaderProgram;
	
	private final ShaderProgram.Uniform transformMatrixUniform;
	private final ShaderProgram.Attribute vertexPositionAttribute;
	
	private final List<ShaderProgram.Attribute> attributes;
	
	public static class Switcher extends GLObject {
		private ShaderProgramContainer selectedContainer;
		
		/**
		 * @post Crea el switcher con la implementación de OpenGL especificada
		 */
		public Switcher(GL21 gl) {
			super(gl);
			this.selectedContainer = null;
		}
		
		/**
		 * @pre El contenedor no puede ser nulo
		 * @post Selecciona el contenedor especificado
		 */
		public void select(ShaderProgramContainer container) {
			if ( container != null ) {
				if ( container != this.selectedContainer ) {
					if ( this.selectedContainer != null )
						this.disableVertexAttribArrays();
					
					this.selectedContainer = container;
					
					this.selectedContainer.shaderProgram.use();
					
					this.enableVertexAttribArrays();
				}
			}
			else {
				throw new NullPointerException();
			}
		}
		
		/**
		 * @post Invalida la selección del contenedor actual
		 */
		public void invalidateSelection() {
			if ( this.selectedContainer != null ) {
				this.disableVertexAttribArrays();
				this.selectedContainer = null;
			}
		}
		
		/**
		 * @post Activa los array de atributos
		 */
		private void enableVertexAttribArrays() {
			for ( ShaderProgram.Attribute eachAttribute : this.selectedContainer.attributes ) {
				this.gl.glEnableVertexAttribArray(eachAttribute.getLocation());
			}
		}
		
		/**
		 * @post Desactiva los array de atributos
		 */
		private void disableVertexAttribArrays() {
			for ( ShaderProgram.Attribute eachAttribute : this.selectedContainer.attributes ) {
				this.gl.glDisableVertexAttribArray(eachAttribute.getLocation());
			}
		}
	}
	
	/**
	 * @post Crea el contenedor
	 */
	protected ShaderProgramContainer(VertexShader vertexShader, FragmentShader fragmentShader, Collection<ShaderProgram.Attribute> specificAttributes, Collection<ShaderProgram.Uniform> specificUniforms) throws GLException {
		final List<ShaderProgram.Uniform> uniforms;
		
		if ( specificUniforms == null ) {
			uniforms = new ArrayList<ShaderProgram.Uniform>(1);
		}
		else {
			uniforms = new ArrayList<ShaderProgram.Uniform>(specificUniforms);
		}
		
		final List<ShaderProgram.Attribute> attributes;
		if ( specificAttributes == null ) {
			attributes = new ArrayList<ShaderProgram.Attribute>(1);
		}
		else {
			attributes = new ArrayList<ShaderProgram.Attribute>(specificAttributes);
		}
		
		this.transformMatrixUniform = new ShaderProgram.Uniform("transformMatrix");
		this.vertexPositionAttribute = new ShaderProgram.Attribute("vertexPosition");
		
		uniforms.add(this.transformMatrixUniform);
		attributes.add(this.vertexPositionAttribute);
		
		this.attributes = Collections.unmodifiableList(attributes);
		
		this.shaderProgram = new ShaderProgram(vertexShader, fragmentShader, attributes.toArray(new ShaderProgram.Attribute[0]), uniforms.toArray(new ShaderProgram.Uniform[0]));
		
		this.isDestroyed = false;
	}
	
	/**
	 * @post Verifica que no haya sido destruido
	 */
	protected final void checkDestroyed() {
		if ( this.isDestroyed ) {
			throw new IllegalStateException("Cannot use an destroyed shader program container");
		}
	}
	
	/**
	 * @post Devuelve el "uniform" de transformación de matriz
	 */
	public ShaderProgram.Uniform getTransformMatrixUniform() {
		this.checkDestroyed();
		
		return this.transformMatrixUniform;
	}
	
	/**
	 * @post Devuelve el atributo de posición de vértice
	 */
	public ShaderProgram.Attribute getVertexPositionAttribute() {
		this.checkDestroyed();
		
		return this.vertexPositionAttribute;
	}
	
	/**
	 * @post Destruye el contenedor con todo lo que hay en él
	 */
	protected void destroy() {
		this.shaderProgram.destroy();
		
		this.isDestroyed = true;
	}
}
