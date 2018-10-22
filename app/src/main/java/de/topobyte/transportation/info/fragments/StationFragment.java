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

package de.topobyte.transportation.info.fragments;

import android.app.Activity;
import android.app.Dialog;
import android.content.ComponentName;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.support.v4.app.Fragment;
import android.support.v7.app.AlertDialog;
import android.util.DisplayMetrics;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.wefika.flowlayout.FlowLayout;

import org.openmetromaps.maps.MapModel;
import org.openmetromaps.maps.model.Line;
import org.openmetromaps.maps.model.Station;
import org.openmetromaps.maps.model.Stop;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.Set;

import de.topobyte.adt.geo.Coordinate;
import de.topobyte.android.intent.utils.IntentFactory;
import de.topobyte.opnv.model.Borough;
import de.topobyte.opnv.model.ModelData;
import de.topobyte.transportation.info.BackgroundUtil;
import de.topobyte.transportation.info.ColorUtil;
import de.topobyte.transportation.info.DataLoader;
import de.topobyte.transportation.info.Direction;
import de.topobyte.transportation.info.ModelLoader;
import de.topobyte.transportation.info.StopWithDirectedLine;
import de.topobyte.transportation.info.activities.TransportActivity;
import de.topobyte.transportation.info.berlin.R;
import de.topobyte.transportation.info.modelutil.BoroughsUtil;
import de.topobyte.transportation.info.modelutil.LinesUtil;
import de.topobyte.transportation.info.modelutil.StationUtil;

public class StationFragment extends Fragment {

  private static final String SAVE_ID = "station-id";

  private Station station;

  private TransportActivity activity;

  private MapModel model;
  private ModelData data;

  private static String pn = "de.topobyte.apps.offline.stadtplan.berlin";
//  private String pn = "de.topobyte.apps.maps.atestcity.admob";

  public static StationFragment newInstance(Station station)
  {
    StationFragment fragment = new StationFragment();
    fragment.station = station;
    return fragment;
  }

  @Override
  public void onCreate(Bundle savedInstanceState)
  {
    super.onCreate(savedInstanceState);
    model = ModelLoader.loadSafe(getActivity());
    data = DataLoader.loadSafe(getActivity(), model);

    if (savedInstanceState != null) {
      int id = savedInstanceState.getInt(SAVE_ID);
      station = model.getData().stations.get(id);
    }
  }

  @Override
  public void onSaveInstanceState(Bundle outState)
  {
    super.onSaveInstanceState(outState);

    outState.putInt(SAVE_ID, station.getId());
  }

  @Override
  public void onAttach(Activity activity)
  {
    super.onAttach(activity);
    this.activity = (TransportActivity) activity;
  }

  @Override
  public View onCreateView(LayoutInflater inflater, ViewGroup container,
                           Bundle savedInstanceState)
  {
    int toolBarColor = getResources().getColor(R.color.background_material_dark);
    activity.getToolbar().setBackgroundColor(toolBarColor);
    activity.getToolbar().setTitle(R.string.station);
    activity.getToolbar().setSubtitle(null);

    View view = inflater.inflate(R.layout.fragment_station, container,
        false);

    TextView headline = view.findViewById(R.id.headline);
    FlowLayout linesContainer = view.findViewById(R.id.lines);
    TextView boroughs = view.findViewById(R.id.boroughs);
    Button showOnOurMap = view.findViewById(R.id.showOnOurMap);
    Button showOnGenericMap = view.findViewById(R.id.showOnAnyMap);
    LinearLayout items = view.findViewById(R.id.items);

    headline.setText(station.getName());

    Set<Borough> boroughSet = StationUtil.getBoroughs(station, data);
    List<Borough> boroughList = BoroughsUtil.getSorted(boroughSet);
    if (boroughSet.size() == 0) {
      boroughs.setVisibility(View.GONE);
    } else {
      String listOfBoroughs = BoroughsUtil.asStringList(boroughList);
      boroughs.setText(listOfBoroughs);
    }

    LinesUtil.setupLinesInLayout(StationFragment.this, linesContainer, station);

    showOnOurMap.setOnClickListener(new View.OnClickListener() {
      @Override
      public void onClick(View view)
      {
        Coordinate loc = StationUtil.getLocation(station);

        boolean installed;
        boolean newEnough = false;
        try {
          PackageInfo info = activity.getPackageManager().getPackageInfo(pn, 0);
          installed = true;
          newEnough = info.versionCode >= 90;
        } catch (PackageManager.NameNotFoundException e) {
          installed = false;
        }

        if (!installed) {
          showPleaseInstallDialog();
          return;
        }

        if (!newEnough) {
          showPleaseUpdateDialog();
          return;
        }

        Intent intent = new Intent();
        intent.setComponent(new ComponentName(pn, "de.topobyte.apps.viewer.Stadtplan"));
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        intent.putExtra("lon", loc.getLongitude());
        intent.putExtra("lat", loc.getLatitude());
        intent.putExtra("min-zoom", 14);
        startActivity(intent);
      }
    });

    showOnGenericMap.setOnClickListener(new View.OnClickListener() {
      @Override
      public void onClick(View view)
      {
        Coordinate loc = StationUtil.getLocation(station);

        String data = "geo:" + loc.getLatitude() + "," + loc.getLongitude();
        Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(data));
        startActivity(intent);
      }
    });

    List<Stop> stops = new ArrayList<>(station.getStops());
    Collections.sort(stops, new Comparator<Stop>() {
      @Override
      public int compare(Stop stop1, Stop stop2)
      {
        return stop1.getLine().getName().compareTo(stop2.getLine().getName());
      }
    });

    List<StopWithDirectedLine> dstops = new ArrayList<>();
    for (Stop stop : stops) {
      if (stop.getLine().isCircular()) {
        // If this line is circular we don't care whether we are at the 'last' stop
        // since there really is no real last stop
        dstops.add(new StopWithDirectedLine(stop, Direction.FORWARD));
        // TODO: we don't know about the opposite line of circular lines
      } else {
        List<Stop> lineStops = stop.getLine().getStops();
        Stop firstStop = lineStops.get(0);
        Stop lastStop = lineStops.get(lineStops.size() - 1);
        if (lastStop.getStation() != station) {
          // If we're not at the last stop in forward direction
          dstops.add(new StopWithDirectedLine(stop, Direction.FORWARD));
        }
        if (firstStop.getStation() != station) {
          // If we're not at the first stop in forward direction
          if (stop.getLine().isCircular()) {
            // Add a backward version of this line only if there is no explicit opposite
            dstops.add(new StopWithDirectedLine(stop, Direction.BACKWARD));
          }
        }
      }
    }

    DisplayMetrics metrics = getResources().getDisplayMetrics();
    LayoutInflater li = activity.getLayoutInflater();

    for (int i = 0; i < dstops.size(); i++) {
      final StopWithDirectedLine stop = dstops.get(i);
      View row = li.inflate(R.layout.row_layout_lines, items, false);

      TextView text = row.findViewById(R.id.text1);

      final Line line = stop.getStop().getLine();
      List<Stop> lineStops = line.getStops();
      int n = stop.getDirection() == Direction.BACKWARD ? 0 : lineStops.size() - 1;
      Stop lastStop = lineStops.get(n);

      String destination = lastStop.getStation().getName();
      if (line.isCircular()) {
        destination = "Ring";
      }
      text.setText(line.getName() + " → " + destination);

      items.addView(row);

      int color = Color.parseColor(line.getColor());
      int color2 = ColorUtil.highlightColor(color);
      BackgroundUtil.setBackground(text, color, color2, 5, metrics);

      text.setOnClickListener(new View.OnClickListener() {
        @Override
        public void onClick(View view)
        {
          activity.showLine(line, stop.getDirection(), stop.getStop());
        }
      });

      if (i < dstops.size() - 1) {
        View divider = li.inflate(R.layout.divider, items, false);
        items.addView(divider);
      }

    }

    return view;
  }

  private void showPleaseInstallDialog()
  {
    SimpleDialogFragment dialog = new AppDetailsDialogFragment();

    Bundle args = new Bundle();
    args.putInt(SimpleDialogFragment.ARG_MESSAGE, R.string.map_not_installed);
    dialog.setArguments(args);

    dialog.show(getFragmentManager(), "please-install");
  }

  private void showPleaseUpdateDialog()
  {
    SimpleDialogFragment dialog = new AppDetailsDialogFragment();

    Bundle args = new Bundle();
    args.putInt(SimpleDialogFragment.ARG_MESSAGE, R.string.map_not_new_enough);
    dialog.setArguments(args);

    dialog.show(getFragmentManager(), "please-update");
  }

  public static abstract class SimpleDialogFragment extends DialogFragment {

    private static String ARG_MESSAGE = "message";

    private int message;

    public abstract void ok();

    public abstract void cancel();

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState)
    {
      Bundle args = getArguments();
      message = args.getInt(ARG_MESSAGE);

      AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
      builder.setMessage(message)
          .setPositiveButton(android.R.string.ok, new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id)
            {
              ok();
            }
          })
          .setNegativeButton(android.R.string.cancel, new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id)
            {
              cancel();
            }
          });

      return builder.create();
    }
  }

  public static class AppDetailsDialogFragment extends SimpleDialogFragment {

    @Override
    public void ok()
    {
      Intent intent = IntentFactory.createGooglePlayAppDetailsIntent(pn);
      startActivity(intent);
    }

    @Override
    public void cancel()
    {
      // do nothing
    }

  }


}