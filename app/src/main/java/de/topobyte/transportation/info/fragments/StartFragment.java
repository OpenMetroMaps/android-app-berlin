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

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import org.openmetromaps.maps.MapModel;

import de.topobyte.android.util.onclick.ClickDelegate;
import de.topobyte.android.util.onclick.ClickListener;
import de.topobyte.transportation.info.ModelLoader;
import de.topobyte.transportation.info.activities.AboutActivity;
import de.topobyte.transportation.info.activities.LinesActivity;
import de.topobyte.transportation.info.activities.NetworkMapActivity;
import de.topobyte.transportation.info.activities.StationsActivity;
import de.topobyte.transportation.info.berlin.R;

public class StartFragment extends Fragment implements ClickDelegate {

  public StartFragment()
  {
  }

  private static final int ID_LINES = 0;
  private static final int ID_STATIONS = 1;
  private static final int ID_ABOUT = 2;
  private static final int ID_SCHEMATIC_MAP = 3;
  private static final int ID_GEOGRAPHIC_MAP = 4;

  @Override
  public View onCreateView(LayoutInflater inflater, ViewGroup container,
                           Bundle savedInstanceState)
  {
    View view = inflater.inflate(R.layout.fragment_main, container, false);

    MapModel model = ModelLoader.loadSafe(getActivity());

    view.findViewById(R.id.buttonNetworkMapSchematic).setOnClickListener(
        new ClickListener(this, ID_SCHEMATIC_MAP));
    view.findViewById(R.id.buttonNetworkMapGeographic).setOnClickListener(
        new ClickListener(this, ID_GEOGRAPHIC_MAP));
    view.findViewById(R.id.buttonLines).setOnClickListener(
        new ClickListener(this, ID_LINES));
    view.findViewById(R.id.buttonStations).setOnClickListener(
        new ClickListener(this, ID_STATIONS));
    view.findViewById(R.id.buttonInfo).setOnClickListener(
        new ClickListener(this, ID_ABOUT));

    return view;
  }

  @Override
  public void onClick(int id)
  {
    switch (id) {
      case ID_SCHEMATIC_MAP: {
        Intent intent = new Intent(getActivity(), NetworkMapActivity.class);
        intent.putExtra(NetworkMapActivity.EXTRA_TITLE, getString(R.string.schematic_map));
        intent.putExtra(NetworkMapActivity.EXTRA_VIEW_INDEX, 0);
        startActivity(intent);
        break;
      }
      case ID_GEOGRAPHIC_MAP: {
        Intent intent = new Intent(getActivity(), NetworkMapActivity.class);
        intent.putExtra(NetworkMapActivity.EXTRA_TITLE, getString(R.string.geographic_map));
        intent.putExtra(NetworkMapActivity.EXTRA_VIEW_INDEX, 1);
        startActivity(intent);
        break;
      }
      case ID_LINES:
        startActivity(new Intent(getActivity(), LinesActivity.class));
        break;
      case ID_STATIONS:
        startActivity(new Intent(getActivity(), StationsActivity.class));
        break;
      case ID_ABOUT:
        startActivity(new Intent(getActivity(), AboutActivity.class));
        break;
    }
  }
}