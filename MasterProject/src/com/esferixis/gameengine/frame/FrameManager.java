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

package com.esferixis.gameengine.frame;

import java.io.Serializable;

import com.esferixis.gameengine.physics.time.TemporalEvent;
import com.esferixis.gameengine.physics.time.TemporalEventsManager;
import com.esferixis.gameengine.platform.PlatformServiceManager;
import com.esferixis.gameengine.renderengine.frontend.RenderEngineFrontend;
import com.esferixis.gameengine.renderengine.frontend.renderingFrame.RenderingFrameEmmiter;
import com.esferixis.gameengine.renderengine.frontend.renderingFrame.RenderingFrameRenderer;
import com.esferixis.math.intervalarithmetic.FloatClosedInterval;

/**
 * Tarea de cuadro
 * 
 * @author ariel
 *
 */
public abstract class FrameManager implements Serializable {
	/**
	 * 
	 */
	private static final long serialVersionUID = -5994612168917660636L;
	
	private TemporalEventsManager temporalEventsManager;
	
	private final float startTime;
	private final int framesPerSecond;
	
	private long frameIndex;
	
	private transient PlatformServiceManager serviceManager;
	private transient RenderEngineFrontend renderEngineFrontend;
	
	private transient boolean pendingPauseNotification = false;
	
	private transient long pendingSystemTime;
	private transient long systemStartTime;
	
	private boolean frameDrop;
	
	/**
	 * @pre El tiempo de comienzo no puede ser anterior al último evento temporal.
	 * 		Los cuadros por segundo tienen que ser positivos
	 * @post Crea el administrador de cuadro con el manejador de eventos temporales, el tiempo de comienzo y los cuadros por segundo
	 * 		 especificados.
	 * 		 Por defecto, el "frame drop" está activado
	 */
	public FrameManager(TemporalEventsManager temporalEventsManager, float startTime, int framesPerSecond) {
		if ( temporalEventsManager != null ) {
			if ( startTime >= temporalEventsManager.getCurrentTime() ) {
				if ( framesPerSecond > 0 ) {
					this.temporalEventsManager = temporalEventsManager;
					this.startTime = startTime;
					this.framesPerSecond = framesPerSecond;
					this.frameIndex = 0;
					
					this.frameDrop = true;
					
					this.prepareNewFrame(startTime);
				}
				else {
					throw new IllegalArgumentException("Expected positive frames per second");
				}
			}
			else {
				throw new IllegalArgumentException("Expected start time equal or greater than last launched event time");
			}
		}
		else {
			throw new NullPointerException();
		}
	}
	
	/**
	 * @pre El administrador de servicios de plataforma, y el frontend
	 * 		del motor de renderización no pueden ser nulos
	 * @post Asigna los servicios de plataforma.
	 * 		 Asocia el manejador de cuadros con el manejador de servicios de plataforma, y el frontend
	 * 		 del motor de renderización especificado.
	 * 		 Tiene que hacerse después de su deserialización o creación
	 */
	public void setPlatformServices(final PlatformServiceManager serviceManager, final RenderEngineFrontend renderEngineFrontend) {
		if ( ( serviceManager != null ) && ( renderEngineFrontend != null ) ) {
			if ( this.serviceManager == null ) {
				this.serviceManager = serviceManager;
				this.renderEngineFrontend = renderEngineFrontend;
				
				this.notifyPause();
			}
			else {
				throw new IllegalStateException("Platform services has been initialized");
			}
		}
		else {
			throw new NullPointerException();
		}
	}
	
	private void checkPlatformServices() {
		if ( this.serviceManager == null ) {
			throw new IllegalStateException("Missing platform services initialization");
		}
	}
	
	/**
	 * @post Prepara un nuevo cuadro
	 */
	private void prepareNewFrame(final float time) {
		this.temporalEventsManager.addEvent(new TemporalEvent(time) {

			@Override
			protected void launch(TemporalEventsManager eventsManager) {
				if ( FrameManager.this.temporalEventsManager != null ) {
					FrameManager.this.newFrame();
				}
			}
			
		});
	}
	
	/**
	 * @post Realiza un nuevo cuadro
	 */
	private void newFrame() {
		this.checkPlatformServices();
		
		if ( this.frameIndex != Long.MAX_VALUE ) {
			final long frameDuration = 1000000000 / this.framesPerSecond;
			
			final FloatClosedInterval timeInterval = new FloatClosedInterval(this.frameIndex, this.frameIndex+1).divide(this.framesPerSecond).add(this.startTime);
	
			if ( (!this.frameDrop) || ( this.pendingPauseNotification || ( this.pendingSystemTime < frameDuration ) ) ) {
				this.renderEngineFrontend.getScreenRenderingFrame().render(new RenderingFrameEmmiter() {
					
					@Override
					protected void render_internal(final RenderingFrameRenderer renderer) {
						FrameManager.this.render(renderer, timeInterval.getMin());
					}
					
				});
			}
			
			if ( (!this.frameDrop) || ( this.pendingPauseNotification ) ) {
				this.pendingSystemTime = 0;
				this.pendingPauseNotification = false;
			}
			else {
				long systemEndTime = System.nanoTime();
				
				this.pendingSystemTime += systemEndTime - this.systemStartTime - frameDuration;
				
				if ( this.pendingSystemTime < 1000000 ) {
					try {
						Thread.sleep(-this.pendingSystemTime/1000000);
					} catch (InterruptedException e) {
						Thread.currentThread().interrupt();
						throw new RuntimeException(e);
					}
					
					this.pendingSystemTime = 0;
				}
			}
			
			this.executeFrameTask(timeInterval);
			
			this.serviceManager.newFrame();
			
			this.frameIndex++;
			this.systemStartTime = System.nanoTime();
			
			this.prepareNewFrame(timeInterval.getMax());
		}
		else {
			throw new RuntimeException("Fatal error: Frame index overflow");
		}
	}
	
	/**
	 * @post Especifica si tiene que haber "frame drop"
	 */
	public void setFrameDrop(boolean value) {
		if ( value != this.frameDrop ) {			
			this.frameDrop = value;
		}
	}
	
	/**
	 * @post Devuelve si tiene que haber "frame drop"
	 */
	public boolean getFrameDrop() {
		return this.frameDrop;
	}
	
	/**
	 * @post Desasocia el manejador de eventos
	 */
	public void detach() {
		this.temporalEventsManager = null;
	}
	
	/**
	 * @post Notifica una pausa
	 */
	protected final void notifyPause() {
		this.pendingPauseNotification = true;
	}
	
	/**
	 * @post Ejecuta la tarea de cuadro en el intervalo de tiempo especificado
	 */
	protected abstract void executeFrameTask(FloatClosedInterval timeInterval);
	
	/**
	 * @post Efectúa la renderización con el renderizador de cuadro en el instante de tiempo especificado
	 */
	protected abstract void render(RenderingFrameRenderer renderer, float time);
}
