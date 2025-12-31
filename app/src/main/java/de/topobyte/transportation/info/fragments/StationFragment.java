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
import android.util.DisplayMetrics;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.fragment.app.DialogFragment;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;

import com.wefika.flowlayout.FlowLayout;

import org.openmetromaps.maps.model.Line;
import org.openmetromaps.maps.model.Station;
import org.openmetromaps.maps.model.Stop;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Set;

import de.topobyte.adt.geo.Coordinate;
import de.topobyte.android.intent.utils.IntentFactory;
import de.topobyte.transportation.info.BackgroundUtil;
import de.topobyte.transportation.info.ColorUtil;
import de.topobyte.transportation.info.Direction;
import de.topobyte.transportation.info.Region;
import de.topobyte.transportation.info.RegionData;
import de.topobyte.transportation.info.RegionDataViewModel;
import de.topobyte.transportation.info.StopWithDirectedLine;
import de.topobyte.transportation.info.activities.TransportActivity;
import de.topobyte.transportation.info.berlin.R;
import de.topobyte.transportation.info.model.Borough;
import de.topobyte.transportation.info.modelutil.BoroughsUtil;
import de.topobyte.transportation.info.modelutil.LinesUtil;
import de.topobyte.transportation.info.modelutil.StationUtil;

public class StationFragment extends Fragment {

  private int stationId;
  private Region region;
  private Station station;

  private TransportActivity activity;

  private RegionDataViewModel vm;

  private static final String ARG_REGION = "region";
  private static final String ARG_STATION_ID = "station-id";

  private TextView headline;
  private FlowLayout linesContainer;
  private TextView boroughs;
  private Button showOnOurMap;
  private Button showOnGenericMap;
  private LinearLayout items;

  public static StationFragment newInstance(String regionId, int stationId)
  {
    StationFragment fragment = new StationFragment();
    Bundle args = new Bundle();
    args.putString(ARG_REGION, regionId);
    args.putInt(ARG_STATION_ID, stationId);
    fragment.setArguments(args);
    return fragment;
  }

  @Override
  public void onCreate(Bundle savedInstanceState)
  {
    super.onCreate(savedInstanceState);
    Bundle args = requireArguments();
    String regionId = args.getString(ARG_REGION, Region.BERLIN.getStringId());
    region = Region.findByStringId(regionId);
    stationId = args.getInt(ARG_STATION_ID);
  }

  @Override
  public void onAttach(Activity activity)
  {
    super.onAttach(activity);
    this.activity = (TransportActivity) activity;
  }

  @Override
  public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState)
  {
    super.onViewCreated(view, savedInstanceState);

    vm = new ViewModelProvider(requireActivity()).get(RegionDataViewModel.class);

    vm.getState().observe(getViewLifecycleOwner(), state -> {
      if (state.status != RegionDataViewModel.Status.READY) return;

      RegionData regionData = state.data;
      station = regionData.getModel().getData().stations.get(stationId);
      init(regionData);
    });

    vm.loadIfNeeded(region);
  }

  @Override
  public View onCreateView(LayoutInflater inflater, ViewGroup container,
                           Bundle savedInstanceState)
  {
    View view = inflater.inflate(R.layout.fragment_station, container,
        false);

    headline = view.findViewById(R.id.headline);
    linesContainer = view.findViewById(R.id.lines);
    boroughs = view.findViewById(R.id.boroughs);
    showOnOurMap = view.findViewById(R.id.showOnOurMap);
    showOnGenericMap = view.findViewById(R.id.showOnAnyMap);
    items = view.findViewById(R.id.items);

    return view;
  }

  private void updateToolbar()
  {
    activity.getToolbar().setTitle(R.string.station);
    activity.getToolbar().setBackgroundColor(getResources().getColor(R.color.toolbar_background));
    activity.getToolbar().setSubtitle(region.getNameStringId());
  }

  private void init(RegionData regionData)
  {
    updateToolbar();

    headline.setText(station.getName());

    Set<Borough> boroughSet = StationUtil.getBoroughs(station, regionData.getData());
    List<Borough> boroughList = BoroughsUtil.getSorted(boroughSet);
    if (boroughSet.size() == 0) {
      boroughs.setVisibility(View.GONE);
    } else {
      String listOfBoroughs = BoroughsUtil.asStringList(boroughList);
      boroughs.setText(listOfBoroughs);
    }

    LinesUtil.setupLinesInLayout(StationFragment.this, linesContainer, station);

    String regionName = getResources().getString(regionData.getRegion().getNameStringId());
    showOnOurMap.setText(String.format(getResources().getString(R.string.show_on_map), regionName));

    showOnOurMap.setOnClickListener(view -> {
      Coordinate loc = StationUtil.getLocation(station);

      String pn = regionData.getRegion().getStadtplanApp();

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
        showPleaseInstallDialog(regionData.getRegion());
        return;
      }

      if (!newEnough) {
        showPleaseUpdateDialog(regionData.getRegion());
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
    });

    showOnGenericMap.setOnClickListener(view -> {
      Coordinate loc = StationUtil.getLocation(station);

      String data = "geo:" + loc.getLatitude() + "," + loc.getLongitude();
      Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(data));
      startActivity(intent);
    });

    List<Stop> stops = new ArrayList<>(station.getStops());
    Collections.sort(stops, (stop1, stop2) -> stop1.getLine().getName().compareTo(stop2.getLine().getName()));

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
          // If we're not at the first stop in backward direction
          dstops.add(new StopWithDirectedLine(stop, Direction.BACKWARD));
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

      text.setOnClickListener(view -> activity.showLine(region.getStringId(), line, stop.getDirection(), stop.getStop()));

      if (i < dstops.size() - 1) {
        View divider = li.inflate(R.layout.divider, items, false);
        items.addView(divider);
      }
    }
  }

  private void showPleaseInstallDialog(Region region)
  {
    SimpleDialogFragment dialog = new AppDetailsDialogFragment();

    Bundle args = new Bundle();
    args.putInt(SimpleDialogFragment.ARG_MESSAGE, R.string.map_not_installed);
    args.putInt(SimpleDialogFragment.ARG_MESSAGE_REPLACEMENT, region.getNameStringId());
    args.putString(AppDetailsDialogFragment.ARG_PACKAGE, region.getStadtplanApp());
    dialog.setArguments(args);

    dialog.show(getParentFragmentManager(), "please-install");
  }

  private void showPleaseUpdateDialog(Region region)
  {
    SimpleDialogFragment dialog = new AppDetailsDialogFragment();

    Bundle args = new Bundle();
    args.putInt(SimpleDialogFragment.ARG_MESSAGE, R.string.map_not_new_enough);
    args.putInt(SimpleDialogFragment.ARG_MESSAGE_REPLACEMENT, region.getNameStringId());
    args.putString(AppDetailsDialogFragment.ARG_PACKAGE, region.getStadtplanApp());
    dialog.setArguments(args);

    dialog.show(getParentFragmentManager(), "please-update");
  }

  public static abstract class SimpleDialogFragment extends DialogFragment {

    private static String ARG_MESSAGE = "message";
    private static String ARG_MESSAGE_REPLACEMENT = "replacement";

    private int message;
    private int replacement;

    public abstract void ok();

    public abstract void cancel();

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState)
    {
      Bundle args = getArguments();
      message = args.getInt(ARG_MESSAGE);
      replacement = args.getInt(ARG_MESSAGE_REPLACEMENT);

      AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
      String template = getResources().getString(message);
      String fullMessage = String.format(template, getResources().getString(replacement));
      builder.setMessage(fullMessage)
          .setPositiveButton(android.R.string.ok, (dialog, id) -> ok())
          .setNegativeButton(android.R.string.cancel, (dialog, id) -> cancel());

      return builder.create();
    }
  }

  public static class AppDetailsDialogFragment extends SimpleDialogFragment {

    private static String ARG_PACKAGE = "package";

    private String packageName;

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState)
    {
      Dialog dialog = super.onCreateDialog(savedInstanceState);
      Bundle args = getArguments();
      packageName = args.getString(ARG_PACKAGE);
      return dialog;
    }

    @Override
    public void ok()
    {
      Intent intent = IntentFactory.createGooglePlayAppDetailsIntent(packageName);
      startActivity(intent);
    }

    @Override
    public void cancel()
    {
      // do nothing
    }

  }

}
