/*
 * Copyright (c) 2015 LingoChamp Inc.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *   http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.liulishuo.filedownloader.util;

import android.os.Handler;
import android.os.HandlerThread;
import android.os.Message;

import com.liulishuo.filedownloader.BaseDownloadTask;
import com.liulishuo.filedownloader.FileDownloader;

import java.lang.ref.WeakReference;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;

/**
 * The serial queue, what used to dynamically increase tasks, and tasks in the queue will
 * automatically start download one by one.
 */

public class FileDownloadSerialQueue {

    private final BlockingQueue<BaseDownloadTask> mTasks = new LinkedBlockingQueue<>();
    private final HandlerThread mHandlerThread;
    private final Handler mHandler;

    private final static int WHAT_NEXT = 1;
    private final static int ID_INVALID = 0;
    private int mWorkingTaskId = ID_INVALID;

    public FileDownloadSerialQueue() {
        mHandlerThread = new HandlerThread("SerialDownloadManager");
        mHandlerThread.start();
        mHandler = new Handler(mHandlerThread.getLooper(), new SerialLoop());
        sendNext();
    }

    /**
     * Enqueues the given task sometime in the serial queue. If the {@code task} is in the head of
     * the serial queue, the {@code task} will be started automatically.
     */
    public void enqueue(BaseDownloadTask task) {
        try {
            mTasks.put(task);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    /**
     * Returns the identify of the working task, if there is task is working, you will receive
     * {@link #ID_INVALID}.
     *
     * @return the identify of the working task
     */
    public int getWorkingTaskId() {
        return mWorkingTaskId;
    }

    /**
     * Attempts to stop the working task, halts the processing of waiting tasks, and returns a list
     * of the tasks that were awaiting execution. These tasks are drained (removed) from the task
     * queue upon return from this method.
     */
    public List<BaseDownloadTask> shutdown() {
        if (mWorkingTaskId != ID_INVALID) {
            FileDownloader.getImpl().pause(mWorkingTaskId);
        }

        final List<BaseDownloadTask> unDealTaskList = new ArrayList<>();
        mTasks.drainTo(unDealTaskList);
        mHandler.removeMessages(WHAT_NEXT);
        mHandlerThread.interrupt();
        mHandlerThread.quit();

        return unDealTaskList;
    }


    private class SerialLoop implements Handler.Callback {

        @Override
        public boolean handleMessage(Message msg) {
            switch (msg.what) {
                case WHAT_NEXT:
                    try {
                        mWorkingTaskId = mTasks.take().
                                addFinishListener(
                                        new SerialFinishCallback(
                                                new WeakReference<>(FileDownloadSerialQueue.this))).
                                start();
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                    break;
            }
            return false;
        }
    }

    private static class SerialFinishCallback implements BaseDownloadTask.FinishListener {
        private final WeakReference<FileDownloadSerialQueue> mQueueWeakReference;

        SerialFinishCallback(WeakReference<FileDownloadSerialQueue> queueWeakReference) {
            this.mQueueWeakReference = queueWeakReference;
        }

        @Override
        public void over(BaseDownloadTask task) {
            task.removeFinishListener(this);

            if (mQueueWeakReference == null) {
                return;
            }

            final FileDownloadSerialQueue queue = mQueueWeakReference.get();
            if (queue == null) {
                return;
            }

            queue.mWorkingTaskId = ID_INVALID;
            queue.sendNext();
        }
    }

    private void sendNext() {
        mHandler.sendEmptyMessage(WHAT_NEXT);
    }

}
