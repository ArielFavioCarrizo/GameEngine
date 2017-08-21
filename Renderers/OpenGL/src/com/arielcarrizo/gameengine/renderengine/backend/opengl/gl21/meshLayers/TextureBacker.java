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

import java.nio.ByteBuffer;
import java.util.Arrays;
import java.util.Collections;
import java.util.EnumMap;
import java.util.List;
import java.util.Map;

import com.arielcarrizo.gameengine.renderengine.backend.opengl.GLException;
import com.arielcarrizo.gameengine.renderengine.backend.opengl.gl21.GL21;
import com.arielcarrizo.gameengine.renderengine.backend.opengl.gl21.GLObject;
import com.arielcarrizo.gameengine.renderengine.backend.opengl.gl21.errorchecker.GLErrorChecker;
import com.arielcarrizo.gameengine.renderengine.backend.opengl.gl21.errorchecker.GLErrorException;
import com.esferixis.gameengine.renderengine.backend.texture.Texture;
import com.esferixis.gameengine.renderengine.backend.texture.TextureObject;
import com.esferixis.gameengine.renderengine.picture.RasterPicture;
import com.esferixis.gameengine.renderengine.picture.RasterPicture2d;
import com.esferixis.gameengine.renderengine.picture.RasterPicture3d;
import com.esferixis.misc.counter.Counter;
import com.esferixis.misc.counter.IntCounter;
import com.esferixis.misc.loader.DataLoadingErrorException;

public final class TextureBacker<P extends RasterPicture<?>> extends GLObject {
	private final Texture<P> texture;
	
	private int glTextureObject;
	
	private List<Integer> pictureDimensions;
	
	private final LayeredGeometryRendererSubsystem layeredGeometryRendererSubsystem;
	private final TextureLinkedAllocatableElement textureLinkedAllocatableElement;
	
	private int glTextureType;
	
	private TextureObject<P> cachedTextureObject;
	
	private int dependencyCount;
	
	private MeshLayersConfigBacker lastMeshLayersConfigBacker;
	
	private boolean isLoaded;
	
	private static final Map<RasterPicture.PixelFormat, Integer> glPixelFormatByPixelFormat;
	static {
		Map<RasterPicture.PixelFormat, Integer> mapping = new EnumMap<RasterPicture.PixelFormat, Integer>(RasterPicture.PixelFormat.class);
		mapping.put(RasterPicture.PixelFormat.SCALAR, GL21.GL_RED);
		mapping.put(RasterPicture.PixelFormat.VERTEX2, GL21.GL_RG);
		mapping.put(RasterPicture.PixelFormat.VERTEX3, GL21.GL_RGB);
		mapping.put(RasterPicture.PixelFormat.VERTEX4, GL21.GL_RGBA);
		
		glPixelFormatByPixelFormat = Collections.unmodifiableMap(mapping);
	}
	
	private static final Map<RasterPicture.ComponentFormat, Integer> glTypeByComponentFormat;
	static {
		Map<RasterPicture.ComponentFormat, Integer> mapping = new EnumMap<RasterPicture.ComponentFormat, Integer>(RasterPicture.ComponentFormat.class);
		mapping.put(RasterPicture.ComponentFormat.BYTE, GL21.GL_UNSIGNED_BYTE);
		mapping.put(RasterPicture.ComponentFormat.FLOAT, GL21.GL_FLOAT);
		
		glTypeByComponentFormat = Collections.unmodifiableMap(mapping);
	}
	
	/**
	 * @pre La textura no puede ser nula
	 * @post Crea el "backer" de la textura especificada, con el subsistema
	 * 		 de renderizado de texturas especificado
	 */
	public static <P extends RasterPicture<?>> TextureBacker<P> create(LayeredGeometryRendererSubsystem layeredGeometryRendererSubsystem, Texture<P> texture) {
		if ( ( layeredGeometryRendererSubsystem != null ) && ( texture != null ) ) {
			return new TextureBacker<P>(layeredGeometryRendererSubsystem, texture);
		}
		else {
			throw new NullPointerException();
		}
	}
	
	/**
	 * @pre La textura no puede ser nula
	 * @post Crea el "backer" de la imagen de textura especificada con el subsistema
	 * 		 de renderizado de texturas especificado
	 */
	private TextureBacker(LayeredGeometryRendererSubsystem layeredGeometryRendererSubsystem, Texture<P> texture) {
		super(layeredGeometryRendererSubsystem.getGL());
		
		if ( texture != null ) {
			this.texture = texture;
			
			this.dependencyCount = 0;
			
			this.textureLinkedAllocatableElement = new TextureLinkedAllocatableElement(this);
			this.layeredGeometryRendererSubsystem = layeredGeometryRendererSubsystem;
			
			this.lastMeshLayersConfigBacker = null;
			
			this.isLoaded = false;
		}
		else {
			throw new NullPointerException();
		}
	}
	
	/**
	 * @post Devuelve el tipo de textura
	 */
	int getGlTextureType() {
		return this.glTextureType;
	}
	
	/**
	 * @post Devuelve las dimensiones de la imagen
	 */
	List<Integer> getPictureDimensions() {
		return this.pictureDimensions;
	}
	
	/**
	 * @post Realiza un "bind" de la textura sobre la unidad de textura activa
	 */
	void bindTexture() {
		this.gl.glBindTexture(this.glTextureType, this.glTextureObject);
	}
	
	/**
	 * @post Verifica si el "backer" tiene la textura cargada
	 */
	private void checkLoaded() {
		if ( !this.isLoaded ) {
			throw new IllegalStateException("Expected loaded texture");
		}
	}
	
	/**
	 * @post Devuelve el elemento representante de la textura en el MRU
	 */
	TextureLinkedAllocatableElement getTextureLinkedAllocatableElement() {
		return this.textureLinkedAllocatableElement;
	}
	
	/**
	 * @post Devuelve el subsistema de renderizado de geometría texturizada
	 */
	LayeredGeometryRendererSubsystem getTexturedGeometryRendererSubsystem() {
		return this.layeredGeometryRendererSubsystem;
	}
	
	/**
	 * @post Devuelve el id del objeto de OpenGL
	 */
	public int getId() {
		this.checkLoaded();
		return this.glTextureObject;
	}
	
	/**
	 * @post Asigna el objeto textura "cacheado"
	 */
	public void setCachedTextureObject(TextureObject<P> cachedTextureObject) {
		this.checkLoaded();
		this.cachedTextureObject = cachedTextureObject;
	}
	
	/**
	 * @post Devuelve el objeto textura "cacheado"
	 */
	public TextureObject<P> getCachedTextureObject() {
		this.checkLoaded();
		return this.cachedTextureObject;
	}
	
	/**
	 * @throws DataLoadingErrorException 
	 * @pre La textura no tiene que estar cargada
	 * @post Carga la textura
	 */
	public void load() throws GLException, DataLoadingErrorException {
		if ( !this.isLoaded() ) {
			final P picture = this.texture.getPicture();
			
			final int format = glPixelFormatByPixelFormat.get(picture.getPixelFormat());
			final int type = glTypeByComponentFormat.get(picture.getComponentFormat());
			
			this.glTextureObject = this.gl.glGenTextures();
			
			this.pictureDimensions = Collections.unmodifiableList(picture.accept(new RasterPicture.Visitor<List<Integer>>(){
				@Override
				public List<Integer> visit(RasterPicture2d picture) {
					return Arrays.asList(picture.getWidth(), picture.getHeight());
				}

				@Override
				public List<Integer> visit(RasterPicture3d picture) {
					return Arrays.asList(picture.getWidth(), picture.getHeight(), picture.getDepth());
				}
				
			}));
			
			this.glTextureType = picture.accept(new RasterPicture.Visitor<Integer>(){
				@Override
				public Integer visit(RasterPicture2d picture) {
					return GL21.GL_TEXTURE_2D;
				}

				@Override
				public Integer visit(RasterPicture3d picture) {
					return GL21.GL_TEXTURE_3D;
				}
				
			});
			
			// Asignar unidad de textura forzando su selección
			this.layeredGeometryRendererSubsystem.allocateTextureUnit(this, true);
			
			final ByteBuffer localByteBuffer = picture.getData().asReadOnlyBuffer();
			localByteBuffer.rewind();
			
			picture.accept(new RasterPicture.Visitor<Void>(){
				private final GL21 gl = TextureBacker.this.gl;
	
				@Override
				public Void visit(RasterPicture2d picture) {
					this.gl.glTexImage2D(GL21.GL_TEXTURE_2D, 0, format, picture.getWidth(), picture.getHeight(), 0, format, type, localByteBuffer);
					
					return null;
				}
	
				@Override
				public Void visit(RasterPicture3d picture) {
					this.gl.glTexImage3D(GL21.GL_TEXTURE_3D, 0, format, picture.getWidth(), picture.getHeight(), picture.getDepth(), 0, format, type, localByteBuffer);
					
					return null;
				}
				
			});
			
			this.cachedTextureObject = null;
			
			try {
				new GLErrorChecker(this.gl).checkGLError();
			}
			catch ( GLErrorException e ) {
				this.unload();
				throw new GLException(e);
			}
			
			this.isLoaded = true;
		}
		else {
			throw new IllegalStateException("Attemped to load texture when it has been loaded");
		}
	}
	
	/**
	 * @post Notifica el último backer de configuración de capas de malla que
	 * 		 usó éste backer de textura
	 */
	public void notifyLastMeshLayersConfigBacker(MeshLayersConfigBacker meshLayersConfigBacker) {
		this.lastMeshLayersConfigBacker = meshLayersConfigBacker;
		
		if ( !this.isLoaded() ) {
			this.lastMeshLayersConfigBacker.notifyDirtyState();
		}
	}
	
	/**
	 * @post Devuelve si está cargada
	 */
	public boolean isLoaded() {
		return this.isLoaded;
	}
	
	/**
	 * @post Descarga la textura
	 */
	public void unload() {
		this.checkLoaded();
		
		if ( this.lastMeshLayersConfigBacker != null ) {
			this.lastMeshLayersConfigBacker.notifyDirtyState();
		}
		
		this.layeredGeometryRendererSubsystem.notifyTextureBackerDestroy(this);
		this.gl.glDeleteTextures(this.glTextureObject);
		this.cachedTextureObject = null;
		this.isLoaded = false;
	}
	
	/**
	 * @post Incrementa en uno la cuenta de dependencias
	 */
	public void notifyNewDependency() {
		if ( this.dependencyCount != Integer.MAX_VALUE ) {
			this.dependencyCount++;
			
			if ( ( this.dependencyCount == 1 ) && ( !this.isLoaded() ) ) {
				this.layeredGeometryRendererSubsystem.getRenderEngineBackend().attachTextureBacker(this);
			}
		}
		else {
			throw new IllegalStateException("Dependency count overflow");
		}
	}
	
	/**
	 * @post Decrementa en uno la cuenta de dependencias
	 */
	public void notifyRemovedDependency() {
		if ( this.dependencyCount != 0 ) {
			this.dependencyCount--;
			
			if ( ( this.dependencyCount == 0 ) && ( !this.isLoaded() ) ) {
				this.layeredGeometryRendererSubsystem.getRenderEngineBackend().removeTextureBacker( (Texture) this.getTexture());
			}
		}
		else {
			throw new IllegalStateException("Dependency count underflow");
		}
	}
	
	/**
	 * @post Devuelve si tiene dependencias
	 */
	public boolean hasDependencies() {
		return (this.dependencyCount != 0);
	}
	
	/**
	 * @post Devuelve la textura
	 */
	public Texture<P> getTexture() {
		return this.texture;
	}
}
