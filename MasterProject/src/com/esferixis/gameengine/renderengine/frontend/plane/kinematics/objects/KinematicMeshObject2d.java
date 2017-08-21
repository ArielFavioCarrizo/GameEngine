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
package com.esferixis.gameengine.renderengine.frontend.plane.kinematics.objects;

import com.esferixis.gameengine.physics.plane.kinematics.mapper.PlaneKinematicMapper;
import com.esferixis.gameengine.physics.plane.statics.PlaneMapper;
import com.esferixis.gameengine.renderengine.frontend.meshLayers.MeshLayersConfigProfile;
import com.esferixis.gameengine.renderengine.frontend.misc.mesh.Mesh;
import com.esferixis.gameengine.renderengine.frontend.misc.mesh.MeshTriangle;
import com.esferixis.gameengine.renderengine.frontend.misc.mesh.colored.ColoredMeshData;
import com.esferixis.gameengine.renderengine.frontend.misc.mesh.colored.ColoredMeshVertex;
import com.esferixis.gameengine.renderengine.frontend.plane.staticstage.StaticPlaneObjectRenderer;
import com.esferixis.gameengine.renderengine.frontend.plane.staticstage.mesh.StaticMeshObject2d;
import com.esferixis.geometry.plane.finite.ConvexPolygon;
import com.esferixis.geometry.plane.finite.FiniteAffineHolomorphicShape;
import com.esferixis.geometry.plane.finite.FiniteProportionalHolomorphicShape;
import com.esferixis.geometry.plane.finite.FiniteProportionalHolomorphicShapeGroup;
import com.esferixis.math.Vector2f;
import com.esferixis.misc.ElementCallback;
import com.esferixis.misc.loader.DataLoadingErrorException;
import com.esferixis.misc.observer.ObserverManager;

/**
 * @author ariel
 *
 */
public final class KinematicMeshObject2d extends KinematicRenderPlaneObject {
	/**
	 * 
	 */
	private static final long serialVersionUID = 3831456056996527781L;
	
	final ObserverManager<KinematicMeshObject2d, KinematicMeshObject2dObserver> observerManager = new ObserverManager<>(this, KinematicMeshObject2dObserver.class);
	
	private Mesh<Vector2f, ColoredMeshData<Vector2f>> mesh;
	private MeshLayersConfigProfile meshLayersConfigProfile;
	
	/**
	 * @post Crea un objeto de malla cinemático sin especificar nada
	 */
	public KinematicMeshObject2d() {
		this(null, null, null);
	}
	
	/**
	 * @post Crea un objeto de malla cinemático, con la malla,
	 * 		 el perfil de configuración de capas, y el mapeador
	 * 		 cinemático especificados
	 */
	public KinematicMeshObject2d(Mesh<Vector2f, ColoredMeshData<Vector2f>> mesh, MeshLayersConfigProfile meshLayersConfigProfile, PlaneKinematicMapper kinematicMapper) {
		super(kinematicMapper);
		this.mesh = mesh;
		this.meshLayersConfigProfile = meshLayersConfigProfile;
	}

	/* (non-Javadoc)
	 * @see com.esferixis.gameengine.renderengine.frontend.plane.kinematics.KinematicRenderPlaneObject#notifyObservers(com.esferixis.misc.ElementCallBack)
	 */
	@Override
	protected void notifyObservers(ElementCallback<? super KinematicRenderPlaneObjectObserver<?>> elementCallBack) {
		this.observerManager.notifyObservers(elementCallBack);
	}
	
	/**
	 * @post Devuelve la malla
	 */
	public Mesh<Vector2f, ColoredMeshData<Vector2f>> getMesh() {
		return this.mesh;
	}
	
	/**
	 * @post Especifica la malla
	 */
	public void setMesh(final Mesh<Vector2f, ColoredMeshData<Vector2f>> mesh) {
		this.observerManager.notifyObservers(new ElementCallback<KinematicMeshObject2dObserver>() {

			@Override
			public void run(KinematicMeshObject2dObserver observer) {
				observer.notifyMeshChange(mesh);
			}
			
		});
		this.mesh = mesh;
	}
	
	/**
	 * @post Devuelve el perfil de configuración de capas
	 */
	public MeshLayersConfigProfile getMeshLayersConfigProfile() {
		return this.meshLayersConfigProfile;
	}
	
	/**
	 * @post Especifica el perfil de configuración de capas
	 */
	public void setMeshLayersConfigProfile(final MeshLayersConfigProfile meshLayersConfigProfile) {
		this.observerManager.notifyObservers(new ElementCallback<KinematicMeshObject2dObserver>() {

			@Override
			public void run(KinematicMeshObject2dObserver observer) {
				observer.notifyMeshLayersConfigProfileChange(meshLayersConfigProfile);
			}
			
		});
		this.meshLayersConfigProfile = meshLayersConfigProfile;
	}

	/* (non-Javadoc)
	 * @see com.esferixis.gameengine.renderengine.frontend.plane.kinematics.objects.KinematicRenderPlaneObject#render_instant(com.esferixis.gameengine.renderengine.frontend.plane.staticstage.StaticPlaneObjectRenderer, com.esferixis.gameengine.physics.plane.statics.PlaneMapper, float)
	 */
	@Override
	protected void render_internal(StaticPlaneObjectRenderer objectRenderer, PlaneMapper instantPlaneMapper,
			float time) {
		if ( ( this.mesh != null ) && ( this.meshLayersConfigProfile != null ) ) {
			objectRenderer.render(new StaticMeshObject2d(this.mesh, this.meshLayersConfigProfile, instantPlaneMapper));
		}
	}

	/* (non-Javadoc)
	 * @see com.arielcarrizo.gameengine.renderengine.frontend.plane.kinematics.objects.KinematicRenderPlaneObject#loadBoundingShape()
	 */
	@Override
	protected FiniteAffineHolomorphicShape<?> loadBoundingShape() {
		ColoredMeshData<Vector2f> meshData;
		try {
			meshData = this.mesh.getDataLoader().get();
		} catch (DataLoadingErrorException e) {
			throw new RuntimeException(e);
		}
		
		final FiniteAffineHolomorphicShape<ConvexPolygon>[] resultShapes = new FiniteAffineHolomorphicShape[meshData.getTriangles().size()];
		
		int i = 0;
		for ( MeshTriangle<Vector2f, ColoredMeshVertex<Vector2f>> eachTriangle : meshData.getTriangles() ) {
			resultShapes[i++] = new ConvexPolygon(eachTriangle.getPoint1().getPosition(), eachTriangle.getPoint2().getPosition(), eachTriangle.getPoint3().getPosition()).castToAffine();
		}
		
		return FiniteProportionalHolomorphicShapeGroup.castToAffine(new FiniteProportionalHolomorphicShapeGroup<FiniteAffineHolomorphicShape<? extends ConvexPolygon>>(resultShapes));
	}

	/* (non-Javadoc)
	 * @see com.arielcarrizo.gameengine.renderengine.frontend.plane.kinematics.objects.KinematicRenderPlaneObject#accept(com.arielcarrizo.gameengine.renderengine.frontend.plane.kinematics.objects.KinematicRenderPlaneObject.Visitor)
	 */
	@Override
	public <R, T extends Throwable> R accept(Visitor<R, T> visitor) throws T {
		return visitor.visit(this);
	}
}
