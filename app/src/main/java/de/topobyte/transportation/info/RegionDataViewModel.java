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

import android.app.Application;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class RegionDataViewModel extends AndroidViewModel {

  public enum Status {IDLE, LOADING, READY, ERROR}

  public static final class UiState {
    public final Status status;
    public final String region;
    public final RegionData data;
    public final Throwable error;

    public UiState(Status status, String region, RegionData data, Throwable error)
    {
      this.status = status;
      this.region = region;
      this.data = data;
      this.error = error;
    }

    public static UiState idle()
    {
      return new UiState(Status.IDLE, null, null, null);
    }

    public static UiState loading(String region)
    {
      return new UiState(Status.LOADING, region, null, null);
    }

    public static UiState ready(String region, RegionData data)
    {
      return new UiState(Status.READY, region, data, null);
    }

    public static UiState error(String region, Throwable t)
    {
      return new UiState(Status.ERROR, region, null, t);
    }
  }

  private final MutableLiveData<UiState> state = new MutableLiveData<>(UiState.idle());
  private final ExecutorService executor = Executors.newSingleThreadExecutor();

  // in-memory cache inside the VM (lives as long as the Activity scope)
  private String loadedRegion = null;
  private RegionData cachedData = null;

  // guard against late results overwriting newer requests
  private int requestVersion = 0;

  public RegionDataViewModel(@NonNull Application application)
  {
    super(application);
  }

  public LiveData<UiState> getState()
  {
    return state;
  }

  public synchronized void loadIfNeeded(@NonNull Region region)
  {
    UiState current = state.getValue();

    // Already have correct region loaded
    if (cachedData != null && region.equals(loadedRegion)) {
      if (current == null || current.status != Status.READY) {
        state.setValue(UiState.ready(region.getStringId(), cachedData));
      }
      return;
    }

    // If currently loading the same region, do nothing
    if (current != null && current.status == Status.LOADING && region.equals(current.region)) {
      return;
    }

    // Start new load
    final int myVersion = ++requestVersion;
    state.setValue(UiState.loading(region.getStringId()));

    executor.execute(() -> {
      try {
        RegionData data = RegionData.load(getApplication().getApplicationContext(), region);

        synchronized (RegionDataViewModel.this) {
          // Ignore if another load started after this one
          if (myVersion != requestVersion) return;

          loadedRegion = region.getStringId();
          cachedData = data;
        }

        state.postValue(UiState.ready(region.getStringId(), data));
      } catch (Throwable t) {
        synchronized (RegionDataViewModel.this) {
          if (myVersion != requestVersion) return;
        }
        state.postValue(UiState.error(region.getStringId(), t));
      }
    });
  }

  public synchronized RegionData getCachedDataOrNull()
  {
    return cachedData;
  }

  public synchronized String getLoadedRegionOrNull()
  {
    return loadedRegion;
  }

  @Override
  protected void onCleared()
  {
    super.onCleared();
    executor.shutdownNow();
  }
}
