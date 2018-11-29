package org.androidsocket.Interfaces;

import android.content.Context;

public interface IAdapter {
    String[] getRowContent(Object rowData);
    Context getContext();
}
