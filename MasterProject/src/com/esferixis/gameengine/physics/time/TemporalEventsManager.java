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
package com.esferixis.gameengine.physics.time;

import java.io.Serializable;

/**
 * @author ariel
 *
 */
public abstract class TemporalEventsManager implements Serializable {
	/**
	 * 
	 */
	private static final long serialVersionUID = 539744274677877953L;

	TemporalEventsManager() {
		
	}
	
	/**
	 * @post Devuelve el tiempo actual,
	 * 		 si no se lanzó ninguno devuelve Float.NEGATIVE_INFINITY
	*/
	public abstract float getCurrentTime();
	
	/**
	 * @pre Tiene que que haber eventos pendientes
	 * @post Devuelve el tiempo del próximo evento.
	 */
	public abstract float getNearestEventTime();
	
	/**
	 * @post Devuelve si quedan eventos pendientes
	 */
	public abstract boolean remainingEvents();
	
	/**
	 * @pre El evento temporal no puede ser nulo, y no puede estar en el pasado
	 * @post Agrega el evento temporal especificado, si está agregado
	 * 		 no hace nada
	 */
	public abstract void addEvent(TemporalEvent temporalEvent);
	
	/**
	 * @pre El evento temporal no puede ser nulo
	 * @post Quita el evento temporal especificado, si no está agregado
	 * 		 no hace nada
	 */
	public abstract void removeEvent(TemporalEvent temporalEvent);
}
