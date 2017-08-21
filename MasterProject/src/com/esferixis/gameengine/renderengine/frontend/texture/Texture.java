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
package com.esferixis.gameengine.renderengine.frontend.texture;

import java.io.Serializable;

import com.esferixis.gameengine.renderengine.frontend.RenderEngineFrontendLoadableObject;
import com.esferixis.gameengine.renderengine.frontend.implementation.CoreFactory;
import com.esferixis.gameengine.renderengine.frontend.implementation.texture.TextureCore;
import com.esferixis.gameengine.renderengine.picture.ProceduralPicture2d;
import com.esferixis.gameengine.renderengine.picture.ProceduralPicture3d;
import com.esferixis.gameengine.renderengine.picture.RasterPicture;
import com.esferixis.gameengine.renderengine.picture.RasterPicture2d;
import com.esferixis.gameengine.renderengine.picture.RasterPicture3d;
import com.esferixis.math.Vector2f;
import com.esferixis.math.Vector3f;
import com.esferixis.math.Vector4f;
import com.esferixis.math.Vectorf;
import com.esferixis.misc.accesor.AccesorHolder;
import com.esferixis.misc.accesor.AccesorWhiteList;
import com.esferixis.misc.loader.AbstractDataLoader;
import com.esferixis.misc.loader.DataLoader;
import com.esferixis.misc.loader.SerializableLengthedDataLoader;

/**
 * Textura, del frontend
 * 
 * @author ariel
 *
 */
public final class Texture<P extends RasterPicture<? extends Vectorf>> extends RenderEngineFrontendLoadableObject implements Serializable {
	/**
	 * 
	 */
	private static final long serialVersionUID = 6762543770974515738L;
	private final SerializableLengthedDataLoader<P> loader;
	
	private static final CoreFactory<Texture<? extends RasterPicture<?>>, TextureCore<?>> coreFactory = new CoreFactory<Texture<? extends RasterPicture<?>>, TextureCore<?>>() {

		@Override
		protected TextureCore<?> createCore(Texture<?> texture) {
			return PackageAccesors.textureCoreAccesor.get().create(texture);
		}
		
	};
	
	private transient TextureCore<P> core;
	
	// Clase de acceso de implementación
	public static final class Accesor {
		private Accesor() {};
		
		@AccesorWhiteList
		private static final Class<?>[] accesorHolders = new Class[] {
			com.esferixis.gameengine.renderengine.frontend.meshLayers.PackageAccesors.class,
			com.esferixis.gameengine.renderengine.frontend.PackageAccesors.class
		};
		
		/**
		 * @post Devuelve el núcleo de la textura especificada
		 */
		public <P extends RasterPicture<? extends Vectorf>> TextureCore<P> getCore(Texture<P> texture) {
			return texture.getCore();
		}
	}
	
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
	 * @post Devuelve el núcleo
	 */
	@Override
	protected TextureCore<P> getCore() {
		if ( this.core == null ) {
			this.core = (TextureCore<P>) this.coreFactory.getCore(this);
		}
		
		return this.core;
	}
	
	/**
	 * @post Crea la textura con la clase de imagen y el cargador especificado
	 */
	public Texture(SerializableLengthedDataLoader<P> loader) {
		if ( loader != null ) {
			this.core = null;
			this.loader = loader;
		}
		else {
			throw new NullPointerException();
		}
	}
	
	/**
	 * @post Devuelve el cargador
	 */
	public SerializableLengthedDataLoader<P> getLoader() {
		return this.loader;
	}

	/* (non-Javadoc)
	 * @see com.esferixis.gameengine.renderengine.frontend.RenderEngineFrontendObject#hashCode()
	 */
	@Override
	public int hashCode() {
		return this.loader.hashCode();
	}

	/* (non-Javadoc)
	 * @see com.esferixis.gameengine.renderengine.frontend.RenderEngineFrontendObject#equals(java.lang.Object)
	 */
	@Override
	public boolean equals(Object other) {
		if ( ( other != null ) && ( other instanceof Texture ) ) {
			return ( ( Texture<?> ) other ).loader.equals(this.loader);
		}
		else {
			return false;
		}
	}

	/* (non-Javadoc)
	 * @see com.esferixis.gameengine.renderengine.frontend.RenderEngineFrontendObject#accept(com.esferixis.gameengine.renderengine.frontend.RenderEngineFrontendObject.Visitor)
	 */
	@Override
	public <R, E extends Throwable> R accept(Visitor<R, E> visitor) throws E {
		return visitor.visit(this);
	}
}
