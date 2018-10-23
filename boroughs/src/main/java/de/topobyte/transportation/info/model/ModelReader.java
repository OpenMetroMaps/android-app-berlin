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

import org.openmetromaps.maps.MapModel;
import org.openmetromaps.maps.model.Station;

import java.io.DataInputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;

public class ModelReader {

    private List<Borough> boroughs = new ArrayList<>();

    private DataInputStream dis;
    private MapModel model;

    public ModelReader(DataInputStream dis, MapModel model) {
        this.dis = dis;
        this.model = model;
    }

    public static ModelData read(DataInputStream dis, MapModel model) throws IOException {
        ModelReader reader = new ModelReader(dis, model);

        reader.read();

        return new ModelData(reader.boroughs);
    }

    private void read() throws IOException {
        readBoroughs();

        readBoroughsToStations();
    }

    private void readBoroughs() throws IOException {
        short n = dis.readShort();
        for (int i = 0; i < n; i++) {
            int level = dis.readByte();
            String name = dis.readUTF();
            boroughs.add(new Borough(i, level, name, new HashSet<>()));
        }
    }

    private void readBoroughsToStations() throws IOException {
        short n = dis.readShort();
        for (int i = 0; i < n; i++) {
            short a = dis.readShort();
            short b = dis.readShort();
            Borough borough = boroughs.get(a);
			Station station = model.getData().stations.get(b);
			borough.getStations().add(station);
        }
    }

}
