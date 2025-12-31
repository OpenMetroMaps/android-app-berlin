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

package de.topobyte.transportation.info.activities;

import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;

import de.topobyte.transportation.info.Region;
import de.topobyte.transportation.info.berlin.R;
import de.topobyte.transportation.info.map.NetworkMapFragment;


public class NetworkMapActivity extends ToolbarActivity {

  public static String EXTRA_REGION = "region";
  public static String EXTRA_VIEW_INDEX = "view-index";
  public static String EXTRA_TITLE = "title";

  @Override
  protected void onCreate(Bundle savedInstanceState)
  {
    super.onCreate(savedInstanceState);

    setContentView(R.layout.activity_with_toolbar);

    String regionId = getIntent().getStringExtra(EXTRA_REGION);
    String title = getIntent().getStringExtra(EXTRA_TITLE);

    Region region = Region.findByStringId(regionId);

    setupToolbar(true);
    getSupportActionBar().setTitle(title);
    getSupportActionBar().setSubtitle(region.getNameStringId());

    if (savedInstanceState == null) {
      int viewIndex = getIntent().getIntExtra(EXTRA_VIEW_INDEX, 0);

      Bundle args = new Bundle();
      args.putString(NetworkMapFragment.ARG_REGION, regionId);
      args.putInt(NetworkMapFragment.ARG_VIEW_INDEX, viewIndex);

      NetworkMapFragment fragment = new NetworkMapFragment();
      fragment.setArguments(args);

      getSupportFragmentManager().beginTransaction()
          .add(R.id.container, fragment).commit();
    }
  }

  @Override
  public boolean onCreateOptionsMenu(Menu menu)
  {
    getMenuInflater().inflate(R.menu.main, menu);
    return true;
  }

  @Override
  public boolean onOptionsItemSelected(MenuItem item)
  {
    int id = item.getItemId();

    if (id == R.id.action_settings) {
      return true;
    }
    if (id == R.id.action_info) {
      startActivity(new Intent(getApplicationContext(), AboutActivity.class));
      return true;
    }
    if (id == android.R.id.home) {
      finish();
      return true;
    }

    return super.onOptionsItemSelected(item);
  }

}
