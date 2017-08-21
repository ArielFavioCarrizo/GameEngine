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
import java.util.PriorityQueue;
import java.util.Queue;

/**
 * Máquina de estados temporales
 * 
 * @author Ariel Favio Carrizo
 *
 */
public final class RootTemporalEventsEngine extends TemporalEventsEngine implements Serializable {
	/**
	 * 
	 */
	private static final long serialVersionUID = -5052368056251587074L;
	
	private float currentTime;
	
	/**
	 * @post Crea una máquina
	 */
	public RootTemporalEventsEngine() {
		this.currentTime = -Float.MAX_VALUE;
	}

	/**
	 * @pre La máquina no tiene que estar en marcha, y tiene que haber eventos
	 * 		pendientes.
	 * @post Pone en funcionamiento la máquina.
	 */
	public void run() {
		if ( !this.running ) {
			
			if ( this.getEventsManager().remainingEvents() ) {
				this.running = true;
			
				while ( this.running ) {
					if ( this.getEventsManager().remainingEvents() ) {
						this.currentTime = this.getEventsManager().getNearestEventTime();
						this.launchNearestEvent();
					}
					else {
						throw new RuntimeException("Fatal error: No temporal events left");
					}
				}
			}
			else {
				throw new IllegalStateException("Expected temporal events");
			}
		}
		else {
			throw new IllegalStateException("Expected that the engine isn't running");
		}
	}
	
	/**
	 * @pre La máquina tiene que estar en marcha
	 * @post Detiene la máquina de eventos temporales
	 */
	public void stop() {
		if ( this.running ) {
			this.running = false;
		}
		else {
			throw new IllegalStateException("Expected that the engine is running");
		}
	}

	/* (non-Javadoc)
	 * @see com.arielcarrizo.gameengine.physics.time.TemporalEventsEngine#notifyLastEventToBeLaunchedChange()
	 */
	@Override
	protected void notifyLastEventToBeLaunchedChange() {
		
	}

	/* (non-Javadoc)
	 * @see com.arielcarrizo.gameengine.physics.time.TemporalEventsEngine#getCurrentTime()
	 */
	@Override
	protected float getCurrentTime() {
		return this.currentTime;
	}
}
