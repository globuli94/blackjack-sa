package htwg.softwarearchitektur.blackjack;

import io.gatling.javaapi.core.ChainBuilder;
import io.gatling.javaapi.core.Session;

import java.util.Map;

import static io.gatling.javaapi.core.CoreDsl.exec;
// statt nur http importieren wir hier das ganze HttpDsl:
import static io.gatling.javaapi.http.HttpDsl.*;

public class LoadGamePageChains {

	private static final Map<CharSequence, String> headers_0 = Map.ofEntries(
		Map.entry("Accept", "text/html,application/xhtml+xml,application/xml;q=0.9,image/avif,image/webp,image/apng,*/*;q=0.8"),
		Map.entry("Cache-Control", "no-cache"),
		Map.entry("Pragma", "no-cache"),
		Map.entry("Sec-Fetch-Dest", "document"),
		Map.entry("Sec-Fetch-Mode", "navigate"),
		Map.entry("Sec-Fetch-Site", "none"),
		Map.entry("Sec-Fetch-User", "?1"),
		Map.entry("Upgrade-Insecure-Requests", "1"),
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

	public static final ChainBuilder extractSession = exec(
		http("extract session")
			.get("/")
			.headers(headers_0)
			.disableFollowRedirect()
			.check(status().is(303))
			.check(
				headerRegex("Location", "sessionId=([0-9a-fA-F\\-]+)")
					.saveAs("sessionId")
			)
	);

	public static final ChainBuilder loadGamePageChain = extractSession
		.exec(
			http("load game state")
				.get((Session session) -> {
					String sid = session.getString("sessionId");
					return "/game/" + sid + "/state";
				})
				.headers(headers_1)
		);
}
