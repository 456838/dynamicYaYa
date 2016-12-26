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

package com.liulishuo.filedownloader;

import com.liulishuo.filedownloader.message.MessageSnapshot;
import com.liulishuo.filedownloader.message.MessageSnapshotTaker;
import com.liulishuo.filedownloader.model.FileDownloadHeader;
import com.liulishuo.filedownloader.model.FileDownloadStatus;
import com.liulishuo.filedownloader.util.FileDownloadHelper;
import com.liulishuo.filedownloader.util.FileDownloadLog;
import com.liulishuo.filedownloader.util.FileDownloadUtils;

import java.io.File;
import java.security.InvalidParameterException;
import java.util.ArrayList;

/**
 * The download task hunter.
 */

public class DownloadTaskHunter implements ITaskHunter, ITaskHunter.IStarter, ITaskHunter.IMessageHandler,
        BaseDownloadTask.LifeCycleCallback {

    private IFileDownloadMessenger mMessenger;

    @Override
    public boolean updateKeepAhead(MessageSnapshot snapshot) {
        if (!FileDownloadStatus.isKeepAhead(getStatus(), snapshot.getStatus())) {
            if (FileDownloadLog.NEED_LOG) {
                FileDownloadLog.d(this, "can't update mStatus change by keep ahead, %d, but the" +
                        " current mStatus is %d, %d", mStatus, getStatus(), getId());
            }
            return false;
        }

        update(snapshot);
        return true;
    }

    @Override
    public boolean updateKeepFlow(MessageSnapshot snapshot) {
        final int currentStatus = getStatus();
        final int nextStatus = snapshot.getStatus();

        if (FileDownloadStatus.paused == currentStatus && FileDownloadStatus.isIng(nextStatus)) {
            if (FileDownloadLog.NEED_LOG) {
                /**
                 * Occur such situation, must be the running-mStatus waiting for turning up in flow
                 * thread pool(or binder thread) when there is someone invoked the {@link #pause()} .
                 *
                 * High concurrent cause.
                 */
                FileDownloadLog.d(this, "High concurrent cause, callback pending, but has already" +
                        " be paused %d", getId());
            }
            return true;
        }

        if (!FileDownloadStatus.isKeepFlow(currentStatus, nextStatus)) {
            if (FileDownloadLog.NEED_LOG) {
                FileDownloadLog.d(this, "can't update mStatus change by keep flow, %d, but the" +
                        " current mStatus is %d, %d", mStatus, getStatus(), getId());
            }

            return false;
        }

        update(snapshot);
        return true;
    }

    @Override
    public boolean updateMoreLikelyCompleted(MessageSnapshot snapshot) {
        if (!FileDownloadStatus.isMoreLikelyCompleted(mTask.getRunningTask().getOrigin())) {
            return false;
        }

        update(snapshot);
        return true;
    }

    @Override
    public boolean updateSameFilePathTaskRunning(MessageSnapshot snapshot) {
        if (!mTask.getRunningTask().getOrigin().isPathAsDirectory()) {
            return false;
        }

        if (snapshot.getStatus() != FileDownloadStatus.warn ||
                getStatus() != FileDownloadStatus.connected) {
            return false;
        }

        update(snapshot);
        return true;
    }

    @Override
    public IFileDownloadMessenger getMessenger() {
        return mMessenger;
    }

    @Override
    public MessageSnapshot prepareErrorMessage(Throwable cause) {
        mStatus = FileDownloadStatus.error;
        mThrowable = cause;
        return MessageSnapshotTaker.catchException(mTask.getRunningTask().getOrigin());
    }

    private void update(final MessageSnapshot snapshot) {
        final BaseDownloadTask task = mTask.getRunningTask().getOrigin();
        final byte status = snapshot.getStatus();

        this.mStatus = status;
        this.mIsLargeFile = snapshot.isLargeFile();

        switch (status) {
            case FileDownloadStatus.pending:
                this.mSoFarBytes = snapshot.getLargeSofarBytes();
                this.mTotalBytes = snapshot.getLargeTotalBytes();

                // notify
                mMessenger.notifyPending(snapshot);
                break;
            case FileDownloadStatus.started:
                // notify
                mMessenger.notifyStarted(snapshot);
                break;
            case FileDownloadStatus.connected:
                this.mTotalBytes = snapshot.getLargeTotalBytes();
                this.mIsResuming = snapshot.isResuming();
                this.mEtag = snapshot.getEtag();

                final String filename = snapshot.getFileName();
                if (filename != null) {
                    if (task.getFilename() != null) {
                        FileDownloadLog.w(this, "already has mFilename[%s], but assign mFilename[%s] again",
                                task.getFilename(), filename);
                    }
                    mTask.setFileName(filename);
                }

                mSpeedMonitor.start();

                // notify
                mMessenger.notifyConnected(snapshot);
                break;
            case FileDownloadStatus.progress:
                this.mSoFarBytes = snapshot.getLargeSofarBytes();
                mSpeedMonitor.update(snapshot.getLargeSofarBytes());

                // notify
                mMessenger.notifyProgress(snapshot);
                break;
//            case FileDownloadStatus.blockComplete:
            /**
             * Handled by {@link FileDownloadList#removeByCompleted(BaseDownloadTask)}
             */
//                break;
            case FileDownloadStatus.retry:
                this.mSoFarBytes = snapshot.getLargeSofarBytes();
                this.mThrowable = snapshot.getThrowable();
                mRetryingTimes = snapshot.getRetryingTimes();

                mSpeedMonitor.reset();

                // notify
                mMessenger.notifyRetry(snapshot);
                break;
            case FileDownloadStatus.error:
                this.mThrowable = snapshot.getThrowable();
                this.mSoFarBytes = snapshot.getLargeSofarBytes();

                mSpeedMonitor.end(this.mSoFarBytes);
                // to FileDownloadList
                FileDownloadList.getImpl().remove(mTask.getRunningTask(), snapshot);

                break;
            case FileDownloadStatus.paused:
                /**
                 * Handled by {@link #pause()}
                 */
                break;
            case FileDownloadStatus.completed:
                this.mIsReusedOldFile = snapshot.isReusedDownloadedFile();
                // only carry total data back
                this.mSoFarBytes = snapshot.getLargeTotalBytes();
                this.mTotalBytes = snapshot.getLargeTotalBytes();

                mSpeedMonitor.end(this.mSoFarBytes);
                // to FileDownloadList
                FileDownloadList.getImpl().remove(mTask.getRunningTask(), snapshot);

                break;
            case FileDownloadStatus.warn:
                mSpeedMonitor.reset();

                final int sameIdTaskCount = FileDownloadList.getImpl().count(task.getId());

                final int sameStoreTaskCount;
                // generate same task id.
                if (sameIdTaskCount <= 1 && task.isPathAsDirectory()) {
                    sameStoreTaskCount = FileDownloadList.getImpl().count(FileDownloadUtils.
                            generateId(task.getUrl(), task.getTargetFilePath()));
                } else {
                    sameStoreTaskCount = 0;
                }

                if (sameIdTaskCount + sameStoreTaskCount <= 1) {
                    // 1. this progress kill by sys and relive,
                    // for add at least one mListener
                    // or 2. pre downloading task has already completed/error/paused
                    // request mStatus
                    final int currentStatus = FileDownloadServiceProxy.getImpl().
                            getStatus(task.getId());
                    FileDownloadLog.w(this, "warn, but no mListener to receive, " +
                            "switch to pending %d %d", task.getId(), currentStatus);

                    //noinspection StatementWithEmptyBody
                    if (FileDownloadStatus.isIng(currentStatus)) {
                        // ing, has callbacks
                        // keep and wait callback

                        this.mStatus = FileDownloadStatus.pending;
                        this.mTotalBytes = snapshot.getLargeTotalBytes();
                        this.mSoFarBytes = snapshot.getLargeSofarBytes();

                        mSpeedMonitor.start();

                        mMessenger.
                                notifyPending(((MessageSnapshot.IWarnMessageSnapshot) snapshot).
                                        turnToPending());
                        break;
                    } else {
                        // already over and no callback
                    }

                }

                // to FileDownloadList
                FileDownloadList.getImpl().remove(mTask.getRunningTask(), snapshot);
                break;
        }
    }

    @Override
    public void onBegin() {
        if (FileDownloadMonitor.isValid()) {
            FileDownloadMonitor.getMonitor().onTaskBegin(mTask.getRunningTask().getOrigin());
        }

        if (FileDownloadLog.NEED_LOG) {
            FileDownloadLog.v(this, "filedownloader:lifecycle:start %s by %d ", toString(), getStatus());
        }
    }

    @Override
    public void onIng() {
        if (FileDownloadMonitor.isValid() && getStatus() == FileDownloadStatus.started) {
            FileDownloadMonitor.getMonitor().onTaskStarted(mTask.getRunningTask().getOrigin());
        }
    }

    @Override
    public void onOver() {
        final BaseDownloadTask origin = mTask.getRunningTask().getOrigin();

        if (FileDownloadMonitor.isValid()) {
            FileDownloadMonitor.getMonitor().onTaskOver(origin);
        }

        if (FileDownloadLog.NEED_LOG) {
            FileDownloadLog.v(this, "filedownloader:lifecycle:over %s by %d ", toString(),
                    getStatus());
        }

        if (mTask.getFinishListenerList() != null) {
            @SuppressWarnings("unchecked") final ArrayList<BaseDownloadTask.FinishListener>
                    listenersCopy =
                    (ArrayList<BaseDownloadTask.FinishListener>) mTask.getFinishListenerList().clone();
            final int numListeners = listenersCopy.size();
            for (int i = 0; i < numListeners; ++i) {
                listenersCopy.get(i).over(origin);
            }
        }

        FileDownloader.getImpl().getLostConnectedHandler().taskWorkFine(mTask.getRunningTask());
    }

    interface ICaptureTask {
        FileDownloadHeader getHeader();

        BaseDownloadTask.IRunningTask getRunningTask();

        void setFileName(String fileName);

        ArrayList<BaseDownloadTask.FinishListener> getFinishListenerList();
    }

    private final ICaptureTask mTask;
    private byte mStatus = FileDownloadStatus.INVALID_STATUS;
    private Throwable mThrowable = null;

    private final IDownloadSpeed.Monitor mSpeedMonitor;
    private final IDownloadSpeed.Lookup mSpeedLookup;

    private long mSoFarBytes;
    private long mTotalBytes;

    private int mRetryingTimes;

    private boolean mIsLargeFile;

    volatile boolean mIsUsing = false;

    private boolean mIsResuming;
    private String mEtag;

    private boolean mIsReusedOldFile = false;

    DownloadTaskHunter(ICaptureTask task) {
        mTask = task;
        final DownloadSpeedMonitor monitor = new DownloadSpeedMonitor();
        mSpeedMonitor = monitor;
        mSpeedLookup = monitor;
        mMessenger = new FileDownloadMessenger(task.getRunningTask(), this);
    }

    @Override
    public void intoLaunchPool() {
        final BaseDownloadTask.IRunningTask runningTask = mTask.getRunningTask();
        final BaseDownloadTask origin = runningTask.getOrigin();

        mIsUsing = true;
        if (FileDownloadMonitor.isValid()) {
            FileDownloadMonitor.getMonitor().onRequestStart(origin);
        }

        if (FileDownloadLog.NEED_LOG) {
            FileDownloadLog.v(this, "call start " +
                            "Url[%s], Path[%s] Listener[%s], Tag[%s]",
                    origin.getUrl(), origin.getPath(), origin.getListener(), origin.getTag());
        }

        boolean ready = true;

        try {
            prepare();
        } catch (Throwable e) {
            ready = false;

            FileDownloadList.getImpl().add(runningTask);
            FileDownloadList.getImpl().remove(runningTask, prepareErrorMessage(e));
        }

        if (ready) {
            FileDownloadTaskLauncher.getImpl().launch(this);
        }
    }

    @Override
    public boolean pause() {
        final BaseDownloadTask.IRunningTask runningTask = mTask.getRunningTask();
        final BaseDownloadTask origin = runningTask.getOrigin();
        if (FileDownloadStatus.isOver(getStatus())) {
            if (FileDownloadLog.NEED_LOG) {
                /**
                 * The over-mStatus call-backed and set the over-mStatus to this task between here
                 * area and remove from the {@link FileDownloadList}.
                 *
                 * High concurrent cause.
                 */
                FileDownloadLog.d(this, "High concurrent cause, Already is over, can't pause " +
                        "again, %d %d", getStatus(), origin.getId());
            }
            return false;
        }
        FileDownloadTaskLauncher.getImpl().expire(this);

        this.mStatus = FileDownloadStatus.paused;

        if (!FileDownloader.getImpl().isServiceConnected()) {
            if (FileDownloadLog.NEED_LOG) {
                FileDownloadLog.d(this, "request pause the task[%d] to the download service, but" +
                        " the download service isn't connected yet.", origin.getId());
            }
        } else {
            FileDownloadServiceProxy.getImpl().pause(origin.getId());
        }

        mSpeedMonitor.end(mSoFarBytes);

        // For make sure already added event mListener for receive paused event
        FileDownloadList.getImpl().add(runningTask);
        FileDownloadList.getImpl().remove(runningTask, MessageSnapshotTaker.catchPause(origin));

        FileDownloader.getImpl().getLostConnectedHandler().taskWorkFine(runningTask);

        return true;
    }

    @Override
    public byte getStatus() {
        return mStatus;
    }

    @Override
    public void reset() {
        mThrowable = null;

        mEtag = null;
        mIsResuming = false;
        mRetryingTimes = 0;
        mIsReusedOldFile = false;
        mIsLargeFile = false;

        mSoFarBytes = 0;
        mTotalBytes = 0;

        mSpeedMonitor.reset();
        free();

        if (FileDownloadStatus.isOver(mStatus)) {
            mMessenger.discard();
            mMessenger = new FileDownloadMessenger(mTask.getRunningTask(), this);
        } else {
            mMessenger.reAppointment(mTask.getRunningTask(), this);
        }

        mStatus = FileDownloadStatus.INVALID_STATUS;
    }

    @Override
    public void setMinIntervalUpdateSpeed(int minIntervalUpdateSpeed) {
        mSpeedLookup.setMinIntervalUpdateSpeed(minIntervalUpdateSpeed);
    }

    @Override
    public int getSpeed() {
        return mSpeedLookup.getSpeed();
    }

    @Override
    public long getSofarBytes() {
        return mSoFarBytes;
    }

    @Override
    public long getTotalBytes() {
        return mTotalBytes;
    }

    @Override
    public Throwable getErrorCause() {
        return mThrowable;
    }

    @Override
    public int getRetryingTimes() {
        return mRetryingTimes;
    }

    @Override
    public boolean isReusedOldFile() {
        return mIsReusedOldFile;
    }

    @Override
    public boolean isResuming() {
        return mIsResuming;
    }

    @Override
    public String getEtag() {
        return mEtag;
    }

    @Override
    public boolean isLargeFile() {
        return mIsLargeFile;
    }

    @Override
    public boolean isUsing() {
        return mIsUsing;
    }

    @Override
    public void free() {
        mIsUsing = false;
    }

    private void prepare() {
        final BaseDownloadTask.IRunningTask runningTask = mTask.getRunningTask();
        final BaseDownloadTask origin = runningTask.getOrigin();

        if (origin.getPath() == null) {
            origin.setPath(FileDownloadUtils.getDefaultSaveFilePath(origin.getUrl()));
            if (FileDownloadLog.NEED_LOG) {
                FileDownloadLog.d(this, "save Path is null to %s", origin.getPath());
            }
        }

        final File dir;
        if (origin.isPathAsDirectory()) {
            dir = new File(origin.getPath());
        } else {
            final String dirString = FileDownloadUtils.getParent(origin.getPath());
            if (dirString == null) {
                throw new InvalidParameterException(
                        FileDownloadUtils.formatString("the provided mPath[%s] is invalid," +
                                " can't find its directory", origin.getPath()));
            }
            dir = new File(dirString);
        }

        if (!dir.exists()) {
            //noinspection ResultOfMethodCallIgnored
            dir.mkdirs();
        }
    }

    private int getId() {
        return mTask.getRunningTask().getOrigin().getId();
    }

    @Override
    public void start() {
        final BaseDownloadTask.IRunningTask runningTask = mTask.getRunningTask();
        final BaseDownloadTask origin = runningTask.getOrigin();

        final ILostServiceConnectedHandler lostConnectedHandler = FileDownloader.getImpl().
                getLostConnectedHandler();
        try {

            if (lostConnectedHandler.dispatchTaskStart(runningTask)) {
                return;
            }

            FileDownloadList.getImpl().add(runningTask);
            if (FileDownloadHelper.inspectAndInflowDownloaded(
                    origin.getId(), origin.getTargetFilePath(), origin.isForceReDownload(), true)
                    ) {
                // Will be removed when the complete message is received in #update
                return;
            }

            final boolean succeed = FileDownloadServiceProxy.getImpl().
                    start(
                            origin.getUrl(),
                            origin.getPath(),
                            origin.isPathAsDirectory(),
                            origin.getCallbackProgressTimes(), origin.getCallbackProgressMinInterval(),
                            origin.getAutoRetryTimes(),
                            origin.isForceReDownload(),
                            mTask.getHeader(),
                            origin.isWifiRequired());

            if (!succeed) {
                //noinspection StatementWithEmptyBody
                if (!lostConnectedHandler.dispatchTaskStart(runningTask)) {
                    final MessageSnapshot snapshot = prepareErrorMessage(
                            new RuntimeException("Occur Unknow Error, when request to start" +
                                    " maybe some problem in binder, maybe the process was killed in " +
                                    "unexpected."));

                    if (!FileDownloadList.getImpl().contains(runningTask)) {
                        lostConnectedHandler.taskWorkFine(runningTask);
                        FileDownloadList.getImpl().add(runningTask);
                    }

                    FileDownloadList.getImpl().remove(runningTask, snapshot);

                } else {
                    // the FileDownload Service host process was killed when request stating and it
                    // will be restarted by LostServiceConnectedHandler.
                }
            } else {
                lostConnectedHandler.taskWorkFine(runningTask);
            }

        } catch (Throwable e) {
            e.printStackTrace();

            FileDownloadList.getImpl().remove(runningTask, prepareErrorMessage(e));
        }
    }

    @Override
    public boolean equalListener(FileDownloadListener listener) {
        return mTask.getRunningTask().getOrigin().getListener() == listener;
    }
}
