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
import de.topobyte.transportation.info.model.Borough;
import de.topobyte.transportation.info.model.ModelData;

public class StationUtil {


  public static Set<Borough> getBoroughs(Station station, ModelData data)
  {
    // TODO: make this more efficient by building an index in the TransportApp class
    Set<Borough> boroughs = new HashSet<>();
    for (Borough borough : data.boroughs) {
      if (borough.getStations().contains(station)) {
        boroughs.add(borough);
      }
    }
    return boroughs;
  }

  public static Coordinate getLocation(Station station)
  {
    List<Stop> stops = station.getStops();

    double lon = station.getLocation().getLongitude();
    double lat = station.getLocation().getLatitude();
    int num = 1;
    for (Stop stop : stops) {
      if (stop.getLocation() != null) {
        lon += stop.getLocation().getLongitude();
        lat += stop.getLocation().getLatitude();
        num += 1;
      }
    }
    lon /= num;
    lat /= num;

    return new Coordinate(lon, lat);
  }

}
