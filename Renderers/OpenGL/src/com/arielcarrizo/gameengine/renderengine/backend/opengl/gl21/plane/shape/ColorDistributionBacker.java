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
package com.arielcarrizo.gameengine.renderengine.backend.opengl.gl21.plane.shape;

import java.util.Collection;

import com.arielcarrizo.gameengine.renderengine.backend.opengl.GLException;
import com.arielcarrizo.gameengine.renderengine.backend.opengl.gl21.Gl21RenderEngineBackendObject;
import com.arielcarrizo.gameengine.renderengine.backend.opengl.gl21.Gl21RenderEngineBackendSystem;
import com.arielcarrizo.gameengine.renderengine.backend.opengl.gl21.attributestream.AttributeStream;
import com.arielcarrizo.gameengine.renderengine.backend.opengl.gl21.attributestream.AttributeStreamConfig;
import com.arielcarrizo.gameengine.renderengine.backend.opengl.gl21.globjects.shader.FragmentShader;
import com.arielcarrizo.gameengine.renderengine.backend.opengl.gl21.globjects.shader.VertexShader;
import com.arielcarrizo.gameengine.renderengine.backend.opengl.gl21.globjects.shader.ShaderProgram.Attribute;
import com.arielcarrizo.gameengine.renderengine.backend.opengl.gl21.globjects.shader.ShaderProgram.Uniform;
import com.esferixis.gameengine.renderengine.plane.shape.ColorDistribution;
import com.esferixis.math.Matrix3f;
import com.esferixis.math.Vector2f;

abstract class ColorDistributionBacker<D extends ColorDistribution> extends Gl21RenderEngineBackendObject {
	protected static abstract class ShaderProgramContainer extends com.arielcarrizo.gameengine.renderengine.backend.opengl.gl21.globjects.shader.ShaderProgramContainer {
		/**
		 * @param vertexShader
		 * @param fragmentShader
		 * @param specificAttributes
		 * @param specificUniforms
		 * @throws GLException
		 */
		protected ShaderProgramContainer(VertexShader vertexShader, FragmentShader fragmentShader,
				Collection<Attribute> specificAttributes, Collection<Uniform> specificUniforms) throws GLException {
			super(vertexShader, fragmentShader, specificAttributes, specificUniforms);
		}

		/**
		 * @post Destruye el contenedor
		 */
		@Override
		protected void destroy() {
			super.destroy();
		}
	}
	
	protected static final String vertexShaderSource = 
		"#version 120\n\n" +
		"attribute vec2 vertexPosition;\n" +
		"uniform mat3 transformMatrix;\n\n" +
		"void main(void) {\n" +
		"	gl_Position = vec4( transformMatrix * vec3(vertexPosition, 1.0), 1.0 );\n" +
		"}"
	;
	
	protected final ShaderProgramContainer shaderProgramContainer;
	private final AttributeStreamConfig attributeStreamConfig;
	
	/**
	 * @pre Ninguno de los 3 puede ser nulo
	 * @post Crea el backer de la distribución de color con el sistema del backend del motor de renderización, 
	 * 		 con el contenedor de shader y el stream de atributo de posición especificados
	 */
	public ColorDistributionBacker(Gl21RenderEngineBackendSystem renderEngineBackendSystem, ShaderProgramContainer shaderProgramContainer, AttributeStream<Vector2f> positionAttributeStream) {
		super(renderEngineBackendSystem);
		if ( ( shaderProgramContainer != null ) && ( positionAttributeStream != null ) ) {
			this.shaderProgramContainer = shaderProgramContainer;
			
			{
				final AttributeStreamConfig.Essence essence = new AttributeStreamConfig.Essence();
				essence.addAttributeStream(positionAttributeStream, this.shaderProgramContainer.getVertexPositionAttribute());
				this.attributeStreamConfig = new AttributeStreamConfig(essence);
			}
		}
		else {
			throw new NullPointerException();
		}
	}
	
	/**
	 * @pre Ninguno de los dos parámetros puede ser nulo
	 * @post Prepara el renderizado con la distribución de color y la matriz de transformación especificados
	 */
	public final void prepare(D colorDistribution, Matrix3f transformMatrix) {
		if ( ( colorDistribution != null ) && ( transformMatrix != null ) ) {
			this.renderEngineBackend.getShaderProgramContainerSwitcher().select(this.shaderProgramContainer);
			this.gl.glUniformMatrix3(this.shaderProgramContainer.getTransformMatrixUniform().getLocation(), false, transformMatrix.store());
			this.setSpecificUniforms(colorDistribution);
			this.renderEngineBackend.getAttributeStreamManager().setAttributeStreamConfig(this.attributeStreamConfig);
		}
		else {
			throw new NullPointerException();
		}
	}
	
	/**
	 * @post Actualiza los valores de los uniforms con la distribución de color
	 * 		 especificada
	 */
	protected abstract void setSpecificUniforms(D colorDistribution);
	
	/**
	 * @post Destruye el backer de la distribución de color especificada
	 */
	public void destroy() {
		this.shaderProgramContainer.destroy();
	}
}
