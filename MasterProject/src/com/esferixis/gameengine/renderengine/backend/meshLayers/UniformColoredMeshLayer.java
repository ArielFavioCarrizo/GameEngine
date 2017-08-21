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

import com.esferixis.gameengine.renderengine.misc.colorObject.ColorObject;

/**
 * Capa coloreada uniformemente
 * 
 * @author ariel
 *
 */
public final class UniformColoredMeshLayer extends MeshLayer<UniformColoredMeshLayerVertexData> {
	/**
	 * @pre El objeto de color de máscara no puede ser nulo
	 * @post Crea la capa con el objeto de color de máscara especificado
	 * @param layerDataClass
	 */
	public UniformColoredMeshLayer(ColorObject maskColorObject) {
		super(UniformColoredMeshLayerVertexData.class, maskColorObject);
		
		if ( maskColorObject == null ) {
			throw new NullPointerException();
		}
	}

	/* (non-Javadoc)
	 * @see com.esferixis.gameengine.renderengine.backend.meshLayers.MeshLayer#accept(com.esferixis.gameengine.renderengine.backend.meshLayers.MeshLayer.Visitor)
	 */
	@Override
	public <V, T extends Throwable> V accept(
			com.esferixis.gameengine.renderengine.backend.meshLayers.MeshLayer.Visitor<V, T> visitor) throws T {
		return visitor.visit(this);
	}
}
