package kotlins.pring.snippet

import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.runApplication
import org.springframework.scheduling.annotation.EnableScheduling

@SpringBootApplication
@EnableScheduling
class SnippetApplication

fun main(args: Array<String>) {
	runApplication<SnippetApplication>(*args)
}
