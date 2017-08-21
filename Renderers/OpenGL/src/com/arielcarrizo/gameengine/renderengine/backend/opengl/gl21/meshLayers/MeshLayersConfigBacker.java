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
package com.arielcarrizo.gameengine.renderengine.backend.opengl.gl21.meshLayers;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import com.arielcarrizo.gameengine.renderengine.backend.opengl.GLException;
import com.arielcarrizo.gameengine.renderengine.backend.opengl.GlRenderEngineBackendSystem;
import com.arielcarrizo.gameengine.renderengine.backend.opengl.gl21.Gl21RenderEngineBackendObject;
import com.arielcarrizo.gameengine.renderengine.backend.opengl.gl21.Gl21RenderEngineBackendSystem;
import com.esferixis.gameengine.renderengine.backend.RenderEngineBackend;
import com.esferixis.gameengine.renderengine.backend.exception.OutOfMemoryException;
import com.esferixis.gameengine.renderengine.backend.meshLayers.MeshLayer;
import com.esferixis.gameengine.renderengine.backend.meshLayers.MeshLayersConfig;
import com.esferixis.gameengine.renderengine.backend.meshLayers.SimpleTextureLayer;
import com.esferixis.gameengine.renderengine.backend.meshLayers.UniformColoredMeshLayer;
import com.esferixis.gameengine.renderengine.backend.meshLayers.VertexColoredMeshLayer;
import com.esferixis.gameengine.renderengine.backend.texture.Texture;
import com.esferixis.gameengine.renderengine.backend.texture.TextureObject;
import com.esferixis.gameengine.renderengine.misc.colorObject.ColorObject;
import com.esferixis.gameengine.renderengine.picture.RasterPicture;
import com.esferixis.misc.counter.Counter;
import com.esferixis.misc.counter.IntCounter;
import com.esferixis.misc.counter.OverflowCounterException;

public class MeshLayersConfigBacker extends Gl21RenderEngineBackendObject {
	private final LayeredGeometryRendererSubsystem texturedGeometryRendererSubsystem;
	private final MeshLayersConfig config;
	private final List<LayersRenderingShadersUnit> shadersUnits;
	private final List<TextureObjectBacker<?, ?>> textureObjectsBackers;
	private final List<ColorObject> colorObjects;
	
	private final Counter dependencyCounter;
	
	private boolean dirtyState;
	
	/**
	 * @post Crea un "backer" de la configuración de mapeo de textura especificado con
	 * 		 el subsistema de renderización de geometría especificado y la configuración
	 * 		 especificada
	 */
	public MeshLayersConfigBacker(LayeredGeometryRendererSubsystem texturedGeometryRendererSubsystem, MeshLayersConfig config) throws GLException, OutOfMemoryException {
		super(texturedGeometryRendererSubsystem.getRenderEngineBackend());
		if ( ( texturedGeometryRendererSubsystem != null ) && ( config != null ) ) {
			this.texturedGeometryRendererSubsystem = texturedGeometryRendererSubsystem;
			
			this.config = config;
			
			this.dependencyCounter = new IntCounter();
			
			// Dividir la configuración en perfiles de shader y obtener unidades de shaders de ellos
			ArrayList<LayersRenderingShadersUnit> shadersUnits = new ArrayList<LayersRenderingShadersUnit>();
			this.shadersUnits = Collections.unmodifiableList(shadersUnits);
			
			final ArrayList< TextureObjectBacker<?, ?> > textureObjectBackers = new ArrayList< TextureObjectBacker<?, ?> >();
			final Set<TextureBacker<?>> textureBackers = new HashSet<TextureBacker<?>>();
			
			this.textureObjectsBackers = Collections.unmodifiableList(textureObjectBackers);
			
			final ArrayList< ColorObject > colorObjects = new ArrayList< ColorObject >();
			this.colorObjects = Collections.unmodifiableList(colorObjects);
			
			this.dirtyState = false;
			
			try {
				LayersRenderingShaderProfile.Essence newProfileEssence = new LayersRenderingShaderProfile.Essence();
				
				for ( MeshLayer<?> eachMeshLayer : config.getLayers() ) {
					LayersRenderingShaderProfile.Layer shaderLayer = LayersRenderingShaderProfile.Layer.getLayer(eachMeshLayer);
					
					newProfileEssence.add(shaderLayer);
					
					boolean split = (
							( newProfileEssence.getRequiredSamplers() > texturedGeometryRendererSubsystem.getMaxCombinedTextureUnits() ) ||
							( newProfileEssence.getRequiredVaryings() > texturedGeometryRendererSubsystem.getMaxVaryingFloats() ) ||
							( newProfileEssence.getRequiredVertexAttributes() > texturedGeometryRendererSubsystem.getMaxVertexAttribs() ) ||
							( newProfileEssence.getRequiredVertexUniformComponents() > texturedGeometryRendererSubsystem.getMaxVertexUniformComponents() ) ||
							( newProfileEssence.getRequiredFragmentUniformComponents() > texturedGeometryRendererSubsystem.getMaxFragmentUniformComponents() )
					);
					
					final List< TextureObject<? extends RasterPicture<?>> > newTextureObjects = new ArrayList< TextureObject<?> >();
					
					// Agregar dependencias
					eachMeshLayer.accept(new MeshLayer.Visitor<Void, RuntimeException>() {
						@Override
						public Void visit(SimpleTextureLayer<?> layer) {
							newTextureObjects.add(layer.getTextureObject());
							
							return null;
						}

						@Override
						public Void visit(VertexColoredMeshLayer layer) {
							return null;
						}

						@Override
						public Void visit(UniformColoredMeshLayer layer) {
							return null;
						}
					});
					
					// Si tiene objeto de máscara de color, lo agrega a la lista de objetos de color
					if ( eachMeshLayer.getMaskColorObject() != null ) {
						colorObjects.add(eachMeshLayer.getMaskColorObject());
					}
					
					for ( TextureObject<? extends RasterPicture<?>> eachTextureObject : newTextureObjects ) {
						final Gl21RenderEngineBackendSystem renderEngineBackend = MeshLayersConfigBacker.this.renderEngineBackend;
						final TextureBacker<?> eachTextureBacker = renderEngineBackend.getTextureBacker(eachTextureObject.getTexture());
						
						split |= ( textureBackers.contains(eachTextureBacker) );
						
						textureObjectBackers.add(new TextureObjectBacker(this, eachTextureObject, eachTextureBacker));
						textureBackers.add(eachTextureBacker);
					}
					
					if ( split ) {
						// Remover la última capa (La agregada), asociar un shader y agregarlo
						newProfileEssence.removeLast();
						shadersUnits.add( this.texturedGeometryRendererSubsystem.attachShader( new LayersRenderingShaderProfile(newProfileEssence)) );
						
						// Luego crear un perfil de shader y agregarla a la capa que no pudo ser agregada
						newProfileEssence = new LayersRenderingShaderProfile.Essence();
						newProfileEssence.add(shaderLayer);
					}
				}
				
				// Crear un perfil de shader con la última esencia de perfil de shader que quedó, y agregarlo
				shadersUnits.add( texturedGeometryRendererSubsystem.attachShader( new LayersRenderingShaderProfile(newProfileEssence)) );
			}
			catch (Exception e) {
				this.destroy();
				throw e;
			}
			
			shadersUnits.trimToSize();
			textureObjectBackers.trimToSize();
			colorObjects.trimToSize();
		}
		else {
			throw new NullPointerException();
		}
	}
	
	/**
	 * @post Devuelve la configuración
	 */
	public MeshLayersConfig getConfig() {
		return this.config;
	}
	
	/**
	 * @post Devuelve los shaders
	 */
	public List<LayersRenderingShadersUnit> getShadersUnits() {
		return this.shadersUnits;
	}
	
	/**
	 * @post Devuelve el contador de dependencias
	 */
	public Counter getDependencyCounter() {
		return this.dependencyCounter;
	}
	
	/**
	 * @post Devuelve los "backers" de objetos de textura
	 */
	public List<TextureObjectBacker<?, ?>> getTextureObjectBackers() {
		return this.textureObjectsBackers;
	}
	
	/**
	 * @post Devuelve los objetos de color
	 */
	public List<ColorObject> getColorObjects() {
		return this.colorObjects;
	}
	
	/**
	 * @post Devuelve si está "sucio"
	 */
	boolean isInDirtyState() {
		return this.dirtyState;
	}
	
	/**
	 * @post Limpia el estado "sucio"
	 */
	void clearDirtyState() {
		this.dirtyState = false;
	}
	
	/**
	 * @post Notifica que está en un estado "sucio"
	 */
	public void notifyDirtyState() {
		this.dirtyState = true;
	}
	
	/**
	 * @post Destruye el "backer"
	 */
	public void destroy() {
		for ( TextureObjectBacker<?, ?> eachTextureObjectBacker : this.textureObjectsBackers ) {
			eachTextureObjectBacker.destroy();
		}
		
		for ( LayersRenderingShadersUnit eachShader : this.shadersUnits ) {
			this.texturedGeometryRendererSubsystem.detachShader(eachShader.getShaderProfile());
		}
	}
}
