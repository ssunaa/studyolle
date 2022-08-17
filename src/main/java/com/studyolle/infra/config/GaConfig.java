package com.studyolle.infra.config;

import com.google.api.client.googleapis.javanet.GoogleNetHttpTransport;
import com.google.api.client.http.HttpRequestInitializer;
import com.google.api.client.http.HttpTransport;
import com.google.api.client.json.JsonFactory;
import com.google.api.client.json.gson.GsonFactory;
import com.google.api.services.analyticsreporting.v4.AnalyticsReporting;
import com.google.api.services.analyticsreporting.v4.AnalyticsReporting.Builder;
import com.google.api.services.analyticsreporting.v4.AnalyticsReportingScopes;
import com.google.api.services.analyticsreporting.v4.model.DateRange;
import com.google.api.services.analyticsreporting.v4.model.Dimension;
import com.google.api.services.analyticsreporting.v4.model.GetReportsRequest;
import com.google.api.services.analyticsreporting.v4.model.GetReportsResponse;
import com.google.api.services.analyticsreporting.v4.model.Metric;
import com.google.api.services.analyticsreporting.v4.model.ReportRequest;
import com.google.auth.http.HttpCredentialsAdapter;
import com.google.auth.oauth2.GoogleCredentials;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * <pre>
 *
 * Project : studyolle
 * Package : com.studyolle.infra.config
 * ClassName : GaConfig
 * Created : 2022-08-09 SEONHWA
 * </pre>
 *
 * @author SEONHWA
 * @since 2022-08-09 오후 4:20
 */
//@Configuration
//@RequiredArgsConstructor //생성자 자동생성
public class GaConfig {

    private static final String APPLICATION_NAME = "Hello Analytics Reporting";
    private static final JsonFactory JSON_FACTORY = GsonFactory.getDefaultInstance();
    private static final String KEY_FILE_LOCATION = "C:/WORK/doc/abstract-arc-279505-35bf63540df1.json";
    private static final String VIEW_ID = "272699439";

    @Bean
    public AnalyticsReporting gaConfig()
            throws Exception {
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

    @Bean
    public static GetReportsResponse getReport(AnalyticsReporting service) throws IOException {
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

}
