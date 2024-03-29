package net.egobeta.ego.demo.nosql;

import android.content.Context;
import android.util.Log;

import com.amazonaws.AmazonClientException;
import net.amazonaws.mobile.AWSMobileClient;
import net.amazonaws.mobile.util.ThreadUtils;
import net.egobeta.ego.R;

import com.amazonaws.mobileconnectors.dynamodbv2.dynamodbmapper.DynamoDBMapper;
import com.amazonaws.mobileconnectors.dynamodbv2.dynamodbmapper.DynamoDBQueryExpression;
import com.amazonaws.mobileconnectors.dynamodbv2.dynamodbmapper.DynamoDBScanExpression;
import com.amazonaws.mobileconnectors.dynamodbv2.dynamodbmapper.PaginatedQueryList;
import com.amazonaws.mobileconnectors.dynamodbv2.dynamodbmapper.PaginatedScanList;
import com.amazonaws.services.dynamodbv2.model.AttributeValue;
import com.amazonaws.services.dynamodbv2.model.ComparisonOperator;
import com.amazonaws.services.dynamodbv2.model.Condition;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;


public class DemoNoSQLTableBooks extends DemoNoSQLTableBase {
    private static final String LOG_TAG = DemoNoSQLTableBooks.class.getSimpleName();

    /** Inner classes use this value to determine how many results to retrieve per service call. */
    private static final int RESULTS_PER_RESULT_GROUP = 40;

    /** The isbn of the book */
    public static String isbn = "12335678904";


    /** Removing sample data removes the items in batches of the following size. */
    private static final int MAX_BATCH_SIZE_FOR_DELETE = 50;


    /********* Primary Get Query Inner Classes *********/

    public class DemoGetWithPartitionKeyAndSortKey extends DemoNoSQLOperationBase {
        private Book result;
        private boolean resultRetrieved = true;

        DemoGetWithPartitionKeyAndSortKey(final Context context) {
            super(context.getString(R.string.nosql_operation_get_by_partition_and_sort_text), String.format(context.getString(R.string.nosql_operation_example_get_by_partition_and_sort_text),
                    "userId", AWSMobileClient.defaultMobileClient().getIdentityManager().getCachedUserID(),
                    "itemId", "demo-userId-500000"));
        }

        @Override
        public boolean executeOperation() {
            // Retrieve an item by passing the partition key using the object mapper.
            result = mapper.load(Book.class, AWSMobileClient.defaultMobileClient().getIdentityManager().getCachedUserID());

            if (result != null) {
                resultRetrieved = false;
                return true;
            }
            return false;
        }

        @Override
        public List<DemoNoSQLResult> getNextResultGroup() {
            if (resultRetrieved) {
                return null;
            }
            final List<DemoNoSQLResult> results = new ArrayList<>();
            results.add(new DemoNoSQLBooksResult(result));
            resultRetrieved = true;
            return results;
        }

        @Override
        public void resetResults() {
            resultRetrieved = false;
        }
    }

    /* ******** Primary Index Query Inner Classes ******** */

    public class DemoQueryWithPartitionKeyAndSortKeyCondition extends DemoNoSQLOperationBase {

        private PaginatedQueryList<Book> results;
        private Iterator<Book> resultsIterator;

        DemoQueryWithPartitionKeyAndSortKeyCondition(final Context context) {
            super(context.getString(R.string.nosql_operation_title_query_by_partition_and_sort_condition_text),
                  String.format(context.getString(R.string.nosql_operation_example_query_by_partition_and_sort_condition_text),
                      "userId", AWSMobileClient.defaultMobileClient().getIdentityManager().getCachedUserID(),
                      "itemId", "demo-userId-500000"));
        }

        @Override
        public boolean executeOperation() {
            final Book itemToFind = new Book();
            itemToFind.setIsbn(isbn);

            final Condition rangeKeyCondition = new Condition()
                .withComparisonOperator(ComparisonOperator.LT.toString())
                .withAttributeValueList(new AttributeValue().withS("demo-itemId-500000"));
            final DynamoDBQueryExpression<Book> queryExpression = new DynamoDBQueryExpression<Book>()
                .withHashKeyValues(itemToFind)
                .withRangeKeyCondition("itemId", rangeKeyCondition)
                .withConsistentRead(false)
                .withLimit(RESULTS_PER_RESULT_GROUP);

            results = mapper.query(Book.class, queryExpression);
            if (results != null) {
                resultsIterator = results.iterator();
                if (resultsIterator.hasNext()) {
                    return true;
                }
            }
            return false;
        }

        /**
         * Gets the next page of results from the query.
         * @return list of results, or null if there are no more results.
         */
        public List<DemoNoSQLResult> getNextResultGroup() {
            return getNextResultsGroupFromIterator(resultsIterator);
        }

        @Override
        public void resetResults() {
            resultsIterator = results.iterator();
        }
    }

    public class DemoQueryWithPartitionKeyOnly extends DemoNoSQLOperationBase {

        private PaginatedQueryList<Book> results;
        private Iterator<Book> resultsIterator;

        DemoQueryWithPartitionKeyOnly(final Context context) {
            super(context.getString(R.string.nosql_operation_title_query_by_partition_text),
                String.format(context.getString(R.string.nosql_operation_example_query_by_partition_text),
                    "userId", AWSMobileClient.defaultMobileClient().getIdentityManager().getCachedUserID()));
        }

        @Override
        public boolean executeOperation() {
            final Book itemToFind = new Book();
            itemToFind.setIsbn(isbn);

            final DynamoDBQueryExpression<Book> queryExpression = new DynamoDBQueryExpression<Book>()
                .withHashKeyValues(itemToFind)
                .withConsistentRead(false)
                .withLimit(RESULTS_PER_RESULT_GROUP);

            results = mapper.query(Book.class, queryExpression);
            if (results != null) {
                resultsIterator = results.iterator();
                if (resultsIterator.hasNext()) {
                    return true;
                }
            }
            return false;
        }

        @Override
        public List<DemoNoSQLResult> getNextResultGroup() {
            return getNextResultsGroupFromIterator(resultsIterator);
        }

        @Override
        public void resetResults() {
            resultsIterator = results.iterator();
        }
    }

    public class DemoQueryWithPartitionKeyAndFilter extends DemoNoSQLOperationBase {

        private PaginatedQueryList<Book> results;
        private Iterator<Book> resultsIterator;

        DemoQueryWithPartitionKeyAndFilter(final Context context) {
            super(context.getString(R.string.nosql_operation_title_query_by_partition_and_filter_text),
                  String.format(context.getString(R.string.nosql_operation_example_query_by_partition_and_filter_text),
                      "userId", AWSMobileClient.defaultMobileClient().getIdentityManager().getCachedUserID(),
                      "latitude", "1111500000"));
        }

        @Override
        public boolean executeOperation() {
            final Book itemToFind = new Book();
            itemToFind.setIsbn(isbn);

            // Use an expression names Map to avoid the potential for attribute names
            // colliding with DynamoDB reserved words.
            final Map <String, String> filterExpressionAttributeNames = new HashMap<>();
            filterExpressionAttributeNames.put("#latitude", "latitude");

            final Map<String, AttributeValue> filterExpressionAttributeValues = new HashMap<>();
            filterExpressionAttributeValues.put(":Minlatitude",
                new AttributeValue().withN("1111500000"));

            final DynamoDBQueryExpression<Book> queryExpression = new DynamoDBQueryExpression<Book>()
                .withHashKeyValues(itemToFind)
                .withFilterExpression("#latitude > :Minlatitude")
                .withExpressionAttributeNames(filterExpressionAttributeNames)
                .withExpressionAttributeValues(filterExpressionAttributeValues)
                .withConsistentRead(false)
                .withLimit(RESULTS_PER_RESULT_GROUP);

            results = mapper.query(Book.class, queryExpression);
            if (results != null) {
                resultsIterator = results.iterator();
                if (resultsIterator.hasNext()) {
                    return true;
                }
            }
            return false;
        }

        @Override
        public List<DemoNoSQLResult> getNextResultGroup() {
            return getNextResultsGroupFromIterator(resultsIterator);
        }

        @Override
        public void resetResults() {
             resultsIterator = results.iterator();
         }
    }

    public class DemoQueryWithPartitionKeySortKeyConditionAndFilter extends DemoNoSQLOperationBase {

        private PaginatedQueryList<Book> results;
        private Iterator<Book> resultsIterator;

        DemoQueryWithPartitionKeySortKeyConditionAndFilter(final Context context) {
            super(context.getString(R.string.nosql_operation_title_query_by_partition_sort_condition_and_filter_text),
                  String.format(context.getString(R.string.nosql_operation_example_query_by_partition_sort_condition_and_filter_text),
                      "userId", AWSMobileClient.defaultMobileClient().getIdentityManager().getCachedUserID(),
                      "itemId", "demo-userId-500000",
                      "latitude", "1111500000"));
        }

        public boolean executeOperation() {
            final Book itemToFind = new Book();
            itemToFind.setIsbn(isbn);

            final Condition rangeKeyCondition = new Condition()
                    .withComparisonOperator(ComparisonOperator.LT.toString())
                .withAttributeValueList(new AttributeValue().withS("demo-itemId-500000"));

            // Use an expression names Map to avoid the potential for attribute names
            // colliding with DynamoDB reserved words.
            final Map <String, String> filterExpressionAttributeNames = new HashMap<>();
            filterExpressionAttributeNames.put("#latitude", "latitude");

            final Map<String, AttributeValue> filterExpressionAttributeValues = new HashMap<>();
            filterExpressionAttributeValues.put(":Minlatitude",
                new AttributeValue().withN("1111500000"));

            final DynamoDBQueryExpression<Book> queryExpression = new DynamoDBQueryExpression<Book>()
                .withHashKeyValues(itemToFind)
                .withRangeKeyCondition("itemId", rangeKeyCondition)
                .withFilterExpression("#latitude > :Minlatitude")
                .withExpressionAttributeNames(filterExpressionAttributeNames)
                .withExpressionAttributeValues(filterExpressionAttributeValues)
                .withConsistentRead(false)
                .withLimit(RESULTS_PER_RESULT_GROUP);

            results = mapper.query(Book.class, queryExpression);
            if (results != null) {
                resultsIterator = results.iterator();
                if (resultsIterator.hasNext()) {
                    return true;
                }
            }
            return false;
        }

        @Override
        public List<DemoNoSQLResult> getNextResultGroup() {
            return getNextResultsGroupFromIterator(resultsIterator);
        }

        @Override
        public void resetResults() {
            resultsIterator = results.iterator();
        }
    }

    /* ******** Secondary Named Index Query Inner Classes ******** */


    public class DemoCategoriesQueryWithPartitionKeyAndSortKeyCondition extends DemoNoSQLOperationBase {

        private PaginatedQueryList<Book> results;
        private Iterator<Book> resultsIterator;
        DemoCategoriesQueryWithPartitionKeyAndSortKeyCondition (final Context context) {
            super(
                context.getString(R.string.nosql_operation_title_index_query_by_partition_and_sort_condition_text),
                context.getString(R.string.nosql_operation_example_index_query_by_partition_and_sort_condition_text,
                    "category", "demo-category-3",
                    "longitude", "1111500000"));
        }

        public boolean executeOperation() {
            // Perform a query using a partition key and sort key condition.
            final Book itemToFind = new Book();
            itemToFind.setAuthor("Charles Dickens");
            final Condition sortKeyCondition = new Condition()
                .withComparisonOperator(ComparisonOperator.LT.toString())

                .withAttributeValueList(new AttributeValue().withN(Double.toString(1111500000.0)));
            // Perform get using Partition key and sort key condition
            DynamoDBQueryExpression<Book> queryExpression = new DynamoDBQueryExpression<Book>()
                .withHashKeyValues(itemToFind)
                .withRangeKeyCondition("longitude", sortKeyCondition)
                .withConsistentRead(false);
            results = mapper.query(Book.class, queryExpression);
            if (results != null) {
                resultsIterator = results.iterator();
                if (resultsIterator.hasNext()) {
                    return true;
                }
            }
            return false;
        }

        @Override
        public List<DemoNoSQLResult> getNextResultGroup() {
            return getNextResultsGroupFromIterator(resultsIterator);
        }

        @Override
        public void resetResults() {
            resultsIterator = results.iterator();
        }
    }

    public class DemoCategoriesQueryWithPartitionKeyOnly extends DemoNoSQLOperationBase {

        private PaginatedQueryList<Book> results;
        private Iterator<Book> resultsIterator;

        DemoCategoriesQueryWithPartitionKeyOnly(final Context context) {
            super(
                context.getString(R.string.nosql_operation_title_index_query_by_partition_text),
                context.getString(R.string.nosql_operation_example_index_query_by_partition_text,
                    "category", "demo-category-3"));
        }

        public boolean executeOperation() {
            // Perform a query using a partition key and filter condition.
            final Book itemToFind = new Book();
            itemToFind.setAuthor("Charles Dickens");

            // Perform get using Partition key
            DynamoDBQueryExpression<Book> queryExpression = new DynamoDBQueryExpression<Book>()
                .withHashKeyValues(itemToFind)
                .withConsistentRead(false);
            results = mapper.query(Book.class, queryExpression);
            if (results != null) {
                resultsIterator = results.iterator();
                if (resultsIterator.hasNext()) {
                    return true;
                }
            }
            return false;
        }

        @Override
        public List<DemoNoSQLResult> getNextResultGroup() {
            return getNextResultsGroupFromIterator(resultsIterator);
        }

        @Override
        public void resetResults() {
            resultsIterator = results.iterator();
        }
    }

    public class DemoCategoriesQueryWithPartitionKeyAndFilterCondition extends DemoNoSQLOperationBase {

        private PaginatedQueryList<Book> results;
        private Iterator<Book> resultsIterator;

        DemoCategoriesQueryWithPartitionKeyAndFilterCondition (final Context context) {
            super(
                context.getString(R.string.nosql_operation_title_index_query_by_partition_and_filter_text),
                context.getString(R.string.nosql_operation_example_index_query_by_partition_and_filter_text,
                    "category","demo-category-3",
                    "itemId", "demo-itemId-500000"));
        }

        public boolean executeOperation() {
            // Perform a query using a partition key and filter condition.
            final Book itemToFind = new Book();
            itemToFind.setAuthor("Charles Dickens");

            // Use an expression names Map to avoid the potential for attribute names
            // colliding with DynamoDB reserved words.
            final Map <String, String> filterExpressionAttributeNames = new HashMap<>();
            filterExpressionAttributeNames.put("#itemId", "itemId");

            final Map<String, AttributeValue> filterExpressionAttributeValues = new HashMap<>();
            filterExpressionAttributeValues.put(":MinitemId",
                new AttributeValue().withS("demo-itemId-500000"));

            // Perform get using Partition key and sort key condition
            DynamoDBQueryExpression<Book> queryExpression = new DynamoDBQueryExpression<Book>()
                .withHashKeyValues(itemToFind)
                .withFilterExpression("#itemId > :MinitemId")
                .withExpressionAttributeNames(filterExpressionAttributeNames)
                .withExpressionAttributeValues(filterExpressionAttributeValues)
                .withConsistentRead(false);
            results = mapper.query(Book.class, queryExpression);
            if (results != null) {
                resultsIterator = results.iterator();
                if (resultsIterator.hasNext()) {
                    return true;
                }
            }
            return false;
        }

        @Override
        public List<DemoNoSQLResult> getNextResultGroup() {
            return getNextResultsGroupFromIterator(resultsIterator);
        }

        @Override
        public void resetResults() {
            resultsIterator = results.iterator();
        }
    }

    public class DemoCategoriesQueryWithPartitionKeySortKeyAndFilterCondition extends DemoNoSQLOperationBase {

        private PaginatedQueryList<Book> results;
        private Iterator<Book> resultsIterator;

        DemoCategoriesQueryWithPartitionKeySortKeyAndFilterCondition (final Context context) {
            super(
                context.getString(R.string.nosql_operation_title_index_query_by_partition_sort_condition_and_filter_text),
                context.getString(R.string.nosql_operation_example_index_query_by_partition_sort_condition_and_filter_text,
                    "category", "demo-category-3",
                    "longitude", "1111500000",
                    "itemId", "demo-itemId-500000"));
        }

        public boolean executeOperation() {
            // Perform a query using a partition key, sort condition, and filter.
            final Book itemToFind = new Book();
            itemToFind.setAuthor("Charles Dickens");
            final Condition sortKeyCondition = new Condition()
                .withComparisonOperator(ComparisonOperator.LT.toString())
                .withAttributeValueList(new AttributeValue().withN(Double.toString(1111500000.0)));

            // Use a map of expression names to avoid the potential for attribute names
            // colliding with DynamoDB reserved words.
            final Map<String, String> filterExpressionAttributeNames = new HashMap<>();
            filterExpressionAttributeNames.put("#itemId", "itemId");

            final Map<String, AttributeValue> filterExpressionAttributeValues = new HashMap<>();
            filterExpressionAttributeValues.put(":MinitemId",
                new AttributeValue().withS("demo-itemId-500000"));

            // Perform get using Partition key and sort key condition
            DynamoDBQueryExpression<Book> queryExpression = new DynamoDBQueryExpression<Book>()
                .withHashKeyValues(itemToFind)
                .withRangeKeyCondition("longitude", sortKeyCondition)
                .withFilterExpression("#itemId > :MinitemId")
                .withExpressionAttributeNames(filterExpressionAttributeNames)
                .withExpressionAttributeValues(filterExpressionAttributeValues)
                .withConsistentRead(false);
            results = mapper.query(Book.class, queryExpression);
            if (results != null) {
                resultsIterator = results.iterator();
                if (resultsIterator.hasNext()) {
                    return true;
                }
            }
            return false;
        }

        @Override
        public List<DemoNoSQLResult> getNextResultGroup() {
            return getNextResultsGroupFromIterator(resultsIterator);
        }

        @Override
        public void resetResults() {
            resultsIterator = results.iterator();
        }
    }

    /********* Scan Inner Classes *********/

    public class DemoScanWithFilter extends DemoNoSQLOperationBase {

        private PaginatedScanList<Book> results;
        private Iterator<Book> resultsIterator;

        DemoScanWithFilter(final Context context) {
            super(context.getString(R.string.nosql_operation_title_scan_with_filter),
                String.format(context.getString(R.string.nosql_operation_example_scan_with_filter),
                    "latitude", "1111500000"));
        }

        @Override
        public boolean executeOperation() {
            // Use an expression names Map to avoid the potential for attribute names
            // colliding with DynamoDB reserved words.
            final Map <String, String> filterExpressionAttributeNames = new HashMap<>();
            filterExpressionAttributeNames.put("#latitude", "latitude");

            final Map<String, AttributeValue> filterExpressionAttributeValues = new HashMap<>();
            filterExpressionAttributeValues.put(":Minlatitude", new AttributeValue().withN("1111500000"));

            final DynamoDBScanExpression scanExpression = new DynamoDBScanExpression()
                .withFilterExpression("#latitude > :Minlatitude")
                .withExpressionAttributeNames(filterExpressionAttributeNames)
                .withExpressionAttributeValues(filterExpressionAttributeValues);

            results = mapper.scan(Book.class, scanExpression);
            if (results != null) {
                resultsIterator = results.iterator();
                if (resultsIterator.hasNext()) {
                    return true;
                }
            }
            return false;
        }

        @Override
        public List<DemoNoSQLResult> getNextResultGroup() {
            return getNextResultsGroupFromIterator(resultsIterator);
        }

        @Override
        public boolean isScan() {
            return true;
        }

        @Override
        public void resetResults() {
            resultsIterator = results.iterator();
        }
    }

    public class DemoScanWithoutFilter extends DemoNoSQLOperationBase {

        private PaginatedScanList<Book> results;
        private Iterator<Book> resultsIterator;

        DemoScanWithoutFilter(final Context context) {
            super(context.getString(R.string.nosql_operation_title_scan_without_filter),
                context.getString(R.string.nosql_operation_example_scan_without_filter));
        }

        @Override
        public boolean executeOperation() {
            final DynamoDBScanExpression scanExpression = new DynamoDBScanExpression();
            results = mapper.scan(Book.class, scanExpression);
            if (results != null) {
                resultsIterator = results.iterator();
                if (resultsIterator.hasNext()) {
                    return true;
                }
            }
            return false;
        }

        @Override
        public List<DemoNoSQLResult> getNextResultGroup() {
            return getNextResultsGroupFromIterator(resultsIterator);
        }

        @Override
        public boolean isScan() {
            return true;
        }

        @Override
        public void resetResults() {
            resultsIterator = results.iterator();
        }
    }

    /**
     * Helper Method to handle retrieving the next group of query results.
     * @param resultsIterator the iterator for all the results (makes a new service call for each result group).
     * @return the next list of results.
     */
    private static List<DemoNoSQLResult> getNextResultsGroupFromIterator(final Iterator<Book> resultsIterator) {
        if (!resultsIterator.hasNext()) {
            return null;
        }
        List<DemoNoSQLResult> resultGroup = new LinkedList<>();
        int itemsRetrieved = 0;
        do {
            // Retrieve the item from the paginated results.
            final Book item = resultsIterator.next();
            // Add the item to a group of results that will be displayed later.
            resultGroup.add(new DemoNoSQLBooksResult(item));
            itemsRetrieved++;
        } while ((itemsRetrieved < RESULTS_PER_RESULT_GROUP) && resultsIterator.hasNext());
        return resultGroup;
    }

    /** The DynamoDB object mapper for accessing DynamoDB. */
    private final DynamoDBMapper mapper;

    public DemoNoSQLTableBooks() {
        mapper = AWSMobileClient.defaultMobileClient().getDynamoDBMapper();
    }

    @Override
    public String getTableName() {
        return "Locations";
    }

    @Override
    public String getPartitionKeyName() {
        return "Artist";
    }

    public String getPartitionKeyType() {
        return "String";
    }

    @Override
    public String getSortKeyName() {
        return "itemId";
    }

    public String getSortKeyType() {
        return "String";
    }

    @Override
    public int getNumIndexes() {
        return 1;
    }

    @Override
    public void insertSampleData() throws AmazonClientException {
        Log.d(LOG_TAG, "Inserting Sample data.");
        final Book firstItem = new Book();

        /*********************** First, save a single book ****************************/
        firstItem.setIsbn(isbn);
        firstItem.setAuthor("Charles Dickens");
        firstItem.setHardCover(true);
        firstItem.setPrice(1299);
        firstItem.setTitle("Moby Dick");
        firstItem.setAwesome(true);
        AmazonClientException lastException = null;

        try {
            mapper.save(firstItem);
        } catch (final AmazonClientException ex) {
            Log.e(LOG_TAG, "Failed saving item : " + ex.getMessage(), ex);
            lastException = ex;
        }
        /******************* Now save an array(batch) of books ************************/
        final Book[] items = new Book[SAMPLE_DATA_ENTRIES_PER_INSERT - 1];
        for (int count = 0; count < SAMPLE_DATA_ENTRIES_PER_INSERT - 1; count++) {
            final Book item = new Book();
            item.setIsbn(isbn);
            item.setAuthor("Charles Dickens");
            item.setHardCover(true);
            item.setPrice(1299);
            item.setTitle("Moby Dick");
            item.setAwesome(true);

            items[count] = item;
        }


        try {
            mapper.batchSave(Arrays.asList(items));
        } catch (final AmazonClientException ex) {
            Log.e(LOG_TAG, "Failed saving item batch : " + ex.getMessage(), ex);
            lastException = ex;
        }
        /**************************************************************************/

        if (lastException != null) {
            // Re-throw the last exception encountered to alert the user.
            throw lastException;
        }
    }

    @Override
    public void removeSampleData() throws AmazonClientException {

        final Book itemToFind = new Book();
        itemToFind.setIsbn(isbn);

        final DynamoDBQueryExpression<Book> queryExpression = new DynamoDBQueryExpression<Book>()
            .withHashKeyValues(itemToFind)
            .withConsistentRead(false)
            .withLimit(MAX_BATCH_SIZE_FOR_DELETE);

        final PaginatedQueryList<Book> results = mapper.query(Book.class, queryExpression);

        Iterator<Book> resultsIterator = results.iterator();

        AmazonClientException lastException = null;

        if (resultsIterator.hasNext()) {
            final Book item = resultsIterator.next();

            // Demonstrate deleting a single item.
            try {
                mapper.delete(item);
            } catch (final AmazonClientException ex) {
                Log.e(LOG_TAG, "Failed deleting item : " + ex.getMessage(), ex);
                lastException = ex;
            }
        }

        final List<Book> batchOfItems = new LinkedList<Book>();
        while (resultsIterator.hasNext()) {
            // Build a batch of books to delete.
            for (int index = 0; index < MAX_BATCH_SIZE_FOR_DELETE && resultsIterator.hasNext(); index++) {
                batchOfItems.add(resultsIterator.next());
            }
            try {
                // Delete a batch of items.
                mapper.batchDelete(batchOfItems);
            } catch (final AmazonClientException ex) {
                Log.e(LOG_TAG, "Failed deleting item batch : " + ex.getMessage(), ex);
                lastException = ex;
            }

            // clear the list for re-use.
            batchOfItems.clear();
        }


        if (lastException != null) {
            // Re-throw the last exception encountered to alert the user.
            // The logs contain all the exceptions that occurred during attempted delete.
            throw lastException;
        }
    }

    private List<DemoNoSQLOperationListItem> getSupportedDemoOperations(final Context context) {
        List<DemoNoSQLOperationListItem> noSQLOperationsList = new ArrayList<DemoNoSQLOperationListItem>();
        noSQLOperationsList.add(new DemoNoSQLOperationListHeader(
            context.getString(R.string.nosql_operation_header_get)));
        noSQLOperationsList.add(new DemoGetWithPartitionKeyAndSortKey(context));

        noSQLOperationsList.add(new DemoNoSQLOperationListHeader(
            context.getString(R.string.nosql_operation_header_primary_queries)));
        noSQLOperationsList.add(new DemoQueryWithPartitionKeyOnly(context));
        noSQLOperationsList.add(new DemoQueryWithPartitionKeyAndFilter(context));
        noSQLOperationsList.add(new DemoQueryWithPartitionKeyAndSortKeyCondition(context));
        noSQLOperationsList.add(new DemoQueryWithPartitionKeySortKeyConditionAndFilter(context));

        noSQLOperationsList.add(new DemoNoSQLOperationListHeader(
            context.getString(R.string.nosql_operation_header_secondary_queries, "Categories")));

        noSQLOperationsList.add(new DemoCategoriesQueryWithPartitionKeyOnly(context));
        noSQLOperationsList.add(new DemoCategoriesQueryWithPartitionKeyAndFilterCondition(context));
        noSQLOperationsList.add(new DemoCategoriesQueryWithPartitionKeyAndFilterCondition(context));
        noSQLOperationsList.add(new DemoCategoriesQueryWithPartitionKeyAndSortKeyCondition(context));
        noSQLOperationsList.add(new DemoCategoriesQueryWithPartitionKeySortKeyAndFilterCondition(context));
        noSQLOperationsList.add(new DemoNoSQLOperationListHeader(
            context.getString(R.string.nosql_operation_header_scan)));
        noSQLOperationsList.add(new DemoScanWithoutFilter(context));
        noSQLOperationsList.add(new DemoScanWithFilter(context));
        return noSQLOperationsList;
    }

    @Override
    public void getSupportedDemoOperations(final Context context,
                                           final SupportedDemoOperationsHandler opsHandler) {
        new Thread(new Runnable() {
            @Override
            public void run() {
                final List<DemoNoSQLOperationListItem> supportedOperations = getSupportedDemoOperations(context);
                ThreadUtils.runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        opsHandler.onSupportedOperationsReceived(supportedOperations);
                    }
                });
            }
        }).start();
    }
}
