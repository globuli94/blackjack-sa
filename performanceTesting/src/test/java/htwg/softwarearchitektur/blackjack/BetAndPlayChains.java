package htwg.softwarearchitektur.blackjack;

import io.gatling.javaapi.core.ChainBuilder;
import io.gatling.javaapi.core.Session;

import java.util.Map;

import static io.gatling.javaapi.core.CoreDsl.exec;
import static io.gatling.javaapi.core.CoreDsl.pause;
import static io.gatling.javaapi.http.HttpDsl.http;

public class BetAndPlayChains {

	private static final Map<CharSequence, String> headers_0 = Map.ofEntries(
		Map.entry("Cache-Control", "no-cache"),
		Map.entry("Origin", "http://localhost:8080"),
		Map.entry("Pragma", "no-cache"),
		Map.entry("Sec-Fetch-Dest", "empty"),
		Map.entry("Sec-Fetch-Mode", "cors"),
		Map.entry("Sec-Fetch-Site", "same-origin"),
		Map.entry("sec-ch-ua", "Chromium\";v=\"135\", \"Not-A.Brand\";v=\"8"),
		Map.entry("sec-ch-ua-mobile", "?0"),
		Map.entry("sec-ch-ua-platform", "Linux")
	);

	private static final Map<CharSequence, String> headers_1 = Map.ofEntries(
		Map.entry("Cache-Control", "no-cache"),
		Map.entry("Pragma", "no-cache"),
		Map.entry("Sec-Fetch-Dest", "empty"),
		Map.entry("Sec-Fetch-Mode", "cors"),
		Map.entry("Sec-Fetch-Site", "same-origin"),
		Map.entry("sec-ch-ua", "Chromium\";v=\"135\", \"Not-A.Brand\";v=\"8"),
		Map.entry("sec-ch-ua-mobile", "?0"),
		Map.entry("sec-ch-ua-platform", "Linux")
	);

	public static final ChainBuilder betAndPlayChain = exec(
		http("bet 100 - step 1")
			.post((Session session) -> "/game/" + session.getString("sessionId") + "/bet/100")
			.headers(headers_0)
			.resources(
				http("state after bet1")
					.get((Session session) -> "/game/" + session.getString("sessionId") + "/state")
					.headers(headers_1),
				http("bet 100 - step 2")
					.post((Session session) -> "/game/" + session.getString("sessionId") + "/bet/100")
					.headers(headers_0),
				http("state after bet2")
					.get((Session session) -> "/game/" + session.getString("sessionId") + "/state")
					.headers(headers_1),
				http("bet 100 - step 3")
					.post((Session session) -> "/game/" + session.getString("sessionId") + "/bet/100")
					.headers(headers_0),
				http("state after bet3")
					.get((Session session) -> "/game/" + session.getString("sessionId") + "/state")
					.headers(headers_1),
				http("bet 100 - step 4")
					.post((Session session) -> "/game/" + session.getString("sessionId") + "/bet/100")
					.headers(headers_0),
				http("state after bet4")
					.get((Session session) -> "/game/" + session.getString("sessionId") + "/state")
					.headers(headers_1)
			)
	)
		.pause(7)
		.exec(
			http("hit - step 1")
				.post((Session session) -> "/game/" + session.getString("sessionId") + "/hit")
				.headers(headers_0)
				.resources(
					http("state after hit1")
						.get((Session session) -> "/game/" + session.getString("sessionId") + "/state")
						.headers(headers_1),
					http("hit - step 2")
						.post((Session session) -> "/game/" + session.getString("sessionId") + "/hit")
						.headers(headers_0),
					http("state after hit2")
						.get((Session session) -> "/game/" + session.getString("sessionId") + "/state")
						.headers(headers_1),
					http("hit - step 3")
						.post((Session session) -> "/game/" + session.getString("sessionId") + "/hit")
						.headers(headers_0),
					http("state after hit3")
						.get((Session session) -> "/game/" + session.getString("sessionId") + "/state")
						.headers(headers_1),
					http("hit - step 4")
						.post((Session session) -> "/game/" + session.getString("sessionId") + "/hit")
						.headers(headers_0),
					http("state after hit4")
						.get((Session session) -> "/game/" + session.getString("sessionId") + "/state")
						.headers(headers_1)
				)
		);
}
