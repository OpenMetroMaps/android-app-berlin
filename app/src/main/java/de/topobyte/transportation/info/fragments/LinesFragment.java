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

package de.topobyte.transportation.info.fragments;

import android.app.Activity;
import android.graphics.Color;
import android.os.Bundle;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.Display;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.TextView;

import androidx.fragment.app.Fragment;

import org.openmetromaps.maps.MapModel;
import org.openmetromaps.maps.model.Line;

import java.util.List;

import de.topobyte.android.FlexGridLayout;
import de.topobyte.transportation.info.BackgroundUtil;
import de.topobyte.transportation.info.ColorUtil;
import de.topobyte.transportation.info.Direction;
import de.topobyte.transportation.info.activities.LinesActivity;
import de.topobyte.transportation.info.berlin.R;

public class LinesFragment extends Fragment {
  private LinesActivity activity;

  @Override
  public void onAttach(Activity activity)
  {
    super.onAttach(activity);
    this.activity = (LinesActivity) activity;
  }

  @Override
  public View onCreateView(LayoutInflater inflater, ViewGroup container,
                           Bundle savedInstanceState)
  {
    View view = inflater.inflate(R.layout.fragment_lines, container, false);

    WindowManager windowManager = activity.getWindowManager();
    Display display = windowManager.getDefaultDisplay();
    int width = display.getWidth();
    int height = display.getHeight();
    Log.i("display", "size: " + width + " x " + height);

    TextView text1 = view.findViewById(R.id.text1);
    TextView text2 = view.findViewById(R.id.text2);
    FlexGridLayout flex1 = view.findViewById(R.id.flex1);
    FlexGridLayout flex2 = view.findViewById(R.id.flex2);

    MapModel model = activity.getApp().getModel();
    final List<Line> lines = model.getData().lines;

    activity.getToolbar().setTitle(R.string.lines);
    activity.getToolbar().setSubtitle(null);

    LayoutInflater li = getActivity().getLayoutInflater();

    for (final Line line : lines) {
      FlexGridLayout flex = flex1;
      if (line.getName().startsWith("U")) {
        flex = flex2;
      }

      DisplayMetrics metrics = getResources().getDisplayMetrics();

      TextView text = (TextView) li.inflate(R.layout.linebutton, flex, false);
      text.setText(line.getName());
      text.setClickable(true);
      flex.addView(text);

      int color = Color.parseColor(line.getColor());
      int color2 = ColorUtil.highlightColor(color);
      BackgroundUtil.setBackground(text, color, color2, 5, metrics);

      text.setOnClickListener(new View.OnClickListener() {
        @Override
        public void onClick(View view)
        {
          activity.showLine(line, Direction.FORWARD, null);
        }
      });
    }

    return view;
  }

}
