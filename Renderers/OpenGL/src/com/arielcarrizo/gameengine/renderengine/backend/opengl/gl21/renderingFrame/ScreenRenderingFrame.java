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
package com.arielcarrizo.gameengine.renderengine.backend.opengl.gl21.renderingFrame;


import com.arielcarrizo.gameengine.renderengine.backend.opengl.gl21.Gl21RenderEngineBackendSystem;

/**
 * Implementación particular del rectángulo de renderización de pantalla
 */
public final class ScreenRenderingFrame extends GlRenderingFrame {
	/**
	 * @pre El engine de rendering no puede ser nulo
	 * @post Crea el cuadro de renderización de pantalla con el engine de rendering
	 * 		 especificado
	 */
	public ScreenRenderingFrame(Gl21RenderEngineBackendSystem renderEngine) {
		super(renderEngine);
	}

	/* (non-Javadoc)
	 * @see com.esferixis.gameengine.renderengine.backend.opengl.gl21.renderingFrame.GlRenderingFrame#select()
	 */
	@Override
	protected void select() {
		// TODO Auto-generated method stub
		
	}

	/* (non-Javadoc)
	 * @see com.esferixis.gameengine.renderengine.backend.renderingFrame.RenderingFrame#getWidth()
	 */
	@Override
	public int getWidth() {
		return this.renderEngineSystem.getDisplayManager().getScreenConfig().getWidth();
	}

	/* (non-Javadoc)
	 * @see com.esferixis.gameengine.renderengine.backend.renderingFrame.RenderingFrame#getHeight()
	 */
	@Override
	public int getHeight() {
		return this.renderEngineSystem.getDisplayManager().getScreenConfig().getHeight();
	}
}
