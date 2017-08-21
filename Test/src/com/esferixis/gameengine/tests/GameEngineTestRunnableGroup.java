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
package com.esferixis.gameengine.tests;

import java.util.InputMismatchException;
import java.util.Scanner;

import com.esferixis.gameengine.platform.PlatformServiceManager;
import com.esferixis.gameengine.platform.PlatformServiceManagerException;
import com.esferixis.gameengine.tests.menu.Menu;
import com.esferixis.gameengine.tests.menu.MenuElement;
import com.esferixis.misc.ElementProcessor;

/**
 * @author ariel
 *
 */
public final class GameEngineTestRunnableGroup extends GameEngineTestRunnable {
	private final GameEngineTestRunnable[] gameEngineTestRunnables;
	
	/**
	 * @pre El array de runnables no pueden ser nulos
	 * @post Crea el grupo con el tìtulo y los runnables especificados
	 */
	public GameEngineTestRunnableGroup(String title, GameEngineTestRunnable... gameEngineTestRunnables) {
		super(title);
		
		if ( gameEngineTestRunnables != null ) {
			this.gameEngineTestRunnables = gameEngineTestRunnables.clone();
		}
		else {
			throw new NullPointerException();
		}
	}

	/* (non-Javadoc)
	 * @see com.esferixis.gameengine.tests.GameEngineTestRunnable#run(com.esferixis.gameengine.platform.PlatformServiceManager)
	 */
	@Override
	public void run(final PlatformServiceManager platformServiceManager) {
		new Menu(this.getTitle(), this.gameEngineTestRunnables, new ElementProcessor<GameEngineTestRunnable, MenuElement>(){

			@Override
			public MenuElement process(final GameEngineTestRunnable element) {
				return new MenuElement(element.getTitle()) {
					@Override
					public void run() {
						element.run(platformServiceManager);
						
						try {
							platformServiceManager.getDisplayManager().setScreenConfig(null);
						} catch (PlatformServiceManagerException e) {
							throw new RuntimeException(e);
						}
					}
				};
			}
			
		}).run();
	}
}
