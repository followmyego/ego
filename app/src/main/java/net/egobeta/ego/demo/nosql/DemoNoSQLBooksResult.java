package net.egobeta.ego.demo.nosql;

import android.content.Context;
import android.content.res.Resources;
import android.util.DisplayMetrics;
import android.view.Gravity;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.amazonaws.AmazonClientException;
import net.amazonaws.mobile.AWSMobileClient;

import com.amazonaws.mobileconnectors.dynamodbv2.dynamodbmapper.DynamoDBMapper;

import java.util.Set;

public class DemoNoSQLBooksResult implements DemoNoSQLResult {
    private static final int KEY_TEXT_COLOR = 0xFF333333;
    private final Book result;

    public DemoNoSQLBooksResult(final Book result) {
        this.result = result;
    }

    @Override
    public void updateItem() {
        /** Update Author if this method is called **/
        final DynamoDBMapper mapper = AWSMobileClient.defaultMobileClient().getDynamoDBMapper();
        final String originalValue = result.getAuthor();
        result.setAuthor("C.S Lewis");
        try {
            mapper.save(result);
        } catch (final AmazonClientException ex) {
            /** Restore original data if save fails, and re-throw. **/
            result.setAuthor(originalValue);
            throw ex;
        }
    }

    @Override
    public void deleteItem() {
        final DynamoDBMapper mapper = AWSMobileClient.defaultMobileClient().getDynamoDBMapper();
        mapper.delete(result);
    }

    private void setKeyTextViewStyle(final TextView textView) {
        textView.setTextColor(KEY_TEXT_COLOR);
        LinearLayout.LayoutParams layoutParams = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.MATCH_PARENT);
        layoutParams.setMargins(dp(5), dp(2), dp(5), 0);
        textView.setLayoutParams(layoutParams);
    }

    /**
     * @param dp number of design pixels.
     * @return number of pixels corresponding to the desired design pixels.
     */
    private int dp(int dp) {
        DisplayMetrics metrics = Resources.getSystem().getDisplayMetrics();
        return dp * (metrics.densityDpi / DisplayMetrics.DENSITY_DEFAULT);
    }
    private void setValueTextViewStyle(final TextView textView) {
        LinearLayout.LayoutParams layoutParams = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.MATCH_PARENT);
        layoutParams.setMargins(dp(15), 0, dp(15), dp(2));
        textView.setLayoutParams(layoutParams);
    }

    private void setKeyAndValueTextViewStyles(final TextView keyTextView, final TextView valueTextView) {
        setKeyTextViewStyle(keyTextView);
        setValueTextViewStyle(valueTextView);
    }

    private static String bytesToHexString(byte[] bytes) {
        final StringBuilder builder = new StringBuilder();
        builder.append(String.format("%02X", bytes[0]));
        for(int index = 1; index < bytes.length; index++) {
            builder.append(String.format(" %02X", bytes[index]));
        }
        return builder.toString();
    }

    private static String byteSetsToHexStrings(Set<byte[]> bytesSet) {
        final StringBuilder builder = new StringBuilder();
        int index = 0;
        for (byte[] bytes : bytesSet) {
            builder.append(String.format("%d: ", ++index));
            builder.append(bytesToHexString(bytes));
            if (index < bytesSet.size()) {
                builder.append("\n");
            }
        }
        return builder.toString();
    }

    @Override
    public View getView(final Context context, final View convertView, int position) {
        final LinearLayout layout;
        final TextView resultNumberTextView;
        final TextView isbnKeyTextView;
        final TextView isbnValueTextView;
        final TextView hardcoverKeyTextView;
        final TextView hardcoverValueTextView;
        final TextView authorKeyTextView;
        final TextView authorValueTextView;
        final TextView priceKeyTextView;
        final TextView priceValueTextView;
        final TextView titleKeyTextView;
        final TextView titleValueTextView;
        final TextView awesomeKeyTextView;
        final TextView awesomeValueTextView;
        if (convertView == null) {
            layout = new LinearLayout(context);
            layout.setOrientation(LinearLayout.VERTICAL);
            resultNumberTextView = new TextView(context);
            resultNumberTextView.setGravity(Gravity.CENTER_HORIZONTAL);
            layout.addView(resultNumberTextView);


            isbnKeyTextView = new TextView(context);
            isbnValueTextView = new TextView(context);
            setKeyAndValueTextViewStyles(isbnKeyTextView, isbnValueTextView);
            layout.addView(isbnKeyTextView);
            layout.addView(isbnValueTextView);

            hardcoverKeyTextView = new TextView(context);
            hardcoverValueTextView = new TextView(context);
            setKeyAndValueTextViewStyles(hardcoverKeyTextView, hardcoverValueTextView);
            layout.addView(hardcoverKeyTextView);
            layout.addView(hardcoverValueTextView);

            authorKeyTextView = new TextView(context);
            authorValueTextView = new TextView(context);
            setKeyAndValueTextViewStyles(authorKeyTextView, authorValueTextView);
            layout.addView(authorKeyTextView);
            layout.addView(authorValueTextView);

            priceKeyTextView = new TextView(context);
            priceValueTextView = new TextView(context);
            setKeyAndValueTextViewStyles(priceKeyTextView, priceValueTextView);
            layout.addView(priceKeyTextView);
            layout.addView(priceValueTextView);

            titleKeyTextView = new TextView(context);
            titleValueTextView = new TextView(context);
            setKeyAndValueTextViewStyles(titleKeyTextView, titleValueTextView);
            layout.addView(titleKeyTextView);
            layout.addView(titleValueTextView);

            awesomeKeyTextView = new TextView(context);
            awesomeValueTextView = new TextView(context);
            setKeyAndValueTextViewStyles(awesomeKeyTextView, awesomeValueTextView);
            layout.addView(awesomeKeyTextView);
            layout.addView(awesomeValueTextView);
        } else {
            layout = (LinearLayout) convertView;
            resultNumberTextView = (TextView) layout.getChildAt(0);

            isbnKeyTextView = (TextView) layout.getChildAt(1);
            isbnValueTextView = (TextView) layout.getChildAt(2);

            hardcoverKeyTextView = (TextView) layout.getChildAt(3);
            hardcoverValueTextView = (TextView) layout.getChildAt(4);

            authorKeyTextView = (TextView) layout.getChildAt(5);
            authorValueTextView = (TextView) layout.getChildAt(6);

            priceKeyTextView = (TextView) layout.getChildAt(7);
            priceValueTextView = (TextView) layout.getChildAt(8);

            titleKeyTextView = (TextView) layout.getChildAt(9);
            titleValueTextView = (TextView) layout.getChildAt(10);

            awesomeKeyTextView = (TextView) layout.getChildAt(11);
            awesomeValueTextView = (TextView) layout.getChildAt(12);
        }

        resultNumberTextView.setText(String.format("#%d", +position + 1));

        isbnKeyTextView.setText("ISBN");
        isbnValueTextView.setText(result.getIsbn());

        authorKeyTextView.setText("Author");
        authorValueTextView.setText(result.getAuthor());

        hardcoverKeyTextView.setText("Is Hardcover?");
        hardcoverValueTextView.setText(result.getHardCover().toString());

        priceKeyTextView.setText("Price");
        priceValueTextView.setText("" + result.getPrice());

        titleKeyTextView.setText("Title");
        titleValueTextView.setText("" + result.getTitle());

        awesomeKeyTextView.setText("Is Awesome?");
        awesomeValueTextView.setText(result.getAwesome().toString());

        return layout;
    }
}
