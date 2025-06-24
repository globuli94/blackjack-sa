package util

import akka.actor.ActorSystem
import akka.kafka.{ConsumerSettings, Subscriptions}
import akka.kafka.scaladsl.Consumer
import akka.stream.Materializer
import akka.stream.scaladsl.Sink
import org.apache.kafka.clients.consumer.ConsumerConfig
import org.apache.kafka.common.serialization.StringDeserializer

import scala.concurrent.ExecutionContext

class KafkaConsumer(
    kafkaHost: String,
    topic: String,
    groupId: String
)(implicit system: ActorSystem, mat: Materializer, ec: ExecutionContext) {

  private val consumerSettings = ConsumerSettings(system, new StringDeserializer, new StringDeserializer)
    .withBootstrapServers(kafkaHost)
    .withGroupId(groupId)

  def consume(printValue: Boolean = true): Unit = {
    print("Starting to consume")
    Consumer
      .plainSource(consumerSettings, Subscriptions.topics(topic))
      .runWith(Sink.foreach { msg =>
        if (printValue)
          println(s"Consumed from $topic | Key: ${msg.key()} | Value: ${msg.value()}")
      })
  }

  def consumeWithHandler(handler: (String, String) => Unit): Unit = {
    Consumer
      .plainSource(consumerSettings, Subscriptions.topics(topic))
      .runWith(Sink.foreach { msg =>
        handler(msg.key(), msg.value())
      })
  }
}
