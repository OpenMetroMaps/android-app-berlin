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

package de.topobyte.android;

import android.content.Context;
import android.content.res.TypedArray;
import android.util.AttributeSet;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;

import java.util.ArrayList;
import java.util.List;

import de.topobyte.transportation.info.berlin.R;


public class FlexGridLayout extends ViewGroup {

  public FlexGridLayout(Context context)
  {
    this(context, null);
  }

  public FlexGridLayout(Context context, AttributeSet attrs)
  {
    this(context, attrs, 0);
  }

  private int gw = 200;

  public FlexGridLayout(Context context, AttributeSet attrs, int defStyle)
  {
    super(context, attrs, defStyle);

    TypedArray a = context.obtainStyledAttributes(attrs, R.styleable.FlexGridLayout, defStyle, 0);

    try {
      gw = Math.round(a.getDimension(R.styleable.FlexGridLayout_colwidth, 200));
      Log.d("flex", "desired column width: " + gw);
    } finally {
      a.recycle();
    }
  }

  @Override
  protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec)
  {
    super.onMeasure(widthMeasureSpec, heightMeasureSpec);

    Log.d("flex", "onMeasure. col width: " + gw);

    int sizeWidth = MeasureSpec.getSize(widthMeasureSpec) - getPaddingLeft() - getPaddingRight();
    int sizeHeight = MeasureSpec.getSize(heightMeasureSpec);
    Log.d("flex", "onMeasure. Size: " + sizeWidth + " x " + sizeHeight);

    int modeWidth = MeasureSpec.getMode(widthMeasureSpec);
    int modeHeight = MeasureSpec.getMode(heightMeasureSpec);

    int maxCols = sizeWidth / gw;
    Log.d("flex", "onMeasure. Max cols: " + maxCols);

    int colWidth = Math.round(sizeWidth / maxCols);
    int widthSpec = MeasureSpec.makeMeasureSpec(colWidth, MeasureSpec.AT_MOST);
    Log.d("flex", "onMeasure. col width: " + colWidth);

    int height = getPaddingTop() + getPaddingBottom();

    Grid grid = new Grid(this, maxCols);

    for (Row row : grid.getRows()) {
      int lineWidth = 0;
      int lineHeight = 0;

      int widthUsed = 0;
      for (View child : row) {
        measureChildWithMargins(child, widthSpec, 0, heightMeasureSpec, 0);
        MarginLayoutParams lp = (MarginLayoutParams) child.getLayoutParams();

        int childWidthMode = MeasureSpec.EXACTLY;
        int childWidthSize = colWidth - lp.leftMargin - lp.rightMargin;

        int childHeightMode = MeasureSpec.AT_MOST;
        int childHeightSize = sizeHeight;

        if (sizeHeight == 0) {
          childHeightMode = MeasureSpec.UNSPECIFIED;
        }

        child.measure(
            MeasureSpec.makeMeasureSpec(childWidthSize, childWidthMode),
            MeasureSpec.makeMeasureSpec(childHeightSize, childHeightMode)
        );

        int childWidth = child.getMeasuredWidth() + lp.leftMargin + lp.rightMargin;
        lineWidth += colWidth;
        lineHeight = Math.max(lineHeight, child.getMeasuredHeight() + lp.topMargin + lp.bottomMargin);
      }

      height += lineHeight;
    }

    setMeasuredDimension(sizeWidth,
        (modeHeight == MeasureSpec.EXACTLY) ? sizeHeight : height);
  }

  @Override
  protected void onLayout(boolean changed, int l, int t, int r, int b)
  {
    int width = getWidth();
    int height = getHeight();
    Log.d("flex", "onLayout. Size: " + width + " x " + height);

    int maxCols = width / gw;
    Log.d("flex", "onLayout. Max cols: " + maxCols);
    int colWidth = Math.round(width / maxCols);
    Log.d("flex", "onLayout. Col width: " + colWidth);

    Grid grid = new Grid(this, maxCols);

    int linesSum = getPaddingTop();

    List<Integer> lineHeights = new ArrayList<>();
    for (Row row : grid.getRows()) {
      int maxHeight = 0;
      for (View child : row) {
        MarginLayoutParams lp = (MarginLayoutParams) child.getLayoutParams();

        int childWidth = child.getMeasuredWidth() + lp.leftMargin + lp.rightMargin;
        int childHeight = child.getMeasuredHeight() + lp.bottomMargin + lp.topMargin;

        Log.d("flex", "Child: " + childWidth + " x " + childHeight);

        maxHeight = Math.max(maxHeight, childHeight);
      }
      lineHeights.add(maxHeight);
    }

    int left;
    int top = getPaddingTop();

    for (int i = 0; i < grid.getRows().size(); i++) {
      Row row = grid.getRows().get(i);
      int lineHeight = lineHeights.get(i);

      left = getPaddingLeft();
      for (View child : row) {
        MarginLayoutParams lp = (MarginLayoutParams) child.getLayoutParams();
        // if height is match_parent we need to remeasure child to line height
        if (lp.height == LayoutParams.MATCH_PARENT) {
          int childWidthMode = MeasureSpec.AT_MOST;
          int childWidthSize = colWidth;

          child.measure(
              MeasureSpec.makeMeasureSpec(childWidthSize, childWidthMode),
              MeasureSpec.makeMeasureSpec(lineHeight - lp.topMargin - lp.bottomMargin, MeasureSpec.EXACTLY)
          );
        }
        int childWidth = child.getMeasuredWidth();
        int childHeight = child.getMeasuredHeight();

        child.layout(left + lp.leftMargin,
            top + lp.topMargin,
            left + childWidth + lp.leftMargin,
            top + childHeight + lp.topMargin);

        left += colWidth;
      }
      top += lineHeight;
    }
  }

  @Override
  protected LayoutParams generateLayoutParams(ViewGroup.LayoutParams p)
  {
    return new LayoutParams(p);
  }

  @Override
  public LayoutParams generateLayoutParams(AttributeSet attrs)
  {
    return new MarginLayoutParams(getContext(), attrs);
  }

  @Override
  protected LayoutParams generateDefaultLayoutParams()
  {
    return new MarginLayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.MATCH_PARENT);
  }

}
