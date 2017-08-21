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
package com.esferixis.gameengine.renderengine.frontend.renderingFrame;

import com.esferixis.gameengine.renderengine.frontend.plane.staticstage.PlaneRendererEmmiter;
import com.esferixis.math.Vector4f;

/**
 * @author ariel
 *
 */
final class ProtectedRenderingFrameRenderer extends RenderingFrameRenderer {
	private final RenderingFrameRenderer targetRenderer;
	private boolean onContext;
	
	/**
	 * @pre El renderizador no puede ser nulo
	 * @post Crea el renderizador protegido con el renderizador especificado
	 */
	public ProtectedRenderingFrameRenderer(RenderingFrameRenderer targetRenderer) {
		if ( targetRenderer != null ) {
			this.targetRenderer = targetRenderer;
			this.onContext = true;
		}
		else {
			throw new NullPointerException();
		}
	}
	
	private void checkContext() {
		if ( !this.onContext ) {
			throw new IllegalStateException("Invalid context");
		}
	}
	
	/* (non-Javadoc)
	 * @see com.esferixis.gameengine.renderengine.frontend.staticstage.renderingFrame.RenderingFrameRenderer#clear(com.esferixis.math.Vector4f)
	 */
	@Override
	public void clear(Vector4f color) {
		this.checkContext();
		this.targetRenderer.clear(color);
	}

	/* (non-Javadoc)
	 * @see com.esferixis.gameengine.renderengine.frontend.staticstage.renderingFrame.RenderingFrameRenderer#render(com.esferixis.gameengine.renderengine.frontend.staticstage.plane.PlaneRendererEmmiter)
	 */
	@Override
	public void render(PlaneRendererEmmiter planeRendererEmmiter) {
		this.checkContext();
		this.targetRenderer.render(planeRendererEmmiter);
	}

	/**
	 * @post Sale del contexto
	 */
	void exitContext() {
		this.onContext = false;
	}
	
}
