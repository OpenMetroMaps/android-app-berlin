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

package de.topobyte.android.util;

import android.text.Editable;
import android.text.TextWatcher;
import android.widget.EditText;

public class TextWatcherAdapter implements TextWatcher {

  public interface TextWatcherListener {

    void onTextChanged(EditText view, String text);

  }

  private final EditText view;
  private final TextWatcherListener listener;

  public TextWatcherAdapter(EditText editText, TextWatcherListener listener)
  {
    this.view = editText;
    this.listener = listener;
  }

  @Override
  public void onTextChanged(CharSequence s, int start, int before, int count)
  {
    listener.onTextChanged(view, s.toString());
  }

  @Override
  public void beforeTextChanged(CharSequence s, int start, int count,
                                int after)
  {
    // ignore
  }

  @Override
  public void afterTextChanged(Editable s)
  {
    // ignore
  }

}