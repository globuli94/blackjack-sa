package controller.util;

import akka.actor.ActorSystem
import akka.kafka.ProducerSettings
import akka.kafka.scaladsl.Producer
import akka.stream.{Materializer, QueueOfferResult}
import akka.stream.scaladsl.{Keep, Sink, Source, SourceQueueWithComplete}
import org.apache.kafka.clients.producer.ProducerRecord
import org.apache.kafka.common.serialization.StringSerializer

import scala.util.{Failure, Success}
import scala.concurrent.ExecutionContext

class KafkaProducer(kafkaHost: String)(implicit system: ActorSystem, mat: Materializer, ec: ExecutionContext) {

  private val producerSettings = ProducerSettings(system, new StringSerializer, new StringSerializer)
    .withBootstrapServers(kafkaHost)

  def publish(topic: String, key: String, value: String): Unit = {
    val done = Source.single(new ProducerRecord[String, String](topic, key, value))
      .runWith(Producer.plainSink(producerSettings))
  }
}
