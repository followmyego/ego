package net.egobeta.ego.demo;

import android.app.AlertDialog;
import android.content.Context;
import android.os.AsyncTask;
import android.os.Bundle;
import android.text.Html;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;

import net.amazonaws.mobile.AWSMobileClient;
import net.egobeta.ego.R;

import com.amazonaws.mobileconnectors.amazonmobileanalytics.AnalyticsEvent;
import com.amazonaws.mobileconnectors.amazonmobileanalytics.EventClient;
import com.amazonaws.mobileconnectors.amazonmobileanalytics.monetization.GooglePlayMonetizationEventBuilder;


import java.util.Map;

public class AppAnalyticsDemoFragment extends DemoFragmentBase implements View.OnClickListener {

    private static final String LOG_TAG = AppAnalyticsDemoFragment.class.getSimpleName();

    private ImageButton customEventButton;

    private ImageButton monetizationEventButton;

    private Context context;

    @Override
    public View onCreateView(final LayoutInflater inflater, final ViewGroup container,
                             final Bundle savedInstanceState) {

        // Inflate the layout for this fragment
        final View retVal = inflater.inflate(R.layout.fragment_demo_app_analytics, container, false);
        context = container.getContext();

        customEventButton = (ImageButton)retVal.findViewById(R.id.imageButton_demoAnalytics_customEvent);
        customEventButton.setOnClickListener(this);

        monetizationEventButton = (ImageButton)retVal.findViewById(R.id.imageButton_demoAnalytics_monetizationEvent);
        monetizationEventButton.setOnClickListener(this);

        return retVal;
    }

    @Override
    public void onClick(View view) {
        if (view == customEventButton) {
            generateCustomEvent();
        } else if (view == monetizationEventButton) {
            generateMonetizationEvent();
        }
    }

    private void showAlertMessageForEvent(final AnalyticsEvent event) {
        new AlertDialog.Builder(context)
            .setTitle("EVENT SUBMITTED")
            .setMessage(prettyPrintEvent(event))
            .setNegativeButton("OK", null)
            .create()
            .show();
    }

    private void generateCustomEvent() {
        Log.d(LOG_TAG, "Generating custom event...");

        final EventClient eventClient =
            AWSMobileClient.defaultMobileClient().getMobileAnalyticsManager().getEventClient();

        final AnalyticsEvent event = eventClient.createEvent("DemoCustomEvent")
            // A music app use case might include attributes such as:
            // .withAttribute("Playlist", "Amazing Songs 2015")
            // .withAttribute("Artist", "Various")
            // .withMetric("Song playtime", playTime);
            .withAttribute("DemoAttribute1", "DemoAttributeValue1")
            .withAttribute("DemoAttribute2", "DemoAttributeValue2")
            .withMetric("DemoMetric1", Math.random());

        eventClient.recordEvent(event);
        eventClient.submitEvents();
        showAlertMessageForEvent(event);
    }

    private void generateMonetizationEvent() {
        Log.d(LOG_TAG, "Generating monetization event...");

        final EventClient eventClient =
            AWSMobileClient.defaultMobileClient().getMobileAnalyticsManager().getEventClient();

        // This creates a Google Play monetization event.  To create an Amazon monetization
        // event instead, use AmazonMonetizationEventBuilder.
        final AnalyticsEvent event = GooglePlayMonetizationEventBuilder.create(eventClient)
            .withFormattedItemPrice("$1.00")
            .withProductId("DEMO_PRODUCT_ID")
            .withQuantity(1.0)
            .withTransactionId("DEMO_TRANSACTION_ID").build();

        eventClient.recordEvent(event);
        eventClient.submitEvents();
        showAlertMessageForEvent(event);
    }

    private CharSequence prettyPrintEvent(final AnalyticsEvent event) {
        final StringBuilder buffer = new StringBuilder();
        buffer.append("<b>EVENT TYPE :</b> <br/>");
        buffer.append(event.getEventType());
        buffer.append("<br/>");

        for (final Map.Entry<String, String> attribute : event.getAllAttributes().entrySet()) {
            buffer.append("<b>ATTRIBUTE :</b> <br/>");
            buffer.append(attribute.getKey());
            buffer.append(" = ");
            buffer.append(attribute.getValue());
            buffer.append("<br/>");
        }

        for (final Map.Entry<String, Double> metric : event.getAllMetrics().entrySet()) {
            buffer.append("<b>METRIC :</b> <br/>");
            buffer.append(metric.getKey());
            buffer.append(" = ");
            buffer.append(metric.getValue());
            buffer.append("<br/>");
        }

        return Html.fromHtml(buffer.toString());
    }
}
