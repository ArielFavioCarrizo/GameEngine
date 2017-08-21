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
package com.arielcarrizo.gameengine.renderengine.backend.opengl.gl21;

import java.io.IOException;
import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.lang.reflect.Proxy;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.Set;
import java.util.logging.Logger;

import com.arielcarrizo.gameengine.renderengine.backend.opengl.GLException;
import com.arielcarrizo.gameengine.renderengine.backend.opengl.GlRenderEngineBackendSystem;
import com.arielcarrizo.gameengine.renderengine.backend.opengl.gl21.attributestream.AttributeStreamManager;
import com.arielcarrizo.gameengine.renderengine.backend.opengl.gl21.errorchecker.GLErrorChecker;
import com.arielcarrizo.gameengine.renderengine.backend.opengl.gl21.globjects.shader.GlUtils;
import com.arielcarrizo.gameengine.renderengine.backend.opengl.gl21.globjects.shader.ShaderProgramContainer;
import com.arielcarrizo.gameengine.renderengine.backend.opengl.gl21.mesh.ColoredMeshBacker;
import com.arielcarrizo.gameengine.renderengine.backend.opengl.gl21.mesh.MeshBacker;
import com.arielcarrizo.gameengine.renderengine.backend.opengl.gl21.meshLayers.LayeredGeometryRendererSubsystem;
import com.arielcarrizo.gameengine.renderengine.backend.opengl.gl21.meshLayers.LayersVertexDataBacker;
import com.arielcarrizo.gameengine.renderengine.backend.opengl.gl21.meshLayers.MeshLayersConfigBacker;
import com.arielcarrizo.gameengine.renderengine.backend.opengl.gl21.meshLayers.TextureBacker;
import com.arielcarrizo.gameengine.renderengine.backend.opengl.gl21.plane.GlPlaneObjectRenderer;
import com.arielcarrizo.gameengine.renderengine.backend.opengl.gl21.plane.PlaneRenderingSubsystem;
import com.arielcarrizo.gameengine.renderengine.backend.opengl.gl21.renderingFrame.ScreenRenderingFrame;
import com.arielcarrizo.gameengine.renderengine.backend.opengl.gl21.space.Layered3dGeometryRenderer;
import com.arielcarrizo.gameengine.renderengine.backend.opengl.gl21.universe.GL3dGeometryRenderer;
import com.esferixis.gameengine.platform.PlatformServiceManagerException;
import com.esferixis.gameengine.platform.display.DisplayManager;
import com.esferixis.gameengine.renderengine.backend.RenderEngineBackend;
import com.esferixis.gameengine.renderengine.backend.exception.LoadedMeshLoadAttemptException;
import com.esferixis.gameengine.renderengine.backend.exception.LoadedTextureLoadAttemptException;
import com.esferixis.gameengine.renderengine.backend.exception.LoadedTextureMappingConfigLoadAttempt;
import com.esferixis.gameengine.renderengine.backend.exception.MeshDependencyException;
import com.esferixis.gameengine.renderengine.backend.exception.MeshLayersConfigDependencyException;
import com.esferixis.gameengine.renderengine.backend.exception.MissingMeshLoadException;
import com.esferixis.gameengine.renderengine.backend.exception.MissingTextureLoadException;
import com.esferixis.gameengine.renderengine.backend.exception.MissingTextureMappingConfigLoadException;
import com.esferixis.gameengine.renderengine.backend.exception.OutOfMemoryException;
import com.esferixis.gameengine.renderengine.backend.meshLayers.MeshLayersConfig;
import com.esferixis.gameengine.renderengine.backend.misc.mesh.Mesh;
import com.esferixis.gameengine.renderengine.backend.misc.mesh.MeshTriangleVertex;
import com.esferixis.gameengine.renderengine.backend.misc.mesh.Mesh.Data;
import com.esferixis.gameengine.renderengine.backend.misc.mesh.colored.ColoredMeshData;
import com.esferixis.gameengine.renderengine.backend.misc.mesh.colored.ColoredMeshVertex;
import com.esferixis.gameengine.renderengine.backend.plane.PlaneRendererEmmiter;
import com.esferixis.gameengine.renderengine.backend.renderingFrame.RenderingFrame;
import com.esferixis.gameengine.renderengine.backend.renderingFrame.RenderingFrameEmmiter;
import com.esferixis.gameengine.renderengine.backend.renderingFrame.RenderingFrameRenderer;
import com.esferixis.gameengine.renderengine.backend.space.universe.SpaceGeometryRendererEmmiter;
import com.esferixis.gameengine.renderengine.backend.space.universe.SpaceUniverseRendererEmmiter;
import com.esferixis.gameengine.renderengine.backend.texture.Texture;
import com.esferixis.gameengine.renderengine.picture.RasterPicture;
import com.esferixis.gameengine.renderengine.space.camera.Camera3d;
import com.esferixis.math.Matrix4f;
import com.esferixis.math.Vector3f;
import com.esferixis.math.Vector4f;
import com.esferixis.math.Vectorf;
import com.esferixis.math.intervalarithmetic.FloatClosedInterval;
import com.esferixis.misc.ElementProcessor;
import com.esferixis.misc.dynamicFields.DynamicField;
import com.esferixis.misc.exception.NotImplementedException;
import com.esferixis.misc.loader.DataLoadingErrorException;
import com.esferixis.misc.reference.InmutableReference;

public final class Gl21RenderEngineBackendSystem extends GlRenderEngineBackendSystem {
	private final static Logger LOGGER = Logger.getLogger("GLRenderEngineBackend");
	
	private final GL21 gl;
	private final GlUtils glUtils;
	
	private final DisplayManager displayManager;
	
	private final LayeredGeometryRendererSubsystem layeredGeometryRenderererSubsystem;
	private final PlaneRenderingSubsystem planeRenderingSubsystem;
	private final AttributeStreamManager attributeStreamManager;
	
	private static final DynamicField<MeshBacker<?, ?, ?>> meshBackerField = new DynamicField<MeshBacker<?, ?, ?>>();
	private static final DynamicField<TextureBacker<? extends RasterPicture<?>>> textureBackerField = new DynamicField<TextureBacker<? extends RasterPicture<?>>>();
	private static final DynamicField<MeshLayersConfigBacker> meshLayersConfigBackerField = new DynamicField<MeshLayersConfigBacker>();
	
	private final RenderEngineBackend renderEngineBackend;
	
	private final ShaderProgramContainer.Switcher shaderProgramContainerSwitcher;
	
	private Set<Mesh<?, ?>> loadedMeshes;
	private Set<MeshLayersConfig> loadedMeshLayersConfigs;
	private Set<Texture<?>> loadedTextures;
	
	private static final boolean debug = false;
	
	/**
	 * @pre La implementación de OpenGL y el administrador
	 * 		de pantalla no pueden ser nulos
	 * @post Crea el engine de rendering con la implementación de
	 * 		 OpenGL y el administrador de pantalla especificados
	 */
	public Gl21RenderEngineBackendSystem(GL21 gl, DisplayManager displayManager) throws PlatformServiceManagerException {
		if ( gl != null ) {
			if ( debug ) {
				final GL21 originalGL = gl;
				final GLErrorChecker errorChecker = new GLErrorChecker(gl);
				
				gl = (GL21) Proxy.newProxyInstance(gl.getClass().getClassLoader(), new Class[] { GL21.class },
					new InvocationHandler() {

						@Override
						public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {
							Object result = method.invoke(originalGL, args);
							errorChecker.checkGLError();
							return result;
						}
					
					}
				);
				
				LOGGER.warning("Using checked GL proxy. Performance penality.");
			}
			
			this.gl = gl;
			this.glUtils = new GlUtils(this.gl);
			this.displayManager = displayManager;
			
			try {				
				this.shaderProgramContainerSwitcher = new ShaderProgramContainer.Switcher(this.gl);
				
				this.attributeStreamManager = new AttributeStreamManager(this.gl, 1024);
				
				this.layeredGeometryRenderererSubsystem = new LayeredGeometryRendererSubsystem(this);
				this.planeRenderingSubsystem = new PlaneRenderingSubsystem(this);
				
				this.gl.glBlendFunc(GL21.GL_SRC_ALPHA, GL21.GL_ONE_MINUS_SRC_ALPHA);
				
				this.loadedMeshes = new HashSet<Mesh<?, ?>>();
				this.loadedMeshLayersConfigs = new HashSet<MeshLayersConfig>();
				this.loadedTextures = new HashSet<Texture<?>>();
				
				this.renderEngineBackend = new RenderEngineBackend(new ScreenRenderingFrame(Gl21RenderEngineBackendSystem.this)) {
					/* (non-Javadoc)
					 * @see com.esferixis.gameengine.renderengine.backend.RenderEngineBackend#load(com.esferixis.gameengine.renderengine.backend.mesh.Mesh)
					 */
					@Override
					public  <V extends Vectorf> void load(Mesh<V, ? extends Mesh.Data<V, ? extends MeshTriangleVertex<V>>> mesh) throws NullPointerException, DataLoadingErrorException, PlatformServiceManagerException, MissingTextureMappingConfigLoadException, LoadedMeshLoadAttemptException, OutOfMemoryException {
						if ( mesh != null ) {
							if ( !this.isLoaded(mesh) ) {
								try {
									final Mesh.Data<V, ? extends MeshTriangleVertex<V>> meshData = mesh.getData();
									mesh.dynamicFieldsContainer().add(meshBackerField, 
									meshData.accept(new Mesh.Data.Visitor<V, MeshBacker<V, ?, ?>, Exception >() {
	
										@Override
										public MeshBacker<V, ?, ?> visit(ColoredMeshData<V> coloredMeshData) throws Exception {
											return new ColoredMeshBacker<V>(Gl21RenderEngineBackendSystem.this, coloredMeshData);
										}
									}) );
								} catch ( RuntimeException | DataLoadingErrorException | PlatformServiceManagerException e) {
									throw e;
								} catch ( GLException e ) {
									throw new PlatformServiceManagerException(e);
								} catch ( Exception e ) {
									throw new RuntimeException("Unexpected exception", e);
								}
								
								Gl21RenderEngineBackendSystem.this.loadedMeshes.add(mesh);
							}
							else {
								throw new LoadedMeshLoadAttemptException();
							}
						}
						else {
							throw new NullPointerException();
						}
					}
					
					/* (non-Javadoc)
					 * @see com.esferixis.gameengine.renderengine.backend.RenderEngineBackend#unload(com.esferixis.gameengine.renderengine.backend.mesh.Mesh)
					 */
					@Override
					public <V extends Vectorf> void unload(Mesh<V, ? extends Mesh.Data<V, ? extends MeshTriangleVertex<V>>> mesh) throws NullPointerException, MissingMeshLoadException {
						if ( mesh != null ) {
							Gl21RenderEngineBackendSystem.this.getMeshBacker(mesh).destroy();
							mesh.dynamicFieldsContainer().remove(meshBackerField);
							
							Gl21RenderEngineBackendSystem.this.loadedMeshes.remove(mesh);
						}
						else {
							throw new NullPointerException();
						}
					}
					
					/* (non-Javadoc)
					 * @see com.esferixis.gameengine.renderengine.backend.RenderEngineBackend#load(com.esferixis.gameengine.renderengine.texture.Texture)
					 */
					@Override
					public <V extends Vectorf, P extends RasterPicture<V>> void load(Texture<P> texture) throws NullPointerException, DataLoadingErrorException, PlatformServiceManagerException, LoadedTextureLoadAttemptException {
						if ( texture != null ) {
							if ( !this.isLoaded(texture) ) {
								
								final TextureBacker<P> textureBacker = Gl21RenderEngineBackendSystem.this.getTextureBacker(texture);
								
								try {
									textureBacker.load();
								} catch ( GLException e ) {
									throw new PlatformServiceManagerException(e);
								}
									
								Gl21RenderEngineBackendSystem.this.attachTextureBacker(textureBacker);
								
								Gl21RenderEngineBackendSystem.this.loadedTextures.add(texture);
							}
							else {
								throw new LoadedTextureLoadAttemptException();
							}
						}
						else {
							throw new NullPointerException();
						}
					}
					
					/* (non-Javadoc)
					 * @see com.esferixis.gameengine.renderengine.backend.RenderEngineBackend#unload_internal(com.esferixis.gameengine.renderengine.texture.Texture)
					 */
					@Override
					protected <V extends Vectorf, P extends RasterPicture<V>> void unload_internal(Texture<P> texture) throws NullPointerException, MissingTextureLoadException, MeshLayersConfigDependencyException {
						TextureBacker<?> textureBacker = Gl21RenderEngineBackendSystem.this.getTextureBacker(texture);
						
						if ( textureBacker.isLoaded() ) {
							textureBacker.unload();
							
							if ( !textureBacker.hasDependencies() ) {
								Gl21RenderEngineBackendSystem.this.removeTextureBacker(texture);
							}
							
							Gl21RenderEngineBackendSystem.this.loadedTextures.remove(texture);
						}
						else {
							throw new MissingTextureLoadException();
						}
					}
	
					/* (non-Javadoc)
					 * @see com.esferixis.gameengine.renderengine.backend.RenderEngineBackend#load(com.esferixis.gameengine.renderengine.backend.textureMapping.TextureMappingConfig)
					 */
					@Override
					public void load(MeshLayersConfig meshLayersConfig) throws NullPointerException, PlatformServiceManagerException, MissingTextureLoadException, LoadedTextureMappingConfigLoadAttempt {
						if ( meshLayersConfig != null ) {
							if ( !this.isLoaded(meshLayersConfig) ) {
								MeshLayersConfigBacker meshLayersConfigBacker;
								try {
									meshLayersConfigBacker = new MeshLayersConfigBacker(Gl21RenderEngineBackendSystem.this.layeredGeometryRenderererSubsystem, meshLayersConfig);
								} catch (GLException e) {
									throw new PlatformServiceManagerException(e);
								}
								
								meshLayersConfig.dynamicFieldsContainer().add(meshLayersConfigBackerField, meshLayersConfigBacker);
								
								Gl21RenderEngineBackendSystem.this.loadedMeshLayersConfigs.add(meshLayersConfig);
							}
							else {
								throw new LoadedTextureMappingConfigLoadAttempt();
							}
						}
						else {
							throw new NullPointerException();
						}
					}
	
					/* (non-Javadoc)
					 * @see com.esferixis.gameengine.renderengine.backend.RenderEngineBackend#unload(com.esferixis.gameengine.renderengine.backend.textureMapping.TextureMappingConfig)
					 */
					@Override
					public void unload(MeshLayersConfig meshLayersConfig) throws NullPointerException, MeshDependencyException, MissingTextureMappingConfigLoadException {
						if ( meshLayersConfig != null ) {
							final MeshLayersConfigBacker textureMappingConfigBacker = Gl21RenderEngineBackendSystem.this.getMeshLayersConfigBacker(meshLayersConfig);
							
							if ( textureMappingConfigBacker.getDependencyCounter().isInZero() ) {
								textureMappingConfigBacker.destroy();
								meshLayersConfig.dynamicFieldsContainer().remove(meshLayersConfigBackerField);
								
								Gl21RenderEngineBackendSystem.this.loadedMeshLayersConfigs.remove(meshLayersConfig);
							}
							else {
								throw new MeshDependencyException();
							}
						}
						else {
							throw new NullPointerException();
						}
					}

					@Override
					public <V extends Vectorf> boolean isLoaded(
							Mesh<V, ? extends Data<V, ? extends MeshTriangleVertex<V>>> mesh)
							throws NullPointerException {
						if ( mesh != null ) {
							return ( mesh.dynamicFieldsContainer().get(meshBackerField) != null );
						}
						else {
							throw new NullPointerException();
						}
					}

					@Override
					public <V extends Vectorf, P extends RasterPicture<V>> boolean isLoaded(Texture<P> texture) {
						if ( texture != null ) {
							final InmutableReference<TextureBacker<P>> textureBackerReference = (InmutableReference) texture.dynamicFieldsContainer().get(textureBackerField);
							
							return ( ( textureBackerReference != null ) && textureBackerReference.get().isLoaded() );
						}
						else {
							throw new NullPointerException();
						}
					}

					@Override
					public boolean isLoaded(MeshLayersConfig meshLayersConfig) throws NullPointerException {
						if ( meshLayersConfig != null ) {
							return ( meshLayersConfig.dynamicFieldsContainer().get(meshLayersConfigBackerField) != null );
						}
						else {
							throw new NullPointerException();
						}
					}
				};
			}
			catch (GLException e) {
				this.destroy();
				throw new PlatformServiceManagerException(e);
			}
			catch (RuntimeException e) {
				this.destroy();
				throw e;
			}
			
			try {
				this.renderEngineBackend.load(Texture.BLANKTEXTURE2D);
			} catch (RuntimeException e) {
				throw e;
			} catch (Exception e) {
				throw new RuntimeException(e);
			}
			
			try {
				this.renderEngineBackend.load(Texture.BLANKTEXTURE3D);
			} catch (RuntimeException e) {
				this.renderEngineBackend.unload(Texture.BLANKTEXTURE2D);
				throw e;
			} catch (Exception e) {
				throw new RuntimeException(e);
			}
		}
		else {
			throw new NullPointerException();
		}
	}
	
	public <V extends Vectorf, P extends RasterPicture<V>> void removeTextureBacker(Texture<P> texture) throws NullPointerException, MissingTextureLoadException, MeshLayersConfigDependencyException {
		if ( texture != null ) {
			texture.dynamicFieldsContainer().remove(textureBackerField);
		}
		else {
			throw new NullPointerException();
		}
	}
	
	/**
	 * @post Devuelve el administrador de pantalla
	 */
	public DisplayManager getDisplayManager() {
		return Gl21RenderEngineBackendSystem.this.displayManager;
	}
	
	/**
	 * @post Devuelve las funciones útiles de OpenGL
	 */
	public GlUtils getGLUtils() {
		return this.glUtils;
	}

	/**
	 * @post Devuelve la implementación de OpenGL asociada
	 */
	public GL21 getGL() {
		return this.gl;
	}
	
	/**
	 * @post Devuelve el switcher de contenedores de programas de shader
	 */
	public ShaderProgramContainer.Switcher getShaderProgramContainerSwitcher() {
		return this.shaderProgramContainerSwitcher;
	}
	
	/**
	 * @post Devuelve el administrador de streams de atributos
	 */
	public AttributeStreamManager getAttributeStreamManager() {
		return this.attributeStreamManager;
	}

	/* (non-Javadoc)
	 * @see com.esferixis.gameengine.renderengine.opengl.GenericGlRenderEngine#destroy()
	 */
	@Override
	public void destroy() {
		for ( Mesh<?, ?> eachMesh : new ArrayList<Mesh<?, ?>>(this.loadedMeshes) ) {
			this.renderEngineBackend.unload((Mesh) eachMesh);
		}
		
		for ( MeshLayersConfig eachMeshLayersConfig : new ArrayList<MeshLayersConfig>(this.loadedMeshLayersConfigs) ) {
			this.renderEngineBackend.unload(eachMeshLayersConfig);
		}
		
		for ( Texture<?> eachTexture : new ArrayList<Texture<?>>(this.loadedTextures) ) {
			this.removeTextureBacker( (Texture) eachTexture);
		}
		
		if ( this.shaderProgramContainerSwitcher != null ) this.shaderProgramContainerSwitcher.invalidateSelection();
		this.gl.glUseProgram(0);
		if ( this.planeRenderingSubsystem != null) this.planeRenderingSubsystem.destroy();
		if ( this.layeredGeometryRenderererSubsystem != null ) this.layeredGeometryRenderererSubsystem.destroy();
	}
	
	/**
	 * @pre La malla tiene que estar cargada
	 * @post Devuelve el "backer" de la malla especificada
	 */
	public <V extends Vectorf> MeshBacker<V, ?, ?> getMeshBacker(Mesh<V, ?> mesh) throws NullPointerException, MissingMeshLoadException {
		if ( mesh != null ) {
			final InmutableReference< MeshBacker<V, ?, ?> > meshBackerReference = (InmutableReference<MeshBacker<V, ?, ?>>) (InmutableReference) mesh.dynamicFieldsContainer().get(meshBackerField);
			if ( meshBackerReference != null ) {
				return (MeshBacker<V, ?, ?>) meshBackerReference.get();
			}
			else {
				throw new MissingMeshLoadException();
			}
		}
		else {
			throw new NullPointerException();
		}
	}
	
	/**
	 * @post Devuelve el "backer" de la textura
	 * @param texture
	 * @throws NullPointerException
	 * @throws IllegalArgumentException
	 */
	public <V extends Vectorf, P extends RasterPicture<V>> TextureBacker<P> getTextureBacker(Texture<P> texture) throws NullPointerException, MissingTextureLoadException {
		if ( texture != null ) {
			final TextureBacker<P> textureBacker;
			
			final InmutableReference< TextureBacker<? extends RasterPicture<?>> > textureBackerReference = texture.dynamicFieldsContainer().get(textureBackerField);
			if ( textureBackerReference != null ) {
				textureBacker = (TextureBacker<P>) textureBackerReference.get();
			}
			else {
				textureBacker = TextureBacker.create(Gl21RenderEngineBackendSystem.this.layeredGeometryRenderererSubsystem, texture);
			}
			
			return textureBacker;
		}
		else {
			throw new NullPointerException();
		}
	}
	
	/**
	 * @post Asocia la textura con su backer
	 */
	public <P extends RasterPicture<?>> void attachTextureBacker(TextureBacker<P> textureBacker) {
		InmutableReference<TextureBacker<P>> textureBackerReference = (InmutableReference) textureBacker.getTexture().dynamicFieldsContainer().get(textureBackerField);
		if ( textureBackerReference == null ) {
			textureBacker.getTexture().dynamicFieldsContainer().add(textureBackerField, textureBacker);
		}
		else if ( textureBackerReference.get() != textureBacker ) {
			throw new IllegalStateException("Expected non attached texture backer");
		}
	}
	
	/**
	 * @post Devuelve el "backer" de la configuración de capas de malla
	 * @param texture
	 * @throws NullPointerException
	 * @throws IllegalArgumentException
	 */
	public MeshLayersConfigBacker getMeshLayersConfigBacker(MeshLayersConfig meshLayersConfig) throws NullPointerException, MissingTextureMappingConfigLoadException {
		if ( meshLayersConfig != null ) {
			final InmutableReference< MeshLayersConfigBacker > meshLayersConfigBackerReference = meshLayersConfig.dynamicFieldsContainer().get(meshLayersConfigBackerField);
			if ( meshLayersConfigBackerReference != null ) {
				return meshLayersConfigBackerReference.get();
			}
			else {
				throw new MissingTextureMappingConfigLoadException();
			}
		}
		else {
			throw new NullPointerException();
		}
	}
	
	/**
	 * @post Devuelve el subsistema de renderizado de geometría en capas
	 */
	public LayeredGeometryRendererSubsystem getLayeredGeometryRendererSubsystem() {
		return this.layeredGeometryRenderererSubsystem;
	}
	
	/**
	 * @post Devuelve el susbsistema de renderizado en el plano
	 */
	public PlaneRenderingSubsystem getPlaneRenderingSubsystem() {
		return this.planeRenderingSubsystem;
	}

	/* (non-Javadoc)
	 * @see com.esferixis.gameengine.renderengine.backend.opengl.GlRenderEngineBackendSystem#getRenderEngineBackend()
	 */
	@Override
	public RenderEngineBackend getRenderEngineBackend() {
		return this.renderEngineBackend;
	}
}
