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

import com.arielcarrizo.gameengine.renderengine.backend.opengl.GLException;
import com.arielcarrizo.gameengine.renderengine.backend.opengl.gl21.GL21;
import com.arielcarrizo.gameengine.renderengine.backend.opengl.gl21.Gl21RenderEngineBackendSystem;
import com.arielcarrizo.gameengine.renderengine.backend.opengl.gl21.meshLayers.LayersVertexDataBacker;
import com.esferixis.gameengine.renderengine.backend.meshLayers.VertexLayersData;
import com.esferixis.gameengine.renderengine.backend.misc.mesh.colored.ColoredMeshData;
import com.esferixis.gameengine.renderengine.backend.misc.mesh.colored.ColoredMeshVertex;
import com.esferixis.math.Vectorf;
import com.esferixis.misc.ElementProcessor;

public final class ColoredMeshBacker<V extends Vectorf> extends MeshBacker<V, ColoredMeshVertex<V>, ColoredMeshData<V>> {
	private final LayersVertexDataBacker layersVertexDataBacker;
	
	public ColoredMeshBacker(Gl21RenderEngineBackendSystem renderEngineBackend, ColoredMeshData<V> meshData) throws GLException {
		super(renderEngineBackend, meshData);
		this.layersVertexDataBacker = new LayersVertexDataBacker(renderEngineBackend, meshData.getMeshLayersConfig(), meshData, new ElementProcessor<ColoredMeshVertex<V>, VertexLayersData>() {

			@Override
			public VertexLayersData process(ColoredMeshVertex<V> element) {
				return element.getVertexLayersData();
			}
			
		});
	}

	/**
	 * @post Devuelve el "backer" de los datos de vértice de las capas
	 */
	public LayersVertexDataBacker getLayersVertexDataBacker() {
		return this.layersVertexDataBacker;
	}
	
	/**
	 * @post Libera los recursos ocupados por el "backer"
	 */
	@Override
	public void destroy() throws NullPointerException, IllegalStateException {
		this.checkDestroyed();
		this.layersVertexDataBacker.destroy();
		super.destroy();
	}
}
