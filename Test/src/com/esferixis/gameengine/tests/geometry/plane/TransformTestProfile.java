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
package com.esferixis.gameengine.tests.geometry.plane;

import com.esferixis.gameengine.platform.PlatformServiceManager;
import com.esferixis.gameengine.platform.input.Axis;
import com.esferixis.gameengine.platform.input.ButtonPairBasedVirtualAxis;
import com.esferixis.gameengine.platform.input.InputUnitObserver;
import com.esferixis.gameengine.platform.input.Keyboard;
import com.esferixis.gameengine.renderengine.backend.plane.PlaneObjectComponentRenderer;
import com.esferixis.gameengine.renderengine.backend.plane.shape.ShapeObject;
import com.esferixis.gameengine.renderengine.plane.shape.UniformColorDistribution;
import com.esferixis.geometry.plane.Shape;
import com.esferixis.geometry.plane.finite.FiniteAffineHolomorphicShape;
import com.esferixis.geometry.plane.finite.FiniteProportionalHolomorphicShape;
import com.esferixis.math.Matrix3f;
import com.esferixis.math.ProportionalMatrix3f;
import com.esferixis.math.Vector2f;
import com.esferixis.math.Vector4f;
import com.esferixis.misc.observer.Observer;
import com.esferixis.misc.reference.DynamicReference;

/**
 * @author ariel
 *
 */
public class TransformTestProfile {
	private static final float translateAccelerationRange = 0.001f;
	private static final float angleSpeedRange = 0.03f;
	
	private final Axis horizontalAxis, verticalAxis;
	private final Axis angleSpeedAxis;
	
	private final DynamicReference<Float> horizontalAxisValue;
	private final DynamicReference<Float> verticalAxisValue;
	private final DynamicReference<Float> angleSpeed;
	
	private Vector2f position;
	private final DynamicReference<Float> speedX, speedY;
	
	private float angle;
	
	private final InputUnitObserver<Float> horizontalSpeedAxisObserver = new InputUnitObserver<Float>(Observer.Type.STRONG) {

		@Override
		protected void notifyStateChange(Float newPosition) {
			TransformTestProfile.this.horizontalAxisValue.set(newPosition);
		}
		
	};
	
	private final InputUnitObserver<Float> verticalSpeedAxisObserver = new InputUnitObserver<Float>(Observer.Type.STRONG) {

		@Override
		protected void notifyStateChange(Float newPosition) {
			TransformTestProfile.this.verticalAxisValue.set(newPosition);
		}
		
	};
	
	private final InputUnitObserver<Float> angleSpeedAxisObserver = new InputUnitObserver<Float>(Observer.Type.STRONG) {

		@Override
		protected void notifyStateChange(Float newPosition) {
			TransformTestProfile.this.angleSpeed.set(newPosition * angleSpeedRange);
		}
		
	};
	
	private static void processSpeed(DynamicReference<Float> speed, float axisValue) {
		if ( axisValue != 0.0f ) {
			speed.set(speed.get() + axisValue * translateAccelerationRange);
		}
		else {
			speed.set(0.0f);
		}
	}
	
	public TransformTestProfile(Axis horizontalSpeedAxis, Axis verticalSpeedAxis, Axis angleSpeedAxis) {
		if ( ( horizontalSpeedAxis != null ) && ( verticalSpeedAxis != null ) && ( angleSpeedAxis != null ) ) {
			this.horizontalAxis = horizontalSpeedAxis;
			this.verticalAxis = verticalSpeedAxis;
			this.angleSpeedAxis = angleSpeedAxis;
			
			this.horizontalAxisValue = new DynamicReference<Float>(0.0f);
			this.verticalAxisValue = new DynamicReference<Float>(0.0f);
			this.angleSpeed = new DynamicReference<Float>(0.0f);
			
			this.speedX = new DynamicReference<Float>(0.0f);
			this.speedY = new DynamicReference<Float>(0.0f);
			
			this.position = Vector2f.ZERO;
			this.angle = 0.0f;
		}
		else {
			throw new NullPointerException();
		}
	}
	
	/**
	 * @post Efectúa una actualización
	 */
	public void update() {
		processSpeed(this.speedX, this.horizontalAxisValue.get());
		processSpeed(this.speedY, this.verticalAxisValue.get());
		this.position = this.position.add( new Vector2f( this.speedX.get(), this.speedY.get() ) );
		this.angle += this.angleSpeed.get();
	}
	
	/**
	 * @post Devuelve la matriz de transformación
	 */
	public ProportionalMatrix3f getTransformMatrix() {
		return Matrix3f.IDENTITY.rotate(this.angle).translate(this.position);
	}
	
	public void attachObservers() {
		this.horizontalSpeedAxisObserver.attach(this.horizontalAxis);
		this.verticalSpeedAxisObserver.attach(this.verticalAxis);
		this.angleSpeedAxisObserver.attach(this.angleSpeedAxis);
	}
	
	public void detachObservers() {
		this.horizontalSpeedAxisObserver.detach();
		this.verticalSpeedAxisObserver.detach();
		this.angleSpeedAxisObserver.detach();
	}
	
	/**
	 * @pre El administrador de servicios de plataforma no puede ser nulo
	 * @post Crea un perfil para la primer figura, suministrando el administrador
	 * 		 de servicios de plataforma
	 */
	public static TransformTestProfile createForFirstShape(PlatformServiceManager platformServiceManager) {
		if ( platformServiceManager != null ) {
			if ( platformServiceManager.getInputManager().getKeyboard() != null ) {
				final Keyboard keyboard = platformServiceManager.getInputManager().getKeyboard();
				
				final Axis horizontalSpeedAxis;
				final Axis verticalSpeedAxis;
				final Axis angleSpeedAxis;
				
				horizontalSpeedAxis = new ButtonPairBasedVirtualAxis( keyboard.getMapping().get(Keyboard.Key.KEY_LEFT), keyboard.getMapping().get(Keyboard.Key.KEY_RIGHT) );
				verticalSpeedAxis = new ButtonPairBasedVirtualAxis( keyboard.getMapping().get(Keyboard.Key.KEY_DOWN), keyboard.getMapping().get(Keyboard.Key.KEY_UP) );
				angleSpeedAxis = new ButtonPairBasedVirtualAxis( keyboard.getMapping().get(Keyboard.Key.KEY_A), keyboard.getMapping().get(Keyboard.Key.KEY_Z) );
				
				return new TransformTestProfile(horizontalSpeedAxis, verticalSpeedAxis, angleSpeedAxis);
			}
			else {
				throw new RuntimeException("Cannot find keyboard");
			}
		}
		else {
			throw new NullPointerException();
		}
	}
	
	/**
	 * @pre El administrador de servicios de plataforma no puede ser nulo
	 * @post Crea un perfil para la segunda figura, suministrando el administrador
	 * 		 de servicios de plataforma
	 */
	public static TransformTestProfile createForSecondShape(PlatformServiceManager platformServiceManager) {
		if ( platformServiceManager != null ) {
			if ( platformServiceManager.getInputManager().getKeyboard() != null ) {
				final Keyboard keyboard = platformServiceManager.getInputManager().getKeyboard();
				
				final Axis horizontalSpeedAxis;
				final Axis verticalSpeedAxis;
				final Axis angleSpeedAxis;
				
				horizontalSpeedAxis = new ButtonPairBasedVirtualAxis( keyboard.getMapping().get(Keyboard.Key.KEY_H), keyboard.getMapping().get(Keyboard.Key.KEY_J) );
				verticalSpeedAxis = new ButtonPairBasedVirtualAxis( keyboard.getMapping().get(Keyboard.Key.KEY_N), keyboard.getMapping().get(Keyboard.Key.KEY_U) );
				angleSpeedAxis = new ButtonPairBasedVirtualAxis( keyboard.getMapping().get(Keyboard.Key.KEY_F), keyboard.getMapping().get(Keyboard.Key.KEY_V) );
				
				return new TransformTestProfile(horizontalSpeedAxis, verticalSpeedAxis, angleSpeedAxis);
			}
			else {
				throw new RuntimeException("Cannot find keyboard");
			}
		}
		else {
			throw new NullPointerException();
		}
	}
}
