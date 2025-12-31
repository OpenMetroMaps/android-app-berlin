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

import static android.view.View.VISIBLE;

import android.app.Activity;
import android.content.Context;
import android.graphics.Color;
import android.os.Bundle;
import android.text.Editable;
import android.util.DisplayMetrics;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;

import com.wefika.flowlayout.FlowLayout;

import org.openmetromaps.maps.MapModel;
import org.openmetromaps.maps.model.Line;
import org.openmetromaps.maps.model.Station;
import org.openmetromaps.maps.model.Stop;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.Locale;

import de.topobyte.android.util.AbstractTextWatcher;
import de.topobyte.android.util.Clearable;
import de.topobyte.android.util.FlexAdapter;
import de.topobyte.transportation.info.BackgroundUtil;
import de.topobyte.transportation.info.Region;
import de.topobyte.transportation.info.RegionDataViewModel;
import de.topobyte.transportation.info.activities.StationsActivity;
import de.topobyte.transportation.info.berlin.R;

public class StationsFragment extends Fragment {

  public static String ARG_REGION = "region";

  private StationsActivity activity;

  private EditText edit;
  private ListView list;

  private RegionDataViewModel vm;
  private Region region;

  @Override
  public void onCreate(@Nullable Bundle savedInstanceState)
  {
    super.onCreate(savedInstanceState);

    Bundle args = requireArguments();
    String regionId = args.getString(ARG_REGION, Region.BERLIN.getStringId());
    region = Region.findByStringId(regionId);
  }

  @Override
  public void onAttach(Activity activity)
  {
    super.onAttach(activity);
    this.activity = (StationsActivity) activity;
  }

  @Override
  public View onCreateView(LayoutInflater inflater, ViewGroup container,
                           Bundle savedInstanceState)
  {
    activity.getToolbar().setTitle(R.string.stations);
    activity.getToolbar().setSubtitle(region.getNameStringId());

    View view = inflater.inflate(R.layout.fragment_stations, container,
        false);

    edit = view.findViewById(R.id.editText);
    list = view.findViewById(R.id.list);

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
          init();
          break;
        case ERROR:
          break;
      }
    });

    vm.loadIfNeeded(region);
  }

  private void init()
  {
    edit.setVisibility(VISIBLE);
    list.setVisibility(VISIBLE);

    Clearable.setClearable(edit);

    list.setOnItemClickListener((parent, view1, position, id) -> {
      Station station = (Station) parent.getItemAtPosition(position);
      activity.showStation(region.getStringId(), station);

      InputMethodManager imm = (InputMethodManager) getActivity().getSystemService(
          Context.INPUT_METHOD_SERVICE);
      imm.hideSoftInputFromWindow(edit.getWindowToken(), 0);
    });

    UpdateTextWatcher textWatcher = new UpdateTextWatcher();
    edit.addTextChangedListener(textWatcher);

    updateListWithQuery(null);
  }

  @Override
  public void onResume()
  {
    super.onResume();

    getActivity().getWindow().setSoftInputMode(
        WindowManager.LayoutParams.SOFT_INPUT_STATE_HIDDEN);
  }

  private class StationsAdapter extends FlexAdapter<Station> {

    public StationsAdapter(Context context, int layout, List<Station> values)
    {
      super(context, layout, values);
    }

    @Override
    public void fill(View view, Station station)
    {
      TextView text1 = view.findViewById(R.id.text1);
      FlowLayout linesContainer = view.findViewById(R.id.lines);
      text1.setText(station.getName());

      linesContainer.removeAllViews();

      List<Line> lines = new ArrayList<>();
      for (Stop stop : station.getStops()) {
        lines.add(stop.getLine());
      }
      Collections.sort(lines, new Comparator<Line>() {
        @Override
        public int compare(Line line1, Line line2)
        {
          return line1.getName().compareTo(line2.getName());
        }
      });

//      int n = lines.size();
//      for (int k = 0; k < 3; k++) {
//        for (int i = 0; i < n; i++) {
//          lines.add(lines.get(i));
//        }
//      }

      DisplayMetrics metrics = getResources().getDisplayMetrics();

      for (Line line : lines) {
        int color = Color.parseColor(line.getColor());

        TextView tv = (TextView) getActivity().getLayoutInflater().inflate(R.layout.linesign, linesContainer, false);

        tv.setText(line.getName());
        BackgroundUtil.setBackground(tv, color, 5, metrics);

        linesContainer.addView(tv);
      }

    }

  }

  private class UpdateTextWatcher extends AbstractTextWatcher {

    @Override
    public void afterTextChanged(Editable s)
    {
      String text = s.toString();
      updateListWithQuery(text);
    }

  }

  protected void updateListWithQuery(String query)
  {
    MapModel model = vm.getState().getValue().data.getModel();

    List<Station> items = new ArrayList<>();
    if (query == null || query.length() == 0) {
      items = model.getData().stations;
    } else {
      String lower = query.toLowerCase(Locale.GERMAN);
      for (Station station : model.getData().stations) {
        if (station.getName().toLowerCase(Locale.GERMAN)
            .contains(lower)) {
          items.add(station);
        }
      }
    }

    StationsAdapter adapter = new StationsAdapter(getActivity(),
        R.layout.row_layout_lines_rich, items);
    list.setAdapter(adapter);
  }

}
