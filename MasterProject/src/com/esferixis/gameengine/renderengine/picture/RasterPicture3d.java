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
package com.esferixis.gameengine.renderengine.picture;

import java.nio.ByteBuffer;
import java.nio.FloatBuffer;

import com.esferixis.gameengine.renderengine.picture.RasterPicture.ComponentFormat;
import com.esferixis.math.Vector3f;
import com.esferixis.misc.nio.BufferUtils;

/**
 * @author ariel
 *
 */
public final class RasterPicture3d extends RasterPicture<Vector3f> {
	private final int width, height, depth;
	
	/**
	 * @pre El formato de pixel y el de datos, y los datos no pueden ser nulos.
	 * 		El ancho, el alto y la profundidad deben ser positivos.
	 * 		Y el buffer de datos tiene que ser directo con el tamaño
	 * 		correcto
	 * @post Crea la imagen con el formato, el ancho, el alto, la profundidad y los datos especificados
	 * @param pixelFormat
	 * @param width
	 * @param height
	 * @param data
	 */
	public RasterPicture3d(PixelFormat pixelFormat, ComponentFormat componentFormat, int width, int height, int depth, ByteBuffer data) {
		super(Vector3f.class, pixelFormat, componentFormat, data);
		final int expectedSize = width * height * depth * pixelFormat.getPixelSize() * componentFormat.getSize();
		if ( data.capacity() == expectedSize ) {
			this.width = width;
			this.height = height;
			this.depth = depth;
		}
		else {
			throw new IllegalArgumentException("Invalid buffer size");
		}
	}
	
	/**
	 * @post Devuelve el ancho
	 */
	public int getWidth() {
		return this.width;
	}
	
	/**
	 * @post Devuelve el alto
	 */
	public int getHeight() {
		return this.height;
	}
	
	/**
	 * @post Devuelve la profundidad
	 */
	public int getDepth() {
		return this.depth;
	}
	
	/**
	 * @post Convierte la imagen en el formato de componente especificado
	 * 		 y devuelve la imagen convertida
	 */
	@Override
	public RasterPicture3d convert(ComponentFormat newComponentFormat) {
		if ( !newComponentFormat.equals(this.getComponentFormat()) ) {
			final ByteBuffer newByteBuffer;
			
			if ( newComponentFormat.equals(ComponentFormat.FLOAT) ) {
				ByteBuffer originalData = this.getData();
				newByteBuffer = BufferUtils.createByteBuffer(originalData.capacity()*ComponentFormat.FLOAT.getSize());
				FloatBuffer newFloatBuffer = newByteBuffer.asFloatBuffer();
				
				for ( int i = 0 ; i< originalData.capacity() ; i++ ) {
					newFloatBuffer.put( (float) originalData.get(i) / 255.0f );
				}
			}
			else {
				FloatBuffer originalData = this.getData().asFloatBuffer();
				
				newByteBuffer = BufferUtils.createByteBuffer(originalData.capacity()/ComponentFormat.FLOAT.getSize());
				
				for ( int i = 0 ; i< originalData.capacity() ; i++ ) {
					newByteBuffer.put( (byte) ( originalData.get(i) * 255.0f ) );
				}
			}
			
			return new RasterPicture3d(this.getPixelFormat(), newComponentFormat, this.getWidth(), this.getHeight(), this.getDepth(), newByteBuffer);
		}
		else {
			return this;
		}
	}

	/* (non-Javadoc)
	 * @see com.esferixis.gameengine.renderengine.texture.Picture#visit(com.esferixis.gameengine.renderengine.texture.Picture.Visitor)
	 */
	@Override
	public <R> R accept(Visitor<R> visitor) {
		return visitor.visit(this);
	}
}
