package de.topobyte.transportation.info;

import android.app.Application;

import org.openmetromaps.maps.MapModel;

import de.topobyte.transportation.info.model.ModelData;

public class TransportApp extends Application {

  private MapModel model;
  private ModelData data;

  @Override
  public void onCreate()
  {
    super.onCreate();

    model = ModelLoader.loadSafe(this);
    data = DataLoader.loadSafe(this, model);
  }

  public MapModel getModel()
  {
    return model;
  }

  public ModelData getData()
  {
    return data;
  }

}
