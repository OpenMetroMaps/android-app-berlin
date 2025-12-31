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
import android.view.View;
import android.view.ViewGroup;

import androidx.fragment.app.Fragment;

import de.topobyte.android.util.onclick.ClickDelegate;
import de.topobyte.android.util.onclick.ClickListener;
import de.topobyte.transportation.info.Region;
import de.topobyte.transportation.info.activities.AboutActivity;
import de.topobyte.transportation.info.activities.TransportActivity;
import de.topobyte.transportation.info.berlin.R;

public class StartFragment extends Fragment implements ClickDelegate {

  public StartFragment()
  {
  }

  private static final int ID_ABOUT = 1002;

  @Override
  public View onCreateView(LayoutInflater inflater, ViewGroup container,
                           Bundle savedInstanceState)
  {
    TransportActivity activity = (TransportActivity) requireActivity();

    activity.getToolbar().setTitle(R.string.app_name);
    activity.getToolbar().setSubtitle(null);
    activity.getSupportActionBar().setDisplayHomeAsUpEnabled(false);

    View view = inflater.inflate(R.layout.fragment_start, container, false);

    view.findViewById(R.id.buttonBerlin).setOnClickListener(
        new ClickListener(this, Region.BERLIN.getIntId()));
    view.findViewById(R.id.buttonVienna).setOnClickListener(
        new ClickListener(this, Region.VIENNA.getIntId()));
    view.findViewById(R.id.buttonInfo).setOnClickListener(
        new ClickListener(this, ID_ABOUT));

    return view;
  }

  @Override
  public void onClick(int id)
  {
    if (id == ID_ABOUT) {
      startActivity(new Intent(getActivity(), AboutActivity.class));
    } else {
      Bundle args = new Bundle();
      args.putInt(RegionFragment.ARG_REGION, id);

      RegionFragment fragment = new RegionFragment();
      fragment.setArguments(args);

      requireActivity().getSupportFragmentManager().beginTransaction().addToBackStack("region")
          .replace(R.id.container, fragment).commit();
    }
  }
}
