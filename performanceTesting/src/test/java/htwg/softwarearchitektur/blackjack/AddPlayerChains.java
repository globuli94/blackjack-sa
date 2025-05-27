package htwg.softwarearchitektur.blackjack;

import java.util.Map;

import io.gatling.javaapi.core.ChainBuilder;
import io.gatling.javaapi.core.Session;

import static io.gatling.javaapi.core.CoreDsl.exec;
import static io.gatling.javaapi.core.CoreDsl.pause;
import static io.gatling.javaapi.http.HttpDsl.http;

public class AddPlayerChains {

	public static final Map<CharSequence, String> commonHeaders = Map.ofEntries(
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

	// baut den State-Request dynamisch aus der sessionId
	private static final ChainBuilder getState = exec(
		http("getState")
			.get((Session session) -> "/game/" + session.getString("sessionId") + "/state")
			.headers(commonHeaders)
	);

	public static final ChainBuilder addPlayersChain = exec(
		http("addPlayer p1")
			.post((Session session) -> "/game/" + session.getString("sessionId") + "/addPlayer/p1")
			.headers(commonHeaders)
	)
		.exec(getState)
		.pause(1)
		.exec(
			http("addPlayer p2")
				.post((Session session) -> "/game/" + session.getString("sessionId") + "/addPlayer/p2")
				.headers(commonHeaders)
		)
		.exec(getState)
		.pause(1)
		.exec(
			http("addPlayer p3")
				.post((Session session) -> "/game/" + session.getString("sessionId") + "/addPlayer/p3")
				.headers(commonHeaders)
		)
		.exec(getState)
		.pause(1)
		.exec(
			http("addPlayer p4")
				.post((Session session) -> "/game/" + session.getString("sessionId") + "/addPlayer/p4")
				.headers(commonHeaders)
		)
		.exec(getState);
}
