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

package de.topobyte.transportation.info;

import android.content.Context;
import android.content.res.AssetManager;
import android.util.Log;

import org.openmetromaps.maps.MapModel;

import java.io.BufferedInputStream;
import java.io.DataInputStream;
import java.io.IOException;
import java.io.InputStream;

import de.topobyte.opnv.model.ModelData;
import de.topobyte.opnv.model.ModelReader;

public class DataLoader {

    public static ModelData load(Context context, MapModel mapModel) throws IOException {
        AssetManager assets = context.getAssets();
        InputStream ais = assets.open("boroughs.data");

        BufferedInputStream bis = new BufferedInputStream(ais);
        DataInputStream dis = new DataInputStream(bis);

        ModelData data = ModelReader.read(dis, mapModel);
        dis.close();

        return data;
    }

    public static ModelData loadSafe(Context context, MapModel mapModel) {
        try {
            return load(context, mapModel);
        } catch (IOException e) {
            Log.e("data", "Unable to load data", e);
        }
        return null;
    }
}
