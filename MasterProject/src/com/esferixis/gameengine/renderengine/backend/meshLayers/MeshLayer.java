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
 * @author ariel
 *
 */
public abstract class MeshLayer<L extends LayerVertexData> {
	private final Class<L> layerDataClass;
	
	public interface Visitor<R, T extends Throwable> {
		public R visit(SimpleTextureLayer<?> layer) throws T;
		public R visit(VertexColoredMeshLayer layer) throws T;
		public R visit(UniformColoredMeshLayer layer) throws T;
	}
	
	private final ColorObject maskColorObject;
	
	/**
	 * @pre La clase de mapeado de capa no puede ser nula
	 * @post Crea la capa de malla con la clase de mapeado de capa y
	 * 		 el objeto de color de máscara especificado.
	 * 		 Si éste último es nulo, entonces no lo tiene.
	 */
	MeshLayer(Class<L> layerDataClass, ColorObject maskColorObject) {
		if ( layerDataClass != null ) {
			this.layerDataClass = layerDataClass;
			this.maskColorObject = maskColorObject;
		}
		else {
			throw new NullPointerException();
		}
	}
	
	/**
	 * @post Verifica que el mapeado de capa sea válido
	 */
	@SuppressWarnings("unchecked")
	public final void checkLayerMapping(LayerVertexData layerMapping) {
		if ( layerMapping != null ) {
			if ( layerDataClass.isInstance(layerMapping) ) {
				this.checkLayerMapping_internal( (L) layerMapping);
			}
			else {
				throw new ClassCastException("Invalid layer mapping class");
			}
		}
		else {
			throw new NullPointerException();
		}
	}
	
	/**
	 * @post Verifica que el mapeado de capa sea válido (Específico)
	 */
	protected void checkLayerMapping_internal(L layerMapping) {
		
	}
	
	/**
	 * @post Devuelve el objeto de máscara de color,
	 * 		 si no lleva devuelve null
	 */
	public final ColorObject getMaskColorObject() {
		return this.maskColorObject;
	}
	
	/**
	 * @post Visita con el visitor especificado
	 */
	public abstract <V, T extends Throwable> V accept(Visitor<V, T> visitor) throws T;
}
