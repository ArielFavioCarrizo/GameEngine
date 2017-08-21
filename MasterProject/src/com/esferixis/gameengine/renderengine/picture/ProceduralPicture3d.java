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
import com.esferixis.gameengine.renderengine.picture.RasterPicture.PixelFormat;
import com.esferixis.math.Vector1f;
import com.esferixis.math.Vector2f;
import com.esferixis.math.Vector3f;
import com.esferixis.math.Vector4f;
import com.esferixis.math.Vectorf;
import com.esferixis.misc.loader.AbstractDataLoader;
import com.esferixis.misc.loader.SerializableLengthedDataLoader;
import com.esferixis.misc.nio.BufferUtils;

/**
 * @author ariel
 *
 */
public abstract class ProceduralPicture3d<V extends Vectorf> {
	/**
	 * @post Crea la imagen procedural
	 */
	public ProceduralPicture3d() {
		super();
	}
	
	/**
	 * @post Devuelve el color de la posición especificada
	 * 
	 * 		 Donde el origen de coordenadas está en la esquina izquierda inferior
	 */
	public abstract V getValue(Vector3f position);
	
	/**
	 * @post Crea el cargador raster con el ancho y el alto especificados
	 */
	public SerializableLengthedDataLoader<RasterPicture3d> createRasterLoader(final int width, final int height, final int depth) {
		if ( ( width > 0 ) && ( height > 0 ) ) {
			final PixelFormat pixelFormat = ProceduralPicture3d.this.getValue(Vector3f.ZERO).accept(new Vectorf.Visitor<PixelFormat, RuntimeException>() {

				@Override
				public PixelFormat visit(Vector1f vector1f) {
					return PixelFormat.SCALAR;
				}

				@Override
				public PixelFormat visit(Vector2f vector2f) {
					return PixelFormat.VERTEX2;
				}

				@Override
				public PixelFormat visit(Vector3f vector3f) {
					return PixelFormat.VERTEX3;
				}

				@Override
				public PixelFormat visit(Vector4f vector4f) {
					return PixelFormat.VERTEX4;
				}
				
			});
			
			class LocalDataLoader extends AbstractDataLoader<RasterPicture3d> implements SerializableLengthedDataLoader<RasterPicture3d> {

				/**
				 * 
				 */
				private static final long serialVersionUID = 4236764049765317889L;

				@Override
				public RasterPicture3d get_internal() {
					ByteBuffer byteBuffer = BufferUtils.createByteBuffer(this.getDataLength());
					FloatBuffer floatBuffer = byteBuffer.asFloatBuffer();
					for ( int z = 0 ; z < depth ; z++ ) {
						for ( int y = 0 ; y < height ; y++ ) {
							for ( int x = 0 ; x < width ; x++ ) {
								final Vector3f position = new Vector3f( (float) x / (float) (width-1), (float) y / (float) (height-1), (float) z / (float) (depth-1));
								final V value = ProceduralPicture3d.this.getValue(position);
								floatBuffer.put(value.store());
							}
						}
					}
					
					floatBuffer.flip();
					
					return new RasterPicture3d(pixelFormat, ComponentFormat.FLOAT, width, height, depth, byteBuffer);
				}

				/* (non-Javadoc)
				 * @see com.esferixis.misc.loader.LengthedDataLoader#getDataLength()
				 */
				@Override
				public int getDataLength() {
					return width * height * depth * pixelFormat.getPixelSize() * 4;
				}
				
			};
			
			return new LocalDataLoader();
		}
		else {
			throw new IllegalArgumentException("Invalid size");
		}
	}
}
