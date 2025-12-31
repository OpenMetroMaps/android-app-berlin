// Copyright 2025 Sebastian Kuerten
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

import org.openmetromaps.maps.MapModel;

import de.topobyte.transportation.info.model.ModelData;

public class RegionData {

  private final Region region;
  private final MapModel model;
  private final ModelData data;

  public RegionData(Region region, MapModel model, ModelData data)
  {
    this.region = region;
    this.model = model;
    this.data = data;
  }

  public static RegionData load(Context context, Region region)
  {
    MapModel model = ModelLoader.loadSafe(context, region.getStringId());
    ModelData data = DataLoader.loadSafe(context, model, region.getStringId());
    return new RegionData(region, model, data);
  }

  public Region getRegion()
  {
    return region;
  }

  public MapModel getModel()
  {
    return model;
  }

  public ModelData getData()
  {
    return data;
  }
}
