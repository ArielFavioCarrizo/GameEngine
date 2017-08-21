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
package com.arielcarrizo.gameengine.renderengine.backend.opengl.gl21.meshLayers;

import com.arielcarrizo.gameengine.renderengine.backend.opengl.gl21.globjects.shader.ShaderProgram;

public final class TextureComponent {
	enum Status {
		NOTINITIALIZED,
		INITIALIZED,
		DESTROYED
	}
	private Status status;
	
	private ShaderProgram.Uniform sampler;
	private ShaderProgram.Attribute coordinatesAttribute;
	
	/**
	 * @post Crea el componente de textura especificado
	 */
	public TextureComponent() {
		this.status = Status.NOTINITIALIZED;
		
		this.sampler = null;
		this.coordinatesAttribute = null;
	}
	
	private void checkInitialized() {
		if ( this.status == Status.NOTINITIALIZED ) {
			throw new IllegalStateException("Expected initialized texture component");
		}
		else if ( this.status == Status.DESTROYED ) {
			throw new IllegalStateException("Expected nondestroyed texture component");
		}
	}
	
	/**
	 * @post Inicializa el componente de textura
	 */
	void initialize(ShaderProgram.Uniform sampler, ShaderProgram.Attribute coordinatesAttribute) {
		if ( this.status == Status.NOTINITIALIZED ) {
			throw new IllegalStateException("Attemped to initialize an initialized texture component");
		}
		else if ( this.status == Status.DESTROYED ) {
			throw new IllegalStateException("Attemped to initialize an destroyed texture component");
		}
	}
	
	/**
	 * @pre El componente de textura tiene que estar inicializado
	 * @post Devuelve el sampler
	 */
	public ShaderProgram.Uniform getSampler() {
		this.checkInitialized();
		return this.sampler;
	}
	
	/**
	 * @pre El componente de textura tiene que estar inicializado
	 * @post Devuelve el atributo de coordenadas
	 */
	public ShaderProgram.Attribute getCoordinatesAttribute() {
		this.checkInitialized();
		return this.coordinatesAttribute;
	}
	
	/**
	 * @post Destuye el componente de textura
	 */
	void destroy() {
		this.checkInitialized();
		this.status = Status.DESTROYED;
	}
}
