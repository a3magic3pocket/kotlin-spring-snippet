package kotlins.pring.snippet.kafka

import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking
import org.apache.kafka.clients.consumer.ConsumerRecord
import org.springframework.kafka.annotation.KafkaListener
import org.springframework.kafka.support.Acknowledgment
import org.springframework.stereotype.Service

@Service
class HelloConsumer {

//    @KafkaListener(topics = ["hello"], groupId = "group1", concurrency = "4", batch = "true")
//    fun consumeBatch(records: List<ConsumerRecord<String, String>>, acknowledgment: Acknowledgment) {
//        runBlocking {
//            // 각 메시지에 대해 비동기 처리
//            records.forEach { message ->
//                // 각 메시지마다 비동기적으로 처리
//                launch {
//                    println("consumed|${message.value()}|messages.size=${records.size}")
//                    // 비동기적으로 150ms 대기
//                    delay(1500)
//                }
//            }
//            acknowledgment.acknowledge()
//        }
//    }


    @KafkaListener(topics = ["hello"], groupId = "group1", concurrency = "2")
    fun consume(message: String, acknowledgment: Acknowledgment) {
        println("consumed|$message")

//        Thread.sleep(150)
        Thread.sleep(0)

        acknowledgment.acknowledge()
    }

}