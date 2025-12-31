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

package de.topobyte.transportation.info.model;

import com.vividsolutions.jts.geom.Point;

import org.openmetromaps.maps.model.Station;

import java.io.BufferedOutputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import de.topobyte.jts.indexing.GeometryTesselationMap;

public class MappingWriter {

  private Map<Borough, Integer> boroughToIndex = new HashMap<>();
  private Map<Station, Integer> stationToIndex = new HashMap<>();

  public void map(Station station, Point point, GeometryTesselationMap<Borough> tesselation)
  {
    Set<Borough> boroughs = tesselation.test(point);
    for (Borough b : boroughs) {
      b.getStations().add(station);
    }
  }

  private DataOutputStream dos;

  public void write(List<Borough> boroughsList, Path pathOutput) throws IOException
  {
    OutputStream fos = Files.newOutputStream(pathOutput);
    OutputStream bos = new BufferedOutputStream(fos);
    dos = new DataOutputStream(bos);

    writeBoroughs(boroughsList);
    writeBoroughsToStops(boroughsList);

    dos.close();
  }

  private void writeBoroughs(List<Borough> boroughsList) throws IOException
  {
    dos.writeShort(boroughsList.size());
    for (Borough borough : boroughsList) {
      dos.writeByte(borough.getLevel());
      dos.writeUTF(borough.getName());
    }
  }

  private void writeBoroughsToStops(List<Borough> boroughsList) throws IOException
  {
    int n = 0;
    for (Borough borough : boroughsList) {
      n += borough.getStations().size();
    }
    dos.writeShort(n);
    for (Borough borough : boroughsList) {
      int a = boroughToIndex.get(borough);
      Set<Station> stations = borough.getStations();
      for (Station station : stations) {
        int b = stationToIndex.get(station);
        dos.writeShort(a);
        dos.writeShort(b);
      }
    }
  }

  public void putStation(Station station, int index)
  {
    stationToIndex.put(station, index);
  }

  public void putBorough(Borough borough, int index)
  {
    boroughToIndex.put(borough, index);
  }
}
