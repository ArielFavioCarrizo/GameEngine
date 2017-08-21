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
import java.util.Collections;
import java.util.List;

import com.arielcarrizo.gameengine.renderengine.backend.opengl.GLException;
import com.arielcarrizo.gameengine.renderengine.backend.opengl.gl21.Gl21RenderEngineBackendObject;
import com.arielcarrizo.gameengine.renderengine.backend.opengl.gl21.Gl21RenderEngineBackendSystem;
import com.arielcarrizo.gameengine.renderengine.backend.opengl.gl21.attributestream.AttributeStream;
import com.arielcarrizo.gameengine.renderengine.backend.opengl.gl21.globjects.shader.FragmentShader;
import com.arielcarrizo.gameengine.renderengine.backend.opengl.gl21.globjects.shader.ShaderProgram;
import com.arielcarrizo.gameengine.renderengine.backend.opengl.gl21.globjects.shader.UniformWriteCache;
import com.arielcarrizo.gameengine.renderengine.backend.opengl.gl21.globjects.shader.Vector4fUniformWriteCache;
import com.arielcarrizo.gameengine.renderengine.backend.opengl.gl21.globjects.shader.VertexShader;
import com.arielcarrizo.gameengine.renderengine.backend.opengl.gl21.globjects.shader.ShaderProgram.Attribute;
import com.arielcarrizo.gameengine.renderengine.backend.opengl.gl21.globjects.shader.ShaderProgram.Uniform;
import com.esferixis.gameengine.renderengine.plane.shape.ColorDistribution;
import com.esferixis.gameengine.renderengine.plane.shape.UniformColorDistribution;
import com.esferixis.math.Vector2f;

final class UniformColorDistributionBacker extends ColorDistributionBacker<UniformColorDistribution> {
	private static final String fragmentShaderSource =
		"#version 120\n\n" +
		"uniform vec4 uniformColor;\n\n" +
		"void main(void) {\n" +
		"	gl_FragColor = uniformColor;\n" +
		"}"
	;
	
	private static class ShaderProgramContainer extends ColorDistributionBacker.ShaderProgramContainer {
		private final Vector4fUniformWriteCache uniformColorUniformWriteCache;
		
		/**
		 * @post Crea el contenedor de shaders
		 */
		private static ShaderProgramContainer create(VertexShader vertexShader, FragmentShader fragmentShader) throws GLException {
			final ShaderProgram.Uniform uniformColorUniform = new ShaderProgram.Uniform("uniformColor");
			
			return new ShaderProgramContainer(vertexShader, fragmentShader, uniformColorUniform);
		}
		
		/**
		 * @param vertexShader
		 * @param fragmentShader
		 * @param specificAttributes
		 * @param specificUniforms
		 * @throws GLException
		 */
		protected ShaderProgramContainer(VertexShader vertexShader, FragmentShader fragmentShader, Uniform uniformColorUniform) throws GLException {
			super(vertexShader, fragmentShader, (List<ShaderProgram.Attribute>) (List) Collections.emptyList(), Collections.singletonList(uniformColorUniform));
			
			this.uniformColorUniformWriteCache = new Vector4fUniformWriteCache(vertexShader.getGL(), uniformColorUniform);
		}
		
	}
	
	private final ShaderProgramContainer shaderProgramContainer;
	
	public UniformColorDistributionBacker(Gl21RenderEngineBackendSystem renderEngineBackendSystem, AttributeStream<Vector2f> positionAttributeStream) throws GLException {
		super(renderEngineBackendSystem, ShaderProgramContainer.create(new VertexShader(renderEngineBackendSystem.getGL(), vertexShaderSource), new FragmentShader(renderEngineBackendSystem.getGL(), fragmentShaderSource)), positionAttributeStream);
		
		this.shaderProgramContainer = (ShaderProgramContainer) ((ColorDistributionBacker<UniformColorDistribution>) this).shaderProgramContainer;
	}

	/* (non-Javadoc)
	 * @see com.esferixis.gameengine.renderengine.backend.opengl.gl21.plane.shape.ColorDistributionBacker#setSpecificUniforms(com.esferixis.gameengine.renderengine.backend.plane.shape.ColorDistribution)
	 */
	@Override
	protected void setSpecificUniforms(UniformColorDistribution colorDistribution) {
		this.shaderProgramContainer.uniformColorUniformWriteCache.set(colorDistribution.getColor());
	}
}
