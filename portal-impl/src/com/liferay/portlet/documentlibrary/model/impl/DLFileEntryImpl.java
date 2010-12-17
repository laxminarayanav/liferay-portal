/**
 * Copyright (c) 2000-2010 Liferay, Inc. All rights reserved.
 *
 * This library is free software; you can redistribute it and/or modify it under
 * the terms of the GNU Lesser General Public License as published by the Free
 * Software Foundation; either version 2.1 of the License, or (at your option)
 * any later version.
 *
 * This library is distributed in the hope that it will be useful, but WITHOUT
 * ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS
 * FOR A PARTICULAR PURPOSE. See the GNU Lesser General Public License for more
 * details.
 */

package com.liferay.portlet.documentlibrary.model.impl;

import com.liferay.portal.kernel.exception.PortalException;
import com.liferay.portal.kernel.exception.SystemException;
import com.liferay.portal.kernel.log.Log;
import com.liferay.portal.kernel.log.LogFactoryUtil;
import com.liferay.portal.kernel.util.FileUtil;
import com.liferay.portal.kernel.util.GetterUtil;
import com.liferay.portal.kernel.util.StringBundler;
import com.liferay.portal.kernel.util.StringPool;
import com.liferay.portal.kernel.util.UnicodeProperties;
import com.liferay.portal.model.Lock;
import com.liferay.portal.service.LockLocalServiceUtil;
import com.liferay.portlet.documentlibrary.model.DLFileEntry;
import com.liferay.portlet.documentlibrary.model.DLFileVersion;
import com.liferay.portlet.documentlibrary.model.DLFolder;
import com.liferay.portlet.documentlibrary.model.DLFolderConstants;
import com.liferay.portlet.documentlibrary.service.DLRepositoryLocalServiceUtil;
import com.liferay.portlet.documentlibrary.service.DLRepositoryServiceUtil;
import com.liferay.portlet.documentlibrary.util.DLUtil;

import java.io.IOException;
import java.io.InputStream;

import java.util.Iterator;
import java.util.List;
import java.util.Map;

/**
 * @author Brian Wing Shun Chan
 * @author Alexander Chow
 */
public class DLFileEntryImpl
	extends DLFileEntryModelImpl implements DLFileEntry {

	public DLFileEntryImpl() {
	}

	public InputStream getContentStream()
		throws PortalException, SystemException {

		return getContentStream(getVersion());
	}

	public InputStream getContentStream(String version)
		throws PortalException, SystemException {

		return DLRepositoryServiceUtil.getFileAsStream(
			getFileEntryId(), version);
	}

	public String getExtraSettings() {
		if (_extraSettingsProperties == null) {
			return super.getExtraSettings();
		}
		else {
			return _extraSettingsProperties.toString();
		}
	}

	public UnicodeProperties getExtraSettingsProperties() {
		if (_extraSettingsProperties == null) {
			_extraSettingsProperties = new UnicodeProperties(true);

			try {
				_extraSettingsProperties.load(super.getExtraSettings());
			}
			catch (IOException ioe) {
				_log.error(ioe, ioe);
			}
		}

		return _extraSettingsProperties;
	}

	public DLFileVersion getFileVersion()
		throws PortalException, SystemException {

		return getFileVersion(getVersion());
	}

	public DLFileVersion getFileVersion(String version)
		throws PortalException, SystemException {

		return DLRepositoryLocalServiceUtil.getFileVersion(
			getFileEntryId(), version);
	}

	public List<DLFileVersion> getFileVersions(int status)
		throws SystemException {

		return DLRepositoryLocalServiceUtil.getFileVersions(
			getFileEntryId(), status);
	}

	public DLFolder getFolder() {
		DLFolder folder = null;

		if (getFolderId() > 0) {
			try {
				folder = DLRepositoryLocalServiceUtil.getFolder(getFolderId());
			}
			catch (Exception e) {
				folder = new DLFolderImpl();

				_log.error(e, e);
			}
		}
		else {
			folder = new DLFolderImpl();
		}

		return folder;
	}

	public String getIcon() {
		return DLUtil.getFileIcon(getExtension());
	}

	public DLFileVersion getLatestFileVersion()
		throws PortalException, SystemException {

		return DLRepositoryLocalServiceUtil.getLatestFileVersion(
			getFileEntryId());
	}

	public Lock getLock() {
		try {
			return LockLocalServiceUtil.getLock(
				DLFileEntry.class.getName(), getFileEntryId());
		}
		catch (Exception e) {
		}

		return null;
	}

	public String getLuceneProperties() {
		UnicodeProperties extraSettingsProps = getExtraSettingsProperties();

		Iterator<Map.Entry<String, String>> itr =
			extraSettingsProps.entrySet().iterator();

		StringBundler sb = new StringBundler(
			extraSettingsProps.entrySet().size() + 4);

		sb.append(FileUtil.stripExtension(getTitle()));
		sb.append(StringPool.SPACE);
		sb.append(getDescription());
		sb.append(StringPool.SPACE);

		while (itr.hasNext()) {
			Map.Entry<String, String> entry = itr.next();

			String value = GetterUtil.getString(entry.getValue());

			sb.append(value);
		}

		return sb.toString();
	}

	public long getDataRepositoryId() {
		return DLFolderConstants.getDataRepositoryId(
			getGroupId(), getFolderId());
	}

	public boolean hasLock() {
		try {
			return DLRepositoryServiceUtil.hasFileEntryLock(getFileEntryId());
		}
		catch (Exception e) {
		}

		return false;
	}

	public boolean isLocked() {
		try {
			return DLRepositoryServiceUtil.isFileEntryLocked(getFileEntryId());
		}
		catch (Exception e) {
		}

		return false;
	}

	public void setExtraSettings(String extraSettings) {
		_extraSettingsProperties = null;

		super.setExtraSettings(extraSettings);
	}

	public void setExtraSettingsProperties(
		UnicodeProperties extraSettingsProperties) {

		_extraSettingsProperties = extraSettingsProperties;

		super.setExtraSettings(_extraSettingsProperties.toString());
	}

	private static Log _log = LogFactoryUtil.getLog(DLFileEntryImpl.class);

	private UnicodeProperties _extraSettingsProperties = null;

}