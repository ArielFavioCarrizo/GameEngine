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

package com.esferixis.gameengine.renderengine.material;

import com.esferixis.math.ExtraMath;
import com.esferixis.math.Vector3f;

/**
 * Material de superficie
 * Nota: No hay componente ambiental porque hay luces ambientales para tal fin
 * 
 * @author Ariel Favio Carrizo
 */
public final class Material {
	private final Vector3f baseColor;
	private final float metalness, roughness;
	
	/**
	 * @pre El color base no puede ser nulo y su intensidad no puede
	 * 		ser mayor a la raíz cuadrada de 3, y la metalicidad y la
	 * 		rugosidad tienen que estar comprendidas entre 0 y 1
	 * @post Crea el material con el color base, con la metalicidad y la rugosidad
	 * 		 especificadas
	 */
	public Material(Vector3f baseColor, float metalness, float roughness) {
		if ( baseColor != null ) {
			if ( ( baseColor.lengthSquared() <= 3 ) && ExtraMath.containedByInterval(metalness, 0.0f, 1.0f) && ExtraMath.containedByInterval(roughness, 0.0f, 1.0f) ) {
				this.baseColor = baseColor;
				this.metalness = metalness;
				this.roughness = roughness;
			}
			else {
				throw new IllegalArgumentException("Invalid parameters");
			}
		}
		else {
			throw new NullPointerException();
		}
	}
	
	/**
	 * @post Devuelve el color base
	 */
	public Vector3f getBaseColor() {
		return this.baseColor;
	}
	
	/**
	 * @post Devuelve la metalicidad
	 */
	public float getMetalness() {
		return this.metalness;
	}
	
	/**
	 * @post Devuelve la rugosidad
	 */
	public float getRoughness() {
		return this.roughness;
	}
}
