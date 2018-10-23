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
import org.openmetromaps.maps.xml.DesktopXmlModelReader;
import org.openmetromaps.maps.xml.XmlModel;
import org.openmetromaps.maps.xml.XmlModelConverter;

import java.io.BufferedInputStream;
import java.io.DataInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;

import de.topobyte.system.utils.SystemPaths;
import de.topobyte.xml.domabstraction.iface.ParsingException;

public class PrintModel {

    public static void main(String[] args) throws IOException, ParsingException {
        Path pathOMM = SystemPaths.CWD.resolve("app/src/main/assets/model.omm");
        Path pathData = SystemPaths.CWD.resolve("app/src/main/assets/boroughs.data");

        InputStream isModel = Files.newInputStream(pathOMM);
        BufferedInputStream bisModel = new BufferedInputStream(isModel);

        XmlModel xmlModel = DesktopXmlModelReader.read(bisModel);
        MapModel model = new XmlModelConverter().convert(xmlModel);

        InputStream is = Files.newInputStream(pathData);
        DataInputStream dis = new DataInputStream(new BufferedInputStream(is));
        ModelData data = ModelReader.read(dis, model);

        for (Borough borough : data.boroughs) {
            System.out.println(String.format("%d: %s", borough.getLevel(), borough.getName()));
            for (Station station : borough.getStations()) {
                System.out.println(station.getName());
            }
        }
    }

}
