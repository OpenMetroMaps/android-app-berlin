// Copyright 2018 Sebastian Kuerten
//
// This file is part of android-app-berlin.
//
// android-app-berlin is free software: you can redistribute it and/or modify
// it under the terms of the GNU Lesser General Public License as published by
// the Free Software Foundation, either version 3 of the License, or
// (at your option) any later version.
//
// android-app-berlin is distributed in the hope that it will be useful,
// but WITHOUT ANY WARRANTY; without even the implied warranty of
// MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
// GNU Lesser General Public License for more details.
//
// You should have received a copy of the GNU Lesser General Public License
// along with android-app-berlin. If not, see <http://www.gnu.org/licenses/>.

package de.topobyte.transportation.info;

import androidx.core.graphics.ColorUtils;

public class ColorUtil {

  public static int highlightColor(int color)
  {
    float[] hsl = new float[3];
    ColorUtils.colorToHSL(color, hsl);
    float v = hsl[2];
    if (v <= 0.5) {
      v = Math.min(1, v * 1.3f);
    } else {
      v = Math.max(0, v * 0.75f);
    }
    hsl[2] = v;
    return ColorUtils.HSLToColor(hsl);
  }

}
