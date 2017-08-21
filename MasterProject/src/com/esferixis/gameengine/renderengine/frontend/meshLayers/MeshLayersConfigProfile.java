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
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.HashSet;
import java.util.Set;

import com.esferixis.gameengine.renderengine.frontend.RenderEngineFrontendLoadableObject;
import com.esferixis.misc.accesor.AccesorHolder;
import com.esferixis.misc.accesor.AccesorWhiteList;

/**
 * Perfil de configuración de capas
 * 
 * @author ariel
 *
 */
public final class MeshLayersConfigProfile implements Serializable {
	/**
	 * 
	 */
	private static final long serialVersionUID = 5075411308085049705L;
	
	private final MeshLayersConfig meshLayersConfig;
	private final Collection<LayerDataFieldValue<?>> fieldValues;
	
	// Clase de acceso de implementación
	public static final class Accesor {
		private Accesor() {}
		
		@AccesorWhiteList
		private static final Class<?>[] packageAccesors = new Class[] {
			com.esferixis.gameengine.renderengine.frontend.renderingFrame.PackageAccesors.class
		};
		
		/**
		 * @post Prepara su uso y devuelve las dependencias
		 */
		public void prepareForUse(MeshLayersConfigProfile meshLayersConfigProfile) {
			if ( meshLayersConfigProfile != null ) {
				meshLayersConfigProfile.prepareForUse();
			}
			else {
				throw new NullPointerException();
			}
		}
	}
	
	/**
	 * @post Crea un perfil de configuración sin campos de valores
	 */
	public MeshLayersConfigProfile(MeshLayersConfig meshLayersConfig) {
		this(meshLayersConfig, (Collection) Collections.emptyList());
	}
	
	/**
	 * @post Crea un perfil de configuración con los valores de miembro especificados
	 * 		 Todos los valores de miembro tienen que pertenecer a la misma configuración de capas,
	 * 		 no puede repetirse ninguno, y no puede faltar ninguno.
	 */
	public MeshLayersConfigProfile(MeshLayersConfig meshLayersConfig, LayerDataFieldValue<?>... fieldValues) {
		this(meshLayersConfig, Arrays.asList(fieldValues));
	}
	
	/**
	 * @post Crea un perfil de configuración con la configuración de capas especificada y los valores de miembro especificados
	 * 		 Todos los valores de miembro tienen que pertenecer a la configuración de capas especificada,
	 * 		 no puede repetirse ninguno, y no puede faltar ninguno.
	 */
	public MeshLayersConfigProfile(MeshLayersConfig meshLayersConfig, Collection<LayerDataFieldValue<?>> fieldValues) {
		if ( ( meshLayersConfig != null ) && ( fieldValues != null ) ) {
			this.meshLayersConfig = meshLayersConfig;
			this.fieldValues = Collections.unmodifiableList(new ArrayList<LayerDataFieldValue<?>>(fieldValues));
			
			if ( !this.fieldValues.isEmpty() ) {
				final Set<LayerDataField<?>> addedFields = new HashSet<LayerDataField<?>>();
				
				for ( LayerDataFieldValue<?> eachValue : this.fieldValues ) {
					if ( !eachValue.getField().getMeshLayersConfig().equals(this.meshLayersConfig) ) {
						throw new IllegalArgumentException("Mesh layers config mismatch");
					}
					
					if ( !addedFields.add(eachValue.getField()) ) {
						throw new IllegalArgumentException("Ambiguous field values");
					}
				}
				
				if ( addedFields.size() != meshLayersConfig.getFieldCount() ) {
					throw new IllegalArgumentException("Missing fields");
				}
			}
		}
		else {
			throw new NullPointerException();
		}
	}
	
	/**
	 * @post Devuelve los valores de miembro
	 */
	Collection<LayerDataFieldValue<?>> getFieldValues() {
		return this.fieldValues;
	}
	
	/**
	 * @post Devuelve la configuración de capas asociada.
	 */
	public MeshLayersConfig getMeshLayersConfig() {
		return this.meshLayersConfig;
	}
	
	/**
	 * @post Prepara su uso y devuelve las dependencias
	 */
	private void prepareForUse() {
		for ( LayerDataFieldValue<?> eachValue : this.fieldValues ) {
			( ( LayerDataField ) eachValue.getField()).setValue(eachValue.getValue());
		}
	}
}
