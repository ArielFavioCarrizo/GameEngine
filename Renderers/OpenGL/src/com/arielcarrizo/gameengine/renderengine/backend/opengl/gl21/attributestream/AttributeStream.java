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

import java.util.Collection;
import java.util.List;
import java.util.Map;

import com.arielcarrizo.gameengine.renderengine.backend.opengl.gl21.globjects.shader.ShaderProgram;
import com.esferixis.geometry.plane.finite.FiniteProportionalHolomorphicShape;
import com.esferixis.misc.classextra.ConcreteInstanceClassIndexer;
import com.esferixis.misc.map.ArrayMap;

public abstract class AttributeStream<T> {
	private AttributeStreamManager manager;
	
	private final int attributeSize, componentSize, glType;
	
	private StreamingVbo vbo;
	
	private boolean sended;
	
	private final Map<Object, T> storedValuePerKey;
	private Object keyToStoreValue;
	
	private int realValueSize;
	
	protected interface Visitor<R> {
		public R visit(Vector2fAttributeStream stream);
	}
	
	protected static final ConcreteInstanceClassIndexer<AttributeStream<?>> CONCRETEINSTANCECLASSINDEXER = new ConcreteInstanceClassIndexer<AttributeStream<?>>( (Class< AttributeStream<?> >) (Class<?>) AttributeStream.class, 1) {

		@Override
		public int classIndex(AttributeStream<?> attributeStream) {
			return attributeStream.accept(new Visitor<Integer>() {

				@Override
				public Integer visit(Vector2fAttributeStream stream) {
					return 0;
				}
				
			});
		}
		
	};
	
	/**
	 * @post Crea el stream con el tamaño de atributo y el tipo de datos de OpenGL
	 * 		 especificado
	 */
	AttributeStream(int attributeSize, int componentSize, int glType) {
		if ( ( attributeSize >= 1 ) && ( attributeSize <= 4 ) ) {
			if ( componentSize > 0 ) {
				this.attributeSize = attributeSize;
				this.componentSize = componentSize;
				this.glType = glType;
				
				this.manager = null;
				
				this.storedValuePerKey = new ArrayMap<Object, T>();
			}
			else {
				throw new IllegalArgumentException("Illegal component size");
			}
		}
		else {
			throw new IllegalArgumentException("Illegal attribute size");
		}
	}
	
	/**
	 * @post Devuelve el administrador
	 */
	AttributeStreamManager getManager() {
		return this.manager;
	}
	
	/**
	 * @post Devuelve el vbo asociado
	 */
	StreamingVbo getVbo() {
		return this.vbo;
	}
	
	/**
	 * @post Activa el stream con el vbo y el atributo especificado
	 */
	void activate(AttributeStreamManager manager, StreamingVbo vbo, ShaderProgram.Attribute attribute) {
		if ( ( manager != null ) && ( vbo != null ) ) {
			if ( this.manager == null ) {
				this.manager = manager;
				this.vbo = vbo;
				
				this.vbo.attachAttribute(attribute.getLocation());
				
				this.keyToStoreValue = null;
				
				final int componentAlignment = this.getManager().getComponentAlignment();
				int bufferComponentsPerValue = (int) (this.getComponentsPerVertex() / componentAlignment) * componentAlignment;
				
				if ( this.getComponentsPerVertex() % componentAlignment != 0 ) {
					bufferComponentsPerValue += componentAlignment;
				}
				
				this.realValueSize = this.getComponentSize() * bufferComponentsPerValue;
			}
			else {
				throw new IllegalStateException("Attemped to activate an stream when it has been activated");
			}
		}
		else {
			throw new NullPointerException();
		}
	}
	
	/**
	 * @post Desactiva el stream y devuelve el vbo
	 */
	StreamingVbo deactivate() {
		if ( this.manager != null ) {
			this.manager = null;
			this.storedValuePerKey.clear();
			this.keyToStoreValue = null;
			
			return this.vbo;
		}
		else {
			throw new IllegalStateException("Attemped to deactivate an stream when it has been deactivated");
		}
	}
	
	/**
	 * @post Develve el tamaño en componentes por vértice
	 */
	protected final int getComponentsPerVertex() {
		return this.attributeSize;
	}
	
	/**
	 * @post Devuelve el tamaño de cada componente
	 */
	protected final int getComponentSize() {
		return this.componentSize;
	}
	
	/**
	 * @post Devuelve el tipo OpenGL de cada componente
	 */
	protected final int getGLType() {
		return this.glType;
	}
	
	/**
	 * @pre Tiene que haber un valor almacenado con la clave especificada,
	 * 		y la clave no puede ser nula
	 * @post Envía el valor almacenado con la clave especificada,
	 * 		 si no existe no hace nada
	 */
	private void sendStoredValueByKey(Object key) {
		if ( key != null ) {
			final T storedValue = this.storedValuePerKey.remove(key);
			if ( storedValue != null ) {
				this.send(storedValue);
			}
			else {
				throw new IllegalStateException("Stored value doesn't exists with this key");
			}
		}
		else {
			throw new NullPointerException();
		}
	}
	
	void sendStoredValuesByKeys(List<Object> keys) {
		for ( Object eachKey : keys ) {
			this.sendStoredValueByKey(eachKey);
		}
	}
	
	/**
	 * @pre No tiene que haber una clave previa
	 * @post Indica que tiene que almacenar el próximo valor con la clave especificada
	 */
	void storeValueByKey(Object key) {
		if ( this.keyToStoreValue == null ) {
			this.keyToStoreValue = key;
		}
		else {
			throw new IllegalStateException("Expected that hasn't pending key to store value");
		}
	}
	
	/**
	 * @pre Tiene que tener una ubicación asignada desde el administrador.
	 * 		Sólo puede enviarse un valor a cada vértice
	 * @post Envía el dato correspondiente al atributo, correspondiente
	 * 		 al vértice en construcción
	 */
	public final void send(T value) {
		if ( this.manager != null ) {
			this.manager.checkStreaming();
			if ( !this.sended ) {
				this.manager.notifySendValueEvent();
				
				int startPosition = this.getVbo().getByteBuffer().position();
				this.send_internal(value);
				this.getVbo().getByteBuffer().position(startPosition + this.realValueSize);
				
				this.sended = true;
				
				if ( this.keyToStoreValue != null ) {
					this.storedValuePerKey.put(this.keyToStoreValue, value);
					this.keyToStoreValue = null;
				}
			}
			else {
				throw new IllegalStateException("Vertex attribute value has been sended");
			}
		}
		else {
			throw new IllegalStateException("Unallocated attribute stream");
		}
	}
	
	/**
	 * @post Devuelve si envió el dato
	 */
	boolean hasSendedValue() {
		return this.sended;
	}
	
	/**
	 * @pre Tiene que tener una ubicación asignada desde el administrador.
	 * 		Sólo puede enviarse un valor a cada vértice
	 * @post Envía el dato correspondiente al atributo, correspondiente
	 * 		 al vértice en construcción
	 */
	protected abstract void send_internal(T value);
	
	/**
	 * @post Notifica que hay un nuevo vértice
	 */
	void notifyNewVertex() {
		this.sended = false;
	}
	
	/**
	 * @post Procesa con el visitor especificado
	 */
	protected abstract <R> R accept(Visitor<R> visitor);
}
