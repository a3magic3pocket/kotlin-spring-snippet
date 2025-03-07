package kotlins.pring.snippet.kafka

import org.springframework.kafka.core.KafkaTemplate
import org.springframework.scheduling.annotation.Scheduled
import org.springframework.stereotype.Service
import java.time.ZoneId
import java.time.ZonedDateTime
import java.time.format.DateTimeFormatter

@Service
class HelloProducer(
    private val kafkaTemplate: KafkaTemplate<String, String>
) {

//    @Scheduled(fixedRate = 1000L)
//    @Scheduled(fixedRate = 500L)
    @Scheduled(fixedRate = 50L)
    fun sendHello() {
        val now = ZonedDateTime.now(ZoneId.of("UTC"))
        val nowIsoString = now.format(DateTimeFormatter.ISO_INSTANT)
        val message = "hello|$nowIsoString"

        kafkaTemplate.send("hello", message)
    }

}