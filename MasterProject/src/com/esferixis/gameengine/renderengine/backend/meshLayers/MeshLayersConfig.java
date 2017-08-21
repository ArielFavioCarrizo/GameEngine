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
package com.esferixis.gameengine.renderengine.backend.meshLayers;

import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.List;

import com.esferixis.gameengine.renderengine.backend.LoadableBackendObject;
import com.esferixis.misc.dynamicFields.DynamicFieldsContainer;

/**
 * Configuración de textura
 * 
 * @author ariel
 *
 */
public final class MeshLayersConfig extends LoadableBackendObject {
	private final DynamicFieldsContainer dynamicFieldsContainer;
	private final List< MeshLayer<?> > layers;
	
	/**
	 * @pre El array de capas no puede ser nulo y tiene que tener por lo menos una capa
	 * @post Crea una configuración de textura con las capas
	 * 		 especificadas
	 * 
	 * 		 El orden de dibujado corresponde al orden en que
	 * 		 se suministran las capas
	 */
	public MeshLayersConfig(Collection<MeshLayer<?>> layers) {
		this(layers.toArray(new MeshLayer[0]));
	}
	
	/**
	 * @pre El array de capas no puede ser nulo y tiene que tener por lo menos una capa
	 * @post Crea una configuración de textura con las capas
	 * 		 especificadas
	 * 
	 * 		 El orden de dibujado corresponde al orden en que
	 * 		 se suministran las capas
	 */
	public MeshLayersConfig(MeshLayer<?>... layers) {
		if ( layers != null ) {
			if ( layers.length > 0 ) {
				this.layers = Collections.unmodifiableList( Arrays.asList( layers.clone() ) );
				this.dynamicFieldsContainer = new DynamicFieldsContainer();
			}
			else {
				throw new IllegalArgumentException("Expected at least one layer");
			}
		}
		else {
			throw new NullPointerException();
		}
	}
	
	/**
	 * @post Verifica la validez del de los datos de capa
	 */
	public void checkVertexLayersData(VertexLayersData checkVertexLayersData) {
		if ( checkVertexLayersData != null ) {
			if ( checkVertexLayersData.getLayersData().size() == this.getLayers().size() ) {
				for ( int i = 0 ; i < this.getLayers().size() ; i++ ) {
					this.getLayers().get(i).checkLayerMapping(checkVertexLayersData.getLayersData().get(i));
				}
			}
			else {
				throw new IllegalArgumentException("Layers mapping size mismatch with mesh layers config");
			}
		}
		else {
			throw new NullPointerException();
		}
	}
	
	/**
	 * @post Devuelve las capas (Sólo lectura)
	 */
	public List<MeshLayer<?>> getLayers() {
		return this.layers;
	}
	
	/**
	 * @post Devuelve el contenedor de miembros dinámicos
	 */
	public DynamicFieldsContainer dynamicFieldsContainer() {
		return this.dynamicFieldsContainer;
	}

	/* (non-Javadoc)
	 * @see com.esferixis.gameengine.renderengine.backend.LoadableBackendObject#accept(com.esferixis.gameengine.renderengine.backend.LoadableBackendObject.Visitor)
	 */
	@Override
	public <R, E extends Throwable> R accept(Visitor<R, E> visitor) throws E {
		return visitor.visit(this);
	}
}
