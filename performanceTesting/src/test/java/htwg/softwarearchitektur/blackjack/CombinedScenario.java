package htwg.softwarearchitektur.blackjack;

import io.gatling.javaapi.core.ScenarioBuilder;
import io.gatling.javaapi.core.Simulation;
import io.gatling.javaapi.http.HttpProtocolBuilder;

import static io.gatling.javaapi.core.CoreDsl.scenario;
import static io.gatling.javaapi.core.CoreDsl.atOnceUsers;
import static io.gatling.javaapi.http.HttpDsl.http;

import static htwg.softwarearchitektur.blackjack.LoadGamePageChains.loadGamePageChain;
import static htwg.softwarearchitektur.blackjack.AddPlayerChains.addPlayersChain;
import static htwg.softwarearchitektur.blackjack.StartGameChains.startGameChain;
import static htwg.softwarearchitektur.blackjack.BetAndPlayChains.betAndPlayChain;
import static htwg.softwarearchitektur.blackjack.SaveGameChains.saveGameChain;
import static htwg.softwarearchitektur.blackjack.LoadGameChains.loadGameChain;

public class CombinedScenario extends Simulation {

	private HttpProtocolBuilder httpProtocol = http
		.baseUrl("http://localhost:8080")
		.inferHtmlResources()
		.acceptHeader("*/*")
		.acceptEncodingHeader("gzip, deflate, br")
		.acceptLanguageHeader("de-DE,de;q=0.9,en-US;q=0.8,en;q=0.7")
		.userAgentHeader("Mozilla/5.0 (X11; Linux x86_64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/135.0.0.0 Safari/537.36");

	private ScenarioBuilder scn = scenario("CombinedScenario")
		.exec(loadGamePageChain)
		.exec(addPlayersChain)
		.exec(startGameChain)
		.exec(betAndPlayChain)
		.exec(saveGameChain)
		.exec(loadGameChain)
		.exec(loadGameChain)
		.exec(loadGameChain);

	{
		setUp(
			scn.injectOpen(atOnceUsers(50))
		).protocols(httpProtocol);
	}
}
