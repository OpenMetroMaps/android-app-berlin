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

package de.topobyte.android.util.onclick;

import android.view.View;

public class ClickListener implements View.OnClickListener {

  private final int id;
  private final ClickDelegate delegate;

  public ClickListener(ClickDelegate delegate, int id)
  {
    this.delegate = delegate;
    this.id = id;
  }

  @Override
  public void onClick(View v)
  {
    delegate.onClick(id);
  }

}
