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

package de.topobyte.transportation.info.map;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import org.openmetromaps.maps.MapModel;
import org.openmetromaps.maps.MapView;
import org.openmetromaps.maps.ModelUtil;
import org.openmetromaps.maps.PlanRenderer;

import de.topobyte.transportation.info.ModelLoader;

public class NetworkMapFragment extends Fragment {

  NetworkMapView view;

  @Override
  public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState)
  {
    view = new NetworkMapView(getActivity());

    MapModel model = ModelLoader.loadSafe(getActivity());
    ModelUtil.ensureView(model);

    MapView view = model.getViews().get(0);

    this.view.configure(view, PlanRenderer.StationMode.CONVEX, PlanRenderer.SegmentMode.CURVE);

    return this.view;
  }

}
