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

package com.esferixis.gameengine.platform.input;

import java.util.Map;

/**
 * Teclado
 * 
 * @author Ariel Favio Carrizo
 *
 */
public abstract class Keyboard {
	public static enum Key {
		KEY_0, KEY_1, KEY_2, KEY_3, KEY_4, KEY_5, KEY_6, KEY_7, KEY_8, KEY_9,
		KEY_A, KEY_ADD, KEY_APOSTROPHE, KEY_APPS, KEY_AT, KEY_AX, KEY_B, KEY_BACK, KEY_BACKSLASH, KEY_C, KEY_CAPITAL,
		KEY_CIRCUMFLEX, KEY_COLON, KEY_COMMA, KEY_CONVERT, KEY_D, KEY_DECIMAL, KEY_DELETE, KEY_DIVIDE, KEY_DOWN,
		KEY_E, KEY_END, KEY_EQUALS, KEY_ESCAPE, KEY_F, KEY_F1, KEY_F10, KEY_F11, KEY_F12, KEY_F13, KEY_F14, KEY_F15,
		KEY_F2, KEY_F3, KEY_F4, KEY_F5, KEY_F6, KEY_F7, KEY_F8, KEY_F9, KEY_G, KEY_GRAVE, KEY_H, KEY_HOME, KEY_I,
		KEY_INSERT, KEY_J, KEY_K, KEY_KANA, KEY_KANJI, KEY_L, KEY_LBRACKET, KEY_LCONTROL, KEY_LEFT, KEY_LMENU, KEY_LMETA, 
		KEY_LSHIFT, KEY_LWIN, KEY_M, KEY_MINUS, KEY_MULTIPLY, KEY_N, KEY_NEXT, KEY_NOCONVERT, KEY_NONE, KEY_NUMLOCK,
		KEY_NUMPAD0, KEY_NUMPAD1, KEY_NUMPAD2, KEY_NUMPAD3, KEY_NUMPAD4, KEY_NUMPAD5, KEY_NUMPAD6, KEY_NUMPAD7, KEY_NUMPAD8,
		KEY_NUMPAD9, KEY_NUMPADCOMMA, KEY_NUMPADENTER, KEY_NUMPADEQUALS, KEY_O, KEY_P, KEY_PAUSE, KEY_PERIOD, KEY_POWER,
		KEY_PRIOR, KEY_Q, KEY_R, KEY_RBRACKET, KEY_RCONTROL, KEY_RETURN, KEY_RIGHT, KEY_RMENU, KEY_RMETA, KEY_RSHIFT, KEY_RWIN,
		KEY_S, KEY_SCROLL, KEY_SEMICOLON,	KEY_SLASH, KEY_SLEEP, KEY_SPACE, KEY_STOP, KEY_SUBTRACT, KEY_SYSRQ, KEY_T, KEY_TAB,
		KEY_U, KEY_UNDERLINE, KEY_UNLABELED, KEY_UP, KEY_V, KEY_W, KEY_X, KEY_Y, KEY_YEN, KEY_Z
	};
	
	/**
	 * @post Devuelve el mapeo de teclas
	 */
	public abstract Map<Key, Button> getMapping();
}