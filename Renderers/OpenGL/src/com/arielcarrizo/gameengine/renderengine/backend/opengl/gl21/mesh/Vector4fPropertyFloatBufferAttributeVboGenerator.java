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
package com.arielcarrizo.gameengine.renderengine.backend.opengl.gl21.mesh;

import java.nio.FloatBuffer;

import com.esferixis.gameengine.renderengine.backend.misc.mesh.MeshTriangleVertex;
import com.esferixis.math.Vector4f;
import com.esferixis.math.Vectorf;
import com.esferixis.misc.ElementProcessor;

public final class Vector4fPropertyFloatBufferAttributeVboGenerator<P extends MeshTriangleVertex<Vector4f>> extends FloatBufferAttributeVboGenerator<Vector4f, P> {
	/**
	 * @pre La implementación de OpenGL y la clase de propiedad no pueden ser nulas
	 * @post Crea el generador de Vbo con la implementación de OpenGL y el generador
	 * 		 de valor de atributo especificado
	 * @param gl
	 * @param sizeperpoint
	 */
	public Vector4fPropertyFloatBufferAttributeVboGenerator(ElementProcessor<P, Vector4f> pointProcessor) {
		super(4, pointProcessor);
	}
}
