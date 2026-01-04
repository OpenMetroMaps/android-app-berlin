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
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import de.topobyte.android.util.onclick.ClickDelegate;
import de.topobyte.android.util.onclick.ClickListener;
import de.topobyte.transportation.info.Region;
import de.topobyte.transportation.info.activities.AboutActivity;
import de.topobyte.transportation.info.activities.LinesActivity;
import de.topobyte.transportation.info.activities.NetworkMapActivity;
import de.topobyte.transportation.info.activities.StationsActivity;
import de.topobyte.transportation.info.activities.TransportActivity;
import de.topobyte.transportation.info.berlin.R;

public class RegionFragment extends Fragment implements ClickDelegate {

  public static String ARG_REGION = "region";

  public RegionFragment()
  {
  }

  private static final int ID_LINES = 0;
  private static final int ID_STATIONS = 1;
  private static final int ID_ABOUT = 2;
  private static final int ID_SCHEMATIC_MAP = 3;
  private static final int ID_GEOGRAPHIC_MAP = 4;

  private int regionId = 0;
  private Region region = null;

  @Override
  public void onCreate(@Nullable Bundle savedInstanceState)
  {
    super.onCreate(savedInstanceState);
    setHasOptionsMenu(true);
  }

  @Override
  public View onCreateView(LayoutInflater inflater, ViewGroup container,
                           Bundle savedInstanceState)
  {
    Bundle args = getArguments();
    if (args != null) {
      regionId = args.getInt(ARG_REGION, 0);
    }

    this.region = Region.findByIntId(regionId);

    TransportActivity activity = (TransportActivity) requireActivity();

    activity.getToolbar().setTitle(R.string.app_name);
    activity.getToolbar().setSubtitle(region.getNameStringId());
    activity.getSupportActionBar().setDisplayHomeAsUpEnabled(true);

    View view = inflater.inflate(R.layout.fragment_region, container, false);

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
        intent.putExtra(NetworkMapActivity.EXTRA_REGION, region.getStringId());
        intent.putExtra(NetworkMapActivity.EXTRA_TITLE, getString(R.string.schematic_map));
        intent.putExtra(NetworkMapActivity.EXTRA_VIEW_INDEX, 0);
        startActivity(intent);
        break;
      }
      case ID_GEOGRAPHIC_MAP: {
        Intent intent = new Intent(getActivity(), NetworkMapActivity.class);
        intent.putExtra(NetworkMapActivity.EXTRA_REGION, region.getStringId());
        intent.putExtra(NetworkMapActivity.EXTRA_TITLE, getString(R.string.geographic_map));
        intent.putExtra(NetworkMapActivity.EXTRA_VIEW_INDEX, 1);
        startActivity(intent);
        break;
      }
      case ID_LINES: {
        Intent intent = new Intent(getActivity(), LinesActivity.class);
        intent.putExtra(LinesActivity.EXTRA_REGION, region.getStringId());
        startActivity(intent);
        break;
      }
      case ID_STATIONS: {
        Intent intent = new Intent(getActivity(), StationsActivity.class);
        intent.putExtra(StationsActivity.EXTRA_REGION, region.getStringId());
        startActivity(intent);
        break;
      }
      case ID_ABOUT: {
        startActivity(new Intent(getActivity(), AboutActivity.class));
        break;
      }
    }
  }

  @Override
  public boolean onOptionsItemSelected(MenuItem item)
  {
    int id = item.getItemId();
    if (id == android.R.id.home) {
      requireActivity()
          .getSupportFragmentManager()
          .popBackStack();
      return true;
    }
    return super.onOptionsItemSelected(item);
  }
}
