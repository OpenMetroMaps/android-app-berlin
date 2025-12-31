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

import de.topobyte.transportation.info.berlin.R;

public enum Region {

  BERLIN(0, "berlin", R.string.berlin, "de.topobyte.apps.offline.stadtplan.berlin"),
  VIENNA(1, "vienna", R.string.vienna, "de.topobyte.apps.offline.stadtplan.wien");

  private final int intId;
  private final String stringId;
  private final int nameStringId;
  private final String stadtplanApp;

  Region(int id, String assetDirectory, int nameStringId, String stadtplanApp)
  {
    this.intId = id;
    this.stringId = assetDirectory;
    this.nameStringId = nameStringId;
    this.stadtplanApp = stadtplanApp;
  }

  public int getIntId()
  {
    return intId;
  }

  public String getStringId()
  {
    return stringId;
  }

  public int getNameStringId()
  {
    return nameStringId;
  }

  public String getStadtplanApp()
  {
    return stadtplanApp;
  }

  public static Region findByIntId(int id)
  {
    for (Region region : values()) {
      if (region.getIntId() == id) return region;
    }
    return null;
  }

  public static Region findByStringId(String id)
  {
    for (Region region : values()) {
      if (region.getStringId().equals(id)) return region;
    }
    return null;
  }
}
