package com.honeywell.hch.airtouchv3.framework.webservice.task;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.os.Environment;

import com.honeywell.hch.airtouchv3.HPlusApplication;
import com.honeywell.hch.airtouchv3.app.airtouch.model.tccmodel.weather.WeatherPageData;
import com.honeywell.hch.airtouchv3.framework.app.AppManager;
import com.honeywell.hch.airtouchv3.framework.config.AppConfig;
import com.honeywell.hch.airtouchv3.framework.model.UserLocationData;
import com.honeywell.hch.airtouchv3.framework.model.xinzhi.Now;
import com.honeywell.hch.airtouchv3.lib.util.BitmapUtil;
import com.honeywell.hch.airtouchv3.lib.util.BlurImageUtil;
import com.honeywell.hch.airtouchv3.lib.util.FileUtils;
import com.honeywell.hch.airtouchv3.lib.util.LogUtil;
import com.honeywell.hch.airtouchv3.lib.util.StringUtil;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.net.ssl.HttpsURLConnection;

/**
 * Created by wuyuan on 9/10/15.
 */
public class DownloadBackgroundTask extends AsyncTask<Object, Object, Object> {

    private static final String CHINA_CITYCODE_PRE = "CH";
    private static final String INDIA_CITYCODE_PRE = "IN";

    private static final String CHINA_CITYBACKGROUND_NAME = "China";
    private static final String INDIA_CITYBACKGROUND_NAME = "India";

    private static final int ALL_CITY_IS_CHINA = 0;

    private static final int ALL_CITY_IS_INDIA = 1;

    private static final int BOTH_CHINA_AND_INDIA = 2;

    private static final String BLUR_FILE_INDEX = "blur-";

    private Context mContext;

    private String localRootPath;
    private String mLocalVersionJsonPath;

    private Set<String> mLocalCityCodeList = new HashSet<>();

    private Map<String, String> serverVersionMap = new HashMap<>();
    private JSONArray mLocalVersionJsonArray = new JSONArray();
//    private JSONArray mLocalVersionWillWritedJsonArray = new JSONArray();

    private static final String CHINA_CITY_SERVER_URL = "https://hch.blob.core.chinacloudapi.cn/airtouchscitybackground/";
    private static final String INDIA_CITY_SERVER_URL = "https://hch.blob.core.chinacloudapi.cn/indiaairtouchscitybackground/";

    private static final String[] COUNRTRY_SERVER_URL = {CHINA_CITY_SERVER_URL,INDIA_CITY_SERVER_URL};

    private static final String[] BACKGROUND_PRE_URL = {FileUtils.BACKGROUND_FILE_PATH,FileUtils.INDIA_BACKGROUND_FILE_PATH};

    private static final String[] BACKGROUND_BLUR_PRE_URL = {FileUtils.BACKGROUND_BLUR_FILE_PATH,FileUtils.INDIA_BACKGROUND_BLUR_FILE_PATH};


    private static final String VERSION_URL = "city.txt";

    private static final String IAMGE_NAME_KEY = "image";

    private static final String IMAGE_VERSION = "version";

    private static final String DEFAULT_CITY_KEY = "Default";

    private JSONArray bitmapJsonArray = null;

    private boolean isRunning = false;

    private boolean isDefaultCity = false;

    private String mBlurBackgroundPath = "";

    private int mCountryCount = 1;

    public DownloadBackgroundTask() {
        mContext = HPlusApplication.getInstance().getApplicationContext();
    }


    @Override
    protected Object doInBackground(Object... params) {

        try {
            isRunning = true;

            //get local city list after login success
            getLocalCityCodeList();

            getCountryCount();

            //consider use chinese account in other country.
            for (int i = 0; i < mCountryCount;i++){
                resetData();
                if(!constructLocationPath(i)){
                    continue;
                }


                mLocalVersionJsonPath = localRootPath + File.separator + VERSION_URL;

                //判断是否有文件。
                File versionJsonFile = new File(localRootPath, VERSION_URL);
                //get local version.txt content
                if (versionJsonFile.exists()) {
                    readLocalCityFile(mLocalVersionJsonPath);
                }

                //get the version data from server
                if (!getMapForJson(getServerJsonFile(getServerPath(i) + VERSION_URL), serverVersionMap)) {
                    setBackgroundListWhenNoServerList();
                    return null;
                }

                downLoadFileAndWriteJsonToLocal(i);

                setBackgroundListWhenHasServerList();
            }

        }catch (Exception e){
            LogUtil.log(LogUtil.LogLevel.ERROR,"Download","doInBackground exception = " + e.toString());
        }

        return null;
    }

    private void resetData(){
        serverVersionMap.clear();
        mLocalVersionJsonArray = null;
    }

    private boolean constructLocationPath(int index){

        File bgFile = createDirFile(Environment.getExternalStorageDirectory().getPath(), FileUtils.BACKGROUND_FILE_PATH);
        if(bgFile == null){
            return false;
        }

        String path = getLocationLastPathName(index);

        File bgFile3 = createDirFile(bgFile.getAbsolutePath(), path);
        if (bgFile3 == null) {
            return false;
        }

        localRootPath = bgFile3.getAbsolutePath();

        File bgFile2 = createDirFile(bgFile.getAbsolutePath(), getLocationLastPathBlurName(index));
        if (bgFile2 == null) {
            return false;
        }
        mBlurBackgroundPath = bgFile2.getAbsolutePath();
        return true;
    }

    private void getCountryCount(){
        if (isAllCityIsChina() || isAllCityIsIndia()){
            mCountryCount = 1;
        }
        else{
            mCountryCount = 2;
        }
    }

    private String getServerPath(int index){
        int cityAllBelong = getLocalCityList();
        if (cityAllBelong == ALL_CITY_IS_CHINA){
            return CHINA_CITY_SERVER_URL;
        }
        else if(cityAllBelong == ALL_CITY_IS_INDIA){
            return INDIA_CITY_SERVER_URL;
        }
        else{
           return COUNRTRY_SERVER_URL[index];
        }
    }

    private String getLocationLastPathName(int index){
        int cityAllBelong = getLocalCityList();
        if (cityAllBelong == ALL_CITY_IS_CHINA){
           return FileUtils.BACKGROUND_FILE_PATH;
        }
        else if(cityAllBelong == ALL_CITY_IS_INDIA){
            return FileUtils.INDIA_BACKGROUND_FILE_PATH;
        }
        else{
            return BACKGROUND_PRE_URL[index];
        }
    }

    private String getLocationLastPathBlurName(int index){
        int cityAllBelong = getLocalCityList();
        if (cityAllBelong == ALL_CITY_IS_CHINA){
            return FileUtils.BACKGROUND_BLUR_FILE_PATH;
        }
        else if(cityAllBelong == ALL_CITY_IS_INDIA){
            return FileUtils.INDIA_BACKGROUND_BLUR_FILE_PATH;
        }
        else{
            return BACKGROUND_BLUR_PRE_URL[index];
        }
    }

    private int getLocalCityList(){
        if(isAllCityIsChina()){
            return ALL_CITY_IS_CHINA;
        }
        else if(isAllCityIsIndia()){
            return ALL_CITY_IS_INDIA;
        }
        else{
            return BOTH_CHINA_AND_INDIA;
        }

    }

    private boolean isAllCityIsChina() {
        boolean result = true;
        for(String cityCode : mLocalCityCodeList) {
            if (cityCode.startsWith(CHINA_CITYCODE_PRE)) {
                continue;
            }
            else {
                result = false;
            }
        }
        return result;
    }

    private boolean isAllCityIsIndia() {
        boolean result = true;
        for(String cityCode : mLocalCityCodeList) {
            if (cityCode.startsWith(INDIA_CITYCODE_PRE)) {
                continue;
            }
            else {
                result = false;
            }
        }
        return result;
    }

    @Override
    protected void onPostExecute(Object responseResult) {
        isRunning = false;
        super.onPostExecute(responseResult);
    }

    private File createDirFile(String rootPath,String fileName){
        File bgFile = new File(rootPath, fileName);
        if (!bgFile.exists() && !bgFile.mkdir()) {
            return null;
        }
        return bgFile;
    }


    public boolean isRunning() {
        return isRunning;
    }

    private void downLoadFileAndWriteJsonToLocal(int index) {
        try {
            for (String cityCode : mLocalCityCodeList) {
                isDefaultCity = false;
                //if the version of the city is not same as the server.
                if (!isHasSameCityVersion(cityCode) && isTheSameCountry(cityCode)) {
                    boolean isDownSuccess = false;
                    String orialCityCode = cityCode;
                    cityCode = isDefaultCity ? "Default" : cityCode;
                    //down load the citycode.txt,get the json,and check the different
                    if (getCityImageJsonFile(cityCode,index)) {
                        JSONArray localJsonArrayList = getJsonArrayOfTheFile(localRootPath + File.separator + cityCode + ".txt");

                        for (int i = 0; i < bitmapJsonArray.length(); i++) {
                            JSONObject serverBgOb = bitmapJsonArray.getJSONObject(i);
                            String imageName = serverBgOb.getString("image");
                            if (!isLocalJsonArrayContain(imageName, localJsonArrayList)) {
                                //down load the file and wirte to the local citycode.txt
                                LogUtil.log(LogUtil.LogLevel.INFO, "DownLoad", "begin to down file =" + imageName);
                                downloadBackground(imageName, index);
                                LogUtil.log(LogUtil.LogLevel.INFO, "DownLoad", "end to down file =" + imageName);
                                writeFile(localRootPath + File.separator + cityCode + ".txt", serverBgOb.toString(), true);
                                saveBlurBackground(localRootPath + File.separator + imageName, imageName);
//                                writeTheImageToBackgroundList(localRootPath + File.separator + imageName, orialCityCode, imageName);
//                                updateTheSameCityCode(orialCityCode, cityCode,localRootPath + File.separator + imageName, imageName);
                                isDownSuccess = true;
                            }
                        }
                    }

                    if (isDownSuccess){
                        //if all file down load success,write the version to the city.txt
                        String cityVersion = serverVersionMap.get(cityCode);
                        JSONObject jsonObject = new JSONObject();
                        jsonObject.put(cityCode, cityVersion);
                        if (mLocalVersionJsonArray == null){
                            mLocalVersionJsonArray = new JSONArray();
                        }
                        mLocalVersionJsonArray.put(jsonObject);
                    }

                }

            }
        } catch (Exception e) {
            LogUtil.log(LogUtil.LogLevel.ERROR, "DownLoadBackground", e.toString());
        }

        try{

            if (mLocalVersionJsonArray != null && mLocalVersionJsonArray.length() > 0){
                //clear and rewrite
                File file = new File(localRootPath + File.separator + VERSION_URL);

                if (file.exists()) {
                    file.delete();
                }

                writeFile(localRootPath + File.separator + VERSION_URL, mLocalVersionJsonArray.toString(), false);
                mLocalVersionJsonArray = null;
            }
        }
        catch (Exception e){

        }

    }


    private void writeTheImageToBackgroundList(String filePath, String cityCode,String fileName) {
        UserLocationData gpsUserLocationData = AppManager.shareInstance().getGpsUserLocation();
        setBackgroundFileToCityBackground(gpsUserLocationData,filePath,cityCode,fileName);

        List<UserLocationData> userLocationDataList = AppManager.shareInstance().getUserLocationDataList();
        for (UserLocationData userLocationData : userLocationDataList) {
            setBackgroundFileToCityBackground(userLocationData,filePath,cityCode,fileName);
        }
    }

    private void setBackgroundFileToCityBackground(UserLocationData willAddLocationData,String filePath, String cityCode,String fileName){
        if (cityCode.equalsIgnoreCase(willAddLocationData.getCity())) {
            setBlurAndCityPath(willAddLocationData, filePath, fileName);
        }

    }

    private void setBlurAndCityPath(UserLocationData willAddLocationData,String filePath,String fileName){
        willAddLocationData.getCityBackgroundDta().addItemToCityPathList(filePath);

        String blurName = BLUR_FILE_INDEX + fileName;
        willAddLocationData.getCityBackgroundDta().addItemToBlurList(mBlurBackgroundPath + File.separator + blurName);
        updateBackgroundList(willAddLocationData);
    }

    private boolean isLocalJsonArrayContain(String imageName, JSONArray localJsonArrayList) {
        try {
            if (localJsonArrayList != null && localJsonArrayList.length() > 0) {
                for (int i = 0; i < localJsonArrayList.length(); i++) {
                    JSONObject localBgOb = localJsonArrayList.getJSONObject(i);
                    String localImageName = localBgOb.getString("image");
                    if (imageName != null && imageName.equalsIgnoreCase(localImageName)) {
                        return true;
                    }
                }

            }
        } catch (Exception e) {

        }

        return false;
    }

    private boolean isTheSameCountry(String cityCode){
        if (cityCode.startsWith(CHINA_CITYCODE_PRE) && mBlurBackgroundPath.contains(FileUtils.BACKGROUND_BLUR_FILE_PATH)){
            return true;
        }
        else if(cityCode.startsWith(INDIA_CITYCODE_PRE) && mBlurBackgroundPath.contains(FileUtils.INDIA_BACKGROUND_BLUR_FILE_PATH)){
            return true;
        }

        return false;
    }

    private boolean isDefaultCity(String cityCode){
        String cityFileServerVersion = serverVersionMap.get(cityCode);
        if (StringUtil.isEmpty(cityFileServerVersion)) {
            return true;
        }
        return false;
    }

    private boolean isHasSameCityVersion(String cityCode) {

        String cityFileServerVersion = serverVersionMap.get(cityCode);
        if (StringUtil.isEmpty(cityFileServerVersion)) {
            cityFileServerVersion = serverVersionMap.get(DEFAULT_CITY_KEY);
            isDefaultCity = true;
        }

        if (mLocalVersionJsonArray != null){
            int removeIndex = -1;
            for (int i = 0; i < mLocalVersionJsonArray.length(); i++){
                try{
                    JSONObject jsonObject = mLocalVersionJsonArray.getJSONObject(i);
                    String cityFileLocalVersion = "";
                    if (!isDefaultCity) {
                        cityFileLocalVersion = jsonObject.getString(cityCode);
                    }
                    else{
                        cityFileLocalVersion = jsonObject.getString(DEFAULT_CITY_KEY);
                    }
                    if (cityFileServerVersion != null && cityFileServerVersion.equalsIgnoreCase(cityFileLocalVersion)) {
                        return true;
                    }
                    else if (!StringUtil.isEmpty(cityFileLocalVersion) && !cityFileServerVersion.equalsIgnoreCase(cityFileLocalVersion)){
                        //have the different version between server and local.
                        removeIndex = i;
                    }
                }
                catch (Exception e){
                    LogUtil.log(LogUtil.LogLevel.INFO, "isHasSameCityVersion", e.toString());
                }

            }
            if (removeIndex != -1){
                mLocalVersionJsonArray.remove(removeIndex);
            }
        }

        return false;
    }

    private boolean getCityImageJsonFile(String cityCode,int index) {
        if (bitmapJsonArray != null) {
            bitmapJsonArray = null;
        }

        try {
            String cityImageJsonFile = getServerPath(index) + cityCode + ".txt";
            String jsonString = getServerJsonFile(cityImageJsonFile);
            bitmapJsonArray = new JSONArray(jsonString);
        } catch (Exception e) {
            LogUtil.log(LogUtil.LogLevel.ERROR, "Download", "Exception e =" + e.toString());
            return false;
        }
        return true;
    }

    private void getLocalCityCodeList() {
        String currentCityCode = AppConfig.shareInstance().getGpsCityCode();
        if (!StringUtil.isEmpty(currentCityCode)) {
            mLocalCityCodeList.add(currentCityCode);
        }
        List<UserLocationData> userLocationDataList = AppManager.shareInstance().getUserLocationDataList();
        for (UserLocationData userLocationData : userLocationDataList) {
            mLocalCityCodeList.add(userLocationData.getCity());
        }
    }

    private String getServerJsonFile(String uri) {
        String resultString = "";
        try {

            URL url = new URL(uri);
            HttpsURLConnection conn = (HttpsURLConnection) url.openConnection();
            conn.setConnectTimeout(300 * 1000);
            conn.setReadTimeout(300 * 1000);
            conn.setRequestMethod("GET");
            InputStream inStream = conn.getInputStream();
            if (conn.getResponseCode() == HttpURLConnection.HTTP_OK) {
                BufferedReader reader = new BufferedReader(new InputStreamReader(inStream));
                StringBuilder resultBuilder = new StringBuilder();
                String line = null;
                while ((line = reader.readLine()) != null) {
                    resultBuilder.append(line);
                }
                resultString = resultBuilder.toString();
                inStream.close();
            } else {
            }
        } catch (Exception e) {
            e.printStackTrace();
            LogUtil.log(LogUtil.LogLevel.ERROR, "DownLoadBackground", "getServerJsonFile error: " + e.toString());
        }
        return resultString;
    }

    private JSONArray getJsonArrayOfTheFile(String localFilePath) {

        JSONArray localImageJsonArray = null;
        try {
            File file = new File(localFilePath);

            if (!file.exists()) {
                file.createNewFile();
            }

            String jsonString = readLocalJsonFile(localFilePath);
            localImageJsonArray = new JSONArray(jsonString);
        } catch (Exception e) {

        }
        return localImageJsonArray;
    }

    private String readLocalJsonFile(String localFilePath) {
        String resultString = "";
        File file = new File(localFilePath);
        FileInputStream fis = null;
        try {
            fis = new FileInputStream(file);


            BufferedReader reader = new BufferedReader(new InputStreamReader(fis));
            StringBuilder resultBuilder = new StringBuilder();
            String line = null;
            while ((line = reader.readLine()) != null) {
                resultBuilder.append(line);
            }
            resultString = resultBuilder.toString();

        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            if (fis != null) {
                try {
                    fis.close();
                } catch (Exception e) {

                }

            }
        }
        return resultString;
    }

    public void readLocalCityFile(String filePath) {
       mLocalVersionJsonArray = getJsonArrayOfTheFile(filePath);
    }

    //写文件
    public void writeFile(String fileName, String write_str, boolean isAppend) throws IOException {

        File file = new File(fileName);

        if (!file.exists()) {
            file.createNewFile();
        }

        FileOutputStream fos = new FileOutputStream(file, isAppend);

        byte[] bytes = write_str.getBytes();

        fos.write(bytes);

        fos.close();
    }

    /**
     * {    "CHGD000000":"1444802026",    "Default":"1444802026"} to map
     * @param jsonStr
     * @param map
     * @return
     */
    public boolean getMapForJson(String jsonStr, Map<String, String> map) {
        JSONObject jsonObject;
        try {
            jsonObject = new JSONObject(jsonStr);

            Iterator<String> keyIter = jsonObject.keys();
            String key;
            String value;
            while (keyIter.hasNext()) {
                key = keyIter.next();
                value = (String) jsonObject.get(key);
                map.put(key, value);
            }
        } catch (Exception e) {
            // TODO: handle exception
            e.printStackTrace();
            return false;
        }
        return true;
    }


    private void downloadBackground(String fileName,int index) throws Exception {

        byte[] data = getImage(getServerPath(index) + fileName);
        if (data != null) {

            Bitmap mBitmap = BitmapFactory.decodeByteArray(data, 0, data.length);
            FileUtils.saveFile(mBitmap, localRootPath + "/" + fileName);
        }

    }

    /**
     * --------------------------------------------------------
     * <p/>
     * //    download the file
     * <p/>
     * /**
     * Get image from newwork
     *
     * @param path The path of image
     * @return byte[]
     * @throws Exception
     */
    public byte[] getImage(String path) throws Exception {
        URL url = new URL(path);
        HttpsURLConnection conn = (HttpsURLConnection) url.openConnection();
        conn.setConnectTimeout(300 * 1000);
        conn.setReadTimeout(300 * 1000);
        conn.setRequestMethod("GET");
        InputStream inStream = conn.getInputStream();
        if (conn.getResponseCode() == HttpURLConnection.HTTP_OK) {
            return FileUtils.readStream(inStream);
        }
        return null;
    }




    public void setBackgroundListWhenHasServerList() {

        File bgFile = new File(localRootPath);
        if (bgFile != null && bgFile.exists() && bgFile.listFiles() != null) {
            File[] fileList = bgFile.listFiles();
            List<UserLocationData> userLocationDataList = AppManager.shareInstance().getUserLocationDataList();
            for (UserLocationData userLocationData : userLocationDataList) {
                addDifferentTypePath(fileList,userLocationData);
            }

            addDifferentTypePath(fileList, AppManager.shareInstance().getGpsUserLocation());
        }

    }

    private void addDifferentTypePath(File[] fileList,UserLocationData userLocationData){

        if (fileList != null &&
                fileList.length > 0 &&
                fileList[0].getName() != null &&
                ((!fileList[0].getAbsolutePath().contains(FileUtils.INDIA_BACKGROUND_FILE_PATH) && userLocationData.getCity().contains(CHINA_CITYCODE_PRE))
                ||(fileList[0].getAbsolutePath().contains(FileUtils.INDIA_BACKGROUND_FILE_PATH) && userLocationData.getCity().contains(INDIA_CITYCODE_PRE)) ))
        {
            if (StringUtil.isEmpty(serverVersionMap.get(userLocationData.getCity()))) {
                //add default
                addPathListToLocationDataItem(fileList,userLocationData,true);
            } else {
                //add citycode background
                addPathListToLocationDataItem(fileList, userLocationData, false);
            }
            updateBackgroundList(userLocationData);
        }

    }


    public void setBackgroundListWhenNoServerList(){
        File bgFile = new File(localRootPath);
        if (bgFile != null && bgFile.exists()) {
            File[] fileList = bgFile.listFiles();
            List<UserLocationData> userLocationDataList = AppManager.shareInstance().getUserLocationDataList();
            for (UserLocationData userLocationData : userLocationDataList) {
                if (!StringUtil.isEmpty(getLocalVersionString(userLocationData.getCity()))) {
                    //add citycode background
                    addPathListToLocationDataItem(fileList,userLocationData,false);
                }
            }

            UserLocationData gpsLocation = AppManager.shareInstance().getGpsUserLocation();
            if (!StringUtil.isEmpty(getLocalVersionString(gpsLocation.getCity()))) {
                //add citycode background
                addPathListToLocationDataItem(fileList,gpsLocation,false);
            }
        }
    }

    private String getLocalVersionString(String cityCode){
        String cityVersion = "";
        if (mLocalVersionJsonArray != null){
            for (int i = 0; i < mLocalVersionJsonArray.length(); i++){
                try{
                    JSONObject jsonObject = mLocalVersionJsonArray.getJSONObject(i);
                    cityVersion = jsonObject.getString(cityCode);
                }
                catch (Exception e){

                }
            }
        }
        return cityVersion;
    }

    private void addPathListToLocationDataItem(File[] files, UserLocationData userLocationDataItem, boolean isDefault) {

        if (files != null &&
                files.length > 0 &&
                files[0].getName() != null &&
                ((!files[0].getAbsolutePath().contains(FileUtils.INDIA_BACKGROUND_FILE_PATH) && userLocationDataItem.getCity().contains(CHINA_CITYCODE_PRE))
                        ||(files[0].getAbsolutePath().contains(FileUtils.INDIA_BACKGROUND_FILE_PATH) && userLocationDataItem.getCity().contains(INDIA_CITYCODE_PRE)) ))
        {
            for (File file : files) {
                if (!isLocationDataItemHasThisFile(file,userLocationDataItem)){
                    if (isDefault && file.getName().contains(DEFAULT_CITY_KEY) && !file.getName().contains(".txt")
                            || !isDefault && file.getName().contains(userLocationDataItem.getCity()) && !file.getName().contains(".txt")) {
                        userLocationDataItem.getCityBackgroundDta().addItemToCityPathList(file.getAbsolutePath());

                        String blurName = BLUR_FILE_INDEX + file.getName();
                        userLocationDataItem.getCityBackgroundDta().addItemToBlurList(mBlurBackgroundPath + File.separator + blurName);
                    }
                }
            }
            updateBackgroundList(userLocationDataItem);
        }

    }

    private void updateBackgroundList(UserLocationData userLocationData){
        int weatherCode = 0;
        WeatherPageData weatherData = userLocationData.getCityWeatherData();
        if (weatherData != null && weatherData.getWeather() != null){
            Now now = weatherData.getWeather().getNow();
            if (now != null){
                weatherCode = now.getCode();
            }
        }
        userLocationData.getCityBackgroundDta().initmCityBackgroundObjectListList(weatherCode, false);
    }

    private boolean isLocationDataItemHasThisFile(File files, UserLocationData userLocationDataItem){
        List<String> backgroundList = userLocationDataItem.getCityBackgroundDta().getCityBackgroundPathList();
        if (backgroundList.contains(files.getAbsolutePath())){
            return true;
        }
        return false;
    }

    private void saveBlurBackground(String backgroundPath,String backgroundFileName){

        Bitmap mBackgroundBitmap = BitmapUtil.createBitmapEffectlyFromPath(mContext, backgroundPath);
        Bitmap mBlurBitmap = BlurImageUtil.fastblur(mContext, mBackgroundBitmap, BlurImageUtil.MAIN_ACTVITIY_BLUR_RADIO);
        if (mBackgroundBitmap != null) {
            mBackgroundBitmap.recycle();
        }

        if (mBlurBitmap != null){
            backgroundFileName = BLUR_FILE_INDEX + backgroundFileName;

            FileUtils.saveFile(mBlurBitmap,mBlurBackgroundPath + File.separator + backgroundFileName);
            if (mBlurBitmap != null) {
                mBlurBitmap.recycle();
            }
        }

    }



}
