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

package de.topobyte.transportation.info.modelutil;

import android.graphics.Color;
import android.support.v4.app.Fragment;
import android.util.DisplayMetrics;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.wefika.flowlayout.FlowLayout;

import org.openmetromaps.maps.model.Line;
import org.openmetromaps.maps.model.Station;
import org.openmetromaps.maps.model.Stop;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import de.topobyte.transportation.info.BackgroundUtil;
import de.topobyte.transportation.info.berlin.R;

public class LinesUtil {

  public static void setupLinesInLayout(Fragment fragment, FlowLayout linesContainer,
                                        Station station)
  {
    setupLinesInLayout(fragment, linesContainer, station, null);
  }

  public static void setupLinesInLayout(Fragment fragment, FlowLayout linesContainer, Stop stop)
  {
    Station station = stop.getStation();

    setupLinesInLayout(fragment, linesContainer, station, stop);
  }

  public static void setupLinesInLayout(Fragment fragment, FlowLayout linesContainer,
                                        Station station, Stop stop)
  {
    linesContainer.removeAllViews();

    Line line = stop == null ? null : stop.getLine();

    List<Line> lines = new ArrayList<>();
    for (Stop s : station.getStops()) {
      // Ignore the stop that has been passed as parameter, if any
      if (stop == s) {
        continue;
      }
      // Ignore the line of the stop itself
      if (line == s.getLine()) {
        continue;
      }
      // TODO: Ignore the explicit opposite line of the one at the passed stop, if any
      // (i.e. in Berlin, do not show the other Ringbahn at Ringbahn stops).
      lines.add(s.getLine());
    }
    Collections.sort(lines, new Comparator<Line>() {
      @Override
      public int compare(Line line1, Line line2)
      {
        return line1.getName().compareTo(line2.getName());
      }
    });

    linesContainer.setVisibility(lines.size() == 0 ? View.GONE : View.VISIBLE);

    DisplayMetrics metrics = fragment.getResources().getDisplayMetrics();

    for (int i = 0; i < lines.size(); i++) {
      Line l = lines.get(i);
      int color = Color.parseColor(l.getColor());

      TextView tv = (TextView) fragment.getActivity().getLayoutInflater().inflate(
          R.layout.linesign, linesContainer, false);

      ViewGroup.MarginLayoutParams mlp = (ViewGroup.MarginLayoutParams) tv.getLayoutParams();
      if (i == 0) {
        mlp.leftMargin = 0;
      }
      if (i == lines.size() - 1) {
        mlp.rightMargin = 0;
      }

      tv.setText(l.getName());
      BackgroundUtil.setBackground(tv, color, 5, metrics);

      linesContainer.addView(tv);
    }

  }

}
