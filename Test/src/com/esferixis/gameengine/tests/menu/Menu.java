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
package com.esferixis.gameengine.tests.menu;

import java.util.InputMismatchException;
import java.util.Scanner;

import com.esferixis.gameengine.platform.PlatformServiceManagerException;
import com.esferixis.misc.ElementProcessor;

/**
 * @author ariel
 *
 */
public final class Menu {
	private final String title;
	private MenuElement[] menuElement;
	
	/**
	 * @pre Ningún parámetro puede ser nulo, y tampoco ningún elemento
	 * 		del menú puede ser nulo
	 * @post Crea un menú con el título y los elementos especificados
	 */
	public Menu(String title, MenuElement[] menuElement) {
		if ( ( title != null ) && ( menuElement != null ) ) {
			this.title = title;
			this.menuElement = menuElement.clone();
			
			for ( MenuElement eachMenuElement : this.menuElement ) {
				if ( eachMenuElement == null ) {
					throw new NullPointerException();
				}
			}
		}
		else {
			throw new NullPointerException();
		}
	}
	
	/**
	 * @pre Ningún parámetro puede ser nulo, y tampoco ningún elemento
	 * 		puede ser nulo
	 * @post Crea un menú con el título, los elementos y el procesador de elementos
	 * 		 especificado
	 */
	public <T> Menu(String title, T[] element, ElementProcessor<T, MenuElement> elementProcessor) {
		if ( ( title != null ) && ( element != null ) && ( elementProcessor != null ) ) {
			this.title = title;
			
			element = element.clone();
			
			this.menuElement = new MenuElement[element.length];
			
			for ( int i = 0 ; i < element.length ; i++ ) {
				final T eachElement = element[i];
				
				if ( eachElement != null ) {
					this.menuElement[i] = elementProcessor.process(eachElement);
				}
				else {
					throw new NullPointerException();
				}
			}
		}
		else {
			throw new NullPointerException();
		}
	}
	
	/**
	 * @post Devuelve el título
	 */
	public String getTitle() {
		return this.title;
	}
	
	/**
	 * @post Ejecuta el menú
	 */
	public void run() {
		final Scanner inScanner = new Scanner(System.in);
		boolean exit = false;
		
		while ( !exit ) {
			System.out.println(this.title);
			
			for ( int i = 0 ; i < this.menuElement.length ; i++ ) {
				System.out.println(i + ". '" + this.menuElement[i].getTitle() + "'");
			}
			System.out.println(this.menuElement.length + ". exit");
			
			System.out.println("Option number: ");
			
			int optionNumber;
			
			try {
				optionNumber = inScanner.nextInt();
			}
			catch ( InputMismatchException e ) {
				optionNumber = -1;
			}
				
			if ( ( optionNumber >= 0 ) && ( optionNumber < this.menuElement.length ) ) {
				this.menuElement[optionNumber].run();
				
				System.out.println();
			}
			else if ( optionNumber == this.menuElement.length ) {
				exit = true;
			}
			else {
				System.out.println("Invalid option number");
			}
		}
	}
}
