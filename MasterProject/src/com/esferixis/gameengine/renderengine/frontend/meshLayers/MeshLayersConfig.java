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
package com.esferixis.gameengine.renderengine.frontend.meshLayers;

import java.io.IOException;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;

import com.esferixis.gameengine.renderengine.frontend.RenderEngineFrontendLoadableObject;
import com.esferixis.gameengine.renderengine.frontend.implementation.CoreFactory;
import com.esferixis.gameengine.renderengine.frontend.implementation.meshLayers.MeshLayersConfigCore;
import com.esferixis.gameengine.renderengine.frontend.texture.Texture;
import com.esferixis.gameengine.renderengine.frontend.texture.TextureObject;
import com.esferixis.gameengine.renderengine.frontend.texture.TextureObject2d;
import com.esferixis.gameengine.renderengine.frontend.texture.TextureObject2dObserver;
import com.esferixis.gameengine.renderengine.frontend.texture.TextureObject3d;
import com.esferixis.gameengine.renderengine.frontend.texture.TextureObject3dObserver;
import com.esferixis.gameengine.renderengine.frontend.texture.TextureObjectObserver;
import com.esferixis.gameengine.renderengine.misc.colorObject.ColorObject;
import com.esferixis.gameengine.renderengine.misc.colorObject.ColorObjectObserver;
import com.esferixis.gameengine.renderengine.picture.RasterPicture;
import com.esferixis.gameengine.renderengine.picture.RasterPicture2d;
import com.esferixis.gameengine.renderengine.picture.RasterPicture3d;
import com.esferixis.gameengine.renderengine.texture.CoordinateWrap;
import com.esferixis.gameengine.renderengine.texture.TextureQualitySettings;
import com.esferixis.math.Vector4f;
import com.esferixis.math.Vectorf;
import com.esferixis.misc.RuntimeId;
import com.esferixis.misc.Property.Setteable;
import com.esferixis.misc.Property.SetteableGroup;
import com.esferixis.misc.accesor.AccesorHolder;
import com.esferixis.misc.accesor.AccesorWhiteList;
import com.esferixis.misc.observer.Observer;
import com.esferixis.misc.reference.DynamicReference;
import com.sun.xml.internal.ws.org.objectweb.asm.Type;

/**
 * Configuración de textura
 * 
 * @author ariel
 *
 */
public final class MeshLayersConfig extends RenderEngineFrontendLoadableObject implements Serializable {
	/**
	 * 
	 */
	private static final long serialVersionUID = 2532027899604254387L;
	
	private final List< MeshLayer<?> > layers;
	
	private transient RuntimeId id;
	
	private int fieldCount;
	
	public static final class Accesor {
		private Accesor() {}
		
		@AccesorWhiteList
		private static final Class<?>[] packageAccesors = new Class[]{
			com.esferixis.gameengine.renderengine.frontend.misc.mesh.PackageAccesors.class,
			com.esferixis.gameengine.renderengine.frontend.PackageAccesors.class,
			com.esferixis.gameengine.renderengine.frontend.implementation.mesh.PackageAccesors.class,
			com.esferixis.gameengine.renderengine.frontend.renderingFrame.PackageAccesors.class
		};
		
		/**
		 * @post Devuelve el núcleo
		 */
		public MeshLayersConfigCore getCore(MeshLayersConfig meshLayersConfig) {
			if ( meshLayersConfig != null ) {
				return meshLayersConfig.getCore();
			}
			else {
				throw new NullPointerException();
			}
		}
	}
	
	private static final CoreFactory<MeshLayersConfig, MeshLayersConfigCore> coreFactory = new CoreFactory<MeshLayersConfig, MeshLayersConfigCore>() {

		@Override
		protected MeshLayersConfigCore createCore(MeshLayersConfig meshLayersConfig) throws RuntimeException {
			return PackageAccesors.meshLayersConfigCoreAccesor.get().create(meshLayersConfig);
		}
		
	};
	
	private transient MeshLayersConfigCore core;
		
	/**
	 * @pre El array de capas no puede ser nulo y tiene que tener por lo menos una capa
	 * @post Crea una configuración de textura con las capas
	 * 		 especificadas
	 * 
	 * 		 El orden de dibujado corresponde al orden en que
	 * 		 se suministran las capas
	 * 
	 * 		 Las instancias de capas tienen que ser nuevas, no pueden usarse unas que
	 * 		 ya fueron empleadas para crear una configuración de capa anterior
	 */
	public MeshLayersConfig(MeshLayer<?>... layers) {
		if ( layers != null ) {
			if ( layers.length > 0 ) {
				this.core = null;
				
				this.layers = Collections.unmodifiableList( Arrays.asList( layers.clone() ) );
				
				final List<MeshLayer<?>> attachedLayers = new ArrayList<MeshLayer<?>>(this.layers.size());
				
				try {
					for ( MeshLayer<?> eachLayer : this.layers ) {
						eachLayer.attachMeshLayersConfig(this);
						attachedLayers.add(eachLayer);
					}
				}
				catch ( RuntimeException e ) {
					final Set<LayerDataField<?>> fields = new HashSet<LayerDataField<?>>();
					
					for ( MeshLayer<?> eachLayer : attachedLayers ) {
						fields.addAll(eachLayer.getDataFields());
						
						eachLayer.detachMeshLayersConfig();
					}
					
					for ( LayerDataField<?> eachField : fields ) {
						eachField.detachMeshLayersConfig();
					}
					
					throw e;
				}
				
				this.addSettersToFields();
			}
			else {
				throw new IllegalArgumentException("Expected at least one layer");
			}
		}
		else {
			throw new NullPointerException();
		}
	}
	
	/**
	 * @post Devuelve el núcleo
	 */
	protected MeshLayersConfigCore getCore() {
		if ( this.core == null ) {
			this.core = coreFactory.getCore(this);
		}
		
		return this.core;
	}
	
	/**
	 * @post Devuelve el backend
	 */
	private com.esferixis.gameengine.renderengine.backend.meshLayers.MeshLayersConfig getBackend() {
		return this.getCore().getBackend();
	}
	
	/**
	 * @post Agrega los setters a los campos
	 */
	private void addSettersToFields() {
		com.esferixis.gameengine.renderengine.backend.meshLayers.MeshLayersConfig configBackend = this.getBackend();
		Iterator<com.esferixis.gameengine.renderengine.backend.meshLayers.MeshLayer<?>> layerBackends = configBackend.getLayers().iterator();
		
		final Set<LayerDataField<ColorObject>> processedColorFields = new HashSet<LayerDataField<ColorObject>>();
		final Map<TextureObjectLayerDataField<?, ?>, SetteableGroup<com.esferixis.gameengine.renderengine.frontend.texture.TextureObject<RasterPicture<?>>>> textureObjectSetteableGroupPerTextureObjectLayerDataField = new HashMap<TextureObjectLayerDataField<?, ?>, SetteableGroup<com.esferixis.gameengine.renderengine.frontend.texture.TextureObject<RasterPicture<?>>>>();
		
		final MeshLayersConfigCore core = this.getCore();
		
		for ( MeshLayer<?> eachLayer : this.layers ) {
			try {
				final com.esferixis.gameengine.renderengine.backend.meshLayers.MeshLayer<?> layerBackend = layerBackends.next();
				
				final LayerDataField<ColorObject> maskColorField = eachLayer.getMaskColorField();
				
				if ( maskColorField != null ) {
					if ( processedColorFields.add(maskColorField) ) {
						eachLayer.getMaskColorField().initSetteable(new Setteable<ColorObject>(){
							private ColorObject settedColorObject = null;
							private final ColorObjectObserver colorObjectObserver = new ColorObjectObserver(Observer.Type.STRONG) {

								@Override
								protected void notifyColorChange(Vector4f newColor) {
									layerBackend.getMaskColorObject().setColor(newColor);
								}
								
							};
							
							/**
							 * @post Asigna el color
							 */
							private void setColor(Vector4f color) {
								layerBackend.getMaskColorObject().setColor(color);
							}
							
							@Override
							public void set(ColorObject colorObject) {
								if ( this.settedColorObject != null ) {
									colorObjectObserver.detach();
								}
								
								setColor(colorObject.getColor());
								
								this.settedColorObject = colorObject;
								if ( this.settedColorObject != null ) {
									colorObjectObserver.attach(this.settedColorObject);
								}
							}
							
						});
					}
				}
				
				eachLayer.accept(
						new MeshLayer.Visitor<Void, RuntimeException>() {
							
					@Override
					public Void visit(
							final SimpleTextureLayer<?> layer) throws RuntimeException {
						final DynamicReference<RenderEngineFrontendLoadableObject> dependencyDynamicReference = new DynamicReference<RenderEngineFrontendLoadableObject>();
						
						core.addDependencyDynamicReference(dependencyDynamicReference);
						
						SetteableGroup<com.esferixis.gameengine.renderengine.frontend.texture.TextureObject<RasterPicture<? extends Vectorf>>> textureObjectSetteableGroup = textureObjectSetteableGroupPerTextureObjectLayerDataField.get(layer.getTextureObjectField());
						
						if ( textureObjectSetteableGroup == null ) {
							textureObjectSetteableGroup = new SetteableGroup<com.esferixis.gameengine.renderengine.frontend.texture.TextureObject<RasterPicture<?>>>();
							( (LayerDataField<com.esferixis.gameengine.renderengine.frontend.texture.TextureObject<RasterPicture<?>>>) layer.getTextureObjectField()).initSetteable(textureObjectSetteableGroup);
							textureObjectSetteableGroupPerTextureObjectLayerDataField.put(layer.getTextureObjectField(), textureObjectSetteableGroup);
						}
						
						textureObjectSetteableGroup.addSetter(layer.getTextureObjectField().accept(new TextureObjectLayerDataField.Visitor<Setteable<com.esferixis.gameengine.renderengine.frontend.texture.TextureObject<RasterPicture<? extends Vectorf>>>>() {

							@Override
							public Setteable<TextureObject<RasterPicture<? extends Vectorf>>> visit(
									TextureObject2dLayerDataField textureObject2dLayerDataField) {
								final com.esferixis.gameengine.renderengine.backend.texture.TextureObject2d backendTextureObject = (com.esferixis.gameengine.renderengine.backend.texture.TextureObject2d) (com.esferixis.gameengine.renderengine.backend.texture.TextureObject) ((com.esferixis.gameengine.renderengine.backend.meshLayers.SimpleTextureLayer<RasterPicture<?>>) layerBackend).getTextureObject();
								
								final Setteable<TextureObject2d> setteable = new Setteable<TextureObject2d>() {
									private TextureObject2d settedTextureObject = null;
									
									final TextureObject2dObserver textureObjectObserver = new TextureObject2dObserver(Observer.Type.STRONG) {

										@Override
										protected void notifyCoordinateWrapSChange(CoordinateWrap newCoordinateWrap) {
											backendTextureObject.setCoordinateWrapS(newCoordinateWrap);
										}

										@Override
										protected void notifyCoordinateWrapTChange(CoordinateWrap newCoordinateWrap) {
											backendTextureObject.setCoordinateWrapT(newCoordinateWrap);
										}

										@Override
										protected void notifyBorderColorChange(Vector4f borderColor) {
											backendTextureObject.setBorderColor(borderColor);
										}

										@Override
										protected void notifyTextureObjectChange(Texture<RasterPicture2d> texture) {
											backendTextureObject.setTexture(PackageAccesors.textureAccesor.get().getCore(texture).getBackend());
										}

										@Override
										protected void notifyTextureQualitySettingsChange(
												TextureQualitySettings qualitySettings) {
											backendTextureObject.setQualitySettings(qualitySettings);
										}
										
									};
									
									@Override
									public void set(TextureObject2d textureObject) {
										if ( this.settedTextureObject != null ) {
											this.textureObjectObserver.detach();
										}
										
										backendTextureObject.setCoordinateWrapS(textureObject.getCoordinateWrapS());
										backendTextureObject.setCoordinateWrapT(textureObject.getCoordinateWrapT());
										backendTextureObject.setBorderColor(textureObject.getBorderColor());
										backendTextureObject.setTexture(PackageAccesors.textureAccesor.get().getCore(textureObject.getTexture()).getBackend());
										backendTextureObject.setQualitySettings(textureObject.getQualitySettings());
										
										this.textureObjectObserver.attach(textureObject);
										
										this.settedTextureObject = textureObject;
										
										dependencyDynamicReference.set(textureObject.getTexture());
									}
									
								};
								
								return (Setteable) setteable;
							}

							@Override
							public Setteable<TextureObject<RasterPicture<? extends Vectorf>>> visit(
									TextureObject3dLayerDataField textureObject3dLayerDataField) {
								final com.esferixis.gameengine.renderengine.backend.texture.TextureObject3d backendTextureObject = (com.esferixis.gameengine.renderengine.backend.texture.TextureObject3d) (com.esferixis.gameengine.renderengine.backend.texture.TextureObject) ((com.esferixis.gameengine.renderengine.backend.meshLayers.SimpleTextureLayer<RasterPicture<?>>) layerBackend).getTextureObject();
								
								final Setteable<TextureObject3d> setteable = new Setteable<TextureObject3d>() {
									private TextureObject3d settedTextureObject = null;
									
									final TextureObject3dObserver textureObjectObserver = new TextureObject3dObserver(Observer.Type.STRONG) {

										@Override
										protected void notifyCoordinateWrapSChange(CoordinateWrap newCoordinateWrap) {
											backendTextureObject.setCoordinateWrapS(newCoordinateWrap);
										}

										@Override
										protected void notifyCoordinateWrapTChange(CoordinateWrap newCoordinateWrap) {
											backendTextureObject.setCoordinateWrapT(newCoordinateWrap);
										}
										
										@Override
										protected void notifyCoordinateWrapRChange(CoordinateWrap newCoordinateWrap) {
											backendTextureObject.setCoordinateWrapR(newCoordinateWrap);
										}

										@Override
										protected void notifyBorderColorChange(Vector4f borderColor) {
											backendTextureObject.setBorderColor(borderColor);
										}

										@Override
										protected void notifyTextureObjectChange(Texture<RasterPicture3d> texture) {
											backendTextureObject.setTexture(PackageAccesors.textureAccesor.get().getCore(texture).getBackend());
										}

										@Override
										protected void notifyTextureQualitySettingsChange(
												TextureQualitySettings qualitySettings) {
											backendTextureObject.setQualitySettings(qualitySettings);
										}
										
									};
									
									@Override
									public void set(TextureObject3d textureObject) {
										if ( this.settedTextureObject != null ) {
											this.textureObjectObserver.detach();
										}
										
										backendTextureObject.setCoordinateWrapS(textureObject.getCoordinateWrapS());
										backendTextureObject.setCoordinateWrapT(textureObject.getCoordinateWrapT());
										backendTextureObject.setCoordinateWrapR(textureObject.getCoordinateWrapR());
										backendTextureObject.setBorderColor(textureObject.getBorderColor());
										backendTextureObject.setTexture(PackageAccesors.textureAccesor.get().getCore(textureObject.getTexture()).getBackend());
										backendTextureObject.setQualitySettings(textureObject.getQualitySettings());
										
										this.textureObjectObserver.attach(textureObject);
										
										this.settedTextureObject = textureObject;
										
										dependencyDynamicReference.set(textureObject.getTexture());
									}
									
								};
								
								return (Setteable) setteable;
							}
							
						}));
						
						return null;
					}
	
					@Override
					public Void visit(
							VertexColoredMeshLayer layer) throws RuntimeException {
						return null;
					}
	
					@Override
					public Void visit(
							UniformColoredMeshLayer layer) throws RuntimeException {
						return null;
					}
				});
			} catch (RuntimeException e) {
				throw e;
			} catch (Exception e) {
				throw new RuntimeException(e);
			}
		}
	}
	
	// Para modificar el comportamiento de lectura del objeto
	private void readObject(java.io.ObjectInputStream in) throws IOException, ClassNotFoundException {
		in.defaultReadObject();
		this.addSettersToFields();
	}	
	
	/**
	 * @post Devuelve el id
	 */
	private RuntimeId getId() {
		if ( this.id == null ) {
			this.id = RuntimeId.getId(this);
		}
		
		return this.id;
	}
	
	/**
	 * @post Crea un id de miembro
	 */
	int createFieldId() {
		return (this.fieldCount++);
	}
	
	/**
	 * @post Verifica la validez de los datos de capa de vértice
	 */
	public void checkVertexLayersData(VertexLayersData vertexLayersData) {
		if ( vertexLayersData != null ) {
			if ( vertexLayersData.getLayersData().size() == this.getLayers().size() ) {
				for ( int i = 0 ; i < this.getLayers().size() ; i++ ) {
					this.getLayers().get(i).checkLayerMapping(vertexLayersData.getLayersData().get(i));
				}
			}
			else {
				throw new IllegalArgumentException("Layers mapping size mismatch with mesh layers config");
			}
		}
		else {
			throw new NullPointerException();
		}
	}
	
	/**
	 * @post Devuelve las capas (Sólo lectura)
	 */
	public List<MeshLayer<?>> getLayers() {
		return this.layers;
	}
	
	/**
	 * @post Devuelve la cantidad de miembros
	 */
	int getFieldCount() {
		return this.fieldCount;
	}

	/* (non-Javadoc)
	 * @see com.esferixis.gameengine.renderengine.frontend.RenderEngineFrontendObject#hashCode()
	 */
	@Override
	public int hashCode() {
		return this.layers.hashCode();
	}

	/* (non-Javadoc)
	 * @see com.esferixis.gameengine.renderengine.frontend.RenderEngineFrontendObject#equals(java.lang.Object)
	 */
	@Override
	public boolean equals(Object other) {
		if ( other == this ) {
			return true;
		}
		else if ( ( other != null ) && ( other instanceof MeshLayersConfig ) ) {
			final MeshLayersConfig otherMeshLayersConfig = ( MeshLayersConfig ) other;
			
			if ( ( this.getId() == null ) || ( otherMeshLayersConfig.getId() == null ) ) {
				if ( otherMeshLayersConfig.layers.size() == this.layers.size() ) {
					final Iterator< MeshLayer<?> > thisLayersIterator = this.layers.iterator();
					final Iterator< MeshLayer<?> > otherLayersIterator = otherMeshLayersConfig.layers.iterator();
					
					boolean isEquals = true;
					
					while ( thisLayersIterator.hasNext() && isEquals ) {
						isEquals = thisLayersIterator.next().fuzzyEquals(otherLayersIterator.next());
					}
					
					return isEquals;
				}
				else {
					return false;
				}
			}
			else {
				return otherMeshLayersConfig.getId().equals(this.getId());
			}
		}
		else {
			return false;
		}
	}

	/* (non-Javadoc)
	 * @see com.esferixis.gameengine.renderengine.frontend.RenderEngineFrontendObject#accept(com.esferixis.gameengine.renderengine.frontend.RenderEngineFrontendObject.Visitor)
	 */
	@Override
	public <R, E extends Throwable> R accept(Visitor<R, E> visitor) throws E {
		return visitor.visit(this);
	}
}
