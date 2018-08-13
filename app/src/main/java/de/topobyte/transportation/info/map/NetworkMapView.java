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

import android.content.Context;
import android.graphics.Canvas;
import android.util.AttributeSet;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.Display;
import android.view.MotionEvent;
import android.view.WindowManager;

import org.openmetromaps.maps.MapView;
import org.openmetromaps.maps.MapViewStatus;
import org.openmetromaps.maps.PlanRenderer;
import org.openmetromaps.maps.painting.android.AndroidPaintFactory;
import org.openmetromaps.maps.painting.android.AndroidPainter;

import de.topobyte.android.maps.utils.events.EventManager;
import de.topobyte.android.maps.utils.events.EventManagerManaged;
import de.topobyte.android.maps.utils.events.Vector2;
import de.topobyte.interactiveview.Zoomable;
import de.topobyte.viewports.geometry.Coordinate;
import de.topobyte.viewports.scrolling.ViewportUtil;

public class NetworkMapView extends BaseMapWindowView implements EventManagerManaged, Zoomable {

  public NetworkMapView(Context context)
  {
    super(context);
    init();
  }

  public NetworkMapView(Context context, AttributeSet attrs)
  {
    super(context, attrs);
    init();
  }

  public NetworkMapView(Context context, AttributeSet attrs, int defStyleAttr)
  {
    super(context, attrs, defStyleAttr);
    init();
  }

  private float density;

  private void init()
  {
    DisplayMetrics metrics = new DisplayMetrics();
    WindowManager windowManager = (WindowManager) getContext()
        .getSystemService(Context.WINDOW_SERVICE);
    Display display = windowManager.getDefaultDisplay();
    display.getMetrics(metrics);
    density = metrics.density;
  }

  private PlanRenderer renderer;

  public void configure(MapView view, PlanRenderer.StationMode stationMode,
                        PlanRenderer.SegmentMode segmentMode)
  {
    renderer = new PlanRenderer(view.getLineNetwork(), new MapViewStatus(), stationMode, segmentMode, this,
        this, density, new AndroidPaintFactory());
    scene = view.getConfig().getScene();
  }

  @Override
  protected void onDraw(Canvas canvas)
  {
    super.onDraw(canvas);

    AndroidPainter painter = new AndroidPainter(canvas);

    renderer.paint(painter);
  }

  private final EventManager<NetworkMapView> eventManager = new EventManager<NetworkMapView>(
      this, true);

  public EventManager<NetworkMapView> getEventManager()
  {
    return eventManager;
  }

  @Override
  public boolean onTouchEvent(MotionEvent event)
  {
    super.onTouchEvent(event);
    return eventManager.onTouchEvent(event);
  }

  @Override
  public boolean onTrackballEvent(MotionEvent event)
  {
    super.onTrackballEvent(event);
    return eventManager.onTrackballEvent(event);
  }

  @Override
  public void move(Vector2 distance)
  {
    positionX -= distance.getX() / getZoom();
    positionY -= distance.getY() / getZoom();
    postInvalidate();
  }

  @Override
  public void zoom(float zoomDistance)
  {
    setZoom(getZoom() * (1 + zoomDistance));
  }

  @Override
  public void zoomIn()
  {
    setZoom(getZoom() * 1.1);
    postInvalidate();
  }

  @Override
  public void zoomOut()
  {
    setZoom(getZoom() / 1.1);
    postInvalidate();
  }

  @Override
  public void zoom(float x, float y, float zoomDistance)
  {
    Log.i("test", "zoom: " + zoomDistance);

    ViewportUtil.zoomFixed(this, new Coordinate(x, y), true, zoomDistance);

    postInvalidate();
  }

  @Override
  public void zoomIn(float x, float y)
  {
    ViewportUtil.zoomFixed(this, new Coordinate(x, y), true, 0.1);
    postInvalidate();
  }

  @Override
  public void zoomOut(float x, float y)
  {
    ViewportUtil.zoomFixed(this, new Coordinate(x, y), false, 0.1);
    postInvalidate();
  }

  @Override
  public float getMoveSpeed()
  {
    return 0;
  }

  @Override
  public void longClick(float v, float v2)
  {

  }

  @Override
  public boolean canZoomIn()
  {
    return true;
  }

  @Override
  public boolean canZoomOut()
  {
    return true;
  }


}
