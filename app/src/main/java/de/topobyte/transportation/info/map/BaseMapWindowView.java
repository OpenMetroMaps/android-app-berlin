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
import android.support.annotation.Nullable;
import android.util.AttributeSet;

import org.openmetromaps.maps.LocationToPoint;

import de.topobyte.lightgeom.lina.Point;
import de.topobyte.viewports.scrolling.ViewportUtil;

public class BaseMapWindowView extends BaseSceneView implements LocationToPoint {

  public BaseMapWindowView(Context context)
  {
    super(context);
  }

  public BaseMapWindowView(Context context, @Nullable AttributeSet attrs)
  {
    super(context, attrs);
  }

  public BaseMapWindowView(Context context, @Nullable AttributeSet attrs, int defStyleAttr)
  {
    super(context, attrs, defStyleAttr);
  }


  @Override
  public Point getPoint(Point location)
  {
    double x = ViewportUtil.getViewX(this, location.x);
    double y = ViewportUtil.getViewY(this, location.y);
    return new Point(x, y);
  }

  @Override
  public Point getPoint(Point location, Point point)
  {
    double x = ViewportUtil.getViewX(this, location.x);
    double y = ViewportUtil.getViewY(this, location.y);
    return point.set(x, y);
  }

  @Override
  public double getX(double x)
  {
    return ViewportUtil.getViewX(this, x);
  }

  @Override
  public double getY(double y)
  {
    return ViewportUtil.getViewY(this, y);
  }

}
