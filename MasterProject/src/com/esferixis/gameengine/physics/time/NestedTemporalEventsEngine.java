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

/**
 * @author ariel
 *
 */
public final class NestedTemporalEventsEngine extends TemporalEventsEngine {
	/**
	 * 
	 */
	private static final long serialVersionUID = -7614677928654989327L;
	
	private final TemporalEventsManager containerTemporalEventsManager;
	
	private float exteriorReferenceTime;
	private float interiorStartTime;
	
	private TemporalEvent exteriorTemporalEvent;
	
	/**
	 * @post Crea el motor de eventos temporales anidado, con el manejador de eventos
	 * 		 temporales, y el tiempo de comienzo interno especificado
	 */
	public NestedTemporalEventsEngine(TemporalEventsManager containerTemporalEventsManager, float internalStartTime) {
		if ( containerTemporalEventsManager != null ) {
			this.containerTemporalEventsManager = containerTemporalEventsManager;
			this.exteriorReferenceTime = 0;
			this.interiorStartTime = interiorStartTime;
			this.exteriorTemporalEvent = null;
		}
		else {
			throw new NullPointerException();
		}
	}
	
	/**
	 * @post Arranca la máquina
	 */
	public void start() {
		if ( !this.running ) {
			this.exteriorReferenceTime = this.containerTemporalEventsManager.getCurrentTime();
			
			this.prepareNextExteriorEvent();
			
			this.running = true;
		}
	}
	
	/**
	 * @post Detiene la máquina
	 */
	public void stop() {
		if ( this.running ) {
			this.cancelPendingExteriorEvent();
			
			this.interiorStartTime = this.getCurrentTime();
			
			this.running = false;
		}
	}
	
	/**
	 * @post Prepara el próximo evento exterior
	 */
	private void prepareNextExteriorEvent() {
		if ( this.getEventsManager().remainingEvents() ) {
		
			this.exteriorTemporalEvent = new TemporalEvent(this.exteriorReferenceTime + this.getEventsManager().getNearestEventTime() - this.getEventsManager().getCurrentTime()) {

				@Override
				protected void launch(TemporalEventsManager eventsManager) {
					NestedTemporalEventsEngine.this.launchNearestEvent();
					NestedTemporalEventsEngine.this.prepareNextExteriorEvent();
				}
				
			};
			
			this.containerTemporalEventsManager.addEvent(this.exteriorTemporalEvent);
		}
	}
	
	/**
	 * @post Cancela el evento pendiente exterior, si no hay no hace
	 * 		 nada
	 */
	private void cancelPendingExteriorEvent() {
		if ( this.exteriorTemporalEvent != null ) {
			this.containerTemporalEventsManager.removeEvent(this.exteriorTemporalEvent);
		}
	}

	/* (non-Javadoc)
	 * @see com.arielcarrizo.gameengine.physics.time.TemporalEventsEngine#notifyLastEventToBeLaunchedChange()
	 */
	@Override
	protected void notifyLastEventToBeLaunchedChange() {
		if ( this.running ) {
			this.cancelPendingExteriorEvent();
			this.prepareNextExteriorEvent();
		}
	}

	/* (non-Javadoc)
	 * @see com.arielcarrizo.gameengine.physics.time.TemporalEventsEngine#getCurrentTime()
	 */
	@Override
	protected float getCurrentTime() {
		float result = this.interiorStartTime;
		
		if ( this.running ) {
			result += this.containerTemporalEventsManager.getCurrentTime() - this.exteriorReferenceTime;
		}
		
		return result;
	}
}
