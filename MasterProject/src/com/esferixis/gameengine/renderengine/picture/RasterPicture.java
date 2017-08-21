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

import com.esferixis.gameengine.renderengine.picture.RasterPicture.ComponentFormat;
import com.esferixis.math.Vectorf;

/**
 * @author ariel
 *
 */
public abstract class RasterPicture<V extends Vectorf> {
	public enum PixelFormat {
		SCALAR (1),
		VERTEX2 (2),
		VERTEX3 (3),
		VERTEX4 (4);
		
		private final int pixelSize;
		
		/**
		 * @post Crea el tipo
		 */
		PixelFormat(int pixelSize) {
			this.pixelSize = pixelSize;
		}
		
		/**
		 * @post Devuelve el tamaño de pixel en componentes
		 */
		public int getPixelSize() {
			return this.pixelSize;
		}
	}
	
	public enum ComponentFormat {
		BYTE(1),
		FLOAT(4);
		
		private final int size;
		
		/**
		 * @post Devuelve el tamaño del componente en bytes
		 */
		private ComponentFormat(int size) {
			this.size = size;
		}
		
		/**
		 * @post Devuelve el tamaño en bytes
		 */
		public int getSize() {
			return this.size;
		}
	}
	
	public interface Visitor<V> {
		public V visit(RasterPicture2d picture);
		public V visit(RasterPicture3d picture);
	}
	
	private final Class<V> vectorClass;
	
	private final PixelFormat pixelFormat;
	private final ComponentFormat componentFormat;
	private final ByteBuffer data;
	
	/**
	 * @pre El formato de pixel, el de componente y el buffer de datos no pueden ser nulos
	 * 		El buffer de datos tiene que ser directo
	 * @post Crea la imagen con el formato de pixel, el de componente y los datos especificados
	 */
	RasterPicture(Class<V> vectorClass, PixelFormat pixelFormat, ComponentFormat componentFormat, ByteBuffer data) {
		if ( ( vectorClass != null ) && ( pixelFormat != null ) && ( componentFormat != null ) && ( data != null ) ) {
			if ( data.isDirect() ) {
				this.vectorClass = vectorClass;
				this.pixelFormat = pixelFormat;
				this.componentFormat = componentFormat;
				this.data = data.asReadOnlyBuffer();
			}
			else {
				throw new IllegalArgumentException("Expected direct data buffer");
			}
		}
		else {
			throw new NullPointerException();
		}
	}
	
	/**
	 * @post Devuelve el formato de pixel
	 */
	public final PixelFormat getPixelFormat() {
		return this.pixelFormat;
	}
	
	/**
	 * @post Devuelve el formato de componente
	 */
	public final ComponentFormat getComponentFormat() {
		return this.componentFormat;
	}
	
	/**
	 * @post Devuelve el buffer de datos
	 */
	public final ByteBuffer getData() {
		return this.data;
	}
	
	/**
	 * @post Convierte la imagen en el formato de componente especificado
	 * 		 y devuelve la imagen convertida
	 */
	public abstract RasterPicture<V> convert(ComponentFormat newComponentFormat);
	
	/**
	 * @post Devuelve la clase de vector
	 */
	public final Class<V> getVectorClass() {
		return this.vectorClass;
	}
	
	/**
	 * @pre El visitor no puede ser nulo
	 * @post Procesa la imagen con el visitor especificado
	 */
	public abstract <R> R accept(Visitor<R> visitor);
}
