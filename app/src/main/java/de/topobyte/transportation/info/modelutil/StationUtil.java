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

import org.openmetromaps.maps.model.Station;
import org.openmetromaps.maps.model.Stop;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

import de.topobyte.adt.geo.Coordinate;
import de.topobyte.opnv.model.Borough;

public class StationUtil {


  public static Set<Borough> getBoroughs(Station station)
  {
    Set<Borough> boroughs = new HashSet<>();
    for (Stop s : station.getStops()) {
      // TODO: reimplement boroughs
//      boroughs.addAll(s.getBoroughs());
    }
    return boroughs;
  }

  public static Coordinate getLocation(Station station)
  {
    List<Stop> stops = station.getStops();

    double lon = 0;
    double lat = 0;
    for (Stop stop : stops) {
      lon += stop.getLocation().getLongitude();
      lat += stop.getLocation().getLatitude();
    }
    lon /= stops.size();
    lat /= stops.size();

    return new Coordinate(lon, lat);
  }

}
