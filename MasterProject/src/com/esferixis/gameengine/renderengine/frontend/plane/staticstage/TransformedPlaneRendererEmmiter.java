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
package com.esferixis.gameengine.renderengine.frontend.plane.staticstage;

import com.esferixis.gameengine.physics.plane.statics.PlaneMapper;
import com.esferixis.gameengine.physics.plane.statics.TransformedMapper2d;
import com.esferixis.gameengine.renderengine.frontend.plane.staticstage.mesh.StaticMeshObject2d;
import com.esferixis.gameengine.renderengine.frontend.plane.staticstage.shape.StaticShapeObject;

/**
 * @author ariel
 *
 */
public final class TransformedPlaneRendererEmmiter extends PlaneRendererEmmiter {
	private final PlaneRendererEmmiter planeRendererEmmiter;
	private PlaneMapper transformerMapper;
	
	/**
	 * @pre Ninguno de los dos puede ser nulo
	 * @post Crea un emisor transformado con el emisor y el mapeador de transformación especificados
	 */
	public TransformedPlaneRendererEmmiter(PlaneRendererEmmiter planeRendererEmmiter) {
		if ( planeRendererEmmiter != null ) {
			this.planeRendererEmmiter = planeRendererEmmiter;
		}
		else {
			throw new NullPointerException();
		}
	}
	
	/**
	 * @pre El mapeador no puede ser nulo
	 * @post Especifica el mapeador de transformación
	 */
	public void setTransformerMapper(final PlaneMapper transformerMapper) {
		if ( transformerMapper != null ) {
			this.transformerMapper = transformerMapper;
		}
		else {
			throw new NullPointerException();
		}
	}
	
	/**
	 * @post Devuelve el mapeador de transformación
	 */
	public PlaneMapper getTransformerMapper() {
		return this.transformerMapper;
	}

	/* (non-Javadoc)
	 * @see com.esferixis.gameengine.renderengine.frontend.plane.PlaneRendererEmmiter#render_internal(com.esferixis.gameengine.renderengine.frontend.plane.PlaneObjectRenderer)
	 */
	@Override
	public void render_internal(final StaticPlaneObjectRenderer objectRenderer) {
		this.planeRendererEmmiter.render(new StaticPlaneObjectRenderer() {

			@Override
			public void render(StaticPlaneObject planeObject) throws NullPointerException {
				final TransformedMapper2d transformedMapper = new TransformedMapper2d(planeObject.getMapper(), TransformedPlaneRendererEmmiter.this.transformerMapper);
				
				planeObject.accept(new StaticPlaneObject.Visitor<Void, RuntimeException>() {

					@Override
					public Void visit(StaticMeshObject2d meshObject2d) {
						objectRenderer.render(new StaticMeshObject2d(meshObject2d.getMesh(), meshObject2d.getMeshLayersConfigProfile(), transformedMapper));
						
						return null;
					}

					@Override
					public Void visit(StaticShapeObject shapeObject) {
						objectRenderer.render(new StaticShapeObject(shapeObject.getShape(), shapeObject.getColorDistribution(), transformedMapper));
						
						return null;
					}
				});
			}
		});
	}

}
