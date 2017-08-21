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
package com.arielcarrizo.gameengine.renderengine.backend.opengl.gl21.plane;

import com.arielcarrizo.gameengine.renderengine.backend.opengl.GLException;
import com.arielcarrizo.gameengine.renderengine.backend.opengl.gl21.Gl21RenderEngineBackendObject;
import com.arielcarrizo.gameengine.renderengine.backend.opengl.gl21.Gl21RenderEngineBackendSystem;
import com.arielcarrizo.gameengine.renderengine.backend.opengl.gl21.plane.shape.ShapeRenderingSubsystem;
import com.esferixis.gameengine.renderengine.backend.plane.PlaneObjectComponentRenderer;

public final class PlaneRenderingSubsystem extends Gl21RenderEngineBackendObject {
	private final ShapeRenderingSubsystem shapeRenderingSubsystem;
	
	/**
	 * @post Crea el susbsistema de renderizado en el plano con el backend del motor de rendering
	 * 		 especificado
	 */
	public PlaneRenderingSubsystem(Gl21RenderEngineBackendSystem renderEngineBackendSystem) throws GLException {
		super(renderEngineBackendSystem);
		try {
			this.shapeRenderingSubsystem = new ShapeRenderingSubsystem(this.renderEngineBackend);
		} catch (RuntimeException | GLException e) {
			this.destroy();
			throw e;
		}
	}
	
	/**
	 * @post Devuelve el renderizador de planos
	 */
	public GlPlaneObjectRenderer getPlaneObjectRenderer() {
		return new GlPlaneObjectRenderer(this);
	}
	
	/**
	 * @post Devuelve el subsistema de renderizado de figuras
	 */
	ShapeRenderingSubsystem getShapeRenderingSubsystem() {
		return this.shapeRenderingSubsystem;
	}
	
	/**
	 * @post Destruye el subsistema
	 */
	public void destroy() {
		if ( this.shapeRenderingSubsystem != null ) this.shapeRenderingSubsystem.destroy();
	}
}
