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
package com.esferixis.gameengine.physics.plane.statics;

import com.esferixis.geometry.plane.finite.FiniteProportionalHolomorphicShape;
import com.esferixis.math.Vector2f;
import com.esferixis.misc.ElementCallback;
import com.esferixis.misc.observer.ObserverManager;

/**
 * @author ariel
 *
 */
public final class TransformedMapper2d extends PlaneMapper {
	/**
	 * 
	 */
	private static final long serialVersionUID = -8751285970362429959L;
	
	private final PlaneMapper originalMapper;
	private PlaneMapper transformerMapper;
	
	ObserverManager<TransformedMapper2d, TransformedMapper2dObserver> observerManager = new ObserverManager<TransformedMapper2d, TransformedMapper2dObserver>(this, TransformedMapper2dObserver.class);

	/**
	 * @pre El mapeador original no puede ser nulo
	 * @post Crea la mapeador de composición con el
	 * 		 mapeador original y el mapeador de transformación especificados
	 */
	public TransformedMapper2d(PlaneMapper originalMapper, PlaneMapper transformerMapper) {
		if ( originalMapper != null ) {
			this.originalMapper = originalMapper;
			this.setTransformerMapper(transformerMapper);
		}
		else {
			throw new NullPointerException();
		}
	}
	
	/**
	 * @pre El mapeador original no puede ser nulo
	 * @post Crea la mapeador de composición con el
	 * 		 mapeador original especificado
	 */
	public TransformedMapper2d(PlaneMapper originalMapper) {
		this(originalMapper, IdentityMapper2d.INSTANCE);
	}
	
	/**
	 * @post Devuelve el mapeador original
	 */
	public PlaneMapper getOriginalMapper() {
		return this.originalMapper;
	}
	
	/**
	 * @pre El mapeador transformador no puede ser nulo
	 * @post Especifica el mapeador transformador
	 */
	public void setTransformerMapper(final PlaneMapper transformerMapper) {
		if ( transformerMapper != null ) {
			this.observerManager.notifyObservers(new ElementCallback<TransformedMapper2dObserver>(){
	
				@Override
				public void run(TransformedMapper2dObserver element) {
					element.registerTransformerMapperChange(transformerMapper);
				}
				
			});
			this.transformerMapper = transformerMapper;
		}
		else {
			throw new NullPointerException();
		}
	}
	
	/**
	 * @post Devuelve el mapeador transformador
	 */
	public PlaneMapper getTransformerMapper() {
		return this.transformerMapper;
	}

	/* (non-Javadoc)
	 * @see com.esferixis.gameengine.renderengine.mesh.mapper.RenderingMapper#transform(com.esferixis.math.Vector3f)
	 */
	@Override
	public Vector2f transform(Vector2f original) {
		return this.transformerMapper.transform( this.originalMapper.transform(original) );
	}

	/* (non-Javadoc)
	 * @see com.arielcarrizo.gameengine.physics.plane.statics.PlaneMapper#accept(com.arielcarrizo.gameengine.physics.plane.statics.PlaneMapper.Visitor)
	 */
	@Override
	public <V, T extends Throwable> V accept(Visitor<V, T> visitor) throws T {
		return visitor.visit(this);
	}
}
