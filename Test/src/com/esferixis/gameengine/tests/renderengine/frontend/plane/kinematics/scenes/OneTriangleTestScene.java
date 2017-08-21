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
package com.esferixis.gameengine.tests.renderengine.frontend.plane.kinematics.scenes;

import java.util.Arrays;

import com.esferixis.gameengine.physics.plane.kinematics.mapper.PlaneKinematicMapper;
import com.esferixis.gameengine.physics.plane.kinematics.mapper.StaticAffinePlaneKinematicMapper;
import com.esferixis.gameengine.physics.plane.kinematics.mapper.TransformedPlaneKinematicMapper;
import com.esferixis.gameengine.physics.plane.kinematics.mapper.rotation.LinearRotation;
import com.esferixis.gameengine.physics.plane.kinematics.mapper.rotation.RotationPlaneKinematicMapper;
import com.esferixis.gameengine.physics.time.TemporalEventsEngine;
import com.esferixis.gameengine.platform.input.InputManager;
import com.esferixis.gameengine.renderengine.frontend.meshLayers.MeshLayersConfig;
import com.esferixis.gameengine.renderengine.frontend.meshLayers.MeshLayersConfigProfile;
import com.esferixis.gameengine.renderengine.frontend.meshLayers.VertexColoredMeshLayer;
import com.esferixis.gameengine.renderengine.frontend.meshLayers.VertexColoredMeshLayerVertexData;
import com.esferixis.gameengine.renderengine.frontend.meshLayers.VertexLayersData;
import com.esferixis.gameengine.renderengine.frontend.misc.mesh.Mesh;
import com.esferixis.gameengine.renderengine.frontend.misc.mesh.MeshTriangle;
import com.esferixis.gameengine.renderengine.frontend.misc.mesh.colored.ColoredMeshData;
import com.esferixis.gameengine.renderengine.frontend.misc.mesh.colored.ColoredMeshVertex;
import com.esferixis.gameengine.renderengine.frontend.plane.kinematics.containers.KinematicRenderPlaneObjectContainer;
import com.esferixis.gameengine.renderengine.frontend.plane.kinematics.containers.LinearKinematicRenderPlaneObjectContainer;
import com.esferixis.gameengine.renderengine.frontend.plane.kinematics.containers.RenderizableKinematicRenderPlaneObjectContainer;
import com.esferixis.gameengine.renderengine.frontend.plane.kinematics.objects.KinematicMeshObject2d;
import com.esferixis.gameengine.tests.testscene.TestScene;
import com.esferixis.math.Matrix3f;
import com.esferixis.math.Vector2f;
import com.esferixis.math.Vector4f;
import com.esferixis.misc.loader.MemoryLoader;

/**
 * @author ariel
 *
 */
public final class OneTriangleTestScene extends TestScene {

	public OneTriangleTestScene() {
		super("One triangle");
	}

	@Override
	protected Instance create_checked(InstanceData instanceData) {
		return new TestScene.Instance(instanceData) {

			@Override
			protected void load() {
				final MeshLayersConfig meshLayersConfig = new MeshLayersConfig(new VertexColoredMeshLayer());
				
				final Mesh<Vector2f, ColoredMeshData<Vector2f>> mesh = new Mesh<Vector2f, ColoredMeshData<Vector2f>>(new MemoryLoader<ColoredMeshData<Vector2f>>( new ColoredMeshData<Vector2f>( Arrays.asList( new MeshTriangle<Vector2f, ColoredMeshVertex<Vector2f>>(
						new ColoredMeshVertex<Vector2f>( new Vector2f(1.0f, -1.0f), new VertexLayersData( new VertexColoredMeshLayerVertexData( new Vector4f(1.0f, 0.0f, 0.0f, 1.0f) ) ) ),
						new ColoredMeshVertex<Vector2f>( new Vector2f(1.0f, 1.0f), new VertexLayersData( new VertexColoredMeshLayerVertexData( new Vector4f(0.0f, 0.0f, 1.0f, 1.0f) ) ) ),
						new ColoredMeshVertex<Vector2f>( new Vector2f(-1.0f, -1.0f), new VertexLayersData( new VertexColoredMeshLayerVertexData( new Vector4f(0.0f, 1.0f, 0.0f, 1.0f) ) ) )
				)), meshLayersConfig)));
				
				final MeshLayersConfigProfile meshLayersConfigProfile = new MeshLayersConfigProfile(meshLayersConfig);
				
				this.kinematicRenderPlaneObjectContainer.addObject(new KinematicMeshObject2d(mesh, meshLayersConfigProfile, new TransformedPlaneKinematicMapper<PlaneKinematicMapper, PlaneKinematicMapper>( new RotationPlaneKinematicMapper<LinearRotation>(new LinearRotation(0.0f, 0.0f, 0.5f * (float) Math.PI)), new StaticAffinePlaneKinematicMapper(Matrix3f.IDENTITY.scale(0.3f)))));
			}

			@Override
			protected void destroy_checked() {
				
			}
			
		};
	}
	
}
