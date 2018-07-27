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

package de.topobyte.android;

import android.view.View;
import android.view.ViewGroup;

import java.util.ArrayList;
import java.util.List;

public class Grid {

  private List<Row> rows = new ArrayList<>();

  public Grid(ViewGroup group, int maxCols)
  {
    int nChildren = group.getChildCount();
    int nNotGone = 0;

    Row currentRow = null;

    for (int i = 0; i < nChildren; i++) {
      View child = group.getChildAt(i);
      // Skip gone children
      if (child.getVisibility() == View.GONE) {
        continue;
      }
      nNotGone++;

      if (currentRow == null) {
        currentRow = new Row();
      }
      currentRow.add(child);

      if (currentRow.size() == maxCols) {
        rows.add(currentRow);
        currentRow = null;
      }
    }

    if (currentRow != null) {
      rows.add(currentRow);
    }
  }

  public List<Row> getRows()
  {
    return rows;
  }

}
