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

public abstract class TemporalEvent implements Comparable<TemporalEvent>{
	protected final float launchTime; // Tiempo de lanzamiento
	
	/**
	 * @pre La sesión no puede ser nula y el tiempo tiene que ser un número finito
	 * @post Crea un cambio de estado en el instante especificado
	 */
	public TemporalEvent(float launchTime) {
		if ( !Float.isInfinite(launchTime) && !Float.isNaN(launchTime) ) {
			this.launchTime = launchTime;
		}
		else {
			throw new IllegalArgumentException();
		}
	}
	
	/**
	 * @post Devuelve el instante de tiempo en que sucederá el evento
	 */
	public float getLaunchTime() {
		return this.launchTime;
	}
	
	/**
	 * @post Dispara el evento con el manejador de eventos especificado
	 */
	protected abstract void launch(TemporalEventsManager eventsManager);
	
	/**
	 * @post Devuelve si el otro evento de cambio de estado
	 * 		 es más "pequeño" (Más reciente) o más "grande" (Menos reciente)
	 */
	@Override
	public int compareTo(TemporalEvent other) {
		if ( this.launchTime < other.launchTime) {
			return -1;
		}
		else if ( this.launchTime == other.launchTime ) {
			return 0;
		}
		else {
			return 1;
		}
	}
}
