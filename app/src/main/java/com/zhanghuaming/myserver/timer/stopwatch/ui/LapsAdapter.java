/*
 * Copyright 2017 Phillip Hsu
 *
 * This file is part of ClockPlus.
 *
 * ClockPlus is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * ClockPlus is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with ClockPlus.  If not, see <http://www.gnu.org/licenses/>.
 */

package com.zhanghuaming.myserver.timer.stopwatch.ui;

import android.view.ViewGroup;

import com.zhanghuaming.myserver.timer.list.BaseCursorAdapter;
import com.zhanghuaming.myserver.timer.list.OnListItemInteractionListener;
import com.zhanghuaming.myserver.timer.stopwatch.Lap;
import com.zhanghuaming.myserver.timer.stopwatch.data.LapCursor;


/**
 * Created by Phillip Hsu on 8/9/2016.
 */
public class LapsAdapter extends BaseCursorAdapter<Lap, LapViewHolder, LapCursor> {
    public static final int VIEW_TYPE_FIRST_LAP = 1; // TOneverDO: 0, that's the default view type

    public LapsAdapter() {
        super(null/*OnListItemInteractionListener*/);
    }

    @Override
    protected LapViewHolder onCreateViewHolder(ViewGroup parent, OnListItemInteractionListener<Lap> listener, int viewType) {
        // TODO: Consider defining a view type for the lone first lap. We should persist this first lap
        // when is is created, but this view type will tell us it should not be visible until there
        // are at least two laps.
        // Or could we return null? Probably not because that will get passed into onBindVH, unless
        // you check for null before accessing it.
        // RecyclerView.ViewHolder has an internal field that holds viewType for us,
        // and can be retrieved by the instance via getItemViewType().
        return new LapViewHolder(parent);
    }

    @Override
    public int getItemViewType(int position) {
        return getItemCount() == 1 ? VIEW_TYPE_FIRST_LAP : super.getItemViewType(position);
    }
}
