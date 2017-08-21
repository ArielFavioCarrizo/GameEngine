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

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import com.esferixis.gameengine.renderengine.misc.colorObject.ColorObject;
import com.esferixis.math.Vector4f;

/**
 * @author ariel
 *
 */
public abstract class MeshLayer<L extends LayerVertexData> implements Serializable {
	/**
	 * 
	 */
	private static final long serialVersionUID = 643351236137435828L;

	private final Class<L> layerDataClass;
	
	public interface Visitor<R, T extends Throwable> {
		public R visit(SimpleTextureLayer<?> layer) throws T;
		public R visit(VertexColoredMeshLayer layer) throws T;
		public R visit(UniformColoredMeshLayer layer) throws T;
	}
	
	private final List<LayerDataField<?>> dataFields;
	
	private final ColorLayerDataField maskColorField;
	
	private MeshLayersConfig meshLayersConfig;
	
	/**
	 * @pre La clase de mapeado de capa no puede ser nula
	 * @post Crea la capa de malla con la clase de mapeado de capa, indicando el campo
	 * 		 de color especificado
	 */
	MeshLayer(Class<L> layerDataClass, ColorLayerDataField maskColorField) {
		if ( layerDataClass != null ) {
			this.layerDataClass = layerDataClass;
			this.dataFields = new ArrayList<LayerDataField<?>>();
			this.maskColorField = maskColorField;
			
			if ( this.maskColorField != null ) {
				this.addDataField(maskColorField);
			}
		}
		else {
			throw new NullPointerException();
		}
	}
	
	/**
	 * @post Agrega un atributo
	 */
	protected void addDataField(LayerDataField<?> dataField) {
		if ( dataField != null ) {
			this.dataFields.add(dataField);
		}
		else {
			throw new NullPointerException();
		}
	}
	
	/**
	 * @post Devuelve los campos (Sólo lectura)
	 */
	List<LayerDataField<?>> getDataFields() {
		return Collections.unmodifiableList(this.dataFields);
	}
	
	/**
	 * @pre La capa no puede ser nula y no tiene que estar asignada a ninguna configuración de capa
	 * @post Asigna la configuración de capas especificada a la configuración
	 * 		 de capas especificada
	 */
	protected final void attachMeshLayersConfig(MeshLayersConfig meshLayersConfig) {
		if ( meshLayersConfig != null ) {
			if ( this.meshLayersConfig == null ) {
				final List<LayerDataField<?>> attachedDataFields = new ArrayList<LayerDataField<?>>(this.dataFields.size());
				
				for ( LayerDataField<?> eachDataField : this.dataFields ) {
					attachedDataFields.add(eachDataField);
					eachDataField.attachMeshLayersConfig(meshLayersConfig);
				}
				
				this.meshLayersConfig = meshLayersConfig;
			}
			else {
				throw new IllegalStateException("Cannot use an used layer");
			}
		}
		else {
			throw new NullPointerException();
		}
	}
	
	/**
	 * @post Desasigna la configuración de capas especificada a la configuración
	 * 		 de capas especificada
	 */
	protected final void detachMeshLayersConfig() {
		this.meshLayersConfig = null;
	}
	
	/**
	 * @post Verifica que el mapeado de capa sea válido
	 */
	@SuppressWarnings("unchecked")
	public final void checkLayerMapping(LayerVertexData layerMapping) {
		if ( layerMapping != null ) {
			if ( layerDataClass.isInstance(layerMapping) ) {
				this.checkLayerMapping_internal( (L) layerMapping);
			}
			else {
				throw new ClassCastException("Invalid layer mapping class");
			}
		}
		else {
			throw new NullPointerException();
		}
	}
	
	/**
	 * @post Verifica que el mapeado de capa sea válido (Específico)
	 */
	protected void checkLayerMapping_internal(L layerMapping) {
		
	}
	
	/**
	 * @post Devuelve el campo de color de máscara
	 */
	public final LayerDataField<ColorObject> getMaskColorField() {
		return this.maskColorField;
	}
	
	/**
	 * @post Devuelve el hash
	 */
	@Override
	public int hashCode() {
		int hash = this.getClass().hashCode();
		
		if ( this.maskColorField != null ) {
			hash += 31 * this.maskColorField.hashCode();
		}
		
		return hash;
	}
	
	/**
	 * @pre No puede ser nulo
	 * @post Devuelve si es igual al objeto especificado,
	 * 		 sin tener en cuenta el orden
	 */
	protected boolean fuzzyEquals(MeshLayer<?> other) {
		if ( other != null ) {
			if ( this.maskColorField == null ) {
				return ( other.getMaskColorField() == null );
			}
			else {
				return ( ( other.getMaskColorField() != null ) && other.getMaskColorField().fuzzyEquals(this.getMaskColorField()) );
			}
		}
		else {
			throw new NullPointerException();
		}
	}
	
	/**
	 * @post Devuelve si es igual al objeto especificado.
	 * 		 Si no ha sido asociada, sólo es igual si se trata
	 * 		 de la misma instancia
	 */
	@Override
	public boolean equals(Object other) {
		if ( this.meshLayersConfig != null ) {
			if ( ( other != null ) && other.getClass().equals(this.getClass()) ) {
				MeshLayer<?> otherLayer = (MeshLayer<?>) other;
				
				if ( this.meshLayersConfig.equals(otherLayer.meshLayersConfig) ) {
					return this.fuzzyEquals( (MeshLayer<?>) other);
				}
				else {
					return false;
				}
			}
			else {
				return false;
			}
		}
		else {
			return (other == this);
		}
	}
	
	/**
	 * @post Visita con el visitor especificado
	 */
	public abstract <V, T extends Throwable> V accept(Visitor<V, T> visitor) throws T;
}
