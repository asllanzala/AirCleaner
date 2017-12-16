package com.honeywell.hch.airtouchv3.framework.webservice;

import android.app.DownloadManager;
import android.os.AsyncTask;
import android.util.Log;

import com.honeywell.hch.airtouchv3.HPlusApplication;
import com.honeywell.hch.airtouchv3.VersionCollector;
import com.honeywell.hch.airtouchv3.lib.util.DownloadUtils;
import com.honeywell.hch.airtouchv3.lib.util.JSONSerializer;
import com.honeywell.hch.airtouchv3.lib.util.StringUtil;

public class UpgradeCheckThread extends AsyncTask<String, Integer, VersionCollector> {

	public static final String LOG_TAG = UpgradeCheckThread.class.getSimpleName();
	public static final String ANDROID_VERSION_JSON_URL_PATH = "https://eccap.honeywell.cn/App%20Software/version_Android.txt";
	
	public UpgradeCheckThread() {

	}

	@Override
	protected VersionCollector doInBackground(String... params) {
		try {
//			if (HPlusApplication.isTimeForUpgrade())
//			{
				// Check upgrade
				String jsonData = DownloadUtils.getInstance().downloadJsonData(ANDROID_VERSION_JSON_URL_PATH);
				VersionCollector versionData = parseVersionJsonData(jsonData);
				return versionData;
//			}
		} catch (Exception e) {
			e.printStackTrace();
			Log.e(LOG_TAG, "Download and parse error! " + e.getMessage());
		}

		return null;
	}
	
	@Override
	protected void onCancelled() {
		super.onCancelled();
	}

	@Override
	protected void onPostExecute(VersionCollector versionData) {
		if(versionData != null)
		{
			checkUpgrade(versionData);
		}
	}

	@Override
	protected void onPreExecute() {
		// 任务启动，可以在这里显示一个对话框，这里简单处理
	}

	@Override
	protected void onProgressUpdate(Integer... values) {
		// 更新进度
	}

	/**
	 * Parse the json file data.
	 * 
	 * @param jsonData
	 * @return android version class
	 */
	private VersionCollector parseVersionJsonData(String jsonData) {
		VersionCollector versionCollector = null;

		if (StringUtil.isEmpty(jsonData)) {
			Log.e(LOG_TAG, "JSON file data is null, can't be parsed");
		} else {
			Log.v(LOG_TAG, "Begin to parse JSON file data...");
			// Json解析开始
			versionCollector = new VersionCollector();
			JSONSerializer.deserializeJSONObject(versionCollector,
					JSONSerializer.parseJSONData(jsonData));
			Log.v(LOG_TAG,
					"Android Version Data:" + versionCollector.toString());
			Log.v(LOG_TAG, "Stop to parse JSON file data...");
		}

		return versionCollector;
	}

	private boolean checkUpgrade(VersionCollector versionData) {
//		if(HPlusApplication.canUpgrade(versionData) && HPlusApplication.isTimeForUpgrade())
//		{
			//推送显示版本更新的对话框
            boolean installSuccess = false;
            VersionCollector preVersionCollector = HPlusApplication.getVersionCollector();
            long preDownloadId = preVersionCollector.getDownloadId();
            int preDownloadStatus = DownloadUtils.getInstance().getDownloadStatusByDownloadId(preDownloadId);
            String preFileNameApk = DownloadUtils.getInstance().getDownloadFileNameByDownloadId(preDownloadId);
            String curFileNameApk = DownloadUtils.getInstance().getFileNameApk(versionData.version_name);

            if(preDownloadStatus == DownloadManager.STATUS_SUCCESSFUL && curFileNameApk.equals(preFileNameApk))
            {
                Log.v(LOG_TAG, "上次下载成功过，尝试直接安装");
                // 下载成功，但是上次没有安装
                installSuccess = DownloadUtils.getInstance().install(HPlusApplication.getInstance(), preDownloadId, HPlusApplication
			.getVersionCollector().version_name);
            }

            // 不能成功安装，2种情况：没下载过，或者下载的包被删除了
            if(!installSuccess)
            {
                Log.v(LOG_TAG, "不能直接安装，从新去下载");
                HPlusApplication.setVersionCollector(versionData);
                // 还没下载，开始去下载PK
                long downloadId = DownloadUtils.getInstance().startDownload(versionData);
                // 把新的downloadId存储起来
                HPlusApplication.getVersionCollector().setDownloadId(downloadId);
//                HPlusApplication.persistVersionCollector();
            }
			return true;
//		}
//		return false;
	}
	
	
}
