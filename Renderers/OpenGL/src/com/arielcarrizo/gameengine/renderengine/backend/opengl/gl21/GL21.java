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

import java.nio.ByteBuffer;
import java.nio.DoubleBuffer;
import java.nio.FloatBuffer;
import java.nio.IntBuffer;
import java.nio.ShortBuffer;

/**
 * Interface para implementaciones de OpenGL 2.1 sin el "FIXED PIPELINE"
 */
public interface GL21 {
	public static final int GL_NONE = 0;
	
	public static final int GL_TRUE = 1;
	public static final int GL_FALSE = 0;
	
	public static final int GL_POINT = 6912;
	public static final int GL_LINE = 6913;
	
	public static final int GL_POINTS = 0;
	public static final int GL_LINES = 2;
	public static final int GL_LINE_STRIP = 3;
	public static final int GL_LINE_LOOP = 2;
	public static final int GL_TRIANGLES = 4;
	public static final int GL_TRIANGLE_FAN = 6;
	public static final int GL_TRIANGLE_STRIP = 7;
	
	public static final int GL_CULL_FACE = 2884;
	public static final int GL_FRONT = 1028;
	public static final int GL_BACK = 1029;
	
	public static final int GL_DOUBLE = 5130;
	public static final int GL_FLOAT = 5126;
	public static final int GL_INT = 5124;
	public static final int GL_BYTE = 5120;
	public static final int GL_UNSIGNED_BYTE = 5121;
	public static final int GL_SHORT = 5122;
	
	public static final int GL_BLEND = 3042;
	public static final int GL_SRC_COLOR = 768;
	public static final int GL_ONE_MINUS_SRC_COLOR = 769;
	public static final int GL_DST_COLOR = 774;
	public static final int GL_ONE_MINUS_DST_COLOR = 775;
	public static final int GL_SRC_ALPHA = 770;
	public static final int GL_ONE_MINUS_SRC_ALPHA = 771;
	public static final int GL_DST_ALPHA = 772;
	public static final int GL_ONE_MINUS_DST_ALPHA = 773;
	public static final int GL_CONSTANT_COLOR = 32769;
	public static final int GL_ONE_MINUS_CONSTANT_COLOR = 32770;
	public static final int GL_CONSTANT_ALPHA = 32771;
	public static final int GL_ONE_MINUS_CONSTANT_ALPHA = 32772;
	public static final int GL_SRC_ALPHA_SATURATE = 776;
	
	public static final int GL_MAX_TEXTURE_IMAGE_UNITS = 34930;
	public static final int GL_MAX_COMBINED_TEXTURE_IMAGE_UNITS = 35661;
	public static final int GL_MAX_VARYING_FLOATS = 35659;
	public static final int GL_MAX_VERTEX_UNIFORM_COMPONENTS = 35658;
	public static final int GL_MAX_FRAGMENT_UNIFORM_COMPONENTS = 35657;
	public static final int GL_MAX_UNIFORM_LOCATIONS = 33390;
	
	public static final int GL_TEXTURE_1D = 3552;
	public static final int GL_TEXTURE_2D = 3553;
	public static final int GL_TEXTURE_3D = 32879;
	
	public static final int GL_TEXTURE_RECTANGLE = 34037;
	
	public static final int GL_TEXTURE_CUBE_MAP = 34067;
	
	public static final int GL_DEPTH_STENCIL_TEXTURE_MODE = 37098;
	public static final int GL_TEXTURE_BASE_LEVEL = 33084;
	public static final int GL_TEXTURE_BORDER_COLOR = 4100;
	public static final int GL_TEXTURE_COMPARE_FUNC = 34893;
	public static final int GL_TEXTURE_COMPARE_MODE = 34892;
	public static final int GL_TEXTURE_LOD_BIAS = 34049;
	public static final int GL_TEXTURE_MIN_FILTER = 10241;
	public static final int GL_TEXTURE_MAG_FILTER = 10240;
	public static final int GL_TEXTURE_MIN_LOD = 33082;
	public static final int GL_TEXTURE_MAX_LOD = 33083;
	public static final int GL_TEXTURE_MAX_LEVEL = 33085;
	public static final int GL_TEXTURE_WRAP_S = 10242;
	public static final int GL_TEXTURE_WRAP_T = 10243;
	public static final int GL_TEXTURE_WRAP_R = 32882;
	
	public static final int GL_LEQUAL = 515;
	public static final int GL_GEQUAL = 518;
	public static final int GL_LESS = 513;
	public static final int GL_GREATER = 516;
	public static final int GL_EQUAL = 514;
	public static final int GL_NOTEQUAL = 517;
	public static final int GL_ALWAYS = 519;
	public static final int GL_NEVER = 512;
	
	public static final int GL_NEAREST = 9728;
	public static final int GL_LINEAR = 9729;
	public static final int GL_NEAREST_MIPMAP_NEAREST = 9984;
	public static final int GL_LINEAR_MIPMAP_NEAREST = 9985;
	public static final int GL_NEAREST_MIPMAP_LINEAR = 9986;
	public static final int GL_LINEAR_MIPMAP_LINEAR = 9987;
	
	public static final int GL_UNPACK_ALIGNMENT = 3317;
	
	public static final int GL_RG = 33319;
	public static final int GL_RGB = 6407;
	public static final int GL_RGBA = 6408;
	
	public static final int GL_R8 = 33321;
	public static final int GL_RG8 = 33323;
	public static final int GL_RGB8 = 32849;
	public static final int GL_RGBA8 = 32856;
	
	public static final int GL_R32F = 33326;
	public static final int GL_RG32F = 33328;
	public static final int GL_RGB32F = 34837;
	public static final int GL_RGBA32F = 34836;
	
	public static final int GL_TEXTURE0 = 33984;
	
	public static final int GL_CLAMP_TO_EDGE = 33071;
	public static final int GL_CLAMP_TO_BORDER = 33069;
	public static final int GL_REPEAT = 10497;
	public static final int GL_MIRRORED_REPEAT = 33648;
	
	public static final int GL_RED = 6403;
	public static final int GL_GREEN = 6404;
	public static final int GL_BLUE = 6405;
	public static final int GL_ALPHA = 6406;
	
	public static final int GL_ZERO = 0;
	public static final int GL_ONE = 1;
	
	public static final int	GL_COLOR_BUFFER_BIT	= 16384;
	public static final int GL_DEPTH_BUFFER_BIT = 256;
	public static final int	GL_STENCIL_BUFFER_BIT= 1024;
	
	public static final int	GL_DEPTH_TEST = 2929;
	public static final int	GL_DEPTH_WRITEMASK = 2930;
	
	public static final int GL_COMPILE_STATUS = 35713;
	
	public static final int GL_VERTEX_SHADER = 35633;
	public static final int GL_FRAGMENT_SHADER = 35632;
	
	public static final int GL_MAX_VERTEX_ATTRIBS = 34921;
	
	public static final int GL_LINK_STATUS = 35714;
	
	public static final int GL_INFO_LOG_LENGTH = 35716;
	
	public static final int GL_ARRAY_BUFFER = 34962;
	
	public static final int GL_STATIC_DRAW = 35044;
	public static final int GL_DYNAMIC_DRAW = 35048;
	
	public static final int GL_NO_ERROR = 0;
	
	public int glGetError();
	
	public void glEnable(int cap);
	public void glDisable(int cap);
	
	public boolean glGetBoolean(int pname);
	public float glGetFloat(int pname);
	public int glGetInteger(int pname);
	
	public void glCullFace(int mode);
	
	public void glViewport(int x, int y, int width, int height);
	
	public void glClearColor(float red, float green, float blue, float alpha);
	public void glClear(int mask);
	
	public int glGenBuffers();
	
	public void glBindBuffer(int target, int buffer);
	
	public void glBufferData(int target, ByteBuffer data, int usage);
	
	public void glBufferData(int target, DoubleBuffer data, int usage);
	
	public void glBufferData(int target, FloatBuffer data, int usage);
	
	public void glBufferData(int target, IntBuffer data, int usage);
	
	public void glBufferData(int target, long data_size, int usage);
	
	public void glBufferData(int target, ShortBuffer data, int usage);
	
	public void glBufferSubData(int target, long offset, java.nio.ByteBuffer data);
	
	public void glEnableVertexAttribArray(int index);
	
	public void glDisableVertexAttribArray(int index);
	
	public void glDeleteBuffers(int buffer);
	
	public void glVertexAttribPointer(int index, int size, int type,
			boolean normalized, int stride, long buffer_buffer_offset);
	
	public void glDrawArrays(int mode, int first, int count);
	
	public int glGenTextures();
	
	public void glActiveTexture(int texture);
	
	public void glBindTexture(int target, int texture);
	
	public void glPixelStorei(int pname, int param);
	
	public void glTexImage2D(int target, int level, int internalformat, int width, int height, int border, int format, int type, java.nio.ByteBuffer pixels);
	
	public void glTexParameter(int target, int pname, FloatBuffer param);
	
	public void glTexParameter(int target, int pname, IntBuffer param);
	
	public void glTexParameterf(int target, int pname, float param);
	
	public void glTexParameteri(int target, int pname, int param);
	
	public void glTexImage3D(int target, int level, int internalFormat,
			int width, int height, int depth, int border, int format, int type,
			ByteBuffer pixels);
	
	public void glDeleteTextures(int texture);
	
	public int glCreateShader(int type);
	public void glDeleteShader(int shader);
	
	public void glShaderSource(int shader, java.nio.ByteBuffer string);
	public void glShaderSource(int shader, java.lang.CharSequence string);
	public void glShaderSource(int shader, java.lang.CharSequence[] strings);
	
	public void glCompileShader(int shader);
	
	public String glGetShaderInfoLog(int shader, int maxLength);
	
	public int glCreateProgram();
	public void glLinkProgram(int program);
	public void glValidateProgram(int program);
	public void glDeleteProgram(int program);
	
	public void glAttachShader(int program, int shader);
	
	public void glBindAttribLocation(int program, int index, java.nio.ByteBuffer name);
	public void glBindAttribLocation(int program, int index, java.lang.CharSequence name);
	
	public int glGetShaderi(int shader, int pname);
	public void glGetShader(int shader, int pname, java.nio.IntBuffer params);
	
	public int glGetProgram(int program, int pname);
	public void glGetProgram(int program, int pname, java.nio.IntBuffer params);
	public void glUseProgram(int program);
	
	public int glGetAttribLocation(int program, java.lang.CharSequence name);
	
	public int glGetUniformLocation(int program, java.lang.CharSequence name);
	
	public void glGetUniform(int program, int location, java.nio.IntBuffer params);
	
	public void glUniform1f(int location, float v0);
	public void glUniform1(int location, java.nio.FloatBuffer value);
	public void glUniform1i(int location, int v0);
	public void glUniform1(int location, java.nio.IntBuffer value);
	public void glUniform2f(int location, float v0, float v1);
	public void glUniform2(int location, java.nio.FloatBuffer value);
	public void glUniform2i(int location, int v0, int v1);
	public void glUniform2(int location, java.nio.IntBuffer value);
	public void glUniform3f(int location, float v0, float v1, float v2);
	public void glUniform3(int location, java.nio.FloatBuffer value);
	public void glUniform3i(int location, int v0, int v1, int v2);
	public void glUniform3(int location, java.nio.IntBuffer value);
	public void glUniform4f(int location, float v0, float v1, float v2, float v3);
	public void glUniform4(int location, java.nio.FloatBuffer value);
	public void glUniform4i(int location, int v0, int v1, int v2, int v3);
	public void glUniform4(int location, java.nio.IntBuffer value);
	
	public void	glUniformMatrix2(int location, boolean transpose, java.nio.FloatBuffer matrices);
	public void	glUniformMatrix3(int location, boolean transpose, java.nio.FloatBuffer matrices);
	public void	glUniformMatrix4(int location, boolean transpose, java.nio.FloatBuffer matrices);
	
	public void glDrawElements(int mode, java.nio.ByteBuffer indices);
	public void glDrawElements(int mode, java.nio.IntBuffer indices);
	public void glDrawElements(int mode, java.nio.ShortBuffer indices);
	public void glDrawElements(int mode, int indices_count, int type, long indices_buffer_offset);
	
	public void glGetBufferParameter(int target, int pname, java.nio.IntBuffer params);
	public int glGetBufferParameteri(int target, int pname);
	
	public void glDepthMask(boolean flag);
	public void glDepthFunc(int func);
	public void glClearDepth(double depth);
	
	public void	glColorMask(boolean red, boolean green, boolean blue, boolean alpha);
	
	public void glBlendFunc(int sfactor, int dfactor);
	
	/**
	 * @post Obtiene una descripción del código de error especificado
	 */
	public String getErrorString(int errorCode);
}
