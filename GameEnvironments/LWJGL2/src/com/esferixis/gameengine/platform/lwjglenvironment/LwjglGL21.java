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

package com.esferixis.gameengine.platform.lwjglenvironment;

import java.nio.ByteBuffer;
import java.nio.DoubleBuffer;
import java.nio.FloatBuffer;
import java.nio.IntBuffer;
import java.nio.ShortBuffer;

import org.lwjgl.opengl.GL11;
import org.lwjgl.opengl.GL12;
import org.lwjgl.opengl.GL13;
import org.lwjgl.opengl.GL15;
import org.lwjgl.opengl.GL20;
import org.lwjgl.opengl.Util;

import com.arielcarrizo.gameengine.renderengine.backend.opengl.gl21.GL21;

/**
 * Implementación de OpenGL basada en LWJGL
 * 
 * @author Ariel Favio Carrizo
 *
 */
final class LwjglGL21 implements GL21 {

	@Override
	public void glClear(int mask) {
		GL11.glClear(mask);
	}

	@Override
	public int glGenBuffers() {
		return GL15.glGenBuffers();
	}

	@Override
	public void glBindBuffer(int target, int buffer) {
		GL15.glBindBuffer(target, buffer);
	}

	@Override
	public void glBufferData(int target, ByteBuffer data, int usage) {
		GL15.glBufferData(target, data, usage);
	}

	@Override
	public void glBufferData(int target, DoubleBuffer data, int usage) {
		GL15.glBufferData(target, data, usage);
	}

	@Override
	public void glBufferData(int target, FloatBuffer data, int usage) {
		GL15.glBufferData(target, data, usage);
	}

	@Override
	public void glBufferData(int target, IntBuffer data, int usage) {
		GL15.glBufferData(target, data, usage);
	}

	@Override
	public void glBufferData(int target, long data_size, int usage) {
		GL15.glBufferData(target, data_size, usage);
	}

	@Override
	public void glBufferData(int target, ShortBuffer data, int usage) {
		GL15.glBufferData(target, data, usage);
	}

	@Override
	public void glEnableVertexAttribArray(int index) {
		GL20.glEnableVertexAttribArray(index);
	}

	@Override
	public void glDisableVertexAttribArray(int index) {
		GL20.glDisableVertexAttribArray(index);
	}

	@Override
	public void glDeleteBuffers(int buffer) {
		GL15.glDeleteBuffers(buffer);
	}

	@Override
	public void glVertexAttribPointer(int index, int size, int type,
			boolean normalized, int stride, long buffer_buffer_offset) {
		GL20.glVertexAttribPointer(index, size, type, normalized, stride, buffer_buffer_offset);
	}

	@Override
	public void glDrawArrays(int mode, int first, int count) {
		GL11.glDrawArrays(mode, first, count);
	}

	@Override
	public int glGenTextures() {
		return GL11.glGenTextures();
	}

	@Override
	public void glActiveTexture(int texture) {
		GL13.glActiveTexture(texture);
	}

	@Override
	public void glBindTexture(int target, int texture) {
		GL11.glBindTexture(target, texture);
	}

	@Override
	public void glPixelStorei(int pname, int param) {
		GL11.glPixelStorei(pname, param);
	}
	
	@Override
	public void glTexImage2D(int target, int level, int internalformat, int width, int height, int border, int format, int type, java.nio.ByteBuffer pixels) {
		GL11.glTexImage2D(target, level, internalformat, width, height, border, format, type, pixels);
	}

	@Override
	public void glTexParameter(int target, int pname, FloatBuffer param) {
		GL11.glTexParameter(target, pname, param);
	}

	@Override
	public void glTexParameter(int target, int pname, IntBuffer param) {
		GL11.glTexParameter(target, pname, param);
	}

	@Override
	public void glTexParameterf(int target, int pname, float param) {
		GL11.glTexParameterf(target, pname, param);
	}

	@Override
	public void glTexParameteri(int target, int pname, int param) {
		GL11.glTexParameteri(target, pname, param);
	}

	@Override
	public void glTexImage3D(int target, int level, int internalFormat,
			int width, int height, int depth, int border, int format, int type,
			ByteBuffer pixels) {
		GL12.glTexImage3D(target, level, internalFormat, width, height, depth, border, format, type, pixels);
	}

	@Override
	public void glDeleteTextures(int texture) {
		GL11.glDeleteTextures(texture);
	}

	/* (non-Javadoc)
	 * @see com.esferixis.gameengine.renderengine.opengl21.GL21#glShaderSource(int, java.nio.ByteBuffer)
	 */
	@Override
	public void glShaderSource(int shader, ByteBuffer string) {
		GL20.glShaderSource(shader, string);
	}

	/* (non-Javadoc)
	 * @see com.esferixis.gameengine.renderengine.opengl21.GL21#glShaderSource(int, java.lang.CharSequence)
	 */
	@Override
	public void glShaderSource(int shader, CharSequence string) {
		GL20.glShaderSource(shader, string);
	}

	/* (non-Javadoc)
	 * @see com.esferixis.gameengine.renderengine.opengl21.GL21#glShaderSource(int, java.lang.CharSequence[])
	 */
	@Override
	public void glShaderSource(int shader, CharSequence[] strings) {
		GL20.glShaderSource(shader, strings);
	}

	/* (non-Javadoc)
	 * @see com.esferixis.gameengine.renderengine.opengl21.GL21#glCompileShader(int)
	 */
	@Override
	public void glCompileShader(int shader) {
		GL20.glCompileShader(shader);
	}

	/* (non-Javadoc)
	 * @see com.esferixis.gameengine.renderengine.opengl21.GL21#glCreateProgram()
	 */
	@Override
	public int glCreateProgram() {
		return GL20.glCreateProgram();
	}

	/* (non-Javadoc)
	 * @see com.esferixis.gameengine.renderengine.opengl21.GL21#glAttachShader(int, int)
	 */
	@Override
	public void glAttachShader(int program, int shader) {
		GL20.glAttachShader(program, shader);
	}

	/* (non-Javadoc)
	 * @see com.esferixis.gameengine.renderengine.opengl21.GL21#glBindAttribLocation(int, int, java.nio.ByteBuffer)
	 */
	@Override
	public void glBindAttribLocation(int program, int index, ByteBuffer name) {
		GL20.glBindAttribLocation(program, index, name);
	}

	/* (non-Javadoc)
	 * @see com.esferixis.gameengine.renderengine.opengl21.GL21#glBindAttribLocation(int, int, java.lang.CharSequence)
	 */
	@Override
	public void glBindAttribLocation(int program, int index, CharSequence name) {
		GL20.glBindAttribLocation(program, index, name);
	}

	/* (non-Javadoc)
	 * @see com.esferixis.gameengine.renderengine.opengl21.GL21#glGetShader(int, int, java.nio.IntBuffer)
	 */
	@Override
	public void glGetShader(int shader, int pname, IntBuffer params) {
		GL20.glGetShader(shader, pname, params);
	}

	/* (non-Javadoc)
	 * @see com.esferixis.gameengine.renderengine.opengl21.GL21#glUniformMatrix2(int, boolean, java.nio.FloatBuffer)
	 */
	@Override
	public void glUniformMatrix2(int location, boolean transpose,
			FloatBuffer matrices) {
		GL20.glUniformMatrix2(location, transpose, matrices);
	}

	/* (non-Javadoc)
	 * @see com.esferixis.gameengine.renderengine.opengl21.GL21#glUniformMatrix3(int, boolean, java.nio.FloatBuffer)
	 */
	@Override
	public void glUniformMatrix3(int location, boolean transpose,
			FloatBuffer matrices) {
		GL20.glUniformMatrix3(location, transpose, matrices);
	}

	/* (non-Javadoc)
	 * @see com.esferixis.gameengine.renderengine.opengl21.GL21#glUniformMatrix4(int, boolean, java.nio.FloatBuffer)
	 */
	@Override
	public void glUniformMatrix4(int location, boolean transpose,
			FloatBuffer matrices) {
		GL20.glUniformMatrix4(location, transpose, matrices);
	}

	/* (non-Javadoc)
	 * @see com.esferixis.gameengine.renderengine.opengl21.GL21#glDrawElements(int, java.nio.ByteBuffer)
	 */
	@Override
	public void glDrawElements(int mode, ByteBuffer indices) {
		GL11.glDrawElements(mode, indices);
	}

	/* (non-Javadoc)
	 * @see com.esferixis.gameengine.renderengine.opengl21.GL21#glDrawElements(int, java.nio.IntBuffer)
	 */
	@Override
	public void glDrawElements(int mode, IntBuffer indices) {
		GL11.glDrawElements(mode, indices);
	}

	/* (non-Javadoc)
	 * @see com.esferixis.gameengine.renderengine.opengl21.GL21#glDrawElements(int, java.nio.ShortBuffer)
	 */
	@Override
	public void glDrawElements(int mode, ShortBuffer indices) {
		GL11.glDrawElements(mode, indices);
	}

	/* (non-Javadoc)
	 * @see com.esferixis.gameengine.renderengine.opengl21.GL21#glDrawElements(int, int, int, long)
	 */
	@Override
	public void glDrawElements(int mode, int indices_count, int type,
			long indices_buffer_offset) {
		GL11.glDrawElements(mode, indices_count, type, indices_buffer_offset);
	}

	/* (non-Javadoc)
	 * @see com.esferixis.gameengine.renderengine.opengl21.GL21#glGetBufferParameter(int, int, java.nio.IntBuffer)
	 */
	@Override
	public void glGetBufferParameter(int target, int pname, IntBuffer params) {
		GL15.glGetBufferParameter(target, pname, params);
	}

	/* (non-Javadoc)
	 * @see com.esferixis.gameengine.renderengine.opengl21.GL21#glGetBufferParameteri(int, int)
	 */
	@Override
	public int glGetBufferParameteri(int target, int pname) {
		return GL15.glGetBufferParameteri(target, pname);
	}

	/* (non-Javadoc)
	 * @see com.esferixis.gameengine.renderengine.opengl21.GL21#glEnable(int)
	 */
	@Override
	public void glEnable(int cap) {
		GL11.glEnable(cap);
	}

	/* (non-Javadoc)
	 * @see com.esferixis.gameengine.renderengine.opengl21.GL21#glDepthMask(boolean)
	 */
	@Override
	public void glDepthMask(boolean flag) {
		GL11.glDepthMask(flag);
	}

	/* (non-Javadoc)
	 * @see com.esferixis.gameengine.renderengine.opengl21.GL21#glDepthFunc(int)
	 */
	@Override
	public void glDepthFunc(int func) {
		GL11.glDepthFunc(func);
	}

	/* (non-Javadoc)
	 * @see com.esferixis.gameengine.renderengine.opengl21.GL21#glColorMask(boolean, boolean, boolean, boolean)
	 */
	@Override
	public void glColorMask(boolean red, boolean green, boolean blue,
			boolean alpha) {
		GL11.glColorMask(red, green, blue, alpha);
	}

	/* (non-Javadoc)
	 * @see com.esferixis.gameengine.renderengine.opengl21.GL21#glCreateShader(int)
	 */
	@Override
	public int glCreateShader(int type) {
		return GL20.glCreateShader(type);
	}

	/* (non-Javadoc)
	 * @see com.esferixis.gameengine.renderengine.opengl21.GL21#glDeleteShader(int)
	 */
	@Override
	public void glDeleteShader(int shader) {
		GL20.glDeleteShader(shader);
	}

	/* (non-Javadoc)
	 * @see com.esferixis.gameengine.renderengine.opengl21.GL21#glDeleteProgram(int)
	 */
	@Override
	public void glDeleteProgram(int program) {
		GL20.glDeleteProgram(program);
	}

	/* (non-Javadoc)
	 * @see com.esferixis.gameengine.renderengine.opengl21.GL21#glGetShader(int, int)
	 */
	@Override
	public int glGetShaderi(int shader, int pname) {
		return GL20.glGetShaderi(shader, pname);
	}

	/* (non-Javadoc)
	 * @see com.esferixis.gameengine.renderengine.opengl21.GL21#glLinkProgram(int)
	 */
	@Override
	public void glLinkProgram(int program) {
		GL20.glLinkProgram(program);
	}

	/* (non-Javadoc)
	 * @see com.esferixis.gameengine.renderengine.opengl21.GL21#glValidateProgram(int)
	 */
	@Override
	public void glValidateProgram(int program) {
		GL20.glValidateProgram(program);
	}

	/* (non-Javadoc)
	 * @see com.esferixis.gameengine.renderengine.backend.opengl21.GL21#glGetProgrami(int, int)
	 */
	@Override
	public int glGetProgram(int program, int pname) {
		return GL20.glGetProgrami(program, pname);
	}

	/* (non-Javadoc)
	 * @see com.esferixis.gameengine.renderengine.backend.opengl21.GL21#glGetProgram(int, int, java.nio.IntBuffer)
	 */
	@Override
	public void glGetProgram(int program, int pname, IntBuffer params) {
		GL20.glGetProgram(program, pname, params);
	}

	/* (non-Javadoc)
	 * @see com.esferixis.gameengine.renderengine.backend.opengl21.GL21#glUseProgram(int)
	 */
	@Override
	public void glUseProgram(int program) {
		GL20.glUseProgram(program);
	}

	/* (non-Javadoc)
	 * @see com.esferixis.gameengine.renderengine.backend.opengl21.GL21#glGetUniformLocation(int, java.lang.CharSequence)
	 */
	@Override
	public int glGetUniformLocation(int program, CharSequence name) {
		return GL20.glGetUniformLocation(program, name);
	}

	/* (non-Javadoc)
	 * @see com.esferixis.gameengine.renderengine.backend.opengl21.GL21#glUniform1f(int, float)
	 */
	@Override
	public void glUniform1f(int location, float v0) {
		GL20.glUniform1f(location, v0);
	}

	/* (non-Javadoc)
	 * @see com.esferixis.gameengine.renderengine.backend.opengl21.GL21#glUniform1fv(int, java.nio.FloatBuffer)
	 */
	@Override
	public void glUniform1(int location, FloatBuffer value) {
		GL20.glUniform1(location, value);
	}

	/* (non-Javadoc)
	 * @see com.esferixis.gameengine.renderengine.backend.opengl21.GL21#glUniform1i(int, int)
	 */
	@Override
	public void glUniform1i(int location, int v0) {
		GL20.glUniform1i(location, v0);
	}

	/* (non-Javadoc)
	 * @see com.esferixis.gameengine.renderengine.backend.opengl21.GL21#glUniform1iv(int, java.nio.IntBuffer)
	 */
	@Override
	public void glUniform1(int location, IntBuffer value) {
		GL20.glUniform1(location, value);
	}

	/* (non-Javadoc)
	 * @see com.esferixis.gameengine.renderengine.backend.opengl21.GL21#glUniform2f(int, float, float)
	 */
	@Override
	public void glUniform2f(int location, float v0, float v1) {
		GL20.glUniform2f(location, v0, v1);
	}

	/* (non-Javadoc)
	 * @see com.esferixis.gameengine.renderengine.backend.opengl21.GL21#glUniform2fv(int, java.nio.FloatBuffer)
	 */
	@Override
	public void glUniform2(int location, FloatBuffer value) {
		GL20.glUniform2(location, value);
	}

	/* (non-Javadoc)
	 * @see com.esferixis.gameengine.renderengine.backend.opengl21.GL21#glUniform2i(int, int, int)
	 */
	@Override
	public void glUniform2i(int location, int v0, int v1) {
		GL20.glUniform2i(location, v0, v1);
	}

	/* (non-Javadoc)
	 * @see com.esferixis.gameengine.renderengine.backend.opengl21.GL21#glUniform2iv(int, java.nio.IntBuffer)
	 */
	@Override
	public void glUniform2(int location, IntBuffer value) {
		GL20.glUniform2(location, value);
	}

	/* (non-Javadoc)
	 * @see com.esferixis.gameengine.renderengine.backend.opengl21.GL21#glUniform3f(int, float, float, float)
	 */
	@Override
	public void glUniform3f(int location, float v0, float v1, float v2) {
		GL20.glUniform3f(location, v0, v1, v2);
	}

	/* (non-Javadoc)
	 * @see com.esferixis.gameengine.renderengine.backend.opengl21.GL21#glUniform3fv(int, java.nio.FloatBuffer)
	 */
	@Override
	public void glUniform3(int location, FloatBuffer value) {
		GL20.glUniform3(location, value);
	}

	/* (non-Javadoc)
	 * @see com.esferixis.gameengine.renderengine.backend.opengl21.GL21#glUniform3i(int, int, int, int)
	 */
	@Override
	public void glUniform3i(int location, int v0, int v1, int v2) {
		GL20.glUniform3i(location, v0, v1, v2);
	}

	/* (non-Javadoc)
	 * @see com.esferixis.gameengine.renderengine.backend.opengl21.GL21#glUniform3iv(int, java.nio.IntBuffer)
	 */
	@Override
	public void glUniform3(int location, IntBuffer value) {
		GL20.glUniform3(location, value);
	}

	/* (non-Javadoc)
	 * @see com.esferixis.gameengine.renderengine.backend.opengl21.GL21#glUniform4f(int, float, float, float, float)
	 */
	@Override
	public void glUniform4f(int location, float v0, float v1, float v2, float v3) {
		GL20.glUniform4f(location, v0, v1, v2, v3);
	}

	/* (non-Javadoc)
	 * @see com.esferixis.gameengine.renderengine.backend.opengl21.GL21#glUniform4fv(int, java.nio.FloatBuffer)
	 */
	@Override
	public void glUniform4(int location, FloatBuffer value) {
		GL20.glUniform4(location, value);
	}

	/* (non-Javadoc)
	 * @see com.esferixis.gameengine.renderengine.backend.opengl21.GL21#glUniform4i(int, int, int, int, int)
	 */
	@Override
	public void glUniform4i(int location, int v0, int v1, int v2, int v3) {
		GL20.glUniform4i(location, v0, v1, v2, v3);
	}

	/* (non-Javadoc)
	 * @see com.esferixis.gameengine.renderengine.backend.opengl21.GL21#glUniform4iv(int, java.nio.IntBuffer)
	 */
	@Override
	public void glUniform4(int location, IntBuffer value) {
		GL20.glUniform4(location, value);
	}

	/* (non-Javadoc)
	 * @see com.esferixis.gameengine.renderengine.backend.opengl.gl21.GL21#glGetAttribLocation(int, java.lang.CharSequence)
	 */
	@Override
	public int glGetAttribLocation(int program, CharSequence name) {
		return GL20.glGetAttribLocation(program, name);
	}

	/* (non-Javadoc)
	 * @see com.esferixis.gameengine.renderengine.backend.opengl.gl21.GL21#glGetShaderInfoLog(int)
	 */
	@Override
	public String glGetShaderInfoLog(int shader, int maxLength) {
		return GL20.glGetShaderInfoLog(shader, maxLength);
	}

	/* (non-Javadoc)
	 * @see com.esferixis.gameengine.renderengine.backend.opengl.gl21.GL21#glClearColor(float, float, float, float)
	 */
	@Override
	public void glClearColor(float red, float green, float blue, float alpha) {
		GL11.glClearColor(red, green, blue, alpha);
	}

	/* (non-Javadoc)
	 * @see com.esferixis.gameengine.renderengine.backend.opengl.gl21.GL21#glDisable(int)
	 */
	@Override
	public void glDisable(int cap) {
		GL11.glDisable(cap);
	}

	/* (non-Javadoc)
	 * @see com.esferixis.gameengine.renderengine.backend.opengl.gl21.GL21#glViewport(int, int, int, int)
	 */
	@Override
	public void glViewport(int x, int y, int width, int height) {
		GL11.glViewport(x, y, width, height);
	}

	/* (non-Javadoc)
	 * @see com.esferixis.gameengine.renderengine.backend.opengl.gl21.GL21#glGetError()
	 */
	@Override
	public int glGetError() {
		return GL11.glGetError();
	}

	/* (non-Javadoc)
	 * @see com.esferixis.gameengine.renderengine.backend.opengl.gl21.GL21#glCullFace(int)
	 */
	@Override
	public void glCullFace(int mode) {
		GL11.glCullFace(mode);
	}

	/* (non-Javadoc)
	 * @see com.esferixis.gameengine.renderengine.backend.opengl.gl21.GL21#glClearDepth(double)
	 */
	@Override
	public void glClearDepth(double depth) {
		GL11.glClearDepth(depth);
	}

	/* (non-Javadoc)
	 * @see com.esferixis.gameengine.renderengine.backend.opengl.gl21.GL21#glGetBoolean(int)
	 */
	@Override
	public boolean glGetBoolean(int pname) {
		return GL11.glGetBoolean(pname);
	}

	/* (non-Javadoc)
	 * @see com.esferixis.gameengine.renderengine.backend.opengl.gl21.GL21#glGetFloat(int)
	 */
	@Override
	public float glGetFloat(int pname) {
		return GL11.glGetFloat(pname);
	}

	/* (non-Javadoc)
	 * @see com.esferixis.gameengine.renderengine.backend.opengl.gl21.GL21#glGetInteger(int)
	 */
	@Override
	public int glGetInteger(int pname) {
		return GL11.glGetInteger(pname);
	}

	/* (non-Javadoc)
	 * @see com.esferixis.gameengine.renderengine.backend.opengl.gl21.GL21#glGetUniformiv(int, int, java.nio.IntBuffer)
	 */
	@Override
	public void glGetUniform(int program, int location, IntBuffer params) {
		GL20.glGetUniform(program, location, params);
	}

	/* (non-Javadoc)
	 * @see com.esferixis.gameengine.renderengine.backend.opengl.gl21.GL21#getErrorString(int)
	 */
	@Override
	public String getErrorString(int errorCode) {
		return Util.translateGLErrorString(errorCode);
	}

	/* (non-Javadoc)
	 * @see com.esferixis.gameengine.renderengine.backend.opengl.gl21.GL21#glBlendFunc(int, int)
	 */
	@Override
	public void glBlendFunc(int sfactor, int dfactor) {
		GL11.glBlendFunc(sfactor, dfactor);
	}

	/* (non-Javadoc)
	 * @see com.esferixis.gameengine.renderengine.backend.opengl.gl21.GL21#glBufferSubData(int, long, java.nio.ByteBuffer)
	 */
	@Override
	public void glBufferSubData(int target, long offset, ByteBuffer data) {
		GL15.glBufferSubData(target, offset, data);
	}
}
