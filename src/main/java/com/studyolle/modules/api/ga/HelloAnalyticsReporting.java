package com.studyolle.modules.api.ga;

import com.google.api.client.googleapis.javanet.GoogleNetHttpTransport;
import com.google.api.client.http.HttpRequestInitializer;
import com.google.api.client.http.HttpTransport;
import com.google.api.client.json.JsonFactory;
import com.google.api.client.json.gson.GsonFactory;
import com.google.api.services.analyticsreporting.v4.AnalyticsReporting;
import com.google.api.services.analyticsreporting.v4.AnalyticsReporting.Builder;
import com.google.api.services.analyticsreporting.v4.AnalyticsReportingScopes;
import com.google.api.services.analyticsreporting.v4.model.ColumnHeader;
import com.google.api.services.analyticsreporting.v4.model.DateRange;
import com.google.api.services.analyticsreporting.v4.model.DateRangeValues;
import com.google.api.services.analyticsreporting.v4.model.Dimension;
import com.google.api.services.analyticsreporting.v4.model.GetReportsRequest;
import com.google.api.services.analyticsreporting.v4.model.GetReportsResponse;
import com.google.api.services.analyticsreporting.v4.model.Metric;
import com.google.api.services.analyticsreporting.v4.model.MetricHeaderEntry;
import com.google.api.services.analyticsreporting.v4.model.Report;
import com.google.api.services.analyticsreporting.v4.model.ReportRequest;
import com.google.api.services.analyticsreporting.v4.model.ReportRow;
import com.google.auth.http.HttpCredentialsAdapter;
import com.google.auth.oauth2.GoogleCredentials;
import java.io.FileInputStream;
import java.io.IOException;
import java.security.GeneralSecurityException;
import java.util.ArrayList;
import java.util.List;
import lombok.extern.slf4j.Slf4j;

/**
 * <pre>
 *
 * Project : studyolle
 * Package : com.studyolle.modules.api.ga
 * ClassName : GaTestConfig
 * Created : 2022-08-05 SEONHWA
 * </pre>
 *
 * @author SEONHWA
 * @since 2022-08-05 오후 3:30
 */
@Slf4j
public class HelloAnalyticsReporting {

    private static final String APPLICATION_NAME = "Hello Analytics Reporting";
    private static final JsonFactory JSON_FACTORY = GsonFactory.getDefaultInstance();
    private static final String KEY_FILE_LOCATION = "C:/WORK/doc/abstract-arc-279505-35bf63540df1.json";
    private static final String VIEW_ID = "272699439";

    public static void main(String[] args) {
        try {
            AnalyticsReporting service = initializeAnalyticsReporting();

            GetReportsResponse response = getReport(service);
            printResponse(response);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * Initializes an Analytics Reporting API V4 service object.
     *
     * @return An authorized Analytics Reporting API V4 service object.
     */
    private static AnalyticsReporting initializeAnalyticsReporting()
            throws GeneralSecurityException, IOException {

        HttpTransport httpTransport = GoogleNetHttpTransport.newTrustedTransport();
        GoogleCredentials credential = GoogleCredentials
                .fromStream(new FileInputStream(KEY_FILE_LOCATION))
                .createScoped(AnalyticsReportingScopes.all());
        HttpRequestInitializer httpRequestInitializer = new HttpCredentialsAdapter(credential);

        // Construct the Analytics Reporting service object.
        return new Builder(httpTransport, JSON_FACTORY, httpRequestInitializer)
                .setApplicationName(APPLICATION_NAME)
                .build();
    }

    /**
     * Queries the Analytics Reporting API V4.
     *
     * @param service An authorized Analytics Reporting API V4 service object.
     * @return GetReportResponse The Analytics Reporting API V4 response.
     */
    private static GetReportsResponse getReport(AnalyticsReporting service)
            throws IOException {
        // Create the DateRange object.
        DateRange dateRange = new DateRange();
        dateRange.setStartDate("7DaysAgo");
        dateRange.setEndDate("today");

        // Create the Metrics object.
        Metric sessions = new Metric()
                .setExpression("ga:sessions")
                .setAlias("sessions");

        Dimension pageTitle = new Dimension().setName("ga:pageTitle");

        // Create the ReportRequest object.
        ReportRequest request = new ReportRequest()
                .setViewId(VIEW_ID)
                .setDateRanges(List.of(dateRange))
                .setMetrics(List.of(sessions))
                .setDimensions(List.of(pageTitle));

        ArrayList<ReportRequest> requests = new ArrayList<>();
        requests.add(request);

        // Create the GetReportsRequest object.
        GetReportsRequest getReport = new GetReportsRequest().setReportRequests(requests);

        // Call the batchGet method.
        return service
                .reports()
                .batchGet(getReport)
                .execute();
    }

    /**
     * Parses and prints the Analytics Reporting API V4 response.
     *
     * @param response An Analytics Reporting API V4 response.
     */
    private static void printResponse(GetReportsResponse response) {

        for (Report report : response.getReports()) {
            ColumnHeader header = report.getColumnHeader();
            List<String> dimensionHeaders = header.getDimensions();
            List<MetricHeaderEntry> metricHeaders = header
                    .getMetricHeader()
                    .getMetricHeaderEntries();
            List<ReportRow> rows = report
                    .getData()
                    .getRows();

            if (rows == null) {
                System.out.println("No data found for " + VIEW_ID);
                return;
            }

            for (ReportRow row : rows) {
                List<String> dimensions = row.getDimensions();
                List<DateRangeValues> metrics = row.getMetrics();

                for (int i = 0; i < dimensionHeaders.size() && i < dimensions.size(); i++) {
                    log.debug(dimensionHeaders.get(i) + ": " + dimensions.get(i));
                }

                for (int j = 0; j < metrics.size(); j++) {
                    log.debug("Date Range (" + j + "): ");
                    DateRangeValues values = metrics.get(j);
                    for (int k = 0; k < values
                            .getValues()
                            .size() && k < metricHeaders.size(); k++) {
                        log.debug(metricHeaders
                                .get(k)
                                .getName() + ": " + values
                                .getValues()
                                .get(k));
                    }
                }
            }
        }
    }
}
