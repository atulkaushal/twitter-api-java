package com.learn.twitter;

import java.io.IOException;
import java.util.List;
import java.util.function.Consumer;
import java.util.logging.Logger;

import com.fasterxml.jackson.core.JsonParseException;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.github.redouane59.twitter.TwitterClient;
import com.github.redouane59.twitter.dto.stream.StreamRules.StreamMeta;
import com.github.redouane59.twitter.dto.stream.StreamRules.StreamRule;
import com.github.redouane59.twitter.dto.tweet.TweetV2;
import com.github.redouane59.twitter.dto.tweet.TweetV2.TweetData;
import com.github.redouane59.twitter.dto.user.UserPublicMetrics;
import com.github.redouane59.twitter.dto.user.UserV2;
import com.github.redouane59.twitter.dto.user.UserV2.UserData.Includes;

/**
 * The Class TwitterAPI.
 *
 * @author Atul
 */
public class TwitterAPI {

  static Logger logger = Logger.getLogger(TwitterAPI.class.getName());

  /**
   * The main method.
   *
   * @param args the arguments
   * @throws JsonParseException the json parse exception
   * @throws JsonMappingException the json mapping exception
   * @throws IOException Signals that an I/O exception has occurred.
   */
  public static void main(String[] args)
      throws JsonParseException, JsonMappingException, IOException {

    // Two ways to pass credentials:
    // 1. By specifying path of credentials json file in application code.

    /*TwitterClient twitterClient =
    new TwitterClient(
    		TwitterClient.OBJECT_MAPPER.readValue(
    				new File("your/path/to/json"),
    				TwitterCredentials.class));*/

    // 2. By passing credential json file path as vm arguments
    // -Dtwitter.credentials.file.path=/your/path/to/json

    TwitterClient twitterClient = new TwitterClient();

    printUserDetails(twitterClient);

    // Get all rules.
    List<StreamRule> allRules = twitterClient.retrieveFilteredStreamRules();

    // print all rules.
    printAllRules(allRules);

    // Delete a single rule by mentioning rule value
    // deleteFilteredStreamRule(twitterClient, "Rule for Java");

    // delete all filter rules.
    deleteAllFilteredStreamRules(twitterClient, allRules);

    // Create rule for filteredStream by providing value and tag.
    twitterClient.addFilteredStreamRule("Java", "Rule for Java");
    twitterClient.addFilteredStreamRule("#100DaysOfCode", "Rule for 100 days of code");

    readStream(twitterClient);
  }

  /**
   * Read stream.
   *
   * @param twitterClient the twitter client
   */
  @SuppressWarnings("unchecked")
  private static void readStream(TwitterClient twitterClient) {
    logger.info("Reading Stream:");
    twitterClient.startFilteredStream(
        new Consumer() {

          public void accept(Object t) {
            TweetV2 tweet = (TweetV2) t;
            logger.info(tweet.getText());
            logger.info(tweet.getData().getTweetType().toString());
          }
        });
  }

  /**
   * Delete all filtered stream rules.
   *
   * @param twitterClient the twitter client
   * @param allRules the all rules
   */
  private static void deleteAllFilteredStreamRules(
      TwitterClient twitterClient, List<StreamRule> allRules) {
    if (allRules != null) {
      for (StreamRule streamRule : allRules) {
        deleteFilteredStreamRule(twitterClient, streamRule.getValue());
      }
    } else {
      logger.info("No rule found to delete.");
    }
  }

  /**
   * Delete filtered stream rule.
   *
   * @param twitterClient the twitter client
   * @param ruleValue the rule value
   */
  private static void deleteFilteredStreamRule(TwitterClient twitterClient, String ruleValue) {
    StreamMeta streamMeta = twitterClient.deleteFilteredStreamRule(ruleValue);
    logger.info(streamMeta.getSummary().toString());
  }

  /**
   * Prints the all rules.
   *
   * @param allRules the all rules
   */
  private static void printAllRules(List<StreamRule> allRules) {
    if (allRules != null) {
      for (StreamRule rule : allRules) {
        logger.info(rule.getId());
        logger.info(rule.getTag() + "," + rule.getValue());
      }
    } else {
      logger.info("No rule found.");
    }
  }

  /**
   * Prints the user details.
   *
   * @param twitterClient the twitter client
   */
  private static void printUserDetails(TwitterClient twitterClient) {
    UserV2 user = twitterClient.getUserFromUserName("TwitterDev");

    logger.info("Twitter ID : " + user.getName());
    logger.info("Display Name: " + user.getDisplayedName());
    logger.info("Total Tweets: " + user.getTweetCount());
    logger.info("Account date: " + user.getDateOfCreation());
    logger.info("Total followers: " + user.getFollowersCount());
    logger.info("Total following: " + user.getFollowingCount());
    logger.info("Location : " + user.getLocation());
    logger.info("Language : " + user.getData().getLang() != null ? user.getData().getLang() : "");
    logger.info("Protected : " + user.getData().isProtectedAccount());
    logger.info("Verified : " + user.getData().isVerified());
    UserPublicMetrics metrics = user.getData().getPublicMetrics();
    logger.info("Listed count: " + metrics.getListedCount());
    logger.info("URL: " + user.getData().getUrl());
    logger.info("Profile Image URL: " + user.getData().getProfileImageUrl());
    Includes includes = user.getIncludes();
    if (includes != null) {
      TweetData[] tweetDataArr = includes.getTweets();
      for (TweetData tweetData : tweetDataArr) {
        logger.info(tweetData.getText());
      }
    }
  }
}
