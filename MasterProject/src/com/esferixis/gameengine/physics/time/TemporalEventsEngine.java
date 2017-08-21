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
 * @author ariel
 *
 */
public abstract class TemporalEventsEngine implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = 3846771343446828852L;
	
	private final Queue<TemporalEvent> timeEventsQueue;
	
	private final TemporalEventsManager eventsManager;
	
	protected boolean running;
	
	/**
	 * @post Crea una máquina
	 */
	public TemporalEventsEngine() {
		this.timeEventsQueue = new PriorityQueue<TemporalEvent>();
		
		this.eventsManager = new TemporalEventsManager() {

			/**
			 * 
			 */
			private static final long serialVersionUID = -7897119159772952029L;

			@Override
			public float getCurrentTime() {
				return TemporalEventsEngine.this.getCurrentTime();
			}

			@Override
			public float getNearestEventTime() {
				final float result;
				
				if ( this.remainingEvents() ) {
					result = TemporalEventsEngine.this.timeEventsQueue.peek().getLaunchTime();
				}
				else {
					throw new IllegalStateException("Expected remaining events");
				}
				
				return result;
			}

			@Override
			public void addEvent(TemporalEvent temporalEvent) {
				if ( temporalEvent != null ) {
					if ( temporalEvent.getLaunchTime() >= this.getCurrentTime() ) {
						final float oldNearestEventTime = ( this.remainingEvents() ? this.getNearestEventTime() : Float.POSITIVE_INFINITY );
						
						TemporalEventsEngine.this.timeEventsQueue.add(temporalEvent);
						
						if ( temporalEvent.getLaunchTime() <= oldNearestEventTime ) {
							TemporalEventsEngine.this.notifyLastEventToBeLaunchedChange();
						}
					}
					else {
						throw new IllegalArgumentException("Attemped to add an past event");
					}
				}
				else {
					throw new NullPointerException();
				}
			}

			@Override
			public void removeEvent(TemporalEvent temporalEvent) {
				final TemporalEvent oldNearestEvent = TemporalEventsEngine.this.timeEventsQueue.peek();
				TemporalEventsEngine.this.timeEventsQueue.remove(temporalEvent);
				
				if ( temporalEvent == oldNearestEvent ) {
					TemporalEventsEngine.this.notifyLastEventToBeLaunchedChange();
				}
			}

			@Override
			public boolean remainingEvents() {
				return !TemporalEventsEngine.this.timeEventsQueue.isEmpty();
			}
			
		};
		
		this.running = false;
	}
	
	/**
	 * @post Devuelve el administrador de eventos
	 */
	public final TemporalEventsManager getEventsManager() {
		return this.eventsManager;
	}
	
	/**
	 * @post Notifica un cambio en el último evento a lanzarse
	 */
	protected abstract void notifyLastEventToBeLaunchedChange();
	
	/**
	 * @post Devuelve si está en marcha
	 */
	public final boolean isRunning() {
		return this.running;
	}
	
	/**
	 * @post Devuelve el tiempo actual
	 */
	protected abstract float getCurrentTime();
	
	/**
	 * @pre Tiene que haber eventos
	 * @post Lanza el evento más cercano
	 */
	protected final void launchNearestEvent() {
		if ( this.getEventsManager().remainingEvents() ) {
			final TemporalEvent nearestEvent = this.timeEventsQueue.poll();
			
			if (nearestEvent != null ) {
				nearestEvent.launch(this.getEventsManager());
			}
			else {
				throw new RuntimeException("Fatal error: No temporal events left");
			}
		}
		else {
			throw new IllegalStateException("Expected remaining events");
		}
	}
}
