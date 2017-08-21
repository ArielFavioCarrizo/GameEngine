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
package com.esferixis.gameengine.tests;

import com.esferixis.gameengine.platform.PlatformServiceManager;
import com.esferixis.gameengine.tests.geometry.plane.AffineHolomorphicShapeDilateTest;
import com.esferixis.gameengine.tests.geometry.plane.ProportionalHolomorphicShapeBoundingAffineTest;
import com.esferixis.gameengine.tests.geometry.plane.ProportionalHolomorphicShapeIntersectionTest;
import com.esferixis.gameengine.tests.geometry.plane.ProportionalHolomorphicShapeMaxDistanceToOriginTest;
import com.esferixis.gameengine.tests.geometry.plane.ProportionalHolomorphicShapeMinDistanceToOriginTest;
import com.esferixis.gameengine.tests.geometry.plane.ProportionalHolomorphicShapeNearestNormalToOriginTest;
import com.esferixis.gameengine.tests.geometry.plane.ProportionalHolomorphicShapeNearestPointBetweenShapePerimetersTest;
import com.esferixis.gameengine.tests.geometry.plane.ProportionalHolomorphicShapePerimetralDilateTest;
import com.esferixis.gameengine.tests.geometry.plane.ProportionalHolomorphicShapeRayIntersectionTest;
import com.esferixis.gameengine.tests.physics.plane.dynamics.body.dynamicscontainertest.DynamicsContainerTest;
import com.esferixis.gameengine.tests.physics.plane.kinematics.collisionDetection.PlaneKinematicBodyPairCollisionDetectionTest;
import com.esferixis.gameengine.tests.physics.plane.kinematics.containers.KinematicContainerTest;
import com.esferixis.gameengine.tests.physics.plane.kinematics.kinematicMapper.KinematicMapperTest;
import com.esferixis.gameengine.tests.renderengine.backend.plane.ParticleSystemTest;
import com.esferixis.gameengine.tests.renderengine.backend.plane.RotatingColorMaskedTexturedSquareTest;
import com.esferixis.gameengine.tests.renderengine.backend.plane.RotatingVertexColoredTriangleTest;
import com.esferixis.gameengine.tests.renderengine.backend.plane.ShapeRenderingTest;
import com.esferixis.gameengine.tests.renderengine.backend.space.RotatingTexturedSquareTest;
import com.esferixis.gameengine.tests.renderengine.backend.space.RotatingTexturedSquareTestWithMutation;
import com.esferixis.gameengine.tests.renderengine.backend.space.RotatingUniformColoredTriangleTest;
import com.esferixis.gameengine.tests.renderengine.backend.space.RotatingVertexColoredCubeTest;

/**
 * @author ariel
 *
 */
public class GameTestsLauncher {
	public static void run(PlatformServiceManager serviceManager) {
		new GameEngineTestRunnableGroup("Game engine tests",
				new GameEngineTestRunnableGroup("Render engine",
					new GameEngineTestRunnableGroup("Backend tests",
							new GameEngineTestRunnableGroup("2d tests",
									new ParticleSystemTest(), new RotatingColorMaskedTexturedSquareTest(), new RotatingVertexColoredTriangleTest(), new ShapeRenderingTest()
							),
							new GameEngineTestRunnableGroup("3d tests",
									new RotatingTexturedSquareTest(), new RotatingTexturedSquareTestWithMutation(), new RotatingUniformColoredTriangleTest(),
									new RotatingVertexColoredCubeTest(), new com.esferixis.gameengine.tests.renderengine.backend.space.RotatingVertexColoredTriangleTest()
							)
					),
					new GameEngineTestRunnableGroup("Frontend tests",
							new GameEngineTestRunnableGroup("2d tests",
								new GameEngineTestRunnableGroup("Static stage",
										new com.esferixis.gameengine.tests.renderengine.frontend.plane.staticstage.RotatingVertexColoredTriangleTest(),
										new com.esferixis.gameengine.tests.renderengine.frontend.plane.staticstage.RotatingColorMaskedTexturedSquareTest(),
										new com.esferixis.gameengine.tests.renderengine.frontend.plane.staticstage.ParticleSystemTest()
								),
								new GameEngineTestRunnableGroup("Kinematic stage",
										new com.esferixis.gameengine.tests.renderengine.frontend.plane.kinematics.KinematicRenderPlaneObjectContainerTest(),
										new com.esferixis.gameengine.tests.renderengine.frontend.plane.kinematics.KinematicRenderPlaneObjectContainerViewerTest()
								)
							)
					)
				),
				new GameEngineTestRunnableGroup("Physics",
						new GameEngineTestRunnableGroup("2d tests",
							new GameEngineTestRunnableGroup("Kinematics",
									new KinematicMapperTest(), new PlaneKinematicBodyPairCollisionDetectionTest(), new KinematicContainerTest()
							),
							new GameEngineTestRunnableGroup("Dynamics",
									new DynamicsContainerTest()
							)
						)
				),
				new GameEngineTestRunnableGroup("Geometry tests",
						new GameEngineTestRunnableGroup("2d tests",
								new ProportionalHolomorphicShapeMaxDistanceToOriginTest(), new ProportionalHolomorphicShapeMinDistanceToOriginTest(), new ProportionalHolomorphicShapeNearestNormalToOriginTest(), new ProportionalHolomorphicShapeNearestPointBetweenShapePerimetersTest(), new ProportionalHolomorphicShapeIntersectionTest(), new ProportionalHolomorphicShapePerimetralDilateTest(), new ProportionalHolomorphicShapeRayIntersectionTest(), new ProportionalHolomorphicShapeBoundingAffineTest(), new AffineHolomorphicShapeDilateTest()
						)
				)
		).run(serviceManager);
	}
}
