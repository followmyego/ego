package net.amazonaws.mobile.content;
//
// Copyright 2016 Amazon.com, Inc. or its affiliates (Amazon). All Rights Reserved.
//
// Code generated by AWS Mobile Hub. Amazon gives unlimited permission to 
// copy, distribute and modify it.
//
// Source code generated from template: aws-my-sample-app-android v0.7
//

import android.os.ConditionVariable;
import android.util.Log;

import com.amazonaws.AmazonClientException;

import java.io.File;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Queue;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Future;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * Class for iterating all available content managed by a content manager as fast as possible.
 * Items are not ordered since they come back as soon as they are available.
 */
public class AvailableS3ContentIterator implements Iterator<ContentItem>, Iterable<ContentItem>,
    S3WholeBucketIterator.S3ListErrorHandler, Runnable {
        private static final String LOG_TAG = AvailableS3ContentIterator.class.getSimpleName();
        /* Queue to store the available content that will be returned through the iterator. */
        private Queue<ContentItem> availableContent = new ConcurrentLinkedQueue<>();

        /** the ConcurrentLinkedQueue doesn't keep a member tracking the count, so this does. */
        private AtomicInteger contentCount;

        /** HashMap to check if a local file is available while iterating remote files. */
        private HashMap<String, FileContent> localFiles = new HashMap<>();;

        /** Condition variable to use to wait for content to be available. */
        private ConditionVariable waitingForContent = new ConditionVariable();

        /** Condition variable to use when the queue is full. */
        private ConditionVariable waitingForReader = new ConditionVariable(true);

        /** Flag for whether we are still iterating content. */
        private volatile boolean areIteratingContent = true;

        /** The executor service to run our thread on. */
        private final ExecutorService executorService;

        /** The content manager for which we are iterating content. */
        private final ContentManager contentManager;

        /** The future for the task that handles iterating content and adding it to the queue. */
        private final Future iteratingFuture;

        /** The s3 Object prefix to limit results. */
        private final String prefix;

        /** The local path prefix for iterating local content. */
        private final String localPrefix;

        /** The s3 Object delimiter to limit the results. */
        private final String delimiter;

        /** Whether to include directories (common prefixes). */
        private final boolean includeDirectories;

        /** The number of items to queue before blocking. */
        private static final long QUEUED_ITEMS_FULL_THRESHOLD_VALUE = 600;
        /** If retrieving items is blocked, once the number of items in the queue drop below
         * this value, retrieving items in the background to fill the queue is resumed. */
        private static final int QUEUED_ITEMS_HYSTERISIS_VALUE = 300;

        /** Keep track of whether an exception occurred while iterating. */
        private volatile AmazonClientException iteratingException;

        /* package */ AvailableS3ContentIterator(final ContentManager contentManager,
                                                 final String s3Prefix,
                                                 final String localPrefix,
                                                 final String delimiter,
                                                 final ExecutorService executorService,
                                                 final boolean includeDirectories) {

            iteratingException = null;
            this.contentManager = contentManager;
            this.executorService = executorService;
            contentCount = new AtomicInteger();
            contentCount.set(0);

            this.prefix = s3Prefix;
            this.localPrefix = localPrefix == null ? "" : localPrefix;
            this.delimiter = delimiter;
            this.includeDirectories = includeDirectories;

            // Start the background task to begin listing objects.
            iteratingFuture = executorService.submit(this);
        }

        @Override
        public void run() {
            final LocalContentCache localContentCache = contentManager.getLocalContentCache();
            // Get an iterator for S3 content.  This starts obtaining content from the bucket in
            // parallel.
            final S3WholeBucketIterator s3Iter = new S3WholeBucketIterator(
                contentManager.getS3Client(), contentManager.getS3bucket(), contentManager.getS3DirPrefix(),
                prefix, delimiter, includeDirectories, this);

            // Iterate the local content.
            for (final File file : localContentCache.getIterableForDirectory(localPrefix)) {
                if (file.exists()) {
                    final String relativePath =
                        localContentCache.absolutePathToRelativePath(file.getAbsolutePath());
                    localFiles.put(relativePath, new FileContent(file, relativePath));
                }
            }

            // Iterate the remote content.
            for (final S3ContentSummary summary : s3Iter) {
                if (contentCount.incrementAndGet() >= QUEUED_ITEMS_FULL_THRESHOLD_VALUE) {
                    waitingForReader.close();
                }
                if (!areIteratingContent) {
                    // Iteration was canceled.  We must cancel iterating S3WholeBucketIterator
                    // to stop it from making any further service calls to get content.
                    s3Iter.cancel();
                    waitingForContent.open();
                    return;
                }
                final FileContent fileContent = localFiles.remove(summary.getFilePath());
                // if file content exists for this item.
                if (fileContent != null) {
                    // if the remote content is different. (Note: we could use summary.getETag() and
                    // check that against the file's MD5SUM to detect modified content, but it would
                    // be significantly slower since we aren't persistantly caching local file
                    // metadata such as MD5SUM.)
                    if (fileContent.getLastModifiedTime() < summary.getLastModifiedTime() ||
                        fileContent.getSize() != summary.getSize()) {
                        fileContent.setContentState(ContentState.CACHED_WITH_NEWER_VERSION_AVAILABLE);
                    }
                    availableContent.add(fileContent);
                } else {
                    if (!ContentState.REMOTE_DIRECTORY.equals(summary.getContentState())) {
                        summary.setContentState(contentManager
                            .getContentStateForTransfer(summary.getFilePath()));
                    }
                    availableContent.add(summary);
                }
                waitingForContent.open();
                if (contentCount.get() >= QUEUED_ITEMS_FULL_THRESHOLD_VALUE) {
                    waitingForReader.block();
                }
            }

            // Add the remaining content.
            for (final FileContent fileContent : localFiles.values()) {
                availableContent.add(fileContent);
            }
            waitingForContent.open();

            areIteratingContent = false;
        }

    /**
     * Call this while iterating to see if the subsequent call to next() will block.  This can
     * also be called in an enhanced for loop.
     * @return true if calling next() will block, otherwise return false.
     */
    public boolean willNextBlock() {
        return (availableContent.isEmpty() && areIteratingContent);
    }

    @Override
    public boolean hasNext() {
        waitingForContent.close();
        if (availableContent.isEmpty() && !areIteratingContent) {
            return false;
        } else if (!availableContent.isEmpty()) {
            return true;
        }
        waitingForContent.block();
        if (iteratingException != null) {
            throw iteratingException;
        }
        return !availableContent.isEmpty();
    }

    @Override
    public ContentItem next() {
        final ContentItem next;
        if (hasNext()) {
            next = availableContent.poll();
            if (contentCount.decrementAndGet() == QUEUED_ITEMS_HYSTERISIS_VALUE) {
                waitingForReader.open();
            }
            return next;
        } else if (iteratingException != null) {
            throw iteratingException;
        }
        return null;
    }

    /**
     * Call this to ensure the background thread is stopped. This only needs to be called in
     * the case that iterator was not exhausted (meaning next() was not called on the iterator
     * until null was returned). After calling this method it is still possible to iterate the
     * remaining items that had been read in the background using next().
     */
    public void cancel() {
        if (areIteratingContent) {
            if (iteratingFuture.isDone()) {
                return;
            }

            areIteratingContent = false;
            waitingForReader.open();
            // indicate we must shut down.
            iteratingFuture.cancel(true);
        }
    }

    @Override
    public void remove() {
        throw new UnsupportedOperationException("Remove not supported.");
    }

    @Override
    public Iterator<ContentItem> iterator() {
        return this;
    }

    @Override
    public void onError(final AmazonClientException ex) {
        Log.e(LOG_TAG, ex.toString());
        iteratingException = ex;
    }
}
