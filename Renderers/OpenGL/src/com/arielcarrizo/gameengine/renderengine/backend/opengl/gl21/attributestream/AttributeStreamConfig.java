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

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

import com.arielcarrizo.gameengine.renderengine.backend.opengl.gl21.globjects.shader.ShaderProgram;
import com.esferixis.misc.map.ArrayMap;

public final class AttributeStreamConfig {
	private final Map<AttributeStream<?>, ShaderProgram.Attribute> locationPerAttributeStream;
	
	public static final class Essence {
		private Map<AttributeStream<?>, ShaderProgram.Attribute> locationPerAttributeStream;
		
		public Essence() {
			this.locationPerAttributeStream = new HashMap<AttributeStream<?>, ShaderProgram.Attribute>();
		}
		
		/**
		 * @pre El stream de atributo no puede ser nulo
		 * @post Agrega un stream de atributo al atributo especificado
		 */
		public void addAttributeStream(AttributeStream<?> attributeStream, ShaderProgram.Attribute attribute) {
			if ( this.locationPerAttributeStream != null ) {
				if ( attributeStream != null ) {
					if ( this.locationPerAttributeStream.put(attributeStream, attribute) != null ) {
						throw new IllegalStateException("Attribute stream has been added");
					}
				}
				else {
					throw new NullPointerException();
				}
			}
			else {
				throw new IllegalStateException("Cannot add stream attribute when the essence has been used");
			}
		}
	};
	
	public AttributeStreamConfig(Essence essence) {
		if ( essence != null ) {
			this.locationPerAttributeStream = Collections.unmodifiableMap( new ArrayMap<AttributeStream<?>, ShaderProgram.Attribute>(essence.locationPerAttributeStream) );
			essence.locationPerAttributeStream = null;
		}
		else {
			throw new NullPointerException();
		}
	}
	
	/**
	 * @post Devuelve el mapa de atributos por stream de atributo
	 */
	Map<AttributeStream<?>, ShaderProgram.Attribute> getLocationPerAttributeStream() {
		return this.locationPerAttributeStream;
	}
}
