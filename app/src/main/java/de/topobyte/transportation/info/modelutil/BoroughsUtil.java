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

package de.topobyte.transportation.info.modelutil;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.Iterator;
import java.util.List;

import de.topobyte.opnv.model.Borough;

public class BoroughsUtil {

  public static List<Borough> getSorted(Collection<Borough> boroughs)
  {
    List<Borough> result = new ArrayList<>();
    result.addAll(boroughs);

    Collections.sort(result, new Comparator<Borough>() {

      @Override
      public int compare(Borough o1, Borough o2)
      {
        int cmp = o2.getLevel() - o1.getLevel();
        if (cmp != 0) {
          return cmp;
        }
        return o1.getName().compareTo(o2.getName());
      }
    });

    return result;
  }

  public static String asStringList(List<Borough> bsl)
  {
    StringBuilder strb = new StringBuilder();
    Iterator<Borough> iter = bsl.iterator();
    while (iter.hasNext()) {
      strb.append(iter.next().getName());
      if (iter.hasNext()) {
        strb.append(", ");
      }
    }
    return strb.toString();
  }
}
