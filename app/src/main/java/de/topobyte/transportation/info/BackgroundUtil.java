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

import android.graphics.drawable.GradientDrawable;
import android.graphics.drawable.StateListDrawable;
import android.util.DisplayMetrics;
import android.util.StateSet;
import android.view.View;

public class BackgroundUtil {

  public static void setBackground(View view, int color, float radius, DisplayMetrics metrics)
  {
    GradientDrawable gd = createGradientDrawable(color, radius, metrics);
    view.setBackgroundDrawable(gd);
  }

  public static void setBackground(View view, int color1, int color2, float radius,
                                   DisplayMetrics metrics)
  {
    StateListDrawable sld = new StateListDrawable();

    GradientDrawable gd1 = createGradientDrawable(color1, radius, metrics);
    GradientDrawable gd2 = createGradientDrawable(color2, radius, metrics);

    sld.addState(new int[]{android.R.attr.state_pressed}, gd2);
    sld.addState(StateSet.WILD_CARD, gd1);

    view.setBackgroundDrawable(sld);
  }

  private static GradientDrawable createGradientDrawable(int color, float radius, DisplayMetrics metrics)
  {
    GradientDrawable gd = new GradientDrawable();
    gd.setColor(color);
    gd.setCornerRadius(metrics.density * 5);
    return gd;
  }

}
