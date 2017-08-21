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
package com.esferixis.gameengine.renderengine.frontend.plane.kinematics.containers.transformer;

import com.esferixis.gameengine.physics.plane.kinematics.mapper.PlaneKinematicMapper;
import com.esferixis.gameengine.physics.plane.kinematics.mapper.TransformedPlaneKinematicMapper;
import com.esferixis.gameengine.renderengine.frontend.meshLayers.MeshLayersConfigProfile;
import com.esferixis.gameengine.renderengine.frontend.misc.mesh.Mesh;
import com.esferixis.gameengine.renderengine.frontend.misc.mesh.colored.ColoredMeshData;
import com.esferixis.gameengine.renderengine.frontend.plane.kinematics.objects.KinematicMeshObject2d;
import com.esferixis.gameengine.renderengine.frontend.plane.kinematics.objects.KinematicMeshObject2dObserver;
import com.esferixis.gameengine.renderengine.frontend.plane.kinematics.objects.KinematicRenderPlaneObject;
import com.esferixis.gameengine.renderengine.frontend.plane.kinematics.objects.KinematicRenderPlaneObjectObserver;
import com.esferixis.gameengine.renderengine.frontend.plane.kinematics.objects.KinematicShapeObject;
import com.esferixis.gameengine.renderengine.frontend.plane.kinematics.objects.KinematicShapeObjectObserver;
import com.esferixis.gameengine.renderengine.plane.shape.ColorDistribution;
import com.esferixis.geometry.plane.finite.FiniteProportionalHolomorphicShape;
import com.esferixis.math.Vector2f;

/**
 * @author ariel
 *
 */
class ObjectBacker<O extends KinematicRenderPlaneObject> {
	private final PlaneKinematicMapper transformerMapper;
	
	private final O originalObject;
	private final O transformedObject;
	private final KinematicRenderPlaneObjectObserver<O> originalObjectObserver;
	
	private ObjectBacker(PlaneKinematicMapper transformerMapper, O originalObject, O transformedObject, KinematicRenderPlaneObjectObserver<O> originalObjectObserver) {
		this.transformerMapper = transformerMapper;
		this.originalObject = originalObject;
		this.transformedObject = transformedObject;
		this.originalObjectObserver = originalObjectObserver;
	}
	
	/**
	 * @pre El objeto no puede ser nulo
	 * @post Crea el backer con el objeto y el transformador especificado
	 */
	public static <O extends KinematicRenderPlaneObject> ObjectBacker<O> create(final O originalObject, final PlaneKinematicMapper transformerMapper) {
		if ( originalObject != null ) {
			return originalObject.accept(new KinematicRenderPlaneObject.Visitor<ObjectBacker<O>, RuntimeException>() {

				@Override
				public ObjectBacker<O> visit(final KinematicMeshObject2d kinematicMeshObject2d) {
					final KinematicMeshObject2d transformedObject = new KinematicMeshObject2d();
					
					final KinematicMeshObject2dObserver observer = new KinematicMeshObject2dObserver() {
						/**
						 * 
						 */
						private static final long serialVersionUID = -7111270279793362025L;

						@Override
						protected void notifyMeshChange(Mesh<Vector2f, ColoredMeshData<Vector2f>> mesh) {
							transformedObject.setMesh(mesh);
						}

						@Override
						protected void notifyMeshLayersConfigProfileChange(
								MeshLayersConfigProfile meshLayersConfigProfile) {
							transformedObject.setMeshLayersConfigProfile(meshLayersConfigProfile);
						}

						@Override
						protected void notifyKinematicMapperChange(PlaneKinematicMapper newKinematicMapper) {
							transformedObject.setKinematicMapper(new TransformedPlaneKinematicMapper<PlaneKinematicMapper, PlaneKinematicMapper>(newKinematicMapper, transformerMapper));
						}
						
					};
					
					return (ObjectBacker<O>) new ObjectBacker<KinematicMeshObject2d>(transformerMapper, kinematicMeshObject2d, transformedObject, observer);
				}

				@Override
				public ObjectBacker<O> visit(KinematicShapeObject kinematicShapeObject) {
					final KinematicShapeObject transformedObject = new KinematicShapeObject();
					
					final KinematicShapeObjectObserver observer = new KinematicShapeObjectObserver() {

						/**
						 * 
						 */
						private static final long serialVersionUID = -5028243966160126212L;

						@Override
						protected void notifyShapeChange(FiniteProportionalHolomorphicShape<?> shape) {
							transformedObject.setShape(shape);
						}

						@Override
						protected void notifyColorDistributionChange(ColorDistribution colorDistribution) {
							transformedObject.setColorDistribution(colorDistribution);
						}

						@Override
						protected void notifyKinematicMapperChange(PlaneKinematicMapper newKinematicMapper) {
							transformedObject.setKinematicMapper(newKinematicMapper);
						}
						
					};
					
					return (ObjectBacker<O>) new ObjectBacker<KinematicShapeObject>(transformerMapper, kinematicShapeObject, transformedObject, observer);
				}
			});
		}
		else {
			throw new NullPointerException();
		}
	}
	
	/**
	 * @post Asocia el backer
	 */
	public void attach() {
		this.originalObjectObserver.update();
		this.originalObjectObserver.attach(this.originalObject);
	}
	
	/**
	 * @post Desasocia el backer
	 */
	public void detach() {
		this.originalObjectObserver.detach();
	}
	
	/**
	 * @post Devuelve el objeto original
	 */
	public O getOriginalObject() {
		return this.originalObject;
	}
	
	/**
	 * @post Devuelve el objeto transformado
	 */
	public O getTransformedObject() {
		return this.transformedObject;
	}
}
