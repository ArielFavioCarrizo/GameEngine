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

package com.esferixis.gameengine.renderengine.backend.texture;

import java.io.IOException;

import com.esferixis.gameengine.renderengine.backend.LoadableBackendObject;
import com.esferixis.gameengine.renderengine.picture.ProceduralPicture2d;
import com.esferixis.gameengine.renderengine.picture.ProceduralPicture3d;
import com.esferixis.gameengine.renderengine.picture.RasterPicture;
import com.esferixis.gameengine.renderengine.picture.RasterPicture2d;
import com.esferixis.gameengine.renderengine.picture.RasterPicture3d;
import com.esferixis.math.Vector2f;
import com.esferixis.math.Vector3f;
import com.esferixis.math.Vector4f;
import com.esferixis.math.Vectorf;
import com.esferixis.misc.dynamicFields.DynamicFieldsContainer;
import com.esferixis.misc.loader.AbstractDataLoader;
import com.esferixis.misc.loader.DataLoader;
import com.esferixis.misc.loader.DataLoadingErrorException;

/**
 * Textura
 * 
 * @author Ariel Favio Carrizo
 *
 */
public final class Texture<P extends RasterPicture<? extends Vectorf>> extends LoadableBackendObject {
	private final DataLoader<P> pictureLoader;
	private final DynamicFieldsContainer dynamicFieldsContainer;
	
	public static final Texture<RasterPicture2d> BLANKTEXTURE2D = new Texture<RasterPicture2d>(
			(new ProceduralPicture2d<Vector4f>() {

				@Override
				public Vector4f getValue(Vector2f position) {
					return Vector4f.ZERO;
				}
				
			}).createRasterLoader(1, 1)
	);

	public static final Texture<RasterPicture3d> BLANKTEXTURE3D = new Texture<RasterPicture3d>(
			(new ProceduralPicture3d<Vector4f>() {

				@Override
				public Vector4f getValue(Vector3f position) {
					return Vector4f.ZERO;
				}
				
			}).createRasterLoader(1, 1, 1)
	);
	
	/**
	 * @pre El cargador de imágenes no puede ser nulo
	 * @post Crea la textura con el cargador de imágenes especificado
	 */
	public Texture(DataLoader<P> pictureLoader) {
		if ( pictureLoader != null ) {
			this.pictureLoader = pictureLoader;
			this.dynamicFieldsContainer = new DynamicFieldsContainer();
		}
		else {
			throw new NullPointerException();
		}
	}
	
	/**
	 * @post Devuelve la imagen
	 */
	public P getPicture() throws DataLoadingErrorException {
		return this.pictureLoader.get();
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
