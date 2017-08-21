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

package com.esferixis.gameengine.tests.physics.plane.kinematics.collisionDetection;

import com.esferixis.math.intervalarithmetic.FloatClosedInterval;

public final class CollisionAnimation {
	private final FloatClosedInterval timeInterval;
	private final Float collisionTime;
	
	private Float startSystemTime;
	private float actualSystemTime;
	
	/**
	 * @post Crea una animación de colisión con el intervalo de tiempo, y el tiempo de colisión
	 * 		 especificados
	 * @param timeInterval
	 * @param collisionTime
	 */
	public CollisionAnimation(FloatClosedInterval timeInterval, Float collisionTime) {
		this.timeInterval = timeInterval;
		this.collisionTime = collisionTime;
		
		this.startSystemTime = null;
	}
	
	/**
	 * @post Devuelve el tiempo actual
	 */
	public float getTime() {
		float time;
		if ( this.startSystemTime == null ) {
			time = 0.0f;
		}
		else {
			time = this.actualSystemTime - this.startSystemTime;
		}
		
		time += this.timeInterval.getMin();
		
		time = Math.min(time, this.timeInterval.getMax());
		
		if ( this.collisionTime != null ) {
			time = Math.min(time, this.collisionTime);
		}
		
		return time;
	}
	
	/**
	 * @post Especifica el tiempo de sistema
	 */
	public void setSystemTime(float systemTime) {
		if ( this.startSystemTime == null ) {
			this.startSystemTime = systemTime;
		}
		
		this.actualSystemTime = systemTime;
	}
}
