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

import static android.view.View.VISIBLE;

import android.content.Context;
import android.os.Bundle;
import android.util.DisplayMetrics;
import android.view.Display;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewTreeObserver;
import android.view.WindowManager;
import android.widget.ZoomControls;

import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;

import org.openmetromaps.maps.MapModel;
import org.openmetromaps.maps.MapView;
import org.openmetromaps.maps.ModelUtil;
import org.openmetromaps.maps.PlanRenderer;

import de.topobyte.android.maps.utils.MapZoomControls;
import de.topobyte.transportation.info.RegionDataViewModel;
import de.topobyte.transportation.info.Region;
import de.topobyte.transportation.info.StartPosition;
import de.topobyte.transportation.info.berlin.R;
import de.topobyte.viewports.geometry.Coordinate;
import de.topobyte.viewports.scrolling.ViewportUtil;

public class NetworkMapFragment extends Fragment {

  public static String ARG_REGION = "region";
  public static String ARG_VIEW_INDEX = "view-index";

  public static String STATE_POS_X = "posX";
  public static String STATE_POS_Y = "posY";
  public static String STATE_ZOOM = "zoom";

  private NetworkMapView map;
  private ZoomControls zoomControls;

  private RegionDataViewModel vm;

  @Override
  public void onCreate(@Nullable Bundle savedInstanceState)
  {
    super.onCreate(savedInstanceState);

    String regionId = null;
    Bundle args = getArguments();
    if (args != null) {
      regionId = args.getString(ARG_REGION, Region.BERLIN.getStringId());
    }
    Region region = Region.findByStringId(regionId);

    vm = new ViewModelProvider(this).get(RegionDataViewModel.class);

    vm.getState().observe(this, state -> {
      switch (state.status) {
        case IDLE:
          break;
        case LOADING:
          break;
        case READY:
          init(state.data.getModel());
          break;
        case ERROR:
          break;
      }
    });

    vm.loadIfNeeded(region);
  }

  private StartPosition startPosition = null;

  @Override
  public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState)
  {
    super.onCreateView(inflater, container, savedInstanceState);

    View layout = inflater.inflate(R.layout.map, container, false);

    map = layout.findViewById(R.id.map);
    zoomControls = layout.findViewById(R.id.zoom_controls);

    if (savedInstanceState != null) {
      double positionX = savedInstanceState.getDouble(STATE_POS_X);
      double positionY = savedInstanceState.getDouble(STATE_POS_Y);
      double zoom = savedInstanceState.getDouble(STATE_ZOOM);
      Coordinate start = new Coordinate(positionX, positionY);
      startPosition = new StartPosition(start, zoom);
    }

    return layout;
  }

  private void init(MapModel model)
  {
    int viewIndex = 0;
    Bundle args = getArguments();
    if (args != null) {
      viewIndex = args.getInt(ARG_VIEW_INDEX, viewIndex);
    }

    DisplayMetrics metrics = new DisplayMetrics();
    WindowManager windowManager = (WindowManager) getActivity()
        .getSystemService(Context.WINDOW_SERVICE);
    Display display = windowManager.getDefaultDisplay();
    display.getMetrics(metrics);

    MapView view = model.getViews().get(viewIndex);
    MapView scaled = ModelUtil.getScaledInstance(view, metrics.density);

    map.configure(scaled, PlanRenderer.StationMode.CONVEX, PlanRenderer.SegmentMode.CURVE);

    MapZoomControls<NetworkMapView> mapZoomControls = new MapZoomControls<>(map, zoomControls);
    map.setOnTouchListener(mapZoomControls);

    if (startPosition == null) {
      addStartPositionSetter(scaled.getConfig().getStartPosition(), 1);
    } else {
      addStartPositionSetter(startPosition.getStart(), startPosition.getZoom());
    }

    map.setVisibility(VISIBLE);
  }

  @Override
  public void onSaveInstanceState(Bundle outState)
  {
    super.onSaveInstanceState(outState);

    double frx = ViewportUtil.getRealX(map, map.getWidth() / 2);
    double fry = ViewportUtil.getRealY(map, map.getHeight() / 2);

    outState.putDouble(STATE_POS_X, frx);
    outState.putDouble(STATE_POS_Y, fry);
    outState.putDouble(STATE_ZOOM, map.getZoom());
  }

  private void addStartPositionSetter(final Coordinate start, final double zoom)
  {
    map.getViewTreeObserver().addOnPreDrawListener(new ViewTreeObserver.OnPreDrawListener() {

      @Override
      public boolean onPreDraw()
      {
        NetworkMapView v = NetworkMapFragment.this.map;
        v.getViewTreeObserver().removeOnPreDrawListener(this);
        v.setPositionX(-start.getX() + v.getWidth() / 2);
        v.setPositionY(-start.getY() + v.getHeight() / 2);
        v.setZoom(zoom);
        return true;
      }

    });
  }

}
