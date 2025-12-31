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

import com.vividsolutions.jts.geom.Coordinate;
import com.vividsolutions.jts.geom.Geometry;
import com.vividsolutions.jts.geom.GeometryFactory;
import com.vividsolutions.jts.geom.Point;
import com.vividsolutions.jts.io.ParseException;
import com.vividsolutions.jts.io.WKBReader;

import org.openmetromaps.maps.MapModel;
import org.openmetromaps.maps.model.Station;
import org.openmetromaps.maps.xml.DesktopXmlModelReader;
import org.openmetromaps.maps.xml.XmlModel;
import org.openmetromaps.maps.xml.XmlModelConverter;

import java.io.BufferedInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;

import de.topobyte.jts.indexing.GeometryTesselationMap;
import de.topobyte.melon.paths.PathUtil;
import de.topobyte.system.utils.SystemPaths;
import de.topobyte.xml.domabstraction.iface.ParsingException;

public class BuildMappingVienna {

    public static void main(String[] args) throws IOException, ParsingException, ParseException {
        BuildMappingVienna task = new BuildMappingVienna();
        task.execute();
    }

    private List<Borough> boroughsList = new ArrayList<>();

    public void execute() throws IOException, ParseException, ParsingException {
        Path pathOMM = SystemPaths.CWD.resolve("app/src/main/assets/vienna/model.omm");
        Path pathData = SystemPaths.CWD.resolve("app/src/main/assets/vienna/boroughs.data");

        System.out.println(pathOMM);

        InputStream is = Files.newInputStream(pathOMM);
        BufferedInputStream bis = new BufferedInputStream(is);

        XmlModel xmlModel = DesktopXmlModelReader.read(bis);
        MapModel model = new XmlModelConverter().convert(xmlModel);

        MappingWriter mappingWriter = new MappingWriter();

        int index = 0;
        for (Station station : model.getData().stations) {
            mappingWriter.putStation(station, index++);
        }

        index = 0;
        for (Borough borough : boroughsList) {
            mappingWriter.putBorough(borough, index++);
        }

        mappingWriter.write(boroughsList, pathData);
    }


    private GeometryTesselationMap<Borough> load(Path pathBoroughs, String dir, int level) throws IOException, ParseException {
        Path path = pathBoroughs.resolve(dir);
        GeometryTesselationMap<Borough> tesselation = new GeometryTesselationMap<>();
        for (Path p : PathUtil.find(path, "*.wkb")) {
            String filename = p.getFileName().toString();
            String name = filename.substring(0, filename.length() - 4);
            if (name.equals("Berlin")) {
                continue;
            }
            Geometry geometry = new WKBReader().read(Files.readAllBytes(p));
            Borough borough = new Borough(boroughsList.size() + 1, level, name, new HashSet<>());
            boroughsList.add(borough);
            tesselation.add(geometry, borough);
        }
        return tesselation;
    }

}
