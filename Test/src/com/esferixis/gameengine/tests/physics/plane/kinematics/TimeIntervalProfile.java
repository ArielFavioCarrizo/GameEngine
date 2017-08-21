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

package com.esferixis.gameengine.tests.physics.plane.kinematics;

import com.esferixis.gameengine.platform.PlatformServiceManager;
import com.esferixis.gameengine.platform.input.Axis;
import com.esferixis.gameengine.platform.input.Button;
import com.esferixis.gameengine.platform.input.ButtonPairBasedVirtualAxis;
import com.esferixis.gameengine.platform.input.InputUnitObserver;
import com.esferixis.gameengine.platform.input.Keyboard;
import com.esferixis.math.intervalarithmetic.FloatClosedInterval;
import com.esferixis.misc.observer.Observer;
import com.esferixis.misc.reference.DynamicReference;

public final class TimeIntervalProfile {
	private final PlatformServiceManager platformServiceManager;
	
	private final DynamicReference<Float> minSpeedAxisValue = new DynamicReference<Float>(0.0f);
	private final DynamicReference<Float> maxSpeedAxisValue = new DynamicReference<Float>(0.0f);
	
	private final InputUnitObserver<Float> minSpeedAxisObserver;
	private final InputUnitObserver<Float> maxSpeedAxisObserver;
	
	private final Axis minSpeedAxis;
	private final Axis maxSpeedAxis;
	
	private float minSpeedValue;
	private float maxSpeedValue;
	
	private static final float accelerationFactor = 0.01f;
	
	private FloatClosedInterval timeInterval;
	
	/**
	 * @post Crea el perfil de intervalo de tiempo con el administrador de servicio
	 * 		 de plataforma especificado
	 * @param platformServiceManager
	 */
	public TimeIntervalProfile(PlatformServiceManager platformServiceManager, FloatClosedInterval initialTimeInterval) {
		if ( platformServiceManager != null ) {
			this.platformServiceManager = platformServiceManager;
			
			this.minSpeedAxisObserver = new InputUnitObserver<Float>(Observer.Type.STRONG) {
	
				@Override
				protected void notifyStateChange(Float newPosition) {
					TimeIntervalProfile.this.minSpeedAxisValue.set(newPosition);
				}
				
			};
			
			this.maxSpeedAxisObserver = new InputUnitObserver<Float>(Observer.Type.STRONG) {
	
				@Override
				protected void notifyStateChange(Float newPosition) {
					TimeIntervalProfile.this.maxSpeedAxisValue.set(newPosition);
				}
				
			};
			
			{
				final Keyboard keyboard = platformServiceManager.getInputManager().getKeyboard();
				this.minSpeedAxis = new ButtonPairBasedVirtualAxis( keyboard.getMapping().get(Keyboard.Key.KEY_1), keyboard.getMapping().get(Keyboard.Key.KEY_2) );
				this.maxSpeedAxis = new ButtonPairBasedVirtualAxis( keyboard.getMapping().get(Keyboard.Key.KEY_3), keyboard.getMapping().get(Keyboard.Key.KEY_4) );
			}
			
			this.minSpeedValue = 0.0f;
			this.maxSpeedValue = 0.0f;
			
			this.timeInterval = new FloatClosedInterval(0.0f, 0.0f);
		}
		else {
			throw new NullPointerException();
		}
	}
	
	/**
	 * @post Asocia los observers
	 */
	public void attachObservers() {
		this.minSpeedAxisObserver.attach(this.minSpeedAxis);
		this.maxSpeedAxisObserver.attach(this.maxSpeedAxis);
	}
	
	/**
	 * @post Actualiza
	 */
	public void update() {
		if ( this.minSpeedAxisValue.get() != 0.0f ) {
			this.minSpeedValue += this.minSpeedAxisValue.get() * accelerationFactor;
		}
		else {
			this.minSpeedValue = 0.0f;
		}
		
		if ( this.maxSpeedAxisValue.get() != 0.0f ) {
			this.maxSpeedValue += this.maxSpeedAxisValue.get() * accelerationFactor;
		}
		else {
			this.maxSpeedValue = 0.0f;
		}
			
		{
			float newMin = this.timeInterval.getMin();
			float newMax = this.timeInterval.getMax();
			
			if ( newMin + this.minSpeedValue > newMax ) {
				newMin = newMax;
				this.minSpeedValue = 0.0f;
			}
			else {
				newMin += this.minSpeedValue;
			}
			
			if ( newMax + this.maxSpeedValue < newMin ) {
				newMax = newMin;
				this.maxSpeedValue = 0.0f;
			}
			else {
				newMax += this.maxSpeedValue;
			}
			
			this.timeInterval = new FloatClosedInterval(newMin, newMax);
		}
	}
	
	/**
	 * @post Devuelve el intervalo de tiempo
	 */
	public FloatClosedInterval getTimeInterval() {
		return this.timeInterval;
	}
	
	/**
	 * @post Desasocia los observadores
	 */
	public void detachObservers() {
		this.minSpeedAxisObserver.detach();
		this.maxSpeedAxisObserver.detach();
	}
}
