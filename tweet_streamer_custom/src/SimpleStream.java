import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.List;

import twitter4j.FilterQuery;
import twitter4j.Query;
import twitter4j.QueryResult;
import twitter4j.StallWarning;
import twitter4j.Status;
import twitter4j.StatusDeletionNotice;
import twitter4j.StatusListener;
import twitter4j.Twitter;
import twitter4j.TwitterException;
import twitter4j.TwitterFactory;
import twitter4j.TwitterStream;
import twitter4j.TwitterStreamFactory;
import twitter4j.User;
import twitter4j.conf.ConfigurationBuilder;

public class SimpleStream {
	public static void main(String[] args) throws IOException, TwitterException {

		final BufferedWriter bw = new BufferedWriter(new FileWriter(new File(
				"./tweets.txt")));
		System.setProperty("http.proxyHost", "proxy.iiit.ac.in");
		System.setProperty("http.proxyPort", "8080");
		ConfigurationBuilder cb = new ConfigurationBuilder();
		cb.setDebugEnabled(true);
		cb.setOAuthConsumerKey("2IRkz9g2E3DuxoNkYsLd2eq1e");
		cb.setOAuthConsumerSecret("0qIvgH9SKq1Pfq0UWuyhixflFOlVkDG9WT7dGOlwwj91K3mjPC");
		cb.setOAuthAccessToken("2321297846-b1S3s8swM3Zpy22FdgE23byzbIDDE5o1BINjElv");
		cb.setOAuthAccessTokenSecret("wqxkBlWuowbaSCKi6GCA7MH7FmOvGjf4MFZIHNnz400Mo");

		// TwitterFactory tf = new TwitterFactory(cb.build());
		// Twitter twitter = tf.getInstance();
		// Twitter twitter = TwitterFactory.getSingleton();
		/*
		 * List<Status> statuses = twitter.getHomeTimeline();
		 * System.out.println("Showing home timeline."); for (Status status :
		 * statuses) { System.out.println(status.getUser().getName() + ":" +
		 * status.getText()); }*
		 * 
		 * Query query = new Query("ireland"); QueryResult result =
		 * twitter.search(query); for (Status status : result.getTweets()) {
		 * System.out.println("@" + status.getUser().getScreenName() + ":" +
		 * status.getText()); }
		 */

		TwitterStream twitterStream = new TwitterStreamFactory(cb.build())
				.getInstance();

		StatusListener listener = new StatusListener() {

			@Override
			public void onException(Exception arg0) {
				// TODO Auto-generated method stub

			}

			@Override
			public void onDeletionNotice(StatusDeletionNotice arg0) {
				// TODO Auto-generated method stub

			}

			@Override
			public void onScrubGeo(long arg0, long arg1) {
				// TODO Auto-generated method stub

			}

			@Override
			public void onStatus(Status status) {

				System.out.println("Status: " + status.getText());

				try {
					String tweet = status.getText();
					if (!tweet.startsWith("RT")) {
						bw.write(status.getText() + "\n\n");
						bw.flush();
					}
				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}

				// User user = status.getUser();

				// gets Username
				/*
				 * String username = status.getUser().getScreenName();
				 * System.out.println(username); String profileLocation =
				 * user.getLocation(); System.out.println(profileLocation); long
				 * tweetId = status.getId(); System.out.println(tweetId); String
				 * content = status.getText(); System.out.println(content
				 * +"\n");
				 */

			}

			@Override
			public void onTrackLimitationNotice(int arg0) {
				// TODO Auto-generated method stub

			}

			@Override
			public void onStallWarning(StallWarning arg0) {
				// TODO Auto-generated method stub

			}

		};
		FilterQuery fq = new FilterQuery();
		String lang[] = { "en" };
		fq.language(lang);

		String keywords[] = {"NASA" };

		fq.track(keywords);

		twitterStream.addListener(listener);
		twitterStream.filter(fq);

	}
}
