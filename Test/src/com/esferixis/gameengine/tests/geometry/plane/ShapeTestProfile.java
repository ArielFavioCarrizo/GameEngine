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
import com.esferixis.math.Vector2f;
import com.esferixis.math.Vector4f;
import com.esferixis.misc.observer.Observer;
import com.esferixis.misc.reference.DynamicReference;

/**
 * @author ariel
 *
 */
public class ShapeTestProfile<S extends Shape<?>> {
	private final TransformTestProfile transformTestProfile;
	private final Shape<?> initialShape;
	
	public ShapeTestProfile(Shape<?> initialShape, Axis horizontalSpeedAxis, Axis verticalSpeedAxis, Axis angleSpeedAxis) {
		if ( ( initialShape != null ) && ( horizontalSpeedAxis != null ) && ( verticalSpeedAxis != null ) && ( angleSpeedAxis != null ) ) {
			this.transformTestProfile = new TransformTestProfile(horizontalSpeedAxis, verticalSpeedAxis, angleSpeedAxis);
			this.initialShape = initialShape;
		}
		else {
			throw new NullPointerException();
		}
	}
	
	private ShapeTestProfile(Shape<?> initialShape, TransformTestProfile transformTestProfile) {
		if ( ( initialShape != null ) && ( transformTestProfile != null ) ) {
			this.transformTestProfile = transformTestProfile;
			this.initialShape = initialShape;
		}
		else {
			throw new NullPointerException();
		}
	}
	
	/**
	 * @post Efectúa una actualización
	 */
	public void update() {
		this.transformTestProfile.update();
	}
	
	/**
	 * @post Devuelve la figura transformada
	 */
	public S getActualShape() {
		return (S) this.initialShape.transform(this.transformTestProfile.getTransformMatrix());
	}
	
	/**
	 * @pre Ninguno de los dos puede ser nulo
	 * @post Renderiza la figura en el renderizador especificado con el color especificado
	 */
	public void render(PlaneObjectComponentRenderer objectRenderer, Vector4f color) {
		if ( ( objectRenderer != null ) && ( color != null ) ) {
			objectRenderer.render(new ShapeObject(this.getActualShape(), new UniformColorDistribution(color)));
		}
		else {
			throw new NullPointerException();
		}
	}
	
	public void attachObservers() {
		this.transformTestProfile.attachObservers();
	}
	
	public void detachObservers() {
		this.transformTestProfile.detachObservers();
	}
	
	/**
	 * @pre El administrador de servicios de plataforma no puede ser nulo
	 * @post Crea un perfil para la primer figura, suministrando el administrador
	 * 		 de servicios de plataforma y la figura especificada
	 */
	public static <S extends Shape<?>> ShapeTestProfile<S> createForFirstShape(PlatformServiceManager platformServiceManager, S shape) {
		if ( ( platformServiceManager != null ) && ( shape != null ) ) {
			return new ShapeTestProfile<S>(shape, TransformTestProfile.createForFirstShape(platformServiceManager));
		}
		else {
			throw new NullPointerException();
		}
	}
	
	public static ShapeTestProfile<FiniteProportionalHolomorphicShape<?>> createForFirstShape(PlatformServiceManager platformServiceManager, FiniteProportionalHolomorphicShape<?> shape) {
		return (ShapeTestProfile) createForFirstShape(platformServiceManager, (Shape) shape);
	}
	
	public static ShapeTestProfile<FiniteAffineHolomorphicShape<?>> createForFirstShape(PlatformServiceManager platformServiceManager, FiniteAffineHolomorphicShape<?> shape) {
		return (ShapeTestProfile) createForFirstShape(platformServiceManager, (Shape) shape);
	}
	
	/**
	 * @pre El administrador de servicios de plataforma no puede ser nulo
	 * @post Crea un perfil para la segunda figura, suministrando el administrador
	 * 		 de servicios de plataforma y la figura especificada
	 */
	public static <S extends Shape<?>> ShapeTestProfile<S> createForSecondShape(PlatformServiceManager platformServiceManager, S shape) {
		if ( ( platformServiceManager != null ) && ( shape != null ) ) {
			if ( platformServiceManager.getInputManager().getKeyboard() != null ) {
				return new ShapeTestProfile<S>(shape, TransformTestProfile.createForSecondShape(platformServiceManager));
			}
			else {
				throw new RuntimeException("Cannot find keyboard");
			}
		}
		else {
			throw new NullPointerException();
		}
	}
	
	public static ShapeTestProfile<FiniteProportionalHolomorphicShape<?>> createForSecondShape(PlatformServiceManager platformServiceManager, FiniteProportionalHolomorphicShape<?> shape) {
		return (ShapeTestProfile) createForSecondShape(platformServiceManager, (Shape) shape);
	}
	
	public static ShapeTestProfile<FiniteAffineHolomorphicShape<?>> createForSecondShape(PlatformServiceManager platformServiceManager, FiniteAffineHolomorphicShape<?> shape) {
		return (ShapeTestProfile) createForSecondShape(platformServiceManager, (Shape) shape);
	}
}
