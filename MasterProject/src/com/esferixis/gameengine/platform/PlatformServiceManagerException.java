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

package com.esferixis.gameengine.platform;

/**
 * Excepción de administrador de servicio
 * 
 * @author Ariel Favio Carrizo
 *
 */
public class PlatformServiceManagerException extends Exception {
	private static final long serialVersionUID = -4782080456598223116L;
	
	/**
	 * @post Crea una excepción sin descripción
	 */
	public PlatformServiceManagerException() {
		super();
	}
	
	/**
	 * @post Crea una excepción con la descripción especificada
	 * @param message
	 */
	public PlatformServiceManagerException(String message) {
		super(message);
	}
	
	/**
	 * @post Crea una excepción con la descripción y la causa especificados
	 * @param message
	 * @param cause
	 */
	public PlatformServiceManagerException(String message, Throwable cause) {
		super(message, cause);
	}
	
	/**
	 * @post Crea una excepción con la causa especificada
	 * @param cause
	 */
	public PlatformServiceManagerException(Throwable cause) {
		super(cause);
	}
}
