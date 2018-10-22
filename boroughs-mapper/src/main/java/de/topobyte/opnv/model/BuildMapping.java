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

package de.topobyte.opnv.model;

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
import java.io.BufferedOutputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import de.topobyte.jts.indexing.GeometryTesselationMap;
import de.topobyte.melon.paths.PathUtil;
import de.topobyte.system.utils.SystemPaths;
import de.topobyte.xml.domabstraction.iface.ParsingException;

public class BuildMapping {

    public static void main(String[] args) throws IOException, ParsingException, ParseException {
        BuildMapping task = new BuildMapping();
        task.execute();
    }

    private List<Borough> boroughsList = new ArrayList<>();
    private Map<Borough, Integer> boroughToIndex = new HashMap<>();
    private Map<Station, Integer> stationToIndex = new HashMap<>();

    public void execute() throws IOException, ParseException, ParsingException {
        Path pathBoroughs = SystemPaths.HOME.resolve("github/ThatsBerlin/admin-areas");
        Path pathOMM = SystemPaths.CWD.resolve("app/src/main/assets/model.omm");
        Path pathData = SystemPaths.CWD.resolve("app/src/main/assets/boroughs.data");

        System.out.println(pathBoroughs);
        System.out.println(pathOMM);

        GeometryTesselationMap<Borough> tesselationBundesland = load(pathBoroughs, "bundesland", 4);
        GeometryTesselationMap<Borough> tesselationBezirk = load(pathBoroughs, "berlin-bezirk", 9);
        GeometryTesselationMap<Borough> tesselationOrtsteil = load(pathBoroughs, "berlin-ortsteil", 10);

        InputStream is = Files.newInputStream(pathOMM);
        BufferedInputStream bis = new BufferedInputStream(is);

        XmlModel xmlModel = DesktopXmlModelReader.read(bis);
        MapModel model = new XmlModelConverter().convert(xmlModel);

        int index = 0;
        for (Station station : model.getData().stations) {
            stationToIndex.put(station, index++);
        }

        index = 0;
        for (Borough borough : boroughsList) {
            boroughToIndex.put(borough, index++);
        }

        for (Station station : model.getData().stations) {
            Coordinate c = new Coordinate(station.getLocation().getLongitude(), station.getLocation().getLatitude());
            Point point = new GeometryFactory().createPoint(c);
            map(station, point, tesselationBundesland);
            map(station, point, tesselationBezirk);
            map(station, point, tesselationOrtsteil);
        }

        write(pathData);
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

    private void map(Station station, Point point, GeometryTesselationMap<Borough> tesselation) {
        Set<Borough> boroughs = tesselation.test(point);
        for (Borough b : boroughs) {
            b.getStations().add(station);
        }
    }

    private DataOutputStream dos;

    private void write(Path pathOutput) throws IOException {
        OutputStream fos = Files.newOutputStream(pathOutput);
        OutputStream bos = new BufferedOutputStream(fos);
        dos = new DataOutputStream(bos);

        writeBoroughs();
        writeBoroughsToStops();

        dos.close();
    }

    private void writeBoroughs() throws IOException
    {
        dos.writeShort(boroughsList.size());
        for (Borough borough : boroughsList) {
            dos.writeByte(borough.getLevel());
            dos.writeUTF(borough.getName());
        }
    }

    private void writeBoroughsToStops() throws IOException
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

}
