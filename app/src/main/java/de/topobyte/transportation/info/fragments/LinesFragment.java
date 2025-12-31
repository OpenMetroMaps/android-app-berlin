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

import static android.view.View.GONE;
import static android.view.View.VISIBLE;

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

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;

import org.openmetromaps.maps.MapModel;
import org.openmetromaps.maps.model.Line;

import java.util.ArrayList;
import java.util.List;

import de.topobyte.android.FlexGridLayout;
import de.topobyte.transportation.info.BackgroundUtil;
import de.topobyte.transportation.info.ColorUtil;
import de.topobyte.transportation.info.Direction;
import de.topobyte.transportation.info.Region;
import de.topobyte.transportation.info.RegionData;
import de.topobyte.transportation.info.RegionDataViewModel;
import de.topobyte.transportation.info.activities.LinesActivity;
import de.topobyte.transportation.info.berlin.R;

public class LinesFragment extends Fragment {

  public static String ARG_REGION = "region";

  private LinesActivity activity;

  private RegionDataViewModel vm;
  private Region region;
  private TextView text1;
  private TextView text2;
  private TextView text3;
  private FlexGridLayout flex1;
  private FlexGridLayout flex2;
  private FlexGridLayout flex3;

  @Override
  public void onAttach(Activity activity)
  {
    super.onAttach(activity);
    this.activity = (LinesActivity) activity;
  }

  @Override
  public void onCreate(@Nullable Bundle savedInstanceState)
  {
    super.onCreate(savedInstanceState);

    Bundle args = requireArguments();
    String regionId = args.getString(ARG_REGION, Region.BERLIN.getStringId());
    region = Region.findByStringId(regionId);
  }

  @Override
  public View onCreateView(LayoutInflater inflater, ViewGroup container,
                           Bundle savedInstanceState)
  {
    activity.getToolbar().setBackgroundColor(getResources().getColor(R.color.toolbar_background));

    View view = inflater.inflate(R.layout.fragment_lines, container, false);

    text1 = view.findViewById(R.id.text1);
    text2 = view.findViewById(R.id.text2);
    text3 = view.findViewById(R.id.text3);
    flex1 = view.findViewById(R.id.flex1);
    flex2 = view.findViewById(R.id.flex2);
    flex3 = view.findViewById(R.id.flex3);

    return view;
  }

  @Override
  public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState)
  {
    super.onViewCreated(view, savedInstanceState);

    vm = new ViewModelProvider(requireActivity()).get(RegionDataViewModel.class);

    vm.getState().observe(getViewLifecycleOwner(), state -> {
      switch (state.status) {
        case IDLE:
          break;
        case LOADING:
          break;
        case READY:
          init(state.data);
          break;
        case ERROR:
          break;
      }
    });

    vm.loadIfNeeded(region);
  }

  private void init(RegionData data)
  {
    WindowManager windowManager = activity.getWindowManager();
    Display display = windowManager.getDefaultDisplay();
    int width = display.getWidth();
    int height = display.getHeight();
    Log.i("display", "size: " + width + " x " + height);

    MapModel model = data.getModel();
    final List<Line> lines = model.getData().lines;

    activity.getToolbar().setTitle(R.string.lines);
    activity.getToolbar().setSubtitle(region.getNameStringId());

    LayoutInflater li = getActivity().getLayoutInflater();

    List<Line> sbahn = new ArrayList<>();
    List<Line> ubahn = new ArrayList<>();
    List<Line> other = new ArrayList<>();

    for (final Line line : lines) {
      if (line.getName().startsWith("S")) {
        sbahn.add(line);
      } else if (line.getName().startsWith("U")) {
        ubahn.add(line);
      } else {
        other.add(line);
      }
    }

    add(sbahn, li, flex1);
    add(ubahn, li, flex2);
    add(other, li, flex3);

    setVisibility(text1, flex1, !sbahn.isEmpty());
    setVisibility(text2, flex2, !ubahn.isEmpty());
    setVisibility(text3, flex3, !other.isEmpty());
  }

  private void setVisibility(View view1, View view2, boolean visible)
  {
    if (visible) {
      view1.setVisibility(VISIBLE);
      view2.setVisibility(VISIBLE);
    } else {
      view1.setVisibility(GONE);
      view2.setVisibility(GONE);
    }
  }

  private void add(List<Line> lines, LayoutInflater li, FlexGridLayout flex)
  {
    for (Line line : lines) {
      DisplayMetrics metrics = getResources().getDisplayMetrics();

      TextView text = (TextView) li.inflate(R.layout.linebutton, flex, false);
      text.setText(line.getName());
      text.setClickable(true);
      flex.addView(text);

      int color = Color.parseColor(line.getColor());
      int color2 = ColorUtil.highlightColor(color);
      BackgroundUtil.setBackground(text, color, color2, 5, metrics);

      text.setOnClickListener(view -> activity.showLine(region.getStringId(), line, Direction.FORWARD, null));
    }
  }

}
