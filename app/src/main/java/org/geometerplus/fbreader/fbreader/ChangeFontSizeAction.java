/*
 * Copyright (C) 2007-2015 FBReader.ORG Limited <contact@fbreader.org>
 *
 * This program is free software; you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation; either version 2 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program; if not, write to the Free Software
 * Foundation, Inc., 51 Franklin Street, Fifth Floor, Boston, MA
 * 02110-1301, USA.
 */

package org.geometerplus.fbreader.fbreader;

import com.iyangcong.reader.utils.SharedPreferenceUtil;

import org.geometerplus.zlibrary.core.options.ZLIntegerRangeOption;

class ChangeFontSizeAction extends FBAction {
//	private final int myDelta;

    private SharedPreferenceUtil sharedPreferenceUtil;

    ChangeFontSizeAction(FBReaderApp fbreader/*, int delta*/) {
        super(fbreader);
        sharedPreferenceUtil = SharedPreferenceUtil.getInstance();
//		myDelta = delta;
    }

    @Override
    protected void run(Object... params) {
        final ZLIntegerRangeOption option =
                Reader.ViewOptions.getTextStyleCollection().getBaseStyle().FontSizeOption;
        option.setValue(/*option.getValue() + */sharedPreferenceUtil.getInt(SharedPreferenceUtil.CURRENT_FONT_SIZE, 46));
        Reader.clearTextCaches();
        Reader.getViewWidget().repaint();
    }
}
