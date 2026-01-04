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
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Toast;

import de.topobyte.android.appversions.BuildConfig;
import de.topobyte.android.appversions.VersionUpdateChecker;
import de.topobyte.transportation.info.Constants;
import de.topobyte.transportation.info.berlin.R;
import de.topobyte.transportation.info.fragments.StartFragment;

public class MainActivity extends TransportActivity {

  @Override
  protected void onCreate(Bundle savedInstanceState)
  {
    super.onCreate(savedInstanceState);
    setContentView(R.layout.activity_with_toolbar);

    setupToolbar(false);

    VersionUpdateChecker updateChecker = new VersionUpdateChecker(this);
    SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(this);
    if (updateChecker.isVersionUpdate()) {
      // if (updateChecker.getStoredVersion() < 200) {
      // Editor editor = preferences.edit();
      // editor.putBoolean(getString(R.string.pref_key_something), true);
      // editor.commit();
      // }

      // On update, store the previous version in the FIRST_SEEN_VERSION preference
      if (!preferences.contains(Constants.PREF_FIRST_SEEN_VERSION)) {
        int firstSeenVersion = updateChecker.getStoredVersion();
        if (firstSeenVersion == 0) {
          // This is a new installation or an update from a version lower than 200,
          // store current version code
          firstSeenVersion = BuildConfig.VERSION_CODE;
        }
        Editor editor = preferences.edit();
        editor.putInt(Constants.PREF_FIRST_SEEN_VERSION, firstSeenVersion);
        editor.apply();
      }
      updateChecker.storeCurrentVersion();
    }

    if (savedInstanceState == null) {
      getSupportFragmentManager().beginTransaction()
          .add(R.id.container, new StartFragment()).commit();
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
    return super.onOptionsItemSelected(item);
  }

}
