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

package de.topobyte.android.util;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ListAdapter;

import java.util.ArrayList;
import java.util.List;

public abstract class FlexAdapter<T> extends BaseAdapter implements ListAdapter {

  private final LayoutInflater inflater;
  protected List<T> values = new ArrayList<>();

  private final int layout;

  public FlexAdapter(Context context, int layout, List<T> values)
  {
    this.layout = layout;
    inflater = (LayoutInflater) context
        .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
    this.values = values;
  }

  @Override
  public int getCount()
  {
    return values.size();
  }

  @Override
  public Object getItem(int position)
  {
    return values.get(position);
  }

  @Override
  public long getItemId(int position)
  {
    return position;
  }

  @Override
  public View getView(int position, View convertView, ViewGroup parent)
  {
    View view;

    if (convertView == null) {
      view = inflater.inflate(layout, parent, false);
    } else {
      view = convertView;
    }

    T object = values.get(position);

    fill(view, object);

    return view;
  }

  public abstract void fill(View view, T object);

}
