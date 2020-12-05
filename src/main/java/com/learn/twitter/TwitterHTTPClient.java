package com.learn.twitter;

import java.io.IOException;
import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.logging.Logger;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.HttpClient;
import org.apache.http.client.config.CookieSpecs;
import org.apache.http.client.config.RequestConfig;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.utils.URIBuilder;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.util.EntityUtils;

/** The Class TwitterHTTPClient. */
public class TwitterHTTPClient {

  static Logger logger = Logger.getLogger(TwitterHTTPClient.class.getName());

  // To set your enviornment variables in your terminal run the following line:
  // export 'BEARER_TOKEN'='<your_bearer_token>'

  /**
   * The main method.
   *
   * @param args the arguments
   * @throws IOException Signals that an I/O exception has occurred.
   * @throws URISyntaxException the URI syntax exception
   */
  public static void main(String args[]) throws IOException, URISyntaxException {
    String bearerToken = System.getenv("BEARER_TOKEN");
    if (null != bearerToken) {
      // Replace comma separated usernames with usernames of your choice
      String response = getUsers("TwitterDev,TwitterEng", bearerToken);
      logger.info(response);
    } else {
      logger.info(
          "There was a problem getting you bearer token. Please make sure you set the BEARER_TOKEN environment variable");
    }
  }

  /**
   * Gets the users.
   *
   * @param usernames the usernames
   * @param bearerToken the bearer token
   * @return the users
   * @throws IOException Signals that an I/O exception has occurred.
   * @throws URISyntaxException the URI syntax exception
   */
  /*
   * This method calls the v2 Users endpoint with usernames as query parameter
   * */
  private static String getUsers(String usernames, String bearerToken)
      throws IOException, URISyntaxException {
    String userResponse = null;

    HttpClient httpClient =
        HttpClients.custom()
            .setDefaultRequestConfig(
                RequestConfig.custom().setCookieSpec(CookieSpecs.STANDARD).build())
            .build();

    URIBuilder uriBuilder = new URIBuilder("https://api.twitter.com/2/users/by");
    ArrayList<NameValuePair> queryParameters;
    queryParameters = new ArrayList<NameValuePair>();
    queryParameters.add(new BasicNameValuePair("usernames", usernames));
    queryParameters.add(
        new BasicNameValuePair("user.fields", "created_at,description,pinned_tweet_id"));
    uriBuilder.addParameters(queryParameters);

    HttpGet httpGet = new HttpGet(uriBuilder.build());
    httpGet.setHeader("Authorization", String.format("Bearer %s", bearerToken));
    httpGet.setHeader("Content-Type", "application/json");

    HttpResponse response = httpClient.execute(httpGet);
    HttpEntity entity = response.getEntity();
    if (null != entity) {
      userResponse = EntityUtils.toString(entity, "UTF-8");
    }
    return userResponse;
  }
}
