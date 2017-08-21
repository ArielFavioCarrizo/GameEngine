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

package com.esferixis.gameengine.tests.renderengine.frontend.plane.kinematics;

import java.util.Map;

import com.esferixis.gameengine.frame.FrameManager;
import com.esferixis.gameengine.physics.time.RootTemporalEventsEngine;
import com.esferixis.gameengine.platform.PlatformServiceManager;
import com.esferixis.gameengine.platform.PlatformServiceManagerException;
import com.esferixis.gameengine.platform.display.ScreenConfig;
import com.esferixis.gameengine.platform.input.Axis;
import com.esferixis.gameengine.platform.input.Button;
import com.esferixis.gameengine.platform.input.ButtonPairBasedVirtualAxis;
import com.esferixis.gameengine.platform.input.Keyboard;
import com.esferixis.gameengine.renderengine.frontend.RenderEngineFrontend;
import com.esferixis.gameengine.renderengine.frontend.RenderEngineFrontendConfiguration;
import com.esferixis.gameengine.renderengine.frontend.plane.kinematics.InstantKinematicRenderer;
import com.esferixis.gameengine.renderengine.frontend.plane.kinematics.containers.KinematicRenderPlaneObjectContainer;
import com.esferixis.gameengine.renderengine.frontend.plane.kinematics.containers.RenderizableKinematicRenderPlaneObjectContainer;
import com.esferixis.gameengine.renderengine.frontend.plane.kinematics.view.PlaneCamera;
import com.esferixis.gameengine.renderengine.frontend.plane.kinematics.view.PlaneView;
import com.esferixis.gameengine.renderengine.frontend.plane.staticstage.PlaneRendererEmmiter;
import com.esferixis.gameengine.renderengine.frontend.plane.staticstage.StaticPlaneObjectRenderer;
import com.esferixis.gameengine.renderengine.frontend.renderingFrame.RenderingFrameEmmiter;
import com.esferixis.gameengine.renderengine.frontend.renderingFrame.RenderingFrameRenderer;
import com.esferixis.gameengine.tests.GameEngineTestRunnable;
import com.esferixis.gameengine.tests.menu.Menu;
import com.esferixis.gameengine.tests.menu.MenuElement;
import com.esferixis.gameengine.tests.renderengine.frontend.plane.kinematics.scenes.MultipleStaticTrianglesTestScene;
import com.esferixis.gameengine.tests.renderengine.frontend.plane.kinematics.scenes.OneTriangleTestScene;
import com.esferixis.gameengine.tests.testscene.TestScene;
import com.esferixis.gameengine.tests.testscene.TestSceneRunner;
import com.esferixis.math.Vector2f;
import com.esferixis.math.Vector4f;
import com.esferixis.math.intervalarithmetic.FloatClosedInterval;
import com.esferixis.misc.ElementCallback;
import com.esferixis.misc.ElementProcessor;
import com.esferixis.misc.reference.DynamicReference;

/**
 * Prueba de animación de rotación de triángulo
 * 
 * @author Ariel Favio Carrizo
 *
 */
public final class KinematicRenderPlaneObjectContainerViewerTest extends GameEngineTestRunnable {
	public KinematicRenderPlaneObjectContainerViewerTest() {
		super("Kinematic Render Plane Object Container Viewer (Frontend, kinematics)");
	}
	
	private static TestScene[] testScenes = new TestScene[] {
		new OneTriangleTestScene(), new MultipleStaticTrianglesTestScene()
	};
	
	/**
	 * @post Ejecuta una instancia del juego con el administrador
	 * 		 de servicio de plataforma especificado
	 */
	public void run(final PlatformServiceManager serviceManager) {
		TestSceneRunner.run(serviceManager, testScenes);
	}
}
