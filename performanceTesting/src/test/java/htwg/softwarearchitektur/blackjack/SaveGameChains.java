package htwg.softwarearchitektur.blackjack;

import io.gatling.javaapi.core.ChainBuilder;
import io.gatling.javaapi.core.Session;

import java.util.Map;

import static io.gatling.javaapi.core.CoreDsl.exec;
import static io.gatling.javaapi.http.HttpDsl.http;

public class SaveGameChains {

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

	public static final ChainBuilder saveGameChain = exec(
		http("save game")
			.post((Session session) -> "/game/" + session.getString("sessionId") + "/save")
			.headers(headers_0)
	)
		.exec(
			http("state after save")
				.get((Session session) -> "/game/" + session.getString("sessionId") + "/state")
				.headers(headers_1)
		);
}
