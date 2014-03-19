package org.imogene.android.database;

import java.util.Date;

import org.imogene.android.common.entity.ImogBean;

import android.content.Context;
import android.database.Cursor;

public interface ImogBeanCursor extends Cursor /* CrossProcessCursor */{

	public ImogBean newBean();

	public String getId();

	public Date getModified();

	public String getModifiedBy();

	public String getModifiedFrom();

	public Date getUploadDate();

	public Date getCreated();

	public String getCreatedBy();

	public boolean getFlagRead();

	public boolean getFlagSynchronized();

	public String getMainDisplay(Context context);

	public String getSecondaryDisplay(Context context);
}
