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

package de.topobyte.transportation.info;

import android.content.Context;
import android.content.res.AssetManager;
import android.util.Log;

import org.openmetromaps.maps.MapModel;
import org.openmetromaps.maps.model.Station;
import org.openmetromaps.maps.xml.DesktopXmlModelReader;
import org.openmetromaps.maps.xml.XmlModel;
import org.openmetromaps.maps.xml.XmlModelConverter;

import java.io.BufferedInputStream;
import java.io.IOException;
import java.io.InputStream;

import de.topobyte.xml.domabstraction.iface.ParsingException;

public class ModelLoader {

  public static MapModel load(Context context, String region) throws IOException, ParsingException
  {
    AssetManager assets = context.getAssets();
    InputStream ais = assets.open(region + "/model.omm");

    BufferedInputStream bis = new BufferedInputStream(ais);

    XmlModel xmlModel = DesktopXmlModelReader.read(bis);
    MapModel model = new XmlModelConverter().convert(xmlModel);
    // Work around problem with model converter assigning 0 as id to all stations
    int id = 0;
    for (Station station : model.getData().stations) {
      station.setId(id++);
    }

    bis.close();

    return model;
  }

  public static MapModel loadSafe(Context context, String region)
  {
    try {
      return load(context, region);
    } catch (IOException | ParsingException e) {
      Log.e("data", "Unable to load model", e);
    }
    return null;
  }
}
