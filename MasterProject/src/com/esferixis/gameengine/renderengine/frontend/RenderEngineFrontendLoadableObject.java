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
package com.esferixis.gameengine.renderengine.frontend;

import com.esferixis.gameengine.renderengine.frontend.implementation.core.Core;
import com.esferixis.gameengine.renderengine.frontend.meshLayers.MeshLayersConfig;
import com.esferixis.gameengine.renderengine.frontend.misc.mesh.Mesh;
import com.esferixis.gameengine.renderengine.frontend.texture.Texture;
import com.esferixis.misc.accesor.AccesorHolder;
import com.esferixis.misc.accesor.AccesorWhiteList;
import com.esferixis.misc.dynamicFields.DynamicFieldsContainerObject;

/**
 * @author ariel
 *
 */
public abstract class RenderEngineFrontendLoadableObject extends DynamicFieldsContainerObject {
	public interface Visitor<R, E extends Throwable> {
		public R visit(MeshLayersConfig meshLayersConfig) throws E;
		public R visit(Texture<?> texture) throws E;
		public R visit(Mesh<?, ?> mesh) throws E;
	}
	
	public static final class Accesor {
		private Accesor() {}
		
		@AccesorWhiteList
		private static final Class<?>[] packageAccesors = new Class[] {
			com.esferixis.gameengine.renderengine.frontend.implementation.core.PackageAccesors.class
		};
		
		/**
		 * @post Devuelve el núcleo del objeto especificado
		 */
		public Core<?, ?> getCore(RenderEngineFrontendLoadableObject object) {
			return object.getCore();
		}
	}
	
	/**
	 * @post Crea el objeto especificado
	 */
	public RenderEngineFrontendLoadableObject() {
		
	}
	
	/**
	 * @post Devuelve el hash
	 */
	@Override
	public abstract int hashCode();
	
	/**
	 * @post Devuelve si es igual al objeto especificado
	 */
	@Override
	public abstract boolean equals(Object other);
	
	/**
	 * @post Devuelve el núcleo
	 */
	protected abstract Core<?, ?> getCore();
	
	/**
	 * @post Lo visita con el visitor especificado
	 */
	public abstract <R, E extends Throwable> R accept(Visitor<R, E> visitor) throws E;
}
