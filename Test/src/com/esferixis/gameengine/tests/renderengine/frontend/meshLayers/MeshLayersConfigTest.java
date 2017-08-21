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
package com.esferixis.gameengine.tests.renderengine.frontend.meshLayers;

import org.junit.Test;

import com.esferixis.gameengine.renderengine.frontend.meshLayers.ColorLayerDataField;
import com.esferixis.gameengine.renderengine.frontend.meshLayers.MeshLayersConfig;
import com.esferixis.gameengine.renderengine.frontend.meshLayers.SimpleTextureLayer;
import com.esferixis.gameengine.renderengine.frontend.meshLayers.TextureObject2dLayerDataField;
import com.esferixis.gameengine.renderengine.frontend.meshLayers.UniformColoredMeshLayer;
import com.esferixis.gameengine.renderengine.frontend.meshLayers.VertexColoredMeshLayer;
import com.esferixis.gameengine.renderengine.picture.RasterPicture2d;

import org.junit.Assert;

/**
 * @author ariel
 *
 */
public class MeshLayersConfigTest {
	@Test
	public void test1() {
		TextureObject2dLayerDataField field1 = new TextureObject2dLayerDataField();
		TextureObject2dLayerDataField field2 = new TextureObject2dLayerDataField();
		
		Assert.assertNotEquals(field1, field2);
		
		MeshLayersConfig meshLayersConfig1 = new MeshLayersConfig(new SimpleTextureLayer<RasterPicture2d>(field1));
		MeshLayersConfig meshLayersConfig2 = new MeshLayersConfig(new SimpleTextureLayer<RasterPicture2d>(field2));
		
		Assert.assertEquals(meshLayersConfig1, meshLayersConfig2);
		
		Assert.assertEquals(field1, field2);
	}
	
	@Test
	public void test2() {
		MeshLayersConfig meshLayersConfig1 = new MeshLayersConfig(new SimpleTextureLayer<RasterPicture2d>(new TextureObject2dLayerDataField()));
		MeshLayersConfig meshLayersConfig2 = new MeshLayersConfig(new UniformColoredMeshLayer(new ColorLayerDataField()));
		
		Assert.assertNotEquals(meshLayersConfig1, meshLayersConfig2);
	}
	
	@Test
	public void test3() {
		MeshLayersConfig meshLayersConfig1 = new MeshLayersConfig(new SimpleTextureLayer<RasterPicture2d>(new TextureObject2dLayerDataField()));
		MeshLayersConfig meshLayersConfig2 = new MeshLayersConfig(new SimpleTextureLayer<RasterPicture2d>(new TextureObject2dLayerDataField()));
		MeshLayersConfig meshLayersConfig3 = new MeshLayersConfig(new UniformColoredMeshLayer(new ColorLayerDataField()));
		
		Assert.assertEquals(meshLayersConfig1, meshLayersConfig2);
		Assert.assertNotEquals(meshLayersConfig1, meshLayersConfig3);
		Assert.assertNotEquals(meshLayersConfig2, meshLayersConfig3);
	}
	
	@Test
	public void test4() {
		final MeshLayersConfig meshLayersConfig1;
		{
			TextureObject2dLayerDataField dataField1 = new TextureObject2dLayerDataField();
			TextureObject2dLayerDataField dataField2 = new TextureObject2dLayerDataField();
			
			meshLayersConfig1 = new MeshLayersConfig(new SimpleTextureLayer<RasterPicture2d>(dataField1), new SimpleTextureLayer<RasterPicture2d>(dataField2));
		}
		
		final MeshLayersConfig meshLayersConfig2;
		{
			TextureObject2dLayerDataField dataField1 = new TextureObject2dLayerDataField();
			TextureObject2dLayerDataField dataField2 = new TextureObject2dLayerDataField();
			
			meshLayersConfig2 = new MeshLayersConfig(new SimpleTextureLayer<RasterPicture2d>(dataField1), new SimpleTextureLayer<RasterPicture2d>(dataField2));
		}
		
		Assert.assertEquals(meshLayersConfig1, meshLayersConfig2);
	}
	
	@Test
	public void test5() {
		final MeshLayersConfig meshLayersConfig1;
		{
			TextureObject2dLayerDataField dataField = new TextureObject2dLayerDataField();
			
			meshLayersConfig1 = new MeshLayersConfig(new SimpleTextureLayer<RasterPicture2d>(dataField), new SimpleTextureLayer<RasterPicture2d>(dataField));
		}
		
		final MeshLayersConfig meshLayersConfig2;
		{
			TextureObject2dLayerDataField dataField1 = new TextureObject2dLayerDataField();
			TextureObject2dLayerDataField dataField2 = new TextureObject2dLayerDataField();
			
			meshLayersConfig2 = new MeshLayersConfig(new SimpleTextureLayer<RasterPicture2d>(dataField1), new SimpleTextureLayer<RasterPicture2d>(dataField2));
		}
		
		Assert.assertNotEquals(meshLayersConfig1, meshLayersConfig2);
	}
	
	@Test
	public void test6() {
		final MeshLayersConfig meshLayersConfig1;
		{
			TextureObject2dLayerDataField dataField1 = new TextureObject2dLayerDataField();
			TextureObject2dLayerDataField dataField2 = new TextureObject2dLayerDataField();
			
			meshLayersConfig1 = new MeshLayersConfig(new SimpleTextureLayer<RasterPicture2d>(dataField1), new VertexColoredMeshLayer(), new SimpleTextureLayer<RasterPicture2d>(dataField2));
		}
		
		final MeshLayersConfig meshLayersConfig2;
		{
			TextureObject2dLayerDataField dataField1 = new TextureObject2dLayerDataField();
			TextureObject2dLayerDataField dataField2 = new TextureObject2dLayerDataField();
			
			meshLayersConfig2 = new MeshLayersConfig(new SimpleTextureLayer<RasterPicture2d>(dataField1), new VertexColoredMeshLayer(), new SimpleTextureLayer<RasterPicture2d>(dataField2));
		}
		
		Assert.assertEquals(meshLayersConfig1, meshLayersConfig2);
	}
	
	@Test
	public void test7() {
		final MeshLayersConfig meshLayersConfig1;
		{
			TextureObject2dLayerDataField dataField1 = new TextureObject2dLayerDataField();
			TextureObject2dLayerDataField dataField2 = new TextureObject2dLayerDataField();
			
			meshLayersConfig1 = new MeshLayersConfig(new SimpleTextureLayer<RasterPicture2d>(dataField1), new VertexColoredMeshLayer(), new SimpleTextureLayer<RasterPicture2d>(dataField2));
		}
		
		final MeshLayersConfig meshLayersConfig2;
		{
			TextureObject2dLayerDataField dataField1 = new TextureObject2dLayerDataField();
			TextureObject2dLayerDataField dataField2 = new TextureObject2dLayerDataField();
			
			meshLayersConfig2 = new MeshLayersConfig(new VertexColoredMeshLayer(), new SimpleTextureLayer<RasterPicture2d>(dataField1), new SimpleTextureLayer<RasterPicture2d>(dataField2));
		}
		
		Assert.assertNotEquals(meshLayersConfig1, meshLayersConfig2);
	}
}
