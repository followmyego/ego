package net.amazonaws.mobile.content;
//
// Copyright 2016 Amazon.com, Inc. or its affiliates (Amazon). All Rights Reserved.
//
// Code generated by AWS Mobile Hub. Amazon gives unlimited permission to 
// copy, distribute and modify it.
//
// Source code generated from template: aws-my-sample-app-android v0.7
//

import android.content.Context;
import android.net.Uri;
import android.util.Log;

import net.amazonaws.mobile.downloader.HttpDownloadException;
import net.amazonaws.mobile.downloader.HttpDownloadListener;
import net.amazonaws.mobile.downloader.HttpDownloadObserver;
import net.amazonaws.mobile.downloader.HttpDownloadUtility;
import net.amazonaws.mobile.downloader.ResponseHandler;
import net.amazonaws.mobile.downloader.query.DownloadState;
import net.amazonaws.mobile.util.StringFormatUtils;
import net.amazonaws.mobile.util.ThreadUtils;

import java.io.File;
import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.HashMap;
import java.util.List;

public class CloudFrontTransferHelper implements TransferHelper, HttpDownloadListener {
    private static final String LOG_TAG = CloudFrontTransferHelper.class.getSimpleName();
    /** Manages Http downloads. */
    private final HttpDownloadUtility downloadUtility;

    /** Map from the download ID to the Transfer Observer. */
    private final HashMap<Long, HttpDownloadObserver> downloadsInProgress;

    /** Map from the S3 Key to the download ID. */
    private final HashMap<String, Long> managedFilesToDownloads;

    /** Map from the download ID to the progress listener. */
    private final HashMap<Long, ContentProgressListener> progressListeners;

    /** http domain for retrieving content. */
    private final String cloudFrontDomain;

    private final String cloudFrontUrlPrefix;

    /** The local path to content being downloaded always ending in "/". */
    private final String localTransferPath;

    private final LocalContentCache localContentCache;

    private long sizeTransferring;

    public CloudFrontTransferHelper(final Context context,
                            final String cloudFrontDomain,
                            final String cloudFrontUrlPrefix,
                            final String localTransferPath,
                            final LocalContentCache cache) {
        this.cloudFrontDomain = cloudFrontDomain;
        this.cloudFrontUrlPrefix = cloudFrontUrlPrefix == null ? "" : cloudFrontUrlPrefix;
        this.localContentCache = cache;
        if (localTransferPath.endsWith(DIR_DELIMITER)) {
            this.localTransferPath = localTransferPath;
        } else {
            this.localTransferPath = localTransferPath + DIR_DELIMITER;
        }
        // Retrieval of the first instance starts the service and resumes any paused transfers.
        downloadUtility = HttpDownloadUtility.getInstance(context);
        downloadsInProgress = new HashMap<>();
        managedFilesToDownloads = new HashMap<>();
        progressListeners = new HashMap<>();

        pollAndCleanUpTransfers();
    }

    private String getRelativeFilePath(final String downloadFilePath) {
        return downloadFilePath.substring(localTransferPath.length());
    }

    /**
     * Polls for all download transfers from the transfer utility and handles each appropriately.
     * This is called on initialization to resume any currently in progress transfers and handle
     * any completed transfers.
     */
    private synchronized void pollAndCleanUpTransfers() {
        // Get all transfers being downloaded.
        final List<HttpDownloadObserver> observers =
            downloadUtility.getAllDownloads();

        sizeTransferring = 0;

        for (final HttpDownloadObserver observer : observers) {
            switch (observer.getState()) {
                case COMPLETE: {
                    final String absolutePath = observer.getAbsoluteFilePath();
                    final File completedFile = new File(absolutePath);

                    if (!completedFile.exists()) {
                        Log.w(LOG_TAG, String.format("Completed file '%s' didn't exist.",
                            completedFile.getName()));
                        downloadUtility.removeFinishedDownload(observer.getId(), null);
                        break;
                    }

                    // Add the completed item into our cache.
                    final String filePath = getRelativeFilePath(absolutePath);
                    try {
                        localContentCache.addByMoving(filePath, completedFile);
                    } catch (IOException ex) {
                        Log.e(LOG_TAG, ex.getMessage());
                    }
                    downloadUtility.removeFinishedDownload(observer.getId(), null);
                    break;
                }
                case FAILED:
                    Log.e(LOG_TAG, "Removing failed transfer.");
                    downloadUtility.removeFinishedDownload(observer.getId(), null);
                    break;
                case PAUSED:
                    observer.setDownloadListener(this);
                    // Resume paused transfers, and register ourselves to listen to progress.
                    downloadUtility.resume(observer.getId(), null);
                    break;
                case NOT_STARTED:
                case IN_PROGRESS: {
                    final long downloadId = observer.getId();
                    // Download is pending. We manage these transfers if not already doing so.
                    if (!downloadsInProgress.containsKey(downloadId)) {
                        final String absoluteFilePath = observer.getAbsoluteFilePath();

                        if (absoluteFilePath.startsWith(localTransferPath)) {
                            downloadsInProgress.put(downloadId, observer);
                            // Get relative file path from the absolute file path.
                            final String filePath = getRelativeFilePath(absoluteFilePath);

                            if (managedFilesToDownloads.containsKey(filePath)) {
                                // if we are already downloading this file, it is a duplicate transfer,
                                // this should never happen.
                                Log.e(LOG_TAG, String.format("Detected duplicate transfer for file '%s'",
                                    observer.getAbsoluteFilePath()));
                                downloadUtility.cancel(downloadId, new ResponseHandler() {
                                    @Override
                                    public void onSuccess(long downloadId) {
                                        downloadUtility.removeFinishedDownload(downloadId, null);
                                    }

                                    @Override
                                    public void onError(String errorMessage) {
                                        Log.e(LOG_TAG, String.format(
                                            "Couldn't cancel duplicate transfer for file '%s'",
                                            observer.getAbsoluteFilePath()));
                                    }
                                });
                            } else {
                                sizeTransferring += observer.getBytesTotal();
                                managedFilesToDownloads.put(filePath, observer.getId());
                                observer.setDownloadListener(this);
                            }
                        }
                    }
                    break;
                }
            }
        }
    }

    private boolean startTransfer(final String relativeFilePath, final long fileSize,
                                  final ContentProgressListener listener) {
        final URI uri;
        try {
            uri = new URI("https://" + cloudFrontDomain + "/" + Uri.encode(cloudFrontUrlPrefix + relativeFilePath));
        } catch (final URISyntaxException ex) {
            Log.d(LOG_TAG, ex.getMessage(), ex);
            ThreadUtils.runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    listener.onError(relativeFilePath, ex);
                }
            });
            return false;
        }

        sizeTransferring += fileSize;

        downloadUtility.download(uri, localTransferPath + relativeFilePath, relativeFilePath,
            new ResponseHandler() {
                @Override
                public void onSuccess(long downloadId) {
                    final HttpDownloadObserver observer
                        = downloadUtility.getDownloadById(downloadId);
                    synchronized (CloudFrontTransferHelper.this) {
                        // Set the progress listener for the transfer
                        progressListeners.put(downloadId, listener);

                        downloadsInProgress.put(downloadId, observer);
                        managedFilesToDownloads.put(relativeFilePath, downloadId);
                    }
                    observer.setDownloadListener(CloudFrontTransferHelper.this);

                    // Transfers get created in a NOT_STARTED state, which doesn't initially cause
                    // a state change.
                    onStateChanged(downloadId, observer.getState());
                }

                @Override
                public void onError(final String errorMessage) {
                    Log.d(LOG_TAG, errorMessage);
                    ThreadUtils.runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            listener.onError(relativeFilePath, new IOException(errorMessage));
                        }
                    });
                }
            }, this);

        return true;
    }
    /**
     * Download a file to be placed in the local content cache upon completion.
     * @param relativeFilePath the relative path and name of the file to download.
     * @param fileSize file size of the object. Pass 0 if the file size is unknown.
     * @param listener the progress listener.
     */
    public synchronized void download(final String relativeFilePath, final long fileSize,
                                      final ContentProgressListener listener) {
        final Long downloadId = managedFilesToDownloads.get(relativeFilePath);
        // if this item is not in the currently managed transfers.
        if (downloadId == null) {
            try {
                // download the item.
                startTransfer(relativeFilePath, fileSize, listener);
            } catch (final IllegalStateException ex) {
                ThreadUtils.runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        listener.onError(relativeFilePath, ex);
                    }
                });
            }
            return;
        }

        // ensure the progress listener is set for this transfer
        progressListeners.put(downloadId, listener);

        // Restart transfer service to deal with any in progress downloads.
        downloadUtility.resume(downloadId, null);
    }

    @Override
    public void upload(final File file, final String filePath, final ContentProgressListener listener) {
        throw new UnsupportedOperationException("Upload unsupported.");
    }

    public synchronized void setProgressListener(final String relativeFilePath, final ContentProgressListener listener) {
        final Long downloadId = managedFilesToDownloads.get(relativeFilePath);
        if (downloadId != null) {
            if (listener == null) {
                progressListeners.remove(downloadId);
                return;
            }

            final HttpDownloadObserver observer = downloadsInProgress.get(downloadId);
            if (observer != null) {
                final ContentProgressListener currentListener = progressListeners.get(downloadId);
                progressListeners.put(downloadId, listener);

                if (currentListener != listener) {
                    observer.refresh();
                    final DownloadState downloadState = observer.getState();

                    if (downloadState == DownloadState.PAUSED ||
                        downloadState == DownloadState.NOT_STARTED) {
                        ThreadUtils.runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                onStateChanged(downloadId, downloadState);
                            }
                        });
                    }
                }
            }
        } else {
            Log.w(LOG_TAG, String.format("Attempt to set progress listener for file '%s'," +
                " but no transfer is in progress for that file.", relativeFilePath));
        }
    }

    public long getSizeTransferring() {
        return sizeTransferring;
    }

    public synchronized boolean isTransferring(final String relativeFilePath) {
        final Long downloadId = managedFilesToDownloads.get(relativeFilePath);
        return downloadId != null;
    }

    private synchronized DownloadState getTransferState(final String relativeFilePath) {
        final Long downloadId = managedFilesToDownloads.get(relativeFilePath);
        if (downloadId == null) {
            return null;
        }
        final HttpDownloadObserver observer = downloadsInProgress.get(downloadId);
        observer.refresh();
        return observer.getState();
    }

    public boolean isTransferWaiting(final String relativeFilePath) {
        final DownloadState state = getTransferState(relativeFilePath);
        return (DownloadState.PAUSED.equals(state) || DownloadState.NOT_STARTED.equals(state));
    }

    public synchronized void clearProgressListeners() {
        progressListeners.clear();
    }

    /**
     * Cleans up all accounting data structures related to a transfer. Should be called from a
     * context that is synchronized on this object.
     * @param observer the transfer observer.
     */
    private synchronized void cleanUpDownload(final HttpDownloadObserver observer) {
        final long downloadId = observer.getId();
        final String relativeFilePath = getRelativeFilePath(observer.getAbsoluteFilePath());
        downloadsInProgress.remove(downloadId);
        managedFilesToDownloads.remove(relativeFilePath);
        observer.cleanDownloadListener();
        downloadUtility.removeFinishedDownload(downloadId, null);
        progressListeners.remove(downloadId);
    }

    @Override
    public synchronized void onStateChanged(final long id, final DownloadState state) {
        if (state == DownloadState.COMPLETE) {
            final HttpDownloadObserver observer = downloadsInProgress.get(id);
            if (observer == null) {
                // This should not happen.
                Log.e(LOG_TAG, String.format(
                    "Received state change to download complete for id(%d), but download was not in progress.", id));
                return;
            }
            final String absolutePath = observer.getAbsoluteFilePath();
            final File completedFile = new File(absolutePath);

            // Adjust the size currently transferring to account for the completed item.
            sizeTransferring -= observer.getBytesTotal();

            final ContentProgressListener listener = progressListeners.get(id);
            // This removes the progress listener, so it is important that the listener has already
            // been obtained above to call back onSuccess or onError below.
            cleanUpDownload(observer);

            // Add the completed item to our cache.
            final File cachedFile;
            final String relativeFilePath = getRelativeFilePath(absolutePath);
            try {
                cachedFile = localContentCache.addByMoving(relativeFilePath, completedFile);
            } catch (final IOException ex) {
                Log.e(LOG_TAG, String.format("Can't add file(%s) into the local cache.",
                    relativeFilePath), ex);
                if (listener != null) {
                    ThreadUtils.runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            listener.onError(relativeFilePath, ex);
                        }
                    });
                }
                return;
            }

            if (listener != null) {
                listener.onSuccess(new FileContent(cachedFile, relativeFilePath));
            }
        } else if (state == DownloadState.FAILED) {
            final HttpDownloadObserver observer = downloadsInProgress.get(id);
            if (observer == null) {
                // This should not happen.
                Log.e(LOG_TAG, String.format(
                    "Received state change to failed for id(%d), but download was not in progress.", id));
                return;
            }
            final String filePath = getRelativeFilePath(observer.getAbsoluteFilePath());
            Log.w(LOG_TAG, String.format("Download state changed to failed for '%s'", filePath));
            localContentCache.unPinFile(filePath);
            cleanUpDownload(observer);
            // No need to call back onError here, since the onError handler method will also always be called for this
            // state change and will include the exception related to the error.
        } else if (state == DownloadState.NOT_STARTED || state == DownloadState.PAUSED) {
            final ContentProgressListener listener = progressListeners.get(id);
            if (listener != null) {
                final HttpDownloadObserver observer = downloadsInProgress.get(id);
                if (observer == null) {
                    Log.d(LOG_TAG, String.format(
                        "Received state change for id(%d), but download was not in progress.", id));
                    return;
                }
                observer.refresh();
                final String filePath = getRelativeFilePath(observer.getAbsoluteFilePath());
                // Ensure this happens from the UI thread.
                ThreadUtils.runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        listener.onProgressUpdate(filePath, true, observer.getBytesTransferred(),
                            observer.getBytesTotal());
                    }
                });
            }
        }
    }

    @Override
    public synchronized void onProgressChanged(final long id, final long bytesCurrent, final long bytesTotal) {
        final HttpDownloadObserver observer = downloadsInProgress.get(id);
        if (observer == null) {
            Log.d(LOG_TAG, String.format(
                "Received progress update for id(%d), but download was not in progress.", id));
            return;
        }
        final ContentProgressListener listener = progressListeners.get(id);
        final String filePath = getRelativeFilePath(observer.getAbsoluteFilePath());
        final long maxCacheSize = localContentCache.getMaxCacheSize();

        final boolean isPinned = localContentCache.shouldPinFile(filePath);
        if (!isPinned && bytesTotal > maxCacheSize) {
            // Cancel the transfer.  This will cause the transfer to switch to failed state, and it will
            // be the transfer will be cleaned up at that point.
            downloadUtility.cancel(id, new ResponseHandler() {
                @Override
                public void onSuccess(long downloadId) {
                    final String message = String.format(
                        "Cancelled file '%s' due to transfer size %s exceeds max cache size of %s.",
                        filePath, StringFormatUtils.getBytesString(bytesTotal, true),
                        StringFormatUtils.getBytesString(maxCacheSize, true));
                    Log.e(LOG_TAG, message);
                    if (listener != null) {
                        // This callback always occurs on the UI thread.
                        listener.onError(filePath, new IllegalStateException(message));
                    }
                }

                @Override
                public void onError(String errorMessage) {
                    final String message = String.format(
                        "Couldn't cancel file '%s', whose transfer size of %s exceeds max cache size of %s.",
                        filePath, StringFormatUtils.getBytesString(bytesTotal, true),
                        StringFormatUtils.getBytesString(maxCacheSize, true));
                    // This should never fail, unless the file already completed downloading,
                    // which theoretically might be possible if the cache size is very small,
                    // and the file requested to be downloaded is just slightly bigger than
                    // the cache size.
                    Log.e(LOG_TAG, message);
                    if (listener != null) {
                        // This callback always occurs on the UI thread.
                        listener.onError(filePath, new IllegalStateException(message));
                    }
                }
            });
            return;
        }

        if (listener != null) {
            ThreadUtils.runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    listener.onProgressUpdate(filePath, false, bytesCurrent,
                        bytesTotal);
                }
            });
        }
    }

    @Override
    public synchronized void onError(final long id, final HttpDownloadException ex) {
        final HttpDownloadObserver observer = downloadsInProgress.get(id);
        final String errorDesc;
        if (ex.getMessage() != null) {
            if (ex.getMessage().contains("UnknownHostException")) {
                errorDesc = ex.getMessage().concat(".  It may take up to an hour " +
                    "for the Amazon CloudFront distribution to become ACTIVE " +
                    "and for the DNS records to propagate.");
            } else {
                errorDesc = ex.getMessage();
            }
        } else {
            errorDesc = String.format("Unknown error of type %s, ex.getMessage() returned null.",
                ex.getClass().getSimpleName());
        }
        Log.d(LOG_TAG, errorDesc, ex);

        final ContentProgressListener listener = progressListeners.get(id);
        if (listener != null) {
            ThreadUtils.runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    listener.onError(getRelativeFilePath(observer.getAbsoluteFilePath()),
                        new HttpDownloadException(ex.getErrorCode(), errorDesc));
                }
            });
        }
        // The transfer is not cleaned up here, since it is handled when
        // the state changes to failed above in onStateChanged(), which
        // is guaranteed to always be called after onError().
    }

    private synchronized void deRegisterObservers() {
        for (final HttpDownloadObserver observer : downloadsInProgress.values()) {
            observer.cleanDownloadListener();
        }
    }
    @Override
    public synchronized void destroy() {
        clearProgressListeners();
        deRegisterObservers();
        downloadsInProgress.clear();
        managedFilesToDownloads.clear();
    }
}
