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

package de.topobyte.transportation.info.activities;

import android.os.Bundle;

import org.openmetromaps.maps.model.Line;
import org.openmetromaps.maps.model.Station;
import org.openmetromaps.maps.model.Stop;

import de.topobyte.transportation.info.Direction;
import de.topobyte.transportation.info.TransportApp;
import de.topobyte.transportation.info.berlin.R;
import de.topobyte.transportation.info.fragments.LineStopsFragment;
import de.topobyte.transportation.info.fragments.StationFragment;

public class TransportActivity extends ToolbarActivity {


  @Override
  protected void onCreate(Bundle savedInstanceState)
  {
    super.onCreate(savedInstanceState);
  }

  public TransportApp getApp()
  {
    return (TransportApp) getApplication();
  }

  public void showLine(String regionId, Line line, Direction direction, Stop stop)
  {
    int stationId = -1;
    if (stop != null) {
      stationId = stop.getStation().getId();
    }
    LineStopsFragment stopsFragment = LineStopsFragment.newInstance(regionId, line.getId(), direction, stationId);
    getSupportFragmentManager().beginTransaction()
        .replace(R.id.container, stopsFragment).addToBackStack(null)
        .commit();
  }

  public void showStation(String regionId, Station station)
  {
    StationFragment stationFragment = StationFragment.newInstance(regionId, station.getId());
    getSupportFragmentManager().beginTransaction()
        .replace(R.id.container, stationFragment).addToBackStack(null)
        .commit();
  }

}
