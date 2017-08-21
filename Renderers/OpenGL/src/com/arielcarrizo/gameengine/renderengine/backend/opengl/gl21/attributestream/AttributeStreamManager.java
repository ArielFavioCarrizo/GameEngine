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
package com.arielcarrizo.gameengine.renderengine.backend.opengl.gl21.attributestream;

import java.nio.ByteBuffer;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Stack;

import com.arielcarrizo.gameengine.renderengine.backend.opengl.gl21.GL21;
import com.arielcarrizo.gameengine.renderengine.backend.opengl.gl21.GLObject;
import com.arielcarrizo.gameengine.renderengine.backend.opengl.gl21.globjects.shader.ShaderProgram;

public class AttributeStreamManager extends GLObject {
	private final int bufferVertexSize;
	private final List< Stack<StreamingVbo> > freeVbosPerAttributeStreamClassIndex;
	private final List<AttributeStream<?>> activeAttributeStreams;
	
	private int vertexCount;
	private int vertexCountLimit;
	
	private int remainingMinVertices;
	
	private static final Object repeatFirstKey = new Object();
	private static final Object repeatSecondKey = new Object();
	private static final Object repeatNSub1Key = new Object();
	private static final Object repeatLastKey = new Object();
	
	public enum DrawingMode {
		POINT(GL21.GL_NONE, 1, 1),
		LINE(GL21.GL_LINES, 2, 2),
		LINE_STRIP(GL21.GL_LINE_STRIP, 2, 1, repeatLastKey),
		LINE_LOOP(GL21.GL_LINE_LOOP, 3, 1, repeatFirstKey),
		TRIANGLES(GL21.GL_TRIANGLES, 3, 3),
		TRIANGLE_FAN(GL21.GL_TRIANGLE_FAN, 3, 1, repeatFirstKey, repeatSecondKey),
		TRIANGLE_STRIP(GL21.GL_TRIANGLE_STRIP, 3, 1, repeatLastKey, repeatNSub1Key);
		
		DrawingMode(int glDrawingType, int minVertices, int vertexGranulanity, Object... repeatKeys) {
			this.glDrawingType = glDrawingType;
			this.minVertices = minVertices;
			this.vertexGranulanity = vertexGranulanity;
			this.repeatKeys = Collections.unmodifiableList( Arrays.asList(repeatKeys) );
		}
		
		private final int vertexGranulanity;
		private final int minVertices;
		private final int glDrawingType;
		
		private final List<Object> repeatKeys;
	};
	
	private AttributeStreamConfig attributeStreamConfig;
	private DrawingMode drawingMode;
	
	private boolean firstVertexInStream;
	private boolean firstVertexInBufferEvent;
	
	/**
	 * @pre La implementación de OpenGL no puede ser nula y la cantidad de vértices tiene que ser positiva
	 * @post Crea el administrador del streamer de atributos con el tamaño
	 * 		 de buffer especificado en cantidad de vértices
	 */
	public AttributeStreamManager(GL21 gl, int bufferVertexSize) {
		super(gl);
		if ( bufferVertexSize > 0 ) {
			this.bufferVertexSize = bufferVertexSize;
			
			List< Stack<StreamingVbo> > freeVbosPerAttributeStreamClassIndex = new ArrayList<Stack<StreamingVbo>>(AttributeStream.CONCRETEINSTANCECLASSINDEXER.getConcreteClassesCount());
			for ( int i = 0 ; i < AttributeStream.CONCRETEINSTANCECLASSINDEXER.getConcreteClassesCount() ; i++ ) {
				freeVbosPerAttributeStreamClassIndex.add( new Stack<StreamingVbo>() );
			}
			
			this.freeVbosPerAttributeStreamClassIndex = Collections.unmodifiableList(freeVbosPerAttributeStreamClassIndex);
			this.activeAttributeStreams = new ArrayList<AttributeStream<?>>();
			
			this.drawingMode = null;
			this.vertexCount = 0;
			
			this.attributeStreamConfig = null;
		}
		else {
			throw new IllegalArgumentException("Illegal buffer vertex size");
		}
	}
	
	/**
	 * @post Devuelve la alineación de componentes
	 */
	int getComponentAlignment() {
		return 4;
	}
	
	void checkNotStreaming() {
		if ( this.hasInStreaming() ) {
			throw new IllegalStateException("Unexpected attribute streaming");
		}
	}
	
	void checkStreaming() {
		if ( !this.hasInStreaming() ) {
			throw new IllegalStateException("Expected attribute streaming");
		}
	}
	
	/**
	 * @post Devuelve si está en streaming
	 */
	public boolean hasInStreaming() {
		return (this.drawingMode != null);
	}
	
	/**
	 * @post Notifica a los streams el comienzo de un nuevo vértice
	 */
	private void notifyNewVertex() {
		for ( AttributeStream<?> eachAttributeStream : this.activeAttributeStreams ) {
			eachAttributeStream.notifyNewVertex();
		}
	}
	
	/**
	 * @post Especifica la configuración de stream de atributo.
	 * 		 Si es nulo significa que se deja de realizar
	 * 		 streaming de atributos.
	 * 	 	 IMPORTANTE: Es necesario hacerlo para dejar todos los atributos desactivados,
	 * 		 para dejar en un estado limpio a OpenGL
	 */
	public void setAttributeStreamConfig(AttributeStreamConfig attributeStreamConfig) {
		if ( attributeStreamConfig != this.attributeStreamConfig ) {
			// Desasignar todos los streams de atributo previos
			for ( AttributeStream<?> eachAttributeStream : this.activeAttributeStreams ) {
				final Stack<StreamingVbo> vboStack = this.freeVbosPerAttributeStreamClassIndex.get(AttributeStream.CONCRETEINSTANCECLASSINDEXER.classIndex(eachAttributeStream));
				
				vboStack.push(eachAttributeStream.deactivate());
			}
			
			this.activeAttributeStreams.clear();
			
			if ( attributeStreamConfig != null ) {
				// Activar los streams de atributo
				for ( Map.Entry<AttributeStream<?>, ShaderProgram.Attribute> eachEntry : attributeStreamConfig.getLocationPerAttributeStream().entrySet() ) {
					final AttributeStream<?> attributeStream = eachEntry.getKey();
					final ShaderProgram.Attribute attribute = eachEntry.getValue();
					
					final Stack<StreamingVbo> vboStack = this.freeVbosPerAttributeStreamClassIndex.get(AttributeStream.CONCRETEINSTANCECLASSINDEXER.classIndex(attributeStream));
					final StreamingVbo vbo;
					
					if ( vboStack.isEmpty() ) {
						int requiredComponentsPerVertex = attributeStream.getComponentsPerVertex();
						
						if ( requiredComponentsPerVertex % this.getComponentAlignment() != 0 ) {
							requiredComponentsPerVertex += this.getComponentAlignment();
						}
						
						requiredComponentsPerVertex = (int) (requiredComponentsPerVertex / this.getComponentAlignment()) * this.getComponentAlignment();
						
						vbo = new StreamingVbo(this.gl, requiredComponentsPerVertex * attributeStream.getComponentSize() * this.bufferVertexSize, attributeStream.getComponentSize(), attributeStream.getGLType() );
					}
					else {
						vbo = vboStack.pop();
					}
					
					attributeStream.activate(this, vbo, attribute);
					this.activeAttributeStreams.add(attributeStream);
				}
				
			}
			
			this.attributeStreamConfig = attributeStreamConfig;
		}
	}
	
	private void registerMoment(Object repeatingKey) {
		if ( this.drawingMode.repeatKeys.contains(repeatingKey) ) {
			for ( AttributeStream<?> eachAttributeStream : this.activeAttributeStreams ) {
				eachAttributeStream.storeValueByKey(repeatingKey);
			}
		}
	}
	
	void notifySendValueEvent() {
		if ( ( !this.drawingMode.repeatKeys.isEmpty() ) && ( !this.firstVertexInStream ) && ( this.vertexCount == 0 ) && this.firstVertexInBufferEvent ) {
			this.firstVertexInBufferEvent = false;
			
			for ( AttributeStream<?> eachAttributeStream : this.activeAttributeStreams ) {
				eachAttributeStream.sendStoredValuesByKeys(this.drawingMode.repeatKeys);
			}
			
			this.endVertex();
		}
	}
	
	private void clearVboBuffers() {
		this.vertexCount = 0;
		
		for ( AttributeStream<?> eachAttributeStream : this.activeAttributeStreams ) {
			eachAttributeStream.getVbo().getByteBuffer().clear();
		}
	};
	
	/**
	 * @pre El modo de dibujado no puede ser nulo y no tiene que
	 * 		estar en streaming
	 * @post Comienza el envío de atributos
	 */
	public void startStreaming(DrawingMode drawingMode) {
		this.checkNotStreaming();
		
		if ( drawingMode != null ) {
			
			this.vertexCountLimit = (this.bufferVertexSize / drawingMode.vertexGranulanity) * drawingMode.vertexGranulanity;
			
			this.notifyNewVertex();
			
			this.drawingMode = drawingMode;
			
			this.remainingMinVertices = this.drawingMode.minVertices;
			
			this.firstVertexInStream = true;
			this.firstVertexInBufferEvent = true;
			
			this.clearVboBuffers();
			
			this.registerMoment(repeatFirstKey);
		}
	}
	
	/**
	 * @pre Tiene que estar en streaming y tienen que haber enviado todos los streams
	 * 		los valores correspondientes a cada atributo.
	 * @post Indica que se terminó de enviar todos los atributos de vértice
	 */
	public void endVertex() {
		this.checkStreaming();
		
		for ( AttributeStream<?> eachAttributeStream : this.activeAttributeStreams ) {
			if ( !eachAttributeStream.hasSendedValue() ) {
				throw new IllegalStateException("Missing attribute sending");
			}
		}
		
		if ( this.vertexCount == this.vertexCountLimit ) {
			this.doDrawing();
			this.firstVertexInBufferEvent = true;
		}
		else if ( this.vertexCount == this.vertexCountLimit-1 ) {
			this.registerMoment(repeatLastKey);
		}
		else if ( this.vertexCount == this.vertexCountLimit-2 ) {
			this.registerMoment(repeatNSub1Key);
		}
		
		this.firstVertexInStream = false;
		
		this.vertexCount++;
		
		if ( this.remainingMinVertices > 0 ) this.remainingMinVertices--;
		
		this.notifyNewVertex();
	}
	
	/**
	 * @post Actualiza los Vbo y realiza el renderizado
	 */
	private void doDrawing() {
		if ( this.vertexCount != 0 ) {
			for ( AttributeStream<?> eachAttributeStream : this.activeAttributeStreams ) {
				eachAttributeStream.getVbo().update();
			}
			
			//this.clearVboBuffers();
			
			this.gl.glDrawArrays(this.drawingMode.glDrawingType, 0, this.vertexCount);
			
			this.vertexCount = 0;
		}
		
	}
	
	/**
	 * @pre Tiene que estar en streaming y el último vértice
	 * 		tiene que estar terminado
	 * @post Termina de enviar todos los valores de los
	 * 		 atributos.
	 * 		 Garantizando que haya terminado toda operación de dibujado-
	 */
	public void endStreaming() {
		this.checkStreaming();
		
		for ( AttributeStream<?> eachAttributeStream : this.activeAttributeStreams ) {
			if ( eachAttributeStream.hasSendedValue() ) {
				throw new IllegalStateException("Incomplete vertex");
			}
		}
		
		if ( ( this.remainingMinVertices == 0 ) && ( this.vertexCount % this.drawingMode.vertexGranulanity == 0 ) ) {
			this.doDrawing();
			
			this.drawingMode = null;
		}
		else {
			throw new IllegalStateException("Missing vertices");
		}
	}
}
