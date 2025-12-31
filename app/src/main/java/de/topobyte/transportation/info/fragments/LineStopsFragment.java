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
import android.content.Context;
import android.graphics.Color;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;

import com.wefika.flowlayout.FlowLayout;

import org.openmetromaps.maps.model.Line;
import org.openmetromaps.maps.model.Station;
import org.openmetromaps.maps.model.Stop;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import de.topobyte.android.util.FlexAdapter;
import de.topobyte.transportation.info.Direction;
import de.topobyte.transportation.info.Region;
import de.topobyte.transportation.info.RegionData;
import de.topobyte.transportation.info.RegionDataViewModel;
import de.topobyte.transportation.info.activities.TransportActivity;
import de.topobyte.transportation.info.berlin.R;
import de.topobyte.transportation.info.modelutil.LinesUtil;

public class LineStopsFragment extends Fragment {

  private static final String ARG_REGION = "region";
  private static final String ARG_LINE_ID = "line-id";
  private static final String ARG_LINE_DIR = "line-dir";
  private static final String ARG_STATION_ID = "station-id";

  private int lineId;
  private int stationId;

  private Region region;
  private Line line;
  private Direction direction;
  private Stop stop;

  private TransportActivity activity;

  private RegionDataViewModel vm;

  private ListView list;
  private LineStopsAdapter adapter;
  private List<Stop> stops;

  public static LineStopsFragment newInstance(String regionId, int lineId, Direction direction, int stationId)
  {
    LineStopsFragment fragment = new LineStopsFragment();
    Bundle args = new Bundle();
    args.putString(ARG_REGION, regionId);
    args.putInt(ARG_LINE_ID, lineId);
    args.putString(ARG_LINE_DIR, direction.name());
    args.putInt(ARG_STATION_ID, stationId);
    fragment.setArguments(args);
    return fragment;
  }

  @Override
  public void onCreate(Bundle savedInstanceState)
  {
    super.onCreate(savedInstanceState);
    setHasOptionsMenu(true);

    Bundle args = requireArguments();
    String regionId = args.getString(ARG_REGION, Region.BERLIN.getStringId());
    region = Region.findByStringId(regionId);
    lineId = args.getInt(ARG_LINE_ID);
    direction = Direction.valueOf(args.getString(ARG_LINE_DIR));
    stationId = args.getInt(ARG_STATION_ID);
  }

  @Override
  public void onAttach(Activity activity)
  {
    super.onAttach(activity);
    this.activity = (TransportActivity) activity;
  }

  @Override
  public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState)
  {
    super.onViewCreated(view, savedInstanceState);

    vm = new ViewModelProvider(requireActivity()).get(RegionDataViewModel.class);

    vm.getState().observe(getViewLifecycleOwner(), state -> {
      if (state.status != RegionDataViewModel.Status.READY) return;

      RegionData regionData = state.data;
      for (Line line : regionData.getModel().getData().lines) {
        if (line.getId() == lineId) {
          this.line = line;
          break;
        }
      }
      if (stationId >= 0) {
        Station station = regionData.getModel().getData().stations.get(stationId);
        for (Stop stop : station.getStops()) {
          if (stop.getLine().getId() == lineId) {
            this.stop = stop;
            break;
          }
        }
      }
      init();
    });

    vm.loadIfNeeded(region);
  }

  @Override
  public View onCreateView(LayoutInflater inflater, ViewGroup container,
                           Bundle savedInstanceState)
  {
    View view = inflater.inflate(R.layout.fragment_line_stops, container,
        false);

    list = view.findViewById(R.id.list);

    return view;
  }

  private void init()
  {
    updateToolbar();

    createAdapter();

    if (stop != null) {
      int index = stops.indexOf(stop);
      if (index >= 0) {
        list.setSelection(index);
      }
    }
  }

  private void updateToolbar()
  {
    Region region = vm.getState().getValue().data.getRegion();

    int color = Color.parseColor(line.getColor());
    activity.getToolbar().setBackgroundColor(color);
    activity.getToolbar().setTitle(line.getName());
    activity.getToolbar().setSubtitle(region.getNameStringId());
  }

  private void createAdapter()
  {
    stops = new ArrayList<>(line.getStops());
    if (direction == Direction.BACKWARD) {
      Collections.reverse(stops);
    }

    adapter = new LineStopsAdapter(getActivity(),
        R.layout.row_layout_lines_rich, stops);
    list.setAdapter(adapter);

    list.setOnItemClickListener(new AdapterView.OnItemClickListener() {

      @Override
      public void onItemClick(AdapterView<?> parent, View view,
                              int position, long id)
      {
        Stop stop = stops.get(position);
        activity.showStation(region.getStringId(), stop.getStation());
      }
    });
  }

  @Override
  public void onCreateOptionsMenu(Menu menu, MenuInflater inflater)
  {
    super.onCreateOptionsMenu(menu, inflater);
    inflater.inflate(R.menu.station, menu);
  }

  @Override
  public boolean onOptionsItemSelected(MenuItem item)
  {
    int id = item.getItemId();
    if (id == R.id.action_swap_direction) {
      swapDirection();
      return true;
    }
    return super.onOptionsItemSelected(item);
  }

  private void swapDirection()
  {
    if (!line.isCircular()) {
      if (direction == Direction.FORWARD) {
        direction = Direction.BACKWARD;
      } else if (direction == Direction.BACKWARD) {
        direction = Direction.FORWARD;
      }
    } else {
      // TODO: we don't know about the opposite line of circular lines
      updateToolbar();
    }

    createAdapter();
  }

  private class LineStopsAdapter extends FlexAdapter<Stop> {

    public LineStopsAdapter(Context context, int layout, List<Stop> values)
    {
      super(context, layout, values);
    }

    @Override
    public void fill(View view, Stop stop)
    {
      Line line = stop.getLine();
      Station station = stop.getStation();

      TextView text1 = view.findViewById(R.id.text1);
      FlowLayout linesContainer = view.findViewById(R.id.lines);
      text1.setText(station.getName());

      LinesUtil.setupLinesInLayout(LineStopsFragment.this, linesContainer, stop);
    }

  }

}
