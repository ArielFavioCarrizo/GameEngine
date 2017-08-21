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

import com.esferixis.gameengine.renderengine.picture.RasterPicture;

/**
 * Capa de mapping de textura sencilla
 * @author ariel
 *
 */
public final class SimpleTextureLayer<P extends RasterPicture<?>> extends TextureLayer<SimpleTextureLayerVertexData<?>> {
	/**
	 * 
	 */
	private static final long serialVersionUID = 2683401630660243110L;
	
	private final TextureObjectLayerDataField<?, ?> textureObjectField;
	
	/**
	 * @pre La textura no puede ser nula
	 * @post Crea la capa con el campo de objeto de textura y el campo de objeto de color de máscara
	 * 		 especificado, si es nulo, no tiene.
	 */
	public SimpleTextureLayer(TextureObjectLayerDataField<?, ?> textureObjectField, ColorLayerDataField maskColorField) {
		super( (Class<SimpleTextureLayerVertexData<?>>) (Class<?>) SimpleTextureLayerVertexData.class, maskColorField);
 		if ( textureObjectField != null ) {
 			this.textureObjectField = textureObjectField;
 			this.addDataField(this.textureObjectField);
 		}
 		else {
 			throw new NullPointerException();
 		}
	}
	
	/**
	 * @pre La textura no puede ser nula
	 * @post Crea la capa con el campo de objeto de textura especificado
	 */
	public SimpleTextureLayer(TextureObjectLayerDataField<?, ?> textureObjectField) {
		this(textureObjectField, null);
	}
	
	/**
	 * @post Devuelve el miembro de objeto de textura
	 */
	public TextureObjectLayerDataField<?, ?> getTextureObjectField() {
		return this.textureObjectField;
	}
	
	/**
	 * @post Devuelve el hash
	 */
	@Override
	public int hashCode() {
		return this.textureObjectField.hashCode() * 31 + super.hashCode();
	}
	
	/**
	 * @post Devuelve si es igual al objeto especificado
	 */
	@Override
	public boolean fuzzyEquals(MeshLayer<?> other) {
		if ( ( other != null ) && ( other instanceof SimpleTextureLayer) ) {
			final SimpleTextureLayer<?> otherLayer = (SimpleTextureLayer<?>) other;
			
			return super.fuzzyEquals(otherLayer) && ( otherLayer.getTextureObjectField().fuzzyEquals(this.getTextureObjectField()) );
		}
		else {
			return false;
		}
	}

	/* (non-Javadoc)
	 * @see com.esferixis.gameengine.renderengine.frontend.meshLayers.MeshLayer#accept(com.esferixis.gameengine.renderengine.frontend.meshLayers.MeshLayer.Visitor)
	 */
	@Override
	public <V, T extends Throwable> V accept(
			com.esferixis.gameengine.renderengine.frontend.meshLayers.MeshLayer.Visitor<V, T> visitor) throws T {
		return visitor.visit(this);
	}
}
