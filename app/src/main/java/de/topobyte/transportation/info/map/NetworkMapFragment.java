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
import android.view.ViewTreeObserver;

import org.openmetromaps.maps.MapModel;
import org.openmetromaps.maps.MapView;
import org.openmetromaps.maps.ModelUtil;
import org.openmetromaps.maps.PlanRenderer;

import de.topobyte.transportation.info.ModelLoader;
import de.topobyte.viewports.geometry.Coordinate;
import de.topobyte.viewports.scrolling.ViewportUtil;

public class NetworkMapFragment extends Fragment {

  public static String ARG_VIEW_INDEX = "view-index";

  public static String STATE_POS_X = "posX";
  public static String STATE_POS_Y = "posY";
  public static String STATE_ZOOM = "zoom";

  NetworkMapView view;

  @Override
  public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState)
  {
    view = new NetworkMapView(getActivity());

    MapModel model = ModelLoader.loadSafe(getActivity());
    ModelUtil.ensureView(model);

    int viewIndex = 0;
    Bundle args = getArguments();
    if (args != null) {
      viewIndex = args.getInt(ARG_VIEW_INDEX, viewIndex);
    }

    MapView view = model.getViews().get(viewIndex);

    this.view.configure(view, PlanRenderer.StationMode.CONVEX, PlanRenderer.SegmentMode.CURVE);

    if (savedInstanceState == null) {
      addStartPositionSetter(view.getConfig().getStartPosition(), 1);
    } else {
      double positionX = savedInstanceState.getDouble(STATE_POS_X);
      double positionY = savedInstanceState.getDouble(STATE_POS_Y);
      double zoom = savedInstanceState.getDouble(STATE_ZOOM);
      Coordinate start = new Coordinate(positionX, positionY);
      addStartPositionSetter(start, zoom);
    }

    return this.view;
  }

  @Override
  public void onSaveInstanceState(Bundle outState)
  {
    super.onSaveInstanceState(outState);

    double frx = ViewportUtil.getRealX(view, view.getWidth() / 2);
    double fry = ViewportUtil.getRealY(view, view.getHeight() / 2);

    outState.putDouble(STATE_POS_X, frx);
    outState.putDouble(STATE_POS_Y, fry);
    outState.putDouble(STATE_ZOOM, view.getZoom());
  }

  private void addStartPositionSetter(final Coordinate start, final double zoom)
  {
    this.view.getViewTreeObserver().addOnPreDrawListener(new ViewTreeObserver.OnPreDrawListener() {

      @Override
      public boolean onPreDraw()
      {
        NetworkMapView v = NetworkMapFragment.this.view;
        v.getViewTreeObserver().removeOnPreDrawListener(this);
        v.setPositionX(-start.getX() + v.getWidth() / 2);
        v.setPositionY(-start.getY() + v.getHeight() / 2);
        v.setZoom(zoom);
        return true;
      }

    });
  }

}
