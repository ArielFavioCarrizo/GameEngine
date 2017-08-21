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
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import com.esferixis.gameengine.renderengine.frontend.meshLayers.LayerVertexData;

/**
 * @author ariel
 *
 */
public final class VertexLayersData implements Serializable {
	/**
	 * 
	 */
	private static final long serialVersionUID = -8653235570700671264L;
	
	private final List<LayerVertexData> layersData;
	
	/**
	 * @pre Los datos de vértice de capa no pueden ser nulos
	 * @post Crea la información de capas de vértice con la información de vértice de cada capa
	 */
	public VertexLayersData(LayerVertexData... layerMappings) {
		if  ( layerMappings != null ) {
			this.layersData = Collections.unmodifiableList( Arrays.asList(layerMappings.clone()) );
		}
		else {
			throw new NullPointerException();
		}
	}
	
	/**
	 * @pre Los datos de vértice de capa no pueden ser nulos
	 * @post Crea la información de capas de vértice con la información de vértice de cada capa
	 */
	public VertexLayersData(List<LayerVertexData> layerMappings) {
		this(layerMappings.toArray(new LayerVertexData[0]));
	}
	
	/**
	 * @post Devuelve los datos de vértice de capa (Sólo lectura)
	 */
	public List<LayerVertexData> getLayersData() {
		return this.layersData;
	}
}
