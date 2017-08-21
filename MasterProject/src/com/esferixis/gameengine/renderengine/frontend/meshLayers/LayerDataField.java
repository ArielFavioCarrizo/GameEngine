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
package com.esferixis.gameengine.renderengine.frontend.meshLayers;

import java.io.Serializable;

import com.esferixis.misc.Property.Setteable;
import com.esferixis.misc.constraints.Constraint;

/**
 * @author ariel
 *
 */
public abstract class LayerDataField<T> implements Serializable {
	/**
	 * 
	 */
	private static final long serialVersionUID = -199517029089953289L;
	
	private MeshLayersConfig meshLayersConfig;
	private int id;
	
	private final Constraint<? super T> constraint;
	
	private transient Setteable<T> setteable;
	
	/**
	 * @post Crea el miembro especificado, con la restricción especificada.
	 * 		 Si es nula, se considera que no tiene
	 */
	LayerDataField(Constraint<? super T> constraint) {
		this.meshLayersConfig = null;
		this.id = 0;
		
		this.constraint = constraint;
	}
	
	/**
	 * @post Inicializa el setteable
	 */
	void initSetteable(Setteable<T> setteable) {
		if ( this.setteable == null ) {
			this.setteable = setteable;
		}
		else {
			throw new IllegalStateException("Cannot initialize an setteable when it has been initialized");
		}
	}
	
	/**
	 * @post Especifica el valor
	 */
	void setValue(T value) {
		if ( this.setteable != null ) {
			this.setteable.set(value);
		}
		else {
			throw new IllegalStateException("Expected initialized setteable");
		}
	}
	
	/**
	 * @post Crea el miembro especificado, sin restricciones
	 */
	public LayerDataField() {
		this(null);
	}
	
	/**
	 * @post Asigna la configuración de capas especificada
	 */
	void attachMeshLayersConfig(MeshLayersConfig meshLayersConfig) {
		if ( meshLayersConfig != this.meshLayersConfig ) {
			if ( this.meshLayersConfig == null ) {
				this.meshLayersConfig = meshLayersConfig;
				this.id = this.meshLayersConfig.createFieldId();
			}
			else {
				throw new IllegalStateException("Cannot share layer data fields between different mesh layers configs");
			}
		}
	}
	
	/**
	 * @post Desasigna la configuración de capas especificada
	 */
	void detachMeshLayersConfig() {
		this.meshLayersConfig = null;
	}
	
	/**
	 * @post Devuelve la configuración de capas a la que pertenece.
	 * 		 Si no fue usado todavía en la creación de la configuración de
	 * 		 capas, devuelve null
	 */
	public MeshLayersConfig getMeshLayersConfig() {
		return this.meshLayersConfig;
	}
	
	/**
	 * @post Chequea el valor especificado
	 */
	protected final void checkValue(T value) {
		if ( this.constraint != null ) {
			this.constraint.checkValue(value);
		}
	}
	
	/**
	 * @post Devuelve el hash
	 */
	@Override
	public int hashCode() {
		return this.getClass().hashCode();
	}
	
	/**
	 * @pre No puede ser nulo
	 * @post Devuelve si son iguales en apariencia, teniendo en cuenta el id
	 * 		 y la clase
	 */
	protected boolean fuzzyEquals(LayerDataField<?> other) {
		if ( other != null ) {
			return other.getClass().equals(this.getClass()) && ( other.id == this.id );
		}
		else {
			throw new NullPointerException();
		}
	}
	
	/**
	 * @post Devuelve si es igual al objeto especificado.
	 * 		 Si no ha sido empleado en la creación de configuración
	 * 		 de capa, sólo es igual si se trata de la misma
	 * 		 instancia
	 */
	@Override
	public boolean equals(Object other) {
		if ( this.meshLayersConfig != null ) {
			if ( ( other != null ) && other.getClass().equals(this.getClass()) ) {
				final LayerDataField<?> otherField = (LayerDataField<?>) other;
				
				if ( this.getMeshLayersConfig().equals(otherField.getMeshLayersConfig()) ) {
					return ( otherField.id == this.id );
				}
				else {
					return false;
				}
			}
			else {
				return false;
			}
		}
		else {
			return (other == this);
		}
	}
}
