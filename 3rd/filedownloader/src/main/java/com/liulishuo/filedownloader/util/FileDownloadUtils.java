/*
 * Copyright (c) 2015 LingoChamp Inc.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *    http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.liulishuo.filedownloader.util;

import android.app.ActivityManager;
import android.content.Context;
import android.content.pm.PackageManager;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Build;
import android.os.Environment;
import android.os.StatFs;
import android.text.TextUtils;

import com.liulishuo.filedownloader.services.FileDownloadService;
import com.liulishuo.filedownloader.stream.FileDownloadOutputStream;

import java.io.File;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.List;
import java.util.Locale;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * The utils for FileDownloader.
 */
@SuppressWarnings({"SameParameterValue", "WeakerAccess"})
public class FileDownloadUtils {

    private static int MIN_PROGRESS_STEP = 65536;
    private static long MIN_PROGRESS_TIME = 2000;

    /**
     * @param minProgressStep The minimum bytes interval in per step to sync to the file and the
     *                        database.
     *                        <p>
     *                        Used for adjudging whether is time to sync the downloaded so far bytes
     *                        to database and make sure sync the downloaded buffer to local file.
     *                        <p/>
     *                        More smaller more frequently, then download more slowly, but will more
     *                        safer in scene of the process is killed unexpected.
     *                        <p/>
     *                        Default 65536, which follow the value in
     *                        com.android.providers.downloads.Constants.
     * @see com.liulishuo.filedownloader.services.FileDownloadRunnable#onProgress(long, long, FileDownloadOutputStream)
     * @see #setMinProgressTime(long)
     */
    public static void setMinProgressStep(int minProgressStep) throws IllegalAccessException {
        if (isDownloaderProcess(FileDownloadHelper.getAppContext())) {
            MIN_PROGRESS_STEP = minProgressStep;
        } else {
            throw new IllegalAccessException("This value is used in the :filedownloader process," +
                    " so set this value in your process is without effect. You can add " +
                    "'process.non-separate=true' in 'filedownloader.properties' to share the main " +
                    "process to FileDownloadService. Or you can configure this value in " +
                    "'filedownloader.properties' by 'download.min-progress-step'.");
        }
    }

    /**
     * @param minProgressTime The minimum millisecond interval in per time to sync to the file and
     *                        the database.
     *                        <p>
     *                        Used for adjudging whether is time to sync the downloaded so far bytes
     *                        to database and make sure sync the downloaded buffer to local file.
     *                        <p/>
     *                        More smaller more frequently, then download more slowly, but will more
     *                        safer in scene of the process is killed unexpected.
     *                        <p/>
     *                        Default 2000, which follow the value in
     *                        com.android.providers.downloads.Constants.
     * @see com.liulishuo.filedownloader.services.FileDownloadRunnable#onProgress(long, long, FileDownloadOutputStream)
     * @see #setMinProgressStep(int)
     */
    public static void setMinProgressTime(long minProgressTime) throws IllegalAccessException {
        if (isDownloaderProcess(FileDownloadHelper.getAppContext())) {
            MIN_PROGRESS_TIME = minProgressTime;
        } else {
            throw new IllegalAccessException("This value is used in the :filedownloader process," +
                    " so set this value in your process is without effect. You can add " +
                    "'process.non-separate=true' in 'filedownloader.properties' to share the main " +
                    "process to FileDownloadService. Or you can configure this value in " +
                    "'filedownloader.properties' by 'download.min-progress-time'.");
        }
    }

    public static int getMinProgressStep() {
        return MIN_PROGRESS_STEP;
    }

    public static long getMinProgressTime() {
        return MIN_PROGRESS_TIME;
    }

    /**
     * Checks whether the filename looks legitimate.
     */
    @SuppressWarnings({"SameReturnValue", "UnusedParameters"})
    public static boolean isFilenameValid(String filename) {
//        filename = filename.replaceFirst("/+", "/"); // normalize leading
        // slashes
//        return filename.startsWith(Environment.getDownloadCacheDirectory()
//                .toString())
//                || filename.startsWith(Environment
//                .getExternalStorageDirectory().toString());
        return true;
    }

    private static String DEFAULT_SAVE_ROOT_PATH;

    public static String getDefaultSaveRootPath() {
        if (!TextUtils.isEmpty(DEFAULT_SAVE_ROOT_PATH)) {
            return DEFAULT_SAVE_ROOT_PATH;
        }

        if (FileDownloadHelper.getAppContext().getExternalCacheDir() == null) {
            return Environment.getDownloadCacheDirectory().getAbsolutePath();
        } else {
            //noinspection ConstantConditions
            return FileDownloadHelper.getAppContext().getExternalCacheDir().getAbsolutePath();
        }
    }

    public static String getDefaultSaveFilePath(final String url) {
        return generateFilePath(getDefaultSaveRootPath(), generateFileName(url));
    }

    public static String generateFileName(final String url) {
        return md5(url);
    }

    /**
     * @see #getTargetFilePath(String, boolean, String)
     */
    public static String generateFilePath(String directory, String filename) {
        if (filename == null) {
            throw new IllegalStateException("can't generate real path, the file name is null");
        }

        if (directory == null) {
            throw new IllegalStateException("can't generate real path, the directory is null");
        }

        return formatString("%s%s%s", directory, File.separator, filename);
    }

    /**
     * The path is used as the default directory in the case of the task without set path.
     *
     * @param path default root path for save download file.
     * @see com.liulishuo.filedownloader.BaseDownloadTask#setPath(String, boolean)
     */
    public static void setDefaultSaveRootPath(final String path) {
        DEFAULT_SAVE_ROOT_PATH = path;
    }

    /**
     * @param targetPath The target path for the download task.
     * @return The temp path is {@code targetPath} in downloading status; The temp path is used for
     * storing the file not completed downloaded yet.
     */
    public static String getTempPath(final String targetPath) {
        return FileDownloadUtils.formatString("%s.temp", targetPath);
    }

    /**
     * @param url  The downloading URL.
     * @param path The absolute file path.
     * @return The download id.
     */
    public static int generateId(final String url, final String path) {
        return generateId(url, path, false);
    }

    /**
     * @param url  The downloading URL.
     * @param path If {@code pathAsDirectory} is {@code true}, {@code path} would be the absolute
     *             directory to place the file;
     *             If {@code pathAsDirectory} is {@code false}, {@code path} would be the absolute
     *             file path.
     * @return The download id.
     */
    public static int generateId(final String url, final String path, final boolean pathAsDirectory) {
        if (pathAsDirectory) {
            return md5(formatString("%sp%s@dir", url, path)).hashCode();
        } else {
            return md5(formatString("%sp%s", url, path)).hashCode();
        }
    }

    private static String md5(String string) {
        byte[] hash;
        try {
            hash = MessageDigest.getInstance("MD5").digest(string.getBytes("UTF-8"));
        } catch (NoSuchAlgorithmException e) {
            throw new RuntimeException("Huh, MD5 should be supported?", e);
        } catch (UnsupportedEncodingException e) {
            throw new RuntimeException("Huh, UTF-8 should be supported?", e);
        }

        StringBuilder hex = new StringBuilder(hash.length * 2);
        for (byte b : hash) {
            if ((b & 0xFF) < 0x10) hex.append("0");
            hex.append(Integer.toHexString(b & 0xFF));
        }
        return hex.toString();
    }


    public static String getStack() {
        return getStack(true);
    }

    public static String getStack(final boolean printLine) {
        StackTraceElement[] stackTrace = new Throwable().getStackTrace();
        return getStack(stackTrace, printLine);
    }

    public static String getStack(final StackTraceElement[] stackTrace, final boolean printLine) {
        if ((stackTrace == null) || (stackTrace.length < 4)) {
            return "";
        }

        StringBuilder t = new StringBuilder();

        for (int i = 3; i < stackTrace.length; i++) {
            if (!stackTrace[i].getClassName().contains("com.liulishuo.filedownloader")) {
                continue;
            }
            t.append("[");
            t.append(stackTrace[i].getClassName().substring("com.liulishuo.filedownloader".length()));
            t.append(":");
            t.append(stackTrace[i].getMethodName());
            if (printLine) {
                t.append("(").append(stackTrace[i].getLineNumber()).append(")]");
            } else {
                t.append("]");
            }
        }
        return t.toString();
    }

    private static Boolean IS_DOWNLOADER_PROCESS;

    public static boolean isDownloaderProcess(final Context context) {
        if (IS_DOWNLOADER_PROCESS != null) {
            return IS_DOWNLOADER_PROCESS;
        }

        boolean result = false;
        do {
            if (FileDownloadProperties.getImpl().PROCESS_NON_SEPARATE) {
                result = true;
                break;
            }

            int pid = android.os.Process.myPid();
            final ActivityManager activityManager = (ActivityManager) context.
                    getSystemService(Context.ACTIVITY_SERVICE);

            final List<ActivityManager.RunningAppProcessInfo> runningAppProcessInfoList =
                    activityManager.getRunningAppProcesses();

            if (null == runningAppProcessInfoList || runningAppProcessInfoList.isEmpty()) {
                FileDownloadLog.w(FileDownloadUtils.class, "The running app process info list from" +
                        " ActivityManager is null or empty, maybe current App is not running.");
                return false;
            }

            for (ActivityManager.RunningAppProcessInfo runningAppProcessInfo : runningAppProcessInfoList) {
                if (runningAppProcessInfo.pid == pid) {
                    result = runningAppProcessInfo.processName.endsWith(":filedownloader");
                    break;
                }
            }

        } while (false);

        IS_DOWNLOADER_PROCESS = result;
        return IS_DOWNLOADER_PROCESS;
    }

    public static String[] convertHeaderString(final String nameAndValuesString) {
        final String[] lineString = nameAndValuesString.split("\n");
        final String[] namesAndValues = new String[lineString.length * 2];

        for (int i = 0; i < lineString.length; i++) {
            final String[] nameAndValue = lineString[i].split(": ");
            /**
             * @see Headers#toString()
             * @see Headers#name(int)
             * @see Headers#value(int)
             */
            namesAndValues[i * 2] = nameAndValue[0];
            namesAndValues[i * 2 + 1] = nameAndValue[1];
        }

        return namesAndValues;
    }

    public static long getFreeSpaceBytes(final String path) {
        long freeSpaceBytes;
        final StatFs statFs = new StatFs(path);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN_MR2) {
            freeSpaceBytes = statFs.getAvailableBytes();
        } else {
            //noinspection deprecation
            freeSpaceBytes = statFs.getAvailableBlocks() * (long) statFs.getBlockSize();
        }

        return freeSpaceBytes;
    }

    public static String formatString(final String msg, Object... args) {
        return String.format(Locale.ENGLISH, msg, args);
    }

    private final static String INTERNAL_DOCUMENT_NAME = "filedownloader";
    private final static String OLD_FILE_CONVERTED_FILE_NAME = ".old_file_converted";

    @SuppressWarnings("ResultOfMethodCallIgnored")
    public static void markConverted(final Context context) {
        final File file = getConvertedMarkedFile(context);
        try {
            file.getParentFile().mkdirs();
            file.createNewFile();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private static Boolean FILENAME_CONVERTED = null;

    /**
     * @return Whether has converted all files' name from 'filename'(in old architecture) to
     * 'filename.temp', if it's in downloading state.
     * <p>
     * If {@code true}, You can check whether the file has completed downloading with
     * {@link File#exists()} directly.
     * <p>
     * when {@link FileDownloadService#onCreate()} is invoked, This value will be assigned to
     * {@code true} only once since you upgrade the filedownloader version to 0.3.3 or higher.
     */
    public static boolean isFilenameConverted(final Context context) {
        if (FILENAME_CONVERTED == null) {
            FILENAME_CONVERTED = getConvertedMarkedFile(context).exists();
        }

        return FILENAME_CONVERTED;
    }

    public static File getConvertedMarkedFile(final Context context) {
        return new File(context.getFilesDir().getAbsolutePath() + File.separator +
                INTERNAL_DOCUMENT_NAME, OLD_FILE_CONVERTED_FILE_NAME);
    }

    private static final Pattern CONTENT_DISPOSITION_PATTERN =
            Pattern.compile("attachment;\\s*filename\\s*=\\s*\"([^\"]*)\"");

    //

    /**
     * The same to com.android.providers.downloads.Helpers#parseContentDisposition.
     * </p>
     * Parse the Content-Disposition HTTP Header. The format of the header
     * is defined here: http://www.w3.org/Protocols/rfc2616/rfc2616-sec19.html
     * This header provides a filename for content that is going to be
     * downloaded to the file system. We only support the attachment type.
     */
    public static String parseContentDisposition(String contentDisposition) {
        if (contentDisposition == null) {
            return null;
        }

        try {
            Matcher m = CONTENT_DISPOSITION_PATTERN.matcher(contentDisposition);
            if (m.find()) {
                return m.group(1);
            }
        } catch (IllegalStateException ex) {
            // This function is defined as returning null when it can't parse the header
        }
        return null;
    }

    /**
     * @param path            If {@code pathAsDirectory} is true, the {@code path} would be the
     *                        absolute directory to settle down the file;
     *                        If {@code pathAsDirectory} is false, the {@code path} would be the
     *                        absolute file path.
     * @param pathAsDirectory whether the {@code path} is a directory.
     * @param filename        the file's name.
     * @return the absolute path of the file. If can't find by params, will return {@code null}.
     */
    public static String getTargetFilePath(String path, boolean pathAsDirectory, String filename) {
        if (path == null) {
            return null;
        }

        if (pathAsDirectory) {
            if (filename == null) {
                return null;
            }

            return FileDownloadUtils.generateFilePath(path, filename);
        } else {
            return path;
        }
    }

    /**
     * The same to {@link File#getParent()}, for non-creating a file object.
     *
     * @return this file's parent pathname or {@code null}.
     */
    public static String getParent(final String path) {
        int length = path.length(), firstInPath = 0;
        if (File.separatorChar == '\\' && length > 2 && path.charAt(1) == ':') {
            firstInPath = 2;
        }
        int index = path.lastIndexOf(File.separatorChar);
        if (index == -1 && firstInPath > 0) {
            index = 2;
        }
        if (index == -1 || path.charAt(length - 1) == File.separatorChar) {
            return null;
        }
        if (path.indexOf(File.separatorChar) == index
                && path.charAt(firstInPath) == File.separatorChar) {
            return path.substring(0, index + 1);
        }
        return path.substring(0, index);
    }

    private final static String FILEDOWNLOADER_PREFIX = "FileDownloader";

    public static String getThreadPoolName(String name) {
        return FILEDOWNLOADER_PREFIX + "-" + name;
    }

    public static boolean isNetworkOnWifiType() {
        final ConnectivityManager manager = (ConnectivityManager) FileDownloadHelper.getAppContext().
                getSystemService(Context.CONNECTIVITY_SERVICE);
        final NetworkInfo info = manager.getActiveNetworkInfo();

        return info != null && info.getType() == ConnectivityManager.TYPE_WIFI;
    }

    public static boolean checkPermission(String permission) {
        final int perm = FileDownloadHelper.getAppContext().checkCallingOrSelfPermission(permission);
        return perm == PackageManager.PERMISSION_GRANTED;
    }

    public static long convertContentLengthString(String s) {
        if (s == null) return -1;
        try {
            return Long.parseLong(s);
        } catch (NumberFormatException e) {
            return -1;
        }
    }
}
